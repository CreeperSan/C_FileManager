package com.creepersan.file.function.video.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.view.MotionEvent
import android.view.View
import com.creepersan.file.R
import com.creepersan.file.activity.BaseActivity
import com.creepersan.file.function.video.service.VideoPlayerService
import com.creepersan.file.utils.Logger
import com.creepersan.file.utils.dp2px
import kotlinx.android.synthetic.main.activity_video_player.*

class VideoPlayerActivity : BaseActivity(), ServiceConnection, View.OnTouchListener {

    override val mLayoutID: Int = R.layout.activity_video_player

    companion object {
        private const val UPDATE_TIME_NAP = 300L

        private const val STATE_UNDEFINE = 0
        private const val STATE_SLIDE_LEFT = 1
        private const val STATE_SLIDE_RIGHT = 2
        private const val STATE_SLIDE_HORIZONTAL = 3
        private const val STATE_SLIDE_TAP = 4
    }
    private val MOVE_MIN_DISTANCE = dp2px(this, 2f)

    private val mMediaPlayer by lazy { MediaPlayer() }
    private val mThread by lazy { UpdateThread() }

    private lateinit var mBinder : VideoPlayerService.VideoPlayerServiceBinder
    private var isBindSuccess = false
    private var isShowingControlPannel = false
    private var mVideoPath = ""
    private var mCurrentPosition = 60000L
    private var mDuration = 1000*60*60L
    private var mTouchStartX = 0f
    private var mTouchStartY = 0f
    private var mTouchPrevX = 0f
    private var mTouchPrevY = 0f
    private var mTouchState = STATE_UNDEFINE
    private var isEnableHorizontalProgressSlide = mConfig.videoPlayerIsSlideProgress()
    private var isEnableVerticalLeftBrightnessSlide = mConfig.videoPlayerIsLeftSlideBrightness()
    private var isEnableVerticalRightVolumeSlide = mConfig.videoPlayerIsRightSlideVolume()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFilePath()
        connectToService()
    }
    private fun onConnected(){
        initSurfaceView()
        mThread.start()
    }
    override fun onDestroy() {
        super.onDestroy()
        // Unbind Service
        if (isBindSuccess){
            unbindService(this)
            mThread.stopThread()
        }
    }

    private fun connectToService(){
        bindService(Intent(this, VideoPlayerService::class.java), this, Context.BIND_AUTO_CREATE)
    }
    private fun initFilePath(){
        mVideoPath = intent.data.path ?: ""
    }
    private fun initSurfaceView(){
        videoPlayerSurfaceView.setOnTouchListener(this)
    }

    /* 回调 */
    override fun onServiceDisconnected(name: ComponentName?) {
        finish()
    }
    override fun onServiceConnected(name: ComponentName?, binder: IBinder) {
        if (binder is VideoPlayerService.VideoPlayerServiceBinder){
            mBinder = binder
            isBindSuccess = true
            onConnected()
        }else{
            finish()
        }
    }
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val width = v.width
        val height = v.height
        val x = event.x
        val y = event.y
        val deltaStartX = x - mTouchStartX
        val deltaStartY = y - mTouchStartY
        val deltaX = x - mTouchPrevX
        val deltaY = y - mTouchPrevY
        when(event.action){
            MotionEvent.ACTION_DOWN -> { // 按下
                mTouchStartX = x
                mTouchStartY = y
                mTouchState = STATE_UNDEFINE
            }
            MotionEvent.ACTION_MOVE -> { // 移动
                mTouchPrevX = x
                mTouchPrevY = y
                if (mTouchState == STATE_UNDEFINE){ // 松开
                    if (deltaX > MOVE_MIN_DISTANCE){

                    }else if (deltaY > MOVE_MIN_DISTANCE){

                    }
                }
            }
            MotionEvent.ACTION_UP -> {

            }
        }
        return true
    }


    /* 内部界面处理 */
    private fun showVolumeHintLayout(){
        videoPlayerHintLayout.visibility = View.VISIBLE
        videoPlayerHintImage.setImageResource(R.drawable.ic_video_player_volumn)
    }
    private fun showBrightnessHintLayout(){
        videoPlayerHintLayout.visibility = View.VISIBLE
        videoPlayerHintImage.setImageResource(R.drawable.ic_video_player_brightness)
    }
    private fun hideHintLayout(){
        videoPlayerHintLayout.visibility = View.GONE
    }
    private fun setHintText(hint:String){
        videoPlayerHintText.text = hint
    }
    private fun showHintText(durationText:String, durationChange:String){
        videoPlayerProgressHintText.visibility = View.VISIBLE
        videoPlayerProgressHintText.text = String.format("%s\n%s", durationText, durationChange)
    }
    private fun hideHintText(){
        videoPlayerProgressHintText.visibility = View.GONE
    }
    private fun showBrightnessProgressBar(){
        videoPlayerBrightness.visibility = View.VISIBLE
    }
    private fun hideBrightnessProgressBar(){
        videoPlayerBrightness.visibility = View.GONE
    }
    private fun setBrightnessProgress(progress:Int){
        videoPlayerBrightness.setProgress(progress)
    }
    private fun showVolumeProgressBar(){
        videoPlayerVolume.visibility = View.VISIBLE
    }
    private fun hideVolumeProgressBar(){
        videoPlayerVolume.visibility = View.GONE
    }
    private fun setVolumeProgress(progress:Int){
        videoPlayerVolume.setProgress(progress)
    }
    private fun showControlBar(){
        videoPlayerBottomBar.visibility = View.VISIBLE
        videoPlayerToolbar.visibility = View.VISIBLE
    }
    private fun hideControlBar(){
        videoPlayerBottomBar.visibility = View.GONE
        videoPlayerToolbar.visibility = View.GONE
    }
    private fun showUnlockButton(){
        videoPlayerUnlock.visibility = View.VISIBLE
    }
    private fun hideUnlockButton(){
        videoPlayerUnlock.visibility = View.GONE
    }

    /* 内部类 */
    private inner class UpdateThread : Thread(){
        private var isRunning = true

        override fun run() {
            super.run()
            while (isRunning){
                Thread.sleep(UPDATE_TIME_NAP)
            }
        }

        fun stopThread(){
            isRunning = false
            try {
                interrupt()
            }catch (e:InterruptedException){
                Logger.log("终止了视频更新线程")
            }
        }
    }

}