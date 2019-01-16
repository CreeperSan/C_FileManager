package com.creepersan.file.fragment

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.creepersan.file.FileApplication
import com.creepersan.file.R
import kotlinx.android.synthetic.main.fragment_application.*

class ApplicationFragment : BaseMainActivityFragment() {

    override val mLayoutID: Int = R.layout.fragment_application


    override fun getTitle(): String {
        return FileApplication.getInstance().getString(R.string.applicationFragmentTitle)
    }

    override fun getIcon(): Int {
        return R.drawable.ic_application_icon
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initActionBar()
        initRecyclerView()
    }

    private fun initActionBar(){

    }
    private fun initRecyclerView(){
        applicationFragmentRecyclerView.layoutManager = GridLayoutManager(context, 4)
    }


    private inner class ApplicationItemViewHolder(parent:ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_application_item, parent, false)){

        

    }

}