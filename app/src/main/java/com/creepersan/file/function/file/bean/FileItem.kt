package com.creepersan.file.function.file.bean

import android.content.Context
import com.creepersan.file.FileApplication
import com.creepersan.file.R
import com.creepersan.file.utils.getFileModifyTimeShortString
import com.creepersan.file.utils.getFileSize
import com.creepersan.file.utils.getTypeIconID
import java.io.File

class FileItem private constructor(){
    companion object {
        fun fromFile(file:File, context: Context):FileItem{
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
                item.size = file.getFileSize(context)
            }

            // 计算时间
            item.modifyTime = file.getFileModifyTimeShortString()

            // 获取图标
            item.icon = file.getTypeIconID()
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