package com.example.finalproject.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalproject.data.api.CityAPIService
import com.example.finalproject.model.datatype.city_api.CityInfo
import com.example.finalproject.model.datatype.city_api.CityList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CityViewModel: ViewModel() {
    private val service: CityAPIService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(CityAPIService::class.java)
    }

    private val cities: MutableLiveData<List<CityInfo>> by lazy {
        MutableLiveData<List<CityInfo>>().also {
            loadCities(it)
        }
    }

    fun getCities(): LiveData<List<CityInfo>> {
        return cities
    }

    private fun loadCities(liveData: MutableLiveData<List<CityInfo>>) {
        val apiInterface = service.getCapitals()
        apiInterface.enqueue(object: Callback<CityList> {
            override fun onResponse(call: Call<CityList>, response: Response<CityList>) {
                liveData.postValue(response.body()?.data)
            }
            override fun onFailure(call: Call<CityList>, t: Throwable) {
                liveData.postValue(null)
            }
        })
    }

    companion object {
        private const val BASE_URL = "https://countriesnow.space/"
    }
}