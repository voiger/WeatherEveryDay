package com.example.weathereveryday.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import com.example.weathereveryday.R;
import com.example.weathereveryday.databinding.ActivityMainBinding;
import com.example.weathereveryday.pojo.PojoMainWeather;
import com.example.weathereveryday.utils.PermissionRequest;
import com.example.weathereveryday.viewModel.MainViewModel;
import com.google.gson.Gson;

import java.util.Objects;

//todo что нужно добавить проект
//1 splash screen с пред загрузкой данных
//2 добавить возможность смотреть на следющий день
//3 переписать архетектуру приложения, на нормальный mvvm патерн

public class MainActivity extends AppCompatActivity {
    private MainViewModel model;
    private ActivityMainBinding binding;

    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissionGeolocation();
        init();
        if (loadPojoMainWeather() != null){
            updateUI(loadPojoMainWeather());
        }

    }

    private void updateUI(PojoMainWeather pojoMainWeather) {
        StringBuilder icon = new StringBuilder(pojoMainWeather.getWeather().get(0).getIcon());
        switch (Integer.parseInt(icon.substring(0, 2))) {
            case 1:
                binding.imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_clear_day));
                break;
            case 2:
                binding.imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_few_clouds));
                break;
            case 3:
                binding.imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_cloudy_weather));
                break;
            case 4:
                binding.imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_broken_clouds));
                break;
            case 9:
                binding.imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_shower_rain));
                break;
            case 10:
                binding.imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_rainy_weather));
                break;
            case 11:
                binding.imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_storm_weather));
                break;
            case 13:
                binding.imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_snow_weather));
                break;
            case 50:
                binding.imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_broken_clouds));
                break;

        }
        binding.setDescription(pojoMainWeather.getWeather().get(0).getDescription());
        TextView textView = Objects.requireNonNull(getSupportActionBar()).getCustomView().findViewById(R.id.toolbarCityText);
        textView.setText(pojoMainWeather.getName());

        binding.setWeather(pojoMainWeather);
        binding.setSpeed(Math.round(pojoMainWeather.getWind().getSpeed()) + " м/с");
        binding.setPressure(String.valueOf(pojoMainWeather.getMain().getPressure()) + " мм \nрт. ст.");

        savePojoMainWeather(pojoMainWeather);

    }

    private void init() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.toolbar_weather);
        getSupportActionBar().setElevation(0);//убираем тень от toolbar
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        model = new ViewModelProvider(this).get(MainViewModel.class);
        model.getWeather().observe(this, this::updateUI);
        sPref = getPreferences(MODE_PRIVATE);
    }

    //проверка на раезрешение местоположения
    private void checkPermissionGeolocation() {
        PermissionRequest.from(this)//получение разрешение на местоположения
                .setPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .setInSettingsTabText("для работы приложение нужно разрешение на местоположение.")
                .setListener(isGranted -> {
                })
                .request();
    }
    private void savePojoMainWeather(PojoMainWeather pojoMainWeather){
        Gson gson = new Gson();
        String save = gson.toJson(pojoMainWeather);
        Editor ed = sPref.edit();
        ed.putString("saveWeather",save);
        ed.commit();
    }
    private PojoMainWeather loadPojoMainWeather(){
        Gson gson = new Gson();
        String load = sPref.getString("saveWeather","");
        return gson.fromJson(load,PojoMainWeather.class);
    }
}