package com.example.sdcresourcemonitor.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sdcresourcemonitor.model.Alert
import com.example.sdcresourcemonitor.model.AlertStat
import com.example.sdcresourcemonitor.model.AlertTracker
import com.example.sdcresourcemonitor.model.local.AlertDatabase
import com.example.sdcresourcemonitor.model.network.AlertApiService
import com.example.sdcresourcemonitor.util.NotificationHelper
import com.example.sdcresourcemonitor.util.REFRESH_TIME
import com.example.sdcresourcemonitor.util.SharedpreferenceHelper
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class AlertViewModel(application: Application) : BaseViewModel(application) {

    val alerts  =  MutableLiveData<List<Alert>>()
    val trackers = MutableLiveData<List<AlertTracker>>()
    val isLoading = MutableLiveData<Boolean>()
    val hasError = MutableLiveData<Boolean>()
    val entities = MutableLiveData<List<String>>()

    private var alertSection : String = "%%"
    private var alertLevel : String = "%%"
    private var entity : String = "%%"

    private val TAG : String = "AlertViewModel"

    private val alertApi = AlertApiService()
    private val disposable = CompositeDisposable()
//    private val prefHelper = SharedpreferenceHelper.invoke(getApplication())
    private val database = AlertDatabase.invoke(application)

    fun refreshTrackers(newAlertSection : String, newAlertLevel : String, newEntity : String) {
        alertSection = newAlertSection
        alertLevel = newAlertLevel
        entity = newEntity

        Log.i(TAG, "Refresh trackers method is called")
        isLoading.value = true
        val d1 = alertApi.getTrackers()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<List<AlertTracker>>() {
                override fun onSuccess(t: List<AlertTracker>) {
                    trackers.value = t
                }

                override fun onError(e: Throwable) {
                    hasError.value = true
                    isLoading.value = false
                }

            })
        disposable.add(d1)
    }

    fun refreshFromLocal(newAlertSection : String, newAlertLevel : String, newEntity : String) {
        alertSection = newAlertSection
        alertLevel = newAlertLevel
        entity = newEntity
        fetchFromLocal()
    }

    fun refreshData(isLocalDataValid : Boolean) {
        if(isLocalDataValid) fetchFromLocal()
        else fetchFromNetwork()
    }

    private fun fetchFromLocal() {
        isLoading.value = true
        launch {
            val dao = database.getAlertDao()
            dataRetrieved(dao.getAlerts(alertSection,alertLevel,entity))
            Log.i(TAG,"fetch from local database called")
            entitiesRetrieved(dao.getEntities(alertSection,alertLevel))
//            Toast.makeText(getApplication(),"Alert data downloaded from local", Toast.LENGTH_LONG).show()
        }
    }

    private fun fetchFromNetwork() {
        isLoading.value = true
        disposable.add(
            alertApi.getAllAlerts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(object  : DisposableSingleObserver<List<Alert>>() {
                    override fun onSuccess(t: List<Alert>) {
                        loadIntoLocalDatabase(t)
//                        Toast.makeText(getApplication(),"Yahoo, List downloaded", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(e: Throwable) {
                        hasError.value = true
//                        isLoading.value = false
//                        Toast.makeText(getApplication(),"Ayyoo, Error in loading List",Toast.LENGTH_LONG).show()
                    }

                })
        )
    }

    private fun loadIntoLocalDatabase(alerts : List<Alert>) {
        launch {
            val dao = database.getAlertDao()
            dao.deleteAll()
            val alertUuids = dao.insertAll(*alerts.toTypedArray())
            for(i in alertUuids.indices) {
                alerts[i].uuid = alertUuids[i]
            }
            dataRetrieved(dao.getAlerts(alertSection,alertLevel,entity))
            Log.i(TAG,"loading data into database")
            entitiesRetrieved(dao.getEntities(alertSection,alertLevel))
//            Toast.makeText(getApplication(),"Alert data downloaded from network", Toast.LENGTH_LONG).show()
        }

    }

    private fun dataRetrieved(newAlerts : List<Alert>) {
        alerts.value = newAlerts
//        isLoading.value = false
//        hasError.value = false
    }

    private fun entitiesRetrieved(newEntities : List<String>) {
        Log.i("AlertViewModel","entities size :${newEntities.size}")
        entities.value = newEntities
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }


}