package com.example.finalproject.model.datatype.weather

data class CurrentWeather(
    val temperature: Int,
    val weather: String,
    val city: String,
    val country: String,
    val date: String?,
    val time: String?,
    val humidity: Int,
    val wind: Double,
    val pressure: Int,
    val feelsLike: Int,
    val image: String,
    val description: String,
    )
