package com.example.sdcresourcemonitor.view


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sdcresourcemonitor.R
import com.example.sdcresourcemonitor.view.adapter.AlertStatListViewAdapter
import com.example.sdcresourcemonitor.viewModel.AlertStatViewModel
import com.example.sdcresourcemonitor.viewModel.AlertTrackerViewModel
import com.example.sdcresourcemonitor.viewModel.AlertViewModel
import kotlinx.android.synthetic.main.fragment_alert_dashboard.*


class AlertDashBoard : Fragment() {
    private val TAG = "AlertDashBoard"

//    private lateinit var viewModel: AlertStatViewModel
    private val viewModel by lazy {
        activity?.let { ViewModelProvider(it).get(AlertTrackerViewModel::class.java) }
    }
//    private lateinit var listViewModel : AlertViewModel
    val alertStatadapter =
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
        val view = inflater.inflate(R.layout.fragment_alert_dashboard, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel = ViewModelProvider(this).get(AlertStatViewModel::class.java)
//        trackerViewModel = ViewModelProvider(this).get(AlertTrackerViewModel::class.java)
//        listViewModel = ViewModelProvider(this).get(AlertViewModel::class.java)


        errorTextView.visibility = View.GONE
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE

        swipeTorefresh.setOnRefreshListener {
            errorTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
            viewModel?.refreshStats()
            swipeTorefresh.isRefreshing = false
        }

        recyclerView.apply {
            adapter = alertStatadapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context,LinearLayoutManager.VERTICAL))
        }
        viewModel?.refreshStats()
        observeViewModel()
    }



    private fun observeViewModel() {
        Log.i(TAG, "Observing viewmodel")
        viewModel?.alertStats?.observe(viewLifecycleOwner, Observer { alertStats ->
            alertStats?.let {
                setVisibilities(error = false, loading = false, data = true)
                alertStatadapter.updateAlertStats(alertStats)
            }

        })

        viewModel?.hasError?.observe(viewLifecycleOwner, Observer { hasError ->
            hasError?.let {
                if (hasError) {
                    setVisibilities(error = true, loading = false, data = false)
                    Log.i(TAG, "Error in loading")
                }
            }
        })

        viewModel?.isLoading?.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let {
                if (isLoading) {
                    setVisibilities(error = false, loading = true, data = false)
                } else {
                    progressBar.visibility = View.GONE
                    Log.i(TAG, "Not Loading")
                }
            }
        })
    }

    private fun setVisibilities(error : Boolean,loading : Boolean,data : Boolean) {
        if(error) {
            errorTextView.visibility = View.VISIBLE
        }else {
            errorTextView.visibility = View.GONE
        }

        if(loading) {
            progressBar.visibility = View.VISIBLE
        }else {
            progressBar.visibility = View.GONE
        }

        if(data) {
            recyclerView.visibility = View.VISIBLE
        }else {
            recyclerView.visibility = View.GONE
        }
    }


}
