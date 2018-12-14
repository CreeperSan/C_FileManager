package com.creepersan.file.bean

import android.content.Context
import com.creepersan.file.FileApplication
import com.creepersan.file.R
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class FileItem private constructor(){
    companion object {
        private var mTimeFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        private val mDecimalFormatter = DecimalFormat("#.00")

        fun fromFile(file:File):FileItem{
            val item = FileItem()
            item.name = file.name
            item.path = file.path
            item.isHidden = file.isHidden
            item.isFolder = file.isDirectory

            val app = FileApplication.getInstance()

            // 计算大小文本
            if (item.isFolder){// 文件夹的
                val itemCount = file.list().size.toLong()
                if (itemCount > 1){
                    item.size = String.format(app.getString(R.string.fileFragmentItemCounts), itemCount)
                }else{
                    item.size = String.format(app.getString(R.string.fileFragmentItemCount), itemCount)
                }
            }else{ // 文件的
                val itemSize = file.length()
                when {
                    itemSize < 1024L -> item.size = String.format(app.getString(R.string.fileFragmentItemSizeB), mDecimalFormatter.format(itemSize.toDouble()))
                    itemSize < 1024*1024L -> item.size = String.format(app.getString(R.string.fileFragmentItemSizeB), mDecimalFormatter.format(itemSize.toDouble()/1024))
                    itemSize < 1024*1024*1024L -> item.size = String.format(app.getString(R.string.fileFragmentItemSizeB), mDecimalFormatter.format(itemSize.toDouble()/1024/1024))
                    itemSize < 1024*1024*1024*1024L -> item.size = String.format(app.getString(R.string.fileFragmentItemSizeB), mDecimalFormatter.format(itemSize.toDouble()/1024/1024/1024))
                    else -> item.size = String.format(app.getString(R.string.fileFragmentItemSizeB), mDecimalFormatter.format(itemSize.toDouble()))
                }
            }

            // 计算时间
            val tmpModifyTime = file.lastModified()
            item.modifyTime = mTimeFormatter.format(tmpModifyTime)

            // 获取图标
            if (item.isFolder){
                item.icon = R.drawable.ic_file_folder
            }else{
                item.icon = R.drawable.ic_file_file
            }
            return item
        }
    }

    fun getFile():File{
        return File(path)
    }

    var name : String = ""
    var path : String = ""
    var icon : Int = 0
    var size : String = ""
    var isHidden : Boolean = false
    var isFolder : Boolean = false
    var modifyTime : String = ""

}