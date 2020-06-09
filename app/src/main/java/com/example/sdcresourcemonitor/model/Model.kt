package com.example.sdcresourcemonitor.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert_stat")
data class AlertStat
    (
    val date: Long,
    val count: Int,

    @ColumnInfo(name = "critical_count")
    val criticalCount: Int,

    @ColumnInfo(name = "major_count")
    val majorCount: Int,

    @ColumnInfo(name = "minor_count")
    val minorCount: Int,
    val section: String,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uuid")
    var uuid: Long

)

@Entity(tableName = "alert")
data class Alert(
    val date: Long,

    @ColumnInfo(name = "alert_id")
    val alertId: String,

    @ColumnInfo(name = "alert_section")
    val alertSection: String,

    @ColumnInfo(name = "alert_level")
    val alertLevel: String,

    val entity: String,

    @ColumnInfo(name = "os_name")
    val osName: String,

    @ColumnInfo(name = "app_name")
    val appName: String,

    val message: String,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uuid")
    var uuid: Long

)