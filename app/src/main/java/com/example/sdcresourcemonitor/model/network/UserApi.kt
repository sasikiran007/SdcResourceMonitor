package com.example.sdcresourcemonitor.model.network

import com.example.sdcresourcemonitor.model.local.User
import com.example.sdcresourcemonitor.model.network.model.UserLogin
import com.example.sdcresourcemonitor.model.network.model.UserSignup
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {

    @Headers("Content-Type: application/json")
    @POST("signup")
    fun signup(@Body userSignup: String) : Single<Response<ResponseBody>>

    @POST("login")
    fun login(@Body userLogin: String) : Single<Response<ResponseBody>>
}