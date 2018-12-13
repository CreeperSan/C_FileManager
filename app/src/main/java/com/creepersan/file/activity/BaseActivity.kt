package com.creepersan.file.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.creepersan.file.FileApplication
import com.creepersan.file.R
import com.creepersan.file.utils.Logger
import com.creepersan.file.utils.PermissionUtil
import java.io.Serializable

abstract class BaseActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_PERMISSION_STORAGE = 0
    }

    abstract val mLayoutID : Int
    protected val mPermissionUtil by lazy { PermissionUtil }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mLayoutID)
    }

    protected fun <T:BaseActivity> startActivity(clazz: Class<T>, isFinish:Boolean=false,vararg params:Pair<String,Any>){
        val intent = Intent(this@BaseActivity, clazz)
        params.forEach { pair ->
            when(pair.second){
                is String       -> { intent.putExtra(pair.first, pair.second as String       ) }
                is Boolean      -> { intent.putExtra(pair.first, pair.second as Boolean      ) }
                is Byte         -> { intent.putExtra(pair.first, pair.second as Byte         ) }
                is Char         -> { intent.putExtra(pair.first, pair.second as Char         ) }
                is Short        -> { intent.putExtra(pair.first, pair.second as Short        ) }
                is Int          -> { intent.putExtra(pair.first, pair.second as Int          ) }
                is Long         -> { intent.putExtra(pair.first, pair.second as Long         ) }
                is Float        -> { intent.putExtra(pair.first, pair.second as Float        ) }
                is Double       -> { intent.putExtra(pair.first, pair.second as Double       ) }
                is CharSequence -> { intent.putExtra(pair.first, pair.second as CharSequence ) }
                is Parcelable   -> { intent.putExtra(pair.first, pair.second as Parcelable   ) }
                is IntArray     -> { intent.putExtra(pair.first, pair.second as IntArray     ) }
                is Serializable -> { intent.putExtra(pair.first, pair.second as Serializable ) }
                is BooleanArray -> { intent.putExtra(pair.first, pair.second as BooleanArray ) }
                is ByteArray    -> { intent.putExtra(pair.first, pair.second as ByteArray    ) }
                is ShortArray   -> { intent.putExtra(pair.first, pair.second as ShortArray   ) }
                is CharArray    -> { intent.putExtra(pair.first, pair.second as CharArray    ) }
                is LongArray    -> { intent.putExtra(pair.first, pair.second as LongArray    ) }
                is FloatArray   -> { intent.putExtra(pair.first, pair.second as FloatArray   ) }
                is DoubleArray  -> { intent.putExtra(pair.first, pair.second as DoubleArray  ) }
                is Intent       -> { intent.putExtra(pair.first, pair.second as Intent       ) }
                is Bundle       -> { intent.putExtra(pair.first, pair.second as Bundle       ) }
                else            -> { logW("StartActivity 参数 ${pair.first} 不符合类型要求，被抛弃") }
            }
        }
        startActivity(intent)
        if (isFinish){
            finish()
        }
    }

    /**
     *  应用程序管理
     */
    protected fun getFileApplication():FileApplication{
        return application as FileApplication
    }
    protected fun exit(){
        getFileApplication().exit()
    }


    /**
     *  调试类
     */
    protected fun log(content:String){
        Logger.log(content, javaClass.simpleName)
    }
    protected fun logW(content:String){
        Logger.logW(content, javaClass.simpleName)
    }
    protected fun logE(content:String){
        Logger.logE(content, javaClass.simpleName)
    }

    fun toast(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}