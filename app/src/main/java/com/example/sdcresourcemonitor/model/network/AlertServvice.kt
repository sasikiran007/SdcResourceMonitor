package com.example.sdcresourcemonitor.model.network

import android.content.Context
import android.provider.SyncStateContract
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.sdcresourcemonitor.model.Alert
import com.example.sdcresourcemonitor.model.AlertStat
import com.example.sdcresourcemonitor.model.AlertTracker
import com.example.sdcresourcemonitor.model.Event
import com.example.sdcresourcemonitor.util.HEADER_STRING
import com.example.sdcresourcemonitor.util.SharedpreferenceHelper
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class AlertApiService(val context: Context) {
    private val BASE_URL = "http://selfcare.sdc.bsnl.co.in/srmapp/"
    private var prefHelper =    SharedpreferenceHelper.invoke(context)

    private val AUTHORIZATION_TOKEN = prefHelper.getUpdatedAuthorizationToken()

    var okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor{ chain ->
            val request = chain.request().newBuilder().addHeader(HEADER_STRING, AUTHORIZATION_TOKEN).build()
            chain.proceed(request)
        }
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

    fun getTrackers() : Single<List<AlertTracker>> {
        return  api.getAlertTrackers()
    }

    fun getEvents(alertLevel : String) : Single<List<Event>> {
        return api.getEvents(alertLevel)
    }

}