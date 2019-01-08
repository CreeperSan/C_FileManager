package com.creepersan.file.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import com.creepersan.file.R
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


private val mediumSimpleDateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
private val shortSimpleDateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
private const val FILE_POSTFIX_CHAR = "."


private const val FILE_POSTFIX_EMPTY = ""

private const val FILE_POSTFIX_TEXT_TXT       = "txt"

private const val FILE_POSTFIX_AUDIO_MP3      = "mp3"
private const val FILE_POSTFIX_AUDIO_WAV      = "wav"
private const val FILE_POSTFIX_AUDIO_FLAC     = "flac"
private const val FILE_POSTFIX_AUDIO_APE      = "ape"
private const val FILE_POSTFIX_AUDIO_ALAC     = "alac"
private const val FILE_POSTFIX_AUDIO_WV       = "wv"
private const val FILE_POSTFIX_AUDIO_AAC      = "aac"
private const val FILE_POSTFIX_AUDIO_OGG      = "ogg"

private const val FILE_POSTFIX_VIDEO_MPEG     = "mpeg"
private const val FILE_POSTFIX_VIDEO_AVI      = "avi"
private const val FILE_POSTFIX_VIDEO_MOV      = "mov"
private const val FILE_POSTFIX_VIDEO_ASF      = "asf"
private const val FILE_POSTFIX_VIDEO_WMV      = "wmv"
private const val FILE_POSTFIX_VIDEO_NAVI     = "navi"
private const val FILE_POSTFIX_VIDEO_3GP      = "3gp"
private const val FILE_POSTFIX_VIDEO_RA       = "ra"
private const val FILE_POSTFIX_VIDEO_RAM      = "ram"
private const val FILE_POSTFIX_VIDEO_MKV      = "mkv"
private const val FILE_POSTFIX_VIDEO_FLV      = "flv"
private const val FILE_POSTFIX_VIDEO_F4V      = "f4v"
private const val FILE_POSTFIX_VIDEO_RMVB     = "rmvb"
private const val FILE_POSTFIX_VIDEO_WEBM     = "webm"
private const val FILE_POSTFIX_VIDEO_BD       = "bd"
private const val FILE_POSTFIX_VIDEO_DVD      = "dvd"
private const val FILE_POSTFIX_VIDEO_MP4      = "mp4"

private const val FILE_POSTFIX_IMAGE_WEBP     = "webp"
private const val FILE_POSTFIX_IMAGE_BMP      = "bmp"
private const val FILE_POSTFIX_IMAGE_PCX      = "pcx"
private const val FILE_POSTFIX_IMAGE_TIF      = "tif"
private const val FILE_POSTFIX_IMAGE_GIF      = "gif"
private const val FILE_POSTFIX_IMAGE_JPEG     = "jpeg"
private const val FILE_POSTFIX_IMAGE_JPG      = "jpg"
private const val FILE_POSTFIX_IMAGE_TGA      = "tga"
private const val FILE_POSTFIX_IMAGE_EXIF     = "exif"
private const val FILE_POSTFIX_IMAGE_FPX      = "fpx"
private const val FILE_POSTFIX_IMAGE_SVG      = "svg"
private const val FILE_POSTFIX_IMAGE_PSD      = "psd"
private const val FILE_POSTFIX_IMAGE_CDR      = "cdr"
private const val FILE_POSTFIX_IMAGE_PCD      = "pcd"
private const val FILE_POSTFIX_IMAGE_DXF      = "dxf"
private const val FILE_POSTFIX_IMAGE_UFO      = "ufo"
private const val FILE_POSTFIX_IMAGE_EPS      = "eps"
private const val FILE_POSTFIX_IMAGE_AI       = "ai"
private const val FILE_POSTFIX_IMAGE_PNG      = "png"
private const val FILE_POSTFIX_IMAGE_HDRI     = "hdri"
private const val FILE_POSTFIX_IMAGE_RAW      = "raw"
private const val FILE_POSTFIX_IMAGE_WMF      = "wmf"
private const val FILE_POSTFIX_IMAGE_FLIC     = "flic"
private const val FILE_POSTFIX_IMAGE_EMF      = "emf"
private const val FILE_POSTFIX_IMAGE_ICO      = "ico"

