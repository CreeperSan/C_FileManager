package com.creepersan.file.view.dialog

import android.content.Context
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.creepersan.file.R
import com.creepersan.file.view.SimpleDialog

class SeekbarDialog(context: Context) : SimpleDialog(context,
    DIRECTION_CENTER,
    TYPE_CUSTOM_VIEW
),
    SeekBar.OnSeekBarChangeListener {

    private lateinit var seekbar:SeekBar
    private lateinit var hintText:TextView
    private var onProgressChangeAction : ((seekBar: SeekBar, progress: Int, fromUser: Boolean)->Unit)?=null
    private var onSeekBarStartChange : ((seekbar:SeekBar)->Unit)?=null
    private var onSeekBarStopChange : ((seekbar:SeekBar)->Unit)?=null

    init {
        setCustomView(layoutInflater.inflate(R.layout.dialog_seekbar, viewCustomViewGroup, false))
    }

    override fun initCustomView(customView: View) {
        super.initCustomView(customView)
        findViews(customView)
        initSeekbar()
    }

    /* 内部初始化 */
    private fun findViews(customView:View){
        seekbar = customView.findViewById(R.id.dialogSeekbarSeekbar)
        hintText = customView.findViewById(R.id.dialogSeekbarHintText)
    }
    private fun initSeekbar(){
        seekbar.setOnSeekBarChangeListener(this)
    }

    /* 回调方法 */
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        onProgressChangeAction?.invoke(seekBar, progress, fromUser)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        onSeekBarStartChange?.invoke(seekbar)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        onSeekBarStopChange?.invoke(seekbar)
    }

    /* 接口方法 */
    fun setHintText(hint:String):SeekbarDialog{
        hintText.text = hint
        return this
    }

    fun setMax(max:Int):SeekbarDialog{
        seekbar.max = max
        return this
    }

    fun getMax():Int{
        return seekbar.max
    }

    fun setProgress(progress: Int):SeekbarDialog{
        if (progress < 0){
            seekbar.progress = 0
        }else{
            seekbar.progress = progress
        }
        return this
    }

    fun getProgress():Int{
        return seekbar.progress
    }

    fun setOnSeekBarChange(action:((seekBar: SeekBar, progress: Int, fromUser: Boolean)->Unit)?=null):SeekbarDialog{
        onProgressChangeAction = action
        return this
    }

    fun setOnSeekBarStartChange(action:((seekbar:SeekBar)->Unit)?=null):SeekbarDialog{
        onSeekBarStartChange = action
        return this
    }

    fun setOnSeekBarStopChange(action:((seekbar:SeekBar)->Unit)?=null):SeekbarDialog{
        onSeekBarStopChange = action
        return this
    }



}