package com.example.weathereveryday.viewModel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weathereveryday.api.ApiFactory;
import com.example.weathereveryday.api.ApiService;
import com.example.weathereveryday.geolocation.Geolocation;
import com.example.weathereveryday.pojo.PojoMainWeather;
import com.example.weathereveryday.pojo.Weather;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends AndroidViewModel {

    private Geolocation geolocation;

    private MutableLiveData<PojoMainWeather> weatherLiveData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        weatherLiveData = new MutableLiveData<PojoMainWeather>();

        geolocation = new Geolocation(application, weatherLiveData);
    }

    public MutableLiveData<PojoMainWeather> getWeather() {
        return weatherLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        geolocation.clearDisposable();
    }

    public void update() {
        geolocation.update();
    }


}
