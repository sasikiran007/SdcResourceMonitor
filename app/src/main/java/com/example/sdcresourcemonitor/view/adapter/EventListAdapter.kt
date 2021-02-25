package com.example.sdcresourcemonitor.view.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sdcresourcemonitor.R
import com.example.sdcresourcemonitor.databinding.EventHistoryBinding
import com.example.sdcresourcemonitor.model.AlertStat
import com.example.sdcresourcemonitor.model.Event
import com.example.sdcresourcemonitor.view.EventDetails
import com.example.sdcresourcemonitor.view.listener.ViewOnClickListener
import java.time.ZoneOffset
import java.time.ZonedDateTime

class EventListAdapter(private val events: ArrayList<Event>) :

    RecyclerView.Adapter<EventListAdapter.ViewHolder>() , ViewOnClickListener{

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateEvents(newEvents: List<Event>) {
//        Log.i(TAG, "update method called : ${newAlertStats.size}")
        events.clear()
//        newEvents.filter{it.startTime.toLong() > ZonedDateTime.now(ZoneOffset.UTC).plusWeeks(-1).toEpochSecond()}.sortedByDescending{it.startTime.toLong()}
        events.addAll(newEvents.filter {
            it.startTime.toLong() > ZonedDateTime.now(ZoneOffset.UTC).plusWeeks(-2).toEpochSecond()
        }.sortedByDescending { it.startTime.toLong() })
        notifyDataSetChanged()
    }

    class ViewHolder(var view: EventHistoryBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<EventHistoryBinding>(
            LayoutInflater.from(parent.context), R.layout.event_history, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.event = events[position]
        holder.view.listener = this
        holder.view.position = position
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onClick(view: View) {
        val event = events[view.tag.toString().toInt()]
        val activity: AppCompatActivity = view.context as AppCompatActivity
//        Toast.makeText(view.context,"Hello : "+event.hostname,Toast.LENGTH_SHORT).show()
        EventDetails.newInstance(event).show(activity.supportFragmentManager,"Hello00")
    }

}