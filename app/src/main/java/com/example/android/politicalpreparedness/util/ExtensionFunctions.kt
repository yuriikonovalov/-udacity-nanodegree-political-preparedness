package com.example.android.politicalpreparedness.util

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group

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