package com.creepersan.file.utils

import android.content.Context
import com.creepersan.file.function.file.FILE_SORT_TYPE
import com.creepersan.file.function.file.FILE_SORT_CREATE_TIME
import com.creepersan.file.function.file.FILE_SORT_MODIFY_TIME
import com.creepersan.file.function.file.FILE_SORT_SIZE
import com.creepersan.file.function.file.FILE_SORT_NAME
import com.creepersan.file.function.music.*

/**
 *  不要再初始化这个实例，其实可以在Application中获取
 */
class ConfigUtil(context:Context) {
    private val mMainConfig by lazy { context.getSharedPreferences(NAME_MAIN_CONFIG, Context.MODE_PRIVATE) }
    private val mFileConfig by lazy { context.getSharedPreferences(NAME_FILE_CONFIG, Context.MODE_PRIVATE) }
    private val mMusicPlayerConfig by lazy { context.getSharedPreferences(NAME_MUSIC_PLAYER_CONFIG, Context.MODE_PRIVATE) }
    private val mVideoPlayerConfig by lazy { context.getSharedPreferences(NAME_VIDEO_PLAYER_CONFIG, Context.MODE_PRIVATE) }

    companion object {
        private const val NAME_FILE_CONFIG = "file"
        private const val NAME_MAIN_CONFIG = "main"
        private const val NAME_MUSIC_PLAYER_CONFIG = "music_player"
        private const val NAME_VIDEO_PLAYER_CONFIG = "music_player"

        // Main
        private const val KEY_MAIN_IS_CONFIRM_ON_EXIT = "is_confirm_on_exit"
        private const val DEFAULT_FILE_IS_CONFIRM_ON_EXIT = true
        private const val KEY_MAIN_CONFIRM_ON_EXIT_DELAY = "confirm_on_exit_delay"
        private const val DEFAULT_MAIN_CONFIRM_ON_EXIT_DELAY = 300

        // File
        private const val KEY_FILE_IS_FOLDER_FIRST = "is_folder_first"
        private const val DEFAULT_FILE_IS_FOLDER_FIRST = true
        private const val KEY_FILE_IS_SHOW_HIDDEN_FILE = "is_show_hidden_file"
        private const val DEFAULT_FILE_IS_SHOW_HIDDEN_FILE = false
        private const val KEY_FILE_SORT = "sort"
        private const val KEY_FILE_IS_SORT_ORDER_REVERSE = "is_sort_order_reverse"
        private const val DEFAULT_FILE_IS_SORT_ORDER_REVERSE = false
        private const val KEY_FILE_IS_SORT_CASE_SENSITIVE = "is_sort_case_sensitive"
        private const val DEFAULT_FILE_IS_SORT_CASE_SENSITIVE = false

        // Music Player
        private const val KEY_MUSIC_PLAYER_LOOP_MODE = "loop_mode"

        // Video Player
        private const val KEY_VIDEO_PLAYER_RIGHT_SLIDE_VOLUME = "right_slide"
        private const val DEFAULT_VIDEO_PLAYER_RIGHT_SLIDE_VOLUME = true
        private const val KEY_VIDEO_PLAYER_LEFT_SLIDE_BRIGHTNESS = "left_slide"
        private const val DEFAULT_VIDEO_PLAYER_LEFT_SLIDE_BRIGHTNESS = true
        private const val KEY_VIDEO_PLAYER_SLIDE_PROGRESS = "slide"
        private const val DEFAULT_VIDEO_PLAYER_SLIDE_PROGRESS = true
        private const val KEY_HORIZONTAL_SLIDE_PROGRESS_UNIT = "horizontal_slide_progress_unit"
        private const val DEFAULT_HORIZONTAL_SLIDE_PROGRESS_UNIT = 1000

    }

    /* File Fragment 相关 */

    fun getFileIsFolderFirst():Boolean{
        return mFileConfig.getBoolean(KEY_FILE_IS_FOLDER_FIRST, DEFAULT_FILE_IS_FOLDER_FIRST)
    }
    fun setFileIsFolderFirst(status:Boolean){
        mFileConfig.edit().putBoolean(KEY_FILE_IS_FOLDER_FIRST, status).apply()
    }
    fun getFileIsShowHiddenFile():Boolean{
        return mFileConfig.getBoolean(KEY_FILE_IS_SHOW_HIDDEN_FILE, DEFAULT_FILE_IS_SHOW_HIDDEN_FILE)
    }
    fun setFileIsShowHiddenFile(status:Boolean){
        mFileConfig.edit().putBoolean(KEY_FILE_IS_SHOW_HIDDEN_FILE, status).apply()
    }
    fun getFileSortOrder():Int{
        return mFileConfig.getInt(KEY_FILE_SORT, FILE_SORT_NAME)
    }
    fun setFileSortOrderAsName(){
        mFileConfig.edit().putInt(KEY_FILE_SORT, FILE_SORT_NAME).apply()
    }
    fun setFileSortOrderAsCreateTime(){
        mFileConfig.edit().putInt(KEY_FILE_SORT, FILE_SORT_CREATE_TIME).apply()
    }
    fun setFileSortOrderAsModifyTime(){
        mFileConfig.edit().putInt(KEY_FILE_SORT, FILE_SORT_MODIFY_TIME).apply()
    }
    fun setFileSortOrderAsSize(){
        mFileConfig.edit().putInt(KEY_FILE_SORT, FILE_SORT_SIZE).apply()
    }
    fun setFileSortOrderAsType(){
        mFileConfig.edit().putInt(KEY_FILE_SORT, FILE_SORT_TYPE).apply()
    }
    fun getFileIsOrderReverse():Boolean{
        return mFileConfig.getBoolean(KEY_FILE_IS_SORT_ORDER_REVERSE, DEFAULT_FILE_IS_SORT_ORDER_REVERSE)
    }
    fun setFileIsOrderReverse(status:Boolean){
        mFileConfig.edit().putBoolean(KEY_FILE_IS_SORT_ORDER_REVERSE, status).apply()
    }
    fun getFileIsSortCaseSensitive():Boolean{
        return mFileConfig.getBoolean(KEY_FILE_IS_SORT_CASE_SENSITIVE, DEFAULT_FILE_IS_SORT_CASE_SENSITIVE)
    }
    fun setFileIsSortCaseSensitive(state:Boolean){
        mFileConfig.edit().putBoolean(KEY_FILE_IS_SORT_CASE_SENSITIVE, state).apply()
    }

