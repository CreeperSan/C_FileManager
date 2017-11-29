package creepersan.com.cfilemanager.callback

import android.app.Dialog
import android.content.DialogInterface
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import creepersan.com.cfilemanager.R

/** 创建文件回掉函数
 * Created by CreeperSan on 2017/11/27.
 */
abstract class DialogCreateFileCallback{
    protected lateinit var dialogView: View
    protected lateinit var dialog : Dialog
    protected lateinit var title : TextView
    protected lateinit var editText : TextInputEditText
    protected lateinit var editTextLayout : TextInputLayout
    protected lateinit var folderCheckbox : RadioButton
    protected lateinit var fileCheckbox : RadioButton
    protected lateinit var txtCheckbox : RadioButton
    protected lateinit var markdowmCheckbox : RadioButton
    protected lateinit var cancel : TextView
    protected lateinit var commit : TextView

    fun init(dialogView:View,dialog:Dialog,dismissListener:DialogInterface.OnDismissListener? = null){
        this.dialogView = dialogView
        this.dialog = dialog
        title = dialogView.findViewById(R.id.dialogFileFragmentTitle)
        editText = dialogView.findViewById(R.id.dialogFileFragmentTextInput)
        editTextLayout = dialogView.findViewById(R.id.dialogFileFragmentTextInputLayout)
        folderCheckbox = dialogView.findViewById(R.id.dialogFileFragmentFolder)
        fileCheckbox = dialogView.findViewById(R.id.dialogFileFragmentFile)
        txtCheckbox = dialogView.findViewById(R.id.dialogFileFragmentTxt)
        markdowmCheckbox = dialogView.findViewById(R.id.dialogFileFragmentMarkdown)
        cancel = dialogView.findViewById(R.id.dialogFileFragmentCancel)
        commit = dialogView.findViewById(R.id.dialogFileFragmentCommit)
        commit.setOnClickListener {
            onCommit(editText.text.toString(),when{
                folderCheckbox.isChecked ->Selection.FOLDER
                fileCheckbox.isChecked ->Selection.FILE
                txtCheckbox.isChecked ->Selection.TXT
                markdowmCheckbox.isChecked ->Selection.MARKDOWN
                else -> Selection.NONE
            })
        }
        cancel.setOnClickListener {
            onCancel()
        }
        dialog.setOnDismissListener(dismissListener)
    }

    open fun onCommit(name:String,selection:Int){
        dialog.dismiss()
    }

    open fun onCancel(){
        dialog.dismiss()
    }

    /**
     *  选择上的后缀
     */
    object Selection{
        const val NONE = -1
        const val FOLDER = 0
        const val FILE = 1
        const val TXT = 2
        const val MARKDOWN = 3
    }

}