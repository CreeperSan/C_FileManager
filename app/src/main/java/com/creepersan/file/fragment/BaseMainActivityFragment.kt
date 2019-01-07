package com.creepersan.file.fragment

import com.creepersan.file.activity.MainActivity

abstract class BaseMainActivityFragment : BaseFragment(){
    protected val activity by lazy { mActivity as MainActivity }
    protected open fun isCanPasteFileHere():Boolean = false
    abstract fun getTitle():String
    abstract fun getIcon():Int

    open fun onBackPressed():Boolean{
        return false
    }
}