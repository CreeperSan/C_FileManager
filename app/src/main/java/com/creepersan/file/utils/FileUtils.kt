package com.creepersan.file.utils

import android.content.Context
import com.creepersan.file.R
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


private val mediumSimpleDateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
private val shortSimpleDateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
private val mDecimalFormatter = DecimalFormat("#0.00")

fun File.getTypeName(context:Context):String{
    return if (this.isDirectory){
         context.getString(R.string.fileTypeFolder)
    }else{
        context.getString(R.string.fileTypeFile)
    }
}

fun File.getTypeIconID():Int{
    return if (this.isDirectory){
        R.drawable.ic_file_folder
    }else{
        R.drawable.ic_file_file
    }
}

fun File.getFileSizeByte(context:Context):String{
    return String.format(context.getString(R.string.fileFragmentItemSizeB), this.length())
}

fun File.getFileSize(context:Context):String{
    var fileSize = this.length().toDouble()
    var sizeLevel = 0
    while (fileSize > 1024 && sizeLevel < 4){
        fileSize %= 1024
        sizeLevel += 1
    }
    val stringID = when(sizeLevel){
        0 -> { R.string.fileFragmentItemSizeB }
        1 -> { R.string.fileFragmentItemSizeKB }
        2 -> { R.string.fileFragmentItemSizeMB }
        3 -> { R.string.fileFragmentItemSizeGB }
        4 -> { R.string.fileFragmentItemSizeTB }
        else -> { R.string.fileFragmentItemSizeTB }
    }
    return String.format(context.getString(stringID), mDecimalFormatter.format(fileSize))
}

fun File.getFileModifyTimeString():String{
    return mediumSimpleDateFormatter.format(this.lastModified())
}

fun File.getFileModifyTimeShortString():String{
    return shortSimpleDateFormatter.format(this.lastModified())
}

fun File.isReadableString(context:Context):String{
    return if (this.canRead()){
        context.getString(R.string.booleanTrue)
    }else{
        context.getString(R.string.booleanFalse)
    }
}

fun File.isWritableString(context:Context):String{
    return if (this.canWrite()){
        context.getString(R.string.booleanTrue)
    }else{
        context.getString(R.string.booleanFalse)
    }
}

fun File.isExecutableString(context:Context):String{
    return if (this.canExecute()){
        context.getString(R.string.booleanTrue)
    }else{
        context.getString(R.string.booleanFalse)
    }
}

fun File.isHiddenString(context:Context):String{
    return if (this.isHidden){
        context.getString(R.string.booleanTrue)
    }else{
        context.getString(R.string.booleanFalse)
    }
}


