package com.creepersan.file.function.music.service

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.widget.RemoteViews
import com.creepersan.file.FileApplication
import com.creepersan.file.R
import com.creepersan.file.function.ID_MUSIC_NOTIFICATION
import com.creepersan.file.function.ID_MUSIC_NOTIFICATION_CHANNEL
import com.creepersan.file.function.REQUEST_CODE_MUSIC_PENDING_INTENT_PLAY_PAUSE
import com.creepersan.file.function.music.MUSIC_DEFAULT_IMAGE_ID
import com.creepersan.file.function.music.MUSIC_PLAYER_LOOP_NO
import com.creepersan.file.function.music.bean.MusicBean
import com.creepersan.file.service.BaseService
import com.creepersan.file.utils.Logger
import java.io.File
import java.lang.Exception
import java.lang.IllegalStateException

class MusicPlayerService : BaseService() {
    private val mBinder by lazy { MusicPlayerServiceBinder() }
    private val mMediaPlayer by lazy { MediaPlayer() }
    private val mMusicList by lazy { ArrayList<MusicBean>() }
    private val retriever by lazy { MediaMetadataRetriever() }
    private var mState = STATE_NOT_PREPARE
    private var mCurrentMusicBean:MusicBean? = null
    private val mNotificationChannel by lazy { getNotificationChannel() }
    private val mBroadcastReceiver by lazy { MusicPlayerBroadcastReceiver() }
    private val mBroadcastReceiverIntentFiler by lazy { IntentFilter(ACTION) }
    private val mPendingIntentPlayPause by lazy {
        PendingIntent.getBroadcast(
            this.applicationContext,
            REQUEST_CODE_MUSIC_PENDING_INTENT_PLAY_PAUSE,
            Intent(ACTION).apply {
                putExtra(KEY_ACTION, VAL_ACTION_PLAY_PAUSE)
            },
            PendingIntent.FLAG_UPDATE_CURRENT) }
    private val mNotificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private val mNotificationBigView by lazy {
        val view = RemoteViews(packageName, R.layout.notification_music_big)
        view.setOnClickPendingIntent(R.id.notificationMusicBigPlayPause, mPendingIntentPlayPause)
        view
    }
    private val mNotificationView by lazy {
        val view = RemoteViews(packageName, R.layout.notification_music_normal)
        view.setOnClickPendingIntent(R.id.notificationMusicNormalPlayPause, mPendingIntentPlayPause)
        view
    }
    private val mNotification by lazy {
        // 创建通知建造者
        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationCompat.Builder(FileApplication.getInstance(), mNotificationChannel.id)
        }else{
            NotificationCompat.Builder(FileApplication.getInstance())
        }
        notificationBuilder
            .setCustomContentView(mNotificationView)
            .setCustomBigContentView(mNotificationBigView)
            .setSmallIcon(R.drawable.ic_music_icon)
            .build()
    }
    private val mConfigUtil by lazy { FileApplication.getConfigInstance() }
    private var mLoopMode = MUSIC_PLAYER_LOOP_NO

    companion object {
        const val STATE_NOT_PREPARE = 0
        const val STATE_IDLE = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSE = 3
        const val STATE_STOP = 5
        const val STATE_PREPARING = 6
        const val STATE_DESTROY = 7
        const val STATE_ERROR = 8

        const val RESULT_EMPTY = "< Empty >"

        // 下面是广播接收器用的

        const val ACTION = "com.creepersan.file.function.MusicPlayer" // 在Manifest中也有相关定义，如果要修改的话，记得要把哪个也修改了

        const val KEY_ACTION = "action"
        const val VAL_ACTION_NULL = -1
        const val VAL_ACTION_PLAY_PAUSE = 0
        const val VAL_ACTION_NEXT = 1
        const val VAL_ACTION_PREVIOUS = 2
        const val VAL_ACTION_LIKE = 3
        const val VAL_ACTION_LOOP = 4
    }

    /* 生命周期 */
    override fun onCreate() {
        super.onCreate()
        // 注册广播接收器
        registerReceiver(mBroadcastReceiver, mBroadcastReceiverIntentFiler)
        // 初始化参数
        initPlayerParam()
    }
    override fun onDestroy() {
        super.onDestroy()
        // 注销广播接收器
        unregisterReceiver(mBroadcastReceiver)
    }
    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }
    private fun onLoadFinish(file:File){
        mCurrentMusicBean = try {
            MusicBean(retriever, file)
        }catch (e:IllegalStateException){
            e.printStackTrace()
            null
        }
    }
    private fun onPlayMusic(){
        // 更新通知
        val title = getCurrentTitle()
        val artist = getCurrentArtist()
        refreshNotification(getCurrentTitle(), getCurrentArtist())
        refreshNotificationImage(getCurrentMusicImage())
        refreshNotificationControlButtonState(isPlaying(), false)
        postNotificationUpdate()
        // 开启为前台服务
        startForeground(ID_MUSIC_NOTIFICATION, mNotification)
    } // 播放音乐时的后续
    private fun onPauseMusic(){
        // 更新通知
        refreshNotificationControlButtonState(isPlaying(), false)
        postNotificationUpdate()
        // 停止前台服务
        stopForeground(false)
    } // 暂停播放音乐是的后续

    /* 初始化 */
    private fun initPlayerParam(){
        // 初始化循环模式
        mLoopMode = mConfigUtil.getMusicPlayerLoopMode()
    }

    /* 内部处理方法 */
    private fun getMusicBean(filePath:String):MusicBean?{
        return mCurrentMusicBean
    }
    private fun getMusicImage(filePath: String):Bitmap?{
        try {
            retriever.setDataSource(filePath)
        }catch (e:IllegalStateException){
            return null
        }
        val picData = retriever.embeddedPicture
        picData ?: return null
        return BitmapFactory.decodeByteArray(retriever.embeddedPicture, 0, picData.size)
    }
    @TargetApi(Build.VERSION_CODES.O)
    private fun getNotificationChannel():NotificationChannel{
        val notificationChannel = NotificationChannel(ID_MUSIC_NOTIFICATION_CHANNEL, applicationContext.getString(R.string.musicPlayerNotificationChannelName), NotificationManager.IMPORTANCE_NONE)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(notificationChannel)
        return notificationChannel
    }
    private fun refreshNotification(title:String, artist:String){
        mNotificationView.setTextViewText(R.id.notificationMusicNormalTitle, title)
        mNotificationView.setTextViewText(R.id.notificationMusicNormalArtist, artist)

        mNotificationBigView.setTextViewText(R.id.notificationMusicBigTitle, title)
        mNotificationBigView.setTextViewText(R.id.notificationMusicBigArtist, artist)
    }
    private fun refreshNotificationImage(image:Bitmap? = null){
        if (image == null){
            mNotificationView.setImageViewResource(R.id.notificationMusicNormalImage, MUSIC_DEFAULT_IMAGE_ID)
            mNotificationBigView.setImageViewResource(R.id.notificationMusicBigImage, MUSIC_DEFAULT_IMAGE_ID)
        }else{
            mNotificationView.setImageViewBitmap(R.id.notificationMusicNormalImage, image)
            mNotificationBigView.setImageViewBitmap(R.id.notificationMusicBigImage, image)
        }
    }
    private fun refreshNotificationControlButtonState(isPlaying:Boolean=false, isLike:Boolean=false){
        if (isPlaying){
            mNotificationView.setImageViewResource(R.id.notificationMusicNormalPlayPause, R.drawable.ic_music_player_pause)
            mNotificationBigView.setImageViewResource(R.id.notificationMusicBigPlayPause, R.drawable.ic_music_player_pause)
        }else{
            mNotificationView.setImageViewResource(R.id.notificationMusicNormalPlayPause, R.drawable.ic_music_player_play)
            mNotificationBigView.setImageViewResource(R.id.notificationMusicBigPlayPause, R.drawable.ic_music_player_play)
        }
        if (isLike){
            mNotificationBigView.setImageViewResource(R.id.notificationMusicBigLike, R.drawable.ic_music_like)
        }else{
            mNotificationBigView.setImageViewResource(R.id.notificationMusicBigLike, R.drawable.ic_music_unlike)
        }
    }
    private fun postNotificationUpdate(){
        mNotificationManager.notify(ID_MUSIC_NOTIFICATION, mNotification)
    }

    /* 接口处理 */
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
        onLoadFinish(file)
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
        mState = STATE_PREPARING
        mMediaPlayer.setOnPreparedListener {
            mState = STATE_IDLE
            onLoadFinish(file)
            action(mBinder)
            mMediaPlayer.setOnPreparedListener(null)
        }
        mMediaPlayer.prepareAsync()
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
            onPlayMusic()
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
            onPauseMusic()
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
    fun getCurrentMusicInfo():MusicBean?{
        return mCurrentMusicBean
    }
    fun getCurrentTitle():String{
        return getCurrentMusicInfo()?.title ?: RESULT_EMPTY
    }
    fun getCurrentArtist():String{
        return getCurrentMusicInfo()?.author ?: RESULT_EMPTY
    }
    fun getCurrentAlbume():String{
        return getCurrentMusicInfo()?.album ?: RESULT_EMPTY
    }
    fun getCurrentMusicImage():Bitmap?{
        return getMusicImage(mCurrentMusicBean?.path ?: "")
    }
    // TODO : 设置播放模式
    fun setPlayerLoopMode(loopMode: Int){
        // 更改参数
        mLoopMode = loopMode
        // 写入参数
        mConfigUtil.setMusicPlayerLoopMode(mLoopMode)
    }
    fun getPlayerLoopMode():Int{
        return mLoopMode
    }




    /* 内部类 */
    // 广播接收器
    inner class MusicPlayerBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when(intent.getIntExtra(KEY_ACTION, VAL_ACTION_NULL)){
                VAL_ACTION_PLAY_PAUSE -> {
                    playOrPause()
                }
                VAL_ACTION_LIKE -> {

                }
                VAL_ACTION_NEXT -> {

                }
                VAL_ACTION_PREVIOUS -> {

                }
                VAL_ACTION_LOOP -> {

                }
            }
        }
    }
    // 服务操作接口
    inner class MusicPlayerServiceBinder : Binder(){

        fun prepareAsync(path:String, action: (mBinder: MusicPlayerServiceBinder) -> Unit){
            loadMusicAsync(path, action)
        }

        fun prepare(path: String):Boolean{
            return loadMusic(path)
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

        fun getCurrentMusicBean():MusicBean?{
            return this@MusicPlayerService.getCurrentMusicInfo()
        }

        fun getCurrentMusicImage():Bitmap?{
            return this@MusicPlayerService.getCurrentMusicImage()
        }

        fun setPlayerLoopMode(loopMode:Int){
            this@MusicPlayerService.setPlayerLoopMode(loopMode)
        }
        fun getPlayerLoopMode():Int{
            return mLoopMode
        }

    }

}