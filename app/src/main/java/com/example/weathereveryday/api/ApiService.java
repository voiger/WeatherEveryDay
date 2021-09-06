package com.example.weathereveryday.api;

import com.example.weathereveryday.pojo.PojoMainWeather;

import java.util.ArrayList;
import java.util.Observable;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @GET("weather?lang=ru&units=metric")
    Call<PojoMainWeather> getArrayListTasksServer(@Query("q") String City, @Query("appid") String appid);

}
