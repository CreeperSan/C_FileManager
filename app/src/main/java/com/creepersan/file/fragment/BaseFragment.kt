package com.creepersan.file.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.creepersan.file.FileApplication
import com.creepersan.file.activity.BaseActivity
import com.creepersan.file.utils.ConfigUtil

abstract class BaseFragment : Fragment() {
    abstract val mLayoutID:Int
    protected val mActivity by lazy { activity as BaseActivity }
    protected val mConfig by lazy { FileApplication.getConfigInstance() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(mLayoutID, container, false)
    }



    /**
     * 调试类
     */
    protected fun toast(message:String){
        mActivity.toast(message)
    }

}