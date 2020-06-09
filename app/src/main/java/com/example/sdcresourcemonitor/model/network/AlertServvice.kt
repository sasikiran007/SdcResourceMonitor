package com.example.sdcresourcemonitor.model.network

import com.example.sdcresourcemonitor.model.Alert
import com.example.sdcresourcemonitor.model.AlertStat
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class AlertApiService {
    private val BASE_URL = "http://selfcare.sdc.bsnl.co.in/srmapp/"

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
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