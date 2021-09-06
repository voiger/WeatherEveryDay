package com.example.weathereveryday.geolocation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.weathereveryday.Config;
import com.example.weathereveryday.api.ApiFactory;
import com.example.weathereveryday.api.ApiService;
import com.example.weathereveryday.pojo.PojoMainWeather;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Geolocation {

    private Context context;
    private String city;

    private MutableLiveData<PojoMainWeather> pojoMainWeatherLiveData;
    private MutableLiveData<Location> liveDataLoc;
    private CompositeDisposable compositeDisposable;

    public Geolocation(Context context, MutableLiveData<PojoMainWeather> pojoMainWeatherLiveData) {
        this.context = context;
        this.pojoMainWeatherLiveData = pojoMainWeatherLiveData;
        compositeDisposable = new CompositeDisposable();
        liveDataLoc = new MutableLiveData<Location>();

        update();

    }

    public void update(){
        getCurrentLocation();
        liveDataLoc.observeForever(location -> {
            city = convertFromLocationToCity(location);
            loadWeather(city);
        });
    }
    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {
        compositeDisposable = new CompositeDisposable();
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(context);
        Disposable disposable = locationProvider.getLastKnownLocation()
                .subscribe(new Consumer<Location>() {
                    @Override
                    public void accept(Location location) throws Exception {
                        liveDataLoc.setValue(location);

                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        try {
                            Thread.sleep(5000);
                            Toast.makeText(context,"Проверьте наличие интернета",Toast.LENGTH_SHORT).show();
                        } catch (InterruptedException e) {
                            e.printStackTrace();

                        }
                        getCurrentLocation();
                    }
                });
        compositeDisposable.add(disposable);

    }

    //получение из координат в город
    private String convertFromLocationToCity(Location location) {

        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addresses.get(0).getLocality();
    }

//загрузка данных с серваера
// todo нужно сделать нормальную обработку ошибок
    private void loadWeather(String city) {
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();

        apiService.getArrayListTasksServer(city, Config.appIdToken).enqueue(new Callback<PojoMainWeather>() {
            @Override
            public void onResponse(Call<PojoMainWeather> call, Response<PojoMainWeather> response) {
                if(response.code() == 200){
                    pojoMainWeatherLiveData.setValue(response.body());
                }else {
                    notificationNoInternetToast();
                    loadWeather(city);
                }

            }
            @Override
            public void onFailure(Call<PojoMainWeather> call, Throwable t) {
                t.printStackTrace();
                notificationNoInternetToast();
                loadWeather(city);//перезапукаем метод пока не появится инет
            }
        });


    }

    private void notificationNoInternetToast(){
        try {
            Thread.sleep(5000);
            Toast.makeText(context,"Проверьте наличие интернета",Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            e.printStackTrace();

        }

    }

    public void clearDisposable() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }
}
