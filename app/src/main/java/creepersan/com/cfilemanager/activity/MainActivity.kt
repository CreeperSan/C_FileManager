package creepersan.com.cfilemanager.activity

import android.animation.Animator
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import creepersan.com.cfilemanager.R
import creepersan.com.cfilemanager.base.BaseActivity
import creepersan.com.cfilemanager.base.BaseTabFragment
import creepersan.com.cfilemanager.views.holder.SimpleItemViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseActivity() {
    private var tabFragmentList = ArrayList<BaseTabFragment>()          //打开的页面列表
    private var categoryDrawerItemList = ArrayList<CategoryDrawerItem>()                //滑动展开列表队列
    private lateinit var drawerAdapter : DrawerRecyclerViewAdapter

    override fun getLayoutID(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAnimation()
        initDrawerRecyclerViewData()
        initDrawerRecyclerView()

    }

    /**
     * 各种的初始化
     */
    private fun initAnimation(){

    }
    private fun initDrawerRecyclerViewData(){
        categoryDrawerItemList.add(CategoryDrawerItem(DrawerItem.Type.HEADER))
        categoryDrawerItemList.add(CategoryDrawerItem(DrawerItem.Type.CATEGORY_WINDOW))
        val localCategoryDrawerItem = CategoryDrawerItem(DrawerItem.Type.CATEGORY_LOCAL)
        localCategoryDrawerItem.add(LocalDrawerItem(DrawerItem.Type.ITEM_LOCAL_HOME))
        localCategoryDrawerItem.add(LocalDrawerItem(DrawerItem.Type.ITEM_LOCAL_EXTERNAL))
        localCategoryDrawerItem.add(LocalDrawerItem(DrawerItem.Type.ITEM_LOCAL_ROOT))
        categoryDrawerItemList.add(localCategoryDrawerItem)
        categoryDrawerItemList.add(CategoryDrawerItem(DrawerItem.Type.CATEGORY_BOOKMARK))
        val libraryCategoryDrawerItem = CategoryDrawerItem(DrawerItem.Type.CATEGORY_LIBRARY)
        libraryCategoryDrawerItem.add(LibraryDrawerItem(DrawerItem.Type.ITEM_LIBRARY_IMAGE))
        libraryCategoryDrawerItem.add(LibraryDrawerItem(DrawerItem.Type.ITEM_LIBRARY_VIDEO))
        libraryCategoryDrawerItem.add(LibraryDrawerItem(DrawerItem.Type.ITEM_LIBRARY_MUSIC))
        libraryCategoryDrawerItem.add(LibraryDrawerItem(DrawerItem.Type.ITEM_LIBRARY_APPLICATION))
        categoryDrawerItemList.add(libraryCategoryDrawerItem)
        val toolsCategoryDrawerItem = CategoryDrawerItem(DrawerItem.Type.CATEGORY_TOOLS)
        toolsCategoryDrawerItem.add(ToolsDrawerItem(DrawerItem.Type.ITEM_TOOLS_FILE_TRANSFER))
        categoryDrawerItemList.add(toolsCategoryDrawerItem)

    }
    private fun initDrawerRecyclerView() {
        drawerAdapter = DrawerRecyclerViewAdapter()
        mainDrawerRecyclerView.layoutManager = LinearLayoutManager(this)
        mainDrawerRecyclerView.adapter = drawerAdapter
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
            return type/1000 == 0
        }
    }
    private inner class CategoryDrawerItem(type: Int) : DrawerItem(type){

        var isExpand = false
        var innerDrawerItemList = ArrayList<DrawerItem>()

        fun add(item:DrawerItem){
            innerDrawerItemList.add(item)
        }
        fun size(): Int {
            return if (isExpand) 1+innerDrawerItemList.size else 1
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
    private inner class WindowDrawerItem(type: Int) : DrawerItem(type){
        override fun getTitle(): String {
            return "窗口"
        }

    }
    private inner class LocalDrawerItem(type: Int) : DrawerItem(type){
        override fun getTitle(): String {
            when(type){
                Type.ITEM_LOCAL_HOME -> return getString(R.string.localHome)
                Type.ITEM_LOCAL_EXTERNAL -> return getString(R.string.localExternal)
                Type.ITEM_LOCAL_ROOT -> return getString(R.string.localRoot)
                else -> return ""
            }
        }

    }
    private inner class BookmarkDrawerItem(type: Int) : DrawerItem(type){
        override fun getTitle(): String {
            return ""
        }
    }
    private inner class LibraryDrawerItem(type: Int) : DrawerItem(type){
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
    private inner class ToolsDrawerItem(type: Int) : DrawerItem(type){
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

        val animRotateTo180 : Animator
        val animRotateTo0 : Animator

        init {
            animRotateTo180 = loadAnimator(R.animator.rotate_0_to_180)
            animRotateTo0 = loadAnimator(R.animator.rotate_180_to_0)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when(holder){
                is DrawerHeaderViewHolder -> {
                    holder.headerImage.loadImage(R.mipmap.test_img_bg)
                }
                is SimpleItemViewHolder -> {
                    val drawerItem = categoryDrawerItemList[position]
                    holder.titleText.text = drawerItem.getTitle()
                }
            }
        }

        override fun getItemCount(): Int {
            var count = 0
            for (tempCategory in categoryDrawerItemList){
                count++
                if (tempCategory.isExpand){
                    count += tempCategory.innerDrawerItemList.size
                }
            }
            return count
        }

        fun getItem(pos:Int){
            var count = 0
            for (tempCategory in categoryDrawerItemList){
                Arrays
                count += tempCategory.size()
                if (count)
            }
        }

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
            when(categoryDrawerItemList[position].type){
                DrawerItem.Type.HEADER -> {
                    return TYPE_HEADER
                }
                else -> {
                    return TYPE_ITEM
                }
            }
        }

    }

}
