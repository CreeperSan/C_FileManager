package com.creepersan.file.function.app_viewer.bean

import android.graphics.drawable.Drawable

class AppViewerBean(
    var name:String,
    var packageName:String,
    var versionName:String,
    var versionCode:Long,
    var icon:Drawable,
    var installTime:Long,
    var updateTime:Long
)