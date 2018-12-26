package com.creepersan.file.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.creepersan.file.FileApplication
import com.creepersan.file.R
import com.creepersan.file.activity.MainActivity
import com.creepersan.file.bean.FileItem
import com.creepersan.file.utils.*
import com.creepersan.file.view.SimpleDialog
import kotlinx.android.synthetic.main.fragment_file.*
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class FileFragment : BaseMainActivityFragment(), Toolbar.OnMenuItemClickListener {

    companion object {
        private const val ID_MORE_ACTION_OPEN = 0
        private const val ID_MORE_ACTION_CUT = 1
        private const val ID_MORE_ACTION_COPY = 2
        private const val ID_MORE_ACTION_APPEND_OPERATION_LIST = 3
        private const val ID_MORE_ACTION_RENAME = 6
        private const val ID_MORE_ACTION_INFO = 4
        private const val ID_MORE_ACTION_DELETE = 5
    }

    override val mLayoutID: Int = R.layout.fragment_file
    private val mFileAdapter by lazy { FileAdapter() }
    private val mPathAdapter by lazy { PathAdapter() }
    private val mFileStack by lazy { LinkedList<FileStackInfo>() }
    private val mCreateFileDialog by lazy {
        val dialogView = getView(R.layout.dialog_file_fragment_new)
        val hintText = dialogView.findViewById<TextView>(R.id.dialogFileFragmentHintText)
        val titleEditText = dialogView.findViewById<EditText>(R.id.dialogFileFragmentNewName)
        val typeRadioGroup = dialogView.findViewById<RadioGroup>(R.id.dialogFileFragmentRadioGroup)
        val typeFileRadioButton = dialogView.findViewById<RadioButton>(R.id.dialogFileFragmentRadioButtonFile)
        val typeFolderRadioButton = dialogView.findViewById<RadioButton>(R.id.dialogFileFragmentRadioButtonFolder)

        val dialog = AlertDialog.Builder(mActivity).setView(dialogView)
            .setTitle(R.string.dialogFileFragmentNewTitle)
            .setPositiveButton(R.string.dialogFileFragmentNewPosText) { _, _ ->
                val fileName = titleEditText.text.toString()
                // 防止文件名称重复
                if (fileName == ""){
                    hintText.visibility = View.VISIBLE
                    hintText.setText(R.string.dialogFileFragmentNewHintFileNameEmpty)
                    toast(getString(R.string.dialogFileFragmentNewHintFileNameEmpty))
                    return@setPositiveButton
                }
                // 防止文件名称已经存在
                mFileStack.last.mFileList.forEach {  tmpFileItem ->
                    if (tmpFileItem.name == fileName){
                        hintText.visibility = View.VISIBLE
                        hintText.setText(R.string.dialogFileFragmentNewHintFileNameExist)
                        toast(getString(R.string.dialogFileFragmentNewHintFileNameExist))
                        return@setPositiveButton
                    }
                }
                // 判断创建的是文件还是文件夹
                val prefix = mFileStack.last.mPath
                val tmpFile = File("$prefix/$fileName")
                try {
                    when(typeRadioGroup.checkedRadioButtonId){
                        typeFileRadioButton.id -> {
                            tmpFile.createNewFile()
                        }
                        typeFolderRadioButton.id -> {
                            tmpFile.mkdir()
                        }
                    }
                }catch (e:IOException){
                    toast(getString(R.string.dialogFileFragmentNewHintFileNameIllegal))
                }
                toast(getString(R.string.dialogFileFragmentNewHintFileCreateSuccess))
            }
            .setNegativeButton(R.string.dialogFileFragmentNewNegText, null)
            .create()
        dialog.setOnShowListener {
            hintText.visibility = View.GONE
            titleEditText.setText("")
            typeRadioGroup.check(typeFileRadioButton.id)
        }
        dialog
    }
    private var mFileMoreDialogTmpFilePathArrayList = ArrayList<String>() // 更多对话框弹出时选择的文件
    private val mFileMoreDialog by lazy {
        val dialog = SimpleDialog(context!!, SimpleDialog.DIRECTION_BOTTOM, SimpleDialog.TYPE_LIST)
            .setItems(arrayListOf(
                SimpleDialog.DialogListItem(getString(R.string.dialogFileFragmentFileOperationOpen), R.drawable.ic_file_open, ID_MORE_ACTION_OPEN),
                SimpleDialog.DialogListItem(getString(R.string.dialogFileFragmentFileOperationCut), R.drawable.ic_file_cut, ID_MORE_ACTION_CUT),
                SimpleDialog.DialogListItem(getString(R.string.dialogFileFragmentFileOperationCopy), R.drawable.ic_file_copy, ID_MORE_ACTION_COPY),
                SimpleDialog.DialogListItem(getString(R.string.dialogFileFragmentFileOperationOperationListAppend), R.drawable.ic_file_operation_list_add, ID_MORE_ACTION_APPEND_OPERATION_LIST),
                SimpleDialog.DialogListItem(getString(R.string.dialogFileFragmentFileOperationRename), R.drawable.ic_file_rename, ID_MORE_ACTION_RENAME),
                SimpleDialog.DialogListItem(getString(R.string.dialogFileFragmentFileOperationInfo), R.drawable.ic_file_info, ID_MORE_ACTION_INFO),
                SimpleDialog.DialogListItem(getString(R.string.dialogFileFragmentFileOperationDelete), R.drawable.ic_file_delete, ID_MORE_ACTION_DELETE)
            ),object : SimpleDialog.OnDialogListItemClickListener{
                override fun onItemClick(dialog: SimpleDialog, item: SimpleDialog.DialogListItem, pos: Int) {
                    when(item.id){
                        ID_MORE_ACTION_OPEN -> {
                            openFile()
                        }
                        ID_MORE_ACTION_CUT -> {
                            cutFile()
                        }
                        ID_MORE_ACTION_COPY -> {
                            copyFile()
                        }
                        ID_MORE_ACTION_APPEND_OPERATION_LIST -> {

                        }
                        ID_MORE_ACTION_RENAME -> {
                            renameFile()
                        }
                        ID_MORE_ACTION_INFO -> {
                            infoFile()
                        }
                        ID_MORE_ACTION_DELETE -> {
                            deleteFile()
                        }
                    }
                    dialog.dismiss()
                }
            })
        dialog
    }
    private val mMessageDialog by lazy {
        SimpleDialog(context!!, SimpleDialog.DIRECTION_CENTER, SimpleDialog.TYPE_MESSAGE)
    }
    private val mInfoDialog by lazy {
        val dialog = object : SimpleDialog(context!!, SimpleDialog.DIRECTION_CENTER, SimpleDialog.TYPE_CUSTOM_VIEW){
            lateinit var textFileName : TextView
            lateinit var imageFileIcon : ImageView
            lateinit var textFileType : TextView
            lateinit var textFilePath : TextView
            lateinit var textFileSize : TextView
            lateinit var textModifyTime : TextView
            lateinit var textReadable : TextView
            lateinit var textWritable : TextView
            lateinit var textExecutable : TextView
            lateinit var textHidden : TextView

            override fun initCustomView(customView: View) {
                textFileName = customView.findViewById(R.id.dialogBaseFileInfoTitle)
                imageFileIcon = customView.findViewById(R.id.dialogBaseFileInfoIcon)
                textFileType = customView.findViewById(R.id.dialogBaseFileInfoFileType)
                textFilePath = customView.findViewById(R.id.dialogBaseFileInfoFilePath)
                textFileSize = customView.findViewById(R.id.dialogBaseFileInfoFileSize)
                textModifyTime = customView.findViewById(R.id.dialogBaseFileInfoModifyTime)
                textReadable = customView.findViewById(R.id.dialogBaseFileInfoReadable)
                textWritable = customView.findViewById(R.id.dialogBaseFileInfoWritable)
                textExecutable = customView.findViewById(R.id.dialogBaseFileInfoExecutable)
                textHidden = customView.findViewById(R.id.dialogBaseFileInfoHidden)
            }

            fun setFile(file:File){
                textFileName.text = file.name
                imageFileIcon.setImageResource(file.getTypeIconID())
                textFileType.text = file.getTypeName(context)
                textFilePath.text = file.path
                textFileSize.text = String.format(getString(R.string.dialogFileFragmentInfoFileSize), file.getFileSize(context), file.getFileSizeByte(context))
                textModifyTime.text = file.getFileModifyTimeString()
                textReadable.text = file.isReadableString(context)
                textWritable.text = file.isWritableString(context)
                textExecutable.text = file.isExecutableString(context)
                textHidden.text = file.isHiddenString(context)
            }


        }
        dialog.setCustomView(layoutInflater.inflate(R.layout.dialog_base_custom_view_file_info, dialog.viewCustomViewGroup, false))
            .setTitle(getString(R.string.dialogFileFragmentFileOperationInfo))
            .setPosButton(getString(R.string.baseDialogPositiveButtonText), object : SimpleDialog.OnDialogButtonClickListener{
                override fun onButtonClick(dialog: SimpleDialog) {
                dialog.dismiss()
                }
            })
        dialog
    }
    private val mRenameDialog by lazy {
        val dialog = object : SimpleDialog(context!!, SimpleDialog.DIRECTION_CENTER, SimpleDialog.TYPE_CUSTOM_VIEW){
            lateinit var editTextFileName : EditText
            lateinit var imageClear : ImageView

            override fun initCustomView(customView: View) {
                editTextFileName = customView.findViewById(R.id.dialogBaseFileRenameFileName)
                imageClear = customView.findViewById(R.id.dialogBaseFileRenameClear)

                imageClear.setOnClickListener {
                    clearText()
                }
            }

            fun getNewName():String{
                return editTextFileName.text.toString().trim()
            }

            fun setName(fileName:String){
                editTextFileName.setText(fileName)
            }

            fun clearText(){
                editTextFileName.setText("")
            }

            override fun show() {
                super.show()
                if (mFileMoreDialogTmpFilePathArrayList.size > 1){
                    clearText()
                }
            }

        }
        dialog.setCustomView(layoutInflater.inflate(R.layout.dialog_base_main_rename, dialog.viewCustomViewGroup, false))
        dialog
    }

    private var mTmpFileRecyclerViewScrollPos = 0
    private var isMultiChoosing = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initStack()
        initPathRecyclerView()
        initFileRecyclerView()
    }

    private fun initToolbar(){
        fragmentFileActionBarToolbar.inflateMenu(R.menu.fragment_file)
        fragmentFileActionBarToolbar.setOnMenuItemClickListener(this)
        stopMultiChoosing()
    }
    private fun initStack(){
        mFileStack.push(FileStackInfo.fromExternalStorage())
    }
    private fun initPathRecyclerView(){
        fragmentFilePathRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        fragmentFilePathRecyclerView.adapter = mPathAdapter

    }
    private fun initFileRecyclerView(){
        fragmentFileFileRecyclerView.layoutManager = LinearLayoutManager(activity)
        fragmentFileFileRecyclerView.adapter = mFileAdapter
    }

    /**
     *  操作统一入口
     */
    // 用于文件目录堆栈返回是刷新用的，已经是2个不同的文件夹
    fun updatePathRecyclerView(){
        mPathAdapter.notifyDataSetChanged()
        fragmentFilePathRecyclerView.scrollToPosition(mFileStack.size-1)
    }
    // 用于文件目录堆栈返回是刷新用的，已经是2个不同的文件夹
    fun updateFileRecyclerView(){
        mFileAdapter.notifyDataSetChanged()

        val layoutManager = fragmentFileFileRecyclerView.layoutManager

        when(layoutManager){
            is LinearLayoutManager -> {
                layoutManager.scrollToPositionWithOffset(mFileStack.last.mScrollYAxisPosition,0)
            }
        }

    }
    // 用于当前所在的文件发生变化用的，还是同一个文件夹
    fun refreshFileRecyclerView(){
        isMultiChoosing = false

        val tmpPath = mFileStack.last.mPath
        mFileStack.removeLast()
        mFileStack.addLast(FileStackInfo.fromPath(tmpPath))
        mFileAdapter.notifyDataSetChanged()
        mPathAdapter.notifyDataSetChanged()
    }
    // 打开文件
    fun openFile(){
        toast("打开文件 ${mFileMoreDialogTmpFilePathArrayList.toString()}")
        mFileMoreDialogTmpFilePathArrayList.clear()
    }
    fun deleteFile(){
        // 防止操作的文件为空
        if (mFileMoreDialogTmpFilePathArrayList.size == 0){
            toast(getString(R.string.toastFileFragmentFileNoFileSelected))
            return
        }
        // 整理对话框提示消息
        if (mFileMoreDialogTmpFilePathArrayList.size == 1){
            val tmpFile = File(mFileMoreDialogTmpFilePathArrayList[0])
            if (tmpFile.isDirectory){
                mMessageDialog.setTitle(getString(R.string.dialogFileFragmentFileDeleteFolderTitle))
            }else{
                mMessageDialog.setTitle(getString(R.string.dialogFileFragmentFileDeleteFileTitle))
            }
            mMessageDialog.setMessage(String.format(getString(R.string.dialogFileFragmentFileDeleteConfirmMessage), tmpFile.name))
        }else{
            mMessageDialog.setTitle(getString(R.string.dialogFileFragmentFileDeleteFilesTitle))
            mMessageDialog.setMessage(getString(R.string.dialogFileFragmentFilesDeleteConfirmMessage))
        }
        // 设置点击的按钮事件
        mMessageDialog.setPosButton(getString(R.string.baseDialogPositiveButtonText), object : SimpleDialog.OnDialogButtonClickListener{
            override fun onButtonClick(dialog: SimpleDialog) {
                // 检查里面是否存在不存在的文件
                val fileList = ArrayList<File>()
                var isHasFileMissed = false
                var isHasFileExist = false
                mFileMoreDialogTmpFilePathArrayList.forEach {
                    val tmpFile = File(it)
                    if (tmpFile.exists()){
                        fileList.add(tmpFile)
                        isHasFileExist = true
                    }else{
                        isHasFileMissed = true
                    }
                }
                if (!isHasFileExist){
                    toast(getString(R.string.toastFileFragmentFileNotExist))
                    return
                }
                // 删除文件
                var isAllDeleteSuccess = true
                var isAllFail = true
                fileList.forEach { tmpFile ->
                    if (tmpFile.delete()){ // 删除失败
                        isAllFail = false
                    }else{ // 删除成功
                        isAllDeleteSuccess = false
                    }
                }
                // 弹出提示
                if (isAllFail){ // 删除文件全都失败
                    if (isHasFileMissed){ // 有文件丢失
                        toast(getString(R.string.toastFileFragmentDeleteFailSuccessFileMissed))
                    }else{ // 没有文件丢失
                        toast(getString(R.string.toastFileFragmentDeleteFailSuccessFileExist))
                    }
                }else if (isAllDeleteSuccess){ // 删除文件部分失败
                    if (isHasFileMissed){ // 有文件丢失
                        toast(getString(R.string.toastFileFragmentDeleteSuccessFileMissed))
                    }else{ // 没有文件丢失
                        toast(getString(R.string.toastFileFragmentDeleteSuccessFileExist))
                    }
                }else{ // 删除文件全部成功
                    if (isHasFileMissed){ // 有文件丢失
                        toast(getString(R.string.toastFileFragmentDeletePartlySuccessFileMissed))
                    }else{ // 没有文件丢失
                        toast(getString(R.string.toastFileFragmentDeletePartlySuccessFileExist))
                    }
                }
                // 隐藏对话框
                dialog.dismiss()
            }
        })
        mMessageDialog.setNegButton(getString(R.string.baseDialogNegativeButtonText), object : SimpleDialog.OnDialogButtonClickListener{
            override fun onButtonClick(dialog: SimpleDialog) {
                dialog.dismiss()
            }
        })
        // 显示对话框
        mMessageDialog.show()
        // 退出多选模式
        mMessageDialog.setOnDismissListener {
            stopMultiChoosing()
            mMessageDialog.setOnDismissListener(null)
        }
    }
    fun infoFile(){
        if (mFileMoreDialogTmpFilePathArrayList.size < 1){
            toast(getString(R.string.toastFileFragmentFileNoFileSelected))
            return
        }
        val file = File(mFileMoreDialogTmpFilePathArrayList[0])
        if (!file.exists()){
            toast(getString(R.string.toastFileFragmentFileNotExist))
            return
        }
        mInfoDialog.setFile(file)
        mInfoDialog.show()
    }
    fun copyFile(){
        stopMultiChoosing()
    }
    fun copyAppendFile(){
        stopMultiChoosing()
    }
    fun cutFile(){

    }
    fun renameFile(){
        val size = mFileMoreDialogTmpFilePathArrayList.size
        if (size <= 0){ // 没有选择文件
            toast(getString(R.string.toastFileFragmentFileNoFileSelected))
            return
        }else if (size == 1){ // 选择了1个文件
            mRenameDialog.setTitle(getString(R.string.titleDialogFileFileFragmentRenameFileText))
            val filePath = mFileMoreDialogTmpFilePathArrayList[0]
            if (filePath.contains("/")){
                mRenameDialog.setName(filePath.substring(filePath.lastIndexOf("/")+1))
            }
        }else{ // 选择了多个文件
            mRenameDialog.setTitle(getString(R.string.titleDialogFileFileFragmentRenameFilesText))
        }
        mRenameDialog
            .setPosButton(getString(R.string.baseDialogPositiveButtonText),object : SimpleDialog.OnDialogButtonClickListener{
                override fun onButtonClick(dialog: SimpleDialog) {
                    val nameFile = mRenameDialog.getNewName().trim()
                    val isMultiFile = mFileMoreDialogTmpFilePathArrayList.size > 1
                    // 检查是否为空
                    if(nameFile == ""){
                        toast(getString(R.string.hintDialogFileFragmentRenameNameEmpty))
                        return
                    }
                    // 检查文件名称是否存在
                    mFileStack.last.mFileList.forEach {  item ->
                        if (item.name.trim() == nameFile){
                            toast(getString(R.string.hintDialogFileFileFragmentRenameFilesFileNameExist))
                            return
                        }
                    }
                    // 重命名
                    var tmpIndex = 1
                    mFileMoreDialogTmpFilePathArrayList.forEach {filePath ->
                        val tmpFile = File(filePath)
                        if (tmpFile.exists()){
                            // 计算后缀
                            var numStr = ""
                            var postFix = ""
                            if(filePath.contains(".") && mFileMoreDialogTmpFilePathArrayList.size > 1){
                                postFix = filePath.substring(filePath.lastIndexOf("."))
                            }
                            // 计算索引序号
                            if (isMultiFile){
                                numStr = String.format("%${mFileMoreDialogTmpFilePathArrayList.size.toString().length}d", tmpIndex).replace(" ","0")
                                tmpIndex += 1
                            }
                            tmpFile.renameTo(File("${tmpFile.parentFile.absolutePath}/$nameFile$numStr$postFix"))
                        }
                    }
                    dialog.dismiss()
                }
            })
            .setNegButton(getString(R.string.baseDialogNegativeButtonText),object : SimpleDialog.OnDialogButtonClickListener{
                override fun onButtonClick(dialog: SimpleDialog) {
                    dialog.dismiss()
                }
            })
            .setOnDismissListener {
                stopMultiChoosing()
                mRenameDialog.setOnDismissListener(null)
            }
        mRenameDialog.show()
    }
    fun selectAllFile(){
        mFileMoreDialogTmpFilePathArrayList.clear()
        mFileStack.last.mFileList.forEach { tmpFileItem ->
            mFileMoreDialogTmpFilePathArrayList.add(tmpFileItem.path)
        }
        updateFileRecyclerView()
        refreshToolbarChoosingTitle()
    }
    // 状态改变
    fun startMultiChoosing(){
        // 改变标志位
        isMultiChoosing = true
        // 改变菜单
        fragmentFileActionBarToolbar.menu.clear()
        fragmentFileActionBarToolbar.inflateMenu(R.menu.fragment_file_choosing)
        // 设置返回按钮以及标题
        fragmentFileActionBarToolbar.setNavigationIcon(R.drawable.ic_file_right_arrow_white)
        fragmentFileActionBarToolbar.setNavigationOnClickListener {
            stopMultiChoosing()
        }
        // 清楚标志
        mFileMoreDialogTmpFilePathArrayList.clear()
        // 更新状态
        updateFileRecyclerView()
        // 更新标题
        refreshToolbarChoosingTitle()
    }
    fun stopMultiChoosing(){
        // 改变标志位
        isMultiChoosing = false
        // 改变菜单
        fragmentFileActionBarToolbar.menu.clear()
        fragmentFileActionBarToolbar.inflateMenu(R.menu.fragment_file)
        // 设置返回按钮以及标题
        fragmentFileActionBarToolbar.title = "文件"
        fragmentFileActionBarToolbar.navigationIcon = null
        // 刷新文件
        mFileAdapter.notifyDataSetChanged()
        // 清楚标志
        mFileMoreDialogTmpFilePathArrayList.clear()
    }
    fun refreshToolbarChoosingTitle(){
        fragmentFileActionBarToolbar.title = "${mFileMoreDialogTmpFilePathArrayList.size}/${mFileStack.last.mFileList.size}"
    }
    //



    /**
     *  事件回调
     */
    fun onBackPressed():Boolean{
        return when {
            isMultiChoosing -> {
                stopMultiChoosing()
                true
            }
            mFileStack.size > 1 -> {
                mFileStack.removeLast()
                updatePathRecyclerView()
                updateFileRecyclerView()
                true
            }
            else -> false
        }
    }
    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        when(p0?.itemId){
            // 以下是在平常状态下
            R.id.menuFileFragmentNew -> {
                mCreateFileDialog.show()
            }
            R.id.menuFileFragmentChoose -> {
                if(isMultiChoosing){
                    stopMultiChoosing()
                }else{
                    startMultiChoosing()
                }
            }
            R.id.menuFileFragmentRefresh -> {
                refreshFileRecyclerView()
            }
            R.id.menuFileFragmentSearch -> {
                SimpleDialog(context!!, SimpleDialog.DIRECTION_CENTER,SimpleDialog.TYPE_MESSAGE)
                    .setTitle("操作文件")
                    .setPosButton("好的", object : SimpleDialog.OnDialogButtonClickListener {
                        override fun onButtonClick(dialog: SimpleDialog) {
                            toast("好的")
                            dialog.dismiss()
                        }
                    })
                    .setNegButton("不好", object : SimpleDialog.OnDialogButtonClickListener {
                        override fun onButtonClick(dialog: SimpleDialog) {
                            toast("不好")
                        }
                    })
//                    .setItems(arrayListOf(
//                        SimpleDialog.DialogListItem("编辑", R.drawable.ic_file_selected),
//                        SimpleDialog.DialogListItem("剪切", R.drawable.ic_file_selected),
//                        SimpleDialog.DialogListItem("复制", R.drawable.ic_file_selected),
//                        SimpleDialog.DialogListItem("添加到复制", R.drawable.ic_file_selected),
//                        SimpleDialog.DialogListItem("删除", R.drawable.ic_file_selected),
//                        SimpleDialog.DialogListItem("关于", R.drawable.ic_file_selected)
//                    ), object : SimpleDialog.OnDialogListItemClickListener {
//                        override fun onItemClick(item: SimpleDialog.DialogListItem, pos: Int) {
//                            toast(item.title)
//                        }
//                    })
                    .setMessage("这还是一条点单的消息")
                    .show()
            }
            // 以下是在选择文件状态下
            R.id.menuFileFragmentSelectAll -> {
                selectAllFile()
            }
            R.id.menuFileFragmentCopy -> {
                copyFile()
            }
            R.id.menuFileFragmentCut -> {
                cutFile()
            }
            R.id.menuFileFragmentDelete -> {
                deleteFile()
            }
            R.id.menuFileFragmentRename -> {
                renameFile()
            }
        }
        return true
    }


    /**
     *  列表用的Adapter 和 Holder 之类的内容
     */

    inner class FileAdapter : RecyclerView.Adapter<FileHolder>(){
        private fun chooseFile(path:String, pos: Int){
            if (mFileMoreDialogTmpFilePathArrayList.contains(path)){
                mFileMoreDialogTmpFilePathArrayList.remove(path)
            }else{
                mFileMoreDialogTmpFilePathArrayList.add(path)
            }
            notifyItemChanged(pos)
            refreshToolbarChoosingTitle()

        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FileHolder {
            return FileHolder(p0)
        }
        override fun getItemCount(): Int {
            return mFileStack.last.mFileList.size
        }
        override fun onBindViewHolder(holder: FileHolder, pos: Int) {
            val item = mFileStack.last.mFileList[pos]
            val layoutManager = fragmentFileFileRecyclerView.layoutManager

            holder.icon.setImageResource(item.icon)
            holder.title.text = item.name
            holder.more.setImageResource(R.drawable.ic_file_more)
            holder.contentExtra.text = item.modifyTime
            holder.content.text = item.size

            holder.setChoose(isMultiChoosing && mFileMoreDialogTmpFilePathArrayList.contains(item.path)) // TODO : 这里可以优化

            holder.more.setOnClickListener {
                // 添加文件
                mFileMoreDialogTmpFilePathArrayList.clear()
                mFileMoreDialogTmpFilePathArrayList.add(item.path)
                // 设置并弹出对话框
                mFileMoreDialog.setTitle(item.name)
                mFileMoreDialog.show()
            }
            holder.setMoreButtonVisiable(!isMultiChoosing)

            holder.itemView.setOnClickListener{
                if (isMultiChoosing){ // 正在多选
                    chooseFile(item.path, pos)
                }else{ // 没在多选
                    if(item.isFolder){
                        // 记录滚动距离
                        when(layoutManager){
                            is LinearLayoutManager -> {
                                mTmpFileRecyclerViewScrollPos = layoutManager.findFirstVisibleItemPosition()
                            }
                        }
                        mFileStack.last.mScrollYAxisPosition = mTmpFileRecyclerViewScrollPos
                        mTmpFileRecyclerViewScrollPos = 0
                        // 添加新的文件栈
                        mFileStack.addLast(FileStackInfo.fromFile(item.getFile()))
                    }else{
                        mFileMoreDialogTmpFilePathArrayList.clear()
                        mFileMoreDialogTmpFilePathArrayList.add(item.path)
                        openFile()
                    }
                    updatePathRecyclerView()
                    updateFileRecyclerView()
                }
            }
            holder.itemView.setOnLongClickListener{
                startMultiChoosing()
                chooseFile(item.path, pos)
                true
            }
        }
    }
    inner class FileHolder(container:ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_file_file, container, false)){
        val icon = itemView.findViewById<ImageView>(R.id.itemFileFileIcon)
        val more = itemView.findViewById<ImageView>(R.id.itemFileFileMore)
        val title = itemView.findViewById<TextView>(R.id.itemFileFileTitle)
        val content = itemView.findViewById<TextView>(R.id.itemFileFileContent)
        val contentExtra = itemView.findViewById<TextView>(R.id.itemFileFileContentExtra)

        private val DEFAULT_COLOR = Color.WHITE

        fun setChoose(state:Boolean){
            itemView.setBackgroundColor(if (state){
                context?.getColor(R.color.lightgray) ?: DEFAULT_COLOR
            }else{
                context?.getColor(R.color.white) ?: DEFAULT_COLOR
            })
        }
        fun setMoreButtonVisiable(isVisable:Boolean){
            if (isVisable){
                more.visibility = View.VISIBLE
                more.isEnabled = true
            }else{
                more.visibility = View.INVISIBLE
                more.isEnabled = false
            }
        }
    }
    inner class PathAdapter : RecyclerView.Adapter<PathHolder>(){
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PathHolder {
            return PathHolder(p0)
        }

        override fun getItemCount(): Int {
            return mFileStack.size
        }

        override fun onBindViewHolder(holder: PathHolder, pos: Int) {
            val item = mFileStack[pos]

            holder.icon.setImageResource(R.drawable.ic_file_path_diver)
            holder.title.text = item.mName

            holder.itemView.setOnClickListener {
                val tmpPos = holder.adapterPosition
                if (tmpPos+1 < mFileStack.size){
                    mFileStack.removeLast()
                }

                updatePathRecyclerView()
                updateFileRecyclerView()
            }

        }

    }
    inner class PathHolder(container: ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_file_path, container, false)){
        val icon = itemView.findViewById<ImageView>(R.id.itemFilePathIcon)
        val title = itemView.findViewById<TextView>(R.id.itemFilePathTitle)
    }

    /**
     * Item Bean
     */
    class FileStackInfo private constructor(){

        companion object {
            fun fromExternalStorage():FileStackInfo{
                return fromFile(Environment.getExternalStorageDirectory())
            }

            fun fromFile(file:File):FileStackInfo{
                val mStackInfo = FileStackInfo()
                // 初始化当前目录的基础信息
                mStackInfo.mName = file.name
                mStackInfo.mPath = file.path

                // 初始化当前目录下的文件信息
                val tmpFileList = ArrayList<File>()
                val tmpFolderList = ArrayList<File>()
                val tmpConfig = FileApplication.getConfigInstance()
                val isFolderFirst = tmpConfig.getFileIsFolderFirst()
                val isShowHiddenFile = tmpConfig.getFileIsShowHiddenFile()
                val sortType = tmpConfig.getFileSortOrder()
                val isReverse = tmpConfig.getFileIsOrderReverse()
                val tmpSort = file.listFiles().toMutableList()

                when(sortType){
                    ConfigUtil.VAL_FILE_SORT_TYPE -> {
                        if (isReverse){
                            tmpSort.sortByDescending { it.name }
                        }else{
                            tmpSort.sortBy { it.name }
                        }
                    }
                    ConfigUtil.VAL_FILE_SORT_SIZE -> {
                        if (isReverse){
                            tmpSort.sortByDescending { it.length() }
                        }else{
                            tmpSort.sortBy { it.length() }
                        }
                    }
                    ConfigUtil.VAL_FILE_SORT_MODIFY_TIME -> {
                        if (isReverse){
                            tmpSort.sortByDescending { it.lastModified() }
                        }else{
                            tmpSort.sortBy { it.lastModified() }
                        }
                    }
                    ConfigUtil.VAL_FILE_SORT_NAME -> {
                        if (isReverse){
                            tmpSort.sortByDescending { it.name }
                        }else{
                            tmpSort.sortBy { it.name }
                        }
                    }
                }
                tmpSort.forEach { tmpFile ->
                    if (tmpFile.isHidden && !isShowHiddenFile){
                        return@forEach // 如果是隐藏文件并且不显示隐藏文件的话就跳过文件的扫描
                    }
                    if (isFolderFirst){
                        if (tmpFile.isDirectory){
                            tmpFolderList.add(tmpFile)
                        }else{
                            tmpFileList.add(tmpFile)
                        }
                    }else{
                        tmpFileList.add(tmpFile)
                    }
                }

                // 添加数据
                tmpFolderList.forEach { mStackInfo.mFileList.add(FileItem.fromFile(it)) }
                tmpFileList.forEach { mStackInfo.mFileList.add(FileItem.fromFile(it)) }

                return mStackInfo
            }

            fun fromPath(path:String):FileStackInfo{
                return fromFile(File(path))
            }
        }

        var mFileList = ArrayList<FileItem>()
        var mScrollYAxisPosition = 0
        var mName = ""
        var mPath = ""


    }


}