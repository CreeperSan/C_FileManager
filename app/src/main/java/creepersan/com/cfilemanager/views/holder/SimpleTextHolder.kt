package creepersan.com.cfilemanager.views.holder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import creepersan.com.cfilemanager.R

/** 一个TextView的holder
 * Created by CreeperSan on 2017/11/19.
 */
class SimpleTextHolder(context: Context, viewGroup: ViewGroup? = null, attachToParent: Boolean = false)
    : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_file_path, viewGroup, attachToParent)) {

    val textView : TextView = itemView.findViewById<TextView>(R.id.itemFilePathPath)
    val context : Context = itemView.context

    fun showAsPath(){
        textView.isClickable = true
        textView.isFocusable = true
    }

    fun setPathValue(pathName:String,onClickListener: View.OnClickListener){
        textView.text = pathName
        textView.setOnClickListener(onClickListener)
    }


}