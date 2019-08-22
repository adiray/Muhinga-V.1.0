package com.example.dell.muhingalayoutprototypes;

import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FooterAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;
import com.yalantis.jellytoolbar.listener.JellyListener;
import com.yalantis.jellytoolbar.widget.JellyToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class Music extends AppCompatActivity {


    //miscellaneous objects
    Boolean onRefreshing = false, infiniteLoading = false;
    ArrayList<ArtistResponse> allArtistResponseArray = new ArrayList<>();


    //declare the view objects
    SwipeRefreshLayout artistSwipeRefresh; //swipe to refresh view for the land recycler view
    FloatingTextButton loadMoreButton;


    //TOOLBAR VIEWS
    Toolbar musicMainToolBar;


    //recycler view objects
    RecyclerView artistRecyclerView;

    //declare the retrofit objects. All these are used with retrofit
    Retrofit.Builder builder;
    Retrofit myRetrofit;
    RetrofitClient myWebClient;
    retrofit2.Call<ArrayList<ArtistResponse>> allArtistCall;
    Map<String, String> artistFilterMap = new HashMap<String, String>();
    Integer tableOffset = 0;  //this increases the offset from the top of the table when items are being retrieved from backendless
    String tableOffsetString = tableOffset.toString();


    //fast adapter objects
    FastItemAdapter<ArtistResponse> artistFastAdapter = new FastItemAdapter<>();    //create our FastAdapter which will manage everything
    FooterAdapter<ProgressItem> footerAdapter = new FooterAdapter<>();
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

    //Intent objects
    public static final String EXTRA_ARTIST_NAME = "com.example.muhinga.artistName";
    public static final String EXTRA_ARTIST_PROFILE_IMAGE_REFERENCE = "com.example.muhinga.artistProfileImageReference";
    public static final String EXTRA_ARTIST_COVER_IMAGE_REFERENCE = "com.example.muhinga.artistCoverImageReference";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);


        //initialize the views
        artistSwipeRefresh = findViewById(R.id.activity_music_home_artist_swipe_refresh);
        loadMoreButton = findViewById(R.id.music_activity_load_more_button);
        loadMoreButton.setVisibility(View.INVISIBLE);



        //toolBar
        musicMainToolBar = findViewById(R.id.music_action_bar);
        musicMainToolBar.setTitle("Artists");
        Objects.requireNonNull(musicMainToolBar.getOverflowIcon()).setColorFilter(getResources().getColor(R.color.my_color_white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(musicMainToolBar);







     /*   //toolbar
        musicMainToolbar = findViewById(R.id.music_main_toolbar);

        searchArtistEditText = (EditText) LayoutInflater.from(this).inflate(R.layout.music_activity_search_view, null);
        searchArtistEditText.setBackgroundResource(R.color.colorTransparent);

        musicMainToolbar.setContentView(searchArtistEditText);

        musicMainToolbar.setJellyListener(new JellyListener() {
            @Override
            public void onCancelIconClicked() {

                if (TextUtils.isEmpty(searchArtistEditText.getText())) {
                    musicMainToolbar.collapse();
                } else {

                    searchArtistEditText.getText().clear();
                }
            }
        });*/

        /*
        toolbar.getToolbar().setNavigationIcon(R.drawable.ic_menu);
        toolbar.setJellyListener(jellyListener);

        editText = (AppCompatEditText) LayoutInflater.from(this).inflate(R.layout.edit_text, null);
        editText.setBackgroundResource(R.color.colorTransparent);
        toolbar.setContentView(editText);
*/

        //Build out the recycler view
        artistRecyclerView = findViewById(R.id.music_main_recycler_view);
        artistRecyclerView.setHasFixedSize(true);
        artistRecyclerView.setLayoutManager(new GridLayoutManager(Music.this, 2, 1, false));


        //initialize our FastAdapter which will manage everything
        artistFastAdapter = new FastItemAdapter<>();


        //initialize the endless scroll listener
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(footerAdapter) {
            @Override
            public void onLoadMore(int currentPage) {

                loadMoreButton.setVisibility(View.VISIBLE);





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
        artistFilterMap.put("pageSize", "10");
        artistFilterMap.put("offset", tableOffsetString);
        artistFilterMap.put("sortBy", "created%20desc");


        buildRetrofitClient();  //build the retrofit client

        requestArtists(); //make the initial / first  artist request


        artistFastAdapter.withSelectable(true);
        artistFastAdapter.withOnClickListener(new FastAdapter.OnClickListener<ArtistResponse>() {
            @Override
            public boolean onClick(View v, IAdapter<ArtistResponse> adapter, ArtistResponse item, int position) {

                Intent intent = new Intent(Music.this, Artist.class);

                //Todo this is where you stopped. add the extras for the intents
                intent.putExtra(EXTRA_ARTIST_NAME, item.getName());
                intent.putExtra(EXTRA_ARTIST_COVER_IMAGE_REFERENCE, item.getCoverImage());
                intent.putExtra(EXTRA_ARTIST_PROFILE_IMAGE_REFERENCE, item.getProfilePicture());
                intent.putExtra("uniqueId", "musicHomeActivity");
                startActivity(intent);


                return true;
            }
        });





        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadMoreButton.setEnabled(false);
                footerAdapter.clear();
                footerAdapter.add(new ProgressItem().withEnabled(false));
                loadMoreButton.setBackgroundColor(getResources().getColor(R.color.md_blue_grey_500));

                loadMoreArtists();

            }
        });



    }


    /*************************************************************************************************************************************************/


    //inflate the menu layout file for the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.music_home_app_bar_menu, menu);
        return true;
    }


    //specify the actions that happen when each menu item is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.search_artists_button):
                Intent intent = new Intent(Music.this, ArtistSearch.class);
                startActivity(intent);

                break;
            default:
                break;
        }
        return true;
    }


    /*************************************************************************************************************************************************/


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


                if (!onRefreshing && !infiniteLoading) {

                    //perform the normal sequence of actions for a first time load
                    allArtistResponseArray = response.body();
                    artistFastAdapter.add(allArtistResponseArray);
                    artistRecyclerView.setAdapter(footerAdapter.wrap(artistFastAdapter));


                    Log.d("myLogsRequestUrl", response.raw().request().url().toString());

                } else if (onRefreshing && !infiniteLoading) {

                    //perform the sequence of actions for a refreshed load
                    allArtistResponseArray.clear();
                    allArtistResponseArray = response.body();
                    artistFastAdapter.clear();
                    artistRecyclerView.clearOnScrollListeners();
                    artistRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
                    artistFastAdapter.add(response.body());
                    endlessRecyclerOnScrollListener.resetPageCount();


                    Log.d("myLogsRequestUrlOR", response.raw().request().url().toString());


                } else if (infiniteLoading && !onRefreshing) {

                    allArtistResponseArray.addAll(response.body());
                    footerAdapter.clear();
                    loadMoreButton.setEnabled(true);
                    loadMoreButton.setBackgroundColor(getResources().getColor(R.color.md_blue_grey_900));
                    loadMoreButton.setVisibility(View.INVISIBLE);
                    if (response.body().size() > 0) {
                        artistFastAdapter.add(response.body());
                    } else {
                        Toast.makeText(Music.this, "No more items", Toast.LENGTH_LONG).show();
                    }


                    Log.d("myLogsRequestUrlIL", response.raw().request().url().toString() + " table offset = " + tableOffset);
                    infiniteLoading = false;


                }

                Log.d("myLogsOnSuccess", "onResponse: response successful");


            }

            @Override
            public void onFailure(Call<ArrayList<ArtistResponse>> call, Throwable t) {



                if(infiniteLoading && !onRefreshing){

                    footerAdapter.clear();
                    loadMoreButton.setEnabled(true);
                    loadMoreButton.setBackgroundColor(getResources().getColor(R.color.md_blue_grey_900));
                    loadMoreButton.setVisibility(View.INVISIBLE);
                    createBottomDialog(3);

                }else if(!infiniteLoading && !onRefreshing){

                    createBottomDialog(1);


                }else if(onRefreshing && !infiniteLoading){


                    createBottomDialog(2);



                }



                Log.d("myLogsOnFailure", "onResponse: response unsuccessful");

            }
        });

    }



    /*************************************************************************************************************************************************/




    void createBottomDialog(Integer dialogTag) {


        if (dialogTag == 1) {

            new BottomDialog.Builder(this)
                    .setTitle("SORRY!")
                    .setContent("You do not seem to have a working internet connection. We can't load the data")
                    .setPositiveText("Retry")
                    .setPositiveBackgroundColorResource(R.color.my_color_primary)
                    .setPositiveTextColorResource(android.R.color.white)
                    .onPositive(new BottomDialog.ButtonCallback() {
                        @Override
                        public void onClick(BottomDialog dialog) {

                            requestArtists();
                        }
                    }).show();
        }else if( dialogTag ==2){


            new BottomDialog.Builder(this)
                    .setTitle("SORRY!")
                    .setContent("We have failed to refresh the artists, Please retry!")
                    .setPositiveText("Retry")
                    .setPositiveBackgroundColorResource(R.color.my_color_primary)
                    .setPositiveTextColorResource(android.R.color.white)
                    .onPositive(new BottomDialog.ButtonCallback() {
                        @Override
                        public void onClick(BottomDialog dialog) {

                            refreshArtists();

                        }
                    }).show();





        }else if(dialogTag == 3){
            new BottomDialog.Builder(this)
                    .setTitle("SORRY!")
                    .setContent("We have failed to automatically load more artists, Please retry!")
                    .setPositiveText("Retry")
                    .setPositiveBackgroundColorResource(R.color.my_color_primary)
                    .setPositiveTextColorResource(android.R.color.white)
                    .onPositive(new BottomDialog.ButtonCallback() {
                        @Override
                        public void onClick(BottomDialog dialog) {

                            loadMoreButton.callOnClick();

                        }
                    }).show();}


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







/*  <com.yalantis.jellytoolbar.widget.JellyToolbar
        android:id="@+id/music_main_toolbar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/my_color_primary"
        android:paddingStart="8dp"
        app:cancelIcon="@drawable/cancel_black_simple"
        app:endColor="@color/my_color_primary"
        app:icon="@drawable/search_black_simple"
        app:startColor="@color/my_color_primary_darker_shade"
        app:title="Search Artists"
        app:titleTextColor="@android:color/white" />

*/


/*<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#FFF"
    android:orientation="vertical"
    tools:context=".Music">


    <android.support.v7.widget.Toolbar
        android:id="@+id/music_action_bar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/my_color_primary"
        android:minHeight="64dp"
        app:titleTextColor="@color/my_color_bg"

        />


    <TextView
        android:id="@+id/click_for_search"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="12dp"
        android:layout_marginBottom="10dp"
        android:fontFamily="@font/montserrat_medium"
        android:text="@string/artists"
        android:textAlignment="center"
        android:textColor="@color/my_color_secondary"
        android:textSize="15sp" />

    <android.support.v4.widget.SwipeRefreshLayout
        android:id="@+id/activity_music_home_artist_swipe_refresh"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content">

        <android.support.v7.widget.RecyclerView
            android:id="@+id/music_main_recycler_view"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_gravity="center_horizontal"
            android:layout_margin="8dp" />


    </android.support.v4.widget.SwipeRefreshLayout>


</LinearLayout>*/



/*<?xml version="1.0" encoding="utf-8"?>
<android.support.v7.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    style="@style/CardView.Light"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginEnd="8dp"
    android:layout_marginStart="8dp"
    android:layout_marginTop="8dp">

    <android.support.constraint.ConstraintLayout xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <ImageView
            android:id="@+id/music_activity_artist_rec_view_image"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:contentDescription="@string/artists_image"
            android:scaleType="centerCrop"
            android:src="@drawable/test_default_img1"
            app:layout_constraintBottom_toTopOf="@+id/music_activity_artist_rec_view_name"
            app:layout_constraintDimensionRatio="H,16:9"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintVertical_chainStyle="packed" />

        <TextView
            android:id="@+id/music_activity_artist_rec_view_name"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp"
            android:layout_marginEnd="16dp"
            android:layout_marginStart="16dp"
            android:layout_marginTop="8dp"
            android:fontFamily="@font/montserrat_regular"
            android:lineSpacingExtra="8dp"
            android:text="@string/my_app_name"
            android:textAlignment="center"
            android:textAppearance="@style/TextAppearance.AppCompat.Body1"
            android:textColor="@color/my_color_secondary"
            android:textSize="12sp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@+id/music_activity_artist_rec_view_image" />

    </android.support.constraint.ConstraintLayout>


</android.support.v7.widget.CardView>*/