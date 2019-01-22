package com.creepersan.file.utils

import android.annotation.TargetApi
import android.content.Context
import android.media.AudioManager
import android.os.Build

class VolumeManager(context: Context) {
    companion object {
        private const val VOLUME_UNDEFINE = Int.MIN_VALUE
    }

    private val mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var mVolumeMediaMax = VOLUME_UNDEFINE
    private var mVolumeMediaMin = VOLUME_UNDEFINE

    fun getMediaMaxVolume():Int{
        if (mVolumeMediaMax == VOLUME_UNDEFINE){
            mVolumeMediaMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        }
        return mVolumeMediaMax
    }

    fun getMediaCurrentVolume():Int{
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    fun setMediaVolume(volume:Int, action:((newVolume:Int)->Unit)?=null){
        var tmpVolume = volume
        val max = getMediaMaxVolume()
        val min = getMediaMinVolume()
        if (tmpVolume > max){
            tmpVolume = max
        }else if (tmpVolume < min){
            tmpVolume = min
        }
        val currentVolume = getMediaCurrentVolume()
        if (tmpVolume > currentVolume){
            repeat(tmpVolume-currentVolume){
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
            }
        }else if (tmpVolume < currentVolume){
            repeat(currentVolume-tmpVolume){
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
            }
        }else{
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
        }
        action?.invoke(tmpVolume)
    }


    fun getMediaMinVolume():Int{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            mVolumeMediaMin = mAudioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC)
        }else{
            mVolumeMediaMin = 0
        }
        return mVolumeMediaMin
    }

}