package com.creepersan.file.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import com.creepersan.file.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


private val mediumSimpleDateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
private val shortSimpleDateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
private const val FILE_POSTFIX_CHAR = "."


private const val FILE_POSTFIX_EMPTY = ""

private const val FILE_POSTFIX_TEXT_TXT         = "txt"
private const val FILE_POSTFIX_TEXT_JAVA        = "java"
private const val FILE_POSTFIX_TEXT_PY          = "py"
private const val FILE_POSTFIX_TEXT_PYC         = "pyc"
private const val FILE_POSTFIX_TEXT_C           = "c"
private const val FILE_POSTFIX_TEXT_CPP         = "cpp"
private const val FILE_POSTFIX_TEXT_H           = "h"
private const val FILE_POSTFIX_TEXT_M           = "m"
private const val FILE_POSTFIX_TEXT_CS          = "cs"
private const val FILE_POSTFIX_TEXT_GROOVY      = "groovy"
private const val FILE_POSTFIX_TEXT_SWIFT       = "swift"
private const val FILE_POSTFIX_TEXT_JS          = "js"
private const val FILE_POSTFIX_TEXT_GO          = "go"
private const val FILE_POSTFIX_TEXT_PHP         = "php"
private const val FILE_POSTFIX_TEXT_ERL         = "erl"
private const val FILE_POSTFIX_TEXT_HRL         = "hrl"
private const val FILE_POSTFIX_TEXT_RS          = "rs"
private const val FILE_POSTFIX_TEXT_RLIB        = "rlib"
private const val FILE_POSTFIX_TEXT_PL          = "pl"
private const val FILE_POSTFIX_TEXT_PM          = "pm"
private const val FILE_POSTFIX_TEXT_T           = "t"
private const val FILE_POSTFIX_TEXT_POD         = "pod"
private const val FILE_POSTFIX_TEXT_LUA         = "lua"
private const val FILE_POSTFIX_TEXT_DART        = "dart"
private const val FILE_POSTFIX_TEXT_TS          = "ts"
private const val FILE_POSTFIX_TEXT_S           = "s"
private const val FILE_POSTFIX_TEXT_RB          = "rb"
private const val FILE_POSTFIX_TEXT_CSS         = "css"
private const val FILE_POSTFIX_TEXT_HTML        = "html"
private const val FILE_POSTFIX_TEXT_XML         = "xml"
private const val FILE_POSTFIX_TEXT_JSON        = "json"
private const val FILE_POSTFIX_TEXT_MD          = "md"
private const val FILE_POSTFIX_TEXT_MARKDOWN    = "markdown"
private const val FILE_POSTFIX_TEXT_KT          = "kt"

private const val FILE_POSTFIX_AUDIO_MP3        = "mp3"
private const val FILE_POSTFIX_AUDIO_WAV        = "wav"
private const val FILE_POSTFIX_AUDIO_FLAC       = "flac"
private const val FILE_POSTFIX_AUDIO_APE        = "ape"
private const val FILE_POSTFIX_AUDIO_ALAC       = "alac"
private const val FILE_POSTFIX_AUDIO_WV         = "wv"
private const val FILE_POSTFIX_AUDIO_AAC        = "aac"
private const val FILE_POSTFIX_AUDIO_OGG        = "ogg"

private const val FILE_POSTFIX_VIDEO_MPEG       = "mpeg"
private const val FILE_POSTFIX_VIDEO_AVI        = "avi"
private const val FILE_POSTFIX_VIDEO_MOV        = "mov"
private const val FILE_POSTFIX_VIDEO_ASF        = "asf"
private const val FILE_POSTFIX_VIDEO_WMV        = "wmv"
private const val FILE_POSTFIX_VIDEO_NAVI       = "navi"
private const val FILE_POSTFIX_VIDEO_3GP        = "3gp"
private const val FILE_POSTFIX_VIDEO_RA         = "ra"
private const val FILE_POSTFIX_VIDEO_RAM        = "ram"
private const val FILE_POSTFIX_VIDEO_MKV        = "mkv"
private const val FILE_POSTFIX_VIDEO_FLV        = "flv"
private const val FILE_POSTFIX_VIDEO_F4V        = "f4v"
private const val FILE_POSTFIX_VIDEO_RMVB       = "rmvb"
private const val FILE_POSTFIX_VIDEO_WEBM       = "webm"
private const val FILE_POSTFIX_VIDEO_BD         = "bd"
private const val FILE_POSTFIX_VIDEO_DVD        = "dvd"
private const val FILE_POSTFIX_VIDEO_MP4        = "mp4"

