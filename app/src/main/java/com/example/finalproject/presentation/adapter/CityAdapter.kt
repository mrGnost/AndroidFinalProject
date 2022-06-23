package com.example.finalproject.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.CityItemBinding
import com.example.finalproject.model.datatype.city_api.CityInfo

class CityViewHolder(private val binding: CityItemBinding, val onItemClick: (position: Int) -> Unit)
    : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CityInfo) {
        binding.capital = item
        itemView.setOnClickListener {
            onItemClick(bindingAdapterPosition)
        }
    }
}

class CityAdapter(var data: List<CityInfo>, val onItemClick: (position: Int) -> Unit)
    : RecyclerView.Adapter<CityViewHolder>() {
    private lateinit var binding: CityItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        binding = CityItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = data[position]
        return holder.bind(city)
    }

    override fun getItemCount(): Int = data.size
}