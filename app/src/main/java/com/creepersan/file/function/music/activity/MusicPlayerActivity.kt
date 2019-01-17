package com.creepersan.file.function.music.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import com.creepersan.file.R
import com.creepersan.file.activity.BaseActivity
import com.creepersan.file.function.music.MUSIC_DEFAULT_IMAGE_ID
import com.creepersan.file.function.music.MUSIC_DEFAULT_INFO_STR
import com.creepersan.file.function.music.service.MusicPlayerService
import com.creepersan.file.utils.toFormattedHourMinuteTime
import kotlinx.android.synthetic.main.activity_music_player.*

class MusicPlayerActivity : BaseActivity(), ServiceConnection {

    override val mLayoutID: Int = R.layout.activity_music_player

    private lateinit var mMusicServiceBinder: MusicPlayerService.MusicPlayerServiceBinder
    private val mUpdateProgressThread by lazy { UpdateProgressThread() }
    private var mTmpBitmap:Bitmap? = null
    private var mIntentPath:String = ""
    private var isInit = false
    private var isSeeking = false
    private var mDuration = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 检查是否为播放音乐请求带过来的
        if (intent.data != null){
            mIntentPath = intent.data.path ?: ""
        }

        initPlayButton()
        initSeekBar()

        //
        connectToService()

    }

    /* 初始化方法 */
    private fun initPlayButton(){
        musicPlayerControlPlay.setOnClickListener {
            // 是否准备好
            if (!isInit){
                toastNotInit()
                return@setOnClickListener
            }
            // 播放或者暂停
            if (mMusicServiceBinder.playOrPause()){
                musicPlayerControlPlay.setImageResource(if (mMusicServiceBinder.isPlaying()){
                    R.drawable.ic_music_player_pause
                }else{
                    R.drawable.ic_music_player_play
                })
            }else{
                toastNotSuccess()
                return@setOnClickListener
            }

        }
        musicPlayerControlPrevious.setOnClickListener {

        }
        musicPlayerControlNext.setOnClickListener {

        }
    }
    private fun initSeekBar(){
        musicPlayerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (!isInit || !isSeeking) return
                refreshMusicProgress(((progress.toFloat()/seekBar.max.toFloat())*mDuration.toFloat()).toInt(), mDuration, false)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                isSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                isSeeking = false
                if (!isInit){ toastNotInit();return; }
                mMusicServiceBinder.seekProgress(seekBar.progress.toFloat() / seekBar.max.toFloat())
            }

        })
    }

    /* 生命周期 */
    private fun onInit(){
        // 播放传送进来的音乐文件
        if (mIntentPath != ""){
            // 让后台服务准备音乐文件
            mMusicServiceBinder.prepareAsync(mIntentPath) { // 准备完成
                // 播放音乐
                mMusicServiceBinder.play()
                // 获取音乐文件信息以及设置图片
                val tmpBean = mMusicServiceBinder.getCurrentMusicBean()
                mTmpBitmap = mMusicServiceBinder.getCurrentMusicImage()
                if (tmpBean == null){
                    initMusicInfo()
                }else{
                    initMusicInfo(tmpBean.name, tmpBean.author, tmpBean.album, mTmpBitmap)
                }
            }
        }
        // 获取进度线程
        mUpdateProgressThread.start()
    }
    override fun onDestroy() {
        super.onDestroy()
        // 结束刷新进度线程
        if (isInit){
            mUpdateProgressThread.finish()
        }
        // 断开连接
        disconnectToService()
        // 回收图片主页图片Bitmap资源
        if (mTmpBitmap?.isRecycled == true){
            mTmpBitmap?.recycle()
        }
    }
    override fun onPause() {
        super.onPause()
        mUpdateProgressThread.pause()
    }
    override fun onResume() {
        super.onResume()
        mUpdateProgressThread.goOn()
    }



    /* 一些操作的封装 */
    private fun toastNotInit(){
        toast("尚未准备好")
    }
    private fun toastNotSuccess(){
        toast("操作失败")
    }
    private fun connectToService(){
        val intent = Intent(this, MusicPlayerService::class.java)
        startService(intent)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
    }
    private fun disconnectToService(){
        isInit = false
        unbindService(this)
    }
    private fun refreshMusicProgress(progress:Int, duration:Int, isNeedOperateSeekBar:Boolean = true){
        musicPlayerPositionTimeText.text = progress.toFormattedHourMinuteTime()
        musicPlayerDurationTimeText.text = duration.toFormattedHourMinuteTime()
        if (!isNeedOperateSeekBar){ return }
        if (duration == 0){
            musicPlayerSeekBar.progress = 0
        }else{
            musicPlayerSeekBar.progress = (musicPlayerSeekBar.max.toFloat() * (progress.toFloat() / duration.toFloat())).toInt()
        }
    }
    private fun initMusicInfo(name:String=MUSIC_DEFAULT_INFO_STR, author:String=MUSIC_DEFAULT_INFO_STR, album:String=MUSIC_DEFAULT_INFO_STR, image:Bitmap?=null){
        musicPlayerTitle.text = name
        musicPlayerArtist.text = author
        musicPlayerAlbum.text = album
        if (image == null){
            musicPlayerImage.setImageResource(MUSIC_DEFAULT_IMAGE_ID)
        }else{
            musicPlayerImage.setImageBitmap(image)
        }
    }


    /* 一些事件回调方法 */
    override fun onServiceDisconnected(name: ComponentName?) {}
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        service?:return
        mMusicServiceBinder = service as MusicPlayerService.MusicPlayerServiceBinder
        isInit = true
        onInit()
    }

    /* 一些内部类 */
    private inner class UpdateProgressThread : Thread(){
        var isRunning = false
        var isPause = false
        val TIME_WAIT = 300L

        override fun start() {
            isRunning = true
            isPause = false
            super.start()
        }

        override fun run() {
            super.run()
            while (isRunning){
                if (!isPause && !isSeeking){
                    val duration = mMusicServiceBinder.getDuration()
                    val position = mMusicServiceBinder.getProgress()
                    runOnUiThread {
                        mDuration = duration
                        refreshMusicProgress(position, duration)
                    }
                }
                try {
                    sleep(TIME_WAIT)
                }catch (e:InterruptedException){
                    log("刷新音乐进度线程在等待下一次刷新的时候发生了中断线程动作")
                }
            }
        }


        fun finish(){
            interrupt()
            isRunning = false
            isPause = true

        }

        fun pause(){
            isPause = true
        }

        fun goOn(){
            isPause = false
        }

    }
}