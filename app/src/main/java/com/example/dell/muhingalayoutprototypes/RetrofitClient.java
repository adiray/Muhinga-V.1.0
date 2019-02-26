package com.example.dell.muhingalayoutprototypes;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface RetrofitClient {

    @GET("land")
    Call<ArrayList<LandResponse>> getFilteredLand(@QueryMap(encoded = true) Map<String, String> userFilters_Land);


    @GET("real_estate")
    Call<ArrayList<HousesResponse>> getFilteredHouses(@QueryMap (encoded = true) Map<String, String> userFilters);
}
