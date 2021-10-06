package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.data.Repository
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import java.lang.Exception

class RepresentativeViewModel(private val repository: Repository, application: Application) : AndroidViewModel(application) {

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    private val _representatives = MutableLiveData<Result<List<Representative>>>()
    val representatives: LiveData<Result<List<Representative>>>
        get() = _representatives

    // Representative request requires at least 3 parameters: zip, city, address line1.
    val isAddressValid: Boolean
        get() {
            _address.value?.let {
                return !(_address.value!!.line1.isNullOrBlank()
                        || _address.value!!.city.isNullOrBlank()
                        || _address.value!!.zip.isNullOrBlank())
            }
            return false
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


    fun setLoadingState() {
        _representatives.value = Result.Loading
    }

    fun setErrorState() {
        _representatives.value = Result.Error(Exception())
    }

    fun setAddress(address: Address) {
        _address.value = address
    }

    fun setAddress(line1: String, line2: String? = null, city: String, state: String?, zip: String) {
        val composedAddress = Address(line1, line2, city, state, zip)
        _address.value = composedAddress
    }
}
