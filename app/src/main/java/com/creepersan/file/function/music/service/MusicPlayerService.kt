package com.creepersan.file.function.music.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class MusicPlayerService : Service() {
    private val mBinder by lazy { MusicPlayerServiceBinder() }


    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    class MusicPlayerServiceBinder : Binder()

}