package creepersan.com.cfilemanager.application

import android.app.Activity
import android.app.Application
import android.os.Bundle
import creepersan.com.cfilemanager.activity.MainActivity
import creepersan.com.cfilemanager.base.BaseActivity
import creepersan.com.cfilemanager.util.PrefHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/** 主要管理的Application
 * Created by CreeperSan on 2017/11/11.
 */
class ManageApplication : Application(){
    private var stackTopActivity : BaseActivity? = null

    override fun onCreate() {
        super.onCreate()
        initStackTopActivity()
        initEventBus()
        initSharePref()
    }

    /**
     *  初始化
     */
    private fun initEventBus(){
        EventBus.getDefault().register(this)
    }
    private fun initSharePref(){
        PrefHelper.init(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCommand(command:String){

    }

    /**
     *  栈顶Activity相关方法<br>
     *  调用获取StackActivity的时候记得检查hasActivity
     */
    private fun initStackTopActivity() {
        registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks{
            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityResumed(activity: Activity) {
                stackTopActivity = activity as BaseActivity
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                if (activity is MainActivity){
                    stackTopActivity = null
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            }


        })
    }
    fun hasActivity():Boolean{
        return stackTopActivity == null
    }
    fun getStackTopActivity():BaseActivity{
        return stackTopActivity!!
    }
}