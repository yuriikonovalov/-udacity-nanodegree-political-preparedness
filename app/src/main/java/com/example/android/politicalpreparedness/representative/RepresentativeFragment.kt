package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.util.InternetConnection
import com.example.android.politicalpreparedness.util.showNoInternetConnectionToast
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class RepresentativeFragment : Fragment() {

    private val viewModel: RepresentativeViewModel by viewModel()
    private lateinit var binding: FragmentRepresentativeBinding

    private val requestForegroundLocationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            loadRepresentativesWithDeviceLocation()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = RepresentativeListAdapter()

        binding.representativesList.apply {
            this.adapter = adapter
            this.layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.representatives.observe(viewLifecycleOwner, Observer { result ->
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
            if (InternetConnection.isConnected(requireContext())) {
                loadRepresentativesWithEnteredData()
            } else {
                showNoInternetConnectionToast(requireContext())
            }

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

        if (viewModel.isAddressValid) {
            viewModel.loadRepresentatives()
        } else {
            Toast.makeText(requireContext(), getString(R.string.representative_address_not_valid), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadRepresentativesWithDeviceLocation() {
        checkLocationPermissions()

        if (InternetConnection.isConnected(requireContext())) {
            getLocationAndLoadRepresentatives()
        } else {
            showNoInternetConnectionToast(requireContext())
        }

    }

    private fun checkLocationPermissions() {
        if (!isPermissionGranted()) {
            requestLocationPermission()
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

    @SuppressLint("MissingPermission")
    private fun getLocationAndLoadRepresentatives() {
        viewModel.enableLoadingState()
        val errorToast = Toast.makeText(requireContext(),
                getString(R.string.representative_location_not_available), Toast.LENGTH_SHORT)

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationProviderClient.getCurrentLocation(
                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
                CancellationTokenSource().token
        )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val address = geoCodeLocation(task.result)
                        viewModel.setAddress(address)
                        viewModel.loadRepresentatives()
                    } else {
                        errorToast.show()
                    }

                }
                .addOnFailureListener {
                    errorToast.show()
                }

    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
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


}