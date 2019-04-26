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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FooterAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Houses extends AppCompatActivity {


    //miscellaneous objects
    Boolean onRefreshing = false, infiniteLoading = false;  //shows weather the user is refreshing or loading more items respectively
    Boolean onFilteredRefreshing = false, filteredInfiniteLoading = false; //shows whether the user is refeshing or loading more filtered items;
    ArrayList<String> queryStringsHolder = new ArrayList<>();
    ArrayList<String> locationOptions = new ArrayList<String>();
    String queryString = null;
    StringBuilder mb = new StringBuilder();
    Boolean isForRent = false;
    Boolean isForSale = false, filteredState = false; //filtered state helps determine whether the user has applied filters to the items to be returned


    //declare view objects
    EditText housePriceEditText;
    CheckBox forRentCheck, forSaleCheck;
    Button filterHousesButton;
    SwipeRefreshLayout housesSwipeRefresh;  //swipe to refresh view for the houses recycler view
    Spinner houseLocationSpinner;

    //Toolbar objects
    Toolbar mainToolBar;

    //Expandable Layout objects
    ExpandableLayout housesExpandableLayout;


    //declare recycler view objects
    RecyclerView housesMainRecView;
    ArrayList<real_estate> allHousesResponseArray, filteredHousesResponseArray;   //holds all the houses objects that have been returned since the user last refreshed

    //declare the retrofit objects. All these are used with retrofit
    Retrofit.Builder builder;
    Retrofit myRetrofit;
    RetrofitClient myWebClient;
    retrofit2.Call<ArrayList<HousesResponse>> allHousesCall, filteredHousesCall, locationsCall;
    //holds the dynamic parameters used in the request url query
    Map<String, String> housesFilterMap = new HashMap<String, String>();
    Map<String, String> housesUserFilterMap = new HashMap<>();
    Map<String, String> locationsMap = new HashMap<>(); //holds the query for the locations


    Integer tableOffset = 0, filteredTableOffset = 0, testFilteredTableOffset = 0;   //this increases the offset from the top of the table when items are being retrieved from backendless
    String tableOffsetString = tableOffset.toString(), filteredTableOffsetString = filteredTableOffset.toString();


    //create our FastAdapter which will manage everything
    FastItemAdapter<real_estate> housesFastAdapter;
    FooterAdapter<ProgressItem> footerAdapter = new FooterAdapter<>();
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;


    public static final String EXTRA_ARRAY_H = "com.example.muhinga.housesItemImageReferences";
    public static final String EXTRA_DESCRIPTION_H = "com.example.muhinga.housesItemDescription";
    public static final String EXTRA_TITLE_H = "com.example.muhinga.housesItemTitle";
    public static final String EXTRA_PRICE_H = "com.example.muhinga.housesItemPrice";
    public static final String EXTRA_LOCATION_H = "com.example.muhinga.housesItemLocation";
    public static final String EXTRA_RENT_SALE_H = "com.example.muhinga.housesItemRentOrSale";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houses);

        //Initialize the views
        housesSwipeRefresh = findViewById(R.id.houses_swipe_refresh);
        housePriceEditText = findViewById(R.id.house_max_price_edit_text);
        forRentCheck = findViewById(R.id.houses_for_rent_check_box);
        forSaleCheck = findViewById(R.id.houses_for_sale_check_box);
        filterHousesButton = findViewById(R.id.submit_houses_filter_button);
        houseLocationSpinner = findViewById(R.id.houses_location_spinner);

        //Expandable layout views
        housesExpandableLayout = findViewById(R.id.expandable_layout);

        //initialize the toolbar
        mainToolBar = findViewById(R.id.houses_action_bar);
        mainToolBar.setTitle(R.string.add_filters);
        setSupportActionBar(mainToolBar);


        //build out the main recycler view
        housesMainRecView = findViewById(R.id.houses_activity_rec_view);
        housesMainRecView.setHasFixedSize(true);
        housesMainRecView.setLayoutManager(new GridLayoutManager(Houses.this, 1, 1, false));


        //initialize our FastAdapter which will manage everything
        housesFastAdapter = new FastItemAdapter<>();
        allHousesResponseArray = new ArrayList<>();
        filteredHousesResponseArray = new ArrayList<>();


        //initialize the endless scroll listener
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(footerAdapter) {
            @Override
            public void onLoadMore(int currentPage) {


                footerAdapter.clear();
                footerAdapter.add(new ProgressItem().withEnabled(false));

                //a statement to check if the user is loading more items that have been filtered or just loading more of all items unfiltered
                if (filteredState) {
                    loadMoreFilteredHousesBackendless();
                } else {
                    loadMoreHouses();
                }

            }
        };


        //set the on refresh listener to the swipe to refresh view
        housesSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //a statement to check if the user is refreshing items that have been filtered or just refreshing all items unfiltered
                if (filteredState) {
                    refreshFilteredHousesBackendless();
                } else {
                    refreshHouses();
                }
                Log.d("myLogsrefreshingvalue", onRefreshing.toString());
            }


        });


        //set the infinite/endless load on scroll listener to the recycler view
        housesMainRecView.addOnScrollListener(endlessRecyclerOnScrollListener);

        //fill the query map object for the retrofit query
        housesFilterMap.put("pageSize", "4");
        housesFilterMap.put("offset", tableOffsetString);
        housesFilterMap.put("sortBy", "created%20desc");


        buildRetrofitClient();  //build the retrofit client

        requestHousesBackendless();    //make the initial / first  houses request

        populateLocationsBackendless(); //add the locations to the locations spinner


        //add the onclick listener for the filter button
        filterHousesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              /*  getFilters();
                requestFilteredHouses();
                filteredState = true;*/

                getFiltersBackendless();
                requestFilteredHousesBackendless();
                filteredState = true;


            }
        });


        //add an on click to the fast adapter objects
        housesFastAdapter.withSelectable(true);
        housesFastAdapter.withOnClickListener(new FastAdapter.OnClickListener<real_estate>() {
            @Override
            public boolean onClick(View v, IAdapter<real_estate> adapter, real_estate item, int position) {


                // Handle click here
                Intent intent = new Intent(Houses.this, HousesDetails.class);
                ArrayList<String> housesItemImageReferences = new ArrayList<>();
                String location, title, price, rentSale, description;
                Boolean isForRent;


                //add the image references to the image reference array
                housesItemImageReferences.add(item.getMian_image_reference());
                housesItemImageReferences.add(item.getImg_2());
                housesItemImageReferences.add(item.getImg_3());
                housesItemImageReferences.add(item.getImg_4());
                housesItemImageReferences.add(item.getImg_5());

                //add the other details to the respective variables
                location = item.getLocation();
                title = item.getTitle();
                price = item.getPrice();
                description = item.getDescription();
                if (item.getRent()) {

                    rentSale = "For Rent";
                } else {
                    rentSale = "For Sale";
                }


                intent.putExtra(EXTRA_ARRAY_H, housesItemImageReferences);
                intent.putExtra(EXTRA_LOCATION_H, location);
                intent.putExtra(EXTRA_TITLE_H, title);
                intent.putExtra(EXTRA_PRICE_H, price);
                intent.putExtra(EXTRA_RENT_SALE_H, rentSale);
                intent.putExtra(EXTRA_DESCRIPTION_H, description);
                startActivity(intent);


                return true;
            }
        });


        forRentCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    forSaleCheck.setChecked(false);
                }
            }
        });

        forSaleCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    forRentCheck.setChecked(false);

                }
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
                if (housesExpandableLayout.isExpanded()) {
                    housesExpandableLayout.collapse();
                } else {
                    housesExpandableLayout.expand();
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
        allHousesCall = myWebClient.getFilteredHouses(housesFilterMap);
        filteredHousesCall = myWebClient.getFilteredHouses(housesUserFilterMap);
        locationsCall = myWebClient.getFilteredHouses(locationsMap);

    }


    /*************************************************************************************************************************************************/

    void requestHousesBackendless() {

        /*todo



         * */

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(10).setOffset(tableOffset);
        queryBuilder.addSortBy("created DESC");
        Backendless.Data.of(real_estate.class).find(queryBuilder, new AsyncCallback<List<real_estate>>() {
            @Override
            public void handleResponse(List<real_estate> response) {

                if (response != null) {

                    if (!onRefreshing && !infiniteLoading) {

                        //perform the normal sequence of actions for a first time load
                        allHousesResponseArray.addAll(response);
                        housesFastAdapter.add(allHousesResponseArray);
                        housesMainRecView.setAdapter(footerAdapter.wrap(housesFastAdapter));


                        Log.d("myLogsReqHouses", "request was successful");

                    } else if (onRefreshing && !infiniteLoading) {

                        //perform the sequence of actions for a refreshed load
                        allHousesResponseArray.clear();
                        allHousesResponseArray.addAll(response);
                        housesFastAdapter.clear();
                        housesMainRecView.clearOnScrollListeners();
                        housesMainRecView.addOnScrollListener(endlessRecyclerOnScrollListener);
                        housesFastAdapter.add(allHousesResponseArray);
                        endlessRecyclerOnScrollListener.resetPageCount();


                        Log.d("myLogsReqHousesOR", "houses were successfully refreshed");


                    } else if (infiniteLoading && !onRefreshing) {

                        allHousesResponseArray.addAll(response);
                        footerAdapter.clear();
                        if (response.size() > 0) {
                            housesFastAdapter.add(response);
                        } else {
                            Toast.makeText(Houses.this, "No more items", Toast.LENGTH_LONG).show();
                        }


                        Log.d("myLogsReqHousesIL", "Infinite load was successful." + " table offset = " + tableOffset);
                        infiniteLoading = false;


                    }

                    Log.d("myLogsOnSuccess", "onResponse: response successful");
                }

            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Log.d("myLogsReqHousesFail", "The request failed. Error: " + fault.toString());

            }
        });


    }


    /*************************************************************************************************************************************************/


    void refreshHouses() {    //method called when user attempts to refresh the houses recycler view


        tableOffset = 0;
        tableOffsetString = tableOffset.toString();
        housesFilterMap.put("offset", tableOffsetString);  //update the value of the offset in the request url
        onRefreshing = true;
        infiniteLoading = false;
        requestHousesBackendless();

        //stop the refreshing animation
        housesSwipeRefresh.setRefreshing(false);


        // housesMainRecView.addOnScrollListener(endlessRecyclerOnScrollListener);
        //onRefreshing = false;

    }


    /*************************************************************************************************************************************************/


    void loadMoreHouses() {

        //TODO The allHousesResponseArray exists just to give a count. Maybe the count could be more effectively stored in an integer value?
        tableOffset = allHousesResponseArray.size();
        tableOffsetString = tableOffset.toString();
        housesFilterMap.put("offset", tableOffsetString);    //update the value of the offset in the request url
        Log.d("myLogs", "loadMoreHouses: " + housesFilterMap.toString());
        infiniteLoading = true;
        onRefreshing = false;
        requestHousesBackendless();
    }


    /*************************************************************************************************************************************************/

    void getFiltersBackendless() {

        //clear the query filter and the string builder mb
        queryString = null;
        mb.delete(0, mb.length());
        queryStringsHolder.clear();


        //the if statement checks if the house location spinner's selected item is 'all' and skips adding the query if it is

        if (houseLocationSpinner != null) {

            if (houseLocationSpinner.getSelectedItem() != null) {

                if (!houseLocationSpinner.getSelectedItem().equals("ALL")) {

                    //this adds "AND" to the query if it is appended to another query and adds nothing if it is alone
                    if (queryStringsHolder.isEmpty()) {
                        queryStringsHolder.add("Location = " + "'" + houseLocationSpinner.getSelectedItem().toString() + "'");
                    } else {
                        queryStringsHolder.add("AND Location = " + "'" + houseLocationSpinner.getSelectedItem().toString() + "'");
                    }
                }
            } else {

                Log.d("myLogsGetFilters", "the HouseLocationSpinner.GetSelectedItem is null");


            }

        } else {

            Log.d("myLogsGetFilters", "the House location spinner is null");

        }

        //this adds the data from the Price edit text
        if (!housePriceEditText.getText().toString().isEmpty()) {

            if (queryStringsHolder.isEmpty()) {
                queryStringsHolder.add("price <= " + housePriceEditText.getText().toString());
            } else {
                queryStringsHolder.add(" AND price <= " + housePriceEditText.getText().toString());
            }

        }


        if (forRentCheck.isChecked()) {

            if (queryStringsHolder.isEmpty()) {
                isForRent = true;
                queryStringsHolder.add("Rent = " + isForRent.toString());
            } else {
                isForRent = true;
                queryStringsHolder.add(" AND Rent = " + isForRent.toString());
            }


        }


        if (forSaleCheck.isChecked()) {
            if (queryStringsHolder.isEmpty()) {
                isForSale = true;
                queryStringsHolder.add("for_sale = " + isForSale.toString());
            } else if (forRentCheck.isChecked()) {
                isForSale = true;
                queryStringsHolder.add(" OR for_sale = " + isForSale.toString());
            } else if (!forRentCheck.isChecked()) {
                isForSale = true;
                queryStringsHolder.add(" AND for_sale = " + isForSale.toString());
            }


        }


        for (int i = 0; i < queryStringsHolder.size(); i++) {

            mb.append(queryStringsHolder.get(i));

        }


        queryString = mb.toString();
        Log.d(queryString, "mquery");


        //update the value of the offset in the request url before you make the call
        filteredTableOffset = 0;
        testFilteredTableOffset = 0;


    }

    /*************************************************************************************************************************************************/

    void populateLocationsBackendless() {


        //add an All locations option for the user to select all available locations
        locationOptions.add(getString(R.string.ALL));

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.addProperty("Location");

        //initialize the spinner and assign it an adapter
        ArrayAdapter<String> locationSpinnerAdapter = new ArrayAdapter<>(Houses.this, android.R.layout.simple_spinner_item, locationOptions);
        //set the layout resource for the spinner adapter
        locationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        houseLocationSpinner.setAdapter(locationSpinnerAdapter);

        Backendless.Data.of(real_estate.class).find(queryBuilder, new AsyncCallback<List<real_estate>>() {
            @Override
            public void handleResponse(List<real_estate> response) {

                String stringHolder;
                int rSize = response.size();

                //a for statement to cycle through the response and add every unique location to the locationOptions array
                for (int counter = 0; counter < rSize; counter++) {
                    stringHolder = response.get(counter).getLocation();
                    if (!locationOptions.contains(stringHolder)) {
                        locationOptions.add(stringHolder);
                    }
                }


            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Toast.makeText(Houses.this, "No locations Available", Toast.LENGTH_LONG).show();
                Log.d("myLogsReqLocations", "location retrieval failed.  Error: " + fault.toString());


            }
        });


    }


    /*************************************************************************************************************************************************/

    void requestFilteredHousesBackendless() {

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(3).setOffset(testFilteredTableOffset);
        queryBuilder.setWhereClause(queryString);
        Backendless.Data.of(real_estate.class).find(queryBuilder, new AsyncCallback<List<real_estate>>() {
            @Override
            public void handleResponse(List<real_estate> response) {


                //do not use the diffutil because it loads only ten items and there is no justification for it to filter only ten items.

                //todo filtered table offset keeps giving me zero. Why????
                if (response != null) {
                    if (!filteredInfiniteLoading && !onFilteredRefreshing) {
                        filteredHousesResponseArray.addAll(response);

                        //update filtered table offset
                        testFilteredTableOffset = testFilteredTableOffset + response.size();

                        Log.d("myLogsRequestFL", " the filtered load is a success. table offset = " + testFilteredTableOffset);


                        housesFastAdapter.clear();
                        housesMainRecView.clearOnScrollListeners();
                        housesMainRecView.addOnScrollListener(endlessRecyclerOnScrollListener);
                        housesFastAdapter.add(response);
                        endlessRecyclerOnScrollListener.resetPageCount();

                    } else if (filteredInfiniteLoading && !onFilteredRefreshing) {


                        filteredHousesResponseArray.addAll(response);
                        testFilteredTableOffset = testFilteredTableOffset + response.size();
                        footerAdapter.clear();
                        if (response.size() > 0) {
                            housesFastAdapter.add(response);
                        } else {
                            Toast.makeText(Houses.this, "No more items", Toast.LENGTH_LONG).show();
                        }

                        Log.d("myLogsRequestFIL", " the filtered infinite load success. table offset , test offset = " + tableOffset + " ," + testFilteredTableOffset);
                        filteredInfiniteLoading = false;


                    } else if (onFilteredRefreshing && !filteredInfiniteLoading) {

                        testFilteredTableOffset = testFilteredTableOffset + response.size();
                        filteredHousesResponseArray.clear();
                        filteredHousesResponseArray.addAll(response);
                        housesFastAdapter.clear();
                        housesMainRecView.clearOnScrollListeners();
                        housesMainRecView.addOnScrollListener(endlessRecyclerOnScrollListener);
                        housesFastAdapter.add(response);
                        endlessRecyclerOnScrollListener.resetPageCount();

                        onFilteredRefreshing = false;
                        Log.d("myLogsRequestFOR", "filtered on refresh was successfull" + testFilteredTableOffset);
                    }
                } else {
                    Toast.makeText(Houses.this, "check the filters", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void handleFault(BackendlessFault fault) {

                if (fault != null) {
                    Log.d("myLogsFilteredFail", " Request has failed because " + fault.toString());
                }

            }
        });


    }

    /*************************************************************************************************************************************************/

    void loadMoreFilteredHousesBackendless() {

        filteredInfiniteLoading = true;
        onFilteredRefreshing = false;
        filteredTableOffset = filteredHousesResponseArray.size();
        requestFilteredHousesBackendless();

    }

    /*************************************************************************************************************************************************/

    void refreshFilteredHousesBackendless() {


        filteredTableOffset = 0;
        testFilteredTableOffset = 0;
        onFilteredRefreshing = true;
        filteredInfiniteLoading = false;
        requestFilteredHousesBackendless();

        //stop the refreshing animation
        housesSwipeRefresh.setRefreshing(false);

    }


}


/*************************************************************************************************************************************************/
/*
    void loadMoreFilteredHouses() {

        filteredInfiniteLoading = true;
        onFilteredRefreshing = false;
        filteredTableOffset = filteredHousesResponseArray.size();
        filteredTableOffsetString = filteredTableOffset.toString();
        housesUserFilterMap.put("offset", filteredTableOffsetString);
        requestFilteredHouses();

    }

*/
/*************************************************************************************************************************************************/


/*************************************************************************************************************************************************/


 /*   void refreshFilteredHouses() {


        filteredTableOffset = 0;
        filteredTableOffsetString = filteredTableOffset.toString();
        housesUserFilterMap.put("offset", filteredTableOffsetString);  //update the value of the offset in the request url
        onFilteredRefreshing = true;
        filteredInfiniteLoading = false;
        requestFilteredHousesB();

        //stop the refreshing animation
        housesSwipeRefresh.setRefreshing(false);

    }

*/


/*************************************************************************************************************************************************/




 /*   void getFilters() {

        //clear the query filter and the string builder mb
        queryString = null;
        mb.delete(0, mb.length());
        queryStringsHolder.clear();

        //the if statement checks if the house location spinner's selected item is 'all' and skips adding the query if it is
        if (!houseLocationSpinner.getSelectedItem().equals("ALL")) {

            //this adds "AND" to the query if it is appended to another query and adds nothing if it is alone
            if (queryStringsHolder.isEmpty()) {
                queryStringsHolder.add("Location%3D" + "'" + houseLocationSpinner.getSelectedItem().toString() + "'");
            } else {
                queryStringsHolder.add("%20AND%20Location%3D" + "'" + houseLocationSpinner.getSelectedItem().toString() + "'");
            }
        }


        //this adds the data from the Price edit text
        if (!housePriceEditText.getText().toString().isEmpty()) {

            if (queryStringsHolder.isEmpty()) {
                queryStringsHolder.add("price%3C%3D" + housePriceEditText.getText().toString());
            } else {
                queryStringsHolder.add("%20AND%20price%3C%3D" + housePriceEditText.getText().toString());
            }

        }


        if (forRentCheck.isChecked()) {

            if (queryStringsHolder.isEmpty()) {
                isForRent = true;
                queryStringsHolder.add("Rent%3D" + isForRent.toString());
            } else {
                isForRent = true;
                queryStringsHolder.add("%20AND%20Rent%3D" + isForRent.toString());
            }


        }


        if (forSaleCheck.isChecked()) {
            if (queryStringsHolder.isEmpty()) {
                isForSale = true;
                queryStringsHolder.add("for_sale%3D" + isForSale.toString());
            } else if (forRentCheck.isChecked()) {
                isForSale = true;
                queryStringsHolder.add("%20OR%20for_sale%3D" + isForSale.toString());
            } else if (!forRentCheck.isChecked()) {
                isForSale = true;
                queryStringsHolder.add("%20AND%20for_sale3D" + isForSale.toString());
            }


        }


        for (int i = 0; i < queryStringsHolder.size(); i++) {

            mb.append(queryStringsHolder.get(i));

        }

        queryString = mb.toString();
        Log.d(queryString, "mquery");


        housesUserFilterMap.putAll(housesFilterMap);

        //update the value of the offset in the request url before you make the call
        filteredTableOffset = 0;
        filteredTableOffsetString = filteredTableOffset.toString();
        housesUserFilterMap.put("offset", filteredTableOffsetString);

        housesUserFilterMap.put("where", queryString);

        //ToDo What if the user refreshes a set of filtered calls?


    }
*/

/*************************************************************************************************************************************************/



  /*  void requestFilteredHouses() {

        filteredHousesCall.clone().enqueue(new Callback<ArrayList<HousesResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<HousesResponse>> call, Response<ArrayList<HousesResponse>> response) {

                //do not use the diffutil because it loads only ten items and there is no justification for it to filter only ten items.

                if (response.isSuccessful()) {
                    if (!filteredInfiniteLoading && !onFilteredRefreshing) {
                        filteredHousesResponseArray = response.body();
                        housesFastAdapter.clear();
                        housesMainRecView.clearOnScrollListeners();
                        housesMainRecView.addOnScrollListener(endlessRecyclerOnScrollListener);
                        housesFastAdapter.add(response.body());
                        endlessRecyclerOnScrollListener.resetPageCount();

                    } else if (filteredInfiniteLoading && !onFilteredRefreshing) {

                        filteredHousesResponseArray.addAll(response.body());
                        footerAdapter.clear();
                        if (response.body().size() > 0) {
                            housesFastAdapter.add(response.body());
                        } else {
                            Toast.makeText(Houses.this, "No more items", Toast.LENGTH_LONG).show();
                        }

                        Log.d("myLogsRequestUrlFIL", response.raw().request().url().toString() + " table offset = " + tableOffset);
                        filteredInfiniteLoading = false;


                    } else if (onFilteredRefreshing && !filteredInfiniteLoading) {

                        filteredHousesResponseArray.clear();
                        filteredHousesResponseArray = response.body();
                        housesFastAdapter.clear();
                        housesMainRecView.clearOnScrollListeners();
                        housesMainRecView.addOnScrollListener(endlessRecyclerOnScrollListener);
                        housesFastAdapter.add(response.body());
                        endlessRecyclerOnScrollListener.resetPageCount();


                        Log.d("myLogsRequestUrlFOR", response.raw().request().url().toString());
                    }
                } else {
                    Toast.makeText(Houses.this, "check the filters", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<HousesResponse>> call, Throwable t) {

                Log.d("myLogsFilteredFail", " Request has failed because " + t.getMessage());
            }
        });

    }*/


/*************************************************************************************************************************************************/


/*************************************************************************************************************************************************/

    /*void requestHouses() {

        //make the call
        allHousesCall.clone().enqueue(new Callback<ArrayList<HousesResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<HousesResponse>> call, Response<ArrayList<HousesResponse>> response) {

                if (!onRefreshing && !infiniteLoading) {

                    //perform the normal sequence of actions for a first time load
                    allHousesResponseArray = response.body();
                    housesFastAdapter.add(allHousesResponseArray);
                    housesMainRecView.setAdapter(footerAdapter.wrap(housesFastAdapter));


                    Log.d("myLogsRequestUrl", response.raw().request().url().toString());

                } else if (onRefreshing && !infiniteLoading) {

                    //perform the sequence of actions for a refreshed load
                    allHousesResponseArray.clear();
                    allHousesResponseArray = response.body();
                    housesFastAdapter.clear();
                    housesMainRecView.clearOnScrollListeners();
                    housesMainRecView.addOnScrollListener(endlessRecyclerOnScrollListener);
                    housesFastAdapter.add(response.body());
                    endlessRecyclerOnScrollListener.resetPageCount();


                    Log.d("myLogsRequestUrlOR", response.raw().request().url().toString());


                } else if (infiniteLoading && !onRefreshing) {

                    allHousesResponseArray.addAll(response.body());
                    footerAdapter.clear();
                    if (response.body().size() > 0) {
                        housesFastAdapter.add(response.body());
                    } else {
                        Toast.makeText(Houses.this, "No more items", Toast.LENGTH_LONG).show();
                    }


                    Log.d("myLogsRequestUrlIL", response.raw().request().url().toString() + " table offset = " + tableOffset);
                    infiniteLoading = false;


                }

                Log.d("myLogsOnSuccess", "onResponse: response successful");


            }

            @Override
            public void onFailure(Call<ArrayList<HousesResponse>> call, Throwable t) {

                Log.d("myLogsOnFailure", "onResponse: response unsuccessful");

            }
        });

    }*/

/*************************************************************************************************************************************************/


/*

    void populateLocations() {

        //add an All locations option for the user to select all available locations
        locationOptions.add(getString(R.string.ALL));

        locationsMap.put("props", "Location");

        //initialize the spinner and assign it an adapter
        ArrayAdapter<String> locationSpinnerAdapter = new ArrayAdapter<>(Houses.this, android.R.layout.simple_spinner_item, locationOptions);
        //set the layout resource for the spinner adapter
        locationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        houseLocationSpinner.setAdapter(locationSpinnerAdapter);

        locationsCall.clone().enqueue(new Callback<ArrayList<HousesResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<HousesResponse>> call, Response<ArrayList<HousesResponse>> response) {

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
            public void onFailure(Call<ArrayList<HousesResponse>> call, Throwable t) {

                Toast.makeText(Houses.this, "No locations Available", Toast.LENGTH_LONG).show();

            }
        });


    }

*/
/*************************************************************************************************************************************************/



      /*  toggleExpandableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(housesExpandableLayout.isExpanded()){

                    housesExpandableLayout.collapse();

                }else {

                    housesExpandableLayout.expand();

                }

            }
        });



          <TextView
        android:id="@+id/toggle_filters_button"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:layout_marginBottom="8dp"
        android:gravity="center_horizontal"
        android:text="TOGGLE FILTERS"
        android:textSize="15sp"
        app:fontFamily="@font/montserrat_bold" />



*/


//todo there is no way for the user to refresh locations