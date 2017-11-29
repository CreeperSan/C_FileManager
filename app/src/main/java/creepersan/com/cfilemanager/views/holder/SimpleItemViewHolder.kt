package creepersan.com.cfilemanager.views.holder

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import com.bumptech.glide.Glide
import creepersan.com.cfilemanager.R

/** 针对item_main_drawer_item.xml布局文件的一个ViewHolder
 * Created by CreeperSan on 2017/11/8.
 */
class SimpleItemViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
    val switch : Switch
    val endIcon: ImageView
    val startIcon: ImageView
    val textLayout : LinearLayout
    val titleText : TextView
    val subTitleText : TextView
    val endLayout : LinearLayout

    constructor(context:Context,viewGroup:ViewGroup) : this(LayoutInflater.from(context).inflate(R.layout.item_main_drawer_item,viewGroup,false))

    init {
        switch = itemView.findViewById<Switch>(R.id.itemMainDrawerItemSwitch)
        endIcon = itemView.findViewById<ImageView>(R.id.itemMainDrawerItemEndButton)
        startIcon = itemView.findViewById<ImageView>(R.id.itemMainDrawerItemStartButton)
        textLayout = itemView.findViewById<LinearLayout>(R.id.itemMainDrawerItemTextLayout)
        titleText = itemView.findViewById<TextView>(R.id.itemMainDrawerItemTextTitle)
        subTitleText = itemView.findViewById<TextView>(R.id.itemMainDrawerItemTextSubTitle)
        endLayout = itemView.findViewById<LinearLayout>(R.id.itemMainDrawerItemEndLayout)
    }

    fun showAsItem(){
        switch.visibility = View.GONE
        endIcon.visibility = View.GONE
        startIcon.visibility = View.VISIBLE
        textLayout.visibility = View.VISIBLE
        titleText.visibility = View.VISIBLE
        subTitleText.visibility = View.GONE
        itemView.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.mainDrawerItemBackground))
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP,12f)
    }

    fun showAsCategory(){
        switch.visibility = View.GONE
        endIcon.visibility = View.VISIBLE
        startIcon.visibility = View.GONE
        textLayout.visibility = View.VISIBLE
        titleText.visibility = View.VISIBLE
        subTitleText.visibility = View.GONE
        itemView.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.mainDrawerCategoryBackground))
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP,14f)
    }

    /**
     *  设置右边的图标
     */
    fun setupEndIcon(visible:Boolean,@DrawableRes img:Int = 0,rotation:Float = 0F,onClickListener : View.OnClickListener? = null){
        if (visible && img!=0){
            endIcon.visibility = View.VISIBLE
            Glide.with(itemView.context).load(img).into(endIcon)
            endIcon.isClickable = onClickListener!=null
            endIcon.isClickable = onClickListener!=null
            if(onClickListener!=null)
                endIcon.setOnClickListener(onClickListener)
            endIcon.rotation = rotation
        }else{
            endIcon.visibility = View.GONE
        }
    }
    /**
     *  设置背景
     */
    fun setupBackground(@DrawableRes drawableRes:Int){
        itemView.setBackgroundResource(drawableRes)
    }
    /**
     *  显示为子项
     */
    fun setupStartIcon(visibility:Int,@DrawableRes img: Int = 0){
        startIcon.visibility = visibility
        if (visibility==View.VISIBLE)
            Glide.with(itemView.context).load(img).into(startIcon)
    }
    fun setupEndIconPadding(size:Float){
        val width = endIcon.width
        val padding = (width*0.5f*size).toInt()
        endIcon.setPadding(padding,padding,padding,padding)
    }

}
