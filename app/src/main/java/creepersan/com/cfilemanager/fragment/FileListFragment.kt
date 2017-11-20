package creepersan.com.cfilemanager.fragment

import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import creepersan.com.cfilemanager.R
import creepersan.com.cfilemanager.base.BaseTabFragment
import creepersan.com.cfilemanager.bean.FileBean
import creepersan.com.cfilemanager.bean.PathItem
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
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initPathRecyclerView()
        initFilePath()
        initFileList()
        initFileRecyclerView()

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

    /**
     *  内部类
     */
    private inner class PathAdapter : RecyclerView.Adapter<SimpleTextHolder>(){
        override fun getItemCount(): Int = pathList.size

        override fun onBindViewHolder(holder: SimpleTextHolder, position: Int) {
            holder.showAsPath()
            holder.setPathValue(pathList[position].name, View.OnClickListener {

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
            holder.content.text = "${fileItem.innerItemCounts}  ${fileItem.modifyTime}"
            
            holder.itemView.setOnClickListener{

            }
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


}