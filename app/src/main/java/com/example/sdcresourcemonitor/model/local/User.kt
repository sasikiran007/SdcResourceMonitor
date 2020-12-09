package com.example.sdcresourcemonitor.model.local

import androidx.room.*

@Entity(tableName = "project")
data class Project(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uuid")
    var uuid: Long,

    val email: String,
    val name: String
    )

@Entity(tableName = "role")
data class Role(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uuid")
    var uuid: Long,

    val email: String,
    val name: String
)

@Entity(tableName = "user")
data class User(
    val name: String,
    @PrimaryKey
    val email: String,
    val designation: String,
    val mobile: String

) {
    @Ignore
    var projects: List<String> = listOf()

    @Ignore
    var roles: MutableList<String> = mutableListOf()
}