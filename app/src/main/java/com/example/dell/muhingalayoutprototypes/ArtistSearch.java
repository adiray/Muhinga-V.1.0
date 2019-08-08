package com.example.dell.muhingalayoutprototypes;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FooterAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArtistSearch extends AppCompatActivity {



    //miscellaneous objects
    Boolean onRefreshing = false, infiniteLoading = false, isAfterFirstSearch = false;
    ArrayList<ArtistResponse> allArtistResponseArray = new ArrayList<>();

    //declare the view objects
    SwipeRefreshLayout artistSwipeRefresh; //swipe to refresh view for the land recycler view

    //recycler view objects
    RecyclerView artistRecyclerView;


    //fast adapter objects
    FastItemAdapter<ArtistResponse> artistFastAdapter = new FastItemAdapter<>();    //create our FastAdapter which will manage everything
    FooterAdapter<ProgressItem> footerAdapter = new FooterAdapter<>();
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;


    //Intent objects
    public static final String EXTRA_SEARCH_ARTIST_NAME = "com.example.muhinga.artistName";
    public static final String EXTRA_SEARCH_ARTIST_PROFILE_IMAGE_REFERENCE = "com.example.muhinga.artistProfileImageReference";
    public static final String EXTRA_SEARCH_ARTIST_COVER_IMAGE_REFERENCE = "com.example.muhinga.artistCoverImageReference";
    public static final String EXTRA_SEARCH_ARTIST_REFERRER_ACTIVITY = "com.example.muhinga.artistSearchUniqueReferringId";



    //Floating Search bar objects
    MaterialSearchBar artistSearchBar;
    List artistSearchSuggestions;
    String searchText , searchTextQuery;




    //declare the retrofit objects. All these are used with retrofit
    Retrofit.Builder builder;
    Retrofit myRetrofit;
    RetrofitClient myWebClient;
    retrofit2.Call<ArrayList<ArtistResponse>> allArtistCall;
    Map<String, String> artistFilterMap = new HashMap<String, String>();
    Integer tableOffset = 0;  //this increases the offset from the top of the table when items are being retrieved from backendless
    String tableOffsetString = tableOffset.toString();







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_search);


        //initialize the views
        artistSwipeRefresh = findViewById(R.id.artist_search_view_swipe_refresh);




        //initialize the search bar
        //Floating Search Bar
        artistSearchBar = findViewById(R.id.music_search_view_search_bar);
        artistSearchSuggestions =  artistSearchBar.getLastSuggestions();
        artistSearchBar.setLastSuggestions(artistSearchSuggestions);

        artistSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                searchText = text.toString();
                searchTextQuery = "name%20LIKE%20'%25"+searchText.trim()+"%25'";
                artistFilterMap.put("where", searchTextQuery);
                requestArtists(); //make the initial / first  artist request

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });



        //Build out the recycler view
        artistRecyclerView = findViewById(R.id.artist_search_view_main_recycler_view);
        artistRecyclerView.setHasFixedSize(true);
        artistRecyclerView.setLayoutManager(new GridLayoutManager(ArtistSearch.this, 2, 1, false));


        //initialize our FastAdapter which will manage everything
        artistFastAdapter = new FastItemAdapter<>();



        //initialize the endless scroll listener
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(footerAdapter) {
            @Override
            public void onLoadMore(int currentPage) {


                footerAdapter.clear();
                footerAdapter.add(new ProgressItem().withEnabled(false));

                loadMoreArtists();

                /*a statement to check if the user is loading more items that have been filtered or just loading more of all items unfiltered
                if (filteredState) {
                    loadMoreFilteredLand();
                } else {
                    loadMoreLand();
                }*/

            }

        };




        //set the on refresh listener to the swipe to refresh view
        artistSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshArtists();

            }


        });



        //set the infinite/endless load on scroll listener to the recycler view
        artistRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);


        //fill the query map object for the retrofit query
        artistFilterMap.put("pageSize", "4");
        artistFilterMap.put("offset", tableOffsetString);
        artistFilterMap.put("where", searchTextQuery);
        artistFilterMap.put("sortBy", "created%20desc");




        artistFastAdapter.withSelectable(true);
        artistFastAdapter.withOnClickListener(new FastAdapter.OnClickListener<ArtistResponse>() {
            @Override
            public boolean onClick(View v, IAdapter<ArtistResponse> adapter, ArtistResponse item, int position) {

                Intent intent = new Intent(ArtistSearch.this, Artist.class);

                //Todo this is where you stopped. add the extras for the intents
                intent.putExtra(EXTRA_SEARCH_ARTIST_NAME, item.getName());
                intent.putExtra(EXTRA_SEARCH_ARTIST_COVER_IMAGE_REFERENCE, item.getCoverImage());
                intent.putExtra(EXTRA_SEARCH_ARTIST_PROFILE_IMAGE_REFERENCE, item.getProfilePicture());
                intent.putExtra("uniqueId","ArtistSearch");
                startActivity(intent);


                return true;
            }
        });








        buildRetrofitClient();  //build the retrofit client






    }




    /*************************************************************************************************************************************************/


    void buildRetrofitClient() {

        //initialize the retrofit client builder using the backendless.com api
        builder = new Retrofit.Builder();
        builder.baseUrl("https://api.backendless.com/1C02E90A-53CA-7B8A-FFC0-214A81B0B500/9BC2AFA1-C83A-E705-FF86-5AEE68432A00/data/")
                .addConverterFactory(GsonConverterFactory.create());

        //use your builder to build a retrofit object
        myRetrofit = builder.build();

        //create a retrofit client using the retrofit object
        myWebClient = myRetrofit.create(RetrofitClient.class);

        //create your call using the retrofit client
        allArtistCall = myWebClient.getFilteredArtist(artistFilterMap);
    }


    /*************************************************************************************************************************************************/



    void requestArtists() {

        //make the call
        allArtistCall.clone().enqueue(new Callback<ArrayList<ArtistResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<ArtistResponse>> call, Response<ArrayList<ArtistResponse>> response) {



                if (!onRefreshing && !infiniteLoading && !isAfterFirstSearch) {

                    //perform the normal sequence of actions for a first time load
                    allArtistResponseArray = response.body();
                    artistFastAdapter.add(allArtistResponseArray);
                    artistRecyclerView.setAdapter(footerAdapter.wrap(artistFastAdapter));
                    isAfterFirstSearch = true;


                    Log.d("myLogsRequestUrlForSrch", response.raw().request().url().toString());

                }  else if(!onRefreshing && !infiniteLoading && isAfterFirstSearch){

                    //perform the sequence of actions for another search
                    allArtistResponseArray.clear();
                    allArtistResponseArray = response.body();
                    artistFastAdapter.clear();
                    artistRecyclerView.clearOnScrollListeners();
                    artistRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
                    artistFastAdapter.add(response.body());
                    endlessRecyclerOnScrollListener.resetPageCount();


                    Log.d("myLogsRequestUrl2ndsrch", response.raw().request().url().toString());

                } else if (onRefreshing && !infiniteLoading) {

                    //perform the sequence of actions for a refreshed load
                    allArtistResponseArray.clear();
                    allArtistResponseArray = response.body();
                    artistFastAdapter.clear();
                    artistRecyclerView.clearOnScrollListeners();
                    artistRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
                    artistFastAdapter.add(response.body());
                    endlessRecyclerOnScrollListener.resetPageCount();


                    Log.d("myLogsRequestUrlORsrch", response.raw().request().url().toString());


                } else if (infiniteLoading && !onRefreshing) {

                    allArtistResponseArray.addAll(response.body());
                    footerAdapter.clear();
                    if (response.body().size() > 0) {
                        artistFastAdapter.add(response.body());
                    } else {
                        Toast.makeText(ArtistSearch.this, "No more items", Toast.LENGTH_LONG).show();
                    }


                    Log.d("myLogsRequestUrlIL", response.raw().request().url().toString() + " table offset = " + tableOffset);
                    infiniteLoading = false;


                }

                Log.d("myLogsOnSuccess", "onResponse: response successful");


            }

            @Override
            public void onFailure(Call<ArrayList<ArtistResponse>> call, Throwable t) {

                Log.d("myLogsOnFailure", "onResponse: response unsuccessful");

            }
        });

    }

    /*************************************************************************************************************************************************/


    void refreshArtists() {    //method called when user attempts to refresh the artists' recycler view


        tableOffset = 0;
        tableOffsetString = tableOffset.toString();
        artistFilterMap.put("offset", tableOffsetString);  //update the value of the offset in the request url
        onRefreshing = true;
        infiniteLoading = false;
        requestArtists();

        //stop the refreshing animation
        artistSwipeRefresh.setRefreshing(false);


    }


    /*************************************************************************************************************************************************/


    void loadMoreArtists() {

        //TODO The allArtistResponseArray exists just to give a count. Maybe the count could be more effectively stored in an integer value?
        tableOffset = allArtistResponseArray.size();
        tableOffsetString = tableOffset.toString();
        artistFilterMap.put("offset", tableOffsetString);    //update the value of the offset in the request url
        Log.d("myLogs", "loadMoreArtists: " + artistFilterMap.toString());
        infiniteLoading = true;
        onRefreshing = false;
        requestArtists();
    }


    /*************************************************************************************************************************************************/









}
