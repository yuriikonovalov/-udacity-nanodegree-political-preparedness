package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.util.InternetConnection
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class RepresentativeFragment : Fragment() {

    private val viewModel: RepresentativeViewModel by viewModel()
    private lateinit var binding: FragmentRepresentativeBinding

    private val requestForegroundLocationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            loadRepresentativesWithDeviceLocation()
        } else {
            Snackbar.make(binding.root, getString(R.string.snackbar_permission_denied), Snackbar.LENGTH_SHORT).show()
        }
    }

    private val requestLocationSettingsLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadRepresentativesWithDeviceLocation(true)
        } else {
            showToastNoLocation()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = RepresentativeListAdapter()

        binding.representativesList.apply {
            this.adapter = adapter
            this.layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.representatives.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Result.Success -> {
                    adapter.submitList(result.data)
                    binding.representativeProgressBar.visibility = View.GONE
                }
                is Result.Loading -> {
                    binding.representativeProgressBar.visibility = View.VISIBLE
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), getString(R.string.representatives_not_found), Toast.LENGTH_SHORT).show()
                    binding.representativeProgressBar.visibility = View.GONE
                }
            }

        })

        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            loadRepresentativesWithEnteredData()
        }

        binding.buttonUseMyLocation.setOnClickListener {
            loadRepresentativesWithDeviceLocation()
        }

        return binding.root
    }


    private fun loadRepresentativesWithEnteredData() {
        viewModel.setAddress(
                binding.addressLine1.text.toString(),
                binding.addressLine2.text.toString(),
                binding.city.text.toString(),
                binding.state.itemOrNotSelected(),
                binding.zip.text.toString()
        )

        if (!viewModel.isAddressValid) {
            Toast.makeText(requireContext(), getString(R.string.representative_address_not_valid), Toast.LENGTH_SHORT).show()
            return
        }

        if (InternetConnection.isConnected(requireContext())) {
            viewModel.loadRepresentatives()
        } else {
            showToastNoInternetConnection()
        }
    }

    private fun loadRepresentativesWithDeviceLocation(isLocationOn: Boolean = false) {
        if (!isPermissionGranted()) {
            requestLocationPermission()
            return
        }
        if (!isLocationOn) {
            checkLocationSettings()
            return
        }

        if (InternetConnection.isConnected(requireContext())) {
            getLocationAndLoadRepresentatives()
        } else {
            showToastNoInternetConnection()
        }

    }

    private fun requestLocationPermission() {
        requestForegroundLocationPermissionLauncher
                .launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun isPermissionGranted(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun checkLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(requireContext())
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            loadRepresentativesWithDeviceLocation(true)
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    requestLocationSettingsLauncher.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // ignore
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationAndLoadRepresentatives() {
        // Show a loading indicator while getting location
        viewModel.setLoadingState()

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationProviderClient.getCurrentLocation(
                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
                CancellationTokenSource().token
        )
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val address = geoCodeLocation(location)
                        viewModel.setAddress(address)
                        viewModel.loadRepresentatives()
                    } else {
                        showToastNoLocation()
                        viewModel.setErrorState()
                    }

                }
                .addOnFailureListener {
                    showToastNoLocation()
                    viewModel.setErrorState()
                }

    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare ?: "",
                            address.subThoroughfare ?: "",
                            address.locality ?: "",
                            address.adminArea ?: "",
                            address.postalCode ?: "")
                }
                .first()
    }

    private fun Spinner.itemOrNotSelected(): String? {
        return if (this.selectedItemPosition == 0) {
            null
        } else {
            this.selectedItem.toString()
        }
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun showToastNoLocation() {
        Toast.makeText(requireContext(),
                getString(R.string.representative_location_not_available), Toast.LENGTH_SHORT).show()
    }

    private fun showToastNoInternetConnection() {
        Toast.makeText(requireContext(), getString(R.string.error_no_internet_connection), Toast.LENGTH_SHORT).show()
    }

}