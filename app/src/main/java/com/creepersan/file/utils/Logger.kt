package com.creepersan.file.utils

import android.util.Log

object Logger{

    private var isEnableLog = true

    const val TAG_MUSIC_PLAYER = "Music Player"
    const val TAG_FILE = "File"
    const val TAG_MAIN = "Main"


    fun log(content:String, tag:String = "${System.currentTimeMillis().toString().substring(9)}调试"){
        if (!isEnableLog) return
        Log.i(tag, content)
    }

    fun logE(content: String, tag: String = "错误"){
        if (!isEnableLog) return
        Log.e(tag, content)
    }

    fun logW(content:String, tag: String = "警告"){
        if (!isEnableLog) return
        Log.w(tag, content)
    }

}