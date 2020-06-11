package com.example.sdcresourcemonitor.view.adapter

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
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sdcresourcemonitor.R
import com.example.sdcresourcemonitor.viewModel.AlertViewModel
import kotlinx.android.synthetic.main.fragment_alert_list.*
import kotlinx.android.synthetic.main.fragment_selected_alerts.*


private const val ARG_OBJECT1 = "alertSection"
private const val ARG_OBJECT2 = "alertLevel"

class AlertListTabbedAdapter(fragment : Fragment, val itemsCount: Int,val alertSection : String) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return itemsCount
    }

    override fun createFragment(position: Int): Fragment {
        val fragment  = SelectedAlertsFragment()
        fragment.arguments = Bundle().apply {
            putString(ARG_OBJECT1,alertSection)
            putInt(ARG_OBJECT2,position)
        }
        return fragment
    }
}

class SelectedAlertsFragment : Fragment() {

    lateinit var alertListAdapter : AlertListAdapter
    lateinit var viewModel : AlertViewModel
    private var _alertSection :String = "%%"
    private var _alertLevel = "%%"
    private val TAG = "SelectedAlertsFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_alert_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(this).get(AlertViewModel::class.java)
        alertListAdapter =
            AlertListAdapter(
                ArrayList()
            )

        arguments?.takeIf { it.containsKey(ARG_OBJECT1) }?.apply {
            _alertSection = getString(ARG_OBJECT1).toString()
        }
        arguments?.takeIf { it.containsKey(ARG_OBJECT2) }?.apply {
            _alertLevel = when(getInt(ARG_OBJECT2)) {
                0 -> "critical"
                1 -> "major"
                2 -> "minor"
                else -> "%%"
            }

        }
        Log.i(TAG,"$_alertLevel , $_alertSection")
        viewModel.refresh(_alertSection,_alertLevel)
        swipeRefreshList.setOnRefreshListener {
            swipeRefreshList.isRefreshing = false
            viewModel.isLoading.value = true
            viewModel.refreshByPassLocal(_alertSection,_alertLevel)
        }

        observeViewModel()


        alertListRecyclerView.apply {
            alertListRecyclerView.layoutManager = LinearLayoutManager(context)
            alertListRecyclerView.adapter = alertListAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }


    private fun observeViewModel() {
        viewModel.alerts.observe(viewLifecycleOwner, Observer { alerts ->
            alerts?.let {
                errorTextView.visibility = View.GONE
                progressBarList.visibility = View.GONE
                Log.i(TAG,"${alerts.size}")
                if(alerts.isEmpty()) {
                    noAlerts.visibility = View.VISIBLE
                    alertListRecyclerView.visibility = View.GONE
                }else {
                    noAlerts.visibility = View.GONE
                    alertListRecyclerView.visibility = View.VISIBLE
                }
                alertListAdapter.update(alerts)
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner,Observer { isLoading ->
            if(isLoading == true) {
                noAlerts.visibility = View.GONE
                alertListRecyclerView.visibility = View.GONE
                errorTextView.visibility = View.GONE
                progressBarList.visibility = View.VISIBLE
            }else {
                noAlerts.visibility = View.GONE
                progressBarList.visibility = View.GONE
                noAlerts.visibility = View.VISIBLE
            }
        })

        viewModel.hasError.observe(viewLifecycleOwner, Observer { hasError ->
            if(hasError == true) {
                noAlerts.visibility = View.GONE
                alertListRecyclerView.visibility = View.GONE
                errorTextView.visibility = View.VISIBLE
                progressBarList.visibility = View.GONE
            }else {
                errorTextView.visibility = View.GONE
                noAlerts.visibility = View.VISIBLE
            }
        })
    }

}
