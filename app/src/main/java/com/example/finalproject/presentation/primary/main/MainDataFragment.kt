package com.example.finalproject.presentation.primary.main

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentMainDataBinding
import com.example.finalproject.model.datatype.weather.CurrentWeather
import com.example.finalproject.presentation.adapter.WeatherAdapter
import com.example.finalproject.presentation.primary.city.CityActivity
import com.example.finalproject.presentation.primary.splash.SplashActivity
import com.example.finalproject.presentation.viewmodel.MainViewModel
import com.example.finalproject.utils.Common
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class MainDataFragment: Fragment() {
    private lateinit var binding: FragmentMainDataBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var gson: Gson
    private var currentCity = ""
    private var gpsPermission = false
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            Log.d("RETURN_DATA", "success")
            val data = result?.data
            currentCity = data?.dataString!!
            updateWeather(currentCity)
        }
    }
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                gpsPermission = true
                if (currentCity == "")
                    locationCall()
            } else {
                Toast.makeText(context, R.string.no_geo_permission, Toast.LENGTH_SHORT).show()
            }
        }

    private fun apiCall(city: String) {
        Log.d("API_CALL", city)
        viewModel.getCurrentWeatherByCity(city).observe(this) { weather ->
            Log.d("API_CALL", weather.first().city)
            applyWeatherToView(weather)
        }
    }

    private fun apiCall(lat: Double, lon: Double) {
        viewModel.getCurrentWeatherByCoords(lat, lon).observe(this) { weather ->
            applyWeatherToView(weather)
        }
    }

    private fun applyWeatherToView(weather: List<CurrentWeather>) {
        if (weather.isEmpty()) {
            Toast.makeText(context, R.string.no_such_city, Toast.LENGTH_SHORT).show()
        }
        binding.currentWeather = weather.first()
        binding.weatherMainImage.setImageResource(resources.getIdentifier(
            weather.first().image, "drawable", activity!!.packageName
        ))
        val dates = weather.groupBy { it.date }.keys.toList().sortedBy { it }
        val todayWeather = weather.filter { it.date == dates[0] }
        var tomorrowWeather = emptyList<CurrentWeather>()
        if (dates.size > 1)
            tomorrowWeather = weather.filter { it.date == dates[1] }
        setupRecyclerView(binding.weatherRecyclerViewToday, todayWeather)
        setupRecyclerView(binding.weatherRecyclerViewTomorrow, tomorrowWeather)
        binding.refreshLayout.isRefreshing = false
        saveWeather(weather)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, weather: List<CurrentWeather>) {
        recyclerView.adapter = WeatherAdapter(weather)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)
    }

    private fun locationCall() {
        binding.refreshLayout.isRefreshing = true
        Log.d("LOCATION", "location call started")
        val locationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                throw IllegalAccessException()
        } catch (ex: Exception) {
            Toast.makeText(context, R.string.no_geo_permission, Toast.LENGTH_SHORT)
            loadWeather()
            return
        }
        val listener = LocationListener {
            Log.d("LOCATION", "location acquired")
            Log.d("LOCATION", "current city: $currentCity.")
            if (currentCity == "") {
                updateWeather(it.latitude, it.longitude)
                this.onStop()
            }
        }
        Log.d("LOCATION", "waiting for location started")
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, listener)
    }

    private fun updateWeather(city: String) {
        if (Common.isConnectedInternet(context!!))
            apiCall(city)
        else {
            Toast.makeText(context, R.string.offline_mode, Toast.LENGTH_SHORT).show()
            loadWeather()
        }
    }

    private fun updateWeather(lat: Double, lon: Double) {
        if (Common.isConnectedInternet(context!!))
            apiCall(lat, lon)
        else {
            Toast.makeText(context, R.string.offline_mode, Toast.LENGTH_SHORT).show()
            loadWeather()
        }
    }

    private fun saveWeather(weather: List<CurrentWeather>) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("Weather", gson.toJson(weather))
            apply()
        }
    }

    private fun loadWeather() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val json = sharedPref.getString("Weather", "")
        if (json == "") {
            Toast.makeText(context, R.string.no_internet_cache, Toast.LENGTH_SHORT).show()
            return
        }
        applyWeatherToView(gson.fromJson(json, Array<CurrentWeather>::class.java).asList())
    }

    private fun showSplashScreen() {
        startActivity(Intent(activity, SplashActivity::class.java))
    }

    private fun requestCity() {
        val intent = Intent(activity, CityActivity::class.java)
        getContent.launch(intent)
    }

    private fun requestGPSPermission() {
        if (ContextCompat.checkSelfPermission(
                context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            gpsPermission = true
            if (currentCity == "")
                locationCall()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gson = GsonBuilder().create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        requestGPSPermission()
        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.isRefreshing = true
            Log.d("REFRESH", currentCity)
            if (currentCity == "" && gpsPermission)
                locationCall()
            else
                updateWeather(currentCity)
        }
        if (currentCity == "" && gpsPermission)
            locationCall()
        else
            updateWeather(currentCity)
        binding.mainInfo.setOnClickListener {
            requestCity()
        }
    }

    companion object {
        const val TAG = "MainDataFragment"

        fun newInstance(): MainDataFragment {
            return MainDataFragment()
        }
    }
}