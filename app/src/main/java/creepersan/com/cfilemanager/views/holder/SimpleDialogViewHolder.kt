package creepersan.com.cfilemanager.views.holder

import android.content.Context
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import creepersan.com.cfilemanager.R

/** 简单的对话框的ViewHolder
 * Created by CreeperSan on 2017/11/10.
 */
class SimpleDialogViewHolder(simpleDialogView: View) {
    val card:CardView
    val contentTab : LinearLayout
    val titleTab : LinearLayout
    val icon : ImageView
    val title : TextView
    val content : TextView
    val negativeButton : Button
    val positiveButton : Button

    constructor(context:Context):this(LayoutInflater.from(context).inflate(R.layout.dialog_simple,null))

    init {
        card = simpleDialogView as CardView
        contentTab = simpleDialogView.findViewById<LinearLayout>(R.id.dialogSimpleContentTab)
        titleTab = simpleDialogView.findViewById<LinearLayout>(R.id.dialogSimpleTitleTab)
        icon = simpleDialogView.findViewById<ImageView>(R.id.dialogSimpleIcon)
        title = simpleDialogView.findViewById<TextView>(R.id.dialogSimpleTitle)
        content = simpleDialogView.findViewById<TextView>(R.id.dialogSimpleContent)
        negativeButton = simpleDialogView.findViewById<Button>(R.id.dialogSimpleNegativeButton)
        positiveButton = simpleDialogView.findViewById<Button>(R.id.dialogSimplePositiveButton)
    }

}