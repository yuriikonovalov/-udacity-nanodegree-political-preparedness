package com.example.android.politicalpreparedness.election.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.util.CommonConstants
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("dateToText")
fun TextView.formatDateToText(date: Date?) {
    val dateFormat = SimpleDateFormat(CommonConstants.ELECTION_DATE_TO_STRING_FORMAT, Locale.getDefault())
    date?.let {
        this.text = dateFormat.format(it)
    }
}