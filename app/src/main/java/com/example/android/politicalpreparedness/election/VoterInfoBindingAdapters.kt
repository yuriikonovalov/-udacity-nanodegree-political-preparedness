package com.example.android.politicalpreparedness.election

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.network.models.formattedElectionDay


@BindingAdapter("app:electionName")
fun Toolbar.setElectionName(result: Result<VoterInfoResponse>) {
    when (result) {
        is Result.Success -> {
            title = result.data.election.name
        }
        is Result.Error -> {
            title = context.getString(R.string.error_something_went_wrong)
        }
        is Result.Loading -> {
            title = ""
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
        else -> {
            visibility = View.INVISIBLE
        }
    }
}

@BindingAdapter("stateVotingLocation")
fun TextView.setStateVotingLocation(result: Result<VoterInfoResponse>) {
    when (result) {
        is Result.Success -> {
            val votingLocationFinderUrl = result.data.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
            if (votingLocationFinderUrl.isNullOrBlank()) {
                visibility = View.INVISIBLE
            } else {
                visibility = View.VISIBLE
            }

        }
        else -> {
            visibility = View.INVISIBLE
        }

    }
}

@BindingAdapter("stateBallotInfo")
fun TextView.setStateBallotInfo(result: Result<VoterInfoResponse>) {
    when (result) {
        is Result.Success -> {
            val ballotInfoUrl = result.data.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
            if (ballotInfoUrl.isNullOrBlank()) {
                visibility = View.INVISIBLE
            } else {
                visibility = View.VISIBLE
            }

        }
        else -> {
            visibility = View.INVISIBLE
        }
    }
}

@BindingAdapter("stateCorrespondenceHeader")
fun TextView.setStateCorrespondenceHeader(result: Result<VoterInfoResponse>) {
    when (result) {
        is Result.Success -> {
            val correspondenceAddress = result.data.state?.get(0)?.electionAdministrationBody?.correspondenceAddress?.toFormattedString()
            if (correspondenceAddress.isNullOrBlank()) {
                visibility = View.INVISIBLE
            } else {
                visibility = View.VISIBLE
            }
        }
        else -> {
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
        else -> {
            visibility = View.INVISIBLE
        }
    }
}

@BindingAdapter("buttonVoter")
fun Button.setButtonVoter(result: Result<VoterInfoResponse>) {
    when (result) {
        is Result.Error -> {
            visibility = View.INVISIBLE
        }
    }
}

@BindingAdapter("progressIndicator")
fun ProgressBar.setProgressIndicator(result: Result<VoterInfoResponse>) {
    when (result) {
        is Result.Loading -> {
            visibility = View.VISIBLE
        }
        else -> {
            visibility = View.GONE
        }
    }
}

@BindingAdapter("imageNoData")
fun ImageView.setImageNoData(result: Result<VoterInfoResponse>) {
    when (result) {
        is Result.Error -> {
            visibility = View.VISIBLE
        }
        else -> {
            visibility = View.GONE
        }
    }
}

private fun TextView.setVisibilityAndText(value: String?) {
    if (value.isNullOrBlank()) {
        visibility = View.INVISIBLE
    } else {
        visibility = View.VISIBLE
        text = value
    }
}
