package creepersan.com.cfilemanager.base

import android.animation.Animator
import android.animation.AnimatorInflater
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.AnimRes
import android.support.annotation.AnimatorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import creepersan.com.cfilemanager.R
import creepersan.com.cfilemanager.application.ManageApplication
import creepersan.com.cfilemanager.callback.DialogButtonCallback
import creepersan.com.cfilemanager.util.Debugger
import creepersan.com.cfilemanager.views.component.SimpleDialogBuilder
import creepersan.com.cfilemanager.views.holder.SimpleDialogViewHolder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.Serializable

/** 基础的Activity
 * Created by CreeperSan on 2017/11/8.
 */
abstract class BaseActivity : AppCompatActivity(){
    protected lateinit var handel : Handler
    private var toast : Toast? = null

    /**
     *  获取布局
     */
    abstract fun getLayoutID():Int

    /**
     *  生命周期
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutID())   //初始化布局
        initHandel()
        initToast()
        initEventBus()
    }

    override fun onDestroy() {
        super.onDestroy()
        unInitEventBus()
    }

    /**
     *  新增生命周期
     */
    open fun onCommand(command: String){

    }

    /**
     *  初始化
     */
    private fun initHandel(){
        handel = Handler()
    }
    private fun initEventBus(){
        EventBus.getDefault().register(this)
    }
    private fun initToast(){
        toast = Toast.makeText(this,"",Toast.LENGTH_SHORT)
    }

    /**
     *  Application
     */
    fun manageApplication():ManageApplication{
        return application as ManageApplication
    }

    /**
     *  去初始化
     */
    private fun unInitEventBus(){
        EventBus.getDefault().unregister(this)
    }

    /**
     * Toast
     */
    fun toast(@StringRes contentID: Int){
        toast(getString(contentID))
    }
    fun toast(content: String){
        if (toast==null){
            toast = Toast.makeText(this,content,Toast.LENGTH_SHORT)
        }else{
            toast?.setText(content)
            toast?.duration = Toast.LENGTH_SHORT
        }
        toast?.show()
    }
    fun toastLong(@StringRes contentID:Int){
        toastLong(getString(contentID))
    }
    fun toastLong(content:String){
        if (toast==null){
            toast = Toast.makeText(this,content,Toast.LENGTH_LONG)
        }else{
            toast?.setText(content)
            toast?.duration = Toast.LENGTH_LONG
        }
        toast?.show()
    }
    fun toastNew(@StringRes content:Int){
        Toast.makeText(this,content,Toast.LENGTH_SHORT).show()
    }
    fun toastNew(content:String){
        Toast.makeText(this,content,Toast.LENGTH_SHORT).show()
    }
    fun toastLongNew(@StringRes content:Int){
        Toast.makeText(this,content,Toast.LENGTH_LONG).show()
    }
    fun toastLongNew(content:String){
        Toast.makeText(this,content,Toast.LENGTH_LONG).show()
    }

    /**
     * Log
     */
    fun log(content:String){
        Debugger.log(javaClass::class.java.simpleName,content,Debugger.LEVEL_INFO)
    }
    fun logW(content:String){
        Debugger.log(javaClass::class.java.simpleName,content,Debugger.LEVEL_WARMING)
    }
    fun logE(content:String){
        Debugger.log(javaClass::class.java.simpleName,content,Debugger.LEVEL_ERROR)
    }

    /**
     * Glide
     */
    protected fun ImageView.loadImage(@DrawableRes imgID:Int){
        Glide.with(this).load(imgID).into(this)
    }

    /**
     *  动画 Animation
     */
    fun loadAnimation(@AnimRes animID:Int):Animation{
        return AnimationUtils.loadAnimation(this,animID)
    }
    fun loadAnimator(@AnimatorRes animID: Int):Animator{
        return AnimatorInflater.loadAnimator(this,animID)
    }

