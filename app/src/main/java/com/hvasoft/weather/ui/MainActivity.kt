package com.hvasoft.weather.ui

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
import com.hvasoft.weather.data.model.Forecast
import com.hvasoft.weather.core.utils.CommonUtils
import com.hvasoft.weather.databinding.ActivityMainBinding
import com.hvasoft.weather.ui.adapter.ForecastAdapter
import com.hvasoft.weather.ui.adapter.OnClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnClickListener {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ForecastAdapter
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setupRecyclerView()
        setupViewModel()
        getLocationAndWeather()
    }

    override fun onStart() {
        super.onStart()
        refreshWeather()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupRecyclerView() {
        adapter = ForecastAdapter(this)

        binding.mainRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupViewModel() {
        val mainViewModel: MainViewModel by viewModels()
        binding.lifecycleOwner = this
        binding.setVariable(BR.viewModel, mainViewModel)

        binding.viewModel?.let {
            it.result.observe(this) { result ->
                adapter.submitList(result.hourly)
            }
            it.snackbarMsg.observe(this) { msgRes ->
                Snackbar.make(binding.root, msgRes, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun getLocationAndWeather() {
        if (!isLocationServiceEnabled()) {
            Toast.makeText(this, R.string.main_location_off, Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            return
        }

        if (isLocationPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                isLocationPermissionAccepted()
                return
            }
            fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                val location: Location? = task.result
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.i(TAG, "CurrentLocation: Latitude: $latitude Longitude: $longitude")
                    getWeather(latitude, longitude)
                } else
                    Toast.makeText(
                        this, R.string.main_error_get_location, Toast.LENGTH_LONG
                    ).show()
            }
        } else {
            Toast.makeText(
                this, R.string.main_permission_location_required, Toast.LENGTH_LONG
            ).show()
            isLocationPermissionAccepted()
            return
        }
    }

    private fun getWeather(latitude: Double, longitude: Double) {
        lifecycleScope.launch {
            binding.viewModel?.getWeatherAndForecast(
                latitude,
                longitude,
                getString(R.string.api_key),
                getString(R.string.api_param_exclude),
                getString(R.string.api_param_units),
                getString(R.string.api_param_lang)
            )
        }
    }

    private fun refreshWeather() {
        with(binding.mainSwipeRefresh) {
            setOnRefreshListener {
                getLocationAndWeather()
                isRefreshing = false
            }
        }
    }

    private fun isLocationServiceEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationPermissionAccepted(): Boolean {
        if (!isLocationPermissionGranted()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this as Activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) showPermissionDeniedDialog()
            else {
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

    private fun showPermissionDeniedDialog() {
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
                Toast.makeText(
                    this, R.string.main_permission_location_required, Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            .show()
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
                    getLocationAndWeather()
                } else {
                    isLocationPermissionAccepted()
                }
                return
            }
        }
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
            binding.root,
            getString(
                R.string.main_message_forecast_temperature,
                date,
                hour,
                temperature,
                humidity
            ),
            Snackbar.LENGTH_LONG
        ).show()
    }
}