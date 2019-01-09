package com.creepersan.file.function.music.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MusicPlayerBroadcastReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION = "com.creepersan.c_filemanager.musicplayer" // 在Manifest中也有相关定义，如果要修改的话，记得要把哪个也修改了
    }

    override fun onReceive(context: Context?, intent: Intent?) {

    }
}