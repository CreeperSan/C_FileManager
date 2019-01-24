package com.creepersan.file.utils

import android.app.Activity
import android.view.WindowManager

private var BRIGHTNESS_UNDEFINE = Float.MIN_VALUE
private var brightnessMax = BRIGHTNESS_UNDEFINE
private var brightnessMin = BRIGHTNESS_UNDEFINE

fun Activity.getActivityBrightness():Float{
    return window.attributes.screenBrightness
}

fun Activity.getActivityMaxBrightness():Float{
    if (brightnessMax == BRIGHTNESS_UNDEFINE){
        brightnessMax = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
    }
    return brightnessMax
}

fun Activity.getActivityMinBrightness():Float{
    if (brightnessMin == BRIGHTNESS_UNDEFINE){
        brightnessMin = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF
    }
    return brightnessMin
}

fun Activity.setActivityBrightness(brightness:Float, action:((newBrightness:Float)->Unit)?=null){
    var realBrightness = brightness
    if (realBrightness > brightnessMax){
        realBrightness = brightnessMax
    }
    if (realBrightness < brightnessMin){
        realBrightness = brightnessMin
    }
    val layoutParams = window.attributes
    layoutParams.screenBrightness = realBrightness
    window.attributes = layoutParams
    action?.invoke(realBrightness)
}

const val BRIGHTNESS_DEFAULT = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF
