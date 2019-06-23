package com.example.dell.muhingalayoutprototypes;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FooterAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.dell.muhingalayoutprototypes.MainActivity.EXTRA_GLOBAL_USER;

public class Venues extends AppCompatActivity {

    //miscellaneous objects
    Boolean onRefreshing = false, infiniteLoading = false;
    Boolean onFilteredRefreshing = false, filteredInfiniteLoading = false; //shows whether the user is refeshing or loading more filtered items;
    ArrayList<VenuesResponse> allVenuesResponseArray = new ArrayList<>(), filteredVenuesResponseArray = new ArrayList<>();
    ArrayList<String> queryStringsHolder = new ArrayList<>();
    ArrayList<String> locationOptions = new ArrayList<String>();
    String queryString = null;
    StringBuilder mb = new StringBuilder();
    Boolean isForRent = false;
    Boolean isForSale = false, filteredState = false; //filtered state helps determine whether the user has applied filters to the items to be returned

    //declare view objects
    Button filterVenuesButton;
    EditText venuesPriceEditText;
    Spinner venuesLocationSpinner;

    //toolbar objects
    Toolbar venuesMainToolbar;

    //expandable layout objects
    ExpandableLayout venuesExpandableLayout;


    //recycler view objects
    RecyclerView venuesMainRecView;
    SwipeRefreshLayout venuesMainRecViewSwipeRefresh;

    //declare the retrofit objects. All these are used with retrofit
    Retrofit.Builder builder;
    Retrofit myRetrofit;
    RetrofitClient myWebClient;
    retrofit2.Call<ArrayList<VenuesResponse>> allVenuesCall, filteredVenuesCall, locationsCall;

    //holds the dynamic parameters used in the request url query
    Map<String, String> venuesFilterMap = new HashMap<String, String>();
    Map<String, String> venuesUserFilterMap = new HashMap<>();
    Map<String, String> locationsMap = new HashMap<>(); //holds the query for the locations

    Integer tableOffset = 0, filteredTableOffset = 0;   //this increases the offset from the top of the table when items are being retrieved from backendless
    String tableOffsetString = tableOffset.toString(), filteredTableOffsetString = filteredTableOffset.toString();


    //fast adapter objects
    FastItemAdapter<VenuesResponse> venuesRecViewFastAdapter;
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    FooterAdapter<ProgressItem> footerAdapter = new FooterAdapter<>();

    public static final String EXTRA_ARRAY = "com.example.muhinga.venuesItemImageReferences";
    public static final String EXTRA_DESCRIPTION = "com.example.muhinga.venuesItemDescription";
    public static final String EXTRA_TITLE = "com.example.muhinga.venuesItemTitle";
    public static final String EXTRA_PRICE = "com.example.muhinga.venuesItemPrice";
    public static final String EXTRA_LOCATION = "com.example.muhinga.venuesItemLocation";
    public static final String EXTRA_SIZE = "com.example.muhinga.venuesItemSize";
    public static final String EXTRA_PHONE = "com.example.muhinga.venuesOwnerPhone";
    public static final String EXTRA_ID = "com.example.muhinga.venuesId";


