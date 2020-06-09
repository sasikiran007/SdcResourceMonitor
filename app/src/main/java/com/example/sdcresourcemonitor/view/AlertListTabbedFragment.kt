package com.example.sdcresourcemonitor.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.sdcresourcemonitor.R
import com.example.sdcresourcemonitor.view.adapter.AlertListTabbedAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_alert_list_tabbed.*


class AlertListTabbedFragment : Fragment() {

    private lateinit var alertListTabbedAdapter : AlertListTabbedAdapter
    private lateinit var viewPager : ViewPager2

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
        alertListTabbedAdapter = AlertListTabbedAdapter(this,3)
        viewPager = view_pager
        viewPager.adapter = alertListTabbedAdapter
//        val tabLayout = view.findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = "OBJECT ${(position + 1)}"
        }.attach()
    }
    companion object {
        fun getInstance()  : Fragment{
            return AlertListTabbedFragment()
        }
    }

}