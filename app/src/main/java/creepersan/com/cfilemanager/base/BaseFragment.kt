package creepersan.com.cfilemanager.base

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/** 基础fragment
 * Created by CreeperSan on 2017/11/8.
 */
abstract class BaseFragment : Fragment(){

    abstract fun getLayoutID():Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutID(),container,false)
    }

    /**
     *  自定义Getter & Setter
     */
    protected fun activity():BaseActivity{
        return activity as BaseActivity
    }

    /**
     * Toast
     */
    fun toast(@StringRes contentID: Int){
        activity().toast(contentID)
    }
    fun toast(content: String){
        activity().toast(content)
    }
    fun toastLong(@StringRes contentID:Int){
        activity().toastLong(contentID)
    }
    fun toastLong(content:String){
        activity().toastLong(content)
    }
    fun toastNew(@StringRes content:Int){
        activity().toastNew(content)
    }
    fun toastNew(content:String){
        activity().toastNew(content)
    }
    fun toastLongNew(@StringRes content:Int){
        activity().toastLongNew(content)
    }
    fun toastLongNew(content:String){
        activity().toastLongNew(content)
    }

    /**
     * Log
     */
    fun log(content:String){
        activity().log(content)
    }
    fun logW(content:String){
        activity().logW(content)
    }
    fun logE(content:String){
        activity().logE(content)
    }

}