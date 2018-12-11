package com.creepersan.file.activity

import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.creepersan.file.R

class BootActivity : BaseActivity() {
    override val mLayoutID: Int = R.layout.activity_boot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 检查权限
        if (hasStoragePermission()){
            startActivity(MainActivity::class.java, true)
        }else{
            requireStoragePermission()
        }
    }


    /**
     *  权限类
     */
    protected fun hasStoragePermission():Boolean{
        return mPermissionUtil.hasStoragePermission(this)
    }
    protected fun requireStoragePermission(){
        mPermissionUtil.requireStoragePermision(this, {
            startActivity(MainActivity::class.java, true)
        }, {
            alertPermissionDenied()
        })
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mPermissionUtil.onPermissionRequestReturn(requestCode, permissions, grantResults, this)
    }

    /**
     *  提示类
     */
    protected fun alertPermissionDenied(){
        AlertDialog.Builder(this)
            .setTitle(R.string.baseAlertPermissionDeniedTitle)
            .setMessage(R.string.baseAlertPermissionDeniedMessage)
            .setPositiveButton(R.string.baseAlertPositiveButtonText) { _, _ ->
                requireStoragePermission()
            }
            .setNegativeButton(R.string.baseAlertNegativeButtonText) { _, _ ->
                getFileApplication().exit()
            }
            .show()
    }
}
