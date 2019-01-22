package com.creepersan.file.function.video.service

import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.creepersan.file.service.BaseService

class VideoPlayerService : BaseService(){
    private val mBinder by lazy { VideoPlayerServiceBinder() }
    private val mMediaPlayer by lazy { MediaPlayer() }

    override fun onCreate() {
        super.onCreate()
    }


    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    inner class VideoPlayerServiceBinder : Binder() {

        fun loadVideo(path:String, action:(()->Unit)?){

        }

        fun pause(){

        }

        fun play(){

        }

        fun isPlaying():Boolean{
            return true
        }

        fun getCurrentPostion():Long{
            return 0
        }

        fun getDuration():Long{
            return 0
        }

        fun goToPosition(position:Long){

        }


    }
}