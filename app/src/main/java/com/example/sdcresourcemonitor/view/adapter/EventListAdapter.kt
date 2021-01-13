package com.example.sdcresourcemonitor.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sdcresourcemonitor.R
import com.example.sdcresourcemonitor.databinding.EventHistoryBinding
import com.example.sdcresourcemonitor.model.Event

class EventListAdapter(private val events: ArrayList<Event>) :

    RecyclerView.Adapter<EventListAdapter.MyViewHolder>() {

    fun updateEvents(newEvents: List<Event>) {
//        Log.i(TAG, "update method called : ${newAlertStats.size}")
        events.clear()
        events.addAll(newEvents)
        notifyDataSetChanged()
    }

    class MyViewHolder(var view: EventHistoryBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = DataBindingUtil.inflate<EventHistoryBinding>(
            LayoutInflater.from(parent.context)
            , R.layout.event_history, parent, false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.view.event = events[position]
    }

    override fun getItemCount(): Int {
        return events.size
    }

}