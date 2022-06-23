package com.example.finalproject.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.WeatherItemBinding
import com.example.finalproject.model.datatype.weather.CurrentWeather

class WeatherViewHolder(private val binding: WeatherItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CurrentWeather) {
        binding.weather = item
        binding.weatherItemImage.setImageResource(binding.root.resources.getIdentifier(
            item.image, "drawable", binding.root.context.packageName
        ))
    }
}

class WeatherAdapter(var data: List<CurrentWeather>) : RecyclerView.Adapter<WeatherViewHolder>() {
    private lateinit var binding: WeatherItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        binding = WeatherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weather = data[position]
        holder.bind(weather)
    }

    override fun getItemCount(): Int = data.size
}