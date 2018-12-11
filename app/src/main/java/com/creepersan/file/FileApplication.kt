package com.creepersan.file

import android.app.Application

class FileApplication : Application(){

    override fun onCreate() {
        super.onCreate()
    }

    fun exit(){
        System.exit(0)
    }

}