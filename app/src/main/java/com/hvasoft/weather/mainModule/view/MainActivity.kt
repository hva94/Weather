package com.hvasoft.weather.mainModule.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.hvasoft.weather.BR
import com.hvasoft.weather.R
import com.hvasoft.weather.common.entities.Forecast
import com.hvasoft.weather.common.utils.CommonUtils
import com.hvasoft.weather.databinding.ActivityMainBinding
import com.hvasoft.weather.mainModule.view.adapter.ForecastAdapter
import com.hvasoft.weather.mainModule.view.adapter.OnClickListener
import com.hvasoft.weather.mainModule.viewModel.MainViewModel
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: ForecastAdapter
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setupViewModel()
        setupObservers()
        setupAdapter()
        setupRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        getLocationAndRequestWeather()
    }

    private fun setupViewModel() {
        val viewModel: MainViewModel by viewModels()
        mBinding.lifecycleOwner = this
        mBinding.setVariable(BR.viewModel, viewModel)
    }

    private fun setupObservers() {
        mBinding.viewModel?.let {
            it.getSnackbarMsg().observe(this) { resMsg ->
                Snackbar.make(mBinding.root, resMsg, Snackbar.LENGTH_LONG).show()
            }
            it.getResult().observe(this) { result ->
                mAdapter.submitList(result.hourly)
            }
        }
    }

    private fun setupAdapter() {
        mAdapter = ForecastAdapter(this)
    }

    private fun setupRecyclerView() {
        mBinding.mainRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.mAdapter
        }
    }

    private fun getLocationAndRequestWeather() {
        if (!isLocationEnabled()) {
            Toast.makeText(this, R.string.main_location_off, Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            return
        }

        if (isLocationPermissionAllowed()) {
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED)
                {
                    requestLocationPermission()
                    return
                }
                mFusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        Toast.makeText(
                            this,
                            R.string.main_error_get_location, Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        Log.i(TAG, "CurrentLocation: Latitude: $latitude Longitude: $longitude")
                        lifecycleScope.launch {
                            mBinding.viewModel?.getWeatherAndForecast(
                                latitude,
                                longitude,
                                getString(R.string.api_key),
                                getString(R.string.api_param_exclude),
                                getString(R.string.api_param_units),
                                getString(R.string.api_param_lang)
                            )
                        }
                    }
                }
            } else {
            Toast.makeText(this,
                R.string.main_permission_location_required, Toast.LENGTH_LONG).show()
            requestLocationPermission()
            return
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun isLocationPermissionAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission(): Boolean {
        if (!isLocationPermissionAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this as Activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                showLocationPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(
                    this as Activity,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    PERMISSION_REQUEST_ACCESS_LOCATION
                )
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_ACCESS_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocationAndRequestWeather()
                } else {
                    requestLocationPermission()
                }
                return
            }
        }
    }

    private fun showLocationPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_permission_location)
            .setMessage(R.string.dialog_permission_location_message)
            .setPositiveButton(
                R.string.dialog_permission_location_positive
            ) { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton(R.string.dialog_negative_button) { _, _ ->
                Toast.makeText(this,
                    R.string.main_permission_location_required, Toast.LENGTH_SHORT).show()
                finish()
            }
            .show()
    }
    
    /**
     * OnClickListener
     * */
    override fun onClick(forecast: Forecast) {
        val date = CommonUtils.getDate(forecast.dt)
        val hour = CommonUtils.getHour(forecast.dt)
        val temperature = CommonUtils.getTemp(forecast.temp)
        val humidity = CommonUtils.getHumidity(forecast.humidity)
        Snackbar.make(
            mBinding.root, "The forecast temperature on $date at $hour will be " +
                    "$temperature with a humidity of $humidity", Snackbar.LENGTH_LONG
        ).show()
    }
}