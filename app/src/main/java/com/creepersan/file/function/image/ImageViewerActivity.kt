package com.creepersan.file.function.image

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import com.bumptech.glide.Glide
import com.creepersan.file.FileApplication
import com.creepersan.file.R
import com.creepersan.file.activity.BaseActivity
import com.creepersan.file.utils.dp2px
import com.creepersan.file.utils.getPostFix
import kotlinx.android.synthetic.main.activity_image_viewer.*
import java.io.File

class ImageViewerActivity : BaseActivity(), Toolbar.OnMenuItemClickListener, View.OnTouchListener {

    companion object {
        private val UNDEFINE = Float.MIN_VALUE
    }

    override val mLayoutID: Int = R.layout.activity_image_viewer

    private var mFilePath = ""
    private val mFile by lazy { File(mFilePath) }
    private var mRotation = 0f
    private var mOffsetX = 0f
    private var mOffsetY = 0f
    private var mTouchStartX1 = UNDEFINE
    private var mTouchStartY1 = UNDEFINE
    private var mTouch2FingerCenterStartX = UNDEFINE
    private var mTouch2FingerCenterStartY = UNDEFINE
    private var mTouch2FingerDistance = UNDEFINE
    private var isTouch2Finger = false
    private var mPrevWidth = UNDEFINE
    private var mPrevHeight = UNDEFINE
    private var mMoveDistanceScale = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFilePath()
        initToolBar()
        initBackground()
        initTouchZone()
        initImage()
    }

    private fun initFilePath(){
        mFilePath = intent.data.path ?: ""
        // 检查文件是否存在
        if (!mFile.exists()){
            toast(R.string.imageViewHintFileNotExist)
            finish()
            return
        }
    }
    private fun initToolBar(){
        // 设置标题
        imageViewerToolBar.title = mFile.name
        // Navigation Icon
        imageViewerToolBar.setNavigationIcon(R.drawable.ic_close_white)
        imageViewerToolBar.setNavigationOnClickListener {
            onBackPressed()
        }
        // 设置菜单
        imageViewerToolBar.inflateMenu(R.menu.image_viewer)
        imageViewerToolBar.setOnMenuItemClickListener(this)
    }
    private fun initBackground(){
        imageViewerRootView.setBackgroundColor(mConfig.imageViewerGetBackgroundColor())
    }
    private fun initTouchZone(){
        imageViewerTouchZone.setOnTouchListener(this)
    }
    private fun initImage(){
        if(mFile.getPostFix().toUpperCase() == "GIF"){
            Glide.with(imageViewerImageView).load(mFile).into(imageViewerImageView)
        }else{
            imageViewerImageView.setImageBitmap(BitmapFactory.decodeFile(mFile.absolutePath))
        }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun setImageViewMargin(x:Float, y:Float){
        val param = imageViewerImageView.layoutParams as ConstraintLayout.LayoutParams
        val left : Int
        val right : Int
        val top : Int
        val bottom : Int
        if (x > 0){
            left = (x*mMoveDistanceScale).toInt()
            right = 0
        }else{
            left = 0
            right = -(x*mMoveDistanceScale).toInt()
        }
        if (y > 0){
            top = (y*mMoveDistanceScale).toInt()
            bottom = 0
        }else{
            top = 0
            bottom = -(y*mMoveDistanceScale).toInt()
        }
        param.setMargins(left, top, right, bottom)
        imageViewerImageView.layoutParams = param
    }
    private fun setImageViewScale(scale:Float){
        val param = imageViewerImageView.layoutParams
        param.width = (mPrevWidth * scale).toInt()
        param.height = (mPrevWidth * scale).toInt()
        imageViewerImageView.layoutParams = param
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when(menuItem.itemId){
            R.id.menuImageViewerLeftRotate90Degree -> {
                mRotation -= 90F
                imageViewerImageView.rotation = mRotation
            }
            R.id.menuImageViewerRightRotate90Degree -> {
                mRotation += 90F
                imageViewerImageView.rotation = mRotation
            }
        }
        return true
    }
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val pointerCount = event.pointerCount
        val action = event.actionMasked
        when(pointerCount){
            1 -> {  // 单个手指触摸
                val x = event.x
                val y = event.y
                when {
                    action == MotionEvent.ACTION_DOWN -> {
                        isTouch2Finger = false
                        mTouchStartX1 = x
                        mTouchStartY1 = y
                    }
                    action == MotionEvent.ACTION_MOVE && !isTouch2Finger-> {
                        val deltaX = x - mTouchStartX1
                        val deltaY = y - mTouchStartY1
                        setImageViewMargin(mOffsetX + deltaX, mOffsetY + deltaY)
                    }
                    action == MotionEvent.ACTION_UP && !isTouch2Finger -> {
                        mOffsetX = mOffsetX + x - mTouchStartX1
                        mOffsetY = mOffsetY + y - mTouchStartY1
                    }
                }

            }
            2 -> { // 两个手指触摸
                val x1 = event.getX(0)
                val y1 = event.getX(0)
                val x2 = event.getX(1)
                val y2 = event.getY(1)
                val centerX = (x1 + x2)/2
                val centerY = (y1 + y2)/2
                val distance = Math.pow(Math.pow((x1.toDouble()-x2.toDouble()),2.toDouble()) + Math.pow((y1.toDouble()-y2.toDouble()),2.toDouble()), 0.5).toFloat()
                when(action){
                    MotionEvent.ACTION_POINTER_DOWN -> {
                        isTouch2Finger = true
                        mTouch2FingerCenterStartX = centerX
                        mTouch2FingerCenterStartY = centerY
                        mTouch2FingerDistance = distance
                        mPrevWidth = imageViewerImageView.width.toFloat()
                        mPrevHeight = imageViewerImageView.height.toFloat()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        setImageViewScale(distance / mTouch2FingerDistance)
                    }
                    MotionEvent.ACTION_POINTER_UP -> {

                    }
                }
            }
        }


















//        // 确定点的坐标
//        if (event.isIDExist(mTouchPointerID1)){
//            mTouchPointerID1 = UNDEFINE
//        }else{
//            mTouchStartX1 = event.getX(event.findPointerIndex(mTouchPointerID1)).toInt()
//            mTouchStartY1 = event.getY(event.findPointerIndex(mTouchPointerID1)).toInt()
//        }
//        if (event.isIDExist(mTouchPointerID2)){
//            mTouchPointerID2 = UNDEFINE
//        }else{
//            mTouchPrevX_2 = event.getX(event.findPointerIndex(mTouchPointerID2)).toInt()
//            mTouchPrevY_2 = event.getY(event.findPointerIndex(mTouchPointerID2)).toInt()
//        }
//
//
//        when(event.actionMasked){
//            MotionEvent.ACTION_DOWN,
//            MotionEvent.ACTION_POINTER_DOWN -> {
//                when(pointerCount){
//                    1 -> {
//                        event.getPointerId(0)
//                    }
//                    2 -> {
//
//                    }
//                }
//            }
//            MotionEvent.ACTION_MOVE -> {
//
//            }
//            MotionEvent.ACTION_POINTER_UP,
//            MotionEvent.ACTION_UP -> {
//
//            }
//        }
//        Logger.log("count:$pointerCount  Pointer1:(${if (mTouchPointerID1!= UNDEFINE){"$mTouchStartX1,$mTouchStartY1"}else{"-UNDEFINE-"}})  Pointer2:(${if (mTouchPointerID2!= UNDEFINE){"$mTouchPrevX_2,$mTouchPrevY_2"}else{"-UNDEFINE-"}})")
        return true
    }

}