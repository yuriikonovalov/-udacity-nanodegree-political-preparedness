package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.data.Repository
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel(private val repository: Repository, application: Application) : AndroidViewModel(application) {

    //TODO: Establish live data for representatives and address

    //TODO: Create function to fetch representatives from API from a provided address

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    private val _representatives = MutableLiveData<Result<List<Representative>>>()
    val representatives: LiveData<Result<List<Representative>>>
        get() = _representatives

    val isAddressValid: Boolean
        get() {
            return _address.value != null
//            _address.value?.let {
//                return !(_address.value!!.line1.isNullOrBlank()
//                        || _address.value!!.city.isNullOrBlank()
//                        || _address.value!!.zip.isNullOrBlank())
//            }
//            return false
        }


    fun loadRepresentatives() {
        val queryAddress = address.value!!.toFormattedString()
        _representatives.value = Result.Loading

        viewModelScope.launch {
            val result = repository.getRepresentatives(queryAddress)
            when (result) {
                is Result.Success -> {
                    val representativesList = result.data.offices.flatMap { office ->
                        office.getRepresentatives(result.data.officials)
                    }
                    _representatives.value = Result.Success(representativesList)
                }
                is Result.Error -> {
                    _representatives.value = Result.Error(result.exception)
                }
            }
        }
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */
    fun enableLoadingState() {
        _representatives.value = Result.Loading
    }

    //TODO: Create function get address from geo location
    fun setAddress(address: Address) {
        _address.value = address
    }

    //TODO: Create function to get address from individual fields
    fun setAddress(line1: String, line2: String? = null, city: String, state: String?, zip: String) {
        val composedAddress = Address(line1, line2, city, state, zip)
        _address.value = composedAddress
    }
}
