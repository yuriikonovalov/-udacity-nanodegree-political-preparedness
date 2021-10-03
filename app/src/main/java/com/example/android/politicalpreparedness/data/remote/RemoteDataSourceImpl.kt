package com.example.android.politicalpreparedness.data.remote

import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import java.lang.Exception

class RemoteDataSourceImpl(private val civicsApiService: CivicsApiService) : RemoteDataSource {

    override suspend fun getElections(): Result<List<Election>> {
        return try {
            val result = civicsApiService.getElections()
            Result.Success(result.elections)
        } catch (e: Exception) {
            Result.Error(e)
        }

    }

    override suspend fun getVoterInfo(address: String, electionId: Long): Result<VoterInfoResponse> {
        return try {
            val result = civicsApiService.getVoterInfo(address, electionId)
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getRepresentatives(address: String): Result<RepresentativeResponse> {
        return try {
            val result = civicsApiService.getRepresentatives(address)
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


}