package com.example.android.politicalpreparedness.data

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.SavedElection
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse

interface Repository {

    suspend fun refreshElectionsCache()

    fun observeElections(): LiveData<Result<List<Election>>>

    fun observeSavedElections(idList: List<Int>): LiveData<Result<List<Election>>>

    fun observeSavedElectionsIds(): LiveData<List<Int>>

    fun observeSavedId(electionId: Int): LiveData<Int?>

    suspend fun insertSavedElection(savedElection: SavedElection)

    suspend fun deleteSavedElection(electionId: Int)

    suspend fun getVoterInfo(address: String, electionId: Long): Result<VoterInfoResponse>

    suspend fun getRepresentatives(address: String): Result<RepresentativeResponse>
}