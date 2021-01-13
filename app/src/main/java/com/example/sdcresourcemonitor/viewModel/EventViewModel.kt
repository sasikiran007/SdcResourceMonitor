package com.example.sdcresourcemonitor.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.sdcresourcemonitor.model.AlertStat
import com.example.sdcresourcemonitor.model.Event
import com.example.sdcresourcemonitor.model.local.AlertDatabase
import com.example.sdcresourcemonitor.model.network.AlertApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class EventViewModel(application: Application) : BaseViewModel(application) {
    private val TAG = "EventViewModel"

    private val alertApiService = AlertApiService(application)
    private val disposable = CompositeDisposable()
    private val database = AlertDatabase.invoke(getApplication())

    val events  =  MutableLiveData<List<Event>>()
    val isLoading = MutableLiveData<Boolean>()
    val hasError = MutableLiveData<Boolean>()

    fun refreshData(isFresh : Boolean) {
        if (isFresh) fetchFromLocal()
        else fetchFromNetwork()
    }

    private fun fetchFromLocal() {
        isLoading.value = true
        launch {
            val dao = database.getEventDao()
            val events = dao.getAll()
            if (events.isNotEmpty()) {
                dataRetrieved(events)
            } else {
                fetchFromNetwork()
            }
        }
    }

    private fun fetchFromNetwork() {
        RxJavaPlugins.setErrorHandler { throwable: Throwable? -> }
        Log.i(TAG, " fetchNetwork  method called")
        isLoading.value = true
        val d1 = alertApiService.getEvents("critical")
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<List<Event>>() {
                override fun onSuccess(t: List<Event>) {
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


    private fun dataRetrieved(newEvents: List<Event>) {
        Log.i(TAG, "Posting value into Event viewmodel data")
        events.value = newEvents
        isLoading.value = false
        hasError.value = false

    }

    private fun loadIntoLocalDatabase(newEvents: List<Event>) {
        launch {
            val dao = database.getEventDao()
            dao.deleteAll()
            val alertStatUuids = dao.insertAll(*newEvents.toTypedArray())
            Log.i(TAG, "Alert stat Data loaded into database")
            for (i in alertStatUuids.indices) {
                newEvents[i].uuid = alertStatUuids[i]
            }
            dataRetrieved(newEvents)

//            Toast.makeText(getApplication(), "Data loading from network", Toast.LENGTH_SHORT).show()
//            isLoading.value = false
//            hasError.value = false

        }

    }


}