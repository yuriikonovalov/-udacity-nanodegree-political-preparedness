package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.network.models.formattedElectionDay
import com.example.android.politicalpreparedness.util.ButtonState
import com.example.android.politicalpreparedness.util.InternetConnection
import com.example.android.politicalpreparedness.util.showIfNotNull
import com.example.android.politicalpreparedness.util.showNoInternetConnectionToast
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class VoterInfoFragment : Fragment() {


    private lateinit var binding: FragmentVoterInfoBinding
    private val argDivision by lazy { VoterInfoFragmentArgs.fromBundle(requireArguments()).argDivision }
    private val argElectionId by lazy {
        VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId
    }

    private val viewModel: VoterInfoViewModel by viewModel() {
        parametersOf(argElectionId, argDivision)
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.viewModel = viewModel
//TODO: check internet connection
        viewModel.voterInfo.observe(viewLifecycleOwner, Observer { result ->
            // binding.resultVoterInfoResponse = result
            when (result) {
                is Result.Success -> {
                    hideProgressBar()
                    populateVoterInfo(result.data)
                }
                is Result.Loading -> {
                    showProgressBar()
                }
                is Result.Error -> {
                    hideProgressBar()
                    showNoDataUI()
                }
            }
        })

        viewModel.buttonState.observe(viewLifecycleOwner, Observer { state ->
            state?.let {
                when (state) {
                    ButtonState.FOLLOW -> {
                        binding.buttonVoter.text = getString(R.string.voter_follow_election)
                    }
                    ButtonState.UNFOLLOW -> {
                        binding.buttonVoter.text = getString(R.string.voter_unfollow_election)
                    }
                }
            }
        })

        viewModel.openBallotUrl.observe(viewLifecycleOwner, Observer { url ->
            openWebPage(url)
        })

        viewModel.openVotingLocationFinderUrl.observe(viewLifecycleOwner, Observer { url ->
            openWebPage(url)
        })

        return binding.root
    }

    private fun showNoDataUI() {
        binding.imageNoData.visibility = View.VISIBLE
        binding.voterInfoGroup.visibility = View.INVISIBLE
    }

    override fun onStart() {
        super.onStart()
        if (InternetConnection.isConnected(requireContext())) {
            viewModel.getVoterInfo()
        } else {
            showNoInternetConnectionToast(requireContext())
            viewModel.setResultError()
        }
    }


    private fun openWebPage(url: String) {
        val webUrl = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webUrl)

        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), getString(R.string.voter_no_app_to_open_link), Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateVoterInfo(response: VoterInfoResponse) {
        val name = response.election.name
        val formattedDate = response.election.formattedElectionDay
        val votingLocationFinderUrl = response.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
        val ballotInfoUrl = response.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
        val addressString = response.state?.get(0)?.electionAdministrationBody?.correspondenceAddress?.toFormattedString()

        with(binding) {
            electionName.showIfNotNull(name)
            electionDate.showIfNotNull(formattedDate)
            stateHeader.showIfNotNull(formattedDate, false)
            stateBallot.showIfNotNull(ballotInfoUrl, false)
            stateLocations.showIfNotNull(votingLocationFinderUrl, false)
            addressGroup.showIfNotNull(addressString)
            address.showIfNotNull(addressString)

            buttonVoter.visibility = View.VISIBLE
        }
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }


}


