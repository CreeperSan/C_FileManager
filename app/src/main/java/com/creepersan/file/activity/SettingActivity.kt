package com.creepersan.file.activity

import android.os.Bundle
import android.view.MenuItem
import com.creepersan.file.R
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity(){
    override val mLayoutID: Int = R.layout.activity_setting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
    }

    private fun initToolbar(){
        setSupportActionBar(settingToolbar)
        // 设置其他
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }

}
