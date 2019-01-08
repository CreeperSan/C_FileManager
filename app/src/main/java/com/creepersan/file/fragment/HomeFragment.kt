package com.creepersan.file.fragment

import android.os.Bundle
import android.os.Environment
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.creepersan.file.FileApplication
import com.creepersan.file.R
import com.creepersan.file.utils.toFormattedStorageString
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseMainActivityFragment() {

    private val mOperationList by lazy { ArrayList<OperationIcon>().apply {
        add(OperationIcon(ID_OPERATION_IMAGE, R.drawable.ic_home_image, getString(R.string.homeFragmentOperationTitleImage), R.drawable.bg_home_fragment_operation_light_green))
        add(OperationIcon(ID_OPERATION_MUSIC, R.drawable.ic_home_music, getString(R.string.homeFragmentOperationTitleMusic), R.drawable.bg_home_fragment_operation_yellow))
        add(OperationIcon(ID_OPERATION_VIDEO, R.drawable.ic_home_video, getString(R.string.homeFragmentOperationTitleVideo), R.drawable.bg_home_fragment_operation_purple))
        add(OperationIcon(ID_OPERATION_DOCUMENT, R.drawable.ic_home_document, getString(R.string.homeFragmentOperationTitleDocument), R.drawable.bg_home_fragment_operation_blue))
        add(OperationIcon(ID_OPERATION_APPLICATION, R.drawable.ic_home_application, getString(R.string.homeFragmentOperationTitleApplication), R.drawable.bg_home_fragment_operation_red))
        add(OperationIcon(ID_OPERATION_COLLECTION, R.drawable.ic_home_collection, getString(R.string.homeFragmentOperationTitleCollection), R.drawable.bg_home_fragment_operation_orange))
        add(OperationIcon(ID_OPERATION_DOWNLOAD, R.drawable.ic_home_download, getString(R.string.homeFragmentOperationTitleDownload), R.drawable.bg_home_fragment_operation_blue))
        add(OperationIcon(ID_OPERATION_MORE, R.drawable.ic_home_more_horizontal, getString(R.string.homeFragmentOperationTitleMore), R.drawable.bg_home_fragment_operation_yellow))
    } }
    private val mStorageList by lazy { ArrayList<StorageItem>() }

    private val mTitle by lazy { FileApplication.getInstance().getString(R.string.fragmentHomeTitle) }
    private val mOperationAdapter by lazy { OperationAdapter() }
    private val mStorageAdapter by lazy { StorageAdapter() }




    override fun getTitle(): String {
        return mTitle
    }
    override fun getIcon(): Int {
        return R.drawable.ic_file_home
    }

    override val mLayoutID: Int = R.layout.fragment_home


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOperationList()
        initStorageListData()
        initStorageList()
    }

    private fun initOperationList(){
        homeFragmentOperationList.layoutManager = GridLayoutManager(activity, OPERATION_HORIZONTAL_ICON_NUM, GridLayoutManager.VERTICAL, false)
        homeFragmentOperationList.adapter = mOperationAdapter
    }
    private fun initStorageListData(){
        mStorageList.clear()

        // 添加内置存储空间
        val storageFile = Environment.getExternalStorageDirectory()
        val usableSpace = storageFile.usableSpace
        val totalSpace = storageFile.totalSpace
        val storageItem = StorageItem(
            R.drawable.ic_home_storage_sdcard,
            storageFile.name,
            "${usableSpace.toFormattedStorageString(activity)} / ${totalSpace.toFormattedStorageString(activity)}",
            STORAGE_PROGRESSBAR_MAX - ((usableSpace.toDouble()/totalSpace.toDouble())*STORAGE_PROGRESSBAR_MAX).toInt()
        )
        mStorageList.add(storageItem)

    }
    private fun initStorageList(){
        homeFragmentStorageList.layoutManager = LinearLayoutManager(mActivity)
        homeFragmentStorageList.adapter = mStorageAdapter
    }


    /* 首页快捷操作图标 */
    private inner class OperationIcon(val id:Int, var icon:Int, var title:String, var bg:Int=R.drawable.bg_home_fragment_operation_blue)
    private inner class OperationAdapter : RecyclerView.Adapter<OperationViewHolder>(){
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): OperationViewHolder {
            return OperationViewHolder(p0)
        }

        override fun getItemCount(): Int {
            return mOperationList.size
        }

        override fun onBindViewHolder(p0: OperationViewHolder, p1: Int) {
            val item = mOperationList[p1]
            p0.initHolder(item.icon, item.title, item.bg)
            p0.setOnClickListener(View.OnClickListener {
                when(item.id){

                }
                toast("Clicked => ${item.title}")
            })
        }

    }
    private inner class OperationViewHolder(parent:ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_fragment_home_operation_icon, parent, false)){
        private val imageIcon = itemView.findViewById<ImageView>(R.id.itemFragmentHomeOperationIcon)
        private val textTitle = itemView.findViewById<TextView>(R.id.itemFragmentHomeOperationTitle)

        fun initHolder(icon:Int, title: String, bg:Int){
            imageIcon.setImageResource(icon)
            textTitle.text = title
            imageIcon.setBackgroundResource(bg)
        }

        fun setOnClickListener(listenet: View.OnClickListener){
            itemView.setOnClickListener(listenet)
        }
    }

    /* 首页存储列表 */
    private inner class StorageItem(val icon:Int, val title:String, var spaceText:String, var spaceProgress:Int)
    private inner class StorageViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_fragment_home_storage, parent, false)){
        private val imageIcon = itemView.findViewById<ImageView>(R.id.itemFragmentHomeStorageIcon)
        private val textTitle = itemView.findViewById<TextView>(R.id.itemFragmentHomeStorageName)
        private val textSpace = itemView.findViewById<TextView>(R.id.itemFragmentHomeStorageSpaceText)
        private val progressBar = itemView.findViewById<ProgressBar>(R.id.itemFragmentHomeStorageProgressBar)
        private val layout = itemView.findViewById<ConstraintLayout>(R.id.itemFragmentHomeLayout)

        fun setOnClickListener(listenet: View.OnClickListener){
            layout.setOnClickListener(listenet)
        }

        fun initHolder(item:StorageItem){
            imageIcon.setImageResource(item.icon)
            textTitle.text = item.title
            textSpace.text = item.spaceText
            progressBar.progress = item.spaceProgress
        }

    }
    private inner class StorageAdapter : RecyclerView.Adapter<StorageViewHolder>(){
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): StorageViewHolder {
            return StorageViewHolder(p0)
        }

        override fun getItemCount(): Int {
            return mStorageList.size
        }

        override fun onBindViewHolder(p0: StorageViewHolder, p1: Int) {
            val item = mStorageList[p1]
            p0.initHolder(item)
            p0.setOnClickListener(View.OnClickListener {
                toast(item.title)
            })
        }

    }


    /* 伴生对象 */
    companion object {
        private const val OPERATION_HORIZONTAL_ICON_NUM = 4 // 首页头部一行图标数量
        private const val STORAGE_PROGRESSBAR_MAX = 100 // 存储设备进度条最大值

        private const val ID_OPERATION_IMAGE = 0
        private const val ID_OPERATION_MUSIC = 1
        private const val ID_OPERATION_VIDEO = 2
        private const val ID_OPERATION_DOCUMENT = 3
        private const val ID_OPERATION_APPLICATION = 4
        private const val ID_OPERATION_MORE = 5
        private const val ID_OPERATION_COLLECTION = 6
        private const val ID_OPERATION_DOWNLOAD = 7
    }
}