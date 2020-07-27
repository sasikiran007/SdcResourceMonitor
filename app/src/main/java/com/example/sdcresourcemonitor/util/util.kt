package com.example.sdcresourcemonitor.util

import android.text.format.DateUtils
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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

@BindingAdapter("stringEpochText")
fun stringEpochToText(view: TextView, epoch: String?) {
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

@BindingAdapter("profilePicture")
fun loadProfilePicture(iView: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(iView.context)
            .load(url)
            .apply(RequestOptions().circleCrop())
            .into(iView)
    }
}
