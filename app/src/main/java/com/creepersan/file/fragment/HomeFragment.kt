package com.creepersan.file.fragment

import com.creepersan.file.FileApplication
import com.creepersan.file.R

class HomeFragment : BaseMainActivityFragment() {

    private val mTitle by lazy { FileApplication.getInstance().getString(R.string.fragmentHomeTitle) }

    override fun getTitle(): String {
        return mTitle
    }

    override fun getIcon(): Int {
        return R.drawable.ic_file_home
    }

    override val mLayoutID: Int = R.layout.fragment_home



}