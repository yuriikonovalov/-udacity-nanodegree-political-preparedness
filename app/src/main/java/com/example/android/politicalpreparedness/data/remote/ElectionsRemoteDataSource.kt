package com.example.android.politicalpreparedness.data.remote

import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.network.models.Election

interface ElectionsRemoteDataSource {
    suspend fun getElections(): Result<List<Election>>
}