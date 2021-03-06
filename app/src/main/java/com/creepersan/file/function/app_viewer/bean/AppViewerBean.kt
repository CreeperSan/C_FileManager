package com.creepersan.file.function.app_viewer.bean

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

class AppViewerBean(
    var name:String,
    var packageName:String,
    var versionName:String,
    var versionCode:Long,
    var icon:Drawable,
    var installTime:Long,
    var updateTime:Long
){

    companion object {
        fun AppViewerBean.getApplicationInfo(packageManager: PackageManager):ApplicationInfo{
            return packageManager.getApplicationInfo(this.packageName, 0)
        }
    }

}