package com.creepersan.file.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadImageGlide(resID:Int){
    Glide.with(this).load(resID).into(this)
}

fun ImageView.loadImageGlide(drawable: Drawable){
    Glide.with(this).load(drawable).into(this)
}
