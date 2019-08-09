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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

public class Land extends AppCompatActivity {

    //miscellaneous objects
    Integer responseCount;
    Boolean onRefreshing = false, infiniteLoading = false; //shows weather the user is refreshing or loading more items respectively
    Boolean filteredState = false;
    Boolean onFilteredRefreshing = false, filteredInfiniteLoading = false; //shows whether the user is refeshing or loading more filtered items;
    ArrayList<String> queryStringsHolder = new ArrayList<>();
    ArrayList<String> locationOptions = new ArrayList<String>();
    String queryString = null;
    public static final String EXTRA_ARRAY = "com.example.muhinga.landItemImageReferences";
    public static final String EXTRA_DESCRIPTION = "com.example.muhinga.landItemDescription";
    public static final String EXTRA_TITLE = "com.example.muhinga.landItemTitle";
    public static final String EXTRA_PRICE = "com.example.muhinga.landItemPrice";
    public static final String EXTRA_LOCATION = "com.example.muhinga.landItemLocation";
    public static final String EXTRA_SIZE = "com.example.muhinga.landItemSize";
    public static final String EXTRA_OWNER_PHONE = "com.example.muhinga.landOwnerPhone";
    public static final String EXTRA_VIEWING_DATES = "com.example.muhinga.landViewingDates";

    StringBuilder mb = new StringBuilder();


    //declare the view objects
    SwipeRefreshLayout landSwipeRefresh; //swipe to refresh view for the land recycler view
    EditText landPriceEditText, landSizeEditText;
    Button filterLandButton;
    Spinner landLocationSpinner;

    //user object
    String globalCurrentUserJson ;



    //Toolbar objects
    Toolbar landMainToolBar;

    //expandable layout
    ExpandableLayout expandableLayout;


    //declare the recycler view objects
    RecyclerView landMainRecView;
    ArrayList<LandResponse> allLandResponseArray, filteredLandResponseArray;


    //declare the retrofit objects. All these are used with retrofit
    Retrofit.Builder builder;
    Retrofit myRetrofit;
    RetrofitClient myWebClient;
    retrofit2.Call<ArrayList<LandResponse>> allLandCall, landLocationsCall, filteredLandCall;
    Map<String, String> landFilterMap = new HashMap<String, String>(), landLocationsMap = new HashMap<String, String>(), landUserFilterMap = new HashMap<String, String>();
    Integer tableOffset = 0, filteredTableOffset = 0;   //this increases the offset from the top of the table when items are being retrieved from backendless
    String tableOffsetString = tableOffset.toString(), filteredTableOffsetString = filteredTableOffset.toString();

