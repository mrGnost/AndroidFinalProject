package com.example.finalproject.presentation.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalproject.data.api.WeatherAPIService
import com.example.finalproject.model.datatype.weather_api.WeatherList
import com.example.finalproject.model.datatype.weather.CurrentWeather
import com.google.android.gms.location.FusedLocationProviderClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class MainViewModel: ViewModel() {
    private val service: WeatherAPIService
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(WeatherAPIService::class.java)
    }

    private val currentWeather: MutableLiveData<List<CurrentWeather>> by lazy {
        MutableLiveData<List<CurrentWeather>>()
    }

    private val location: MutableLiveData<Location> by lazy {
        MutableLiveData<Location>().also {
            loadLocation(fusedLocationClient, it)
        }
    }

    fun getCurrentWeatherByCity(city: String): LiveData<List<CurrentWeather>> {
        val apiInterface = service.getWeatherDataByCity(city, Locale.getDefault().language)
        loadCurrentWeather(apiInterface, currentWeather)
        return currentWeather
    }

    fun getCurrentWeatherByCoords(lat: Double, lon: Double): LiveData<List<CurrentWeather>> {
        val apiInterface = service.getWeatherDataByCoords(lat, lon, Locale.getDefault().language)
        loadCurrentWeather(apiInterface, currentWeather)
        return currentWeather
    }

    private fun loadCurrentWeather(apiInterface: Call<WeatherList>, liveData: MutableLiveData<List<CurrentWeather>>) {
        Log.d("apiCallByCity()", "Start enqueue")
        apiInterface.enqueue(object: Callback<WeatherList> {
            override fun onResponse(call: Call<WeatherList>, response: Response<WeatherList>) {
                Log.d("apiCallByCity()", "Start onResponse")
                val body = response.body()
                Log.d("apiCallByCity()", "$response ${response.body()}")
                if (body != null) {
                    Log.d("apiCallByCity()", "Start assigning values")
                    liveData.postValue(body.list.map { x -> CurrentWeather(
                        temperature = x.main.temp.roundToInt(),
                        weather = x.weather.first().description,
                        city = body.city.name,
                        country = body.city.country,
                        date = dateToPattern(x.dt_txt, body.city.timezone, "MMMM, dd"),
                        time = dateToPattern(x.dt_txt, body.city.timezone, "HH:mm"),
                        pressure = x.main.pressure,
                        humidity = x.main.humidity,
                        feelsLike = x.main.feels_like.roundToInt(),
                        wind = x.wind.speed,
                        image = "_${x.weather.first().icon}",
                        description = x.weather.first().description
                    ) })
                    Log.d("apiCallByCity()", "Finished assignment")
                }
            }
            override fun onFailure(call: Call<WeatherList>, t: Throwable) {
                liveData.postValue(null)
            }
        })
    }

    fun getLocation(client: FusedLocationProviderClient): LiveData<Location> {
        fusedLocationClient = client
        return location
    }

    private fun loadLocation(client: FusedLocationProviderClient, liveData: MutableLiveData<Location>) {
        Log.d("LOCATION", "Start loading")
        client.lastLocation
            .addOnSuccessListener { location : Location? ->
                Log.d("LOCATION", "Location found")
                liveData.postValue(location)
            }
    }

    private fun dateToPattern(date: String, shift: Int, pattern: String): String? {
        val dt = LocalDateTime.parse(date.replaceFirst(' ', 'T'))
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return formatter.format(dt.plusSeconds(Integer.toUnsignedLong(shift)))
    }

    companion object {
        private const val BASE_URL = "https://pro.openweathermap.org/"
    }
}