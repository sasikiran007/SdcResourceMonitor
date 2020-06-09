package com.example.sdcresourcemonitor.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.sdcresourcemonitor.R
import com.example.sdcresourcemonitor.viewModel.AlertViewModel
import kotlinx.android.synthetic.main.fragment_alert_dashboard.*
import kotlinx.android.synthetic.main.fragment_alert_list.*
import kotlinx.android.synthetic.main.fragment_alert_list.errorTextView

class AlertListFragment : Fragment() {

    lateinit var alertListAdapter : AlertListAdapter
    lateinit var viewModel : AlertViewModel
    private var alertSection :String = "%%"
    private var alertLevel = "%%"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alert_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //init viewmodel
        viewModel = ViewModelProvider(this).get(AlertViewModel::class.java)
        alertListAdapter = AlertListAdapter(ArrayList())

        arguments?.let {
            alertSection = AlertListFragmentArgs.fromBundle(it).alertSection
        }

        viewModel.refresh(alertSection,alertLevel)
        swipeRefreshList.setOnRefreshListener {
            swipeRefreshList.isRefreshing = false
            viewModel.isLoading.value = true
            viewModel.refresh(alertSection,alertLevel)
        }

        observeViewModel()


        alertListRecyclerView.apply {
            alertListRecyclerView.layoutManager = LinearLayoutManager(context)
            alertListRecyclerView.adapter = alertListAdapter
            addItemDecoration(DividerItemDecoration(context,LinearLayoutManager.VERTICAL))
        }


    }

    private fun observeViewModel() {
        viewModel.alerts.observe(viewLifecycleOwner, Observer { alerts ->
            alerts?.let {
                errorTextView.visibility = View.GONE
                progressBarList.visibility = View.GONE
                alertListRecyclerView.visibility = View.VISIBLE
                alertListAdapter.update(alerts)
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner,Observer { isLoading ->
            if(isLoading == true) {
                alertListRecyclerView.visibility = View.GONE
                errorTextView.visibility = View.GONE
                progressBarList.visibility = View.VISIBLE
            }else {
                progressBarList.visibility = View.GONE
            }
        })

        viewModel.hasError.observe(viewLifecycleOwner, Observer { hasError ->
            if(hasError == true) {
                alertListRecyclerView.visibility = View.GONE
                errorTextView.visibility = View.VISIBLE
                progressBarList.visibility = View.GONE
            }else {
                errorTextView.visibility = View.GONE
            }
        })
    }


}
