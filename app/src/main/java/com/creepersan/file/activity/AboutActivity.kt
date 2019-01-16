package com.creepersan.file.activity

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import com.creepersan.file.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : BaseActivity(){
    override val mLayoutID: Int = R.layout.activity_about

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActionBar()
        initVersionText()
    }

    private fun initActionBar(){
        // 设置ActionBar
        setSupportActionBar(aboutToolbar)
        // 设置其他
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    private fun initVersionText(){
        val appInfo = packageManager.getPackageInfo( packageName, 0)
        var versionCodeStr = ""
        if (Build.VERSION.SDK_INT >= 28){
            versionCodeStr = appInfo.longVersionCode.toString()
        }else{
            versionCodeStr = appInfo.versionCode.toString()
        }
        aboutVersionText.text = "${appInfo.versionName} ( $versionCodeStr )"
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