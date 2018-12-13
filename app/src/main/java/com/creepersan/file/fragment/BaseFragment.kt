package com.creepersan.file.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.creepersan.file.activity.BaseActivity

abstract class BaseFragment : Fragment() {
    abstract val mLayoutID:Int
    private val mActivity by lazy { activity as BaseActivity }

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