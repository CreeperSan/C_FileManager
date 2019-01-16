package com.creepersan.file.utils

import android.content.Context
import android.content.SharedPreferences

/**
 *  不要再初始化这个实例，其实可以在Application中获取
 */
class ConfigUtil(context:Context) {
    private var mFileConfig : SharedPreferences

    companion object {
        private const val NAME_FILE_CONFIG = "file"

        private const val KEY_FILE_IS_FOLDER_FIRST = "is_folder_first"
        private const val DEFAULT_FILE_IS_FOLDER_FIRST = true
        private const val KEY_FILE_IS_SHOW_HIDDEN_FILE = "is_show_hidden_file"
        private const val DEFAULT_FILE_IS_SHOW_HIDDEN_FILE = false
        private const val KEY_FILE_SORT = "sort"
        const val DEFAULT_FILE_SORT_NAME = 0
        const val DEFAULT_FILE_SORT_CREATE_TIME = 1
        const val DEFAULT_FILE_SORT_MODIFY_TIME = 2
        const val DEFAULT_FILE_SORT_SIZE = 3
        const val DEFAULT_FILE_SORT_TYPE = 4
        private const val KEY_FILE_IS_SORT_ORDER_REVERSE = "is_sort_order_reverse"
        private const val DEFAULT_FILE_IS_SORT_ORDER_REVERSE = false
        private const val KEY_FILE_IS_SORT_CASE_SENSITIVE = "is_sort_case_sensitive"
        private const val DEFAULT_FILE_IS_SORT_CASE_SENSITIVE = false
        private const val KEY_FILE_IS_CONFIRM_ON_EXIT = "is_confirm_on_exiot"
        private const val DEFAULT_FILE_IS_CONFIRM_ON_EXIT = true
        private const val KEY_MAIN_CONFIRM_ON_EXIT_DELAY = "confirm_on_exit_delay"
        private const val DEFAULT_MAIN_CONFIRM_ON_EXIT_DELAY = 300L
    }

    init {
        mFileConfig = context.getSharedPreferences(NAME_FILE_CONFIG, Context.MODE_PRIVATE)
    }

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
        return mFileConfig.getInt(KEY_FILE_SORT, DEFAULT_FILE_SORT_NAME)
    }
    fun setFileSortOrderAsName(){
        mFileConfig.edit().putInt(KEY_FILE_SORT, DEFAULT_FILE_SORT_NAME).apply()
    }
    fun setFileSortOrderAsCreateTime(){
        mFileConfig.edit().putInt(KEY_FILE_SORT, DEFAULT_FILE_SORT_CREATE_TIME).apply()
    }
    fun setFileSortOrderAsModifyTime(){
        mFileConfig.edit().putInt(KEY_FILE_SORT, DEFAULT_FILE_SORT_MODIFY_TIME).apply()
    }
    fun setFileSortOrderAsSize(){
        mFileConfig.edit().putInt(KEY_FILE_SORT, DEFAULT_FILE_SORT_SIZE).apply()
    }
    fun setFileSortOrderAsType(){
        mFileConfig.edit().putInt(KEY_FILE_SORT, DEFAULT_FILE_SORT_TYPE).apply()
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
    fun setConfirmOnExit(state:Boolean){
        mFileConfig.edit().putBoolean(KEY_FILE_IS_CONFIRM_ON_EXIT, state).apply()
    }
    fun getConfirmOnExit():Boolean{
        return mFileConfig.getBoolean(KEY_FILE_IS_CONFIRM_ON_EXIT, DEFAULT_FILE_IS_CONFIRM_ON_EXIT)
    }
    fun setMainConfirmOnExitDealy(delay:Long){
        return mFileConfig.edit().putLong(KEY_MAIN_CONFIRM_ON_EXIT_DELAY, delay).apply()
    }
    fun getMainConfirmOnExitDelay():Long{
        return mFileConfig.getLong(KEY_MAIN_CONFIRM_ON_EXIT_DELAY, DEFAULT_MAIN_CONFIRM_ON_EXIT_DELAY)
    }

}