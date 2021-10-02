package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.models.Election
import org.koin.androidx.viewmodel.ext.android.viewModel


class ElectionsFragment : Fragment() {

    //TODO: Declare ViewModel
    private val viewModel: ElectionsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        //TODO: Add ViewModel values and create ViewModel

        //TODO: Add binding values
        val binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val upcomingElectionsAdapter = ElectionListAdapter(ElectionListener { election ->
            navigateToVoterInfo(election)
        })

        val savedElectionsAdapter = ElectionListAdapter(ElectionListener { election ->
            navigateToVoterInfo(election)
        })


        binding.upcomingElectionsList.apply {
            this.adapter = upcomingElectionsAdapter
            this.layoutManager = LinearLayoutManager(context)
        }
        binding.savedElectionsList.apply {
            this.adapter = savedElectionsAdapter
            this.layoutManager = LinearLayoutManager(context)
        }


        viewModel.upcomingElections.observe(viewLifecycleOwner, Observer { elections ->
            elections as Result.Success
            upcomingElectionsAdapter.submitList(elections.data)
        })

        viewModel.savedElections.observe(viewLifecycleOwner, Observer { savedElections ->
            savedElections as Result.Success
            savedElectionsAdapter.submitList(savedElections.data)
        })


        //TODO: Link elections to voter info

        //TODO: Initiate recycler adapters

        //TODO: Populate recycler adapters
        return binding.root
    }

    private fun navigateToVoterInfo(election: Election) {
        this.findNavController().navigate(
                ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        election.id, election.division
                ))
    }


    override fun onStart() {
        super.onStart()
        //TODO: CHECK INTERNET CONNECTION
        viewModel.loadElections()
    }

}