    //Fast adapter objects
    FastItemAdapter<LandResponse> landFastAdapter = new FastItemAdapter<>();    //create our FastAdapter which will manage everything
    FooterAdapter<ProgressItem> footerAdapter = new FooterAdapter<>();
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land);


        //delay the display of soft keyboard until the user ties to input details into the filters
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        //receive intents
        Intent intent = getIntent();
        globalCurrentUserJson = intent.getStringExtra(MainActivity.EXTRA_GLOBAL_USER);


        //Initialize the views
        landPriceEditText = findViewById(R.id.land_max_price_edit_text);
        landSizeEditText = findViewById(R.id.land_max_size_edit_text);
        filterLandButton = findViewById(R.id.submit_land_filter_button);
        landLocationSpinner = findViewById(R.id.land_home_location_spinner);
        landSwipeRefresh = findViewById(R.id.land_swipe_refresh);


        //toolbar
        landMainToolBar = findViewById(R.id.land_action_bar);
        landMainToolBar.setTitle(R.string.add_filters);
        setSupportActionBar(landMainToolBar);

        //expandable layout
        expandableLayout = findViewById(R.id.land_filter_area_expandable_layout);


        //build out the main recycler view
        landMainRecView = findViewById(R.id.land_activity_rec_view);
        landMainRecView.setHasFixedSize(true);
        landMainRecView.setLayoutManager(new GridLayoutManager(Land.this, 1, 1, false));

        //initialize our FastAdapter which will manage everything
        landFastAdapter = new FastItemAdapter<>();


        //initialize the endless scroll listener
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(footerAdapter) {
            @Override
            public void onLoadMore(int currentPage) {


                footerAdapter.clear();
                footerAdapter.add(new ProgressItem().withEnabled(false));


                //a statement to check if the user is loading more items that have been filtered or just loading more of all items unfiltered
                if (filteredState) {
                    loadMoreFilteredLand();
                } else {
                    loadMoreLand();
                }

            }

        };


        //set the on refresh listener to the swipe to refresh view
        landSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                //a statement to check if the user is refreshing items that have been filtered or just refreshing all items unfiltered
                if (filteredState) {
                    refreshFilteredLand();
                } else {
                    refreshLand();
                }
                Log.d("myLogsrefreshingvalue", onRefreshing.toString());
            }

        });


        //set the infinite/endless load on scroll listener to the recycler view
        landMainRecView.addOnScrollListener(endlessRecyclerOnScrollListener);


        //fill the query map object for the retrofit query
        landFilterMap.put("pageSize", "4");
        landFilterMap.put("offset", tableOffsetString);
        landFilterMap.put("sortBy", "created%20desc");


        buildRetrofitClient();  //build the retrofit client

        requestLand(); //make the initial / first  houses request

        populateLocations(); //add the locations to the locations spinner


        //add the onclick listener for the filter button
        filterLandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getFilters();
                requestFilteredLand();
                filteredState = true;

            }
        });


        //add an onClick on the fast adapter
        landFastAdapter.withSelectable(true);
        landFastAdapter.withOnClickListener(new FastAdapter.OnClickListener<LandResponse>() {
            @Override
            public boolean onClick(View v, IAdapter<LandResponse> adapter, LandResponse item, int position) {

                // Handle click here

                Intent intent = new Intent(Land.this, LandDetails.class);
                ArrayList<Object> landItemImageReferences = new ArrayList<>();
                String location, title, price, size, description , ownerPhone, viewingDates,objectId;

                //add the image references to the image reference array
                landItemImageReferences.add(item.getMianImageReference());
                landItemImageReferences.add(item.getImg2());
                landItemImageReferences.add(item.getImg3());
                landItemImageReferences.add(item.getImg4());
                landItemImageReferences.add(item.getImg5());

                //add the other details to the respective variables
                location = item.getLocation();
                title = item.getTitle();
                price = item.getPrice();
                size = item.getSize();
                description = item.getDescription();
                ownerPhone = item.getPhone();
                viewingDates = item.getViewingDates();
                objectId = item.getObjectId();

                intent.putExtra(EXTRA_ARRAY, landItemImageReferences);
                intent.putExtra(EXTRA_LOCATION, location);
                intent.putExtra(EXTRA_TITLE, title);
                intent.putExtra(EXTRA_PRICE, price);
                intent.putExtra(EXTRA_SIZE, size);
                intent.putExtra(EXTRA_DESCRIPTION, description);
                intent.putExtra(EXTRA_VIEWING_DATES, viewingDates);
                intent.putExtra(EXTRA_OWNER_PHONE, ownerPhone);
                intent.putExtra("globalCurrentUser",globalCurrentUserJson);
                intent.putExtra("currentLandId",objectId);

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
                if (expandableLayout.isExpanded()) {
                    expandableLayout.collapse();
                } else {
                    expandableLayout.expand();
                }


                break;
            default:
                break;
        }
        return true;
    }









    /*************************************************************************************************************************************************/
    //APP BAR CODE ENDS HERE
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
    allLandCall = myWebClient.getFilteredLand(landFilterMap);
    filteredLandCall = myWebClient.getFilteredLand(landUserFilterMap);
    landLocationsCall = myWebClient.getFilteredLand(landLocationsMap);
}



/*************************************************************************************************************************************************/


