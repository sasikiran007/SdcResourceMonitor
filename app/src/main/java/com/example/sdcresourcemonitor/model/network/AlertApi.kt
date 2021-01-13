package com.example.sdcresourcemonitor.model.network

import com.example.sdcresourcemonitor.model.Alert
import com.example.sdcresourcemonitor.model.AlertStat
import com.example.sdcresourcemonitor.model.AlertTracker
import com.example.sdcresourcemonitor.model.Event
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface AlertApi {

    @GET("alert/stat/")
    fun getAllAlertStat(): Single<List<AlertStat>>

    @GET("alert/list/")
    fun getAllAlerts() : Single<List<Alert>>

    @GET("alert/trackers/")
    fun getAlertTrackers() : Single<List<AlertTracker>>

    @GET("alert/events/{alertLevel}/")
    fun getEvents( @Path("alertLevel") alertLevel : String) : Single<List<Event>>
}