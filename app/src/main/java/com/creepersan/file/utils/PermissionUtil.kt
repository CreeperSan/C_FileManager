package com.creepersan.file.utils

import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import com.creepersan.file.activity.BaseActivity
import java.util.HashMap

object PermissionUtil {
    private const val PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val mSuccessLambdaMap = HashMap<Int, (activity:BaseActivity)->Unit>()
    private val mFailLambdaMap = HashMap<Int, (activity:BaseActivity)->Unit>()

    fun hasStoragePermission(activity:BaseActivity):Boolean{
        return activity.checkSelfPermission(PERMISSION_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    fun requireStoragePermision(activity: BaseActivity, onSuccess:((activity:BaseActivity)->Unit)? = null, onFail:((activity:BaseActivity)->Unit)? = null){
        ActivityCompat.requestPermissions(activity, arrayOf(PERMISSION_STORAGE), RequestCode.PERMISSION_EXTERNAL_STORAGE)
        if (onSuccess!=null){
            mSuccessLambdaMap.put(RequestCode.PERMISSION_EXTERNAL_STORAGE, onSuccess)
        }
        if (onFail!=null){
            mFailLambdaMap.put(RequestCode.PERMISSION_EXTERNAL_STORAGE, onFail)
        }
    }

    fun onPermissionRequestReturn(requestCode: Int, permissions: Array<out String>, grantResults: IntArray, activity: BaseActivity){
        val onSuccess = mSuccessLambdaMap.remove(requestCode)
        val onFail = mFailLambdaMap.remove(requestCode)

        when(requestCode){
            RequestCode.PERMISSION_EXTERNAL_STORAGE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    onSuccess?.invoke(activity)
                }else{
                    onFail?.invoke(activity)
                }
            }
        }


    }

    fun clearCallback(){
        mSuccessLambdaMap.clear()
        mFailLambdaMap.clear()
    }

}