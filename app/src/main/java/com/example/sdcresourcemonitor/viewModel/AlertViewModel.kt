package com.example.sdcresourcemonitor.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sdcresourcemonitor.model.Alert
import com.example.sdcresourcemonitor.model.local.AlertDatabase
import com.example.sdcresourcemonitor.model.network.AlertApiService
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
    val isLoading = MutableLiveData<Boolean>()
    val hasError = MutableLiveData<Boolean>()

    private var alertSection : String = "%%"
    private var alertLevel : String = "%%"

    private val alertApi = AlertApiService()
    private val disposable = CompositeDisposable()
    private val prefHelper = SharedpreferenceHelper.invoke(getApplication())
    private val database = AlertDatabase.invoke(application)

    fun refresh(newAlertSection : String, newAlertLevel : String) {
        alertSection = newAlertSection
        alertLevel = newAlertLevel
        val updateTime = prefHelper.getUpdatedTimeList()
        if( updateTime!= null && updateTime != 0L && (System.nanoTime() - updateTime) < REFRESH_TIME)  {
            fetchFromLocal()
        }else {
            fetchFromNetwork()
        }
    }

    fun refreshByPassLocal(newAlertSection : String, newAlertLevel : String) {
        alertSection = newAlertSection
        alertLevel = newAlertLevel
        fetchFromNetwork()
    }

    private fun fetchFromLocal() {
        isLoading.value = true
        launch {
            val dao = database.getAlertDao()
            dataRetrieved(dao.getAlerts(alertSection,alertLevel))
            Toast.makeText(getApplication(),"Alert data downloaded from local", Toast.LENGTH_LONG).show()
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
                        Toast.makeText(getApplication(),"Yahoo, List downloaded", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(e: Throwable) {
                        hasError.value = true
                        isLoading.value = false
                        Toast.makeText(getApplication(),"Ayyoo, Error in loading List",Toast.LENGTH_LONG).show()
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
            prefHelper.saveUpdateTimeList(System.nanoTime())
            dataRetrieved(dao.getAlerts(alertSection,alertLevel))
            Toast.makeText(getApplication(),"Alert data downloaded from network", Toast.LENGTH_LONG).show()
        }

    }

    private fun dataRetrieved(newalerts : List<Alert>) {
        alerts.value = newalerts
        isLoading.value = false
        hasError.value = false
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }


}