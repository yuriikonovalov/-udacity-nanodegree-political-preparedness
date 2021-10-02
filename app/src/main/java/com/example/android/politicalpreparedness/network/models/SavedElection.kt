package com.example.android.politicalpreparedness.network.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_elections_table")
data class SavedElection(
        @PrimaryKey val id: Int)

