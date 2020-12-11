package com.example.sdcresourcemonitor.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.sdcresourcemonitor.model.AlertStat
import com.example.sdcresourcemonitor.model.AlertTracker
import com.example.sdcresourcemonitor.model.local.AlertDatabase
import com.example.sdcresourcemonitor.model.network.AlertApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch


class AlertStatViewModel(application: Application) : BaseViewModel(application) {

    private val TAG = "AlertStatViewModel"

    private val alertApiService = AlertApiService(application)
    private val disposable = CompositeDisposable()
    private val database = AlertDatabase.invoke(getApplication())


    val alertStats = MutableLiveData<List<AlertStat>>()
    val isLoading = MutableLiveData<Boolean>()
    val hasError = MutableLiveData<Boolean>()
    val trackers = MutableLiveData<List<AlertTracker>>()

    fun refreshTrackers() {
        Log.i(TAG, "Refresh trackers method is called")
        isLoading.value = true
        val d1 = alertApiService.getTrackers()
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

    fun refreshData(isFresh: Boolean) {
        if (isFresh) fetchFromLocal()
        else fetchFromNetwork()
    }

    private fun fetchFromLocal() {
        isLoading.value = true
        launch {
            val dao = database.getAlertStatDao()
            val alerts = dao.getAll()
            if (alerts.isNotEmpty()) {
                dataRetrieved(alerts)
//                Toast.makeText(getApplication(), "Loaded data from local", Toast.LENGTH_SHORT)
//                    .show()
            } else {
                fetchFromNetwork()
            }
        }
    }

    private fun fetchFromNetwork() {
        RxJavaPlugins.setErrorHandler { throwable: Throwable? -> }
        Log.i(TAG, " fetchNetwork  method called")
        isLoading.value = true
        val d1 = alertApiService.getAllAlertStat()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<List<AlertStat>>() {
                override fun onSuccess(t: List<AlertStat>) {
//                    Log.i(TAG, "Data from NW is success and loading data into database")
                    loadIntoLocalDatabase(t)
//                    NotificationHelper(getApplication()).createNotification()
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
            Log.i(TAG, "Alert stat Data loaded into database")
            for (i in alertStatUuids.indices) {
                newAlertStats[i].uuid = alertStatUuids[i]
            }
            dataRetrieved(newAlertStats)

//            Toast.makeText(getApplication(), "Data loading from network", Toast.LENGTH_SHORT).show()
//            isLoading.value = false
//            hasError.value = false

        }

    }

    private fun dataRetrieved(newAlertStats: List<AlertStat>) {
        Log.i(TAG, "Posting value into viewmodel data")
        alertStats.value = newAlertStats
        isLoading.value = false
        hasError.value = false

    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }


}