    /**
     *  EventBus
     */
    protected fun postCommand(command:String){
        postEvent(command)
    }
    protected fun postEvent(event:Any){
        EventBus.getDefault().post(event)
    }
    protected fun postStickyEvent(event:Any){
        EventBus.getDefault().postSticky(event)
    }
    protected fun removeStickEvent(event:Any){
        EventBus.getDefault().removeStickyEvent(event)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCommandEvent(command:String){
        onCommand(command)
    }

    /**
     *  系统版本
     */
    fun isVersionOver(sdkVersion:Int):Boolean{
        return Build.VERSION.SDK_INT >= sdkVersion
    }
    fun isVersionOverM():Boolean{
        return isVersionOver(Build.VERSION_CODES.M)
    }
    fun isVersionOverN():Boolean{
        return isVersionOver(Build.VERSION_CODES.N)
    }
    fun isVersionOverO():Boolean{
        return isVersionOver(Build.VERSION_CODES.O)
    }

    /**
     * 权限
     */
    protected fun checkPermission(permission:String):Boolean{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }
    protected fun requestPermission(requestCode:Int,vararg permissions:String){
        ActivityCompat.requestPermissions(this,permissions,requestCode)
    }

    /**
     *  Dialog 对话框
     */
    fun showSimpleInfoDialog(icon:Int, title:String, content:String, positiveStr:String = "",
                             positiveButtonCallback:DialogButtonCallback? = null, negativeStr:String = "",
                             negativeCallback:DialogButtonCallback? = null, level:Int = SimpleDialogBuilder.Level.INFO,
                             showDirection:Int = SimpleDialogBuilder.Direction.CENTER, cancelable: Boolean = true,
                             backgroundDIM:Float = SimpleDialogBuilder.Params.DIM_ALPHA, animation:Int = 0):Dialog{
        val dialogView = getView(R.layout.dialog_simple)
        val holder = SimpleDialogViewHolder(dialogView)
        val dialog : Dialog
        when(showDirection){
            SimpleDialogBuilder.Direction.BOTTOM -> {
                dialog = showDialogAtBottom(dialogView,cancelable,backgroundDIM,animation)
            }
            else -> {
                dialog = showDialog(dialogView,cancelable,backgroundDIM,animation)
            }
        }
        //设置dialog的样式
        holder.icon.setImageResource(icon)
        holder.title.text = title
        holder.content.text = content
        //根据等级设置不同的颜色
        val buttonBackgroundDrawablePos : Drawable
        val buttonBackgroundDrawableNeg : Drawable
        when(level){
            SimpleDialogBuilder.Level.WARMING -> {
                holder.titleTab.setBackgroundColor(ContextCompat.getColor(this,R.color.dialogWarmingBackground))
                buttonBackgroundDrawablePos = ContextCompat.getDrawable(this,R.drawable.selector_button_level_warming)
                buttonBackgroundDrawableNeg = ContextCompat.getDrawable(this,R.drawable.selector_button_level_warming)
            }
            SimpleDialogBuilder.Level.ERROR -> {
                holder.titleTab.setBackgroundColor(ContextCompat.getColor(this,R.color.dialogErrorBackground))
                buttonBackgroundDrawablePos = ContextCompat.getDrawable(this,R.drawable.selector_button_level_error)
                buttonBackgroundDrawableNeg = ContextCompat.getDrawable(this,R.drawable.selector_button_level_error)
            }
            else -> {
                holder.titleTab.setBackgroundColor(ContextCompat.getColor(this,R.color.dialogInfoBackground))
                buttonBackgroundDrawablePos = ContextCompat.getDrawable(this,R.drawable.selector_button_level_info)
                buttonBackgroundDrawableNeg = ContextCompat.getDrawable(this,R.drawable.selector_button_level_info)
            }
        }
        if (positiveStr != ""){
            holder.positiveButton.visibility = View.VISIBLE
            holder.positiveButton.text = positiveStr
            holder.positiveButton.background = buttonBackgroundDrawablePos
            holder.positiveButton.setOnClickListener {
                positiveButtonCallback?.onClick(dialog,holder)
                dialog.cancel()
            }
        }
        if (negativeStr != ""){
            holder.negativeButton.visibility = View.VISIBLE
            holder.negativeButton.text = negativeStr
            holder.negativeButton.background = buttonBackgroundDrawableNeg
            holder.negativeButton.setOnClickListener {
                negativeCallback?.onClick(dialog,holder)
                dialog.cancel()
            }
        }
        return dialog
    }
    fun showDialog(dialogView:View, cancelable:Boolean = true, backgroundAlpha:Float = SimpleDialogBuilder.Params.DIM_ALPHA, animation:Int = 0):Dialog{
        val dialog = Dialog(this, R.style.dialogAlphaBackground)
        dialog.setContentView(dialogView)
        dialog.setCancelable(cancelable)
        dialog.setCanceledOnTouchOutside(cancelable)
        dialog.window.setDimAmount(backgroundAlpha)
        if (animation!=0){
            dialog.window.setWindowAnimations(animation)
        }
        dialog.show()
        return dialog
    }
    fun showDialogAtBottom(dialogView:View, cancelable:Boolean = true, backgroundAlpha:Float = SimpleDialogBuilder.Params.DIM_ALPHA, animation:Int = 0):Dialog{
        val dialog = Dialog(this, R.style.dialogAlphaBackground)
        dialog.setContentView(dialogView)
        dialog.setCancelable(cancelable)
        dialog.setCanceledOnTouchOutside(cancelable)
        dialog.window.setDimAmount(backgroundAlpha)
        dialog.window.setGravity(Gravity.BOTTOM)
        val lp = dialog.window.attributes
        lp.x = 0
        lp.y = 0
        lp.width = resources.displayMetrics.widthPixels
        dialogView.measure(0,0)
        lp.height = dialogView.measuredHeight
        dialog.window.attributes = lp
        if (animation!=0){
            dialog.window.setWindowAnimations(animation)
        }
        dialog.show()
        return dialog
    }

    /**
     *  便捷的方法
     */
    fun getView(layoutID:Int,viewGroup:ViewGroup? = null,attachToRoot:Boolean = false):View{
        return layoutInflater.inflate(layoutID,viewGroup,attachToRoot)
    }

    /**
     *  启动Activity
     */
    protected fun startActivity(clazz: Class<*>,keyArray:Array<String>? = null,valueArray: Array<Any>? = null,isFinish:Boolean = false){
        val intent = Intent(this,clazz)
        if (keyArray!=null && valueArray!=null){    //  插入值
            keyArray.forEachIndexed { index, key ->
                val value = valueArray[index]
                when(value){
                    is String -> {
                        intent.putExtra(key,value)
                    }
                    is Int -> {
                        intent.putExtra(key,value)
                    }
                    is Long -> {
                        intent.putExtra(key,value)
                    }
                    is Float -> {
                        intent.putExtra(key,value)
                    }
                    is Double -> {
                        intent.putExtra(key,value)
                    }
                    is Serializable -> {
                        intent.putExtra(key,value)
                    }
                }
            }
        }
        startActivity(intent)
        if (isFinish) finish()
    }

    /**
     *  动画
     */
    fun View.startAnime(){

    }
}

