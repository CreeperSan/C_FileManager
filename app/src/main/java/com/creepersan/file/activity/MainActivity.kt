package com.creepersan.file.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.creepersan.file.R
import com.creepersan.file.fragment.FileFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    override val mLayoutID: Int = R.layout.activity_main

    private val fragment by lazy { FileFragment() }
    private val mOperationList by lazy { arrayListOf(
        OperationItem(R.drawable.ic_file_paste, getString(R.string.textMainEndDrawerOperationAllPasteTo)),
        OperationItem(R.drawable.ic_file_cut, getString(R.string.textMainEndDrawerOperationAllMoveTo)),
        OperationItem(R.drawable.ic_file_delete, getString(R.string.textMainEndDrawerOperationAllDelete)),
        OperationItem(R.drawable.ic_file_clear, getString(R.string.textMainEndDrawerOperationAllClear))
        ) }
    private val mOperationAdapter by lazy { RightDrawerOperationAdapter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRightDrawer()
        initFrameLayout()

    }

    private fun initRightDrawer(){
        // 初始化操作列表部分
        mainEndDrawerOperationDrawerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mainEndDrawerOperationDrawerView.adapter = mOperationAdapter
    }
    private fun initFrameLayout(){
        supportFragmentManager.beginTransaction().add(mainFrameLayout.id, fragment).commitNow()
    }

    override fun onBackPressed() {
        if (!fragment.onBackPressed()){
            super.onBackPressed()
        }
    }

    class OperationItem(val icon:Int, val name:String)
    inner class RightDrawerOperationAdapter : RecyclerView.Adapter<RightDrawerOperationViewHolder>(){
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RightDrawerOperationViewHolder {
            return RightDrawerOperationViewHolder(p0)
        }

        override fun getItemCount(): Int {
            return mOperationList.size
        }

        override fun onBindViewHolder(holder: RightDrawerOperationViewHolder, pos: Int) {
            val tmpItem = mOperationList[pos]
            holder.setItem(tmpItem.icon, tmpItem.name)
        }
    }
    inner class RightDrawerOperationViewHolder(parent:ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_main_end_drawer_operation_list,parent,false)){
        private val imageIcon = itemView.findViewById<ImageView>(R.id.itemMainEndDrawerOperationIcon)
        private val nameText = itemView.findViewById<TextView>(R.id.itemMainEndDrawerOperationName)

        fun setItem(icon:Int, name:String){
            imageIcon.setImageResource(icon)
            nameText.text = name
        }
    }

}