package creepersan.com.cfilemanager.util

import android.content.Context
import android.content.SharedPreferences
import creepersan.com.cfilemanager.application.ManageApplication

/** SharePreferenceHelper
 * Created by CreeperSan on 2017/11/11.
 */
object PrefHelper {
    private const val NAME_CONFIG = "ConfigPref.xml"
    private const val KEY_PREV_VERSION_CODE = "PrevVersionCode" //上一个版本的版本代码
    private const val KEY_BOOT_LOGO_TIME = "BootLogoWaitTime"   //展示LOGO的时间

    private const val DEFAULT_BOOT_LOGO_TIME = 0L

    private lateinit var context : ManageApplication
    private lateinit var configPref : SharedPreferences

    fun init(context:ManageApplication){
        this.context = context
        configPref = context.getSharedPreferences(NAME_CONFIG,Context.MODE_PRIVATE)
    }

    /**
     *  版本相关信息
     */
    fun getPrevVersion():Int{
        return configPref.getInt(KEY_PREV_VERSION_CODE,0)
    }
    fun setPrevVersion(){
        configPref.edit().putInt(KEY_PREV_VERSION_CODE, context.packageManager.getPackageInfo(context.packageName,0).versionCode  ).apply()
    }
    fun isLatestVersion():Boolean{
        val currentVersion = context.packageManager.getPackageInfo(context.packageName,0).versionCode
        return currentVersion == getPrevVersion()
    }

    /**
     *  启动界面专属
     */
    fun getBootLogoTime():Long{
        return configPref.getLong(KEY_BOOT_LOGO_TIME,DEFAULT_BOOT_LOGO_TIME)
    }
    fun setBootLogoTime(time:Long){
        configPref.edit().putLong(KEY_BOOT_LOGO_TIME, time).apply()
    }




}