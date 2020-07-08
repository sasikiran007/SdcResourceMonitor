package com.example.sdcresourcemonitor.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.sdcresourcemonitor.model.Alert
import com.example.sdcresourcemonitor.model.AlertStat
import com.example.sdcresourcemonitor.model.AlertTracker
import com.example.sdcresourcemonitor.model.local.AlertDao
import com.example.sdcresourcemonitor.model.local.AlertDatabase
import com.example.sdcresourcemonitor.model.network.AlertApiService
import com.example.sdcresourcemonitor.util.NotificationHelper
import com.example.sdcresourcemonitor.util.REFRESH_TIME
import com.example.sdcresourcemonitor.util.SharedpreferenceHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class AlertTrackerViewModel(application: Application) : BaseViewModel(application) {
    private var areAlertsLoading = false

    private var trackers = mutableListOf<AlertTracker>()

    val alertStats = MutableLiveData<List<AlertStat>>()
    val isLoading = MutableLiveData<Boolean>()
    val hasError = MutableLiveData<Boolean>()

    val alerts = MutableLiveData<List<Alert>>()
    val isLoadingAlerts = MutableLiveData<Boolean>()
    val hasErrorAlerts = MutableLiveData<Boolean>()
    val entities = MutableLiveData<List<String>>()
    private var alertSection: String = "%%"
    private var alertLevel: String = "%%"
    private var entity: String = "%%"

    private val TAG: String = "AlertTrackerViewModel"

    private val alertApi = AlertApiService()
    private val disposable = CompositeDisposable()

    //    private val prefHelper = SharedpreferenceHelper.invoke(getApplication())
    private val database = AlertDatabase.invoke(application)

    fun refreshStats() {
        Log.i(TAG, " refresh stats called")
        isLoading.value = true
        getTrackers()
    }


    fun refreshAlerts(newAlertSection: String, newAlertLevel: String, newEntity: String) {
        Log.i(TAG, "1. refresh alerts called : ")
        areAlertsLoading = true
        alertSection = newAlertSection
        alertLevel = newAlertLevel
        entity = newEntity
//        getTrackers()
        fetchAlertsFromNetwork()
    }
    fun refreshAlertsFromLocal(newAlertSection: String, newAlertLevel: String, newEntity: String) {
        Log.i(TAG, "1. refresh alerts from local called : ")
        areAlertsLoading = true
        alertSection = newAlertSection
        alertLevel = newAlertLevel
        entity = newEntity
//        getTrackers()
        fetchAlertsFromLocal()
    }

    private fun getTrackers() {

        disposable.add(
            alertApi.getTrackers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(object : DisposableSingleObserver<List<AlertTracker>>() {
                    override fun onSuccess(t: List<AlertTracker>) {
                        Log.i(TAG, "2. trackers are downloaded from network")

                        trackers.clear()
                        trackers.addAll(t)
                        checkTracker(t)
                    }

                    override fun onError(e: Throwable) {
                        Log.i(TAG, "2. Error while trackers are downloaded from network")
                        hasError.value = true
                        isLoading.value = false
                        areAlertsLoading = false
                    }

                }
                )

        )

    }

    private fun fetchAlertsFromNetwork() {
        Log.i(TAG,"5. Fetching alerts from network is called")
        isLoadingAlerts.value = true
        disposable.add(
            alertApi.getAllAlerts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(object : DisposableSingleObserver<List<Alert>>() {
                    override fun onSuccess(t: List<Alert>) {
                        Log.i(TAG, "6. Alerts : downloaded successfully from network")
                        loadAlertsIntoLocalDatabase(t)
                        areAlertsLoading = false
//                        Toast.makeText(getApplication(),"Yahoo, List downloaded", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(e: Throwable) {
                        Log.i(TAG, "6. Alerts : error in downloading from network")
                        areAlertsLoading = false
                        hasError.value = true
                        areAlertsLoading = false
//                        isLoading.value = false
//                        Toast.makeText(getApplication(),"Ayyoo, Error in loading List",Toast.LENGTH_LONG).show()
                    }

                })
        )

    }

    private fun loadAlertsIntoLocalDatabase(alerts: List<Alert>) {
        Log.i(TAG,"7. loading alerts into database called")
        launch {
            val dao = database.getAlertDao()
            dao.deleteAll()
            val alertUuids = dao.insertAll(*alerts.toTypedArray())
            for (i in alertUuids.indices) {
                alerts[i].uuid = alertUuids[i]
            }
            Log.i(TAG, "8. Alerts : loading data into database")
            alertDataRetrieved(dao.getAlerts(alertSection, alertLevel, entity))

            entitiesRetrieved(dao.getEntities(alertSection, alertLevel))
            Toast.makeText(
                getApplication(),
                "Alerts : data downloaded from network",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun fetchAlertsFromLocal() {
        isLoading.value = true
        Log.i(TAG, "5. fetch alerts from local database called")
        launch {
            val dao = database.getAlertDao()
            val alerts = dao.getAlerts(alertSection, alertLevel, entity)
            if (alerts.isNotEmpty()) {
                Log.i(TAG,"6. Fetching alerts from local is success")
                alertDataRetrieved(dao.getAlerts(alertSection, alertLevel, entity))
                Toast.makeText(
                    getApplication(),
                    "Alerts: data downloaded from local",
                    Toast.LENGTH_SHORT
                )
                areAlertsLoading = false
            } else {
                fetchAlertsFromNetwork()
            }
            entitiesRetrieved(dao.getEntities(alertSection, alertLevel))
            Toast.makeText(getApplication(), "Alerts data downloaded from local", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun alertDataRetrieved(newAlerts: List<Alert>) {
        Log.i(TAG,"7. Posting alerts into viewmodel is success")
        alerts.value = newAlerts

//        isLoading.value = false
//        hasError.value = false
    }

    private fun loadTrackersIntDatabase() {
        launch {
            Log.i(TAG, "<5>. Trackers : loading into database")
            val dao = database.getAlertTrackerDao()
            dao.deleteAll()

            var trackersLocal = dao.getAll()
            Log.i(TAG, "tacker size after deleting all : " + trackersLocal.size)
            for (tracker in trackersLocal) {
                Log.i(TAG, tracker.scriptName + "," + tracker.trackerNumber + "," + tracker.uuid)
            }

            val uuids = dao.insertAll(*trackers.toTypedArray())
            trackersLocal = dao.getAll()
            Log.i(TAG, "tacker size after inserting all : " + trackersLocal.size)
            for (tracker in trackersLocal) {
                Log.i(TAG, tracker.scriptName + "," + tracker.trackerNumber + "," + tracker.uuid)
            }

            Log.i(TAG, "trackers : uuids count : " + uuids.size)
        }
    }

    private fun entitiesRetrieved(newEntities: List<String>) {
        Log.i("AlertViewModel", "entities size :${newEntities.size}")
        entities.value = newEntities
    }

    private fun fetchStatsFromNetwork() {
//        Log.i(TAG, "5. fetch stats from network  method called")
        isLoading.value = true
        val d1 = alertApi.getAllAlertStat()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<List<AlertStat>>() {
                override fun onSuccess(t: List<AlertStat>) {
//                    Log.i(TAG, "6. Stats data from NW is success and loading stats data into database")
                    loadStatsIntoDatabase(t)
//                    NotificationHelper(getApplication()).createNotification()
                }

                override fun onError(e: Throwable) {
                    hasError.value = true
                    isLoading.value = false
//                    Log.i(TAG, "6. Error in loading stats data")
                }

            })
        disposable.add(d1)
    }

    private fun fetchStatsFromLocal() {
//        Log.i(TAG,"5. Fetching local stats")
        isLoading.value = true
        launch {
            val dao = database.getAlertStatDao()
            val stats = dao.getAll()
            if (stats.isNotEmpty()) {
//                Log.i(TAG,"6. Fetching stats from database is success(Next step is 9)")
                statDataRetrieved(stats)
//                Toast.makeText(getApplication(), "Loaded data from local", Toast.LENGTH_SHORT)
//                    .show()
            } else {
                fetchStatsFromNetwork()
            }
        }

    }

    private fun loadStatsIntoDatabase(newAlertStats: List<AlertStat>) {
//        Log.i(TAG,"7. loading stats into local database is called")
        launch {
            val dao = database.getAlertStatDao()
            dao.deleteAll()
            val alertStatUuids = dao.insertAll(*newAlertStats.toTypedArray())
//            Log.i(TAG, "8. Alert stat Data loaded into database")
            for (i in alertStatUuids.indices) {
                newAlertStats[i].uuid = alertStatUuids[i]
            }
            statDataRetrieved(newAlertStats)
//            Toast.makeText(getApplication(), "Data loading from network", Toast.LENGTH_SHORT).show()
            isLoading.value = false
            hasError.value = false

        }

    }

    private fun statDataRetrieved(newAlertStats: List<AlertStat>) {
//        Log.i(TAG, "9. Posting stats into viewmodel data")
        alertStats.value = newAlertStats
        isLoading.value = false
        hasError.value = false

    }

    private fun checkTracker(networkTrackers : List<AlertTracker>) {
        Log.i(TAG,"3. Check trackers called network trackers size :" + networkTrackers.size)
        var fetchData = false
        launch {
            for(networkTtacker in networkTrackers) {
                val dao = database.getAlertTrackerDao()
                Log.i(TAG,"dao ref "+dao)
                val scriptName = networkTtacker.scriptName
                val trackerNumber = networkTtacker.trackerNumber
                val localTrackerNumber  = dao.getTrackerNumber(scriptName)

                Log.i(
                    TAG,
                    scriptName + "," + trackerNumber + "==" + localTrackerNumber.trackerNumber
                )
                if (trackerNumber != localTrackerNumber.trackerNumber) {
                    if (!fetchData) fetchData = true
//                                fetchAlertsFromNetwork(scriptName)
                }
            }
            if (fetchData) {
                Log.i(TAG,"4. No local trackers , so fetching network data")
                loadTrackersIntDatabase()
                fetchStatsFromNetwork()
                fetchAlertsFromNetwork()
            } else {
                Log.i(TAG,"4. local trackers found, so fetching local data")
                fetchStatsFromLocal()
                fetchAlertsFromLocal()
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}