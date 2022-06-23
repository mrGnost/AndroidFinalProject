package com.example.finalproject.model.datatype.weather_api

data class Weather(var dt_txt: String, var main: WeatherStats, var weather: List<WeatherDescription>, var wind: Wind)
