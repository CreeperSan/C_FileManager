package com.creepersan.file.function.text

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.creepersan.file.FileApplication
import com.creepersan.file.R
import com.creepersan.file.activity.BaseActivity
import com.creepersan.file.utils.Logger
import com.creepersan.file.utils.sp2px
import com.creepersan.file.view.SimpleDialog
import kotlinx.android.synthetic.main.activity_text_viewer.*
import java.io.*
import java.lang.Exception
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException

class TextViewerActivity : BaseActivity(), Toolbar.OnMenuItemClickListener {

    companion object {
        private const val TAG = "文本浏览器"
    }

    override val mLayoutID: Int = R.layout.activity_text_viewer

    private var mFilePath = ""
    private lateinit var mFile : File
    private val mTextSize = sp2px(FileApplication.getInstance(), mConfig.textViewerGetTextSize().toFloat())
    private var mCoding = mConfig.textViewerGetTextCoding()
    private val mCodingDialog by lazy { SimpleDialog(this, SimpleDialog.DIRECTION_CENTER, SimpleDialog.TYPE_LIST).apply {
        setTitleID(R.string.settingDialogTextViewerDefaultCodingTitle)
        setItems(ArrayList<SimpleDialog.DialogListItem>().apply {
            add(SimpleDialog.DialogListItem(String.format(getString(R.string.settingDialogTextViewerDefaultCodingItemDefault), Charset.defaultCharset().name()), R.drawable.ic_android, 0))
            for(tmpCharset in Charset.availableCharsets()){
                val key = tmpCharset.key
                add(SimpleDialog.DialogListItem(key, R.drawable.ic_text_viewer_coding, 1))
            }
        }, object : SimpleDialog.OnDialogListItemClickListener {
            override fun onItemClick(dialog: SimpleDialog, item: SimpleDialog.DialogListItem, pos: Int) {
                when(item.id){
                    0 -> {
                        mCoding = mConfig.textViewerGetDefaultTextCoding()
                    }
                    1 -> {
                        mCoding = item.title
                    }
                }
                initData()
                dialog.dismiss()
            }
        })
    } }
    private var mPrevText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPath()
        initFile()
        initData()
        initToolbar()
        initEditText()
    }

    private fun initPath(){
        mFilePath = intent.data.path ?: ""
    }
    private fun initToolbar(){
        // 标题
        textViewerToolBar.title = mFile.name
        // 菜单
        textViewerToolBar.inflateMenu(R.menu.text_viewer)
        textViewerToolBar.setOnMenuItemClickListener(this)
        // 设置左上角图标
        textViewerToolBar.setNavigationIcon(R.drawable.ic_close_white)
        textViewerToolBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    private fun initFile(){
        mFile = File(mFilePath)
        if (!mFile.exists()){
            toast(R.string.textViewerFileNotExistHint)
            finish()
        }
    }
    private fun initData(){
        textViewerEditText.setText(String(readData()))
    }
    private fun initEditText(){
        textViewerEditText.textSize = mTextSize.toFloat()
    }



    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when(menuItem.itemId){
            R.id.menuTextViewerFileCoding -> {
                mCodingDialog.show()
            }
            R.id.menuTextViewerFileEdit -> {
                mPrevText = textViewerEditText.text.toString()
                setEditing(true)
            }
            R.id.menuTextViewerFileSave -> {
                val fileWriter = try {
                    OutputStreamWriter(FileOutputStream(mFile), Charset.forName(mCoding))
                }catch (e:UnsupportedCharsetException){
                    Logger.logE("写入文件 : 编码格式不支持",TAG)
                    toast(R.string.textViewerHintFileWriteFailUnsupportedCharset)
                    return true
                }
                fileWriter.write(textViewerEditText.text.toString())
                fileWriter.close()
                toast(R.string.textViewerHintFileWritten)
                setEditing(false)
            }
        }
        return true
    }
    override fun onBackPressed() {
        if (isEditing()){
            textViewerEditText.setText(mPrevText)
            mPrevText = ""
            setEditing(false)
        }else{
            finish()
        }
    }




    private fun readData():CharArray{
        val buffer = CharArray(mFile.length().toInt()) {0.toChar()}
        mFile.reader(Charset.forName(mCoding)).read(buffer, 0, buffer.size)
        return buffer
    }
    private fun setEditing(state:Boolean){
        textViewerToolBar.menu.clear()
        if (state){
            textViewerEditText.isEnabled = true
            textViewerToolBar.inflateMenu(R.menu.text_viewer_editing)
            textViewerToolBar.setNavigationIcon(R.drawable.ic_arrow_left_white)
        }else{
            textViewerEditText.isEnabled = false
            textViewerToolBar.inflateMenu(R.menu.text_viewer)
            textViewerToolBar.setNavigationIcon(R.drawable.ic_close_white)
        }
    }
    private fun isEditing():Boolean{
        return textViewerEditText.isEnabled
    }
}