package com.example.sdcresourcemonitor.util

import android.text.format.DateFormat
import android.text.format.DateUtils
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sdcresourcemonitor.R
import com.example.sdcresourcemonitor.model.Event
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("longTime")
fun longTime(view: TextView, epoch: Long?) {
    if (epoch != null) {
        view.text = DateUtils.getRelativeTimeSpanString(
            epoch * 1000,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        ).toString()

    } else {
        view.text = "error"
    }
}

@BindingAdapter("textTime")
fun textTime(view: TextView, epoch: String?) {
    val epochLong = epoch?.toLongOrNull()
    if (epochLong != null) {
        view.text = DateUtils.getRelativeTimeSpanString(
            epochLong * 1000,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        ).toString()

    } else {
        view.text = "error"
    }
}

//@BindingAdapter("stringEpochText")
//fun stringEpochToText(view: TextView, epoch: String?) {
//    val epochLong = epoch?.toLongOrNull()
//
//    if (epochLong != null) {
//        view.text = DateUtils.getRelativeTimeSpanString(
//            epochLong * 1000,
//            System.currentTimeMillis(),
//            DateUtils.MINUTE_IN_MILLIS
//        ).toString()
//    } else {
//        view.text = "error"
//    }`
//}

@BindingAdapter("profilePicture")
fun loadProfilePicture(iView: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(iView.context)
            .load(url)
            .apply(RequestOptions().circleCrop())
            .into(iView)
    }
}

@BindingAdapter("timeMessage")
fun timeMessage(view : TextView, event : Event?) {
    if(event != null ){

        val startTime = event.startTime
        val endTime = event.endTime
        val isEnded = event.isEnded
        val endTimeLong = endTime.toLongOrNull()
        val startTimeLong = startTime.toLongOrNull()
        if(endTimeLong != null && startTimeLong != null) {

            val format = SimpleDateFormat("dd/MM/yy HH:mm:ss")
            val endTimeF = Date(endTimeLong)
            val startTimeF = Date(startTimeLong)
            Log.i("TimeMessage","$endTimeF - $startTimeF")
            if (isEnded == 1) {
                view.text = "${format.format(startTimeF)} - ${format.format(endTimeF)}"
            } else {
                view.text = "${format.format(startTimeF)}"

            }
        }else {
            view.text = ""
        }
    }
}

