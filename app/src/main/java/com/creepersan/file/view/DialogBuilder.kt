package com.creepersan.file.view

import android.app.Dialog
import android.content.Context
import android.view.View

class DialogBuilder {
    private var title = ""                                          // 标题
    private var message = ""                                        // 信息
    private var mItemList : ArrayList<DialogListItem>? = null       // 如果是图标列表的话
    private var isCancelable = true                                 // 是否可以使用返回键或者点击外面取消
    private var isOutsideClickable = true                           // 是否可以点击外面取消
    private var popDirection = DIRECTION_CENTER                     // 对话框弹出位置
    private var backgroundAlpha = 0.5f                              // 背景透明度
    private var animateDuration = 300                               // 动画时间
    private var posButtonText = ""                                  // 确定文本
    private var posButtonClickListener:View.OnClickListener? = null // 确定的点击事件
    private var negButtonText = ""                                  // 取消文本
    private var negButtonClickListener:View.OnClickListener? = null // 取消的点击事件
    private var customView : View? = null                           // 自定义View
    private var type = TYPE_EMPTY                                   // 对话框类型

    companion object {
        const val DIRECTION_CENTER = 0
        const val DIRECTION_BOTTOM = 1
        const val TYPE_EMPTY = 0
        const val TYPE_MESSAGE = 1
        const val TYPE_LIST_CLICK = 2
        const val TYPE_LIST_SELECT = 3
        const val TYPE_LIST_MULTI = 4
        const val TYPE_LIST_CUSTOM = 5
    }

    fun setTitle(title:String){
        this.title = title
    }

    fun setMessage(message:String){
        type = TYPE_MESSAGE
        this.message = message
    }

    fun setItems(collection:List<DialogListItem>){

        this.type = TYPE_LIST_CLICK
    }

    fun setCancelable(isCancelable:Boolean){
        this.isCancelable = isCancelable
    }

    fun setOutsideClickable(isOutsideClickable:Boolean){
        this.isOutsideClickable = isOutsideClickable
    }

    fun setPopupDirection(popupDirection:Int){
        this.popDirection = popupDirection
    }

    fun setBackgroundAlpha(alpha:Float){
        this.backgroundAlpha = alpha
    }

    fun setAnimationDuration(duration:Int){
        this.animateDuration = duration
    }

    fun setPosButton(){

    }

    fun setNegButton(){

    }

    fun setView(){

    }

    fun create(context:Context):Dialog{
        val dialog = Dialog(context)
        
        return dialog
    }

}

class DialogListItem(var index:Int, var icon:Int, var title:String, var description:String)