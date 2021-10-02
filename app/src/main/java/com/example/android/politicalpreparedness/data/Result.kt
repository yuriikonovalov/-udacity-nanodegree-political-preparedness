package com.example.android.politicalpreparedness.data

object ResultConstants {
    const val ELECTION_NOT_FOUND = "Election not found."
    const val ELECTIONS_NOT_FOUND = "Elections not found."
}

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

}
