package com.example.android.politicalpreparedness.election

import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.network.models.formattedElectionDay


@BindingAdapter("electionName")
fun Toolbar.setElectionName(result: Result<VoterInfoResponse>) {
    when (result) {
        is Result.Success -> {
            visibility = View.VISIBLE
            this.title = result.data.election.name
        }
        is Result.Error -> {
            visibility = View.INVISIBLE
        }
        is Result.Loading -> {
            visibility = View.INVISIBLE
        }
    }
}

@BindingAdapter("electionDate")
fun TextView.setElectionDate(result: Result<VoterInfoResponse>) {
    when (result) {
        is Result.Success -> {
            visibility = View.VISIBLE
            this.text = result.data.election.formattedElectionDay
        }
        is Result.Error -> {
            visibility = View.INVISIBLE
        }
        is Result.Loading -> {
            visibility = View.INVISIBLE
        }
    }
}

@BindingAdapter("stateHeader")
fun TextView.setStateHeader(result: Result<VoterInfoResponse>) {
    when (result) {
        is Result.Success -> {
            val ballotInfoUrl = result.data.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
            val votingLocationFinderUrl = result.data.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl

            if (ballotInfoUrl.isNullOrBlank() && votingLocationFinderUrl.isNullOrBlank()) {
                visibility = View.INVISIBLE
            } else {
                visibility = View.VISIBLE
            }
        }
        is Result.Error -> {
            visibility = View.INVISIBLE
        }
        is Result.Loading -> {
            visibility = View.INVISIBLE
        }
    }
}

@BindingAdapter("stateVotingLocation")
fun TextView.setStateVotingLocation(result: Result<VoterInfoResponse>) {
    when (result) {
        is Result.Success -> {
            val votingLocationFinderUrl = result.data.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
            setVisibility(votingLocationFinderUrl)
        }
        is Result.Error -> {
            visibility = View.INVISIBLE
        }
        is Result.Loading -> {
            visibility = View.INVISIBLE
        }
    }
}

@BindingAdapter("stateBallotInfo")
fun TextView.setStateBallotInfo(result: Result<VoterInfoResponse>) {
    when (result) {
        is Result.Success -> {
            val ballotInfoUrl = result.data.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
            setVisibility(ballotInfoUrl)
        }
        is Result.Error -> {
            visibility = View.INVISIBLE
        }
        is Result.Loading -> {
            visibility = View.INVISIBLE
        }
    }
}

@BindingAdapter("stateCorrespondenceHeader")
fun TextView.setStateCorrespondenceHeader(result: Result<VoterInfoResponse>) {
    when (result) {
        is Result.Success -> {
            val correspondenceAddress = result.data.state?.get(0)?.electionAdministrationBody?.correspondenceAddress?.toFormattedString()
            setVisibility(correspondenceAddress)
        }
        is Result.Error -> {
            visibility = View.INVISIBLE
        }
        is Result.Loading -> {
            visibility = View.INVISIBLE
        }
    }
}

@BindingAdapter("address")
fun TextView.setAddress(result: Result<VoterInfoResponse>) {
    when (result) {
        is Result.Success -> {
            val correspondenceAddress = result.data.state?.get(0)?.electionAdministrationBody
                    ?.correspondenceAddress?.toFormattedString()
            setVisibilityAndText(correspondenceAddress)
        }
        is Result.Error -> {
            visibility = View.INVISIBLE
        }
        is Result.Loading -> {
            visibility = View.INVISIBLE
        }
    }
}

@BindingAdapter("buttonVoter")
fun Button.setButtonVoter(result: Result<VoterInfoResponse>) {
    when (result) {
        is Result.Success -> {
            visibility = View.VISIBLE
        }
        is Result.Error -> {
            visibility = View.INVISIBLE
        }
        is Result.Loading -> {
            visibility = View.INVISIBLE
        }
    }
}

@BindingAdapter("progressBarVisibility")
fun ProgressBar.setProgressBarVisibility(result: Result<VoterInfoResponse>) {
    when (result) {
        is Result.Success -> {
            visibility = View.GONE
        }
        is Result.Error -> {
            visibility = View.GONE
        }
        is Result.Loading -> {
            visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("imageNoData")
fun Button.setImageNoData(result: Result<VoterInfoResponse>) {
    when (result) {
        is Result.Success -> {
            visibility = View.GONE
        }
        is Result.Error -> {
            visibility = View.VISIBLE
        }
        is Result.Loading -> {
            visibility = View.GONE
        }
    }
}


private fun TextView.setVisibilityAndText(value: String?) {
    if (value.isNullOrBlank()) {
        visibility = View.INVISIBLE
    } else {
        visibility = View.VISIBLE
        this.text = value
    }
}

private fun TextView.setVisibility(value: String?) {
    visibility = if (value.isNullOrBlank()) {
        View.INVISIBLE
    } else {
        View.VISIBLE
    }
}