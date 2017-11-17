package creepersan.com.cfilemanager.views.component

import android.app.Dialog
import android.support.annotation.AnimRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import creepersan.com.cfilemanager.R
import creepersan.com.cfilemanager.base.BaseActivity
import creepersan.com.cfilemanager.callback.DialogButtonCallback

/** 对话框建造器
 * Created by CreeperSan on 2017/11/11.
 */
class SimpleDialogBuilder(var baseActivity : BaseActivity){
    private var icon = R.drawable.ic_android
    private var title = ""
    private var content = ""
    private var posButtonStr = ""
    private var posButtonCallBack : DialogButtonCallback? = null
    private var negButtonStr = ""
    private var negButtonCallback : DialogButtonCallback? = null
    private var level = Level.INFO
    private var showDirection = Direction.CENTER
    private var cancelable = true
    private var backgroundDim = Params.DIM_ALPHA
    private var animation = 0

    fun setIcon(@DrawableRes iconID:Int): SimpleDialogBuilder {
        icon = iconID
        return this
    }
    fun setTitle(title:String): SimpleDialogBuilder {
        this.title = title
        return this
    }
    fun setTitle(@StringRes titleStrID:Int): SimpleDialogBuilder {
        return setTitle(baseActivity.getString(titleStrID))
    }
    fun setContent(content:String): SimpleDialogBuilder {
        this.content = content
        return this
    }
    fun setContent(@StringRes contentStrID:Int): SimpleDialogBuilder {
        return setContent(baseActivity.getString(contentStrID))
    }
    fun setPositiveButton(buttonStr:String,callback:DialogButtonCallback): SimpleDialogBuilder {
        this.posButtonStr = buttonStr
        this.posButtonCallBack = callback
        return this
    }
    fun setPositiveButton(@StringRes buttonStr:Int, callback:DialogButtonCallback): SimpleDialogBuilder {
        return setPositiveButton(baseActivity.getString(buttonStr),callback)
    }
    fun setNegativeButton(buttonStr:String,callback:DialogButtonCallback): SimpleDialogBuilder {
        this.negButtonStr = buttonStr
        this.negButtonCallback = callback
        return this
    }
    fun setNegativeButton(@StringRes buttonStr:Int, callback:DialogButtonCallback): SimpleDialogBuilder {
        return setNegativeButton(baseActivity.getString(buttonStr),callback)
    }
    fun setLevel(level:Int): SimpleDialogBuilder {
        this.level = level
        return this
    }
    fun setShowDirection(direction:Int): SimpleDialogBuilder {
        this.showDirection = direction
        return this
    }
    fun setCancelable(cancelable:Boolean): SimpleDialogBuilder {
        this.cancelable = cancelable
        return this
    }
    fun setBackgroundDim(backgroundDim:Float): SimpleDialogBuilder {
        this.backgroundDim = backgroundDim
        return this
    }
    fun setAnimation(@AnimRes animation:Int): SimpleDialogBuilder {
        this.animation = animation
        return this
    }
    fun show():Dialog{
        return baseActivity.showSimpleInfoDialog(icon,title,content,posButtonStr,posButtonCallBack,
                negButtonStr,negButtonCallback,level,showDirection,cancelable,backgroundDim,
                animation)
    }

    /**
     *  默认变量
     */
    object Params{
        const val DIM_ALPHA = 0.5f
    }

    /**
     *  提示等级
     */
    object Level{
        const val INFO = 0
        const val WARMING = 1
        const val ERROR = 2
    }

    /**
     * 显示方向
     */
    object Direction{
        const val CENTER = 0
        const val BOTTOM = 1
    }
}