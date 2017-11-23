package creepersan.com.cfilemanager.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import creepersan.com.cfilemanager.R
import creepersan.com.cfilemanager.base.BaseActivity
import creepersan.com.cfilemanager.base.BaseTabFragment
import creepersan.com.cfilemanager.fragment.FileListFragment
import creepersan.com.cfilemanager.util.BundleBuilder
import creepersan.com.cfilemanager.views.holder.SimpleItemViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : BaseActivity() {
    private var displayList = ArrayList<DrawerItem>()                //滑动展开列表队列
    private lateinit var categoryHeader : DrawerItem                    //头部分类
    private lateinit var categoryWindow : CategoryFragmentDrawerItem            //窗口分类
    private lateinit var categoryLocal : CategoryDrawerItem             //本地分类
    private lateinit var categoryBookmark : CategoryDrawerItem          //书签分类
    private lateinit var categoryLibrary : CategoryDrawerItem           //库分类
    private lateinit var categoryTools : CategoryDrawerItem             //工具分类
    private lateinit var drawerAdapter : DrawerRecyclerViewAdapter
    private lateinit var fragmentManager : FragmentManager
    private lateinit var fragmentPagerAdapter : TabViewPagerAdapter

    override fun getLayoutID(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragmentBase()
        initDrawerRecyclerViewData()
        initFragment()
        initDrawerRecyclerView()
        initFragmentViewPager()
    }

    /**
     * 各种的初始化
     */
    private fun initFragmentBase(){
        fragmentManager = supportFragmentManager
    }
    private fun initDrawerRecyclerViewData(){
        //先生成
        categoryHeader = CategoryDrawerItem(DrawerItem.Type.HEADER)
        categoryWindow = CategoryFragmentDrawerItem(DrawerItem.Type.CATEGORY_WINDOW)
        categoryLocal = CategoryDrawerItem(DrawerItem.Type.CATEGORY_LOCAL)
                .add(LocalDrawerItem(DrawerItem.Type.ITEM_LOCAL_ROOT))
                .add(LocalDrawerItem(DrawerItem.Type.ITEM_LOCAL_EXTERNAL))
                .add(LocalDrawerItem(DrawerItem.Type.ITEM_LOCAL_HOME))
        categoryBookmark = CategoryDrawerItem(DrawerItem.Type.CATEGORY_BOOKMARK)
                .add(BookmarkDrawerItem(DrawerItem.Type.ITEM_BOOKMARK))
        categoryLibrary = CategoryDrawerItem(DrawerItem.Type.CATEGORY_LIBRARY)
                .add(LibraryDrawerItem(DrawerItem.Type.ITEM_LIBRARY_IMAGE))
                .add(LibraryDrawerItem(DrawerItem.Type.ITEM_LIBRARY_MUSIC))
                .add(LibraryDrawerItem(DrawerItem.Type.ITEM_LIBRARY_VIDEO))
                .add(LibraryDrawerItem(DrawerItem.Type.ITEM_LIBRARY_APPLICATION))
        categoryTools = CategoryDrawerItem(DrawerItem.Type.CATEGORY_TOOLS)
                .add(ToolsDrawerItem(DrawerItem.Type.ITEM_TOOLS_FILE_TRANSFER))
        //后获取
        displayList.add(categoryHeader)
        displayList.add(categoryWindow)
        displayList.add(categoryLocal)
        displayList.add(categoryBookmark)
        displayList.add(categoryLibrary)
        displayList.add(categoryTools)
    }
    private fun initFragment(){
        val fileFragment1 = FileListFragment()
        val fileFragment2 = FileListFragment()
        val fileFragment3 = FileListFragment()
        fileFragment1.arguments = null
        fileFragment2.arguments = BundleBuilder()
                .put(FileListFragment.Argument.ROOT_PATH,FileListFragment.RootPath.SD_CARD)
                .build()
        fileFragment3.arguments = null
        categoryWindow.add(fileFragment1)
        categoryWindow.add(fileFragment2)
        categoryWindow.add(fileFragment3)
    }
    private fun initDrawerRecyclerView() {
        drawerAdapter = DrawerRecyclerViewAdapter()
        mainDrawerRecyclerView.layoutManager = LinearLayoutManager(this)
        mainDrawerRecyclerView.adapter = drawerAdapter
    }
    private fun initFragmentViewPager(){
        fragmentPagerAdapter = TabViewPagerAdapter()
        mainViewPager.adapter = fragmentPagerAdapter
    }


    /**
     *  内部类
     */
    private abstract class DrawerItem(var type:Int){
        object Type{
            val HEADER : Int = -1               //头部
            val CATEGORY_WINDOW  = 0            //窗口组
            val ITEM_WINDOW = 1
            val CATEGORY_LOCAL = 1000       //本地组
            val ITEM_LOCAL_HOME = 1001
            val ITEM_LOCAL_EXTERNAL = 1002
            val ITEM_LOCAL_ROOT = 1003
            val CATEGORY_BOOKMARK = 2000        //书签组
            val ITEM_BOOKMARK = 2001
            val CATEGORY_LIBRARY = 3000         //库组
            val ITEM_LIBRARY_IMAGE = 3001
            val ITEM_LIBRARY_VIDEO = 3002
            val ITEM_LIBRARY_MUSIC = 3003
            val ITEM_LIBRARY_APPLICATION = 3004
            val CATEGORY_TOOLS = 4000           //工具组
            val ITEM_TOOLS_FILE_TRANSFER = 4001
        }

        abstract fun getTitle():String

        //是否为分组
        fun isCategory():Boolean{
            return this is CategoryDrawerItem
        }
    }
    private abstract class TabDrawerItem(type: Int):DrawerItem(type){

    }
    private inner class HeaderDrawerItem() : DrawerItem(Type.HEADER){
        override fun getTitle(): String = ""

    }
    private open inner class CategoryDrawerItem(type: Int) : DrawerItem(type){

        var isExpand = false
        private var innerDrawerItemList = ArrayList<DrawerItem>()

        open fun add(item:DrawerItem):CategoryDrawerItem{
            innerDrawerItemList.add(item)
            return this
        }
        fun size(): Int {
            return if (isExpand) 1+innerDrawerItemList.size else 1
        }
        fun getDrawerItemList():ArrayList<DrawerItem>{
            return innerDrawerItemList
        }

        override fun getTitle(): String {
            when(type){
                Type.HEADER -> return getString(R.string.categoryHeader)
                Type.CATEGORY_WINDOW -> return getString(R.string.categoryWindow)
                Type.CATEGORY_LOCAL -> return getString(R.string.categoryLocal)
                Type.CATEGORY_BOOKMARK -> return getString(R.string.categoryBookmark)
                Type.CATEGORY_LIBRARY -> return getString(R.string.categoryLibrary)
                Type.CATEGORY_TOOLS -> return getString(R.string.categoryTools)
                else -> return getString(R.string.categoryUnknown)
            }
        }
    }
    private inner class CategoryFragmentDrawerItem(type:Int) : CategoryDrawerItem(type){
        val tabFragmentList = ArrayList<BaseTabFragment>()

        fun add(fragment:BaseTabFragment){
            tabFragmentList.add(fragment)
            add(WindowDrawerItem(Type.ITEM_WINDOW))
        }

        override fun add(item: DrawerItem): CategoryFragmentDrawerItem {
            return super.add(item) as CategoryFragmentDrawerItem
        }
    }
    private inner class WindowDrawerItem(type: Int) : TabDrawerItem(type){

        override fun getTitle(): String {
            return "窗口 #${Random().nextInt(100)}"
        }
    }
    private inner class LocalDrawerItem(type: Int) : TabDrawerItem(type){
        override fun getTitle(): String {
            when(type){
                Type.ITEM_LOCAL_HOME -> return getString(R.string.localHome)
                Type.ITEM_LOCAL_EXTERNAL -> return getString(R.string.localExternal)
                Type.ITEM_LOCAL_ROOT -> return getString(R.string.localRoot)
                else -> return ""
            }
        }

    }
    private inner class BookmarkDrawerItem(type: Int) : TabDrawerItem(type){
        override fun getTitle(): String {
            return ""
        }
    }
    private inner class LibraryDrawerItem(type: Int) : TabDrawerItem(type){
        override fun getTitle(): String {
            when(type){
                Type.ITEM_LIBRARY_IMAGE -> return getString(R.string.libraryImage)
                Type.ITEM_LIBRARY_VIDEO -> return getString(R.string.libraryVideo)
                Type.ITEM_LIBRARY_MUSIC -> return getString(R.string.libraryMusic)
                Type.ITEM_LIBRARY_APPLICATION -> return getString(R.string.libraryApplication)
                else -> return ""
            }
        }
    }
    private inner class ToolsDrawerItem(type: Int) : TabDrawerItem(type){
        override fun getTitle(): String {
            when(type){
                Type.ITEM_TOOLS_FILE_TRANSFER -> return getString(R.string.toolsFileTransfer)
                else -> return ""
            }
        }
    }
    private inner class DrawerHeaderViewHolder(viewGroup: ViewGroup) : RecyclerView.ViewHolder(getView(R.layout.item_main_drawer_head, viewGroup) as ViewGroup) {
        val headerImage : ImageView

        init {
            headerImage = itemView.findViewById(R.id.itemMainDrawerHeaderImage)
        }
    }
    private inner class DrawerRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        val TYPE_HEADER = 0
        val TYPE_ITEM = 1

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val drawerItem = displayList[position]
            when(holder){
                is DrawerHeaderViewHolder -> {
                    holder.headerImage.loadImage(R.mipmap.test_img_bg)
                }
                is SimpleItemViewHolder -> {
                    //界面初始化
                    holder.switch.visibility = View.GONE
                    holder.startIcon.visibility = if (drawerItem.isCategory()) View.GONE else View.VISIBLE
                    holder.subTitleText.visibility = if (drawerItem.isCategory()) View.GONE else View.VISIBLE
                    holder.endIcon.visibility = if (drawerItem.isCategory()) View.VISIBLE else View.GONE
                    holder.titleText.text = drawerItem.getTitle()
                    if (drawerItem.isCategory()){
                        holder.endIcon.rotation = if( (drawerItem as CategoryDrawerItem).isExpand ) 180f else 0f
                    }
                    //逻辑操作
                    holder.itemView.setOnClickListener {
                        if (drawerItem.isCategory()){///////////////////类别被点击了
                            val categoryDrawerItem = drawerItem as CategoryDrawerItem
                            if (categoryDrawerItem.isExpand){//合上
                                categoryDrawerItem.isExpand = false
                                displayList.removeAll(categoryDrawerItem.getDrawerItemList())
                                drawerAdapter.notifyItemChanged(holder.adapterPosition)
                                drawerAdapter.notifyItemRangeRemoved(holder.adapterPosition + 1,categoryDrawerItem.getDrawerItemList().size)
                            }else{//展开
                                categoryDrawerItem.isExpand = true
                                displayList.addAll(holder.adapterPosition + 1,categoryDrawerItem.getDrawerItemList())
                                drawerAdapter.notifyItemChanged(holder.adapterPosition)
                                drawerAdapter.notifyItemRangeInserted(holder.adapterPosition + 1,categoryDrawerItem.getDrawerItemList().size)
                            }
                        }else{///////////////////////条目被点击了

                        }
                    }
                }
            }
        }

        override fun getItemCount(): Int = displayList.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            when(viewType){
                TYPE_HEADER -> {
                    return DrawerHeaderViewHolder(parent)
                }
                else -> {
                    return SimpleItemViewHolder(this@MainActivity,parent)
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            when(displayList[position].type){
                DrawerItem.Type.HEADER -> {
                    return TYPE_HEADER
                }
                else -> {
                    return TYPE_ITEM
                }
            }
        }

    }
    private inner class TabViewPagerAdapter : FragmentPagerAdapter(fragmentManager){
        override fun getItem(position: Int): Fragment = categoryWindow.tabFragmentList[position]

        override fun getCount(): Int = categoryWindow.tabFragmentList.size

    }

    override fun onBackPressed() {
        if (!categoryWindow.tabFragmentList[mainViewPager.currentItem].onBackPressed()){
            super.onBackPressed()
        }
    }
}
