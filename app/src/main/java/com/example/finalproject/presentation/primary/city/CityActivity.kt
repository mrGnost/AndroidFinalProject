package com.example.finalproject.presentation.primary.city

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.R

class CityActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CITY_ACTIVITY", "activity created")
        setContentView(R.layout.pick_city_screen)
        setSupportActionBar(findViewById(R.id.toolbar))
        title = "Weather"
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, CityFragment.newInstance(), CityFragment.TAG)
                .commit()
        }
    }
}