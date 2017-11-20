package creepersan.com.cfilemanager.util

import java.text.SimpleDateFormat
import java.util.*

/** 格式化工具
 * Created by CreeperSan on 2017/11/19.
 */
object FormatHelper {
    /**
     *  时间格式化
     */
    private val timerFormatter1 : SimpleDateFormat by lazy {  SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }

    fun formatTime(timestamp:Long):String{
        return timerFormatter1.format(Date(timestamp))
    }
}