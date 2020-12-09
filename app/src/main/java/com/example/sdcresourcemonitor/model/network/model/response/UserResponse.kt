package com.example.sdcresourcemonitor.model.network.model.response

import com.example.sdcresourcemonitor.model.local.Project
import com.example.sdcresourcemonitor.model.local.Role

data class UserResponse(
    val email: String,
    val name: String,
    val mobile : String,
    val designation : String,
    val projects : List<Project>,
    val roles : List<Role>

)