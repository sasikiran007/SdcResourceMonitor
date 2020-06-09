package com.example.sdcresourcemonitor.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sdcresourcemonitor.model.Alert
import com.example.sdcresourcemonitor.model.AlertStat

@Database(entities = arrayOf(AlertStat::class, Alert::class),version = 1)
abstract class AlertDatabase : RoomDatabase() {

    abstract fun getAlertStatDao() : AlertStatDao

    abstract fun getAlertDao() : AlertDao

    companion object {
        @Volatile
        private var instance : AlertDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDataBse(context).also {
                instance = it
            }
        }
        private fun buildDataBse(context: Context)  = Room.databaseBuilder(context,AlertDatabase::class.java,"alertdatabae").build()

    }

}