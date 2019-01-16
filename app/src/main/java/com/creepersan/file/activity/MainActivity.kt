package com.creepersan.file.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.creepersan.file.FileApplication
import com.creepersan.file.R
import com.creepersan.file.activity.MainActivity.StartActionBaseItem.Companion.ID_ABOUT
import com.creepersan.file.activity.MainActivity.StartActionBaseItem.Companion.ID_EXIT
import com.creepersan.file.activity.MainActivity.StartActionBaseItem.Companion.ID_HOME
import com.creepersan.file.activity.MainActivity.StartActionBaseItem.Companion.ID_INTERNAL_STORAGE
import com.creepersan.file.activity.MainActivity.StartActionBaseItem.Companion.ID_SETTING
import com.creepersan.file.fragment.ApplicationFragment
import com.creepersan.file.fragment.BaseMainActivityFragment
import com.creepersan.file.fragment.FileFragment
import com.creepersan.file.fragment.HomeFragment
import com.creepersan.file.utils.Logger
import com.creepersan.file.utils.getTypeIconID
import com.creepersan.file.view.SimpleDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : BaseActivity() {
    override val mLayoutID: Int = R.layout.activity_main

    private val mMessageDialog by lazy { SimpleDialog(this, SimpleDialog.DIRECTION_CENTER, SimpleDialog.TYPE_MESSAGE) }

    private val mOperationActionList by lazy { arrayListOf(
        EndOperationItem(R.drawable.ic_file_paste, getString(R.string.textMainEndDrawerOperationAllPasteTo), EndOperationItem.PASTE_ALL),
        EndOperationItem(R.drawable.ic_file_cut, getString(R.string.textMainEndDrawerOperationAllMoveTo), EndOperationItem.CUT_ALL),
        EndOperationItem(R.drawable.ic_file_delete, getString(R.string.textMainEndDrawerOperationAllDelete), EndOperationItem.DELETE_ALL),
        EndOperationItem(R.drawable.ic_close, getString(R.string.textMainEndDrawerOperationAllClear), EndOperationItem.CLEAR_ALL)
        ) }
    private val mOperationAdapter by lazy { EndDrawerOperationAdapter() }
    private val mDrawerEndFileList by lazy { ArrayList<EndFileItem>() }
    private val mDrawerEndFileAdapter by lazy { EndDrawerFileAdapter() }
    private val mDrawerStartAdapter by lazy { StartDrawerAdapter() }
    private val mDrawerStartItemList by lazy {
        ArrayList<StartActionBaseItem>().apply {
            add(StartActionCatalogItem(R.drawable.ic_main_create_window, getString(R.string.mainStartDrawerCatalogCreateWindow), true))
            add(StartActionSimpleItem(R.drawable.ic_file_home, getString(R.string.mainStartDrawerCatalogItemHome), ID_HOME))
            add(StartActionSimpleItem(R.drawable.ic_file_phone, getString(R.string.mainStartDrawerCatalogItemInternalStorage), ID_INTERNAL_STORAGE))
            add(StartActionSimpleItem(R.drawable.ic_file_setting, getString(R.string.mainStartDrawerSetting), StartActionBaseItem.ID_SETTING))
            add(StartActionSimpleItem(R.drawable.ic_file_info, getString(R.string.mainStartDrawerInfo), StartActionBaseItem.ID_ABOUT))
            add(StartActionSimpleItem(R.drawable.ic_file_exit, getString(R.string.mainStartDrawerExit), StartActionBaseItem.ID_EXIT))
        }
    }
    private val mFragmentPagerAdapter by lazy { MainPagerAdapter() }
    private val mFragmentList by lazy { ArrayList<BaseMainActivityFragment>() }
    private val mViewPagerPageChangeListener by lazy { object : ViewPager.OnPageChangeListener{
        override fun onPageScrollStateChanged(p0: Int) {
        }

        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
        }

        override fun onPageSelected(p0: Int) {
            refreshTopWindowText()
        }

    } }
    private val mWindowManagerDialog by lazy {
        /* 窗口管理器的列表 */
        class WindowManagerViewHolder(parent:ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_main_window_manager, parent, false)){
            val imageClose = itemView.findViewById<ImageView>(R.id.itemMainWindowManagerClose)
            val textTitle = itemView.findViewById<TextView>(R.id.itemMainWindowManagerTitle)
            val imageIcon = itemView.findViewById<ImageView>(R.id.itemMainWindowManagerIcon)

            fun initView(icon: Int, title:String){
                imageIcon.setImageResource(icon)
                textTitle.text = title
            }
        }
        class WindowManagerAdapter(val dialog:SimpleDialog) : RecyclerView.Adapter<WindowManagerViewHolder>(){
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): WindowManagerViewHolder {
                return WindowManagerViewHolder(p0)
            }

            override fun getItemCount(): Int {
                return mFragmentList.size
            }

            override fun onBindViewHolder(p0: WindowManagerViewHolder, p1: Int) {
                val fragment = mFragmentList[p1]
                p0.initView(fragment.getIcon(), fragment.getTitle())
                p0.itemView.setOnClickListener {
                    onJumpFragmentClick(p0.adapterPosition)
                }
                p0.imageClose.setOnClickListener {
                    onCloseFragmentClick(p0.adapterPosition)
                }
            }

            fun onJumpFragmentClick(pos:Int){
                mainViewPager.setCurrentItem(pos, true)
                dialog.dismiss()
            }

            fun onCloseFragmentClick(pos:Int){
                if(mFragmentList.size <= 1){
                    toast(getString(R.string.mainToastWindowManagerAlreadyLastWindow))
                    return
                }
                mFragmentList.removeAt(pos)
                mFragmentPagerAdapter.notifyDataSetChanged()
                notifyDataSetChanged()
                dialog.dismiss()
                refreshTopWindowText()
            }

        }

        val dialog = object : SimpleDialog(this@MainActivity, SimpleDialog.DIRECTION_CENTER, SimpleDialog.TYPE_CUSTOM_VIEW){
            lateinit var windowList : RecyclerView
            private val mWindowManagerAdapter by lazy { WindowManagerAdapter(this) }

            override fun initCustomView(customView: View) {
                super.initCustomView(customView)
                windowList = customView as RecyclerView
                // 初始化列表
                windowList.layoutManager = LinearLayoutManager(this@MainActivity)
                windowList.adapter = mWindowManagerAdapter
            }


        }
            .setTitle(getString(R.string.mainTitleWindowManager))
            .setCustomView(layoutInflater.inflate(R.layout.dialog_main_window_manager, null))
            .setPosButton(getString(R.string.baseDialogPositiveButtonText), object :SimpleDialog.OnDialogButtonClickListener{
                override fun onButtonClick(dialog: SimpleDialog) {
                    dialog.dismiss()
                }
            })
        dialog
    }
    private var isShowFloatingActionButton = false
    private var mPrevBackTime : Long = 0
    private val mBackPressedTimeMax by lazy { mConfig.getMainConfirmOnExitDelay() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFloatActionButton()
        initRightDrawer()
        initLeftDrawer()
        initViewPager()
        initTitle()
    }

    private fun initTitle(){
        mainTitle.setOnClickListener {
            mWindowManagerDialog.show()
        }
        refreshTopWindowText()
    }
    private fun initFloatActionButton(){
        mainFloatActionButton.hide()
        mainFloatActionButton.setOnClickListener {
            isShowFloatingActionButton = false
            onClickFloatActionButton()
        }
    }
    private fun initRightDrawer(){
        // 初始化操作列表部分
        mainEndDrawerOperationDrawerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mainEndDrawerOperationDrawerView.adapter = mOperationAdapter
        // 初始化文件列表
        mainEndDrawerFileRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true).apply {
            stackFromEnd = true
        }
        mainEndDrawerFileRecyclerView.adapter = mDrawerEndFileAdapter
    }
    private fun initLeftDrawer(){
        // 初始化页面标签
        mainStartDrawerList.layoutManager = LinearLayoutManager(this)
        mainStartDrawerList.adapter = mDrawerStartAdapter
    }
    private fun initViewPager(){
        mainViewPager.offscreenPageLimit = Int.MAX_VALUE
        mFragmentList.add(HomeFragment())
        mFragmentList.add(FileFragment())
        mFragmentList.add(ApplicationFragment())
        mainViewPager.adapter = mFragmentPagerAdapter
        mFragmentPagerAdapter.notifyDataSetChanged()
        mainViewPager.addOnPageChangeListener(mViewPagerPageChangeListener)
    }

    private fun hideStartDrawer(){
        mainDrawerLayout.closeDrawer(Gravity.START)
    }
    private fun refreshTopWindowText(){
        val rootTextSpanBuilder = SpannableStringBuilder()
        var startPos = -1
        var endPos = -1
        mFragmentList.forEachIndexed { index, fragment ->
            rootTextSpanBuilder.append(" ") // 分割符
            if (index == mainViewPager.currentItem){ // 如果是选择上的
                startPos = rootTextSpanBuilder.length
                val title = fragment.getTitle()
                rootTextSpanBuilder.append(title)
                endPos = rootTextSpanBuilder.length
            }else{ // 如果是没有选择上的
                rootTextSpanBuilder.append(fragment.getTitle())
            }
            rootTextSpanBuilder.append(" ") // 分割符
        }
        if (startPos <= -1 || endPos <= -1){
            Logger.logE("MainActivity获取窗口名称时没有找到当前页面的Fragment，可能是没有Fragment导致的")
            return
        }
        rootTextSpanBuilder.setSpan(ForegroundColorSpan(getColor(R.color.textThemeAlpha50)), startPos, endPos, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        mainTitle.text = rootTextSpanBuilder
    }


    override fun onBackPressed() {
        val fragment = mFragmentList[mainViewPager.currentItem]
        if (!fragment.onBackPressed()){
            activityOnBackPressed()
        }
    }
    private fun activityOnBackPressed(){
        val currentTime = System.currentTimeMillis()
        if(mConfig.getConfirmOnExit()){
            if (currentTime - mPrevBackTime > mBackPressedTimeMax){
                toast(getString(R.string.mainToastConfirmOnBackPressed))
                mPrevBackTime = currentTime
            }else{
                super.onBackPressed()
            }
        }else{
            super.onBackPressed()
        }
    }

    /**
     * 供Fragment调用的
     */
    fun fragmentCopyAppendFile(filePathArray:ArrayList<String>){
        filePathArray.forEach { fragmentAddFile(EndFileItem.OPERATION_COPY, it) }
        fragmentAfterAddFile()
    }
    fun fragmentCutAppendFile(filePathArray:ArrayList<String>){
        filePathArray.forEach { fragmentAddFile(EndFileItem.OPERATION_CUT, it) }
        fragmentAfterAddFile()
    }
    fun fragmentCopyFile(filePathArray:ArrayList<String>){
        fragmentClearFile()
        filePathArray.forEach { fragmentAddFile(EndFileItem.OPERATION_COPY, it) }
        fragmentAfterAddFile()
    }
    fun fragmentCutFile(filePathArray:ArrayList<String>){
        fragmentClearFile()
        filePathArray.forEach { fragmentAddFile(EndFileItem.OPERATION_CUT, it) }
        fragmentAfterAddFile()
    }
    private fun fragmentAddFile(operation:Int, filePath:String){
        val tmpFile = File(filePath)
        if (!tmpFile.exists()){
            return
        }
        val tmpItem = EndFileItem(tmpFile.path, tmpFile.name, operation, tmpFile.getTypeIconID())
        // 如果重复，则替换
        var tmpPos = -1
        mDrawerEndFileList.forEachIndexed { index, endFileItem ->
            if (endFileItem.path == tmpItem.path){
                tmpPos = index
                return@forEachIndexed
            }
        }
        // 添加数据并刷新界面
        if (tmpPos >= 0){
            mDrawerEndFileList[tmpPos] = tmpItem
            mDrawerEndFileAdapter.notifyItemChanged(tmpPos)
        }else{
            mDrawerEndFileList.add(tmpItem)
            mDrawerEndFileAdapter.notifyItemInserted(mDrawerEndFileList.size-1)
        }
    }
    private fun fragmentAfterAddFile(){
        mainFloatActionButton.show()
    }
    private fun fragmentClearFile(){
        mDrawerEndFileList.clear()
    }

    /**
     *  一系列操作
     */
    fun onClickFloatActionButton(){
        mDrawerEndFileList.forEach { item ->
            val currentFragment = mFragmentList[mainViewPager.currentItem]
            if (currentFragment is FileFragment){
                val newPath = currentFragment.getCurrentPath()
                val file = File(item.path)
                when(item.operation){
                    EndFileItem.OPERATION_DELETE -> {
                        file.delete()
                    }
                    EndFileItem.OPERATION_CUT -> {
                        val newFile = File("$newPath/${item.name}")
                        if (!newFile.exists()){
                            file.renameTo(newFile)
                        }else{
                            // 移动失败，文件已存在
                        }
                    }
                    EndFileItem.OPERATION_COPY -> {
                        val newFile = File("$newPath/${item.name}")
                        if (!newFile.exists()){
                            file.copyTo(newFile)
                        }else{
                            // 复制失败，文件已存在
                        }
                    }
                }
            }else{
                Logger.logE("所粘贴的Fragment不是FileFragment，还没有做相应处理")
            }
        }
        // 弹出提示
        toast(getString(R.string.toastMainOperationFinish))
        // 更新显示
        mDrawerEndFileList.clear()
        mDrawerEndFileAdapter.notifyDataSetChanged()
        // 隐藏按钮
        mainFloatActionButton.hide()
    }

    /**
     *  右边Drawer相关
     */
    class EndOperationItem(val icon:Int, val name:String, val id:Int= UNDEFINE){
        companion object {
            const val UNDEFINE = -1
            const val PASTE_ALL = 0
            const val CUT_ALL = 1
            const val DELETE_ALL = 2
            const val CLEAR_ALL = 3
        }
    }
    class EndFileItem(val path:String, val name:String, var operation:Int, val iconID:Int){
        companion object {
            const val OPERATION_CUT = 1
            const val OPERATION_COPY = 0
            const val OPERATION_DELETE = 2
        }
    }
    inner class EndDrawerOperationAdapter : RecyclerView.Adapter<EndDrawerOperationViewHolder>(){
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int):EndDrawerOperationViewHolder {
            return EndDrawerOperationViewHolder(p0)
        }

        override fun getItemCount(): Int {
            return mOperationActionList.size
        }

        override fun onBindViewHolder(holder: EndDrawerOperationViewHolder, pos: Int) {
            val tmpItem = mOperationActionList[pos]
            holder.setItem(tmpItem.icon, tmpItem.name)
            holder.itemView.setOnClickListener{
                when(tmpItem.id){
                    EndOperationItem.CLEAR_ALL -> {
                        mDrawerEndFileList.clear()
                        onClickFloatActionButton()
                    }
                    EndOperationItem.PASTE_ALL -> {
                        mDrawerEndFileList.forEach {
                            it.operation = EndFileItem.OPERATION_COPY
                        }
                        onClickFloatActionButton()
                    }
                    EndOperationItem.CUT_ALL -> {
                        mDrawerEndFileList.forEach {
                            it.operation = EndFileItem.OPERATION_CUT
                        }
                        onClickFloatActionButton()
                    }
                    EndOperationItem.DELETE_ALL -> {
                        mMessageDialog.setTitle(getString(R.string.textMainDialogTitleDeleteFiles))
                        mMessageDialog.setMessage(getString(R.string.textMainDialogMessageDeleteFiles))
                        mMessageDialog.setPosButton(getString(R.string.baseDialogPositiveButtonText), object : SimpleDialog.OnDialogButtonClickListener{
                            override fun onButtonClick(dialog: SimpleDialog) {
                                mDrawerEndFileList.forEach {
                                    it.operation = EndFileItem.OPERATION_DELETE
                                }
                                onClickFloatActionButton()
                                dialog.dismiss()
                            }
                        })
                        mMessageDialog.setNegButton(getString(R.string.baseDialogNegativeButtonText), object : SimpleDialog.OnDialogButtonClickListener{
                            override fun onButtonClick(dialog: SimpleDialog) {
                                dialog.dismiss()
                            }
                        })
                        mMessageDialog.show()
                    }
                }
                mainDrawerLayout.closeDrawer(Gravity.END)
            }
        }
    }
    inner class EndDrawerOperationViewHolder(parent:ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_main_end_drawer_operation_list,parent,false)){
        private val imageIcon = itemView.findViewById<ImageView>(R.id.itemMainEndDrawerOperationIcon)
        private val nameText = itemView.findViewById<TextView>(R.id.itemMainEndDrawerOperationName)

        fun setItem(icon:Int, name:String){
            imageIcon.setImageResource(icon)
            nameText.text = name
        }
    }
    inner class EndDrawerFileViewHolder(parent: ViewGroup):RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_main_end_drawer_file,parent,false)){
        val imageIcon = itemView.findViewById<ImageView>(R.id.itemMainEndDrawerFileIcon)
        val textTitle = itemView.findViewById<TextView>(R.id.itemMainEndDrawerFileTitle)
        val textOperation = itemView.findViewById<TextView>(R.id.itemMainEndDrawerFileOperation)
        val textPath = itemView.findViewById<TextView>(R.id.itemMainEndDrawerFilePath)

        fun initView(item:EndFileItem){
            imageIcon.setImageResource(item.iconID)
            textTitle.text = item.name
            textPath.text = item.path
            textOperation.text = when(item.operation){
                EndFileItem.OPERATION_COPY -> {
                    getString(R.string.textMainEndDrawerFileOperationCopy)
                }
                EndFileItem.OPERATION_CUT -> {
                    getString(R.string.textMainEndDrawerFileOperationCut)
                }
                else -> {
                    getString(R.string.textMainEndDrawerFileOperationCopy)
                }
            }
        }
    }
    inner class EndDrawerFileAdapter : RecyclerView.Adapter<EndDrawerFileViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, p1: Int): EndDrawerFileViewHolder {
            return EndDrawerFileViewHolder(parent)
        }

        override fun getItemCount(): Int {
            return mDrawerEndFileList.size
        }

        override fun onBindViewHolder(holder: EndDrawerFileViewHolder, pos: Int) {
            val item = mDrawerEndFileList[pos]
            holder.initView(item)
        }

    }

    /**
     *  左边Drawer相关
     */
    abstract class StartActionBaseItem(val name:String, val id:Int){
        companion object {
            const val ID_UNDEFINE = 0
            const val ID_EXIT = 1
            const val ID_SETTING = 2
            const val ID_ABOUT = 3
            const val ID_INTERNAL_STORAGE = 4
            const val ID_HOME = 5
        }
    }
    class StartActionCatalogItem(val icon:Int,name:String, var state:Boolean, id:Int=StartActionBaseItem.ID_UNDEFINE):StartActionBaseItem(name, id)
    class StartActionSimpleItem(val icon:Int, name:String, id:Int=StartActionBaseItem.ID_UNDEFINE):StartActionBaseItem(name, id)

    inner class StartDrawerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        val TYPE_SIMPLE = 0
        val TYPE_CATELOG = 1
        val TYPE_UNDEFINE = -1

        override fun getItemViewType(position: Int): Int {
            return when(mDrawerStartItemList[position]){
                is StartActionCatalogItem -> TYPE_CATELOG
                is StartActionSimpleItem -> TYPE_SIMPLE
                else -> TYPE_UNDEFINE
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
            return when(type){
                TYPE_SIMPLE -> StartDrawerSimpleItemViewHolder(parent)
                TYPE_CATELOG -> StartDrawerCatalogViewHolder(parent)
                else -> { StartDrawerCatalogViewHolder(parent) }
            }
        }

        override fun getItemCount(): Int {
            return mDrawerStartItemList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
            val tmpItem = mDrawerStartItemList[pos]
            when(holder){
                is StartDrawerCatalogViewHolder -> {
                    val item = tmpItem as StartActionCatalogItem
                    holder.setState(item.icon, item.name, item.state)
                }
                is StartDrawerSimpleItemViewHolder -> {
                    val item = tmpItem as StartActionSimpleItem
                    holder.initView(item)
                }
            }
            holder.itemView.setOnClickListener {
                when(tmpItem.id){
                    ID_SETTING -> {
                        startActivity(SettingActivity::class.java)
                        hideStartDrawer()
                    }
                    ID_ABOUT -> {
                        startActivity(AboutActivity::class.java)
                        hideStartDrawer()
                    }
                    ID_EXIT -> {
                        FileApplication.getInstance().exit()
                    }
                    ID_HOME -> {
                        mFragmentList.add(HomeFragment())
                        mFragmentPagerAdapter.notifyDataSetChanged()
                        hideStartDrawer()
                        refreshTopWindowText()
                    }
                    ID_INTERNAL_STORAGE -> {
                        mFragmentList.add(FileFragment())
                        mFragmentPagerAdapter.notifyDataSetChanged()
                        hideStartDrawer()
                        refreshTopWindowText()
                    }
                }
            }
        }

    }
    inner class StartDrawerCatalogViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_main_start_drawer_catalog,parent,false)){
        private val imageIcon = itemView.findViewById<ImageView>(R.id.itemMainStartDrawerCatalogTypeIcon)
        private val textName = itemView.findViewById<TextView>(R.id.itemMainStartDrawerCatalogName)
        private val imageState = itemView.findViewById<ImageView>(R.id.itemMainStartDrawerCatalogStateIcon)

        fun setState(iconImage:Int, name:String, isOpen:Boolean){
            imageIcon.setImageResource(iconImage)
            textName.text = name
            imageState.rotation = if (isOpen){
                0f
            }else{
                270f
            }
        }
    }
    inner class StartDrawerCatalogItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_main_start_drawer_catalog_item,parent,false)){
        val textName = itemView as TextView

        fun setText(text:String){
            textName.text = text
        }
    }
    inner class StartDrawerCatalogSwitchViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_main_start_drawer_catalog_switch, parent, false)){
        val imageIcon = itemView.findViewById<ImageView>(R.id.itemMainStartDrawerItemTypeIcon)
        val nameText = itemView.findViewById<TextView>(R.id.itemMainStartDrawerItemName)
        val stateCheckBox = itemView.findViewById<CheckBox>(R.id.itemMainStartDrawerItemCheckbox)

    }
    inner class StartDrawerSimpleItemViewHolder(parent:ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_main_start_drawer_simple, parent, false)){
        val imageIcon = itemView.findViewById<ImageView>(R.id.itemMainStartDrawerSimpleIcon)
        val textName = itemView.findViewById<TextView>(R.id.itemMainStartDrawerSimpleName)

        fun initView(item:StartActionSimpleItem){
            imageIcon.setImageResource(item.icon)
            textName.text = item.name
        }

    }

    /* ViewPager */
    inner class MainPagerAdapter : FragmentStatePagerAdapter(supportFragmentManager){
        override fun getItem(p0: Int): Fragment {
            return mFragmentList[p0]
        }


        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

    }

}