void requestLand() {

    //make the call
    allLandCall.clone().enqueue(new Callback<ArrayList<LandResponse>>() {
        @Override
        public void onResponse(Call<ArrayList<LandResponse>> call, Response<ArrayList<LandResponse>> response) {

            if (!onRefreshing && !infiniteLoading) {

                //perform the normal sequence of actions for a first time load
                allLandResponseArray = response.body();
                landFastAdapter.add(allLandResponseArray);
                landMainRecView.setAdapter(footerAdapter.wrap(landFastAdapter));


                Log.d("myLogsRequestUrl", response.raw().request().url().toString());

            } else if (onRefreshing && !infiniteLoading) {

                //perform the sequence of actions for a refreshed load
                allLandResponseArray.clear();
                allLandResponseArray = response.body();
                landFastAdapter.clear();
                landMainRecView.clearOnScrollListeners();
                landMainRecView.addOnScrollListener(endlessRecyclerOnScrollListener);
                landFastAdapter.add(response.body());
                endlessRecyclerOnScrollListener.resetPageCount();


                Log.d("myLogsRequestUrlOR", response.raw().request().url().toString());


            } else if (infiniteLoading && !onRefreshing) {

                allLandResponseArray.addAll(response.body());
                footerAdapter.clear();
                if (response.body().size() > 0) {
                    landFastAdapter.add(response.body());
                } else {
                    Toast.makeText(Land.this, "No more items", Toast.LENGTH_LONG).show();
                }


                Log.d("myLogsRequestUrlIL", response.raw().request().url().toString() + " table offset = " + tableOffset);
                infiniteLoading = false;


            }

            Log.d("myLogsOnSuccess", "onResponse: response successful");


        }

        @Override
        public void onFailure(Call<ArrayList<LandResponse>> call, Throwable t) {

            Log.d("myLogsOnFailure", "onResponse: response unsuccessful");

        }
    });

}

/*************************************************************************************************************************************************/


void refreshLand() {    //method called when user attempts to refresh the houses recycler view


    tableOffset = 0;
    tableOffsetString = tableOffset.toString();
    landFilterMap.put("offset", tableOffsetString);  //update the value of the offset in the request url
    onRefreshing = true;
    infiniteLoading = false;
    requestLand();

    //stop the refreshing animation
    landSwipeRefresh.setRefreshing(false);


    // housesMainRecView.addOnScrollListener(endlessRecyclerOnScrollListener);
    //onRefreshing = false;

}



    /*************************************************************************************************************************************************/


    void loadMoreLand() {

        //TODO The allHousesResponseArray exists just to give a count. Maybe the count could be more effectively stored in an integer value?
        tableOffset = allLandResponseArray.size();
        tableOffsetString = tableOffset.toString();
        landFilterMap.put("offset", tableOffsetString);    //update the value of the offset in the request url
        Log.d("myLogs", "loadMoreHouses: " + landFilterMap.toString());
        infiniteLoading = true;
        onRefreshing = false;
        requestLand();
    }

    /*************************************************************************************************************************************************/



    /*************************************************************************************************************************************************/


    void getFilters() {

        //clear the query filter and the string builder mb
        queryString = null;
        mb.delete(0, mb.length());
        queryStringsHolder.clear();

        //the if statement checks if the land location spinner's selected item is 'all' and skips adding the query if it is
        if (!landLocationSpinner.getSelectedItem().equals("ALL")) {

            //this adds "AND" to the query if it is appended to another query and adds nothing if it is alone
            if (queryStringsHolder.isEmpty()) {
                queryStringsHolder.add("Location%3D" + "'" + landLocationSpinner.getSelectedItem().toString() + "'");
            } else {
                queryStringsHolder.add("%20AND%20Location%3D" + "'" + landLocationSpinner.getSelectedItem().toString() + "'");
            }
        }


        //this adds the data from the Price edit text
        if (!landPriceEditText.getText().toString().isEmpty()) {

            if (queryStringsHolder.isEmpty()) {
                queryStringsHolder.add("price%3C%3D" + landPriceEditText.getText().toString());
            } else {
                queryStringsHolder.add("%20AND%20price%3C%3D" + landPriceEditText.getText().toString());
            }

        }


        //this adds the data from the size edit text
        if (!landSizeEditText.getText().toString().isEmpty()) {

            if (queryStringsHolder.isEmpty()) {
                queryStringsHolder.add("size%3C%3D" + landSizeEditText.getText().toString());
            } else {
                queryStringsHolder.add("%20AND%20size%3C%3D" + landSizeEditText.getText().toString());
            }

        }


        for (int i = 0; i < queryStringsHolder.size(); i++) {

            mb.append(queryStringsHolder.get(i));

        }

        queryString = mb.toString();
        Log.d(queryString, "mquery");


        landUserFilterMap.putAll(landFilterMap);

        //update the value of the offset in the request url before you make the call
        filteredTableOffset = 0;
        filteredTableOffsetString = filteredTableOffset.toString();
        landUserFilterMap.put("offset", filteredTableOffsetString);

        landUserFilterMap.put("where", queryString);


    }


    /*************************************************************************************************************************************************/


    void populateLocations() {

        //add an All locations option for the user to select all available locations
        locationOptions.add(getString(R.string.ALL));

        landLocationsMap.put("props", "Location");

        //initialize the spinner and assign it an adapter
        ArrayAdapter<String> locationSpinnerAdapter = new ArrayAdapter<>(Land.this, android.R.layout.simple_spinner_item, locationOptions);
        //set the layout resource for the spinner adapter
        locationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        landLocationSpinner.setAdapter(locationSpinnerAdapter);

        landLocationsCall.clone().enqueue(new Callback<ArrayList<LandResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<LandResponse>> call, Response<ArrayList<LandResponse>> response) {

                String stringHolder;
                int rSize = 0;
                if (response.body() != null) {
                    rSize = response.body().size();
                }

                //a for statement to cycle through the response and add every unique location to the locationOptions array
                for (int counter = 0; counter < rSize; counter++) {
                    stringHolder = response.body().get(counter).getLocation();
                    if (!locationOptions.contains(stringHolder)) {
                        locationOptions.add(stringHolder);
                    }
                }


            }

            @Override
            public void onFailure(Call<ArrayList<LandResponse>> call, Throwable t) {

                Toast.makeText(Land.this, "No locations Available", Toast.LENGTH_LONG).show();

            }
        });


    }


