package com.creepersan.file.utils

import android.util.Log

object Logger{

    private val isEnableLog = true

    fun log(content:String, tag:String = "调试"){
        Log.i(tag, content)
    }

    fun logE(content: String, tag: String = "错误"){
        Log.e(tag, content)
    }

    fun logW(content:String, tag: String = "警告"){
        Log.w(tag, content)
    }

}