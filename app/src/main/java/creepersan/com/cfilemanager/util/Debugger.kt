package creepersan.com.cfilemanager.util

import android.util.Log

/** 调试工具包
 * Created by CreeperSan on 2017/11/8.
 */
object Debugger {
    private const final val IS_DEBUG = true     //是否是调试模式\

    const val LEVEL_VERBOSE = 0
    const val LEVEL_DEBUG = 1
    const val LEVEL_INFO = 2
    const val LEVEL_WARMING = 3
    const val LEVEL_ERROR = 4
    var TAG = "LogCat"
    private val TAG_ERROR = "LogcatError"


    fun log(tag:String = TAG,content:String,level:Int = LEVEL_INFO){
        when(level){
            LEVEL_VERBOSE -> {
                Log.v(tag,content)
            }
            LEVEL_DEBUG -> {
                Log.e(tag,content)
            }
            LEVEL_INFO -> {
                Log.i(tag,content)
            }
            LEVEL_WARMING -> {
                Log.w(tag,content)
            }
            LEVEL_ERROR -> {
                Log.e(tag,content)
            }
            else -> {
                Log.i(TAG_ERROR,"未知的等级 : ${content}")
            }
        }
    }

}
