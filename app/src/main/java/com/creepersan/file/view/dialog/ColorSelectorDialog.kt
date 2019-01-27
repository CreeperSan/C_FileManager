package com.creepersan.file.view.dialog

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.creepersan.file.R
import com.creepersan.file.utils.toColorString
import com.creepersan.file.view.SimpleDialog

class ColorSelectorDialog(context: Context) : SimpleDialog(context, SimpleDialog.DIRECTION_CENTER, SimpleDialog.TYPE_CUSTOM_VIEW),
    SeekBar.OnSeekBarChangeListener {

    private lateinit var previewImage   : TextView
    private lateinit var redSeekBar     : SeekBar
    private lateinit var redValueText   : TextView
    private lateinit var greenSeekBar   : SeekBar
    private lateinit var greenValueText : TextView
    private lateinit var blueSeekBar    : SeekBar
    private lateinit var blueValueText  : TextView

    init {
        setCustomView(layoutInflater.inflate(R.layout.dialog_color_selector, viewCustomViewGroup, false))
    }

    override fun initCustomView(customView: View) {
        super.initCustomView(customView)
        findViews(customView)
        initSeekBar()
    }

    private fun findViews(customView:View){
        previewImage    = customView.findViewById(R.id.dialogColorSelectorPreview)
        redSeekBar      = customView.findViewById(R.id.dialogColorSelectorRedSeekBar)
        redValueText    = customView.findViewById(R.id.dialogColorSelectorRedValue)
        greenSeekBar    = customView.findViewById(R.id.dialogColorSelectorGreenSeekBar)
        greenValueText  = customView.findViewById(R.id.dialogColorSelectorGreenValue)
        blueSeekBar     = customView.findViewById(R.id.dialogColorSelectorBlueSeekBar)
        blueValueText   = customView.findViewById(R.id.dialogColorSelectorBlueValue)
    }
    private fun initSeekBar(){
        redSeekBar.setOnSeekBarChangeListener(this)
        greenSeekBar.setOnSeekBarChangeListener(this)
        blueSeekBar.setOnSeekBarChangeListener(this)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        when(seekBar){
            redSeekBar -> {
                redValueText.text = progress.toString()
            }
            greenSeekBar -> {
                greenValueText.text = progress.toString()
            }
            blueSeekBar -> {
                blueValueText.text = progress.toString()
            }
        }
        refreshPreview()

    }

    fun getColor():Int{
        return Color.rgb(redSeekBar.progress, greenSeekBar.progress, blueSeekBar.progress)
    }

    fun setColor(r:Int, g:Int, b:Int):ColorSelectorDialog{
        setSingleColor(r, redSeekBar, redValueText)
        setSingleColor(g, greenSeekBar, greenValueText)
        setSingleColor(b, blueSeekBar, blueValueText)
        refreshPreview()
        return this
    }

    fun setColor(color:Int):ColorSelectorDialog{
        val red = (color shr 16) and 0xFF
        val green = (color shr 8) and 0xFF
        val blue = (color shr 0) and 0xFF
        setColor(red, green, blue)
        return this
    }

    fun refreshPreview():ColorSelectorDialog{
        val color = getColor()
        previewImage.setBackgroundColor(color)
        previewImage.text = color.toColorString()
        return this
    }

    private fun setSingleColor(value:Int, seekBar: SeekBar, textView: TextView){
        val colorValue = when{
            value < 0 -> { 0 }
            value > seekBar.max -> { seekBar.max }
            else -> { value }
        }
        seekBar.progress = colorValue
        textView.text = colorValue.toString()
    }

}