private const val FILE_POSTFIX_ANDROID_APK     = "apk"

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
    return this.length().toFormattedStorageString(context)
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

/* 获取文件后缀名称 */
fun File.getPostFix():String{
    val name = this.name
    return if (name.contains(FILE_POSTFIX_CHAR)){
        name.split(FILE_POSTFIX_CHAR).last()
    }else{
        FILE_POSTFIX_EMPTY
    }
}

fun File.openFileWithIntent(context: Context):Boolean{
    if (!this.exists() || this.isDirectory){
        return false
    }
    when(this.getPostFix()){
        FILE_POSTFIX_EMPTY -> {
            openFile(context, this, "*/*")
        }
        FILE_POSTFIX_TEXT_TXT -> {
            openFile(context, this, "text/plain")
        }
        FILE_POSTFIX_AUDIO_MP3,
        FILE_POSTFIX_AUDIO_WAV,
        FILE_POSTFIX_AUDIO_FLAC,
        FILE_POSTFIX_AUDIO_APE,
        FILE_POSTFIX_AUDIO_ALAC,
        FILE_POSTFIX_AUDIO_WV,
        FILE_POSTFIX_AUDIO_AAC,
        FILE_POSTFIX_AUDIO_OGG -> {
            openFile(context, this, "audio/*")
        }
        FILE_POSTFIX_VIDEO_MPEG,
        FILE_POSTFIX_VIDEO_AVI,
        FILE_POSTFIX_VIDEO_MOV,
        FILE_POSTFIX_VIDEO_ASF,
        FILE_POSTFIX_VIDEO_WMV,
        FILE_POSTFIX_VIDEO_NAVI,
        FILE_POSTFIX_VIDEO_3GP,
        FILE_POSTFIX_VIDEO_RA,
        FILE_POSTFIX_VIDEO_RAM,
        FILE_POSTFIX_VIDEO_MKV,
        FILE_POSTFIX_VIDEO_FLV,
        FILE_POSTFIX_VIDEO_F4V,
        FILE_POSTFIX_VIDEO_RMVB,
        FILE_POSTFIX_VIDEO_WEBM,
        FILE_POSTFIX_VIDEO_BD,
        FILE_POSTFIX_VIDEO_MP4,
        FILE_POSTFIX_VIDEO_DVD -> {
            openFile(context, this, "video/*")
        }
        FILE_POSTFIX_IMAGE_WEBP,
        FILE_POSTFIX_IMAGE_BMP,
        FILE_POSTFIX_IMAGE_PCX,
        FILE_POSTFIX_IMAGE_TIF,
        FILE_POSTFIX_IMAGE_GIF,
        FILE_POSTFIX_IMAGE_JPEG,
        FILE_POSTFIX_IMAGE_JPG,
        FILE_POSTFIX_IMAGE_TGA,
        FILE_POSTFIX_IMAGE_EXIF,
        FILE_POSTFIX_IMAGE_FPX,
        FILE_POSTFIX_IMAGE_SVG,
        FILE_POSTFIX_IMAGE_PSD,
        FILE_POSTFIX_IMAGE_CDR,
        FILE_POSTFIX_IMAGE_PCD,
        FILE_POSTFIX_IMAGE_DXF,
        FILE_POSTFIX_IMAGE_UFO,
        FILE_POSTFIX_IMAGE_EPS,
        FILE_POSTFIX_IMAGE_AI,
        FILE_POSTFIX_IMAGE_PNG,
        FILE_POSTFIX_IMAGE_HDRI,
        FILE_POSTFIX_IMAGE_RAW,
        FILE_POSTFIX_IMAGE_WMF,
        FILE_POSTFIX_IMAGE_FLIC,
        FILE_POSTFIX_IMAGE_EMF,
        FILE_POSTFIX_IMAGE_ICO -> {
            openFile(context, this, "image/*")
        }
        FILE_POSTFIX_ANDROID_APK -> {
            openFile(context, this, "application/*")
        }
    }
    return true
}

private fun openFile(context: Context, file: File, type:String){
    val uri = if (Build.VERSION.SDK_INT >= 24){
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        Uri.fromFile(file)
    }else{
        Uri.fromFile(file)
    }
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.setDataAndType(uri, type)
    context.startActivity(intent)
}


