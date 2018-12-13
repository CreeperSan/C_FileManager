package com.creepersan.file.fragment

import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.creepersan.file.R
import com.creepersan.file.bean.FileItem
import kotlinx.android.synthetic.main.fragment_file.*
import java.io.File
import java.util.*

class FileFragment : BaseFragment(){
    override val mLayoutID: Int = R.layout.fragment_file
    private val mFileAdapter by lazy { FileAdapter() }
    private val mPathAdapter by lazy { PathAdapter() }
    private val mFileStack by lazy { LinkedList<FileStackInfo>() }


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
     *  事件回调
     */
    fun onBackPressed():Boolean{
        return if (mFileStack.size > 1){
            mFileStack.removeLast()
            mFileAdapter.notifyDataSetChanged()
            mPathAdapter.notifyDataSetChanged()
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

            holder.icon.setImageResource(item.icon)
            holder.title.text = item.name
            holder.more.setImageResource(R.drawable.ic_file_more)
            holder.contentExtra.text = item.modifyTime
            holder.content.text = item.size

            holder.itemView.setOnClickListener{
                if(item.isFolder){
                    mFileStack.addLast(FileStackInfo.fromFile(item.getFile()))
                }else{
                    toast(item.size.toString())
                }

                mFileAdapter.notifyDataSetChanged()
                mPathAdapter.notifyDataSetChanged()
                fragmentFilePathRecyclerView.smoothScrollToPosition(mFileStack.size)
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

                mFileAdapter.notifyDataSetChanged()
                mPathAdapter.notifyDataSetChanged()
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
                file.listFiles().forEach { tmpFile ->
                    mStackInfo.mFileList.add(FileItem.fromFile(tmpFile))
                }
                return mStackInfo
            }

            fun fromPath(path:String):FileStackInfo{
                return fromFile(File(path))
            }
        }

        var mFileList = ArrayList<FileItem>()
        var mName = ""
        var mPath = ""


    }

}