/*************************************************************************************************************************************************/



void requestFilteredLand() {

    filteredLandCall.clone().enqueue(new Callback<ArrayList<LandResponse>>() {
        @Override
        public void onResponse(Call<ArrayList<LandResponse>> call, Response<ArrayList<LandResponse>> response) {

            //do not use the diffutil because it loads only ten items and there is no justification for it to filter only ten items.

            if (response.isSuccessful()) {
                if (!filteredInfiniteLoading && !onFilteredRefreshing) {
                    filteredLandResponseArray = response.body();
                    landFastAdapter.clear();
                    landMainRecView.clearOnScrollListeners();
                    landMainRecView.addOnScrollListener(endlessRecyclerOnScrollListener);
                    landFastAdapter.add(response.body());
                    endlessRecyclerOnScrollListener.resetPageCount();

                } else if (filteredInfiniteLoading && !onFilteredRefreshing) {

                    filteredLandResponseArray.addAll(response.body());
                    footerAdapter.clear();
                    if (response.body().size() > 0) {
                        landFastAdapter.add(response.body());
                    } else {
                        Toast.makeText(Land.this, "No more items", Toast.LENGTH_LONG).show();
                    }

                    Log.d("myLogsRequestUrlFIL", response.raw().request().url().toString() + " table offset = " + tableOffset);
                    filteredInfiniteLoading = false;


                } else if (onFilteredRefreshing && !filteredInfiniteLoading) {

                    filteredLandResponseArray.clear();
                    filteredLandResponseArray = response.body();
                    landFastAdapter.clear();
                    landMainRecView.clearOnScrollListeners();
                    landMainRecView.addOnScrollListener(endlessRecyclerOnScrollListener);
                    landFastAdapter.add(response.body());
                    endlessRecyclerOnScrollListener.resetPageCount();


                    Log.d("myLogsRequestUrlFOR", response.raw().request().url().toString());
                }
            } else {
                Toast.makeText(Land.this, "check the filters", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        public void onFailure(Call<ArrayList<LandResponse>> call, Throwable t) {

            Log.d("myLogsFilteredFail", " Request has failed because " + t.getMessage());
        }
    });

}


    /*************************************************************************************************************************************************/



    void loadMoreFilteredLand() {

        filteredInfiniteLoading = true;
        onFilteredRefreshing = false;
        filteredTableOffset = filteredLandResponseArray.size();
        filteredTableOffsetString = filteredTableOffset.toString();
        landUserFilterMap.put("offset", filteredTableOffsetString);
        requestFilteredLand();

    }


/*************************************************************************************************************************************************/



void refreshFilteredLand() {


    filteredTableOffset = 0;
    filteredTableOffsetString = filteredTableOffset.toString();
    landUserFilterMap.put("offset", filteredTableOffsetString);  //update the value of the offset in the request url
    onFilteredRefreshing = true;
    filteredInfiniteLoading = false;
    requestFilteredLand();

    //stop the refreshing animation
    landSwipeRefresh.setRefreshing(false);

}


    /*************************************************************************************************************************************************/






}
