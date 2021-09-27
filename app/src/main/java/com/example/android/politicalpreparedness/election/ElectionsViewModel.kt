package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.data.ElectionsRepository
import com.example.android.politicalpreparedness.data.ElectionsRepositoryImpl
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import java.util.*


//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(private val repository: ElectionsRepository, application: Application) : AndroidViewModel(application) {


    //TODO: Create live data val for upcoming elections

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info

}