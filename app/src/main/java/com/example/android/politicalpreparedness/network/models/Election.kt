package com.example.android.politicalpreparedness.network.models

import androidx.room.*
import com.example.android.politicalpreparedness.util.CommonConstants
import com.squareup.moshi.*
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "election_table")
data class Election(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "electionDay") val electionDay: Date,
        @Embedded(prefix = "division_") @Json(name = "ocdDivisionId") val division: Division
)

val Election.formattedElectionDay: String
    get() {
        return SimpleDateFormat(CommonConstants.ELECTION_DATE_TO_STRING_FORMAT, Locale.getDefault())
                .format(this.electionDay)
    }