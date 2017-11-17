package creepersan.com.cfilemanager.callback

import android.app.Dialog
import creepersan.com.cfilemanager.views.holder.SimpleDialogViewHolder

/** 简单的对话框的回调对象
 * Created by CreeperSan on 2017/11/10.
 */
interface DialogButtonCallback {

    fun onClick(dialog:Dialog,dialogViewHolder: SimpleDialogViewHolder)

}