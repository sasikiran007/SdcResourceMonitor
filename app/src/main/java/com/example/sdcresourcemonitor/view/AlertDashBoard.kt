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
import kotlinx.android.synthetic.main.fragment_alert_dashboard.*


class AlertDashBoard : Fragment() {
    private val TAG = "AlertDashBoard"

    private lateinit var viewModel: AlertStatViewModel
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

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_alert_dashboard, container, false)
//        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(AlertStatViewModel::class.java)

        errorTextView.visibility = View.GONE
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE

        swipeTorefresh.setOnRefreshListener {
            errorTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
            viewModel.refreshByPassLocal()
            swipeTorefresh.isRefreshing = false
        }

        recyclerView.apply {
            adapter = alertStatadapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context,LinearLayoutManager.VERTICAL))
        }

        viewModel.refresh()

        observeViewModel()
    }

    private fun observeViewModel() {
        Log.i(TAG, "Observing viewmodel")
        viewModel.alertStats.observe(viewLifecycleOwner, Observer { alertStats ->
            alertStats?.let {
                errorTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                Log.i(TAG, alertStats.size.toString())
                alertStatadapter.updateAlertStats(alertStats)
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


}
