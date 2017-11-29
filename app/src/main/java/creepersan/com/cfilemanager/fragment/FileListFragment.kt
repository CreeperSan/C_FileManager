package creepersan.com.cfilemanager.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import creepersan.com.cfilemanager.R
import creepersan.com.cfilemanager.base.BaseTabFragment
import creepersan.com.cfilemanager.bean.FileBean
import creepersan.com.cfilemanager.bean.PathItem
import creepersan.com.cfilemanager.callback.DialogCreateFileCallback
import creepersan.com.cfilemanager.views.holder.SimpleTextHolder
import kotlinx.android.synthetic.main.fragment_file.*
import java.io.File

/** 文件列表fragment
 * Created by CreeperSan on 2017/11/8.
 */
class FileListFragment() : BaseTabFragment(){
    private lateinit var pathLayoutManager: LinearLayoutManager
    private lateinit var pathAdapter : PathAdapter
    private lateinit var fileLayoutManager: LinearLayoutManager
    private lateinit var fileAdapter : FileAdapter
    private val pathList = ArrayList<PathItem>()
    private lateinit var anime0to135 : Animation
    private lateinit var anime135to0 : Animation
    private lateinit var animeScaleIn : Animation
    private lateinit var animeScaleOut : Animation
    private lateinit var animeAlphaIn : Animation
    private lateinit var animeAlphaOut : Animation
    private lateinit var animeLabelIn : Animation
    private lateinit var animeLabelOut : Animation
    private var isFloatingActionMenuOpen = false
    //状态型变量
    private var isAnimating = false
    //标志性变量
    private var flagRootPath = RootPath.EXTERNAL
    //实际需要使用的变量
    private val displayList = ArrayList<FileBean>()