    //user
    String globalCurrentUserJson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venues);

        //receive intents
        Intent intent = getIntent();
        globalCurrentUserJson = intent.getStringExtra(MainActivity.EXTRA_GLOBAL_USER);



        //initialize the views
        venuesMainRecViewSwipeRefresh = findViewById(R.id.venues_swipe_refresh);
        filterVenuesButton = findViewById(R.id.submit_venues_filter_button);
        venuesLocationSpinner = findViewById(R.id.venues_location_spinner);
        venuesPriceEditText = findViewById(R.id.venues_max_price_edit_text);


        //Toolbar
        venuesMainToolbar = findViewById(R.id.venues_action_bar);
        venuesMainToolbar.setTitle(R.string.add_filters);
        setSupportActionBar(venuesMainToolbar);

        //expandable layout
        venuesExpandableLayout = findViewById(R.id.venues_filter_area_expandable_layout);



        //build out main recycler view
        venuesMainRecView = findViewById(R.id.venues_activity_rec_view);
        venuesMainRecView.setHasFixedSize(true);
        venuesMainRecView.setLayoutManager(new GridLayoutManager(Venues.this, 1, 1, false));

        //initialize the fast adapter which will manage everything
        venuesRecViewFastAdapter = new FastItemAdapter<>();



        //initialize the endless scroll listener
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(footerAdapter) {
            @Override
            public void onLoadMore(int currentPage) {


                footerAdapter.clear();
                footerAdapter.add(new ProgressItem().withEnabled(false));

                //a statement to check if the user is loading more items that have been filtered or just loading more of all items unfiltered
                if (filteredState) {
                    loadMoreFilteredVenues();
                } else {
                    loadMoreVenues();
                }

            }
        };


        venuesMainRecViewSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //a statement to check if the user is refreshing items that have been filtered or just refreshing all items unfiltered
                if (filteredState) {
                    refreshFilteredVenues();
                } else {
                    refreshVenues();
                }
                Log.d("myLogsrefreshingvalue", onRefreshing.toString());


            }
        });


        venuesMainRecView.addOnScrollListener(endlessRecyclerOnScrollListener);


        //fill the query map object for the retrofit query
        venuesFilterMap.put("pageSize", "4");
        venuesFilterMap.put("offset", tableOffsetString);
        venuesFilterMap.put("sortBy", "created%20desc");


        buildRetrofitClient();  //build the retrofit client

        requestVenues();    //make the initial / first  houses request

        populateLocations(); //add the locations to the locations spinner


        filterVenuesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getFilters();
                requestFilteredVenues();
                filteredState = true;

            }
        });


        venuesRecViewFastAdapter.withSelectable(true);
        venuesRecViewFastAdapter.withOnClickListener(new FastAdapter.OnClickListener<VenuesResponse>() {
            @Override
            public boolean onClick(View v, IAdapter<VenuesResponse> adapter, VenuesResponse item, int position) {

                Intent intent = new Intent(Venues.this, VenuesDetails.class);
                ArrayList<Object> venuesItemImageReferences = new ArrayList<>();
                String location, title, price, size, description, ownerPhone, venueId;



                //add the image references to the image reference array
                venuesItemImageReferences.add(item.getMainImageReference());
                venuesItemImageReferences.add(item.getImg2());
                venuesItemImageReferences.add(item.getImg3());
                venuesItemImageReferences.add(item.getImg4());
                venuesItemImageReferences.add(item.getImg5());

                //add the other details to the respective variables
                location = item.getLocation();
                title = item.getTitle();
                price = item.getPrice();
                size = item.getCapacity();
                description = item.getDescription();
                ownerPhone = item.getPhone();
                venueId = item.getObjectId();

                intent.putExtra(EXTRA_GLOBAL_USER, globalCurrentUserJson);
                intent.putExtra(EXTRA_ARRAY, venuesItemImageReferences);
                intent.putExtra(EXTRA_ID,venueId);
                intent.putExtra(EXTRA_LOCATION, location);
                intent.putExtra(EXTRA_TITLE, title);
                intent.putExtra(EXTRA_PRICE, price);
                intent.putExtra(EXTRA_SIZE, size);
                intent.putExtra(EXTRA_DESCRIPTION, description);
                intent.putExtra(EXTRA_PHONE,ownerPhone);
                startActivity(intent);


                return true;
            }
        });


    }



    //inflate the menu layout file for the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.house_app_bar_menu, menu);
        return true;
    }


    //specify the actions that happen when each menu item is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.toggle_filters):
                if (venuesExpandableLayout.isExpanded()) {
                    venuesExpandableLayout.collapse();
                } else {
                    venuesExpandableLayout.expand();
                }


                break;
            default:
                break;
        }
        return true;
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
        allVenuesCall = myWebClient.getFilteredVenues(venuesFilterMap);
        filteredVenuesCall = myWebClient.getFilteredVenues(venuesUserFilterMap);
        locationsCall = myWebClient.getFilteredVenues(locationsMap);

    }


    /*************************************************************************************************************************************************/


    void requestVenues() {

        //make the call
        allVenuesCall.clone().enqueue(new Callback<ArrayList<VenuesResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<VenuesResponse>> call, Response<ArrayList<VenuesResponse>> response) {

                if (!onRefreshing && !infiniteLoading) {

                    //perform the normal sequence of actions for a first time load
                    allVenuesResponseArray = response.body();
                    venuesRecViewFastAdapter.add(allVenuesResponseArray);
                    venuesMainRecView.setAdapter(footerAdapter.wrap(venuesRecViewFastAdapter));


                    Log.d("myLogsRequestUrl", response.raw().request().url().toString());

                } else if (onRefreshing && !infiniteLoading) {

                    //perform the sequence of actions for a refreshed load
                    allVenuesResponseArray.clear();
                    allVenuesResponseArray = response.body();
                    venuesRecViewFastAdapter.clear();
                    venuesMainRecView.clearOnScrollListeners();
                    venuesMainRecView.addOnScrollListener(endlessRecyclerOnScrollListener);
                    venuesRecViewFastAdapter.add(response.body());
                    endlessRecyclerOnScrollListener.resetPageCount();


                    Log.d("myLogsRequestUrlOR", response.raw().request().url().toString());


                } else if (infiniteLoading && !onRefreshing) {

                    allVenuesResponseArray.addAll(response.body());
                    footerAdapter.clear();
                    if (response.body().size() > 0) {
                        venuesRecViewFastAdapter.add(response.body());
                    } else {
                        Toast.makeText(Venues.this, "No more items", Toast.LENGTH_LONG).show();
                    }


                    Log.d("myLogsRequestUrlIL", response.raw().request().url().toString() + " table offset = " + tableOffset);
                    infiniteLoading = false;


                }

                Log.d("myLogsOnSuccess", "onResponse: response successful");


            }

            @Override
            public void onFailure(Call<ArrayList<VenuesResponse>> call, Throwable t) {

                Log.d("myLogsOnFailure", "onResponse: response unsuccessful");

            }
        });

    }

    /*************************************************************************************************************************************************/


    /*************************************************************************************************************************************************/


    void refreshVenues() {    //method called when user attempts to refresh the houses recycler view


        tableOffset = 0;
        tableOffsetString = tableOffset.toString();
        venuesFilterMap.put("offset", tableOffsetString);  //update the value of the offset in the request url
        onRefreshing = true;
        infiniteLoading = false;
        requestVenues();

        //stop the refreshing animation
        venuesMainRecViewSwipeRefresh.setRefreshing(false);
    }


    /*************************************************************************************************************************************************/


    void loadMoreVenues() {

        //TODO The allVenuesResponseArray exists just to give a count. Maybe the count could be more effectively stored in an integer value?
        tableOffset = allVenuesResponseArray.size();
        tableOffsetString = tableOffset.toString();
        venuesFilterMap.put("offset", tableOffsetString);    //update the value of the offset in the request url
        Log.d("myLogs", "loadMoreHouses: " + venuesFilterMap.toString());
        infiniteLoading = true;
        onRefreshing = false;
        requestVenues();
    }


    /*************************************************************************************************************************************************/


    void getFilters() {

        //clear the query filter and the string builder mb
        queryString = null;
        mb.delete(0, mb.length());
        queryStringsHolder.clear();

        //the if statement checks if the venues location spinner's selected item is 'all' and skips adding the query if it is
        if (!venuesLocationSpinner.getSelectedItem().equals("ALL")) {

            //this adds "AND" to the query if it is appended to another query and adds nothing if it is alone
            if (queryStringsHolder.isEmpty()) {
                queryStringsHolder.add("location%3D" + "'" + venuesLocationSpinner.getSelectedItem().toString() + "'");
            } else {
                queryStringsHolder.add("%20AND%20Location%3D" + "'" + venuesLocationSpinner.getSelectedItem().toString() + "'");
            }
        }


        //this adds the data from the Price edit text
        if (!venuesPriceEditText.getText().toString().isEmpty()) {

            if (queryStringsHolder.isEmpty()) {
                queryStringsHolder.add("price%3C%3D" + venuesPriceEditText.getText().toString());
            } else {
                queryStringsHolder.add("%20AND%20price%3C%3D" + venuesPriceEditText.getText().toString());
            }

        }


        for (int i = 0; i < queryStringsHolder.size(); i++) {

            mb.append(queryStringsHolder.get(i));

        }

        queryString = mb.toString();
        Log.d(queryString, "mquery");


        venuesUserFilterMap.putAll(venuesFilterMap);

        //update the value of the offset in the request url before you make the call
        filteredTableOffset = 0;
        filteredTableOffsetString = filteredTableOffset.toString();
        venuesUserFilterMap.put("offset", filteredTableOffsetString);

        venuesUserFilterMap.put("where", queryString);

        //ToDo What if the user refreshes a set of filtered calls?


    }


    /*************************************************************************************************************************************************/


    void populateLocations() {

        //add an All locations option for the user to select all available locations
        locationOptions.add(getString(R.string.ALL));

        locationsMap.put("props", "location");

        //initialize the spinner and assign it an adapter
        ArrayAdapter<String> locationSpinnerAdapter = new ArrayAdapter<>(Venues.this, android.R.layout.simple_spinner_item, locationOptions);
        //set the layout resource for the spinner adapter
        locationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        venuesLocationSpinner.setAdapter(locationSpinnerAdapter);

        locationsCall.clone().enqueue(new Callback<ArrayList<VenuesResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<VenuesResponse>> call, Response<ArrayList<VenuesResponse>> response) {

                String stringHolder;
                int rSize = response.body().size();

                //a for statement to cycle through the response and add every unique location to the locationOptions array
                for (int counter = 0; counter < rSize; counter++) {
                    stringHolder = response.body().get(counter).getLocation();
                    if (!locationOptions.contains(stringHolder)) {
                        locationOptions.add(stringHolder);
                    }
                }


            }

            @Override
            public void onFailure(Call<ArrayList<VenuesResponse>> call, Throwable t) {

                Toast.makeText(Venues.this, "No locations Available", Toast.LENGTH_LONG).show();

            }
        });


    }


    /*************************************************************************************************************************************************/

    void requestFilteredVenues() {

        filteredVenuesCall.clone().enqueue(new Callback<ArrayList<VenuesResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<VenuesResponse>> call, Response<ArrayList<VenuesResponse>> response) {

                //do not use the diffutil because we are loading  only ten items at a time and there is no justification for it to filter only ten items.

                if (response.isSuccessful()) {
                    if (!filteredInfiniteLoading && !onFilteredRefreshing) {
                        filteredVenuesResponseArray = response.body();
                        venuesRecViewFastAdapter.clear();
                        venuesMainRecView.clearOnScrollListeners();
                        venuesMainRecView.addOnScrollListener(endlessRecyclerOnScrollListener);
                        venuesRecViewFastAdapter.add(response.body());
                        endlessRecyclerOnScrollListener.resetPageCount();

                    } else if (filteredInfiniteLoading && !onFilteredRefreshing) {

                        filteredVenuesResponseArray.addAll(response.body());
                        footerAdapter.clear();
                        if (response.body().size() > 0) {
                            venuesRecViewFastAdapter.add(response.body());
                        } else {
                            Toast.makeText(Venues.this, "No more items", Toast.LENGTH_LONG).show();
                        }

                        Log.d("myLogsRequestUrlFIL", response.raw().request().url().toString() + " table offset = " + tableOffset);
                        filteredInfiniteLoading = false;


                    } else if (onFilteredRefreshing && !filteredInfiniteLoading) {

                        filteredVenuesResponseArray.clear();
                        filteredVenuesResponseArray = response.body();
                        venuesRecViewFastAdapter.clear();
                        venuesMainRecView.clearOnScrollListeners();
                        venuesMainRecView.addOnScrollListener(endlessRecyclerOnScrollListener);
                        venuesRecViewFastAdapter.add(response.body());
                        endlessRecyclerOnScrollListener.resetPageCount();


                        Log.d("myLogsRequestUrlFOR", response.raw().request().url().toString());
                    }
                } else {
                    Toast.makeText(Venues.this, "check the filters", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<VenuesResponse>> call, Throwable t) {

                Log.d("myLogsFilteredFail", " Request has failed because " + t.getMessage());
            }
        });

    }


    /*************************************************************************************************************************************************/


    void loadMoreFilteredVenues() {

        filteredInfiniteLoading = true;
        onFilteredRefreshing = false;
        filteredTableOffset = filteredVenuesResponseArray.size();
        filteredTableOffsetString = filteredTableOffset.toString();
        venuesUserFilterMap.put("offset", filteredTableOffsetString);
        requestFilteredVenues();

    }


    /*************************************************************************************************************************************************/

    void refreshFilteredVenues() {


        filteredTableOffset = 0;
        filteredTableOffsetString = filteredTableOffset.toString();
        venuesUserFilterMap.put("offset", filteredTableOffsetString);  //update the value of the offset in the request url
        onFilteredRefreshing = true;
        filteredInfiniteLoading = false;
        requestFilteredVenues();

        //stop the refreshing animation
        venuesMainRecViewSwipeRefresh.setRefreshing(false);

    }


}
