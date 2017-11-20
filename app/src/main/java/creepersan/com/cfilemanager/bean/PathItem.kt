package creepersan.com.cfilemanager.bean

import android.content.Context

/** 文件路径点Item
 * Created by CreeperSan on 2017/11/19.
 */

class PathItem(var name:String,var value:String){
    constructor(value:String):this(value,value)
    constructor(nameID:Int,value: String,context:Context):this(context.getString(nameID),value)
    constructor(nameID:Int,context:Context):this(context.getString(nameID))
}