package com.creepersan.file.function.video.activity

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import android.widget.SeekBar
import android.widget.Toolbar
import com.creepersan.file.FileApplication
import com.creepersan.file.R
import com.creepersan.file.activity.BaseActivity
import com.creepersan.file.utils.*
import kotlinx.android.synthetic.main.activity_video_player.*
import java.io.File
import java.io.FileNotFoundException
import java.lang.Exception
import java.lang.IllegalStateException

class VideoPlayerActivity : BaseActivity(), View.OnTouchListener, SurfaceHolder.Callback,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener,
    MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnVideoSizeChangedListener,
    SeekBar.OnSeekBarChangeListener, Toolbar.OnMenuItemClickListener {

    override val mLayoutID: Int = R.layout.activity_video_player

    companion object {
        private const val UPDATE_TIME_NAP = 300L

        private const val STATE_UNDEFINE = 0
        private const val STATE_SLIDE_LEFT_BRIGHTNESS = 1
        private const val STATE_SLIDE_RIGHT_VOLUME = 2
        private const val STATE_SLIDE_HORIZONTAL = 3

        private const val SCALE_TYPE_FIT = 0        // 保持比例，全部显示
        private const val SCALE_TYPE_ZOOM = 1       // 保持比例，裁剪显示
        private const val SCALE_TYPE_ORIGINAL = 2   // 保持比例，原始大小
        private const val SCALE_TYPE_FILL = 3       // 适应比例，全部显示

        private const val SCALE_HINT_STAY_TIME = 1000L  // 点击缩放尺寸改变后的提示文本停留时间

        private const val POSITION_UNDEFINE = Int.MIN_VALUE
        private const val TAG = "视频播放器"

        private const val ORIENTATION_USER = ActivityInfo.SCREEN_ORIENTATION_FULL_USER
        private const val ORIENTATION_LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
        private const val ORIENTATION_PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT

        private const val SAVE_POSITION = "SavePosition"
        private const val SAVE_IS_FORCE_ORIENTATION = "IsForceOrientation"
        private const val SAVE_SCREEN_ORIENTATION = "CustomOrientation"
    }
    private val MOVE_UNIT_DISTANCE = dp2px(FileApplication.getInstance(), 8f)

    private val mThread by lazy { UpdateThread() }

    private var isShowingControlPannel = false
    private var mVideoPath = ""
    private var mCurrentPosition = 0
    private var mDuration = 0
    private var mTouchStartX = 0f
    private var mTouchStartY = 0f
    private var mTouchPrevX = 0f
    private var mTouchPrevY = 0f
    private var mTouchState = STATE_UNDEFINE
    private var isEnableHorizontalProgressSlide = mConfig.videoPlayerIsHorizontalSlideProgress()
    private var isEnableVerticalLeftBrightnessSlide = mConfig.videoPlayerIsLeftSlideBrightness()
    private var isEnableVerticalRightVolumeSlide = mConfig.videoPlayerIsRightSlideVolume()
    private var mTmpNewPosition = 0
    private var isControlPanelAlreadyShowing = false
    private var isSeeking = false
    private var mMediaPlayerManager : MediaPlayerManager? = null
    private val mVolumeManager by lazy { VolumeManager(this) }
    private var mPreviousVolume = 0
    private var mPreviousBrightness = 0f
    private var isLock = false
    private var mHorizontalScrollUnitTimeMillisecond = mConfig.videoPlayerGetHorizontalSlideUnit()
    private var mHorizontalScrollBasePosition = POSITION_UNDEFINE
    private var mScaleType = SCALE_TYPE_FIT
    private var mForwardMilliseconds = mConfig.videoPlayerGetJumpTimeUnit()
    private var mHintTextShowTimes = 0 // 用于统计按下了多少次更改尺寸，方便在回调中判断是否该让其隐藏显示
    private val mWindowWidth  by lazy { videoPlayerRootLayout.width }
    private val mWindowHeight by lazy { videoPlayerRootLayout.height }
    private var isForceRotation = false
    private var mScreenOrientation = ORIENTATION_USER
    private var mBackgroundColor = mConfig.videoPlayerGetBackgroundColor()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFilePath()
        initToolBar()
        initMediaPlayerManager()
        initSeekBar()
        initUnlockLayout()
        refreshVolumeProgressBar()
        refreshBrightnessProgressBar()
        initTouchZone()
        initSurfaceView()
        initControlButton()
        initBackgroundColor()
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
    private fun initToolBar(){
        videoPlayerToolbar.apply {
            // Navigation Icon
            setNavigationIcon(R.drawable.ic_close_white)
            setNavigationOnClickListener{
                finish()
            }
            // Menu
            inflateMenu(R.menu.video_player)
            menu.findItem(R.id.menuVideoPlayerLock).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            menu.findItem(R.id.menuVideoPlayerRotate).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            menu.findItem(R.id.menuVideoPlayerScale).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            setOnMenuItemClickListener(this@VideoPlayerActivity)
        }
    }
    private fun initMediaPlayerManager(){
        if (mMediaPlayerManager == null){
            mMediaPlayerManager = MediaPlayerManager()
        }
    }
    private fun initSeekBar(){
        videoPlayerSeekBar.setOnSeekBarChangeListener(this)
    }
    private fun initUnlockLayout(){
        videoPlayerUnlock.setOnClickListener {
            isLock = false
            hideUnlockButton()
        }
    }
    private fun refreshVolumeProgressBar(){
        videoPlayerVolume.setMin(mVolumeManager.getMediaMinVolume())
        videoPlayerVolume.setMax(mVolumeManager.getMediaMaxVolume())
        setVolumeProgress(mVolumeManager.getMediaCurrentVolume())
    }
    private fun refreshBrightnessProgressBar(){
        videoPlayerBrightness.setMax(mVolumeManager.getMediaMaxVolume())
        videoPlayerBrightness.setMin(mVolumeManager.getMediaMinVolume())
        setBrightnessProgress(Math.round((getActivityBrightness() / (getActivityMaxBrightness()-getActivityMinBrightness()))*videoPlayerBrightness.getMax()))
    }
    private fun initSurfaceView(){
        videoPlayerSurfaceView.holder.addCallback(this)
    }
    private fun initTouchZone(){
        videoPlayerTouchZone.setOnTouchListener(this)
    }
    private fun initControlButton(){
        videoPlayerPlayerOrPause.setOnClickListener {
            mMediaPlayerManager?.playOrPause()
            refreshPlayButton()
        }
        videoPlayerForward.setOnClickListener {
            mMediaPlayerManager?.apply {
                seek(getPosition() + mForwardMilliseconds)
            }
            refreshCurrentProgress()
        }
        videoPlayerBackward.setOnClickListener {
            mMediaPlayerManager?.apply {
                seek(getPosition() - mForwardMilliseconds)
            }
            refreshCurrentProgress()
        }
        videoPlayerNext.setOnClickListener {

        }
        videoPlayerPrevious.setOnClickListener {

        }
    }
    private fun initBackgroundColor(){
        videoPlayerRootLayout.setBackgroundColor(mBackgroundColor)
    }

    private fun initScale(){
        val videoWidth = mMediaPlayerManager?.getWidth() ?: 0
        val videoHeight = mMediaPlayerManager?.getHeight() ?: 0
        val layoutParam = videoPlayerSurfaceView.layoutParams
        when(mScaleType){
            SCALE_TYPE_FIT -> {
                if (mWindowWidth > mWindowHeight){ // 屏幕为横屏
                    if (videoWidth > videoHeight){ // 视频为横屏
                        layoutParam.width = mWindowWidth
                        layoutParam.height= ((videoHeight.toFloat()/videoWidth.toFloat())*mWindowWidth.toFloat()).toInt()
                        videoPlayerSurfaceView.layoutParams = layoutParam
                        videoPlayerSurfaceView.invalidate()
                    }else{ // 视频为竖屏
                        val scale = mWindowHeight.toFloat() / videoHeight.toFloat()
                        layoutParam.width = (videoWidth.toFloat() * scale).toInt()
                        layoutParam.height= (videoHeight.toFloat() * scale).toInt()
                        videoPlayerSurfaceView.layoutParams = layoutParam
                        videoPlayerSurfaceView.invalidate()
                    }
                }else{ // 屏幕为竖屏
                    if (videoWidth > videoHeight){ // 视频为横屏
                        layoutParam.width = mWindowWidth
                        layoutParam.height= ((videoHeight.toFloat() / videoWidth.toFloat())*mWindowWidth.toFloat()).toInt()
                        videoPlayerSurfaceView.layoutParams = layoutParam
                        videoPlayerSurfaceView.invalidate()
                    }else{ // 视频为竖屏
                        val scale = mWindowHeight.toFloat() / videoHeight.toFloat()
                        layoutParam.width = (videoWidth.toFloat() * scale).toInt()
                        layoutParam.height= (videoHeight.toFloat() * scale).toInt()
                        videoPlayerSurfaceView.layoutParams = layoutParam
                        videoPlayerSurfaceView.invalidate()
                    }
                }
            }
            SCALE_TYPE_ZOOM -> {
                if (mWindowWidth > mWindowHeight){ // 屏幕为横屏
                    if (videoWidth > videoHeight){ // 视频为横屏
                        layoutParam.width = ((videoWidth.toFloat() / videoHeight.toFloat())* mWindowHeight.toFloat()).toInt()
                        layoutParam.height= mWindowHeight
                        videoPlayerSurfaceView.layoutParams = layoutParam
                        videoPlayerSurfaceView.invalidate()
                    }else{ // 视频为竖屏
                        layoutParam.width = mWindowWidth
                        layoutParam.height= ((mWindowWidth.toFloat()/mWindowHeight.toFloat())*mWindowWidth.toFloat()).toInt()
                        videoPlayerSurfaceView.layoutParams = layoutParam
                        videoPlayerSurfaceView.invalidate()
                    }
                }else{ // 屏幕为竖屏
                    if (videoWidth > videoHeight){ // 视频为横屏
                        layoutParam.width = ((videoWidth.toFloat() / videoHeight.toFloat())*mWindowHeight).toInt()
                        layoutParam.height= mWindowHeight
                        videoPlayerSurfaceView.layoutParams = layoutParam
                        videoPlayerSurfaceView.invalidate()
                    }else{ // 视频为竖屏
                        layoutParam.width = mWindowWidth
                        layoutParam.height= ((videoHeight.toFloat()/videoWidth.toFloat())*mWindowHeight.toFloat()).toInt()
                        videoPlayerSurfaceView.layoutParams = layoutParam
                        videoPlayerSurfaceView.invalidate()
                    }
                }
            }
            SCALE_TYPE_FILL -> {
                layoutParam.width = mWindowWidth
                layoutParam.height = mWindowHeight
                videoPlayerSurfaceView.layoutParams = layoutParam
                videoPlayerSurfaceView.invalidate()
            }
            SCALE_TYPE_ORIGINAL -> {
                layoutParam.width = videoWidth
                layoutParam.height = videoHeight
                videoPlayerSurfaceView.layoutParams = layoutParam
                videoPlayerSurfaceView.invalidate()
            }
            else -> {
                mScaleType = SCALE_TYPE_FIT
                initScale()
            }
        }
    }
    private fun initRotation(){
        if (isForceRotation){
            requestedOrientation = mScreenOrientation
        }
    }

    /* 回调 */
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        // 如果已经锁了触摸，则不去处理其他的触摸反馈时间
        if (isLock){
            if (event.action == MotionEvent.ACTION_DOWN){
                if (isUnlockButtonShowing()){
                    hideUnlockButton()
                }else{
                    showUnlockButton()
                }
            }
            return true
        }
        // 没有锁定触摸的时候的处理
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
            }
            MotionEvent.ACTION_MOVE -> { // 移动
                mTouchPrevX = x
                mTouchPrevY = y
                // 计算状态
                if (mTouchState == STATE_UNDEFINE){
                    if (deltaStartX > MOVE_UNIT_DISTANCE && isEnableHorizontalProgressSlide){
                        onSetSlideModeToHorizontalProgress()
                        if (!isShowingControlPannel){
                            showControlBarProgress()
                        }
                        mTouchState = STATE_SLIDE_HORIZONTAL
                    }else if (deltaStartY > MOVE_UNIT_DISTANCE){
                        if (isEnableVerticalLeftBrightnessSlide && isEnableVerticalRightVolumeSlide){
                            if (mTouchStartX < width/2){
                                onSetSlideModeToLeftBrightness()
                                mTouchState = STATE_SLIDE_LEFT_BRIGHTNESS
                            }else{
                                onSetSlideModeToRightVolume()
                                mTouchState = STATE_SLIDE_RIGHT_VOLUME
                            }
                        }else if (isEnableVerticalLeftBrightnessSlide && !isEnableVerticalRightVolumeSlide){
                            onSetSlideModeToLeftBrightness()
                            mTouchState = STATE_SLIDE_LEFT_BRIGHTNESS
                        }else if (!isEnableVerticalLeftBrightnessSlide && isEnableVerticalRightVolumeSlide){
                            onSetSlideModeToRightVolume()
                            mTouchState = STATE_SLIDE_RIGHT_VOLUME
                        }else if(!isEnableVerticalLeftBrightnessSlide && !isEnableVerticalRightVolumeSlide){
                            mTouchState = STATE_UNDEFINE
                        }
                    }
                }
                // 计算偏移
                when(mTouchState){
                    STATE_SLIDE_LEFT_BRIGHTNESS -> {
                        val adjustValue = mPreviousBrightness - ((y - mTouchStartY) / MOVE_UNIT_DISTANCE).toInt()*((getActivityMaxBrightness()-getActivityMinBrightness())/videoPlayerBrightness.getMax())
                        setActivityBrightness(adjustValue){
                            refreshBrightnessProgressBar()
                            val progress = videoPlayerBrightness.getProgress()
                            if (adjustValue <= BRIGHTNESS_DEFAULT){
                                videoPlayerHintLayoutText.text = getString(R.string.videoPlayerDefaultLight)
                            }else{
                                videoPlayerHintLayoutText.text = progress.toString()
                            }
                        }
                    }
                    STATE_SLIDE_RIGHT_VOLUME -> {
                        val adjustValue = mPreviousVolume - ((y - mTouchStartY) / MOVE_UNIT_DISTANCE).toInt()
                        mVolumeManager.setMediaVolume(adjustValue){ newVolume ->
                            setVolumeProgress(newVolume)
                            videoPlayerHintLayoutText.text = newVolume.toString()
                        }
                    }
                    STATE_SLIDE_HORIZONTAL -> {
                        mMediaPlayerManager?.pause()
                        if (mHorizontalScrollBasePosition == POSITION_UNDEFINE){
                            mHorizontalScrollBasePosition = mMediaPlayerManager?.getPosition() ?: 0
                        }
                        isSeeking = true
                        mTmpNewPosition = mHorizontalScrollBasePosition + Math.floor((x - mTouchStartX)/MOVE_UNIT_DISTANCE.toDouble()).toInt() * mHorizontalScrollUnitTimeMillisecond
                        if (mTmpNewPosition < 0){
                            mTmpNewPosition = 0
                        }else if (mTmpNewPosition > mDuration){
                            mTmpNewPosition = mDuration
                        }
                        showHintText(mTmpNewPosition)
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
                        mMediaPlayerManager?.start()
                        if (!isControlPanelAlreadyShowing){
                            hideControlBar()
                        }
                        hideHintText()
                        mHorizontalScrollBasePosition = POSITION_UNDEFINE
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
        refreshPlayButton()
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
        mTmpNewPosition = ((progress.toFloat()/max.toFloat()) * mDuration.toFloat()).toInt()
        val prefixChar = when {
            mCurrentPosition > mTmpNewPosition -> "-"
            mCurrentPosition < mTmpNewPosition -> "+"
            else -> ""
        }
        val deltaTime = Math.abs(mTmpNewPosition - mCurrentPosition)
        showHintText(mTmpNewPosition.toFormattedHourMinuteTime(), prefixChar+deltaTime.toFormattedHourMinuteTime())
    }
    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        isSeeking = true
        mTmpNewPosition = mMediaPlayerManager?.getPosition() ?: 0
        mMediaPlayerManager?.pause()
    }
    override fun onStopTrackingTouch(seekBar: SeekBar) {
        isSeeking = false
        hideHintText()
        mMediaPlayerManager?.start()
        // 改变进度
        val progress = seekBar.progress
        val max = seekBar.max
        mMediaPlayerManager?.seek( (progress.toFloat()/max.toFloat() * mDuration).toInt() )
    }
    override fun onMenuItemClick(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuVideoPlayerLock -> {
                isLock = true
                hideControlBar()
                hideBrightnessProgressBar()
                hideHintText()
                hideHintLayout()
                hideVolumeProgressBar()
            }
            R.id.menuVideoPlayerRotate -> {
                if(isForceRotation){
                    mScreenOrientation = if (mScreenOrientation == ORIENTATION_PORTRAIT){
                        ORIENTATION_LANDSCAPE
                    }else{
                        ORIENTATION_PORTRAIT
                    }
                }else{
                    isForceRotation = true
                    mScreenOrientation = if (isProtrait()){
                        ORIENTATION_LANDSCAPE
                    }else{
                        ORIENTATION_PORTRAIT
                    }
                }
                initRotation()
            }
            R.id.menuVideoPlayerScale -> {
                val hintText:String
                when(mScaleType){
                    SCALE_TYPE_FIT -> {
                        mScaleType = SCALE_TYPE_ZOOM
                        hintText = getString(R.string.videoPlayerScaleTypeZoom)
                    }
                    SCALE_TYPE_ZOOM -> {
                        mScaleType = SCALE_TYPE_ORIGINAL
                        hintText = getString(R.string.videoPlayerScaleTypeOriginal)
                    }
                    SCALE_TYPE_ORIGINAL -> {
                        mScaleType = SCALE_TYPE_FILL
                        hintText = getString(R.string.videoPlayerScaleTypeFill)
                    }
                    SCALE_TYPE_FILL -> {
                        mScaleType = SCALE_TYPE_FIT
                        hintText = getString(R.string.videoPlayerScaleTypeFit)
                    }
                    else -> {
                        mScaleType = SCALE_TYPE_FIT
                        hintText = getString(R.string.videoPlayerScaleTypeFit)
                    }
                }
                mHintTextShowTimes++
                showHintText(hintText)
                mHandler.postDelayed({
                    mHintTextShowTimes--
                    if (mHintTextShowTimes <= 0){
                        try {
                            hideHintText()
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                },SCALE_HINT_STAY_TIME)
                initScale()
            }
        }
        return true
    }
    private fun onSetSlideModeToLeftBrightness(){
        showBrightnessHintLayout()
        showBrightnessProgressBar()
        mPreviousBrightness = getActivityBrightness()
    }
    private fun onSetSlideModeToRightVolume(){
        showVolumeHintLayout()
        showVolumeProgressBar()
        mPreviousVolume = mVolumeManager.getMediaCurrentVolume()
    }
    private fun onSetSlideModeToHorizontalProgress(){

    }
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            putBoolean(SAVE_IS_FORCE_ORIENTATION, isForceRotation)
            putInt(SAVE_POSITION, mMediaPlayerManager?.getPosition() ?: 0)
            putInt(SAVE_SCREEN_ORIENTATION, mScreenOrientation)
        }
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.apply {
            isForceRotation = getBoolean(SAVE_IS_FORCE_ORIENTATION, false)
            mCurrentPosition = getInt(SAVE_POSITION, POSITION_UNDEFINE)
            mScreenOrientation = getInt(SAVE_SCREEN_ORIENTATION, ORIENTATION_USER)
        }
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
    private fun showHintText(hint:String){
        videoPlayerProgressHintText.visibility = View.VISIBLE
        videoPlayerProgressHintText.text = hint
    }
    private fun showHintText(durationText:String, durationChange:String){
        videoPlayerProgressHintText.visibility = View.VISIBLE
        videoPlayerProgressHintText.text = String.format("%s\n%s", durationText, durationChange)
    }
    private fun showHintText(newPosition:Int){
        val prefixChar = when {
            mCurrentPosition > newPosition -> "-"
            mCurrentPosition < newPosition -> "+"
            else -> ""
        }
        val deltaTime = Math.abs(newPosition - mCurrentPosition)
        showHintText(newPosition.toFormattedHourMinuteTime(), prefixChar+deltaTime.toFormattedHourMinuteTime())
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
        videoPlayerToolbar.visibility = View.GONE
        videoPlayerBottomBar.visibility = View.VISIBLE
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
    private fun isUnlockButtonShowing():Boolean{
        return videoPlayerUnlock.visibility != View.GONE
    }
    private fun hideUnlockButton(){
        videoPlayerUnlock.visibility = View.GONE
    }
    private fun refreshCurrentProgress(){
        if (isSeeking){
            mMediaPlayerManager?.seek(mTmpNewPosition)
            if (mHorizontalScrollBasePosition != POSITION_UNDEFINE){ // 在滑动屏幕调节进度
                videoPlayerCurrentTime.text = mTmpNewPosition.toFormattedHourMinuteTime()
                videoPlayerSeekBar.progress = (((videoPlayerSeekBar.max.toFloat()) * ((mTmpNewPosition).toFloat()) / mDuration.toFloat())).toInt()
            }
        }else{
            mMediaPlayerManager?.apply {
                if (mMediaPlayerManager?.isReady() == false){ return@apply }
                mCurrentPosition = getPosition()
                mDuration = getDuration()
            }
            videoPlayerCurrentTime.text = mCurrentPosition.toFormattedHourMinuteTime()
            videoPlayerDuration.text = mDuration.toFormattedHourMinuteTime()
            videoPlayerSeekBar.progress = (((videoPlayerSeekBar.max.toFloat()) * ((mCurrentPosition).toFloat()) / mDuration.toFloat())).toInt()
        }
    }
    private fun refreshPlayButton(){
        if (true == mMediaPlayerManager?.isPlaying()){
            videoPlayerPlayerOrPause.setImageResource(R.drawable.ic_video_player_pause)
        }else{
            videoPlayerPlayerOrPause.setImageResource(R.drawable.ic_video_player_play)
        }
    }
    private fun isProtrait():Boolean{
        return mWindowWidth < mWindowHeight
    }

    /* 内部类 */
    private inner class UpdateThread : Thread(){
        private var isRunning = true

        override fun run() {
            super.run()
            while (isRunning){
                runOnUiThread { if (isRunning){ refreshCurrentProgress() } }
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
                initRotation()
                initScale()
                // 如果之前有保存进度的话，则跳转至旋转屏幕前的进度
                if (mCurrentPosition != POSITION_UNDEFINE){
                    mMediaPlayerManager?.seek(mCurrentPosition)
                    refreshCurrentProgress()
                }
                isReady = true
                action?.invoke()
                mMediaPlayer.setOnPreparedListener(null)
            }
            mMediaPlayer.prepareAsync()
        }

        fun load(path:String){
            try {
                videoPlayerToolbar.title = File(path).name
            }catch (e:FileNotFoundException){
                toast(R.string.videoPlayerFileNotFound)
                finish()
            }
            isReady = false
            mMediaPlayer.setDataSource(path)
            mMediaPlayer.prepare()
            initRotation()
            initScale()
            // 如果之前有保存进度的话，则跳转至旋转屏幕前的进度
            if (mCurrentPosition != POSITION_UNDEFINE){
                mMediaPlayerManager?.seek(mCurrentPosition)
                refreshCurrentProgress()
            }
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

        fun playOrPause(){
            if (isPlaying()){
                mMediaPlayer.pause()
            }else{
                mMediaPlayer.start()
            }
        }

        fun getPosition():Int{
            return try {
                mMediaPlayer.currentPosition
            }catch (e:IllegalStateException){
                Logger.logE("播放器状态错误", TAG)
                0
            }

        }

        fun getDuration():Int{
            return mMediaPlayer.duration
        }

        fun seek(position:Int){
            val duration = mMediaPlayer.duration
            when {
                position > duration -> mMediaPlayer.seekTo(duration)
                position < 0 -> mMediaPlayer.seekTo(0)
                else -> mMediaPlayer.seekTo(position)
            }
        }

        fun isReady():Boolean{
            return isReady
        }

        fun getWidth():Int{
            return try {
                mMediaPlayer.videoWidth
            }catch (e:Exception){
                e.printStackTrace()
                -1
            }
        }

        fun getHeight():Int{
            return try {
                mMediaPlayer.videoHeight
            }catch (e:Exception){
                e.printStackTrace()
                -1
            }
        }

        fun destory(){
            mMediaPlayer.setDisplay(null)
            mMediaPlayer.reset()
            mMediaPlayer.release()
        }

    }

}