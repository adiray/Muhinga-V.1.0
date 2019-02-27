package com.example.dell.muhingalayoutprototypes;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FooterAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
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

public class Album extends AppCompatActivity {

    //miscellaneous objects
    Boolean onRefreshing = false, infiniteLoading = false; //shows weather the user is refreshing or loading more items respectively
    String selectedAlbumName, selectedArtistName, selectedAlbumNameQueryString, selectedSongUrl, selectedSongTitle, selectedSongCoverImage;


    //declare the view objects
    SwipeRefreshLayout songsSwipeRefreshLayout;
    TextView artistNameTv, albumNameTv;


    //Recycler View objects
    RecyclerView songsRecyclerView;
    ArrayList<SongResponse> songResponseArrayList = new ArrayList<>();
    ArrayList<SongsItem> singleSongsDataArrayList = new ArrayList<>();


    //declare the retrofit objects. All these are used with retrofit
    Retrofit.Builder builder;
    Retrofit myRetrofit;
    RetrofitClient myWebClient;
    retrofit2.Call<ArrayList<SongResponse>> allSongsCall;
    Map<String, String> songsFilterMap = new HashMap<String, String>();
    Integer tableOffset = 0;   //this increases the offset from the top of the table when items are being retrieved from backendless
    String tableOffsetString = tableOffset.toString();


    //Fast adapter objects
    // FastItemAdapter<SongResponse> songsFastAdapter = new FastItemAdapter<>();//create our FastAdapter which will manage everything
    FooterAdapter<ProgressItem> footerAdapter = new FooterAdapter<>();
    FastItemAdapter<SongsItem> songsItemFastItemAdapter = new FastItemAdapter<>(); //create the fast adapter that will handle the songs items
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

    //Intent objects
    public static final String EXTRA_PLAY_SONG_ARTIST_NAME = "com.example.muhinga.PlaySongsArtistName";
    public static final String EXTRA_PLAY_SONG_ALBUM_NAME = "com.example.muhinga.PlaySongsAlbumName";
    public static final String EXTRA_PLAY_SONG_FILE_REFERENCE = "com.example.muhinga.PlaySongsFileReference";
    public static final String EXTRA_PLAY_SONG_TITLE = "com.example.muhinga.PlaySongsTitle";
    public static final String EXTRA_PLAY_SONG_COVER_IMAGE = "com.example.muhinga.PlaySongsCoverImage";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        //get the intent that started this activity
        Intent intent = getIntent();
        selectedAlbumName = intent.getStringExtra(Artist.EXTRA_SELECTED_ALBUM_NAME);
        selectedArtistName = intent.getStringExtra(Artist.EXTRA_SELECTED_ARTIST_NAME);


        //initialize the views
        songsSwipeRefreshLayout = findViewById(R.id.album_view_songs_rec_view_swipe_refresh);
        albumNameTv = findViewById(R.id.album_view_album_name);
        artistNameTv = findViewById(R.id.album_view_artist_name);


        //set up the albums recycler view
        songsRecyclerView = findViewById(R.id.album_view_songs_recycler_view);
        songsRecyclerView.setHasFixedSize(true);
        songsRecyclerView.setLayoutManager(new GridLayoutManager(Album.this, 1, 1, false));


        //initialize our FastAdapter which will manage everything
        songsItemFastItemAdapter = new FastItemAdapter<>();


        //initialize the endless scroll listener
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(footerAdapter) {
            @Override
            public void onLoadMore(int currentPage) {


                footerAdapter.clear();
                footerAdapter.add(new ProgressItem().withEnabled(false));
                loadMoreSongs();

                //ToDo add a method  to LOAD MORE items


            }


        };


        //set the on refresh listener to the swipe to refresh view
        songsSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshSongs();
                //ToDo add a method  to REFRESH items


            }

        });


        //set the infinite/endless load on scroll listener to the recycler view
        songsRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);


        //fill the query map object for the retrofit query
        //create the where clause
        selectedAlbumNameQueryString = "name%3D%20" + "'" + selectedAlbumName + "'";
        songsFilterMap.put("where", selectedAlbumNameQueryString);
        songsFilterMap.put("pageSize", "4");
        songsFilterMap.put("offset", tableOffsetString);
        songsFilterMap.put("sortBy", "created%20desc");
        songsFilterMap.put("loadRelations", "songs");


