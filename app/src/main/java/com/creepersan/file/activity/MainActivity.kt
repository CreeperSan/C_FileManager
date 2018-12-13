package com.creepersan.file.activity

import android.os.Bundle
import com.creepersan.file.R
import com.creepersan.file.fragment.FileFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    override val mLayoutID: Int = R.layout.activity_main

    val fragment by lazy { FileFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFrameLayout()

    }

    private fun initFrameLayout(){
        supportFragmentManager.beginTransaction().add(mainFrameLayout.id, fragment).commitNow()
    }

    override fun onBackPressed() {
        if (!fragment.onBackPressed()){
            super.onBackPressed()
        }
    }

}