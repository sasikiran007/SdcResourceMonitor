package com.example.sdcresourcemonitor.view


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sdcresourcemonitor.R
import com.example.sdcresourcemonitor.model.AlertTracker
import com.example.sdcresourcemonitor.util.SharedpreferenceHelper
import com.example.sdcresourcemonitor.view.adapter.AlertStatListViewAdapter
import com.example.sdcresourcemonitor.viewModel.AlertStatViewModel
import com.example.sdcresourcemonitor.viewModel.AlertViewModel
import kotlinx.android.synthetic.main.fragment_alert_dashboard.*


class AlertDashBoard : Fragment() {

    private val TAG = "AlertDashBoard"
    private lateinit var viewModel: AlertStatViewModel
    private lateinit var alertViewmodel : AlertViewModel
    private lateinit var prefHelper: SharedpreferenceHelper
    private val trackerTimes = hashMapOf<String,String>()

    private val alertStateAdapter =
        AlertStatListViewAdapter(
            ArrayList()
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Log.i(TAG,"onCreate method called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.i(TAG,"onCreateView method called")
        return  inflater.inflate(R.layout.fragment_alert_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(AlertStatViewModel::class.java)
        alertViewmodel = ViewModelProvider(this).get(AlertViewModel::class.java)
        prefHelper = SharedpreferenceHelper.invoke(requireContext())

        errorTextView.visibility = View.GONE
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE

        swipeTorefresh.setOnRefreshListener {
            errorTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
            viewModel.refreshTrackers()
            swipeTorefresh.isRefreshing = false
        }

        recyclerView.apply {
            adapter = alertStateAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context,LinearLayoutManager.VERTICAL))
        }

        viewModel.refreshTrackers()

        observeViewModel()
    }

    private fun observeViewModel() {

        Log.i(TAG, "Observing viewmodel")

        alertViewmodel.alerts.observe(viewLifecycleOwner, Observer { alerts->
            alerts?.let{
                Log.i(TAG,"alerts are observed")
                prefHelper.saveUpdateTrackerTimes(trackerTimes)
            }

        })

        viewModel.trackers.observe(viewLifecycleOwner, Observer { trackers->
            trackers?.let {
                Log.i(TAG,"trackers downloaded")
                for(tracker in trackers) {
                    trackerTimes[tracker.scriptName] = tracker.trackerNumber
                }
                Log.i(TAG,"refreshing data ")
                val isLocalDataFresh = checkLocalData(trackers)
                viewModel.refreshData(isLocalDataFresh)
                if(!isLocalDataFresh) alertViewmodel.refreshData(false)
            }
        })

        viewModel.alertStats.observe(viewLifecycleOwner, Observer { alertStats ->
            alertStats?.let {
                errorTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                Log.i(TAG, alertStats.size.toString())
                alertStateAdapter.updateAlertStats(alertStats)
            }

        })

        viewModel.hasError.observe(viewLifecycleOwner, Observer { hasError ->
            hasError?.let {
                if (hasError) {
                    errorTextView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    Log.i(TAG, "Error in loading")
                }
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let {
                if (isLoading) {
                    errorTextView.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    Log.i(TAG, "its loading")
                } else {
                    progressBar.visibility = View.GONE
                    Log.i(TAG, "Not Loading")
                }
            }
        })
    }

    private fun checkLocalData(trackers : List<AlertTracker>) : Boolean{
        var isFresh = false
        Log.i(TAG,"Check local data is called : ${trackers.size}")
        val hashMap : HashMap<String,String> = prefHelper.getUpdatedTrackerTimes()
        Log.i(TAG,"Hashmap size is : ${hashMap.size}")
        if(hashMap.isEmpty()) return isFresh

        for(tracker in trackers) {
            Log.i(TAG,"hashMap size"+hashMap.size)
            val scriptName = tracker.scriptName
            val trackerNumber = tracker.trackerNumber
            Log.i(TAG, "$scriptName,$trackerNumber," +hashMap["blue"])
            Toast.makeText(context,"$trackerNumber,"+hashMap["blue"],Toast.LENGTH_LONG).show()
            val localTime = hashMap[scriptName]?.toLongOrNull()
            val networkTime = trackerNumber.toLongOrNull()
            if( localTime != null && networkTime != null && (localTime >= networkTime) ) {
                isFresh = true
                break
            }
        }
        Log.i(TAG,"retruning isFresh :"+isFresh)
        return isFresh
    }




}
