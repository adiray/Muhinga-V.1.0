package com.example.dell.muhingalayoutprototypes;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface RetrofitClient {

    @GET("land")
    Call<ArrayList<LandResponse>> getFilteredLand(@QueryMap(encoded = true) Map<String, String> userFilters_Land);

    @GET("venues")
    Call<ArrayList<VenuesResponse>> getFilteredVenues(@QueryMap(encoded = true) Map<String, String> userFilters_Venues);


    @GET("real_estate")
    Call<ArrayList<HousesResponse>> getFilteredHouses(@QueryMap (encoded = true) Map<String, String> userFilters);

    @GET("artist")
    Call<ArrayList<ArtistResponse>> getFilteredArtist(@QueryMap(encoded = true) Map<String, String> userFilters_Artist);

    @GET("artist")
    Call<ArrayList<ArtistAlbumRelationsResponse>> getFilteredArtistWithAlbum(@QueryMap(encoded = true) Map<String, String> userFilters_Artist_withAlbum);

    @GET("album")
    Call<ArrayList<SongResponse>> getAlbumWithSongs(@QueryMap(encoded = true) Map<String, String> userFilters_Album_with_Songs);

    @Headers("Content-Type:application/json")
    @POST("Users")
    Call<User> createUser(@Body User newUser);

    @GET("Users")
    Call<ArrayList<UserResponse>> verifyRegistrationSuccess(@QueryMap(encoded = true) Map<String, String> newUser);

   /* @Headers("Content-Type:application/json")
    @POST("Users/login")
    Call<JsonElement> loginUser (@Body UserLogin newUserLogin);

*/

    @Headers({
            "Accept:application/json",
            "Content-Type:application/json"
    })
    @POST("users/login")
    Call<UserLoginResponse> loginUser(@Body UserLogin newUserLogin);







}
