package com.creepersan.file.function.app_viewer.fragment

import android.graphics.drawable.Drawable
import android.os.AsyncTask
import com.creepersan.file.fragment.BaseMainActivityFragment

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.creepersan.file.FileApplication
import com.creepersan.file.R
import com.creepersan.file.function.app_viewer.bean.AppViewerBean
import com.creepersan.file.utils.generateAppViewerBean
import com.creepersan.file.utils.loadImageGlide
import kotlinx.android.synthetic.main.fragment_application.*
import java.lang.ref.WeakReference
import kotlin.collections.ArrayList

class AppViewerFragment : BaseMainActivityFragment(), Toolbar.OnMenuItemClickListener {

    override val mLayoutID: Int = R.layout.fragment_application

    private var mAppItemList = ArrayList<AppViewerBean>()
    private val mAdapter by lazy { AppViewerAdapter() }
    private val mPackageManager by lazy { activity.packageManager }
    private var isMultiSelecting = false
    private val mSelectPosPool = HashSet<Int>()
    private val mSelectedColor by lazy { activity.getColor(R.color.lightGray) }

    override fun getTitle(): String {
        return FileApplication.getInstance().getString(R.string.appViewerTitle)
    }

    override fun getIcon(): Int {
        return R.drawable.ic_application_icon
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoading()
        initToolBar()
        initData()
        initRecyclerView()
    }

    private fun initToolBar(){
        appViewerToolBar.inflateMenu(R.menu.app_viewer)
        appViewerToolBar.setOnMenuItemClickListener(this)
    }
    private fun initData(){
        val reference = WeakReference<AppViewerFragment>(this)
        GetAppListInfoTask().execute(GetAppListInfoConfig({ resultList ->
            reference.get()?.apply {
                setAppList(resultList)
                showContent()
            }
        }))
    }
    private fun initRecyclerView(){
        appViewerRecyclerView.layoutManager = GridLayoutManager(context, 4)
        appViewerRecyclerView.adapter = mAdapter
    }



    /* 一些封装操作 */
    private fun isMultiSelecting():Boolean{
        return isMultiSelecting
    }
    private fun setMultiSeleteState(state:Boolean){
        appViewerToolBar.menu.clear()
        if (state){
            appViewerToolBar.inflateMenu(R.menu.app_viewer_multi_select)
            appViewerToolBar.setNavigationIcon(R.drawable.ic_arrow_left_white)
            appViewerToolBar.setNavigationOnClickListener {
                onBackPressed()
            }
        }else{
            mSelectPosPool.clear()
            mAdapter.notifyDataSetChanged()
            appViewerToolBar.inflateMenu(R.menu.app_viewer)
            appViewerToolBar.navigationIcon = null
        }
        isMultiSelecting = state
    }
    fun setAppList(appList:ArrayList<AppViewerBean>){
        mAppItemList = appList
        mAdapter.notifyDataSetChanged()
    }
    fun showLoading(){
        appViewerProgressBar.visibility = View.VISIBLE
        appViewerRecyclerView.visibility = View.GONE
    }
    fun showContent(){
        appViewerProgressBar.visibility = View.GONE
        appViewerRecyclerView.visibility = View.VISIBLE
    }

    /* 回调事件 */
    override fun onBackPressed(): Boolean {
        if (isMultiSelecting()){
            setMultiSeleteState(false)
            return true
        }else{
            return false
        }
    }
    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when(menuItem.itemId){
            R.id.menuAppViewerMultiSelect -> {
                setMultiSeleteState(true)
            }
            R.id.menuAppViewerBackup -> {

            }
            R.id.menuAppViewerUninstall -> {

            }
        }
        return true
    }


    /* 内部类 */
    private object Sort{
        const val NAME = 0
        const val INSTALL_TIME = 1
        const val UPDATE_TIME = 2
    }
    private inner class AppViewerAdapter : RecyclerView.Adapter<AppViewerHolder>(){
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AppViewerHolder {
            return AppViewerHolder(p0)
        }

        override fun getItemCount(): Int {
            return mAppItemList.size
        }

        override fun onBindViewHolder(holder: AppViewerHolder, pos: Int) {
            val bean = mAppItemList[pos]
            val position = holder.adapterPosition
            holder.setTitle(bean.name)
            holder.setIcon(bean.icon)
            holder.setSelected(mSelectPosPool.contains(position))
            holder.setOnClickListener(View.OnClickListener {
                if (isMultiSelecting()){    // 正在多选模式下，按下了应用程序
                    if (mSelectPosPool.contains(position)){
                        mSelectPosPool.remove(position)
                    }else{
                        mSelectPosPool.add(position)
                    }
                    mAdapter.notifyItemChanged(position)
                }else{                      // 浏览模式下

                }
            })
        }

    }
    private inner class AppViewerHolder(parent:ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_app_viewer, parent, false)){
        private val imageIcon = itemView.findViewById<ImageView>(R.id.itemAppViewerIcon)
        private val textTitle = itemView.findViewById<TextView>(R.id.itemAppViewerTitle)

        fun setTitle(str:String){
            textTitle.text = str
        }

        fun setIcon(icon:Drawable){
            imageIcon.loadImageGlide(icon)
        }

        fun setSelected(isSelected:Boolean){
            if (isSelected){
                itemView.setBackgroundColor(mSelectedColor)
            }else{
                itemView.background = null
            }
        }

        fun setOnClickListener(listener:View.OnClickListener){
            itemView.setOnClickListener(listener)
        }

    }

    private class GetAppListInfoConfig(
        var action:(ArrayList<AppViewerBean>)->Unit,
        var isShowSystemApp:Boolean=true,
        var sort:Int=Sort.NAME,
        var isCaseSensitive:Boolean=false,
        var isReverse:Boolean=false
    )
    private class GetAppListInfoTask : AsyncTask<GetAppListInfoConfig, Int, ArrayList<AppViewerBean>>(){
        private lateinit var mConfig:GetAppListInfoConfig

        override fun doInBackground(vararg params: GetAppListInfoConfig?): ArrayList<AppViewerBean> {
            if (params.isEmpty()){
                throw IllegalStateException("AppViewer:加载应用需要至少传入至少一个参数")
            }
            mConfig = params[0] ?: throw IllegalStateException("AppViewer:加载应用需要传入的参数不能为空")
            val tmpResult = ArrayList<AppViewerBean>()
            val packageManager = FileApplication.getPackageManager()
            // 添加组合数据
            packageManager.getInstalledPackages(0).forEach {
                tmpResult.add(it.generateAppViewerBean(packageManager))
            }
            // 排序
            tmpResult.sortBy { it.name }
            return tmpResult
        }

        override fun onPostExecute(result: ArrayList<AppViewerBean>) {
            super.onPostExecute(result)
            mConfig.action.invoke(result)
        }

    }

}