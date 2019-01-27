package com.creepersan.file.utils

import android.view.MotionEvent

fun MotionEvent.isIDExist(id:Int):Boolean{
    return this.findPointerIndex(id) < 0
}