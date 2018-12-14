package com.creepersan.file

import android.app.Application
import com.creepersan.file.utils.ConfigUtil

class FileApplication : Application(){

    companion object {
        private var mInstance : FileApplication? = null
        private var mConfig : ConfigUtil? = null

        fun getInstance():FileApplication{
            return mInstance!!
        }

        fun getConfigInstance():ConfigUtil{
            if (mConfig == null){
                synchronized(this) {
                    if (mConfig == null){
                        mConfig = ConfigUtil(FileApplication.getInstance())
                    }
                }
            }
            return mConfig!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }

    fun exit(){
        System.exit(0)
    }

}