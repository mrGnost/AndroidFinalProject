package com.example.finalproject.data.api

import com.example.finalproject.model.datatype.city_api.CityList
import retrofit2.Call
import retrofit2.http.GET

interface CityAPIService {
    @GET("/api/v0.1/countries/capital")
    fun getCapitals(): Call<CityList>
}