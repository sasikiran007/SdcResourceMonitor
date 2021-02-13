package com.example.sdcresourcemonitor.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sdcresourcemonitor.model.Alert
import com.example.sdcresourcemonitor.model.AlertStat
import com.example.sdcresourcemonitor.model.AlertTracker
import com.example.sdcresourcemonitor.model.Event

@Dao
interface AlertStatDao {

    @Insert
    suspend fun insertAll(vararg alertStat: AlertStat) :List<Long>

    @Query("SELECT * from alert_stat")
    suspend fun getAll() : List<AlertStat>

    @Query("DELETE from alert_stat")
    suspend fun deleteAll()

    @Query("SELECT * from alert_stat WHERE uuid = :alertStatId")
    suspend fun getAlertStat(alertStatId : Long) :AlertStat

}

@Dao
interface EventDao {
    @Insert
    suspend fun insertAll(vararg  event: Event) : List<Long>

    @Query("SELECT * from event")
    suspend fun getAll() : List<Event>

    @Query("DELETE from event")
    suspend fun deleteAll()
//
//    @Query("SELECT * from event WHERE uuid = :eventUID")
//    suspend fun getAlertStat(eventUID : Long) :Event
//
//    @Query("SELECT * from event WHERE alert_level = :alertLevel")
//    suspend fun getAlertStat(alertLevel : String) : List<Event>
}

@Dao
interface AlertDao {

    @Insert
    suspend fun insertAll(vararg alert: Alert) :List<Long>

    @Query("SELECT * from alert")
    suspend fun getAll() : List<Alert>

    @Query("DELETE from alert")
    suspend fun deleteAll()

    @Query("SELECT * from alert WHERE uuid = :alertUuid")
    suspend fun getAlert(alertUuid : Long) :Alert

    @Query("SELECT * from alert WHERE alert_section like :alertSection and alert_level like :alertLevel and entity like :entity" )
    suspend fun getAlerts(alertSection : String, alertLevel : String, entity : String) : List<Alert>

    @Query("SELECT DISTINCT entity from alert WHERE alert_section like :alertSection and alert_level like :alertLevel" )
    suspend fun getEntities(alertSection : String, alertLevel : String) : List<String>

}

@Dao
interface AlertTrackerDao {
    @Query("SELECT * from alert_tracker")
    suspend fun getAll() : List<AlertTracker>
}