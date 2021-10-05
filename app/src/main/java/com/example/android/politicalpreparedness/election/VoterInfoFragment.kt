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

    private lateinit var snackBar: Snackbar


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.voterInfo.observe(viewLifecycleOwner, Observer { result ->
            binding.resultVoterInfoResponse = result
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

        loadVoterInfo()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Hide a snackbar if the user presses the ack button instead "Retry"
        hideSnackbarRetry()
    }


    private fun loadVoterInfo() {
        if (InternetConnection.isConnected(requireContext())) {
            viewModel.getVoterInfo()
        } else {
            showSnackBarRetry()
            viewModel.setResultError()
        }
    }

    private fun showSnackBarRetry() {
        snackBar = Snackbar.make(
                requireView(),
                getString(R.string.error_no_network_connection),
                Snackbar.LENGTH_INDEFINITE)

        snackBar.setAction(getString(R.string.retry)) {
            loadVoterInfo()
        }

        snackBar.show()
    }

    private fun hideSnackbarRetry() {
        if (::snackBar.isInitialized && snackBar.isShown) {
            snackBar.dismiss()
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


}


