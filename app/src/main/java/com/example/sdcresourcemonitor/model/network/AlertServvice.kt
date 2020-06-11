package com.example.sdcresourcemonitor.model.network

import com.example.sdcresourcemonitor.model.Alert
import com.example.sdcresourcemonitor.model.AlertStat
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class AlertApiService {
    private val BASE_URL = "http://selfcare.sdc.bsnl.co.in/srmapp/"

    var okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()


    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(AlertApi::class.java)

    fun getAllAlertStat() : Single<List<AlertStat>> {
        return api.getAllAlertStat()
    }

    fun getAllAlerts() : Single<List<Alert>> {
        return api.getAllAlerts()
    }

}