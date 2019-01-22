package com.creepersan.file.function.video.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.creepersan.file.R
import com.creepersan.file.utils.dp2px
import java.lang.Error
import java.lang.Exception

class ColumnProgressBar : View{

    constructor(context: Context):this(context, null)
    constructor(context: Context, attrs: AttributeSet?):this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):this(context, attrs, defStyleAttr, 0)
    constructor(context:Context, attrs:AttributeSet?, defStyleAttr:Int, defStyleRes:Int):super(context, attrs, defStyleAttr, defStyleRes){
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColumnProgressBar)

        mOrientation = typedArray.getInt(R.styleable.ColumnProgressBar_orientation, ORIENTATION_HORIZONTAL)
        mProgress = typedArray.getInt(R.styleable.ColumnProgressBar_progress, mProgress)
        mProgressColor = typedArray.getColor(R.styleable.ColumnProgressBar_progress_color, mProgress)
        mMax = typedArray.getInt(R.styleable.ColumnProgressBar_max, mMax)
        mMin = typedArray.getInt(R.styleable.ColumnProgressBar_min, mMin)
        mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.ColumnProgressBar_border_width, mBorderWidth)
        mBorderColor = typedArray.getColor(R.styleable.ColumnProgressBar_border_color, mBorderColor)
        mBorderPadding = typedArray.getDimensionPixelSize(R.styleable.ColumnProgressBar_border_padding, mBorderPadding)

        // 矫正 Max 与 Min 和 Progress
        if (mMax == mMin){
            throw ValueIllegalException("Max value can not equal to min value")
        }else if (mMax < mMin){
            throw ValueIllegalException("Max value can not less than min value")
        }
        if (mProgress > mMax){
            mProgress = mMax
        }
        if (mProgress < mMin){
            mProgress = mMin
        }

        typedArray.recycle()
    }

    companion object {
        const val ORIENTATION_HORIZONTAL = 0
        const val ORIENTATION_VERTICAL = 1

        private const val DEFAULT_WIDTH_DP  = 560
        private const val DEFAULT_HEIGHT_DP = 36
    }

    private var mOrientation = ORIENTATION_HORIZONTAL
    private var mProgressColor = Color.parseColor("#66CCFF")
    private var mProgress = 50
    private var mMax = 100
    private var mMin = 0
    private var mBorderWidth = dp2px(context, 1f)
    private var mBorderColor = Color.WHITE
    private var mBorderPadding = dp2px(context, 2f)

    private var mProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private var mProgressRect = Rect(0,0,0,0)
    private var mBorderRect = Rect(0,0,0,0)

    fun setProgressColor(color:Int){
        mProgressColor = color
        mProgressPaint.color = mProgressColor
        refreshView()
    }
    fun getProgressColor():Int{
        return mProgressColor
    }
    fun setProgress(progress:Int){
        mProgress = progress
        if (mProgress > mMax){ mProgress = mMax }
        if (mProgress < mMin){ mProgress = mMin }
        refreshView()
    }
    fun getProgress():Int{
        return mProgress
    }
    fun setMax(max:Int){
        if (max <= mMin){
            throw ValueIllegalException("Max value should be more than min value")
        }
        if (mProgress > max){
            mProgress = max
        }
        mMax = max
        refreshView()
    }
    fun getMax():Int{
        return mMax
    }
    fun setMin(min:Int){
        if (min >= mMax){
            throw ValueIllegalException("Min value should be less than max value")
        }
        if (mProgress < min){
            mProgress = min
        }
        mMin = min
        refreshView()
    }
    fun getMin():Int{
        return mMin
    }
    fun setBorderWidth(borderWidth:Int){
        mBorderWidth = borderWidth
        refreshView()
    }
    fun getBorderWidth():Int{
        return mBorderWidth
    }
    fun setBorderPadding(padding:Int){
        mBorderPadding = padding
        refreshView()
    }
    fun getBorderPadding():Int{
        return mBorderPadding
    }
    fun setBorderColor(color: Int){
        mBorderColor = color
        refreshView()
    }
    fun getBorderColor():Int{
        return mBorderColor
    }
    fun getOrirentation():Int{
        return mOrientation
    }
    fun setOrientation(orientation:Int){
        if (orientation != ORIENTATION_VERTICAL && orientation != ORIENTATION_HORIZONTAL){
            throw ValueIllegalException("Orientation must be ORIENTATION_HORIZONTAL or ORIENTATION_VERTICAL")
        }
        mOrientation = orientation
        refreshView()
    }

    fun refreshView(){
        invalidate()
    }



    /* 计算测量空间的长宽 */
    private fun getCalculateSize(defSize:Int, measureSpec:Int):Int{
        val size = MeasureSpec.getSize(measureSpec)
        return when(MeasureSpec.getMode(measureSpec)){
            // 对应 wrap_content
            MeasureSpec.AT_MOST -> defSize
            // 固定大小 对应 match_parent 以及 指定大小,如56dp...
            MeasureSpec.EXACTLY -> size
            // 没有指定大小
            MeasureSpec.UNSPECIFIED -> defSize
            else -> defSize
        }
    }

    /* 重写测量大小方法 */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            getCalculateSize(DEFAULT_WIDTH_DP, widthMeasureSpec),
            getCalculateSize(DEFAULT_HEIGHT_DP, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 绘制边框
        mBorderRect.left = 0
        mBorderRect.top = 0
        mBorderRect.right = width
        mBorderRect.bottom = height
        mBorderPaint.color = mBorderColor
        mBorderPaint.strokeWidth = mBorderWidth.toFloat()
        canvas.drawRect(mBorderRect, mBorderPaint)

        if (mOrientation == ORIENTATION_HORIZONTAL){
            // 计算开始以及结束位置
            val startPos = mBorderWidth + mBorderPadding
            val endPos = width - startPos
            mProgressRect.left = startPos
            mProgressRect.right = ((startPos.toFloat() + ((mProgress.toFloat() - mMin.toFloat()) / (mMax.toFloat() - mMin.toFloat())) * (endPos.toFloat() - startPos.toFloat()))).toInt()
            mProgressRect.top = mBorderWidth/2 + mBorderPadding
            mProgressRect.bottom = height - mBorderWidth/2 - mBorderPadding
            mProgressPaint.color = mProgressColor
            canvas.drawRect(mProgressRect, mProgressPaint)
        }else if (mOrientation == ORIENTATION_VERTICAL){
            // 计算开始以及结束位置
            val startPos = mBorderWidth + mBorderPadding
            val endPos = height - startPos
            mProgressRect.top = height - ((startPos.toFloat() + ((mProgress.toFloat() - mMin.toFloat()) / (mMax.toFloat() - mMin.toFloat())) * (endPos.toFloat() - startPos.toFloat()))).toInt()
            mProgressRect.bottom = height - startPos
            mProgressRect.left = mBorderWidth + mBorderPadding
            mProgressRect.right = width - mBorderWidth - mBorderPadding
            mProgressPaint.color = mProgressColor
            canvas.drawRect(mProgressRect, mProgressPaint)
        }else{
            throw ValueIllegalException("Orientation must be ORIENTATION_HORIZONTAL or ORIENTATION_VERTICAL")
        }



    }

    inner class ValueIllegalException(message:String) : Exception(message)

}