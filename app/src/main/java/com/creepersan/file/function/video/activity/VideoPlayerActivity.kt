package com.creepersan.file.function.video.activity

import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import android.widget.SeekBar
import com.creepersan.file.FileApplication
import com.creepersan.file.R
import com.creepersan.file.activity.BaseActivity
import com.creepersan.file.utils.Logger
import com.creepersan.file.utils.VolumeManager
import com.creepersan.file.utils.dp2px
import com.creepersan.file.utils.toFormattedHourMinuteTime
import kotlinx.android.synthetic.main.activity_video_player.*

class VideoPlayerActivity : BaseActivity(), View.OnTouchListener, SurfaceHolder.Callback,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener,
    MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnVideoSizeChangedListener,
    SeekBar.OnSeekBarChangeListener {

    override val mLayoutID: Int = R.layout.activity_video_player

    companion object {
        private const val UPDATE_TIME_NAP = 300L

        private const val STATE_UNDEFINE = 0
        private const val STATE_SLIDE_LEFT_BRIGHTNESS = 1
        private const val STATE_SLIDE_RIGHT_VOLUME = 2
        private const val STATE_SLIDE_HORIZONTAL = 3
    }
    private val MOVE_UNIT_DISTANCE = dp2px(FileApplication.getInstance(), 8f)

    private val mMediaPlayer by lazy { MediaPlayer() }
    private val mThread by lazy { UpdateThread() }

    private var isShowingControlPannel = false
    private var mVideoPath = ""
    private var mCurrentPosition = 20*60*1000
    private var mDuration = 1000*60*60
    private var mTouchStartX = 0f
    private var mTouchStartY = 0f
    private var mTouchPrevX = 0f
    private var mTouchPrevY = 0f
    private var mTouchState = STATE_UNDEFINE
    private var isEnableHorizontalProgressSlide = mConfig.videoPlayerIsSlideProgress()
    private var isEnableVerticalLeftBrightnessSlide = mConfig.videoPlayerIsLeftSlideBrightness()
    private var isEnableVerticalRightVolumeSlide = mConfig.videoPlayerIsRightSlideVolume()
    private var mTmpNewDirection = 300L
    private var isControlPanelAlreadyShowing = false
    private var isSeeking = false
    private var mMediaPlayerManager : MediaPlayerManager? = null
    private val mVolumeManager by lazy { VolumeManager(this) }
    private var mPreviousVolume = 0
    private var mPreviousBrightness = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFilePath()
        initMediaPlayerManager()
        initSeekBar()
        refreshVolumeProgressBar()
        initBrightnessProgressBar()
        initSurfaceView()
        mThread.start()
    }
    override fun onDestroy() {
        super.onDestroy()
        mThread.stopThread()
        mMediaPlayerManager?.destory()
    }

    private fun initFilePath(){
        mVideoPath = intent.data.path ?: ""
    }
    private fun initMediaPlayerManager(){
        if (mMediaPlayerManager == null){
            mMediaPlayerManager = MediaPlayerManager()
        }
    }
    private fun initSeekBar(){
        videoPlayerSeekBar.setOnSeekBarChangeListener(this)
    }
    private fun refreshVolumeProgressBar(){
        videoPlayerVolume.setMin(mVolumeManager.getMediaMinVolume())
        videoPlayerVolume.setMax(mVolumeManager.getMediaMaxVolume())
        videoPlayerVolume.setProgress(mVolumeManager.getMediaCurrentVolume())
    }
    private fun initBrightnessProgressBar(){

    }
    private fun initSurfaceView(){
        videoPlayerSurfaceView.setOnTouchListener(this)
        videoPlayerSurfaceView.holder.addCallback(this)
    }

    /* 回调 */
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val width = v.width
        val height = v.height
        val x = event.x
        val y = event.y
        val deltaStartX = Math.abs(x - mTouchStartX)
        val deltaStartY = Math.abs(y - mTouchStartY)
        val deltaX = Math.abs(x - mTouchPrevX)
        val deltaY = Math.abs(y - mTouchPrevY)
        when(event.action){
            MotionEvent.ACTION_DOWN -> { // 按下
                mTouchStartX = x
                mTouchStartY = y
                mTouchState = STATE_UNDEFINE
                isControlPanelAlreadyShowing = isShowingControlPannel
                isSeeking = true
            }
            MotionEvent.ACTION_MOVE -> { // 移动
                mTouchPrevX = x
                mTouchPrevY = y
                // 计算状态
                if (mTouchState == STATE_UNDEFINE){
                    if (deltaStartX > MOVE_UNIT_DISTANCE && isEnableHorizontalProgressSlide){
                        mTouchState = STATE_SLIDE_HORIZONTAL
                        if (!isShowingControlPannel){
                            showControlBarProgress()
                        }
                    }else if (deltaStartY > MOVE_UNIT_DISTANCE){
                        if (isEnableVerticalLeftBrightnessSlide && isEnableVerticalRightVolumeSlide){
                            mTouchState = if (mTouchStartX < width/2){
                                showBrightnessHintLayout()
                                showBrightnessProgressBar()
                                STATE_SLIDE_LEFT_BRIGHTNESS
                            }else{
                                showVolumeHintLayout()
                                showVolumeProgressBar()
                                mPreviousVolume = mVolumeManager.getMediaCurrentVolume()
                                STATE_SLIDE_RIGHT_VOLUME
                            }
                        }else if (isEnableVerticalLeftBrightnessSlide && !isEnableVerticalRightVolumeSlide){
                            showBrightnessHintLayout()
                            showBrightnessProgressBar()
                            mTouchState = STATE_SLIDE_LEFT_BRIGHTNESS
                        }else if (!isEnableVerticalLeftBrightnessSlide && isEnableVerticalRightVolumeSlide){
                            showVolumeHintLayout()
                            showVolumeProgressBar()
                            mPreviousVolume = mVolumeManager.getMediaCurrentVolume()
                            mTouchState = STATE_SLIDE_RIGHT_VOLUME
                        }else if(!isEnableVerticalLeftBrightnessSlide && !isEnableVerticalRightVolumeSlide){
                            mTouchState = STATE_UNDEFINE
                        }
                    }
                }
                // 计算偏移
                when(mTouchState){
                    STATE_SLIDE_LEFT_BRIGHTNESS -> {

                    }
                    STATE_SLIDE_RIGHT_VOLUME -> {
                        val adjustValue = mPreviousVolume - ((y - mTouchStartY) / MOVE_UNIT_DISTANCE).toInt()
                        mVolumeManager.setMediaVolume(adjustValue){ newVolume ->
                            videoPlayerVolume.setProgress(newVolume)
                            videoPlayerHintLayoutText.text = newVolume.toString()
                        }
                    }
                    STATE_SLIDE_HORIZONTAL -> {

                    }
                }
            }
            MotionEvent.ACTION_UP -> {// 松开
                when(mTouchState){
                    STATE_UNDEFINE -> {
                        if(isShowingControlPannel){
                            hideControlBar()
                        }else{
                            showControlBarAll()
                        }
                    }
                    STATE_SLIDE_HORIZONTAL -> {
                        if (!isControlPanelAlreadyShowing){
                            hideControlBar()
                        }
                        hideHintLayout()
                    }
                    STATE_SLIDE_RIGHT_VOLUME -> {
                        hideVolumeProgressBar()
                        hideHintLayout()
                    }
                    STATE_SLIDE_LEFT_BRIGHTNESS -> {
                        hideBrightnessProgressBar()
                        hideHintLayout()
                    }
                }
                isSeeking = false
            }
        }
        return true
    }
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }
    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }
    override fun surfaceCreated(holder: SurfaceHolder) {
        mMediaPlayerManager?.setDisplay(holder)
        mMediaPlayerManager?.load(mVideoPath)
        mMediaPlayerManager?.start()
    }
    override fun onCompletion(mp: MediaPlayer) {

    }
    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        when(what){
            MediaPlayer.MEDIA_ERROR_IO -> {

            }
            MediaPlayer.MEDIA_ERROR_MALFORMED -> {

            }
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> {

            }
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> {

            }
            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> {

            }
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> {

            }
            MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> {

            }
        }
        return true
    }
    override fun onInfo(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        when(what){
            MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING -> {

            }
            MediaPlayer.MEDIA_INFO_METADATA_UPDATE -> {

            }
            MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING -> {

            }
            MediaPlayer.MEDIA_INFO_NOT_SEEKABLE -> {

            }
        }
        return true
    }
    override fun onPrepared(mp: MediaPlayer) {

    }
    override fun onSeekComplete(mp: MediaPlayer) {

    }
    override fun onVideoSizeChanged(mp: MediaPlayer, width: Int, height: Int) {

    }
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (!fromUser) return // 如果不是使用者拖动的，那么就直接不处理返回
        val max = seekBar.max
        val newPosition = ((progress.toFloat()/max.toFloat()) * mDuration.toFloat()).toInt()
        val prefixChar = when {
            mCurrentPosition > newPosition -> "-"
            mCurrentPosition < newPosition -> "+"
            else -> ""
        }
        val deltaTime = Math.abs(newPosition - mCurrentPosition)
        showHintText(newPosition.toFormattedHourMinuteTime(), prefixChar+deltaTime.toFormattedHourMinuteTime())
    }
    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        isSeeking = true
    }
    override fun onStopTrackingTouch(seekBar: SeekBar) {
        isSeeking = false
        hideHintText()
        // 改变进度
        val progress = seekBar.progress
        val max = seekBar.max
        mMediaPlayerManager?.seek( (progress.toFloat()/max.toFloat() * mDuration).toInt() )
    }



    /* 内部界面处理 */
    private fun showVolumeHintLayout(){
        videoPlayerHintLayout.visibility = View.VISIBLE
        videoPlayerHintLayoutImage.setImageResource(R.drawable.ic_video_player_volume)
    }
    private fun showBrightnessHintLayout(){
        videoPlayerHintLayout.visibility = View.VISIBLE
        videoPlayerHintLayoutImage.setImageResource(R.drawable.ic_video_player_brightness)
    }
    private fun hideHintLayout(){
        videoPlayerHintLayout.visibility = View.GONE
    }
    private fun setHintText(hint:String){
        videoPlayerHintLayoutText.text = hint
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
    private fun showControlBarAll(){
        isShowingControlPannel = true
        videoPlayerBottomBar.visibility = View.VISIBLE
        videoPlayerToolbar.visibility = View.VISIBLE
        videoPlayerBottomBarButtonLayout.visibility = View.VISIBLE
        videoPlayerBottomBarProgressLayout.visibility = View.VISIBLE
    }
    private fun showControlBarProgress(){
        isShowingControlPannel = true
        videoPlayerBottomBar.visibility = View.VISIBLE
        videoPlayerToolbar.visibility = View.VISIBLE
        videoPlayerBottomBarButtonLayout.visibility = View.GONE
        videoPlayerBottomBarProgressLayout.visibility = View.VISIBLE
    }
    private fun hideControlBar(){
        isShowingControlPannel = false
        videoPlayerBottomBar.visibility = View.GONE
        videoPlayerToolbar.visibility = View.GONE
        videoPlayerBottomBarButtonLayout.visibility = View.GONE
        videoPlayerBottomBarProgressLayout.visibility = View.GONE
    }
    private fun showUnlockButton(){
        videoPlayerUnlock.visibility = View.VISIBLE
    }
    private fun hideUnlockButton(){
        videoPlayerUnlock.visibility = View.GONE
    }
    private fun refreshCurrentProgress(){
        if (!isSeeking){
            mMediaPlayerManager?.apply {
                if (mMediaPlayerManager?.isReady() == false){ return@apply }
                mCurrentPosition = getProgress()
                mDuration = getDuration()
            }
            videoPlayerCurrentTime.text = mCurrentPosition.toFormattedHourMinuteTime()
            videoPlayerDuration.text = mDuration.toFormattedHourMinuteTime()
            videoPlayerSeekBar.progress = (((videoPlayerSeekBar.max.toFloat()) * ((mCurrentPosition).toFloat()) / mDuration.toFloat())).toInt()
        }
    }

    /* 内部类 */
    private inner class UpdateThread : Thread(){
        private var isRunning = true

        override fun run() {
            super.run()
            while (isRunning){
                runOnUiThread { refreshCurrentProgress() }
                try {
                    Thread.sleep(UPDATE_TIME_NAP)
                }catch (e:InterruptedException){
                    Logger.log("中断视频进度更新线程")
                    isRunning = false
                    break
                }
            }
        }

        fun stopThread(){
            isRunning = false
            interrupt()
        }
    }
    private inner class MediaPlayerManager{
        private val mMediaPlayer by lazy { MediaPlayer() }
        private var isReady = false

        init {
            mMediaPlayer.reset()
        }

        fun setDisplay(holder: SurfaceHolder){
            mMediaPlayer.setDisplay(holder)
        }

        fun loadAsync(path:String, action:(()->Unit)?){
            isReady = false
            mMediaPlayer.setDataSource(path)
            mMediaPlayer.setOnPreparedListener {
                isReady = true
                action?.invoke()
                mMediaPlayer.setOnPreparedListener(null)
            }
            mMediaPlayer.prepareAsync()
        }

        fun load(path:String){
            isReady = false
            mMediaPlayer.setDataSource(path)
            mMediaPlayer.prepare()
            isReady = true
        }

        fun start(){
            mMediaPlayer.start()
        }

        fun pause(){
            mMediaPlayer.pause()
        }

        fun isPlaying():Boolean{
            return mMediaPlayer.isPlaying
        }

        fun getProgress():Int{
            return mMediaPlayer.currentPosition
        }

        fun getDuration():Int{
            return mMediaPlayer.duration
        }

        fun seek(position:Int){
            mMediaPlayer.seekTo(position)
            if (!mMediaPlayer.isPlaying){
                mMediaPlayer.start()
            }
        }

        fun isReady():Boolean{
            return isReady
        }

        fun destory(){
            mMediaPlayer.setDisplay(null)
            mMediaPlayer.reset()
            mMediaPlayer.release()
        }
    }

}