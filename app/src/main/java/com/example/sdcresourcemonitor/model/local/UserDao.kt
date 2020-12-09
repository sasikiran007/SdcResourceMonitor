package com.example.sdcresourcemonitor.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
abstract class UserDao {

    @Insert
    abstract fun insertUser(user : User) : Void

    @Insert
    abstract fun insertProjects(projects : List<Project>) : Void

    @Insert
    abstract fun insertRoles(roles : List<Role>) : Void

    @Query("SELECT * FROM User WHERE email = :email")
    abstract fun getUser(email : String) : User

    @Query("SELECT * FROM Project WHERE email = :email")
    abstract fun getProjects(email : String) : List<Project>

    @Query("SELECT * FROM Role WHERE email = :email")
    abstract fun getRoles(email : String) : List<Role>






}