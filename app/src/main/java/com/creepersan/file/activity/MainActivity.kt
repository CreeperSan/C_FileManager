package com.creepersan.file.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.creepersan.file.R
import com.creepersan.file.bean.FileItem
import com.creepersan.file.fragment.FileFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    override val mLayoutID: Int = R.layout.activity_main

    private val fragment by lazy { FileFragment() }
    private val mOperationActionList by lazy { arrayListOf(
        EndOperationItem(R.drawable.ic_file_paste, getString(R.string.textMainEndDrawerOperationAllPasteTo)),
        EndOperationItem(R.drawable.ic_file_cut, getString(R.string.textMainEndDrawerOperationAllMoveTo)),
        EndOperationItem(R.drawable.ic_file_delete, getString(R.string.textMainEndDrawerOperationAllDelete)),
        EndOperationItem(R.drawable.ic_close, getString(R.string.textMainEndDrawerOperationAllClear))
        ) }
    private val mOperationAdapter by lazy { EndDrawerOperationAdapter() }
    private val mOperationList by lazy { ArrayList<FileItem>() }
    private val mDrawerStartAdapter by lazy { StartDrawerAdapter() }
    private val mDrawerStartItemList by lazy {
        ArrayList<StartActionBaseItem>().apply {
            add(StartActionCatalogItem(R.drawable.ic_file_delete, "删除", true))
            add(StartActionCatalogItem(R.drawable.ic_file_more, "更多", false))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRightDrawer()
        initFrameLayout()
    }

    private fun initRightDrawer(){
        // 初始化操作列表部分
        mainEndDrawerOperationDrawerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mainEndDrawerOperationDrawerView.adapter = mOperationAdapter
        // 初始化操作的文件列表部分
        mainStartDrawerList.layoutManager = LinearLayoutManager(this)
        mainStartDrawerList.adapter = mDrawerStartAdapter
    }
    private fun initFrameLayout(){
        supportFragmentManager.beginTransaction().add(mainFrameLayout.id, fragment).commitNow()
    }

    override fun onBackPressed() {
        if (!fragment.onBackPressed()){
            super.onBackPressed()
        }
    }

    class EndOperationItem(val icon:Int, val name:String)
    abstract class StartActionBaseItem(val name:String)
    class StartActionCatalogItem(val icon:Int,name:String, var state:Boolean):StartActionBaseItem(name)
    inner class EndDrawerOperationAdapter : RecyclerView.Adapter<EndDrawerOperationViewHolder>(){
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): EndDrawerOperationViewHolder {
            return EndDrawerOperationViewHolder(p0)
        }

        override fun getItemCount(): Int {
            return mOperationActionList.size
        }

        override fun onBindViewHolder(holder: EndDrawerOperationViewHolder, pos: Int) {
            val tmpItem = mOperationActionList[pos]
            holder.setItem(tmpItem.icon, tmpItem.name)
        }
    }
    inner class EndDrawerOperationViewHolder(parent:ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_main_end_drawer_operation_list,parent,false)){
        private val imageIcon = itemView.findViewById<ImageView>(R.id.itemMainEndDrawerOperationIcon)
        private val nameText = itemView.findViewById<TextView>(R.id.itemMainEndDrawerOperationName)

        fun setItem(icon:Int, name:String){
            imageIcon.setImageResource(icon)
            nameText.text = name
        }
    }
    inner class StartDrawerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        override fun getItemViewType(position: Int): Int {
            return super.getItemViewType(position)
        }

        override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
            return when(type){
                else -> { StartDrawerCatalogViewHolder(parent) }
            }
        }

        override fun getItemCount(): Int {
            return mDrawerStartItemList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
            val tmpItem = mDrawerStartItemList[pos]
            when(holder){
                is StartDrawerCatalogViewHolder -> {
                    val item = tmpItem as StartActionCatalogItem
                    holder.setState(item.icon, item.name, item.state)
                }
            }
        }

    }
    inner class StartDrawerCatalogViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_main_start_drawer_catalog,parent,false)){
        private val imageIcon = itemView.findViewById<ImageView>(R.id.itemMainStartDrawerCatalogTypeIcon)
        private val textName = itemView.findViewById<TextView>(R.id.itemMainStartDrawerCatalogName)
        private val imageState = itemView.findViewById<ImageView>(R.id.itemMainStartDrawerCatalogStateIcon)

        fun setState(iconImage:Int, name:String, isOpen:Boolean){
            imageIcon.setImageResource(iconImage)
            textName.text = name
            imageState.rotation = if (isOpen){
                0f
            }else{
                270f
            }
        }
    }
    inner class StartDrawerCatalogItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_main_start_drawer_catalog_item,parent,false)){
        val textName = itemView as TextView

        fun setText(text:String){
            textName.text = text
        }
    }
    inner class StartDrawerCatalogSwitchViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_main_start_drawer_catalog_switch, parent, false)){
        val imageIcon = itemView.findViewById<ImageView>(R.id.itemMainStartDrawerItemTypeIcon)
        val nameText = itemView.findViewById<TextView>(R.id.itemMainStartDrawerItemName)
        val stateCheckBox = itemView.findViewById<CheckBox>(R.id.itemMainStartDrawerItemCheckbox)

    }
}