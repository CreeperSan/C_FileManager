package com.creepersan.file.utils

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.creepersan.file.FileApplication
import com.creepersan.file.function.app_viewer.bean.AppViewerBean

private val getVersionCodeLambda = if (Build.VERSION.SDK_INT >= 28){
    { packageInfo:PackageInfo ->
        packageInfo.longVersionCode
    }
}else{
    { packageInfo:PackageInfo ->
        packageInfo.versionCode.toLong()
    }
}

fun PackageInfo.generateAppViewerBean(packageManager: PackageManager):AppViewerBean{
    val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
    return AppViewerBean(
        packageManager.getApplicationLabel(applicationInfo).toString(),
        this.packageName,
        this.versionName,
        getVersionCodeLambda.invoke(this),
        applicationInfo.loadIcon(packageManager),
        this.firstInstallTime,
        this.lastUpdateTime
    )
}
