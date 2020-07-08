package com.example.sdcresourcemonitor.model.network

import com.example.sdcresourcemonitor.model.Alert
import com.example.sdcresourcemonitor.model.AlertStat
import com.example.sdcresourcemonitor.model.AlertTracker
import io.reactivex.Single
import retrofit2.http.GET

interface AlertApi {

    @GET("alert/stat/")
    fun getAllAlertStat(): Single<List<AlertStat>>

    @GET("alert/list/")
    fun getAllAlerts() : Single<List<Alert>>

    @GET("alert/trackers/")
    fun getAlertTrackers() : Single<List<AlertTracker>>
}