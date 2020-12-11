package com.example.sdcresourcemonitor.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.sdcresourcemonitor.model.local.User
import org.json.JSONObject

class SharedpreferenceHelper {

    companion object {
        private const val PREF_TIME = "Pref_Time"
        private const val PREF_TIME_LIST = "Pref_Time"
        private const val PREF_TRACKER_TIME = "Pref_Tracker_Time"
        private const val PREF_TRACKER_PERIOD = "Pref_Tracker_Period"

        private const val PREF_USER_DATA = "Pref_User_Data"
//        private const val USER_PROJECTS = "Pref_User_Projects"
//        private const val USER_ROLES = "Pref_User_Roles"
        private const val PREF_AUTHORIZATION_TOKEN = "Pref_Authorization_Token"

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
        Log.i("AlertDashBoard","getUpdatedTrackerTimes called")
        val timeMap : HashMap<String,String> = HashMap()
        val outString = pref?.getString(PREF_TRACKER_TIME,"")
        if(outString.isNullOrEmpty()) return timeMap

        Log.i("AlertDashBoard","$timeMap,$outString")
        val jsonObject = JSONObject(outString)
        val keys = jsonObject.keys()
        Log.i("AlertDashBoard","$timeMap,$outString,$jsonObject,$keys")
        while(keys.hasNext()) {
            val key = keys.next().toString()
            val value :String  = jsonObject.get(key).toString()
            timeMap[key] = value
        }
        return timeMap
    }

    fun saveUpdateUserData(user : String) {
        pref?.edit()?.putString(PREF_USER_DATA,user)?.apply()
    }

    fun saveUpdateAuthorizationToke(token : String){
        pref?.edit()?.putString(PREF_AUTHORIZATION_TOKEN,token)?.apply()
    }

    fun getUpdatedAuthorizationToken() : String {
        return pref?.getString(PREF_AUTHORIZATION_TOKEN,"").toString()

    }

    fun getUpdatedUserData() : User {
        val userJson = pref?.getString(PREF_USER_DATA,"")
        if(userJson.isNullOrEmpty()) return User("","","","")

        val jsonObject = JSONObject(userJson)
        val name = jsonObject.get("name").toString()
        val email = jsonObject.get("email").toString()
        val designation = jsonObject.get("designation").toString()
        val mobile = jsonObject.get("mobile").toString()
        val projects = jsonObject.getJSONArray("projects")
        val roles = jsonObject.getJSONArray("roles")

        val user = User(name,email,designation,mobile)

        val projectList : ArrayList<String> = ArrayList()
        for(i in 0 until projects.length()) {
            projectList.add(i,projects.getString(i))
        }
        user.projects = projectList

        val roleList : ArrayList<String> = ArrayList()
        for(i in 0 until roles.length()) {
            roleList.add(i,roles.getString(i))
        }
        user.roles = roleList

        return user

    }

}