//set the text to the textViews
        artistNameTv.setText(selectedArtistName);
        albumNameTv.setText(selectedAlbumName);


        buildRetrofitClient();  //build the retrofit client

        requestSongs(); //make the initial / first  songs request


        //add an onClickListener to the recycler view and its views
        songsItemFastItemAdapter.withSelectable(true);
        songsItemFastItemAdapter.withOnClickListener(new FastAdapter.OnClickListener<SongsItem>() {
            @Override
            public boolean onClick(View v, IAdapter<SongsItem> adapter, SongsItem item, int position) {

                selectedSongUrl = item.getFile();
                selectedSongTitle = item.getTitle();
                selectedSongCoverImage = item.getCoverImage();

                Intent intent = new Intent(Album.this, PlayMusic.class);
                intent.putExtra(EXTRA_PLAY_SONG_ALBUM_NAME, selectedAlbumName);
                intent.putExtra(EXTRA_PLAY_SONG_ARTIST_NAME, selectedArtistName);
                intent.putExtra(EXTRA_PLAY_SONG_FILE_REFERENCE, selectedSongUrl);
                intent.putExtra(EXTRA_PLAY_SONG_TITLE, selectedSongTitle);
                intent.putExtra(EXTRA_PLAY_SONG_COVER_IMAGE, selectedSongCoverImage);
                startActivity(intent);

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
        allSongsCall = myWebClient.getAlbumWithSongs(songsFilterMap);

    }

    /*************************************************************************************************************************************************/


    void requestSongs() {

        //make the call
        allSongsCall.clone().enqueue(new Callback<ArrayList<SongResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<SongResponse>> call, Response<ArrayList<SongResponse>> response) {


                if (!onRefreshing && !infiniteLoading) {

                    if (response.body() != null && response.body().size() > 0 && response.body().get(0).getSongs() != null) {
                        //perform the normal sequence of actions for a first time load
                        songResponseArrayList = response.body();
                        singleSongsDataArrayList = response.body().get(0).getSongs();
                        //songsFastAdapter.add(songResponseArrayList);
                        songsItemFastItemAdapter.add(singleSongsDataArrayList);
                        songsRecyclerView.setAdapter(footerAdapter.wrap(songsItemFastItemAdapter));
                    }


                    Log.d("myLogsRequestUrl", response.raw().request().url().toString() + songResponseArrayList.size());

                } else if (onRefreshing && !infiniteLoading) {


                    if (response.body() != null && response.body().size() > 0 && response.body().get(0).getSongs() != null) {
                        //perform the sequence of actions for a refreshed load
                        // songResponseArrayList.clear();
                        singleSongsDataArrayList.clear();
                        singleSongsDataArrayList = response.body().get(0).getSongs();
                        //songResponseArrayList = response.body();
                        //songsFastAdapter.clear();
                        songsItemFastItemAdapter.clear();
                        songsRecyclerView.clearOnScrollListeners();
                        songsRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
                        songsItemFastItemAdapter.add(response.body().get(0).getSongs());
                        //songsFastAdapter.add(response.body());
                        endlessRecyclerOnScrollListener.resetPageCount();

                    }

                    Log.d("myLogsRequestUrlOR", response.raw().request().url().toString());


                } else if (infiniteLoading && !onRefreshing) {

                    footerAdapter.clear();

                    if (response.body() != null && response.body().size() > 0 && response.body().get(0).getSongs() != null) {

                        //songResponseArrayList.addAll(response.body());
                        singleSongsDataArrayList.addAll(response.body().get(0).getSongs());

                        if (response.body().get(0).getSongs().size() > 0) {
                            //songsFastAdapter.add(response.body());
                            songsItemFastItemAdapter.add(response.body().get(0).getSongs());
                        } else {
                            Toast.makeText(Album.this, "No more items", Toast.LENGTH_LONG).show();
                        }

                    }
                }

                Log.d("myLogsRequestUrlIL", response.raw().request().url().toString() + " table offset = " + tableOffset);
                infiniteLoading = false;


                Log.d("myLogsOnSuccess", "onResponse: response successful");


            }

            @Override
            public void onFailure(Call<ArrayList<SongResponse>> call, Throwable t) {

                Log.d("myLogsOnFailure", "onResponse: response unsuccessful");

            }
        });

    }


    /*************************************************************************************************************************************************/


    void refreshSongs() {    //method called when user attempts to refresh the songs recycler view


        tableOffset = 0;
        tableOffsetString = tableOffset.toString();
        songsFilterMap.put("offset", tableOffsetString);  //update the value of the offset in the request url
        onRefreshing = true;
        infiniteLoading = false;
        requestSongs();

        //stop the refreshing animation
        songsSwipeRefreshLayout.setRefreshing(false);


        // housesMainRecView.addOnScrollListener(endlessRecyclerOnScrollListener);
        //onRefreshing = false;

    }


    /*************************************************************************************************************************************************/


    void loadMoreSongs() {

        //TODO The allHousesResponseArray exists just to give a count. Maybe the count could be more effectively stored in an integer value?
        tableOffset = singleSongsDataArrayList.size();
        tableOffsetString = tableOffset.toString();
        songsFilterMap.put("offset", tableOffsetString);    //update the value of the offset in the request url
        Log.d("myLogs", "loadMoreSongs: " + songsFilterMap.toString());
        infiniteLoading = true;
        onRefreshing = false;
        requestSongs();
    }


}
