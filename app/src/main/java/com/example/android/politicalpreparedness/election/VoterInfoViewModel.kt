package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.data.Repository
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.SavedElection
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.network.models.queryFormattedAddress
import com.example.android.politicalpreparedness.util.ButtonState
import com.example.android.politicalpreparedness.util.SingleEvent
import kotlinx.coroutines.launch
import java.lang.Exception


class VoterInfoViewModel(private val currentElectionId: Int,
                         private val division: Division,
                         private val repository: Repository,
                         application: Application) : AndroidViewModel(application) {

    private val _voterInfo = MutableLiveData<Result<VoterInfoResponse>>()
    val voterInfo: LiveData<Result<VoterInfoResponse>>
        get() = _voterInfo

    private var _openBallotUrl = SingleEvent<String>()
    val openBallotUrl: LiveData<String>
        get() = _openBallotUrl

    private var _openVotingLocationFinderUrl = SingleEvent<String>()
    val openVotingLocationFinderUrl: LiveData<String>
        get() = _openVotingLocationFinderUrl

    private val savedId = repository.observeSavedId(currentElectionId)

    // If there's no the current id in the DB, set state to FOLLOW.
    // If the current id is saved in DB, set state to UNFOLLOW.
    val buttonState: LiveData<ButtonState> = Transformations.map(savedId) { id ->
        if (id == null) {
            return@map ButtonState.FOLLOW
        } else {
            return@map ButtonState.UNFOLLOW
        }
    }


    fun getVoterInfo() {
        _voterInfo.value = Result.Loading
        viewModelScope.launch {
            _voterInfo.value = repository.getVoterInfo(division.queryFormattedAddress, currentElectionId.toLong())
        }
    }

    fun setResultError() {
        _voterInfo.value = Result.Error(Exception())
    }

    // Trigger only when the URL is not Null
    fun openBallotUrl() {
        val result = voterInfo.value as Result.Success
        val url = result.data.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
        _openBallotUrl.value = url!!
    }

    // Trigger only when the URL is not Null
    fun openVotingLocationFinderUrl() {
        val result = voterInfo.value as Result.Success
        val url = result.data.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
        _openVotingLocationFinderUrl.value = url!!
    }

    fun onButtonClick() {
        if (buttonState.value == ButtonState.FOLLOW) {
            insertSavedElection(currentElectionId)
        } else {
            deleteSavedElection(currentElectionId)
        }
    }

    private fun insertSavedElection(electionId: Int) {
        viewModelScope.launch {
            repository.insertSavedElection(SavedElection(electionId))
        }
    }

    private fun deleteSavedElection(electionId: Int) {
        viewModelScope.launch {
            repository.deleteSavedElection(electionId)
        }
    }
}