private const val FILE_POSTFIX_IMAGE_WEBP       = "webp"
private const val FILE_POSTFIX_IMAGE_BMP        = "bmp"
private const val FILE_POSTFIX_IMAGE_PCX        = "pcx"
private const val FILE_POSTFIX_IMAGE_TIF        = "tif"
private const val FILE_POSTFIX_IMAGE_GIF        = "gif"
private const val FILE_POSTFIX_IMAGE_JPEG       = "jpeg"
private const val FILE_POSTFIX_IMAGE_JPG        = "jpg"
private const val FILE_POSTFIX_IMAGE_TGA        = "tga"
private const val FILE_POSTFIX_IMAGE_EXIF       = "exif"
private const val FILE_POSTFIX_IMAGE_FPX        = "fpx"
private const val FILE_POSTFIX_IMAGE_SVG        = "svg"
private const val FILE_POSTFIX_IMAGE_PSD        = "psd"
private const val FILE_POSTFIX_IMAGE_CDR        = "cdr"
private const val FILE_POSTFIX_IMAGE_DXF        = "dxf"
private const val FILE_POSTFIX_IMAGE_UFO        = "ufo"
private const val FILE_POSTFIX_IMAGE_EPS        = "eps"
private const val FILE_POSTFIX_IMAGE_AI         = "ai"
private const val FILE_POSTFIX_IMAGE_PNG        = "png"
private const val FILE_POSTFIX_IMAGE_HDRI       = "hdri"
private const val FILE_POSTFIX_IMAGE_RAW        = "raw"
private const val FILE_POSTFIX_IMAGE_WMF        = "wmf"
private const val FILE_POSTFIX_IMAGE_FLIC       = "flic"
private const val FILE_POSTFIX_IMAGE_EMF        = "emf"
private const val FILE_POSTFIX_IMAGE_ICO        = "ico"

