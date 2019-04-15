package com.creepersan.file.function.file.fragment

import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.creepersan.file.FileApplication
import com.creepersan.file.R
import com.creepersan.file.fragment.BaseMainActivityFragment

class FileFragment : BaseMainActivityFragment(), Toolbar.OnMenuItemClickListener {

    companion object {
        private const val KEY_ROOT_PATH = "RootPath"
        private val DEFAULT_ROOT_PATH = Environment.getExternalStorageState()


        fun newInstance(rootPath: String = DEFAULT_ROOT_PATH): FileFragment {
            val fragment = FileFragment()
            fragment.arguments = Bundle().apply {
                putString(KEY_ROOT_PATH, rootPath)
            }
            return fragment
        }
    }


    /***
     * 重写的一些属性方法
     */
    private val mTitle by lazy { FileApplication.getInstance().getString(R.string.fileFragmentTitle) }
    override fun getTitle(): String = mTitle
    override fun getIcon(): Int = R.drawable.ic_file_icon
    override val mLayoutID: Int = R.layout.fragment_file


    /***
     * 生命周期
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initArgument()
    }

    private fun initArgument(){

    }





    /**
     *  暴露给Activity的接口
     */
    fun getCurrentPath(): String {
        return ""
    }
    override fun isCanPasteFileHere(): Boolean = true

    /**
     *  事件回调
     */
    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        return true
    }


}