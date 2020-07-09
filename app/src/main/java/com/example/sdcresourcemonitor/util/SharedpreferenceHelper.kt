package com.example.sdcresourcemonitor.util

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

class SharedpreferenceHelper {
    companion object {
        private const val PREF_TIME = "Pref_Time"
        private const val PREF_TIME_LIST = "Pref_Time"
        private const val PREF_TRACKER_TIME = "Pref_Tracker_Time"
        private const val PREF_TRACKER_PERIOD = "Pref_Tracker_Period"
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

    fun saveUpdateTrackerTimes(time : Map<String,String>) {
        val jasonObject = JSONObject(time)
        pref?.edit()?.putString(PREF_TRACKER_TIME,jasonObject.toString())?.apply()
    }

    fun saveUpdateTrackerPeriods(time : Map<String,String>) {
        val jasonObject = JSONObject(time)
        pref?.edit()?.putString(PREF_TRACKER_PERIOD,jasonObject.toString())?.apply()
    }

    fun getUpdatedTrackerTimes() : HashMap<String,String> {
        val timeMap : HashMap<String,String> = HashMap()
        val outString = pref?.getString(PREF_TRACKER_TIME,"")
        val jsonObject = JSONObject(outString!!)
        val keys = jsonObject.keys()
        while(keys.hasNext()) {
            val key = keys.next().toString()
            val value :String  = jsonObject.get(key).toString()
            timeMap[key] = value
        }
        return timeMap
    }

}