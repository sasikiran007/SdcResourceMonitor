package com.example.sdcresourcemonitor.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.common.collect.ComparisonChain
import java.lang.Double.parseDouble
import java.lang.NumberFormatException

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

    @ColumnInfo(name = "entity_id")
    val entityId: String,

    @ColumnInfo(name = "os_name")
    val osName: String,

    @ColumnInfo(name = "app_name")
    val appName: String,

    val property: String,

    @ColumnInfo(name = "property_name")
    val propertyValue: String,

    val hostname: String,

    @ColumnInfo(name = "script_name")
    val scriptName: String,

//    val message: String,


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uuid")
    var uuid: Long

) : Comparable<Alert> {
    override fun compareTo(other: Alert): Int {

//        return compareValuesBy(this,other,*selectors)
        return comparator.compare(this, other)
//        return ComparisonChain.start().compare(entity, other.entity).result()
    }

    companion object {
//        val selectors: Array<(Alert) -> Comparable<*>?> =
//            arrayOf(Alert::entity, Alert::property, Alert::propertyValue)

        val comparator = compareBy<Alert> { it.entity }.thenBy { it.property }
            .thenByDescending {
                var value : Double = 0.0
                var numeric = true
                try {
                    value = parseDouble(it.propertyValue)
                }catch (e: NumberFormatException) {
                    numeric = false
                }

               if(numeric) value else it.propertyValue
            }
    }
}

data class radioEntity(val name: String, val isChecked: Boolean)