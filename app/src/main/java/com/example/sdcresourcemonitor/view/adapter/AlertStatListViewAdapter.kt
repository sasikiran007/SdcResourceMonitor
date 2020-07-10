package com.example.sdcresourcemonitor.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.sdcresourcemonitor.R
import com.example.sdcresourcemonitor.databinding.AlertStatHeaderItemBinding
import com.example.sdcresourcemonitor.databinding.AlertStatItemBinding
import com.example.sdcresourcemonitor.model.AlertStat
import com.example.sdcresourcemonitor.model.AlertTracker
import com.example.sdcresourcemonitor.view.AlertDashBoardDirections
import com.example.sdcresourcemonitor.view.listener.ViewOnClickListener

class AlertStatListViewAdapter(private val alertStats: ArrayList<AlertStat>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    ViewOnClickListener {

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1
    private val TAG = "Adapter"

    private var alertSection = "%%"
    fun updateAlertStats(newAlertStats: List<AlertStat>) {
        Log.i(TAG, "update method called : ${newAlertStats.size}")
        alertStats.clear()
        alertStats.addAll(newAlertStats)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        Log.i(TAG, "View created : $viewType")

        return if (viewType == TYPE_ITEM) {
            val view = DataBindingUtil.inflate<AlertStatItemBinding>(
                inflater,
                R.layout.alert_stat_item,
                parent,
                false
            )
            ItemViewHolder(
                view
            )
        } else {
            val view = DataBindingUtil.inflate<AlertStatHeaderItemBinding>(
                inflater,
                R.layout.alert_stat_header_item,
                parent,
                false
            )
            HeadViewHolder(
                view
            )
        }

    }

    override fun getItemCount() = alertStats.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val alertStat = alertStats[position]
            holder.view.alertStat = alertStat
            holder.view.listener = this
        } else {
            (holder as HeadViewHolder).view.alertStat = alertStats[position]

            holder.view.listener = this
        }
    }

    class HeadViewHolder(val view: AlertStatHeaderItemBinding) : RecyclerView.ViewHolder(view.root)
    class ItemViewHolder(val view: AlertStatItemBinding) : RecyclerView.ViewHolder(view.root)

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return TYPE_HEADER
        else return TYPE_ITEM
    }

    override fun onClick(view: View) {
        var alertSection: String = view.tag.toString()
        val title = "$alertSection Alerts"
        val newTitle = title.capitalize()
        if (alertSection == "all") alertSection = "%%"

//        Toast.makeText(view.context, "Item clicked!!! $alertSection", Toast.LENGTH_SHORT).show()
        Navigation.findNavController(view).navigate(
            AlertDashBoardDirections.actionAlertDashBoard2ToAlertListFragment2(
                alertSection = alertSection, title = newTitle
            )
        )


    }


}