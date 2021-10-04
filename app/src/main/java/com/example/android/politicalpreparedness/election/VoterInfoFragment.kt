package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.network.models.formattedElectionDay
import com.example.android.politicalpreparedness.util.*
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
            //  binding.resultVoterInfoResponse = result
            when (result) {
                is Result.Success -> {
                    displayResultSuccessUI(result.data)
                }
                is Result.Loading -> {
                    displayResultLoadingUI()
                }
                is Result.Error -> {
                    displayResultErrorUI()
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

        if (InternetConnection.isConnected(requireContext())) {
            viewModel.getVoterInfo()
        } else {
            showNoInternetConnectionToast(requireContext())
            viewModel.setResultError()
        }

        return binding.root
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

    private fun displayResultSuccessUI(response: VoterInfoResponse) {
        binding.progressBar.visibility = View.GONE

        val name = response.election.name
        val formattedDate = response.election.formattedElectionDay
        val votingLocationFinderUrl = response.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
        val ballotInfoUrl = response.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
        val addressString = response.state?.get(0)?.electionAdministrationBody?.correspondenceAddress?.toFormattedString()

        with(binding) {
            electionName.title = name
            electionDate.showIfNotNull(formattedDate)
            stateHeader.showIfNotNull(formattedDate, false)
            stateBallot.showIfNotNull(ballotInfoUrl, false)
            stateLocations.showIfNotNull(votingLocationFinderUrl, false)
            addressGroup.showIfNotNull(addressString)
            address.showIfNotNull(addressString)
        }
    }

    private fun displayResultLoadingUI() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun displayResultErrorUI() {
        binding.imageNoData.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }


}


