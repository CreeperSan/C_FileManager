package com.creepersan.file.fragment

import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.creepersan.file.FileApplication
import com.creepersan.file.R
import com.creepersan.file.bean.FileItem
import com.creepersan.file.utils.ConfigUtil
import kotlinx.android.synthetic.main.fragment_file.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class FileFragment : BaseFragment(){
    override val mLayoutID: Int = R.layout.fragment_file
    private val mFileAdapter by lazy { FileAdapter() }
    private val mPathAdapter by lazy { PathAdapter() }
    private val mFileStack by lazy { LinkedList<FileStackInfo>() }

    private var mTmpFileRecyclerViewScrollPos = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initStack()
        initPathRecyclerView()
        initFileRecyclerView()
    }

    private fun initStack(){
        mFileStack.push(FileStackInfo.fromExternalStorage())
    }
    private fun initPathRecyclerView(){
        fragmentFilePathRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        fragmentFilePathRecyclerView.adapter = mPathAdapter

    }
    private fun initFileRecyclerView(){
        fragmentFileFileRecyclerView.layoutManager = LinearLayoutManager(activity)
        fragmentFileFileRecyclerView.adapter = mFileAdapter
    }

    /**
     *  操作统一入口
     */
    fun updatePathRecyclerView(){
        mPathAdapter.notifyDataSetChanged()
        fragmentFilePathRecyclerView.scrollToPosition(mFileStack.size-1)
    }
    fun updateFileRecyclerView(){
        mFileAdapter.notifyDataSetChanged()

        val layoutManager = fragmentFileFileRecyclerView.layoutManager

        when(layoutManager){
            is LinearLayoutManager -> {
                layoutManager.scrollToPositionWithOffset(mFileStack.last.mScrollYAxisPosition,0)
            }
        }

    }

    /**
     *  事件回调
     */
    fun onBackPressed():Boolean{
        return if (mFileStack.size > 1){
            mFileStack.removeLast()
            updatePathRecyclerView()
            updateFileRecyclerView()
            true
        }else{
            false
        }
    }


    /**
     *  列表用的Adapter 和 Holder 之类的内容
     */

    inner class FileAdapter : RecyclerView.Adapter<FileHolder>(){
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FileHolder {
            return FileHolder(p0)
        }

        override fun getItemCount(): Int {
            return mFileStack.last.mFileList.size
        }

        override fun onBindViewHolder(holder: FileHolder, p1: Int) {
            val item = mFileStack.last.mFileList[p1]
            val layoutManager = fragmentFileFileRecyclerView.layoutManager

            holder.icon.setImageResource(item.icon)
            holder.title.text = item.name
            holder.more.setImageResource(R.drawable.ic_file_more)
            holder.contentExtra.text = item.modifyTime
            holder.content.text = item.size

            holder.itemView.setOnClickListener{
                if(item.isFolder){
                    // 记录滚动距离
                    when(layoutManager){
                        is LinearLayoutManager -> {
                            mTmpFileRecyclerViewScrollPos = layoutManager.findFirstVisibleItemPosition()
                        }
                    }
                    mFileStack.last.mScrollYAxisPosition = mTmpFileRecyclerViewScrollPos
                    mTmpFileRecyclerViewScrollPos = 0
                    // 添加新的文件栈
                    mFileStack.addLast(FileStackInfo.fromFile(item.getFile()))
                }else{
                    toast(item.size.toString())
                }

                updatePathRecyclerView()
                updateFileRecyclerView()
            }
            holder.itemView.setOnLongClickListener{
                toast(getString(R.string.debugProcessing))
                true
            }
        }
    }
    inner class FileHolder(container:ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_file_file, container, false)){
        val icon = itemView.findViewById<ImageView>(R.id.itemFileFileIcon)
        val more = itemView.findViewById<ImageView>(R.id.itemFileFileMore)
        val title = itemView.findViewById<TextView>(R.id.itemFileFileTitle)
        val content = itemView.findViewById<TextView>(R.id.itemFileFileContent)
        val contentExtra = itemView.findViewById<TextView>(R.id.itemFileFileContentExtra)
    }
    inner class PathAdapter : RecyclerView.Adapter<PathHolder>(){
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PathHolder {
            return PathHolder(p0)
        }

        override fun getItemCount(): Int {
            return mFileStack.size
        }

        override fun onBindViewHolder(holder: PathHolder, pos: Int) {
            val item = mFileStack[pos]

            holder.icon.setImageResource(R.drawable.ic_file_path_diver)
            holder.title.text = item.mName

            holder.itemView.setOnClickListener {
                val tmpPos = holder.adapterPosition
                if (tmpPos+1 < mFileStack.size){
                    mFileStack.removeLast()
                }

                updatePathRecyclerView()
                updateFileRecyclerView()
            }

        }

    }
    inner class PathHolder(container: ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_file_path, container, false)){
        val icon = itemView.findViewById<ImageView>(R.id.itemFilePathIcon)
        val title = itemView.findViewById<TextView>(R.id.itemFilePathTitle)
    }

    /**
     * Item Bean
     */
    class FileStackInfo private constructor(){

        companion object {
            fun fromExternalStorage():FileStackInfo{
                return fromFile(Environment.getExternalStorageDirectory())
            }

            fun fromFile(file:File):FileStackInfo{
                val mStackInfo = FileStackInfo()
                // 初始化当前目录的基础信息
                mStackInfo.mName = file.name
                mStackInfo.mPath = file.path

                // 初始化当前目录下的文件信息
                val tmpFileList = ArrayList<File>()
                val tmpFolderList = ArrayList<File>()
                val tmpConfig = FileApplication.getConfigInstance()
                val isFolderFirst = tmpConfig.getFileIsFolderFirst()
                val isShowHiddenFile = tmpConfig.getFileIsShowHiddenFile()
                val sortType = tmpConfig.getFileSortOrder()
                val isReverse = tmpConfig.getFileIsOrderReverse()
                val tmpSort = file.listFiles().toMutableList()

                when(sortType){
                    ConfigUtil.VAL_FILE_SORT_TYPE -> {
                        if (isReverse){
                            tmpSort.sortByDescending { it.name }
                        }else{
                            tmpSort.sortBy { it.name }
                        }
                    }
                    ConfigUtil.VAL_FILE_SORT_SIZE -> {
                        if (isReverse){
                            tmpSort.sortByDescending { it.length() }
                        }else{
                            tmpSort.sortBy { it.length() }
                        }
                    }
                    ConfigUtil.VAL_FILE_SORT_MODIFY_TIME -> {
                        if (isReverse){
                            tmpSort.sortByDescending { it.lastModified() }
                        }else{
                            tmpSort.sortBy { it.lastModified() }
                        }
                    }
                    ConfigUtil.VAL_FILE_SORT_NAME -> {
                        if (isReverse){
                            tmpSort.sortByDescending { it.name }
                        }else{
                            tmpSort.sortBy { it.name }
                        }
                    }
                }
                tmpSort.forEach { tmpFile ->
                    if (tmpFile.isHidden && !isShowHiddenFile){
                        return@forEach // 如果是隐藏文件并且不显示隐藏文件的话就跳过文件的扫描
                    }
                    if (isFolderFirst){
                        if (tmpFile.isDirectory){
                            tmpFolderList.add(tmpFile)
                        }else{
                            tmpFileList.add(tmpFile)
                        }
                    }else{
                        tmpFileList.add(tmpFile)
                    }
                }

                // 添加数据
                tmpFolderList.forEach { mStackInfo.mFileList.add(FileItem.fromFile(it)) }
                tmpFileList.forEach { mStackInfo.mFileList.add(FileItem.fromFile(it)) }

                return mStackInfo
            }

            fun fromPath(path:String):FileStackInfo{
                return fromFile(File(path))
            }
        }

        var mFileList = ArrayList<FileItem>()
        var mScrollYAxisPosition = 0
        var mName = ""
        var mPath = ""


    }


}