    override fun getLayoutID(): Int = R.layout.fragment_file

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        if (args!=null){
            flagRootPath = args.getInt(Argument.ROOT_PATH,RootPath.EXTERNAL)
        }else{
            flagRootPath = RootPath.EXTERNAL
        }
    }

    /**
     *  生命周期
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initAnime()
        initPathRecyclerView()
        initFilePath()
        initFileList()
        initFileRecyclerView()
        initFloatingActionButton()
    }
    override fun onResume() {
        super.onResume()
        if(isFloatingActionMenuOpen){
            showViews()
        }else{
            hideViews()
        }
    }

    /**
     *  此处进行初始化
     */
    private fun initAnime(){
        //这里是初始化动画
        anime0to135 = activity().loadAnimation(R.anim.in_floating_button_main_button)
        anime135to0 = activity().loadAnimation(R.anim.out_floating_button_main_button)
        animeAlphaIn = activity().loadAnimation(R.anim.in_floating_button_background)
        animeAlphaOut = activity().loadAnimation(R.anim.out_floating_button_background)
        animeScaleIn = activity().loadAnimation(R.anim.in_floating_button_sub_button)
        animeScaleOut = activity().loadAnimation(R.anim.out_floating_button_sub_button)
        animeLabelIn = activity().loadAnimation(R.anim.in_floating_button_label)
        animeLabelOut = activity().loadAnimation(R.anim.out_floating_button_label)
        //这里是初始化动画结束后的回调事件
        //打开菜单的动画
        animeAlphaIn.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {
                showViews()
                fragmentFileFloatingActionButton.rotation = 135f
            }
            override fun onAnimationEnd(animation: Animation?) {
            }
        })
        //关闭菜单的动画
        animeAlphaOut.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {
                fragmentFileFloatingActionButton.rotation = 0f
            }
            override fun onAnimationEnd(animation: Animation?) {
                hideViews()
            }
        })
    }
    private fun initPathRecyclerView(){
        pathLayoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        pathAdapter = PathAdapter()
        fragmentFilePathRecyclerView.layoutManager = pathLayoutManager
        fragmentFilePathRecyclerView.adapter = pathAdapter
    }
    private fun initFilePath(){
        pathList.clear()
        when(flagRootPath){
            RootPath.ROOT -> {pathList.add(PathItem(R.string.pathRoot,"/",context))}
            RootPath.EXTERNAL -> {pathList.add(PathItem(R.string.pathExternal, Environment.getExternalStorageDirectory().absolutePath,context))}
            RootPath.SD_CARD -> {pathList.add(PathItem(R.string.pathSDCard, Environment.getExternalStorageDirectory().absolutePath,context))}  //TODO SD卡识别尚未添加
            RootPath.OTG -> {pathList.add(PathItem(R.string.pathOTG, Environment.getExternalStorageDirectory().absolutePath,context))}  //TODO OTG识别尚未添加
            else -> {pathList.add(PathItem(R.string.pathExternal, Environment.getExternalStorageDirectory().absolutePath,context))}  //默认返回手机存储目录
        }
    }
    private fun initFileList() {
        val filePathBuilder = StringBuilder()
        pathList.forEach{
            filePathBuilder.append("${it.value}/")
        }
        val filePath = filePathBuilder.toString()
        val file = File(filePath)
        displayList.clear()
        if (file.exists() && file.isDirectory){
            val contentFileList = file.listFiles()
            contentFileList.forEach {
                displayList.add(FileBean(it))
            }
        }else{
            showAsLoading(getString(R.string.filePathNotExist))
        }
    }
    private fun initFileRecyclerView(){
        fileLayoutManager = LinearLayoutManager(context)
        fileAdapter = FileAdapter()
        fragmentFileFileRecyclerView.layoutManager = fileLayoutManager
        fragmentFileFileRecyclerView.adapter = fileAdapter
    }
    private fun initFloatingActionButton(){
        fragmentFileFloatingActionButton.setOnClickListener { v->
            if (isFloatingActionMenuOpen){//关闭
                hideViews()
                startCloseAnime()
            }else{//打开
                showViews()
                startOpenAnime()
            }
            isFloatingActionMenuOpen = !isFloatingActionMenuOpen
        }
        fragmentFileFloatingActionButtonFolder.setOnClickListener {
            startCloseAnime()
            isFloatingActionMenuOpen = false
        }
        fragmentFileFloatingActionButtonFile.setOnClickListener {
            activity().showCreateFileDialog(object : DialogCreateFileCallback(){
                override fun onCommit(name: String, selection: Int) {
                    super.onCommit(name, selection)
                    log("文件名为 ${name} 后缀标志为 ${selection}")

                }
            })
            startCloseAnime()
            isFloatingActionMenuOpen = false
        }
        fragmentFileMenuBackground.setOnClickListener {
            startCloseAnime()
            isFloatingActionMenuOpen = false
        }
    }

    /**
     *  内部类
     */
    private inner class PathAdapter : RecyclerView.Adapter<SimpleTextHolder>(){
        override fun getItemCount(): Int = pathList.size

        override fun onBindViewHolder(holder: SimpleTextHolder, position: Int) {
            holder.showAsPath()
            holder.setPathValue(pathList[position].name, View.OnClickListener {
                val pos = holder.adapterPosition + 1
                val max = pathList.size
                if (pos < max){
                    for (i in pos until max){
                        pathList.removeAt(pathList.size - 1)
                    }
                    pathAdapter.notifyDataSetChanged()
                    initFileList()
                    fileAdapter.notifyDataSetChanged()
                }
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SimpleTextHolder
            = SimpleTextHolder(context,parent)

    }
    private inner class FileDetailViewHolder(parent:ViewGroup) : RecyclerView.ViewHolder(activity().getView(R.layout.item_file_detail,parent)) {
        val icon = itemView.findViewById<ImageView>(R.id.itemFileDetailIcon)
        val title = itemView.findViewById<TextView>(R.id.itemFileDetailTitle)
        val content = itemView.findViewById<TextView>(R.id.itemFileDetailContent)
    }
    private inner class FileAdapter : RecyclerView.Adapter<FileDetailViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileDetailViewHolder {
            return FileDetailViewHolder(parent)
        }

        override fun onBindViewHolder(holder: FileDetailViewHolder, position: Int) {
            val fileItem = displayList[position]
            holder.title.text = fileItem.displayName
            holder.content.text = "${if (fileItem.fileType==FileBean.FileType.FOLDER) fileItem.innerItemCounts else fileItem.displaySize}  ${fileItem.modifyTime}"
            holder.icon.alpha = if (fileItem.isHidden) 0.5f else 1f
            when(fileItem.fileType){
                FileBean.FileType.FOLDER -> {
                    holder.icon.setImageResource(R.drawable.ic_folder)
                }
                FileBean.FileType.MUSIC -> {
                    holder.icon.setImageResource(R.drawable.ic_music)
                }
                FileBean.FileType.VIDEO -> {
                    holder.icon.setImageResource(R.drawable.ic_video)
                }
                FileBean.FileType.IMAGE -> {
                    holder.icon.setImageResource(R.drawable.ic_image)
                }
                FileBean.FileType.APK -> {
                    holder.icon.setImageResource(R.drawable.ic_apk)
                }
                FileBean.FileType.FILE -> {
                    holder.icon.setImageResource(R.drawable.ic_file)
                }
                else -> {
                    holder.icon.setImageResource(R.drawable.ic_file)
                }
            }
            holder.itemView.setOnClickListener{
                if (fileItem.fileType == FileBean.FileType.FOLDER){
                    pathList.add(PathItem(fileItem.displayName))
                    initFileList()
                    fileAdapter.notifyDataSetChanged()
                    pathAdapter.notifyDataSetChanged()
                }else{
                    when(fileItem.fileType){
                        FileBean.FileType.IMAGE -> {
                            openImageFile(fileItem,holder.adapterPosition)
                        }
                        FileBean.FileType.MUSIC -> {
                            openMusicFile(fileItem,holder.adapterPosition)
                        }
                        FileBean.FileType.VIDEO -> {
                            openVideoFile(fileItem,holder.adapterPosition)
                        }
                        FileBean.FileType.DOCUMENT -> {
                            openDocumentFile(fileItem,holder.adapterPosition)
                        }
                        FileBean.FileType.APK -> {
                            openApkFile(fileItem,holder.adapterPosition)
                        }
                        FileBean.FileType.ZIP -> {
                            openZIPFile(fileItem,holder.adapterPosition)
                        }
                        else -> {
                            openFile(fileItem,holder.adapterPosition)
                        }
                    }
                }
            }
        }

        /**
         *  打开文件
         */
        fun openMusicFile(fileItem: FileBean,position: Int){
            val intent = Intent(android.content.Intent.ACTION_VIEW)
            if (activity().isVersionOverN()){
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                val uri = FileProvider.getUriForFile(context,"creepersan.com.cfilemanager.fileprovider",File(fileItem.path))
                intent.setDataAndType(uri, "audio/*");
            }else{
                val uri = Uri.parse("content:/${fileItem.path}")
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uri,"audio/*")
            }
            startActivity(intent)
        }
        fun openVideoFile(fileItem: FileBean,position: Int){
            val intent = Intent(android.content.Intent.ACTION_VIEW)
            if (activity().isVersionOverN()){
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                val uri = FileProvider.getUriForFile(context,"creepersan.com.cfilemanager.fileprovider",File(fileItem.path))
                intent.setDataAndType(uri, "video/*");
            }else{
                val uri = Uri.parse("content:/${fileItem.path}")
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uri,"video/*")
            }
            startActivity(intent)
        }
        fun openImageFile(fileItem: FileBean,position: Int){
            val intent = Intent(android.content.Intent.ACTION_VIEW)
            if (activity().isVersionOverN()){
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                val uri = FileProvider.getUriForFile(context,"creepersan.com.cfilemanager.fileprovider",File(fileItem.path))
                intent.setDataAndType(uri, "image/*");
            }else{
                val uri = Uri.parse("content:/${fileItem.path}")
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uri,"image/*")
            }
            startActivity(intent)
        }
        fun openApkFile(fileItem: FileBean,position: Int){
            val intent = Intent(android.content.Intent.ACTION_VIEW)
            if(activity().isVersionOverN()){
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                val uri = FileProvider.getUriForFile(context,"creepersan.com.cfilemanager.fileprovider",File(fileItem.path))
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            }else{
                val uri = Uri.parse("content:/${fileItem.path}")
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uri,"application/vnd.android.package-archive")
            }
            startActivity(intent)
        }
        fun openFile(fileItem: FileBean,position: Int){
            val intent = Intent(android.content.Intent.ACTION_VIEW)
            if (activity().isVersionOverN()){
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                val uri = FileProvider.getUriForFile(context,"creepersan.com.cfilemanager.fileprovider",File(fileItem.path))
                intent.setDataAndType(uri, "*/*");
            }else{
                val uri = Uri.parse("content:/${fileItem.path}")
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uri,"*/*")
            }
            startActivity(intent)
        }
        fun openDocumentFile(fileItem: FileBean,position: Int){
            val intent = Intent(android.content.Intent.ACTION_VIEW)
            if (activity().isVersionOverN()){
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                val uri = FileProvider.getUriForFile(context,"creepersan.com.cfilemanager.fileprovider",File(fileItem.path))
                intent.setDataAndType(uri, "text/*");
            }else{
                val uri = Uri.parse("content:/${fileItem.path}")
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uri,"text/*")
            }
            startActivity(intent)
        }
        fun openZIPFile(fileItem: FileBean,position: Int){
            val intent = Intent(android.content.Intent.ACTION_VIEW)
            if (activity().isVersionOverN()){
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                val uri = FileProvider.getUriForFile(context,"creepersan.com.cfilemanager.fileprovider",File(fileItem.path))
                intent.setDataAndType(uri, "application/x-gzip");
            }else{
                val uri = Uri.parse("content:/${fileItem.path}")
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uri,"application/x-gzip")
            }
            startActivity(intent)
        }

        override fun getItemCount(): Int = displayList.size

    }

    /**
     * 切换显示样式
     */
    private fun showAsLoading(loadingHint:String? = null){
        if (loadingHint!=null){
            fragmentFilePathLoadingText.text = loadingHint
        }
        fragmentFileFileRecyclerView.visibility = View.GONE
        fragmentFileLoadingLayout.visibility = View.VISIBLE
    }
    private fun showAsList(){
        fragmentFileFileRecyclerView.visibility = View.VISIBLE
        fragmentFileLoadingLayout.visibility = View.GONE
    }

    /**
     *  标志
     */
    object Argument{
        val ROOT_PATH = "RootPath"
    }
    object RootPath{
        val ROOT = 0
        val EXTERNAL = 1
        val SD_CARD = 2
        val OTG = 3
    }

    /**
     *  界面上的操作
     */
    private fun startOpenAnime(){//显示
        fragmentFileFloatingActionButton.startAnimation(anime0to135)
        fragmentFileMenuBackground.startAnimation(animeAlphaIn)
        fragmentFileFloatingActionButtonFile.startAnimation(animeScaleIn)
        fragmentFileFloatingActionButtonFolder.startAnimation(animeScaleIn)
        fragmentFileFloatingActionButtonFolderLabel.startAnimation(animeLabelIn)
        fragmentFileFloatingActionButtonFileLabel.startAnimation(animeLabelIn)
    }
    private fun startCloseAnime(){//隐藏
        fragmentFileFloatingActionButton.startAnimation(anime135to0)
        fragmentFileMenuBackground.startAnimation(animeAlphaOut)
        fragmentFileFloatingActionButtonFile.startAnimation(animeScaleOut)
        fragmentFileFloatingActionButtonFolder.startAnimation(animeScaleOut)
        fragmentFileFloatingActionButtonFolderLabel.startAnimation(animeLabelOut)
        fragmentFileFloatingActionButtonFileLabel.startAnimation(animeLabelOut)
    }
    private fun showViews(){
        fragmentFileMenuBackground.visibility = View.VISIBLE
        fragmentFileFloatingActionButtonFolderLabel.visibility = View.VISIBLE
        fragmentFileFloatingActionButtonFolder.visibility = View.VISIBLE
        fragmentFileFloatingActionButtonFileLabel.visibility = View.VISIBLE
        fragmentFileFloatingActionButtonFile.visibility = View.VISIBLE
    }
    private fun hideViews(){
        fragmentFileMenuBackground.visibility = View.GONE
        fragmentFileFloatingActionButtonFolderLabel.visibility = View.GONE
        fragmentFileFloatingActionButtonFolder.visibility = View.GONE
        fragmentFileFloatingActionButtonFileLabel.visibility = View.GONE
        fragmentFileFloatingActionButtonFile.visibility = View.GONE
    }

    override fun onBackPressed():Boolean {
        if (pathList.size<=1){
            return false
        }else{
            pathList.removeAt(pathList.size-1)
            pathAdapter.notifyDataSetChanged()
            initFileList()
            fileAdapter.notifyDataSetChanged()
            return true
        }
    }

}