private const val FILE_POSTFIX_ANDROID_APK      = "apk"

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
        when(this.getPostFix()){
            // 文本文档
            FILE_POSTFIX_TEXT_TXT -> R.drawable.ic_file_txt
            FILE_POSTFIX_TEXT_JAVA -> R.drawable.ic_file_java
            FILE_POSTFIX_TEXT_PY -> R.drawable.ic_file_python
            FILE_POSTFIX_TEXT_PYC -> R.drawable.ic_file_python
            FILE_POSTFIX_TEXT_C -> R.drawable.ic_file_c
            FILE_POSTFIX_TEXT_CPP -> R.drawable.ic_file_cpp
            FILE_POSTFIX_TEXT_H -> R.drawable.ic_file_h
            FILE_POSTFIX_TEXT_M -> R.drawable.ic_file_m
            FILE_POSTFIX_TEXT_CS -> R.drawable.ic_file_cs
            FILE_POSTFIX_TEXT_GROOVY -> R.drawable.ic_file_groovy
            FILE_POSTFIX_TEXT_SWIFT -> R.drawable.ic_file_swift
            FILE_POSTFIX_TEXT_JS -> R.drawable.ic_file_js
            FILE_POSTFIX_TEXT_GO -> R.drawable.ic_file_go
            FILE_POSTFIX_TEXT_PHP -> R.drawable.ic_file_php
            FILE_POSTFIX_TEXT_ERL -> R.drawable.ic_file_erlang
            FILE_POSTFIX_TEXT_HRL -> R.drawable.ic_file_erlang
            FILE_POSTFIX_TEXT_RS -> R.drawable.ic_file_rust
            FILE_POSTFIX_TEXT_RLIB -> R.drawable.ic_file_rust
            FILE_POSTFIX_TEXT_PL -> R.drawable.ic_file_perl
            FILE_POSTFIX_TEXT_PM -> R.drawable.ic_file_perl
            FILE_POSTFIX_TEXT_T -> R.drawable.ic_file_perl
            FILE_POSTFIX_TEXT_POD -> R.drawable.ic_file_perl
            FILE_POSTFIX_TEXT_LUA -> R.drawable.ic_file_lua
            FILE_POSTFIX_TEXT_DART -> R.drawable.ic_file_dart
            FILE_POSTFIX_TEXT_TS -> R.drawable.ic_file_ts
            FILE_POSTFIX_TEXT_S -> R.drawable.ic_file_s
            FILE_POSTFIX_TEXT_RB -> R.drawable.ic_file_rb
            FILE_POSTFIX_TEXT_CSS -> R.drawable.ic_file_css
            FILE_POSTFIX_TEXT_HTML -> R.drawable.ic_file_html
            FILE_POSTFIX_TEXT_XML -> R.drawable.ic_file_xml
            FILE_POSTFIX_TEXT_JSON -> R.drawable.ic_file_json
            FILE_POSTFIX_TEXT_MD -> R.drawable.ic_file_markdown
            FILE_POSTFIX_TEXT_MARKDOWN -> R.drawable.ic_file_markdown
            FILE_POSTFIX_TEXT_KT -> R.drawable.ic_file_kotlin
            // 音频文件
            FILE_POSTFIX_AUDIO_MP3 -> R.drawable.ic_file_mp3
            FILE_POSTFIX_AUDIO_WAV -> R.drawable.ic_file_wav
            FILE_POSTFIX_AUDIO_FLAC -> R.drawable.ic_file_flac
            FILE_POSTFIX_AUDIO_APE -> R.drawable.ic_file_audio
            FILE_POSTFIX_AUDIO_ALAC -> R.drawable.ic_file_alac
            FILE_POSTFIX_AUDIO_WV -> R.drawable.ic_file_audio
            FILE_POSTFIX_AUDIO_AAC -> R.drawable.ic_file_audio
            FILE_POSTFIX_AUDIO_OGG -> R.drawable.ic_file_ogg
            // 视频文件
            FILE_POSTFIX_VIDEO_MPEG -> R.drawable.ic_file_mpeg
            FILE_POSTFIX_VIDEO_AVI -> R.drawable.ic_file_avi
            FILE_POSTFIX_VIDEO_MOV -> R.drawable.ic_file_mov
            FILE_POSTFIX_VIDEO_ASF -> R.drawable.ic_file_video
            FILE_POSTFIX_VIDEO_WMV -> R.drawable.ic_file_wmv
            FILE_POSTFIX_VIDEO_NAVI -> R.drawable.ic_file_video
            FILE_POSTFIX_VIDEO_3GP -> R.drawable.ic_file_3gp
            FILE_POSTFIX_VIDEO_RA -> R.drawable.ic_file_ra
            FILE_POSTFIX_VIDEO_RAM -> R.drawable.ic_file_video
            FILE_POSTFIX_VIDEO_MKV -> R.drawable.ic_file_video
            FILE_POSTFIX_VIDEO_FLV -> R.drawable.ic_file_video
            FILE_POSTFIX_VIDEO_F4V -> R.drawable.ic_file_video
            FILE_POSTFIX_VIDEO_RMVB -> R.drawable.ic_file_rmvb
            FILE_POSTFIX_VIDEO_WEBM -> R.drawable.ic_file_webm
            FILE_POSTFIX_VIDEO_BD -> R.drawable.ic_file_video
            FILE_POSTFIX_VIDEO_DVD -> R.drawable.ic_file_video
            FILE_POSTFIX_VIDEO_MP4 -> R.drawable.ic_file_mp4
            // 图像文件
            FILE_POSTFIX_IMAGE_WEBP -> R.drawable.ic_file_webp
            FILE_POSTFIX_IMAGE_BMP -> R.drawable.ic_file_bmp
            FILE_POSTFIX_IMAGE_PCX -> R.drawable.ic_file_image
            FILE_POSTFIX_IMAGE_TIF -> R.drawable.ic_file_image
            FILE_POSTFIX_IMAGE_GIF -> R.drawable.ic_file_gif
            FILE_POSTFIX_IMAGE_JPEG -> R.drawable.ic_file_jpg
            FILE_POSTFIX_IMAGE_JPG -> R.drawable.ic_file_jpg
            FILE_POSTFIX_IMAGE_TGA -> R.drawable.ic_file_image
            FILE_POSTFIX_IMAGE_EXIF -> R.drawable.ic_file_image
            FILE_POSTFIX_IMAGE_FPX -> R.drawable.ic_file_image
            FILE_POSTFIX_IMAGE_SVG -> R.drawable.ic_file_svg
            FILE_POSTFIX_IMAGE_PSD -> R.drawable.ic_file_image
            FILE_POSTFIX_IMAGE_CDR -> R.drawable.ic_file_image
            FILE_POSTFIX_IMAGE_DXF -> R.drawable.ic_file_image
            FILE_POSTFIX_IMAGE_UFO -> R.drawable.ic_file_image
            FILE_POSTFIX_IMAGE_EPS -> R.drawable.ic_file_image
            FILE_POSTFIX_IMAGE_AI -> R.drawable.ic_file_image
            FILE_POSTFIX_IMAGE_PNG -> R.drawable.ic_file_png
            FILE_POSTFIX_IMAGE_HDRI -> R.drawable.ic_file_image
            FILE_POSTFIX_IMAGE_RAW -> R.drawable.ic_file_raw
            FILE_POSTFIX_IMAGE_WMF -> R.drawable.ic_file_image
            FILE_POSTFIX_IMAGE_FLIC -> R.drawable.ic_file_image
            FILE_POSTFIX_IMAGE_EMF -> R.drawable.ic_file_image
            FILE_POSTFIX_IMAGE_ICO -> R.drawable.ic_file_image
            // Android系统相关
            FILE_POSTFIX_ANDROID_APK -> R.drawable.ic_file_apk
            // 其他
            else -> R.drawable.ic_file_file
        }
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
        FILE_POSTFIX_TEXT_C,
        FILE_POSTFIX_TEXT_CPP,
        FILE_POSTFIX_TEXT_H,
        FILE_POSTFIX_TEXT_M,
        FILE_POSTFIX_TEXT_CS,
        FILE_POSTFIX_TEXT_GROOVY,
        FILE_POSTFIX_TEXT_SWIFT,
        FILE_POSTFIX_TEXT_JS,
        FILE_POSTFIX_TEXT_GO,
        FILE_POSTFIX_TEXT_PHP,
        FILE_POSTFIX_TEXT_ERL,
        FILE_POSTFIX_TEXT_HRL,
        FILE_POSTFIX_TEXT_RS,
        FILE_POSTFIX_TEXT_RLIB,
        FILE_POSTFIX_TEXT_PL,
        FILE_POSTFIX_TEXT_PM,
        FILE_POSTFIX_TEXT_T,
        FILE_POSTFIX_TEXT_POD,
        FILE_POSTFIX_TEXT_LUA,
        FILE_POSTFIX_TEXT_DART,
        FILE_POSTFIX_TEXT_TS,
        FILE_POSTFIX_TEXT_S,
        FILE_POSTFIX_TEXT_RB,
        FILE_POSTFIX_TEXT_CSS,
        FILE_POSTFIX_TEXT_HTML,
        FILE_POSTFIX_TEXT_XML,
        FILE_POSTFIX_TEXT_JSON,
        FILE_POSTFIX_TEXT_MD,
        FILE_POSTFIX_TEXT_MARKDOWN,
        FILE_POSTFIX_TEXT_KT,
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
        else -> {
            openFile(context, this, "*/*")
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


