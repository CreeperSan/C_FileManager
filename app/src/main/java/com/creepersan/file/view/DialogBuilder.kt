package com.creepersan.file.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.creepersan.file.R
import org.w3c.dom.Text
import java.lang.RuntimeException


class SimpleDialog(context: Context,direction:Int=DIRECTION_CENTER,val type:Int= TYPE_MESSAGE) : Dialog(context) {
    val rootView = layoutInflater.inflate(R.layout.dialog_base, null)
    lateinit var viewTitle : TextView
    lateinit var viewPosButton : TextView
    lateinit var viewNegButton : TextView
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

    fun setPosButton(name:String, listener:OnDialogButtonClickListener):SimpleDialog{
        _setDialogButton(viewPosButton, name, listener)
        return this
    }

    fun setNegButton(name:String, listener:OnDialogButtonClickListener):SimpleDialog{
        _setDialogButton(viewNegButton, name, listener)
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

    fun setMessage(message:String):SimpleDialog{
        if (type != TYPE_MESSAGE) throw TypeUnmatchException()
        viewMessage.text = message
        return this
    }

    fun _setDialogButton(view:TextView, name:String, listener: OnDialogButtonClickListener){
        if (name == ""){
            view.isClickable = false
            view.isFocusable = false
            view.background = null
            view.visibility = View.INVISIBLE
        }else{
            view.text = name
            view.setOnClickListener{
                listener.onButtonClick(this)
            }
        }
    }

    class DialogListItem(var title:String,var icon:Int=0,var description:String="")
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

}































//
//class SimpleDialog(context: Context) : Dialog(context){
//    val customView = LayoutInflater.from(context).inflate(R.layout.dialog_base, null)
//
//    private var title = ""                                                      // 标题
//    private var message = ""                                                    // 信息
//    private var mItemList : List<DialogListItem>? = null                        // 如果是图标列表的话
//    private var mListItemClickListener : DialogListItemClickListener? = null    // 列表对话框的点击回调
//    private var isOutsideClickable = true                                       // 是否可以点击外面取消
//    private var popDirection = SimpleDialog.DIRECTION_CENTER                    // 对话框弹出位置
//    private var backgroundAlpha = 0.5f                                          // 背景透明度
//    private var animateDuration = 300                                           // 动画时间
//    private var posButtonText = ""                                              // 确定文本
//    private var posButtonClickListener:DialogButtonClickListener? = null        // 确定的点击事件
//    private var negButtonText = ""                                              // 取消文本
//    private var negButtonClickListener:DialogButtonClickListener? = null        // 取消的点击事件
//    private var type = SimpleDialog.TYPE_EMPTY                                  // 对话框类型
//
//    companion object {
//        const val DIRECTION_CENTER = 0
//        const val DIRECTION_BOTTOM = 1
//        const val TYPE_EMPTY = 0
//        const val TYPE_MESSAGE = 1
//        const val TYPE_LIST_CLICK = 2
//        const val TYPE_CUSTOM_VIEW = 6
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(customView)
//        // 初始化标题
//        when(popDirection){
//            DIRECTION_BOTTOM -> { // 从底部弹出
//                // 设置标题
//                val viewDialogTitleTop = customView.findViewById<ViewStub>(R.id.dialogBaseTitleViewStub).inflate()
//                setupButton(viewDialogTitleTop.findViewById(R.id.dialogBasePosButton), posButtonText, posButtonClickListener)
//                setupButton(viewDialogTitleTop.findViewById(R.id.dialogBaseNegButton), negButtonText, negButtonClickListener)
//                viewDialogTitleTop.findViewById<TextView>(R.id.dialogBaseTitle).text = title
//                // 设置宽度
//                window?.apply {
//                    val params = this.attributes
//                    params.width = WindowManager.LayoutParams.MATCH_PARENT
//                    params.height = WindowManager.LayoutParams.WRAP_CONTENT
//                    this.attributes = params
//                }
//                // 设置位置
//                window?.setGravity(Gravity.BOTTOM)
//            }
//            else -> { // 从中间弹出对话框
//
//            }
//        }
//        //
//    }
//
//    override fun setTitle(title: CharSequence?) {
//        super.setTitle(title)
//
//    }
//
//    fun setTitle(title:String):SimpleDialog{
//        this.title = title
//        return this
//    }
//
//    fun setMessage(message:String):SimpleDialog{
//        type = SimpleDialog.TYPE_MESSAGE
//        this.message = message
//        return this
//    }
//
//    fun setItems(collection:List<DialogListItem>, listItemClickListener:DialogListItemClickListener?=null):SimpleDialog{
//        this.type = SimpleDialog.TYPE_LIST_CLICK
//        this.mItemList = collection
//        this.mListItemClickListener = listItemClickListener
//        return this
//    }
//
//    fun setOutsideClickable(isOutsideClickable:Boolean):SimpleDialog{
//        this.isOutsideClickable = isOutsideClickable
//        return this
//    }
//
//    fun setPopupDirection(popupDirection:Int):SimpleDialog{
//        this.popDirection = popupDirection
//        return this
//    }
//
//    fun setBackgroundAlpha(alpha:Float):SimpleDialog{
//        this.backgroundAlpha = alpha
//        return this
//    }
//
//    fun setAnimationDuration(duration:Int):SimpleDialog{
//        this.animateDuration = duration
//        return this
//    }
//
//    fun setPosButton(posText:String, listener:DialogButtonClickListener):SimpleDialog{
//        this.posButtonText = posText
//        this.posButtonClickListener = listener
//        return this
//    }
//
//    fun setNegButton(negText:String, listener:DialogButtonClickListener):SimpleDialog{
//        this.negButtonText = negText
//        this.negButtonClickListener = listener
//        return this
//    }
//
//    private fun setupButton(view:TextView, name:String, listener:DialogButtonClickListener?){
//        if (name == ""){ // 不显示按钮
//            view.text = ""
//            view.visibility = View.INVISIBLE
//            view.isClickable = false
//            view.isFocusable = false
//            view.background = null
//        }else{ // 正常显示按钮
//            view.text= name
//            view.setOnClickListener {
//                listener?.onClick(this)
//            }
//        }
//    }
//
//    fun updateDialog():SimpleDialog{
//        return this
//    }
//
////    fun build(context:Context):Dialog{
//
////        val inflater = LayoutInflater.from(context)
////        val customView = inflater.inflate(R.layout.dialog_base, null)
////        val dialog = Dialog(context)
////        dialog.setContentView(R.layout.dialog_base)
////        dialog.setContentView(customView)
////        dialog.setCanceledOnTouchOutside(isOutsideClickable)
////        // 弹出方向
////        when(popDirection){
////            DialogBuilder.DIRECTION_BOTTOM -> { // 从底部弹出
////                // 设置标题
////                val viewDialogTitleTop = customView.findViewById<ViewStub>(R.id.dialogBaseTitleViewStub).inflate()
////                setupButton(viewDialogTitleTop.findViewById(R.id.dialogBasePosButton), posButtonText, posButtonClickListener, dialog)
////                setupButton(viewDialogTitleTop.findViewById(R.id.dialogBaseNegButton), negButtonText, negButtonClickListener, dialog)
////                viewDialogTitleTop.findViewById<TextView>(R.id.dialogBaseTitle).text = title
////                // 设置宽度
////                dialog.window?.apply {
////                    val params = this.attributes
////                    params.width = WindowManager.LayoutParams.MATCH_PARENT
////                    params.height = WindowManager.LayoutParams.WRAP_CONTENT
////                    this.attributes = params
////                }
////                // 设置位置
////                dialog.window?.setGravity(Gravity.BOTTOM)
////            }
////            else -> { // 从中间弹出对话框
////
////            }
////        }
////        // 显示内容类型
////        when(type){
////            DialogBuilder.TYPE_EMPTY -> {
////
////            }
////            DialogBuilder.TYPE_MESSAGE -> {
////                val viewMessage = customView.findViewById<ViewStub>(R.id.dialogBaseMessage).inflate() as TextView
////                viewMessage.text = message
////            }
////            DialogBuilder.TYPE_CUSTOM_VIEW -> {
////
////            }
////            DialogBuilder.TYPE_LIST_CLICK -> {
////                val recyclerView = customView.findViewById<ViewStub>(R.id.dialogBaseList).inflate() as RecyclerView
////                recyclerView.layoutManager = LinearLayoutManager(context)
////                recyclerView.adapter = DialogAdapter(mItemList ?: ArrayList(), mListItemClickListener)
////
////            }
////        }
////        return dialog
////    }
//}
//
//
//class DialogListItem(var icon:Int, var title:String, var description:String="")
//class DialogRecyclerViewHolder(context:Context, parent:ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_dialog_base_list,parent,false)){
//    private val imageIcon = itemView.findViewById<ImageView>(R.id.itemDialogBaseListIcon)!!
//    private val titleText = itemView.findViewById<TextView>(R.id.itemDialogBaseListTitle)!!
//
//    fun initItem(icon:Int, title:String){
//        imageIcon.setImageResource(icon)
//        titleText.text = title
//    }
//}
//class DialogAdapter(val itemList:List<DialogListItem>, val listener:DialogListItemClickListener?=null) : RecyclerView.Adapter<DialogRecyclerViewHolder>(){
//    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DialogRecyclerViewHolder {
//        return DialogRecyclerViewHolder(p0.context, p0)
//    }
//
//    override fun getItemCount(): Int {
//        return itemList.size
//    }
//
//    override fun onBindViewHolder(holder: DialogRecyclerViewHolder, pos: Int) {
//        val tmpItem = itemList[pos]
//        holder.initItem(tmpItem.icon, tmpItem.title)
//        holder.itemView.setOnClickListener {
//            listener?.onClick(tmpItem, holder.adapterPosition)
//        }
//    }
//
//}
//
//
//
//interface DialogButtonClickListener{
//    fun onClick(dialog:Dialog)
//}
//interface DialogListItemClickListener{
//    fun onClick(item:DialogListItem, pos:Int)
//}