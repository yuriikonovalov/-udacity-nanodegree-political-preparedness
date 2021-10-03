package com.example.android.politicalpreparedness.util

import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
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

fun Toolbar.showIfNotNull(value: String?) {
    if (value != null) {
        this.visibility = View.VISIBLE
        this.title = value
    } else {
        this.visibility = View.GONE
    }
}

fun Group.showIfNotNull(value: String?) {
    if (value != null) {
        this.visibility = android.view.View.VISIBLE
    } else {
        this.visibility = android.view.View.GONE
    }
}

fun showNoInternetConnectionToast(context: Context){
    Toast.makeText(context, context.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
}