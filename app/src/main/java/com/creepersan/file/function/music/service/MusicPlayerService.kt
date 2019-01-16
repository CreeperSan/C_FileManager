package com.creepersan.file.function.music.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.creepersan.file.function.music.bean.MusicBean
import com.creepersan.file.utils.Logger
import java.io.File
import java.lang.Exception
import java.lang.IllegalStateException

class MusicPlayerService : Service() {
    private val mBinder by lazy { MusicPlayerServiceBinder() }
    private val mMediaPlayer by lazy { MediaPlayer() }
    private val mMusicList by lazy { ArrayList<MusicBean>() }
    private val retriever by lazy { MediaMetadataRetriever() }
    private var mState = STATE_NOT_PREPARE

    companion object {
        const val STATE_NOT_PREPARE = 0
        const val STATE_IDLE = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSE = 3
        const val STATE_STOP = 5
        const val STATE_PREPARING = 6
        const val STATE_DESTROY = 7
        const val STATE_ERROR = 8
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }


    fun isReadyToPlay():Boolean{
        return when(mState){
            STATE_IDLE,
            STATE_PAUSE,
            STATE_PLAYING -> { true }
            else -> { false }
        }
    }

    fun loadMusic(filePath:String):Boolean{
        return loadMusic(File(filePath))
    }

    fun loadMusic(file:File):Boolean{
        if (!file.exists()){
            return false
        }
        mMediaPlayer.setDataSource(file.absolutePath)
        mState = STATE_PREPARING
        mMediaPlayer.prepare()
        mState = STATE_IDLE
        return true
    }

    fun loadMusicAsync(filePath:String, action:(mBinder:MusicPlayerServiceBinder)->Unit):Boolean{
        return loadMusicAsync(File(filePath), action)
    }

    fun loadMusicAsync(file: File, action:(mBinder:MusicPlayerServiceBinder)->Unit):Boolean{
        if (!file.exists()){
            return false
        }
        mMediaPlayer.reset()
        try {
            mMediaPlayer.setDataSource(file.absolutePath)
        }catch(e:IllegalStateException){
            Logger.logE("播放器加载文件${file.absolutePath}失败")
        }
        mMediaPlayer.prepareAsync()
        mState = STATE_PREPARING
        mMediaPlayer.setOnPreparedListener {
            mState = STATE_IDLE
            action(mBinder)
            mMediaPlayer.setOnPreparedListener(null)
        }
        return true
    }

    fun getState():Int{
        return mState
    }

    fun getArrayList():List<MusicBean>{
        return mMusicList
    }

    fun play():Boolean{
        if (!isReadyToPlay()) return false
        return try {
            mState = STATE_PLAYING
            mMediaPlayer.start()
            true
        }catch (e:Exception){
            mState = STATE_ERROR
            false
        }
    }

    fun pause():Boolean{
        if (!isReadyToPlay()) return false
        return try {
            mState = STATE_PLAYING
            mMediaPlayer.pause()
            true
        }catch (e:Exception){
            mState = STATE_ERROR
            false
        }
    }

    fun playOrPause():Boolean{
        if (!isReadyToPlay()) return false
        return if (mMediaPlayer.isPlaying){
            pause()
        }else{
            play()
        }

    }

    fun isPlaying():Boolean{
        if (!isReadyToPlay()) return false
        return mMediaPlayer.isPlaying
    }

    fun getProgress():Int{
        if (!isReadyToPlay()){ return 0 }
        return mMediaPlayer.currentPosition
    }

    fun getDuration():Int{
        if (!isReadyToPlay()){ return 0 }
        return mMediaPlayer.duration
    }

    fun getProgressPercent():Float{
        if (!isReadyToPlay()){ return 0f }
        val currentPos = mMediaPlayer.currentPosition.toFloat()
        val duration = mMediaPlayer.duration.toFloat()
        return currentPos/duration
    }

    fun getMusicFileInfo(filePath:String){
    }

    fun getImage(filePath: String):Bitmap?{
        retriever.setDataSource(filePath)
        val picData = retriever.embeddedPicture
        picData ?: return null
        return BitmapFactory.decodeByteArray(retriever.embeddedPicture, 0, picData.size)
    }











    inner class MusicPlayerServiceBinder : Binder(){

        fun prepareAsync(path:String, action: (mBinder: MusicPlayerServiceBinder) -> Unit){
            loadMusicAsync(path, action)
        }

        fun prepare(path: String):Boolean{
            return loadMusic(path)
        }



        fun getCurrentTitle():String{
            return ""
        }

        fun getCurrentArtist():String{
            return ""
        }

        fun getCurrentAlbume():String{
            return ""
        }

        fun getProgress():Int{
            return this@MusicPlayerService.getProgress()
        }

        fun getDuration():Int{
            return this@MusicPlayerService.getDuration()
        }

        fun getProgressPercent():Float{
            return this@MusicPlayerService.getProgressPercent()
        }

        fun play():Boolean{
            return this@MusicPlayerService.play()
        }

        fun pause():Boolean{
            return this@MusicPlayerService.pause()
        }

        fun playOrPause():Boolean{
            return this@MusicPlayerService.playOrPause()
        }

        fun isPlaying():Boolean{
            return this@MusicPlayerService.isPlaying()
        }

        fun seekProgress(progress:Float):Boolean{
            if (!isReadyToPlay()){ return false }
            mMediaPlayer.seekTo((mMediaPlayer.duration.toFloat()*progress).toInt())
            return true
        }




    }

}