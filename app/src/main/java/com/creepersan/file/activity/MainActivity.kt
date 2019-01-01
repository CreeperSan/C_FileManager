package com.creepersan.file.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.creepersan.file.FileApplication
import com.creepersan.file.R
import com.creepersan.file.activity.MainActivity.StartActionBaseItem.Companion.ID_EXIT
import com.creepersan.file.fragment.FileFragment
import com.creepersan.file.utils.getTypeIconID
import com.creepersan.file.view.SimpleDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : BaseActivity() {
    override val mLayoutID: Int = R.layout.activity_main

    private val mMessageDialog by lazy { SimpleDialog(this, SimpleDialog.DIRECTION_CENTER, SimpleDialog.TYPE_MESSAGE) }

    private val fragment by lazy { FileFragment() }
    private val mOperationActionList by lazy { arrayListOf(
        EndOperationItem(R.drawable.ic_file_paste, getString(R.string.textMainEndDrawerOperationAllPasteTo), EndOperationItem.PASTE_ALL),
        EndOperationItem(R.drawable.ic_file_cut, getString(R.string.textMainEndDrawerOperationAllMoveTo), EndOperationItem.CUT_ALL),
        EndOperationItem(R.drawable.ic_file_delete, getString(R.string.textMainEndDrawerOperationAllDelete), EndOperationItem.DELETE_ALL),
        EndOperationItem(R.drawable.ic_close, getString(R.string.textMainEndDrawerOperationAllClear), EndOperationItem.CLEAR_ALL)
        ) }
    private val mOperationAdapter by lazy { EndDrawerOperationAdapter() }
    private val mDrawerEndFileList by lazy { ArrayList<EndFileItem>() }
    private val mDrawerEndFileAdapter by lazy { EndDrawerFileAdapter() }
    private val mDrawerStartAdapter by lazy { StartDrawerAdapter() }
    private val mDrawerStartItemList by lazy {
        ArrayList<StartActionBaseItem>().apply {
            add(StartActionCatalogItem(R.drawable.ic_file_delete, "删除", true))
            add(StartActionSimpleItem(R.drawable.ic_file_setting, "退出", StartActionBaseItem.ID_EXIT))
            add(StartActionSimpleItem(R.drawable.ic_file_info, "退出", StartActionBaseItem.ID_EXIT))
            add(StartActionSimpleItem(R.drawable.ic_file_exit, "退出", StartActionBaseItem.ID_EXIT))
        }
    }
    private var isShowFloatingActionButton = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFloatActionButton()
        initRightDrawer()
        initLeftDrawer()
        initFrameLayout()
    }

    private fun initFloatActionButton(){
        mainFloatActionButton.hide()
        mainFloatActionButton.setOnClickListener {
            isShowFloatingActionButton = false
            onClickFloatActionButton()
        }
    }
    private fun initRightDrawer(){
        // 初始化操作列表部分
        mainEndDrawerOperationDrawerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mainEndDrawerOperationDrawerView.adapter = mOperationAdapter
        // 初始化文件列表
        mainEndDrawerFileRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true).apply {
            stackFromEnd = true
        }
        mainEndDrawerFileRecyclerView.adapter = mDrawerEndFileAdapter
    }
    private fun initLeftDrawer(){
        // 初始化页面标签
        mainStartDrawerList.layoutManager = LinearLayoutManager(this)
        mainStartDrawerList.adapter = mDrawerStartAdapter
    }
    private fun initFrameLayout(){
        supportFragmentManager.beginTransaction().add(mainFrameLayout.id, fragment).commitNow()
    }

    override fun onBackPressed() {
        if (!fragment.onBackPressed()){
            super.onBackPressed()
        }
    }

    /**
     * 供Fragment调用的
     */
    fun fragmentCopyFile(filePathArray:ArrayList<String>){
        filePathArray.forEach { fragmentAddFile(EndFileItem.OPERATION_COPY, it) }
        fragmentAfterAddFile()
    }
    fun fragmentCutFile(filePathArray:ArrayList<String>){
        filePathArray.forEach { fragmentAddFile(EndFileItem.OPERATION_CUT, it) }
        fragmentAfterAddFile()

    }
    private fun fragmentAddFile(operation:Int, filePath:String){
        val tmpFile = File(filePath)
        if (!tmpFile.exists()){
            return
        }
        val tmpItem = EndFileItem(tmpFile.path, tmpFile.name, operation, tmpFile.getTypeIconID())
        // 如果重复，则替换
        var tmpPos = -1
        mDrawerEndFileList.forEachIndexed { index, endFileItem ->
            if (endFileItem.path == tmpItem.path){
                tmpPos = index
                return@forEachIndexed
            }
        }
        // 添加数据并刷新界面
        if (tmpPos >= 0){
            mDrawerEndFileList[tmpPos] = tmpItem
            mDrawerEndFileAdapter.notifyItemChanged(tmpPos)
        }else{
            mDrawerEndFileList.add(tmpItem)
            mDrawerEndFileAdapter.notifyItemInserted(mDrawerEndFileList.size-1)
        }
    }
    private fun fragmentAfterAddFile(){
        mainFloatActionButton.show()
    }

    /**
     *  一系列操作
     */
    fun onClickFloatActionButton(){
        mDrawerEndFileList.forEach { item ->
            val newPath = fragment.getCurrentPath()
            val file = File(item.path)
            when(item.operation){
                EndFileItem.OPERATION_DELETE -> {
                    file.delete()
                }
                EndFileItem.OPERATION_CUT -> {
                    val newFile = File("$newPath/${item.name}")
                    if (!newFile.exists()){
                        file.renameTo(newFile)
                    }else{
                        // 移动失败，文件已存在
                    }
                }
                EndFileItem.OPERATION_COPY -> {
                    val newFile = File("$newPath/${item.name}")
                    if (!newFile.exists()){
                        file.copyTo(newFile)
                    }else{
                        // 复制失败，文件已存在
                    }
                }
            }
        }
        // 弹出提示
        toast(getString(R.string.toastMainOperationFinish))
        // 更新显示
        mDrawerEndFileList.clear()
        mDrawerEndFileAdapter.notifyDataSetChanged()
        // 隐藏按钮
        mainFloatActionButton.hide()
    }

    /**
     *  右边Drawer相关
     */
    class EndOperationItem(val icon:Int, val name:String, val id:Int= UNDEFINE){
        companion object {
            const val UNDEFINE = -1
            const val PASTE_ALL = 0
            const val CUT_ALL = 1
            const val DELETE_ALL = 2
            const val CLEAR_ALL = 3
        }
    }
    class EndFileItem(val path:String, val name:String, var operation:Int, val iconID:Int){
        companion object {
            const val OPERATION_CUT = 1
            const val OPERATION_COPY = 0
            const val OPERATION_DELETE = 2
        }
    }
    inner class EndDrawerOperationAdapter : RecyclerView.Adapter<EndDrawerOperationViewHolder>(){
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int):EndDrawerOperationViewHolder {
            return EndDrawerOperationViewHolder(p0)
        }

        override fun getItemCount(): Int {
            return mOperationActionList.size
        }

        override fun onBindViewHolder(holder: EndDrawerOperationViewHolder, pos: Int) {
            val tmpItem = mOperationActionList[pos]
            holder.setItem(tmpItem.icon, tmpItem.name)
            holder.itemView.setOnClickListener{
                when(tmpItem.id){
                    EndOperationItem.CLEAR_ALL -> {
                        mDrawerEndFileList.clear()
                        onClickFloatActionButton()
                    }
                    EndOperationItem.PASTE_ALL -> {
                        mDrawerEndFileList.forEach {
                            it.operation = EndFileItem.OPERATION_COPY
                        }
                        onClickFloatActionButton()
                    }
                    EndOperationItem.CUT_ALL -> {
                        mDrawerEndFileList.forEach {
                            it.operation = EndFileItem.OPERATION_CUT
                        }
                        onClickFloatActionButton()
                    }
                    EndOperationItem.DELETE_ALL -> {
                        mMessageDialog.setTitle(getString(R.string.textMainDialogTitleDeleteFiles))
                        mMessageDialog.setMessage(getString(R.string.textMainDialogMessageDeleteFiles))
                        mMessageDialog.setPosButton(getString(R.string.baseDialogPositiveButtonText), object : SimpleDialog.OnDialogButtonClickListener{
                            override fun onButtonClick(dialog: SimpleDialog) {
                                mDrawerEndFileList.forEach {
                                    it.operation = EndFileItem.OPERATION_DELETE
                                }
                                onClickFloatActionButton()
                                dialog.dismiss()
                            }
                        })
                        mMessageDialog.setNegButton(getString(R.string.baseDialogNegativeButtonText), object : SimpleDialog.OnDialogButtonClickListener{
                            override fun onButtonClick(dialog: SimpleDialog) {
                                dialog.dismiss()
                            }
                        })
                        mMessageDialog.show()
                    }
                }
                mainDrawerLayout.closeDrawer(Gravity.END)
            }
        }
    }
    inner class EndDrawerOperationViewHolder(parent:ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_main_end_drawer_operation_list,parent,false)){
        private val imageIcon = itemView.findViewById<ImageView>(R.id.itemMainEndDrawerOperationIcon)
        private val nameText = itemView.findViewById<TextView>(R.id.itemMainEndDrawerOperationName)

        fun setItem(icon:Int, name:String){
            imageIcon.setImageResource(icon)
            nameText.text = name
        }
    }
    inner class EndDrawerFileViewHolder(parent: ViewGroup):RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_main_end_drawer_file,parent,false)){
        val imageIcon = itemView.findViewById<ImageView>(R.id.itemMainEndDrawerFileIcon)
        val textTitle = itemView.findViewById<TextView>(R.id.itemMainEndDrawerFileTitle)
        val textOperation = itemView.findViewById<TextView>(R.id.itemMainEndDrawerFileOperation)
        val textPath = itemView.findViewById<TextView>(R.id.itemMainEndDrawerFilePath)

        fun initView(item:EndFileItem){
            imageIcon.setImageResource(item.iconID)
            textTitle.text = item.name
            textPath.text = item.path
            textOperation.text = when(item.operation){
                EndFileItem.OPERATION_COPY -> {
                    getString(R.string.textMainEndDrawerFileOperationCopy)
                }
                EndFileItem.OPERATION_CUT -> {
                    getString(R.string.textMainEndDrawerFileOperationCut)
                }
                else -> {
                    getString(R.string.textMainEndDrawerFileOperationCopy)
                }
            }
        }
    }
    inner class EndDrawerFileAdapter : RecyclerView.Adapter<EndDrawerFileViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, p1: Int): EndDrawerFileViewHolder {
            return EndDrawerFileViewHolder(parent)
        }

        override fun getItemCount(): Int {
            return mDrawerEndFileList.size
        }

        override fun onBindViewHolder(holder: EndDrawerFileViewHolder, pos: Int) {
            val item = mDrawerEndFileList[pos]
            holder.initView(item)
        }

    }

    /**
     *  左边Drawer相关
     */
    abstract class StartActionBaseItem(val name:String, val id:Int){
        companion object {
            const val ID_UNDEFINE = 0
            const val ID_EXIT = 1
        }
    }
    class StartActionCatalogItem(val icon:Int,name:String, var state:Boolean, id:Int=StartActionBaseItem.ID_UNDEFINE):StartActionBaseItem(name, id)
    class StartActionSimpleItem(val icon:Int, name:String, id:Int=StartActionBaseItem.ID_UNDEFINE):StartActionBaseItem(name, id)

    inner class StartDrawerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        val TYPE_SIMPLE = 0
        val TYPE_CATELOG = 1
        val TYPE_UNDEFINE = -1

        override fun getItemViewType(position: Int): Int {
            return when(mDrawerStartItemList[position]){
                is StartActionCatalogItem -> TYPE_CATELOG
                is StartActionSimpleItem -> TYPE_SIMPLE
                else -> TYPE_UNDEFINE
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
            return when(type){
                TYPE_SIMPLE -> StartDrawerSimpleItemViewHolder(parent)
                TYPE_CATELOG -> StartDrawerCatalogViewHolder(parent)
                else -> { StartDrawerCatalogViewHolder(parent) }
            }
        }

        override fun getItemCount(): Int {
            return mDrawerStartItemList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
            val tmpItem = mDrawerStartItemList[pos]
            when(holder){
                is StartDrawerCatalogViewHolder -> {
                    val item = tmpItem as StartActionCatalogItem
                    holder.setState(item.icon, item.name, item.state)
                }
                is StartDrawerSimpleItemViewHolder -> {
                    val item = tmpItem as StartActionSimpleItem
                    holder.initView(item)
                }
            }
            holder.itemView.setOnClickListener {
                when(tmpItem.id){
                    ID_EXIT -> {
                        FileApplication.getInstance().exit()
                    }
                }
            }
        }

    }
    inner class StartDrawerCatalogViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_main_start_drawer_catalog,parent,false)){
        private val imageIcon = itemView.findViewById<ImageView>(R.id.itemMainStartDrawerCatalogTypeIcon)
        private val textName = itemView.findViewById<TextView>(R.id.itemMainStartDrawerCatalogName)
        private val imageState = itemView.findViewById<ImageView>(R.id.itemMainStartDrawerCatalogStateIcon)

        fun setState(iconImage:Int, name:String, isOpen:Boolean){
            imageIcon.setImageResource(iconImage)
            textName.text = name
            imageState.rotation = if (isOpen){
                0f
            }else{
                270f
            }
        }
    }
    inner class StartDrawerCatalogItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_main_start_drawer_catalog_item,parent,false)){
        val textName = itemView as TextView

        fun setText(text:String){
            textName.text = text
        }
    }
    inner class StartDrawerCatalogSwitchViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_main_start_drawer_catalog_switch, parent, false)){
        val imageIcon = itemView.findViewById<ImageView>(R.id.itemMainStartDrawerItemTypeIcon)
        val nameText = itemView.findViewById<TextView>(R.id.itemMainStartDrawerItemName)
        val stateCheckBox = itemView.findViewById<CheckBox>(R.id.itemMainStartDrawerItemCheckbox)

    }
    inner class StartDrawerSimpleItemViewHolder(parent:ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_main_start_drawer_simple, parent, false)){
        val imageIcon = itemView.findViewById<ImageView>(R.id.itemMainStartDrawerSimpleIcon)
        val textName = itemView.findViewById<TextView>(R.id.itemMainStartDrawerSimpleName)

        fun initView(item:StartActionSimpleItem){
            imageIcon.setImageResource(item.icon)
            textName.text = item.name
        }
    }

}

