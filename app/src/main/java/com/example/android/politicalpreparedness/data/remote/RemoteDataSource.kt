package com.example.android.politicalpreparedness.data.remote

import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse

interface RemoteDataSource {
    suspend fun getElections(): Result<List<Election>>

    suspend fun getVoterInfo(address: String, electionId: Long): Result<VoterInfoResponse>
}