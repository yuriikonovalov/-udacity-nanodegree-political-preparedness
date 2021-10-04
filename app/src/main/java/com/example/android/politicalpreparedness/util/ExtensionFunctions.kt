package com.example.android.politicalpreparedness.util

import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import com.example.android.politicalpreparedness.R

fun TextView.showIfNotNull(value: String?, setData: Boolean = true) {
    if (value != null) {
        this.visibility = View.VISIBLE
        if (setData) {
            this.text = value
        }
    } else {
        this.visibility = View.GONE
    }
}

fun Group.showIfNotNull(value: String?) {
    if (value == null) {
        this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
    }
}

fun showNoInternetConnectionToast(context: Context) {
    Toast.makeText(context, context.getString(R.string.error_no_internet_connection), Toast.LENGTH_SHORT).show()
}