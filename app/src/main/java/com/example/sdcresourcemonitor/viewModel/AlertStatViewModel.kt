package com.example.sdcresourcemonitor.viewModel

import android.app.Application
import android.text.BoringLayout
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sdcresourcemonitor.model.Alert
import com.example.sdcresourcemonitor.model.AlertStat
import com.example.sdcresourcemonitor.model.local.AlertDatabase
import com.example.sdcresourcemonitor.model.network.AlertApiService
import com.example.sdcresourcemonitor.util.REFRESH_TIME
import com.example.sdcresourcemonitor.util.SharedpreferenceHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class AlertStatViewModel(application: Application) : BaseViewModel(application) {

    private val TAG = "AlertStatViewModel"
//    private val REFRESH_THRESHOLD = 10 * 1000 * 1000 * 1000L
    private val prefsHelper = SharedpreferenceHelper.invoke(getApplication())

    private val alertApiService = AlertApiService()
    private val disposable = CompositeDisposable()
    private val database = AlertDatabase.invoke(getApplication())

    val alertStats = MutableLiveData<List<AlertStat>>()
    val isLoading = MutableLiveData<Boolean>()
    val hasError = MutableLiveData<Boolean>()

    fun refresh() {
        Log.i(TAG, " refresh method called")
        val updateTime = prefsHelper.getUpdatedTime()
        if (updateTime != null && updateTime != 0L && (System.nanoTime() - updateTime) < REFRESH_TIME ) {

            Log.i(TAG, " Refreshing from local : ${updateTime.div(1000000000)} :${REFRESH_TIME.div(1000000000)} : ${System.nanoTime().div(1000000000) - updateTime.div(1000000000)} ")
            fetchFromLocal()
        } else {
            Log.i(TAG, "Refreshing from network")
            fetchFromNetwork()
        }
    }

    fun refreshByPassLocal() {
        fetchFromNetwork()
    }


    private fun fetchFromLocal() {
        isLoading.value = true
        launch {
            val dao = database.getAlertStatDao()
            val alerts = dao.getAll()
            if(alerts.isNotEmpty()) {
                dataRetrieved(alerts)
                Toast.makeText(getApplication(), "Loaded data from local", Toast.LENGTH_SHORT)
                    .show()
            }else {
                fetchFromNetwork()
            }
        }

    }



    private fun dataRetrieved(newAlertStats: List<AlertStat>) {
        Log.i(TAG, "Posting value into viewmodel data")
        alertStats.value = newAlertStats
        isLoading.value = false
        hasError.value = false

    }

    private fun fetchFromNetwork() {
        Log.i(TAG, " fetchNetwork  method called")
        isLoading.value = true
        val d1 = alertApiService.getAllAlertStat()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<List<AlertStat>>() {
                override fun onSuccess(t: List<AlertStat>) {
                    Log.i(TAG, "Data from NW is success and loading data into database")
                    loadIntoLocalDatabase(t)
                }

                override fun onError(e: Throwable) {
                    hasError.value = true
                    isLoading.value = false
                    Log.i(TAG, "Error in loading data")
                }

            })
        disposable.add(d1)

    }

    private fun loadIntoLocalDatabase(newAlertStats: List<AlertStat>) {
        launch {
            val dao = database.getAlertStatDao()
            dao.deleteAll()
            val alertStatUuids = dao.insertAll(*newAlertStats.toTypedArray())
            prefsHelper.saveUpdateTime(System.nanoTime())
            Log.i(TAG, "Alert stat Data loaded into database")
            for (i in alertStatUuids.indices) {
                newAlertStats[i].uuid = alertStatUuids[i]
            dataRetrieved(newAlertStats)            }

        Toast.makeText(getApplication(), "Data loading from network", Toast.LENGTH_SHORT).show()
            isLoading.value = false
            hasError.value = false

        }

    }


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }


}