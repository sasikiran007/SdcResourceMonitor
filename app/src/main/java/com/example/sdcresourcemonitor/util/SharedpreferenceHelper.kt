package com.example.sdcresourcemonitor.util

import android.content.Context
import android.content.SharedPreferences

class SharedpreferenceHelper {
    companion object {
        private const val PREF_TIME = "Pref_Time"
        private const val PREF_TIME_LIST = "Pref_Time"
        private var pref: SharedPreferences? = null
        @Volatile
        private var instance : SharedpreferenceHelper? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildSharedPreferenceHelper(context).also {
                instance = it
            }
        }

        private fun buildSharedPreferenceHelper(context: Context)  : SharedpreferenceHelper{
            pref = context.getSharedPreferences("ALERT_SHAREDPREFS",Context.MODE_PRIVATE)
            return SharedpreferenceHelper()
        }
    }

    fun saveUpdateTime(time : Long) {
        pref?.edit()?.putLong(PREF_TIME,time)?.apply()
    }

    fun getUpdatedTime() : Long? =  pref?.getLong(PREF_TIME,0)

    fun saveUpdateTimeList(time : Long) {
        pref?.edit()?.putLong(PREF_TIME_LIST,time)?.apply()
    }

    fun getUpdatedTimeList() : Long? =  pref?.getLong(PREF_TIME_LIST,0)

}