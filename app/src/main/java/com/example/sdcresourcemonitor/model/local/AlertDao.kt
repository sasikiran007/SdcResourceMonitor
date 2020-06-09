package com.example.sdcresourcemonitor.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sdcresourcemonitor.model.Alert
import com.example.sdcresourcemonitor.model.AlertStat

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
interface AlertDao {

    @Insert
    suspend fun insertAll(vararg alert: Alert) :List<Long>

    @Query("SELECT * from alert")
    suspend fun getAll() : List<Alert>

    @Query("DELETE from alert")
    suspend fun deleteAll()

    @Query("SELECT * from alert WHERE uuid = :alertUuid")
    suspend fun getAlert(alertUuid : Long) :Alert

    @Query("SELECT * from alert WHERE alert_section like :alertSection and alert_level like :alertLevel" )
    suspend fun getAlerts(alertSection : String, alertLevel : String) : List<Alert>

}