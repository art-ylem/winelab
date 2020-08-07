package com.example.winelab.network;



import com.example.winelab.model.Cat;

import java.util.ArrayList;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface Api {

    @GET("images/search")
    Observable<ArrayList<Cat>> getCats(@QueryMap Map<String, Integer> map, @Query("size") String size);
}
