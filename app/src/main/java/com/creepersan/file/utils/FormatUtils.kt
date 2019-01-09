package com.creepersan.file.utils

import android.content.Context
import com.creepersan.file.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat


private val mDecimalFormatter = DecimalFormat("#0.00")
private val mMusicPlayerTimeFormatter = SimpleDateFormat.getDateTimeInstance()

fun Long.toFormattedStorageString(context: Context):String{
    var fileSize = this.toDouble()
    var sizeLevel = 0
    while (fileSize > 1024 && sizeLevel < 4){
        fileSize /= 1024
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
fun Int.toFormattedHourMinuteTime():String{
    val hour = this / 60 / 1000
    if (hour > 99){
        return "99:99"
    }
    var hourStr = ""
    hourStr = if (hour >= 10){
        hour.toString()
    }else{
        "0"+hour.toString()
    }

    val second = this / 1000 % 60
    var secondStr = ""
    secondStr = if (second >= 10){
        second.toString()
    }else{
        "0"+second.toString()
    }

    return "$hourStr:$secondStr"

}
