package com.creepersan.file.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.creepersan.file.R
import kotlinx.android.synthetic.main.fragment_file.*

class FileFragment : BaseFragment(){
    override val mLayoutID: Int = R.layout.fragment_file
    private val mAdapter by lazy { FileAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView(){
        fragmentFileRecyclerView.layoutManager = LinearLayoutManager(activity)
        fragmentFileRecyclerView.adapter = mAdapter
    }


    inner class FileAdapter : RecyclerView.Adapter<FileHolder>(){
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FileHolder {
            return FileHolder(p0)
        }

        override fun getItemCount(): Int {
            return 192
        }

        override fun onBindViewHolder(p0: FileHolder, p1: Int) {

        }

    }

    inner class FileHolder(container:ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_file, container, false)){


    }

}