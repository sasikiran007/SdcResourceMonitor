package com.example.sdcresourcemonitor.util

import android.text.format.DateUtils
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.sdcresourcemonitor.R

@BindingAdapter("textTime")
fun loadImage(view: TextView, epoch: Long?) {
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
