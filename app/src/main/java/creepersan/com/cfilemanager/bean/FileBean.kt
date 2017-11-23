package creepersan.com.cfilemanager.bean

import creepersan.com.cfilemanager.util.FormatHelper
import java.io.File

/** 文件Item
 * Created by CreeperSan on 2017/11/19.
 */
class FileBean(file:File){
    var path = ""
    var displayName = ""
    var modifyTime = ""
    var innerItemCounts = 0
    var isHidden = false
    var displaySize = ""
    //自动判断
    var fileType = 0

    init {
        path = file.absolutePath
        displayName = file.name
        modifyTime = FormatHelper.formatTime(file.lastModified())
        innerItemCounts = if (file.isDirectory && file.exists()){file.list().size}else{0}
        isHidden = file.isHidden
        //自动判断的变量
        if (!file.exists()){
            fileType = FileType.NONE
        }else if (file.isDirectory){
            fileType = FileType.FOLDER
        }else{
            when(getSuffix().toLowerCase()){
                "mp3","wav","ogg" ,"flac","wma","midi"
                        ->{ fileType = FileType.MUSIC }
                "avi","mp4","wmv","rm","rmvb","3gp"
                        -> {fileType = FileType.VIDEO}
                "bmp","gif","jpg","png","tga","jpeg","svg"
                        -> {fileType = FileType.IMAGE}
                "doc","docx","txt","xls","xlcx","ppt","pptx","pdf"
                        -> {fileType = FileType.DOCUMENT}
                "apk"
                        -> {fileType = FileType.APK}
                "zip","rar","7z"
                        -> {fileType = FileType.ZIP}
                else
                        -> {fileType = FileType.FILE}
            }
            val size = file.length()
            if (size < 1024){   //byte
                displaySize = "$size B"
            }else if (size < 1024 * 1024){  //KB
                displaySize = "${FormatHelper.getFormatDecimal(size/1024.toFloat())} KB"
            }else if (size < 1024 * 1024 * 1024){   //MB
                displaySize = "${FormatHelper.getFormatDecimal(size/1024/1024.toFloat())} MB"
            }else if (size < 1024L * 1024 * 1024 * 1024){    //GB
                displaySize = "${FormatHelper.getFormatDecimal(size/1024/1024.toFloat())} GB"
            }else if (size < 1024L * 1024 * 1024 * 1024 * 1024){    //TB
                displaySize = "${FormatHelper.getFormatDecimal(size/1024/1024/1024.toFloat())} TB"
            }else{
                displaySize = "Ultra Big"   //TODO 字符串
            }
        }
    }

    object FileType{
        val NONE = -1
        val FILE = 0
        val FOLDER = 1
        val IMAGE = 2
        val MUSIC = 3
        val VIDEO = 4
        val DOCUMENT = 5
        val APK = 6
        val ZIP = 7
    }

    private fun getSuffix():String{
        return path.substring(path.lastIndexOf(".") + 1)
    }
}