    /* 应用程序相关 */

    fun setMainConfirmOnExit(state:Boolean){
        mMainConfig.edit().putBoolean(KEY_MAIN_IS_CONFIRM_ON_EXIT, state).apply()
    }
    fun getMainConfirmOnExit():Boolean{
        return mMainConfig.getBoolean(KEY_MAIN_IS_CONFIRM_ON_EXIT, DEFAULT_FILE_IS_CONFIRM_ON_EXIT)
    }
    fun setMainConfirmOnExitDealy(delay:Int){
        return mMainConfig.edit().putInt(KEY_MAIN_CONFIRM_ON_EXIT_DELAY, delay).apply()
    }
    fun getMainConfirmOnExitDelay():Int{
        return mMainConfig.getInt(KEY_MAIN_CONFIRM_ON_EXIT_DELAY, DEFAULT_MAIN_CONFIRM_ON_EXIT_DELAY)
    }

    /* Music Player 相关 */

    fun getMusicPlayerLoopMode():Int{
        return mMusicPlayerConfig.getInt(KEY_MUSIC_PLAYER_LOOP_MODE, MUSIC_PLAYER_LOOP_NO)
    }
    fun setMusicPlayerLoopMode(loopMode:Int){
        when(loopMode){
            MUSIC_PLAYER_LOOP_NO,
            MUSIC_PLAYER_LOOP_SINGLE_LOOP,
            MUSIC_PLAYER_LOOP_ORDER,
            MUSIC_PLAYER_LOOP_ORDER_LOOP,
            MUSIC_PLAYER_LOOP_RANDOM->{
                mMusicPlayerConfig.edit().putInt(KEY_MUSIC_PLAYER_LOOP_MODE, loopMode).apply()
            }
            else -> {
                Logger.log("写入未知的LoopMode", Logger.TAG_MUSIC_PLAYER)
            }
        }
    }

    /* Video Player 相关 */
    fun videoPlayerIsRightSlideVolume():Boolean{
        return mVideoPlayerConfig.getBoolean(KEY_VIDEO_PLAYER_RIGHT_SLIDE_VOLUME, DEFAULT_VIDEO_PLAYER_RIGHT_SLIDE_VOLUME)
    }
    fun videoPlayerSetRightSlideVolume(state:Boolean){
        mVideoPlayerConfig.edit().putBoolean(KEY_VIDEO_PLAYER_RIGHT_SLIDE_VOLUME, state).apply()
    }
    fun videoPlayerIsLeftSlideBrightness():Boolean{
        return mVideoPlayerConfig.getBoolean(KEY_VIDEO_PLAYER_LEFT_SLIDE_BRIGHTNESS, DEFAULT_VIDEO_PLAYER_LEFT_SLIDE_BRIGHTNESS)
    }
    fun videoPlayerSetLeftSlideBrightness(state:Boolean){
        mVideoPlayerConfig.edit().putBoolean(KEY_VIDEO_PLAYER_LEFT_SLIDE_BRIGHTNESS, state).apply()
    }
    fun videoPlayerIsHorizontalSlideProgress():Boolean{
        return mVideoPlayerConfig.getBoolean(KEY_VIDEO_PLAYER_SLIDE_PROGRESS, DEFAULT_VIDEO_PLAYER_SLIDE_PROGRESS)
    }
    fun videoPlayerSetHorizontalSlideProgress(state:Boolean){
        mVideoPlayerConfig.edit().putBoolean(KEY_VIDEO_PLAYER_SLIDE_PROGRESS, state).apply()
    }
    fun videoPlayerGetHorizontalSlideUnit():Int{
        return mVideoPlayerConfig.getInt(KEY_HORIZONTAL_SLIDE_PROGRESS_UNIT, DEFAULT_HORIZONTAL_SLIDE_PROGRESS_UNIT)
    }
    fun videoPlayerSetHorizontalSlideUnit(unitMillisecond:Int){
        mVideoPlayerConfig.edit().putInt(KEY_HORIZONTAL_SLIDE_PROGRESS_UNIT, unitMillisecond).apply()
    }

}