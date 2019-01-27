package com.creepersan.file.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView
import com.creepersan.file.R
import kotlinx.android.synthetic.main.activity_permission_description.*

class PermissionDescriptionActivity : BaseActivity() {

    override val mLayoutID: Int = R.layout.activity_permission_description

    private lateinit var mDataStringList:Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolBar()
        initData()
        initRecyclerView()
    }

    private fun initToolBar(){
        // 标题
        permissionDescriptionToolBar.setTitle(R.string.permissionDescriptionTitle)
        // Navigation Icon
        permissionDescriptionToolBar.setNavigationIcon(R.drawable.ic_arrow_left_white)
        permissionDescriptionToolBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    private fun initData(){
        mDataStringList = resources.getStringArray(R.array.permissionDescriptionDataArray)
    }
    private fun initRecyclerView(){
        permissionDescriptionRecyclerView.layoutManager = LinearLayoutManager(this)
        permissionDescriptionRecyclerView.adapter = PermissionAdapter()
    }


    /* 内部类 */
    private inner class PermissionAdapter : RecyclerView.Adapter<PermissionHolder>(){
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PermissionHolder {
            return PermissionHolder(p0)
        }

        override fun getItemCount(): Int {
            return mDataStringList.size / 2
        }

        override fun onBindViewHolder(holder: PermissionHolder, pos: Int) {
            holder.setContent(mDataStringList[pos*2], mDataStringList[pos*2+1])
        }

    }
    private inner class PermissionHolder(parent:ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_permission_description,parent,false)){
        private val titleText = itemView.findViewById<TextView>(R.id.itemPermissionDescriptionTitle)
        private val descriptionText = itemView.findViewById<TextView>(R.id.itemPermissionDescriptionDescription)

        fun setContent(title:String, description:String){
            titleText.text = title
            descriptionText.text = description
        }
    }

}