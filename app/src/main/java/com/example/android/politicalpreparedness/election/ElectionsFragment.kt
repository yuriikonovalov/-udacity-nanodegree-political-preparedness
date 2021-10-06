package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.util.InternetConnection
import com.example.android.politicalpreparedness.util.RefreshCacheResult
import org.koin.androidx.viewmodel.ext.android.viewModel


class ElectionsFragment : Fragment() {

    private val viewModel: ElectionsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {


        val binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val upcomingElectionsAdapter = ElectionListAdapter(ElectionListener { election ->
            viewModel.navigate(election)
        })

        val savedElectionsAdapter = ElectionListAdapter(ElectionListener { election ->
            viewModel.navigate(election)
        })

        binding.upcomingElectionsList.apply {
            this.adapter = upcomingElectionsAdapter
            this.layoutManager = LinearLayoutManager(context)
        }
        binding.savedElectionsList.apply {
            this.adapter = savedElectionsAdapter
            this.layoutManager = LinearLayoutManager(context)
        }

        viewModel.upcomingElections.observe(viewLifecycleOwner, { elections ->
            elections as Result.Success
            upcomingElectionsAdapter.submitList(elections.data)
        })

        viewModel.savedElections.observe(viewLifecycleOwner, { savedElections ->
            savedElections as Result.Success
            savedElectionsAdapter.submitList(savedElections.data)
        })

        viewModel.navigateToVoterInfo.observe(viewLifecycleOwner, { election ->
            election?.let {
                navigateNoVoterInfoFragment(it)
            }
        })

        // Set listener for SwipeRefreshLayout
        binding.upcomingElectionsListSwipeLayout.setOnRefreshListener {
            onSwipeRefreshElectionsCache()
        }

        // Hide the loading indicator for the SwipeRefreshLayout
        viewModel.refreshCacheResult.observe(viewLifecycleOwner, { result ->
            result?.let {
                binding.upcomingElectionsListSwipeLayout.isRefreshing = false
            }
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        refreshElectionsCache()
    }

    private fun refreshElectionsCache(): Boolean {
        if (InternetConnection.isConnected(requireContext())) {
            viewModel.refreshElectionsCache()
            return true
        } else {
            showErrorRefreshCacheToast()
            return false
        }
    }

    private fun onSwipeRefreshElectionsCache() {
        if (!refreshElectionsCache()) {
            // Set result manually to hide the loading indicator in case,
            // when the device isn't connected to internet. In this case,
            // the refreshing function isn't invoked, so the result won't be updated.
            viewModel.setRefreshCacheResult(RefreshCacheResult.FAILURE)
        }
    }

    private fun navigateNoVoterInfoFragment(election: Election) {
        this.findNavController().navigate(
                ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        election.id, election.division
                ))
    }

    private fun showErrorRefreshCacheToast() {
        Toast.makeText(requireContext(), getString(R.string.error_couldnt_refresh_data), Toast.LENGTH_SHORT).show()
    }


}