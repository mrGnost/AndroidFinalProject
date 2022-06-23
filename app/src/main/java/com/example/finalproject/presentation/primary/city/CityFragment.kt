package com.example.finalproject.presentation.primary.city

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.databinding.PickCityFragmentBinding
import com.example.finalproject.model.datatype.city_api.CityInfo
import com.example.finalproject.presentation.adapter.CityAdapter
import com.example.finalproject.presentation.viewmodel.CityViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class CityFragment: Fragment() {
    private lateinit var binding: PickCityFragmentBinding
    private val viewModel: CityViewModel by viewModels()
    private lateinit var gson: Gson
    private lateinit var cities: List<CityInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CITY_ACTIVITY", "fragment created")
        gson = GsonBuilder().create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("CITY_ACTIVITY", "fragment view created")
        binding = PickCityFragmentBinding.inflate(inflater, container, false)
        Log.d("CITY_ACTIVITY", "inflated")
        return binding.root
    }

    override fun onStart() {
        Log.d("CITY_ACTIVITY", "fragment started")
        binding.citySearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val data = Intent()
                data.data = Uri.parse(p0)
                activity!!.setResult(RESULT_OK, data)
                activity!!.finish()
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
        setupRecyclerView()
        super.onStart()
    }

    private fun setupRecyclerView() {
        viewModel.getCities().observe(this) {
            val recyclerView = binding.cityRecyclerView
            cities = it
            recyclerView.adapter = CityAdapter(cities) { position -> onItemClick(position) }
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.setHasFixedSize(true)
        }
    }

    private fun onItemClick(position: Int) {
        Log.d("RETURN_DATA", "start")
        val data = Intent()
        data.data = Uri.parse(cities[position].capital)
        activity!!.setResult(RESULT_OK, data)
        activity!!.finish()
    }

    companion object {
        const val TAG = "CityFragment"

        fun newInstance(): CityFragment {
            return CityFragment()
        }
    }
}