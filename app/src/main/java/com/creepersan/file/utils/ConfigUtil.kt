package com.creepersan.file.utils

import android.content.Context
import android.content.SharedPreferences

class ConfigUtil(context:Context) {
    private var mFileConfig : SharedPreferences? = null

    companion object {
        private const val NAME_FILE_CONFIG = "file"

        private const val KEY_FILE_IS_FOLDER_FIRST = "is_folder_first"
    }

    init {
        mFileConfig = context.getSharedPreferences(NAME_FILE_CONFIG, Context.MODE_PRIVATE)
    }

    private fun getFileIsFolderFirst():Boolean{

    }
    private fun setFileIsFolderFirst(isStatus:Boolean){

    }


}