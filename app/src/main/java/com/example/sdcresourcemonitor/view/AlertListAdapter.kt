package com.example.sdcresourcemonitor.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sdcresourcemonitor.R
import com.example.sdcresourcemonitor.databinding.AlertItemBinding
import com.example.sdcresourcemonitor.model.Alert
import kotlinx.android.synthetic.main.alert_item.view.*
import kotlinx.android.synthetic.main.sample.view.*

class AlertListAdapter(val alerts : ArrayList<Alert>) : RecyclerView.Adapter<AlertListAdapter.AlertListViewHolder>() {


    class AlertListViewHolder(val view : AlertItemBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<AlertItemBinding>(inflater,R.layout.alert_item,parent,false)
//        val view = inflater.inflate(R.layout.alert_item,parent,false)
        return AlertListViewHolder(view)
    }

    override fun getItemCount() = alerts.size

    override fun onBindViewHolder(holder: AlertListViewHolder, position: Int) {
        holder.view.alert = alerts[position]
        holder.view.sl = position + 1
//        holder.view.message.text = alerts[position].message
//        holder.view.entity.text = alerts[position].entity
//        holder.view.time.text = alerts[position].date.toString()
    }

    fun update(newAlerts : List<Alert>) {
        alerts.clear()
        alerts.addAll(newAlerts)
        notifyDataSetChanged()
    }
}