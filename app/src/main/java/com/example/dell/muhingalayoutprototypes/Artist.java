package com.example.dell.muhingalayoutprototypes;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FooterAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Artist extends AppCompatActivity {


    //miscellaneous objects
    Boolean onRefreshing = false, infiniteLoading = false;
    String selectedArtistName, selectedArtistNameQueryString, selectedArtistCoverImageReference, selectedArtistProfileImageReference;
    public static final String EXTRA_SELECTED_ALBUM_NAME = "com.example.muhinga.selectedAlbumName";
    public static final String EXTRA_SELECTED_ARTIST_NAME = "com.example.muhinga.selectedArtistNameTest";


    //declare the view objects
    SwipeRefreshLayout artistViewAlbumRecViewSwipeRefresh;
    ImageView artistProfileImage, artistCoverImage;
    TextView selectedArtistNameTextView;


    //declare the recycler view objects
    RecyclerView artistViewAlbumRecView;
    // ArrayList<ArtistViewAlbumResponse> allAlbumResponseArray;
    ArrayList<ArtistViewAlbumResponse> allAlbumsResponseArray;

    //declare the retrofit objects. All these are used with retrofit
    Retrofit.Builder builder;
    Retrofit myRetrofit;
    RetrofitClient myWebClient;
    retrofit2.Call<ArrayList<ArtistAlbumRelationsResponse>> artistViewAllAlbumsCall;
    Map<String, String> albumsFilterMap = new HashMap<String, String>();
    Integer tableOffset = 0;   //this increases the offset from the top of the table when items are being retrieved from backendless
    String tableOffsetString = tableOffset.toString();


    //Fast adapter objects
    //FastItemAdapter<ArtistViewAlbumResponse> artistViewAlbumFastAdapter = new FastItemAdapter<>(); //create our FastAdapter which will manage everything
    FastItemAdapter<ArtistViewAlbumResponse> albumFastItemAdapter = new FastItemAdapter<>();
    FooterAdapter<ProgressItem> footerAdapter = new FooterAdapter<>();
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);


        //get the intent that started this activity
        Intent intent = getIntent();
        selectedArtistName = intent.getStringExtra(Music.EXTRA_ARTIST_NAME);
        selectedArtistNameQueryString = "name%3D%20" + "'" + selectedArtistName + "'";
        selectedArtistCoverImageReference = intent.getStringExtra(Music.EXTRA_ARTIST_COVER_IMAGE_REFERENCE);
        selectedArtistProfileImageReference = intent.getStringExtra(Music.EXTRA_ARTIST_PROFILE_IMAGE_REFERENCE);
        //name%3D%20'Kygo'


        //Initialize the views
        artistViewAlbumRecViewSwipeRefresh = findViewById(R.id.artist_view_album_swipe_refresh);
        artistCoverImage = findViewById(R.id.artist_view_artist_cover_image);
        artistProfileImage = findViewById(R.id.artist_view_artist_profile_image);
        selectedArtistNameTextView = findViewById(R.id.artist_view_name);

        //load the profile and cover images into the respective views
        Glide.with(this).load(selectedArtistCoverImageReference).into(artistCoverImage);
        Glide.with(this).load(selectedArtistProfileImageReference).into(artistProfileImage);
        selectedArtistNameTextView.setText(selectedArtistName);


        //build out the main recycler view
        artistViewAlbumRecView = findViewById(R.id.artist_view_album_rec_view);
        artistViewAlbumRecView.setHasFixedSize(true);
        artistViewAlbumRecView.setLayoutManager(new GridLayoutManager(Artist.this, 2, 1, false));


        //initialize our FastAdapter which will manage everything
        albumFastItemAdapter = new FastItemAdapter<>();


        //initialize the endless scroll listener
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(footerAdapter) {
            @Override
            public void onLoadMore(int currentPage) {


                footerAdapter.clear();
                footerAdapter.add(new ProgressItem().withEnabled(false));

                //Todo method to load more albums
                loadMoreAlbums();

            }

        };


        //set the on refresh listener to the swipe to refresh view
        artistViewAlbumRecViewSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                //Todo method to refresh the albums
                refreshAlbums();


            }


        });


        //set the infinite/endless load on scroll listener to the recycler view
        artistViewAlbumRecView.addOnScrollListener(endlessRecyclerOnScrollListener);


        //fill the query map object for the retrofit query
        albumsFilterMap.put("where", selectedArtistNameQueryString);
        albumsFilterMap.put("pageSize", "4");
        albumsFilterMap.put("offset", tableOffsetString);
        albumsFilterMap.put("sortBy", "created%20desc");
        albumsFilterMap.put("loadRelations", "albums");


        buildRetrofitClient();  //build the retrofit client

        requestAlbums(); //make the initial / first  houses request


        //add an on click to the fast adapter
        albumFastItemAdapter.withSelectable(true);
        albumFastItemAdapter.withOnClickListener(new FastAdapter.OnClickListener<ArtistViewAlbumResponse>() {
            @Override
            public boolean onClick(View v, IAdapter<ArtistViewAlbumResponse> adapter, ArtistViewAlbumResponse item, int position) {

                //handle click here
                String selectedAlbumName = item.getName();

                Intent mintent = new Intent(Artist.this, Album.class);
                mintent.putExtra(EXTRA_SELECTED_ALBUM_NAME, selectedAlbumName);
                mintent.putExtra(EXTRA_SELECTED_ARTIST_NAME, selectedArtistName);
                startActivity(mintent);

                return true;
            }
        });


    }

    /*************************************************************************************************************************************************/


    void buildRetrofitClient() {

        //initialize the retrofit client builder using the backendless.com api
        builder = new Retrofit.Builder();
        builder.baseUrl("http://api.backendless.com/125AF8BD-1879-764A-FF22-13FB1C162400/6F40C4D4-6CFB-E66A-FFC7-D71E4A8BF100/data/")
                .addConverterFactory(GsonConverterFactory.create());

        //use your builder to build a retrofit object
        myRetrofit = builder.build();

        //create a retrofit client using the retrofit object
        myWebClient = myRetrofit.create(RetrofitClient.class);

        //create your call using the retrofit client
        artistViewAllAlbumsCall = myWebClient.getFilteredArtistWithAlbum(albumsFilterMap);

    }


    /*************************************************************************************************************************************************/


    void requestAlbums() {

        //make the call
        artistViewAllAlbumsCall.clone().enqueue(new Callback<ArrayList<ArtistAlbumRelationsResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<ArtistAlbumRelationsResponse>> call, Response<ArrayList<ArtistAlbumRelationsResponse>> response) {

                if (!onRefreshing && !infiniteLoading) {

                    if (response.body() != null && response.body().size() > 0 && response.body().get(0).getAlbum() != null) {

                        //perform the normal sequence of actions for a first time load
                        allAlbumsResponseArray = response.body().get(0).getAlbum();
                        //   artistViewAlbumFastAdapter.add(allAlbumResponseArray);
                        albumFastItemAdapter.add(allAlbumsResponseArray);
                        artistViewAlbumRecView.setAdapter(footerAdapter.wrap(albumFastItemAdapter));

                    }
                    Log.d("myLogsRequestUrl", response.raw().request().url().toString());

                } else if (onRefreshing && !infiniteLoading) {

                    if (response.body() != null && response.body().size() > 0 && response.body().get(0).getAlbum() != null) {

                        //perform the sequence of actions for a refreshed load
                        allAlbumsResponseArray.clear();
                        allAlbumsResponseArray = response.body().get(0).getAlbum();
                        albumFastItemAdapter.clear();
                        artistViewAlbumRecView.clearOnScrollListeners();
                        artistViewAlbumRecView.addOnScrollListener(endlessRecyclerOnScrollListener);
                        albumFastItemAdapter.add(response.body().get(0).getAlbum());
                        endlessRecyclerOnScrollListener.resetPageCount();

                    }
                    Log.d("myLogsRequestUrlOR", response.raw().request().url().toString());


                } else if (infiniteLoading && !onRefreshing) {

                    footerAdapter.clear();


                    if (response.body() != null && response.body().size() > 0 && response.body().get(0).getAlbum() != null) {

                        allAlbumsResponseArray.addAll(response.body().get(0).getAlbum());
                        if (response.body().size() > 0) {
                            albumFastItemAdapter.add(response.body().get(0).getAlbum());
                        } else {
                            Toast.makeText(Artist.this, "No more items", Toast.LENGTH_LONG).show();
                        }

                    }

                    Log.d("myLogsRequestUrlIL", response.raw().request().url().toString() + " table offset = " + tableOffset);
                    infiniteLoading = false;


                }

                Log.d("myLogsOnSuccess", "onResponse: response successful");


            }

            @Override
            public void onFailure(Call<ArrayList<ArtistAlbumRelationsResponse>> call, Throwable t) {

                Log.d("myLogsOnFailure", "onResponse: response unsuccessful");

            }
        });

    }


    /*************************************************************************************************************************************************/


    void refreshAlbums() {    //method called when user attempts to refresh the albums recycler view


        tableOffset = 0;
        tableOffsetString = tableOffset.toString();
        albumsFilterMap.put("offset", tableOffsetString);  //update the value of the offset in the request url
        onRefreshing = true;
        infiniteLoading = false;
        requestAlbums();

        //stop the refreshing animation
        artistViewAlbumRecViewSwipeRefresh.setRefreshing(false);


    }


    /*************************************************************************************************************************************************/


    void loadMoreAlbums() {

        //TODO The allHousesResponseArray exists just to give a count. Maybe the count could be more effectively stored in an integer value?
        tableOffset = allAlbumsResponseArray.size();
        tableOffsetString = tableOffset.toString();
        albumsFilterMap.put("offset", tableOffsetString);    //update the value of the offset in the request url
        Log.d("myLogs", "loadMoreAlbums: " + albumsFilterMap.toString());
        infiniteLoading = true;
        onRefreshing = false;
        requestAlbums();
    }


}
