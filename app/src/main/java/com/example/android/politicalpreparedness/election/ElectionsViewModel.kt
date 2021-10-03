package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.data.Repository
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.SavedElection
import kotlinx.coroutines.launch


class ElectionsViewModel(private val repository: Repository, application: Application) : AndroidViewModel(application) {

    val upcomingElections = repository.observeElections()
    private val _savedElectionsIds = repository.observeSavedElectionsIds()

    private val _savedElections = Transformations.switchMap(_savedElectionsIds) {
        repository.observeSavedElections(it)
    }
    val savedElections: LiveData<Result<List<Election>>>
        get() = _savedElections

    fun loadElections() {
        viewModelScope.launch {
            repository.refreshElectionsCache()
        }
    }


    //TODO: Create functions to navigate to saved or upcoming election voter info

}