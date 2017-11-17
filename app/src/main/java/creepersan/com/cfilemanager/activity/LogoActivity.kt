package creepersan.com.cfilemanager.activity

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import creepersan.com.cfilemanager.R
import creepersan.com.cfilemanager.base.BaseActivity
import creepersan.com.cfilemanager.callback.DialogButtonCallback
import creepersan.com.cfilemanager.constance.Command
import creepersan.com.cfilemanager.constance.RequestCode
import creepersan.com.cfilemanager.util.PrefHelper
import creepersan.com.cfilemanager.views.component.SimpleDialogBuilder
import creepersan.com.cfilemanager.views.holder.SimpleDialogViewHolder
import kotlinx.android.synthetic.main.activity_logo.*

/** LogoActivity
 * Created by CreeperSan on 2017/11/9.
 */
class LogoActivity : BaseActivity(){

    override fun getLayoutID(): Int = R.layout.activity_logo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initImage()
        if (checkPermission()){
            initFinish()
        }else{
            showRequireDialog()
        }
    }

    /**
     *  初始化部分
     */
    private fun initImage(){
        bootBackground.loadImage(R.mipmap.img_boot_background)
        bootIcon.loadImage(R.mipmap.app_icon)
    }


    /**
     * 初始化相关事件
     */
    private fun initFinish() {
        handel.postDelayed({
            startActivity(MainActivity::class.java,isFinish = true)
        },PrefHelper.getBootLogoTime())
    }

    private fun showRequireDialog() {
        SimpleDialogBuilder(this)
                .setIcon(R.drawable.ic_shield)
                .setTitle(R.string.permissionRequire)
                .setContent(R.string.needReadWriteExternalStoragePermission)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,object : DialogButtonCallback{
                    override fun onClick(dialog: Dialog, dialogViewHolder: SimpleDialogViewHolder) {
                        requestPermission(RequestCode.PERMISSION_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                })
                .setNegativeButton(R.string.cancel,object : DialogButtonCallback{
                    override fun onClick(dialog: Dialog, dialogViewHolder: SimpleDialogViewHolder) {
                        postCommand(Command.EXIT)
                    }
                })
                .show()
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RequestCode.PERMISSION_EXTERNAL_STORAGE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                initFinish()
            }else{
                postCommand(Command.EXIT)
            }
        }
    }

}