package com.example.sdcresourcemonitor.view

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.sdcresourcemonitor.R
import com.example.sdcresourcemonitor.util.AlertLevel
import com.example.sdcresourcemonitor.view.adapter.AlertListTabbedAdapter
import com.example.sdcresourcemonitor.viewModel.AlertViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.alert_item.*
import kotlinx.android.synthetic.main.fragment_alert_list_tabbed.*


class AlertListTabbedFragment : Fragment() {
    private lateinit var alertListTabbedAdapter: AlertListTabbedAdapter
    private lateinit var viewPager: ViewPager2
    private var _alertSection = "%%"
    private val TAG = "AlertListTabbedFragment"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alert_list_tabbed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        arguments?.let {
            _alertSection = AlertListTabbedFragmentArgs.fromBundle(it).alertSection
            Log.i(TAG, _alertSection)
        }


        alertListTabbedAdapter = AlertListTabbedAdapter(this, 3, _alertSection,"%%")
        viewPager = view_pager
        viewPager.isUserInputEnabled = false
        viewPager.adapter = alertListTabbedAdapter
//        val tabLayout = view.findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->

            tab.text = when (position) {
                0 -> "Critical"
                1 -> "Major"
                2 -> "Minor"
                else -> "All"
            }
        }.attach()

    }


    companion object {
        fun getInstance(): Fragment {
            return AlertListTabbedFragment()
        }
    }

}