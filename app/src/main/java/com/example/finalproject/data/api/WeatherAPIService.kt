package com.example.finalproject.data.api

import com.example.finalproject.model.datatype.weather_api.WeatherList
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Query

interface WeatherAPIService {
    companion object {
        const val APP_ID = "ff890e592efdae3c16606b8f99573842"
    }

    @GET("/data/2.5/forecast/hourly?units=metric&appid=$APP_ID")
    fun getWeatherDataByCity(@Query("q") city: String, @Query("lang") language: String): Call<WeatherList>

    @GET("/data/2.5/forecast/hourly?units=metric&appid=$APP_ID")
    fun getWeatherDataByCoords(@Query("lat") latitude: Double, @Query("lon") longitude: Double, @Query("lang") language: String): Call<WeatherList>
}