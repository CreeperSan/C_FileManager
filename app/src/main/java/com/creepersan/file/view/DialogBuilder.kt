package com.creepersan.file.view

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.creepersan.file.R
import com.creepersan.file.utils.Logger
import org.w3c.dom.Text
import java.lang.RuntimeException


open class SimpleDialog(context: Context,direction:Int=DIRECTION_CENTER,val type:Int= TYPE_MESSAGE) : Dialog(context) {
    val rootView = layoutInflater.inflate(R.layout.dialog_base, null)
    lateinit var viewTitle : TextView
    lateinit var viewPosButton : TextView
    lateinit var viewNegButton : TextView
    lateinit var viewCustomViewGroup : FrameLayout
    lateinit var viewRecyclerView : RecyclerView
    lateinit var viewMessage : TextView

    companion object {
        const val DIRECTION_BOTTOM = 1
        const val DIRECTION_CENTER = 0

        const val TYPE_MESSAGE = 0
        const val TYPE_LIST = 1
        const val TYPE_CUSTOM_VIEW = 2
    }

    init {
        // 设置自定义布局
        setContentView(rootView)
        // 设置标题
        when(direction){
            DIRECTION_BOTTOM -> {
                val viewTitleBar = rootView.findViewById<ViewStub>(R.id.dialogBaseTitleViewStub).inflate()
                viewTitle = viewTitleBar.findViewById(R.id.dialogBaseTitle)
                viewPosButton = viewTitleBar.findViewById(R.id.dialogBasePosButton)
                viewNegButton = viewTitleBar.findViewById(R.id.dialogBaseNegButton)
            }
            else -> {
                val viewTitleBar = rootView.findViewById<ViewStub>(R.id.dialogBaseTitleViewStub).inflate()
                viewTitle = viewTitleBar.findViewById(R.id.dialogBaseTitle)
                viewTitle.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                viewTitleBar.findViewById<TextView>(R.id.dialogBasePosButton).visibility = View.GONE // 隐藏了头部的按钮
                viewTitleBar.findViewById<TextView>(R.id.dialogBaseNegButton).visibility = View.GONE // 隐藏了头部的按钮
                val viewBottomBar = rootView.findViewById<ViewStub>(R.id.dialogBaseBottomActionButton).inflate()
                viewPosButton = viewBottomBar.findViewById(R.id.dialogBasePosButton)
                viewNegButton = viewBottomBar.findViewById(R.id.dialogBaseNegButton)
            }
        }
        // 设置弹出位置
        when(direction){
            DIRECTION_BOTTOM -> {
                window?.setGravity(Gravity.BOTTOM)
            }
            else -> {}
        }
        // 设置Dialog宽度
        val params = window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = params
        // 设置内容
        when(type){
            TYPE_MESSAGE -> {
                viewMessage = rootView.findViewById<ViewStub>(R.id.dialogBaseMessage).inflate() as TextView
            }
            TYPE_CUSTOM_VIEW -> {
                viewCustomViewGroup = rootView.findViewById<ViewStub>(R.id.dialogBaseCustomView).inflate() as FrameLayout
            }
            TYPE_LIST -> {
                viewRecyclerView = rootView.findViewById<ViewStub>(R.id.dialogBaseList).inflate() as RecyclerView
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun setTitle(title:String):SimpleDialog{
        viewTitle.text = title
        return this
    }

    fun setTitleID(titleStrID:Int):SimpleDialog{
        viewTitle.text = context.getString(titleStrID)
        return this
    }

    override fun setTitle(strID:Int){
        Logger.logE("请使用 SimpleDialog#setTitleID(Int) 代替", "SimpleDialog")
        throw IllegalStateException()
    }

    fun setPosButton(name:String, listener:OnDialogButtonClickListener):SimpleDialog{
        _setDialogButton(viewPosButton, name, listener)
        return this
    }

    fun setPosButton(nameID:Int, listener:OnDialogButtonClickListener):SimpleDialog{
        _setDialogButton(viewPosButton, context.getString(nameID), listener)
        return this
    }

    fun setNegButton(name:String, listener:OnDialogButtonClickListener?=null):SimpleDialog{
        _setDialogButton(viewNegButton, name, listener)
        return this
    }

    fun setNegButton(nameID:Int, listener:OnDialogButtonClickListener?=null):SimpleDialog{
        _setDialogButton(viewNegButton, context.getString(nameID), listener)
        return this
    }

    fun setItems(items:ArrayList<DialogListItem>, listener: OnDialogListItemClickListener):SimpleDialog{
        if (type != TYPE_LIST) throw TypeUnmatchException()
        if (viewRecyclerView.adapter == null){
            viewRecyclerView.layoutManager = LinearLayoutManager(context)
            viewRecyclerView.adapter = DialogListAdapter(this, items, listener)
        }else{
            val adapter = viewRecyclerView.adapter
            if (adapter is DialogListAdapter){
                adapter.items = items
                adapter.notifyDataSetChanged()
            }
        }
        return this
    }

    fun setCustomView(customView:View, onViewSetAction:((view:View)->Unit)?=null):SimpleDialog{
        if (type != TYPE_CUSTOM_VIEW) throw TypeUnmatchException()
        viewCustomViewGroup.removeAllViews()
        viewCustomViewGroup.addView(customView)
        initCustomView(customView)
        onViewSetAction?.invoke(customView)
        return this
    }

    fun setMessage(message:String):SimpleDialog{
        if (type != TYPE_MESSAGE) throw TypeUnmatchException()
        viewMessage.text = message
        return this
    }

    fun setMessage(messageStrID:Int):SimpleDialog{
        return setMessage(context.getString(messageStrID))
    }

    fun _setDialogButton(view:TextView, name:String, listener: OnDialogButtonClickListener?){
        if (name == ""){
            view.isClickable = false
            view.isFocusable = false
            view.background = null
            view.visibility = View.GONE
        }else{
            view.text = name
            view.isClickable = true
            view.isFocusable = true
            view.visibility = View.VISIBLE
            view.setOnClickListener{
                if (listener == null){
                    dismiss()
                }else{
                    listener.onButtonClick(this)
                }
            }
        }
    }

    class DialogListItem(var title:String,var icon:Int=0, var id:Int=0,var description:String="")
    class TypeUnmatchException:RuntimeException()
    class DialogListAdapter(val dialog:SimpleDialog,var items:ArrayList<DialogListItem>, var listener:OnDialogListItemClickListener) : RecyclerView.Adapter<DialogListItemViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogListItemViewHolder {
            return DialogListItemViewHolder(dialog.context, parent)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: DialogListItemViewHolder, pos: Int) {
            val item = items[pos]
            holder.initView(item.title, item.icon, item.description)
            holder.itemView.setOnClickListener {
                listener.onItemClick(dialog,item, pos)
            }
        }

    }
    class DialogListItemViewHolder(context:Context, parent:ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_dialog_base_list, parent, false)){
        val imageIcon = itemView.findViewById<ImageView>(R.id.itemDialogBaseListIcon)
        val textTitle = itemView.findViewById<TextView>(R.id.itemDialogBaseListTitle)
        val textDescription = itemView.findViewById<TextView>(R.id.itemDialogBaseListDescription)

        fun initView(title: String, icon: Int, description: String){
            imageIcon.setImageResource(icon)
            textTitle.text = title
            if (description == ""){
                textDescription.visibility = View.GONE
            }else{
                textDescription.text = description
            }
        }
    }
    interface OnDialogButtonClickListener{
        fun onButtonClick(dialog:SimpleDialog)
    }
    interface OnDialogListItemClickListener{
        fun onItemClick(dialog: SimpleDialog,item:DialogListItem, pos:Int)
    }


    /**
     *  下面是可以重写的方法
     */
    open fun initCustomView(customView:View){}
}



