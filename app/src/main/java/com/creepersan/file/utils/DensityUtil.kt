package com.creepersan.file.utils

import android.content.Context

fun dp2px(context:Context, dp:Float):Int{
    val scale = context.resources.displayMetrics.density
    return ( dp * scale + 0.5f ).toInt()
}

fun px2dp(context:Context, px:Float):Int{
    val scale = context.resources.displayMetrics.density
    return ( px / scale + 0.5f ).toInt()
}

fun sp2px(context:Context, sp:Float):Int{
    val scale = context.resources.displayMetrics.scaledDensity
    return (sp * scale + 0.5f).toInt()
}

fun px2sp(context: Context, px:Float):Int{
    val scale = context.resources.displayMetrics.scaledDensity
    return (px / scale + 0.5f).toInt()
}