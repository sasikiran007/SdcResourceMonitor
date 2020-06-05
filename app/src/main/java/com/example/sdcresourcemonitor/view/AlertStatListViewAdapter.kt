package com.example.sdcresourcemonitor.view

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
import kotlinx.android.synthetic.main.alert_stat_header_item.view.*
import kotlinx.android.synthetic.main.alert_stat_item.view.*
import javax.net.ssl.HostnameVerifier

class AlertStatListViewAdapter(val alertStats: ArrayList<AlertStat>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),ViewOnClickListener {

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1
    private val TAG = "Adapter"

    private var alertSection = "all"
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
            ItemViewHolder(view)
        } else {
            val view = DataBindingUtil.inflate<AlertStatHeaderItemBinding>(
                inflater,
                R.layout.alert_stat_header_item,
                parent,
                false
            )
            HeadViewHolder(view)
        }

    }

    override fun getItemCount() = alertStats.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val alertStat = alertStats[position]
        if (holder is ItemViewHolder) {
            holder.view.alertStat = alertStats[position]
            holder.view.listener = this
//            holder.itemView.sectionTextView.text = alertStat.section.toUpperCase()
//            holder.itemView.crCount.text = alertStat.criticalCount.toString()
//            holder.itemView.mjCount.text = alertStat.majorCount.toString()
//            holder.itemView.mnCount.text = alertStat.minorCount.toString()
            Log.i("Binded :", "$position")
        } else {
            (holder as HeadViewHolder).view.alertStat = alertStats[position]
            (holder as HeadViewHolder).view.listener = this
//            holder.itemView.totalCrCount.text = alertStat.criticalCount.toString()
//            holder.itemView.totalMjCount.text = alertStat.majorCount.toString()
//            holder.itemView.totalMnCount.text = alertStat.minorCount.toString()
        }
    }

    class HeadViewHolder(val view: AlertStatHeaderItemBinding) : RecyclerView.ViewHolder(view.root)
    class ItemViewHolder(val view: AlertStatItemBinding) : RecyclerView.ViewHolder(view.root)

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return TYPE_HEADER
        else return TYPE_ITEM
    }

    override fun onClick(view: View) {
        view.sectionTextView
        var alertLevel: String
        when (view.tag) {
            "10"  -> alertLevel = "all"
            "11" -> alertLevel = "critical"
            "12" -> alertLevel = "major"
            "13" -> alertLevel = "minor"
            else -> alertLevel = "all"

        }
        Toast.makeText(view.context, "Item clicked!!! ${view.tag} : $alertLevel", Toast.LENGTH_SHORT).show()
        Navigation.findNavController(view).navigate(
            AlertDashBoardDirections.actionAlertDashBoard2ToAlertListFragment(
                alertLevel = alertLevel
            )

        )


    }


}