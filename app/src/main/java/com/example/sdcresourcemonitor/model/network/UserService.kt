package com.example.sdcresourcemonitor.model.network

import com.example.sdcresourcemonitor.model.local.User
import com.example.sdcresourcemonitor.model.network.model.UserLogin
import com.example.sdcresourcemonitor.model.network.model.UserSignup
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class UserService {
    private val BASE_URL = "http://selfcare.sdc.bsnl.co.in/srmapp/"

    var okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()


    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(UserApi::class.java)

    fun signup(userSignup: UserSignup ) : Single<Response<ResponseBody>> {
        val jsonObj = JSONObject()
        jsonObj.put("email",userSignup.email)
        jsonObj.put("uid",userSignup.uid)
        jsonObj.put("token",userSignup.token)

        val user  = api.signup(jsonObj.toString())
        return user
    }

    fun login(userLogin: UserLogin) : Single<Response<ResponseBody>> {
        val jsonObj = JSONObject()
        jsonObj.put("email",userLogin.email)
        jsonObj.put("uid",userLogin.uid)
        val response = api.login(jsonObj.toString())
        return response
    }

}