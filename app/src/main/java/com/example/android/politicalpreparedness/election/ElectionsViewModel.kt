package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.data.Repository
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.util.RefreshCacheResult
import com.example.android.politicalpreparedness.util.SingleEvent
import kotlinx.coroutines.launch


class ElectionsViewModel(private val repository: Repository, application: Application) : AndroidViewModel(application) {

    val upcomingElections = repository.observeElections()
    private val _savedElectionsIds = repository.observeSavedElectionsIds()

    private val _savedElections = Transformations.switchMap(_savedElectionsIds) {
        repository.observeSavedElections(it)
    }
    val savedElections: LiveData<Result<List<Election>>>
        get() = _savedElections


    private val _navigateToVoterInfo = SingleEvent<Election?>()
    val navigateToVoterInfo: LiveData<Election?>
        get() = _navigateToVoterInfo

    // Used for observing the refreshing cache result.
    // If the result is not null - hide a loading indicator for SwipeRefreshLayout.
    private val _refreshCacheResult = SingleEvent<RefreshCacheResult?>()
    val refreshCacheResult: LiveData<RefreshCacheResult?>
        get() = _refreshCacheResult

    fun refreshElectionsCache() {
        viewModelScope.launch {
            _refreshCacheResult.value = repository.refreshElectionsCache()
        }
    }

    fun navigate(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun setRefreshCacheResult(result: RefreshCacheResult) {
        _refreshCacheResult.value = result
    }


}