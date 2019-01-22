package com.creepersan.file.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.creepersan.file.R
import com.creepersan.file.utils.Logger
import com.creepersan.file.view.SimpleDialog
import com.creepersan.file.view.dialog.SeekbarDialog
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity(){
    private val mSettingLayoutManager by lazy { LinearLayoutManager(this) }
    private val mSettingAdapter by lazy { SettingAdapter() }
    private val mSettingBeanList = ArrayList<BaseSettingBean>()
    // 下面是对话框
    private val mHintDialog by lazy { SimpleDialog(this) }
    private val mSeekBarDialog by lazy { SeekbarDialog(this) }

    override val mLayoutID: Int = R.layout.activity_setting

    companion object {
        private const val TYPE_UNKNOWN = -1
        private const val TYPE_CATALOG = 0
        private const val TYPE_NORMAL_SIMPLE = 1 // 只有标题和副标题
        private const val TYPE_NORMAL_SIMPLE_ICON = 2
        private const val TYPE_NORMAL_SIMPLE_SWITCH = 3
        private const val TYPE_NORMAL_SIMPLE_ICON_SWITCH = 4

        private const val ID_CATALOG_GENERAL_SETTING        = 0
        private const val ID_GENERAL_CONFIRM_ON_EXIT         = 1
        private const val ID_GENERAL_CONFIRM_ON_EXIT_DELAY   = 2

        private const val ID_CATALOG_FILE_SETTING           = 1000
        private const val ID_FILE_SHOW_HIDDEN_FILE          = 1001
        private const val ID_FILE_ORDER_REVERSE             = 1002
        private const val ID_FILE_CASE_SENSITIVE            = 1003
        private const val ID_FILE_FOLDER_FIRST            = 1004
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initRecyclerViewData()
        initRecyclerView()
    }

    private fun initToolbar(){
        setSupportActionBar(settingToolbar)
        // 设置其他
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    private fun initRecyclerViewData(){
        // 常规设置
        mSettingBeanList.add(SettingCatalog(getString(R.string.settingGeneralSettingTitle), ID_CATALOG_GENERAL_SETTING))
        mSettingBeanList.add(SettingNormalSimpleSwitch(ID_GENERAL_CONFIRM_ON_EXIT, mConfig.getMainConfirmOnExit(), getString(R.string.settingGeneralConfirmOnExitTitle), getString(R.string.settingGeneralConfirmOnExitSubtitle), { tmpBean, tmpHolder ->
            val bean = tmpBean as SettingNormalSimpleSwitch
            val holder = tmpHolder as SettingNormalSimpleHolder
            bean.state = !bean.state
            holder.setCheck(bean.state)
            mConfig.setMainConfirmOnExit(bean.state)
        }))
        mSettingBeanList.add(SettingNormalSimple(ID_GENERAL_CONFIRM_ON_EXIT_DELAY, getString(R.string.settingGeneralConfirmOnExitDelayTitle), getString(R.string.settingGeneralConfirmOnExitDelaySubtitle), { _, _ ->
            mSeekBarDialog
                .setMax(9)
                .setProgress(mConfig.getMainConfirmOnExitDelay()/100 - 1)
                .setHintText(String.format(getString(R.string.settingDialogGeneralConfirmOnExitDelayHintTemplate), mConfig.getMainConfirmOnExitDelay()))
                .setOnSeekBarChange { _, progress, fromUser ->
                    if (fromUser){
                        val templateStr = getString(R.string.settingDialogGeneralConfirmOnExitDelayHintTemplate)
                        mSeekBarDialog.setHintText(String.format(templateStr, (progress+1)*100))
                    }
                }
                .setTitleID(R.string.settingDialogGeneralConfirmOnExitDelayTitle)
                .setPosButton(R.string.dialogButtonPosText, object :SimpleDialog.OnDialogButtonClickListener{
                    override fun onButtonClick(dialog: SimpleDialog) {
                        val delay = (mSeekBarDialog.getProgress() + 1)*100
                        if (delay < 300){ // 时间小于300MS！！！弹框确认！因为小于300ms将需要很快的速度
                            mHintDialog
                                .setTitleID(R.string.dialogTitleNotice)
                                .setMessage(R.string.settingDialogGeneralConfirmOnExitDelayTimeTooShortHint)
                                .setPosButton(R.string.dialogButtonPosText, object : SimpleDialog.OnDialogButtonClickListener{
                                    override fun onButtonClick(dialog: SimpleDialog) {
                                        mConfig.setMainConfirmOnExitDealy(delay)
                                        mHintDialog.dismiss()
                                        mSeekBarDialog.dismiss()
                                    }
                                })
                                .setNegButton(R.string.dialogButtonNegText, object : SimpleDialog.OnDialogButtonClickListener{
                                    override fun onButtonClick(dialog: SimpleDialog) {
                                        dialog.dismiss()
                                    }
                                })
                                .show()
                        }else{// 设置时间
                            mConfig.setMainConfirmOnExitDealy(delay)
                            dialog.dismiss()
                        }
                    }
                })
                .setNegButton(R.string.dialogButtonNegText, object :SimpleDialog.OnDialogButtonClickListener{
                    override fun onButtonClick(dialog: SimpleDialog) {
                        dialog.dismiss()
                    }
                })
                .show()

        }))

        // 文件设置
        mSettingBeanList.add(SettingCatalog(getString(R.string.settingFileSettingTitle), ID_CATALOG_FILE_SETTING))
        mSettingBeanList.add(SettingNormalSimpleSwitch(ID_FILE_SHOW_HIDDEN_FILE, mConfig.getFileIsShowHiddenFile(), getString(R.string.settingFileSettingShowHiddenFileTitle), getString(R.string.settingFileSettingShowHiddenFileSubtitle), {tmpBean, tmpHolder ->
            val bean = tmpBean as SettingNormalSimpleSwitch
            val holder = tmpHolder as SettingNormalSimpleHolder
            bean.state = !bean.state
            holder.setCheck(bean.state)
            mConfig.setFileIsShowHiddenFile(bean.state)
        }))
        mSettingBeanList.add(SettingNormalSimpleSwitch(ID_FILE_ORDER_REVERSE, mConfig.getFileIsOrderReverse(), getString(R.string.settingFileSettingOrderReverseTitle), getString(R.string.settingFileSettingOrderReverseSubtitle), {tmpBean, tmpHolder ->
            val bean = tmpBean as SettingNormalSimpleSwitch
            val holder = tmpHolder as SettingNormalSimpleHolder
            bean.state = !bean.state
            holder.setCheck(bean.state)
            mConfig.setFileIsOrderReverse(bean.state)
        }))
        mSettingBeanList.add(SettingNormalSimpleSwitch(ID_FILE_CASE_SENSITIVE, mConfig.getFileIsSortCaseSensitive(), getString(R.string.settingFileSettingCaseSensitiveTitle), getString(R.string.settingFileSettingCaseSensitiveSubtitle), { tmpBean, tmpHolder ->
            val bean = tmpBean as SettingNormalSimpleSwitch
            val holder = tmpHolder as SettingNormalSimpleHolder
            bean.state = !bean.state
            holder.setCheck(bean.state)
            mConfig.setFileIsSortCaseSensitive(bean.state)
        }))
        mSettingBeanList.add(SettingNormalSimpleSwitch(ID_FILE_FOLDER_FIRST, mConfig.getFileIsFolderFirst(), getString(R.string.settingFileSettingFolderFirstTitle), getString(R.string.settingFileSettingFolderFirstSubtitle), { tmpBean, tmpHolder ->
            val bean = tmpBean as SettingNormalSimpleSwitch
            val holder = tmpHolder as SettingNormalSimpleHolder
            bean.state = !bean.state
            holder.setCheck(bean.state)
            mConfig.setFileIsFolderFirst(bean.state)
        }))

    }
    private fun initRecyclerView(){
        settingList.layoutManager = mSettingLayoutManager
        settingList.adapter = mSettingAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }

    /* 内部类 */
    // 下面是列表相关的ItemBean
    private abstract inner class BaseSettingBean(val id:Int,val type:Int,val onClickAction:((bean:BaseSettingBean,holder:BaseSettingViewHolder)->Unit)?=null, val onLongClickAction:((bean:BaseSettingBean,holder:BaseSettingViewHolder)->Unit)?=null)
    private inner class SettingCatalog(val title:String, id:Int):BaseSettingBean(id, TYPE_CATALOG)
    private open inner class SettingNormalSimpleIconSwitch(id:Int, val icon:Int,var state:Boolean, val title:String, val subtitle: String="", onClick:((bean:BaseSettingBean,holder:BaseSettingViewHolder)->Unit)?=null, onLongClick:((bean:BaseSettingBean,holder:BaseSettingViewHolder)->Unit)?=null, type: Int=TYPE_NORMAL_SIMPLE_ICON_SWITCH):BaseSettingBean(id, type, onClick, onLongClick)
    private inner class SettingNormalSimple(id:Int, title:String, subtitle: String="", onClick:((bean:BaseSettingBean,holder:BaseSettingViewHolder)->Unit)?=null, onLongClick:((bean:BaseSettingBean,holder:BaseSettingViewHolder)->Unit)?=null):SettingNormalSimpleIconSwitch(id, 0, false, title, subtitle, onClick, onLongClick, TYPE_NORMAL_SIMPLE)
    private inner class SettingNormalSimpleIcon(id:Int, icon:Int, title:String, subtitle: String="", onClick:((bean:BaseSettingBean,holder:BaseSettingViewHolder)->Unit)?=null, onLongClick:((bean:BaseSettingBean,holder:BaseSettingViewHolder)->Unit)?=null):SettingNormalSimpleIconSwitch(id, icon, false, title, subtitle, onClick, onLongClick, TYPE_NORMAL_SIMPLE_ICON)
    private inner class SettingNormalSimpleSwitch(id:Int, state:Boolean, title:String, subtitle: String="", onClick:((bean:BaseSettingBean,holder:BaseSettingViewHolder)->Unit)?=null, onLongClick:((bean:BaseSettingBean,holder:BaseSettingViewHolder)->Unit)?=null):SettingNormalSimpleIconSwitch(id, 0, state, title, subtitle, onClick, onLongClick, TYPE_NORMAL_SIMPLE_SWITCH)

    // 下面是列表相关的ViewHolder
    private abstract inner class BaseSettingViewHolder(parent:ViewGroup, layoutID:Int) : RecyclerView.ViewHolder(layoutInflater.inflate(layoutID, parent, false))
    private inner class CatalogViewHolder(parent: ViewGroup) : BaseSettingViewHolder(parent, R.layout.item_setting_catalog){
        private val textTitle = itemView as TextView
        fun setTitle(str:String){
            textTitle.text = str
        }
    }
    private inner class SettingNormalSimpleHolder(parent: ViewGroup) : BaseSettingViewHolder(parent, R.layout.item_setting_normal){
        private val imageIcon = itemView.findViewById<ImageView>(R.id.itemSettingNormalIcon)
        private val textTitle = itemView.findViewById<TextView>(R.id.itemSettingNormalTitle)
        private val textSubtitle = itemView.findViewById<TextView>(R.id.itemSettingNormalSubTitle)
        private val switch = itemView.findViewById<Switch>(R.id.itemSettingNormalSwitch)

        fun initVisibilityByType(type:Int){
            when(type){
                TYPE_NORMAL_SIMPLE -> {
                    imageIcon.visibility = View.INVISIBLE
                    textTitle.visibility = View.VISIBLE
                    textSubtitle.visibility = View.VISIBLE
                    switch.visibility = View.INVISIBLE
                }
                TYPE_NORMAL_SIMPLE_ICON -> {
                    imageIcon.visibility = View.VISIBLE
                    textTitle.visibility = View.VISIBLE
                    textSubtitle.visibility = View.VISIBLE
                    switch.visibility = View.INVISIBLE
                }
                TYPE_NORMAL_SIMPLE_SWITCH -> {
                    imageIcon.visibility = View.INVISIBLE
                    textTitle.visibility = View.VISIBLE
                    textSubtitle.visibility = View.VISIBLE
                    switch.visibility = View.VISIBLE
                }
                TYPE_NORMAL_SIMPLE_ICON_SWITCH -> {
                    imageIcon.visibility = View.VISIBLE
                    textTitle.visibility = View.VISIBLE
                    textSubtitle.visibility = View.VISIBLE
                    switch.visibility = View.VISIBLE
                }
                else -> {
                    Logger.logE("设置的SimpleViewHolder Type $type 为未知类型，无法处理")
                    throw IllegalStateException()
                }
            }
        }

        fun setContent(title:String, subtitle:String=""){
            if (subtitle == ""){
                textSubtitle.visibility = View.GONE
            }else{
                textSubtitle.text = subtitle
                textSubtitle.visibility = View.VISIBLE
            }
            textTitle.text = title
        }

        fun setSwitchState(isOn:Boolean){
            switch.isChecked = isOn
        }

        fun setIcon(icon:Int){
            imageIcon.setImageResource(icon)
        }

        fun setCheck(isCheck:Boolean){
            switch.isChecked = isCheck
        }

        fun isCheck():Boolean{
            return switch.isChecked
        }
    }

    // 下面是列表相关的Adapter
    private inner class SettingAdapter : RecyclerView.Adapter<BaseSettingViewHolder>(){
        override fun getItemViewType(position: Int): Int {
            return mSettingBeanList[position].type
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseSettingViewHolder {
            return when(viewType){
                TYPE_CATALOG -> CatalogViewHolder(parent)
                TYPE_NORMAL_SIMPLE,
                TYPE_NORMAL_SIMPLE_ICON,
                TYPE_NORMAL_SIMPLE_SWITCH,
                TYPE_NORMAL_SIMPLE_ICON_SWITCH -> SettingNormalSimpleHolder(parent)
                else -> { Logger.logE("设置Setting里面需要创建一个未知类型的SettingBeanViewHolder");throw IllegalStateException(); }
            }
        }

        override fun getItemCount(): Int {
            return mSettingBeanList.size
        }

        override fun onBindViewHolder(tmpHolder: BaseSettingViewHolder, pos: Int) {
            val tmpBean = mSettingBeanList[pos]
            when(tmpBean.type){
                // 设置类别标题
                TYPE_CATALOG -> {
                    val bean = tmpBean as SettingCatalog
                    val holder = tmpHolder as CatalogViewHolder
                    holder.setTitle(bean.title)
                }
                TYPE_NORMAL_SIMPLE ,
                TYPE_NORMAL_SIMPLE_ICON ,
                TYPE_NORMAL_SIMPLE_SWITCH ,
                TYPE_NORMAL_SIMPLE_ICON_SWITCH -> {
                    val bean = tmpBean as SettingNormalSimpleIconSwitch
                    val holder = tmpHolder as SettingNormalSimpleHolder
                    holder.initVisibilityByType(bean.type)
                    holder.setContent(bean.title, bean.subtitle)
                    if (bean.icon!=0){
                        holder.setIcon(bean.icon)
                    }
                    holder.setSwitchState(bean.state)
                    holder.itemView.setOnClickListener{
                        bean.onClickAction?.invoke(tmpBean, holder)
                    }
                    holder.itemView.setOnLongClickListener{
                        bean.onLongClickAction?.invoke(tmpBean, holder)
                        return@setOnLongClickListener true
                    }
                }
                // 其他
                else -> { Logger.logE("需要渲染一个未知类别的设置ItemBean在ViewHolder中");throw IllegalStateException() }
            }
        }

    }
}
