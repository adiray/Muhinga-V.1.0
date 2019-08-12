package com.example.dell.muhingalayoutprototypes;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.LoadRelationsQueryBuilder;
import com.google.gson.Gson;
import com.mikepenz.fastadapter.adapters.FooterAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;

import java.util.ArrayList;
import java.util.List;

public class SavedItems extends AppCompatActivity {


    //other items
    Boolean onRefreshing = false, isHouses = false, isLand = false, isVenues = false;


    //backendless
    LoadRelationsQueryBuilder<HousesResponse> savedHousesQueryBuilder;


    String globalCurrentUserJson, selectedCategory, currentUserId;
    BackendlessUser currentUser;
    ArrayList<HousesResponse> savedHouses = new ArrayList<>();

    //toolBar
    Toolbar mainToolBar;


    //recycler view objects
    RecyclerView mainRecyclerView;
    SwipeRefreshLayout mainSwipeRefresh;

    //response objects
    ArrayList<HousesResponse> savedHousesResponse = new ArrayList<>();

    //create our FastAdapter which will manage everything
    FastItemAdapter<HousesResponse> mainFastAdapter;
    FooterAdapter<ProgressItem> footerAdapter = new FooterAdapter<>();
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_items);


        receiveIntents();
        initializeViews();
        makeinitialRequest();


    }


    void initializeViews() {


        //initialize the toolbar
        mainToolBar = findViewById(R.id.saved_items_action_bar);
        mainToolBar.setTitle(R.string.saved_items);
        setSupportActionBar(mainToolBar);


        //swipe to refresh
        mainSwipeRefresh = findViewById(R.id.saved_items_rec_view_swipe_refresh);


        //recycler view
        mainRecyclerView = findViewById(R.id.saved_items_activity_rec_view);
        mainRecyclerView.setHasFixedSize(true);
        mainRecyclerView.setLayoutManager(new GridLayoutManager(SavedItems.this, 1, 1, false));


        //initialize our FastAdapter which will manage everything
        mainFastAdapter = new FastItemAdapter<>();


        //initialize the endless scroll listener
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(footerAdapter) {
            @Override
            public void onLoadMore(int currentPage) {

                footerAdapter.clear();
                footerAdapter.add(new ProgressItem().withEnabled(false));

                //check which item is being loaded
                if (isHouses) {

                    loadMoreSavedHouses();

                } else if (isVenues) {


                } else if (isLand) {
                }


            }
        };


        //set the on refresh listener to the swipe to refresh view
        mainSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Log.d("myLogsrefreshingvalue", onRefreshing.toString());

                //check which item is being refreshed
                if (isHouses) {

                    refreshSavedHouses();

                } else if (isVenues) {


                } else if (isLand) {
                }


            }


        });


        //set the infinite/endless load on scroll listener to the recycler view
        mainRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);


    }


    void receiveIntents() {


        //receive intents
        Intent intent = getIntent();
        globalCurrentUserJson = intent.getStringExtra("globalCurrentUser");
        selectedCategory = intent.getStringExtra("clicked_category");
        Gson gson = new Gson();
        currentUser = gson.fromJson(globalCurrentUserJson, BackendlessUser.class);
        currentUserId = currentUser.getObjectId();


        switch (selectedCategory) {
            case "houses":

                isHouses = true;


                break;
            case "venues":

                isVenues = true;


                break;
            case "land":


                isLand = true;


                break;
        }


    }


    void makeinitialRequest() {

        if (isHouses) {


            requestSavedHouses();


        } else if (isVenues) {


            requestSavedVenues();


        } else if (isLand) {


            requestSavedLand();


        }


    }


    void requestSavedHouses() {


        //LoadRelationsQueryBuilder<HousesResponse> savedHousesQueryBuilder;
        savedHousesQueryBuilder = LoadRelationsQueryBuilder.of(HousesResponse.class);
        savedHousesQueryBuilder.setRelationName("saved_houses");
        savedHousesQueryBuilder.setPageSize(4).setOffset(0);


        Backendless.Data.of(BackendlessUser.class).loadRelations(currentUserId, savedHousesQueryBuilder, new AsyncCallback<List<HousesResponse>>() {

            @Override
            public void handleResponse(List<HousesResponse> response) {

                /*java.lang.NullPointerException: Attempt to invoke virtual method 'boolean java.util.ArrayList.addAll(java.util.Collection)' on a null object reference
        at com.example.dell.muhingalayoutprototypes.SavedItems$3.handleResponse(SavedItems.java:230)
        at com.example.dell.muhingalayoutprototypes.SavedItems$3.handleResponse(SavedItems.java:225)*/


                //todo try using a java map , print it out and then show results


                if (!response.isEmpty() && !(savedHousesResponse ==null)) {
                    savedHousesResponse.addAll(response);
                    mainFastAdapter.add(savedHousesResponse);
                    mainRecyclerView.setAdapter(footerAdapter.wrap(mainFastAdapter));

                    Log.d("myLogsSvdHseReq", response.toString());
                }
                else {

                    Log.d("myLogsSvdHseReq", "response is empty");

                }


            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Log.d("myLogsSvdHseReqFail", fault.toString());


            }
        });


    }

    void requestSavedVenues() {
    }

    void requestSavedLand() {
    }


    void loadMoreSavedHouses() {

        savedHousesQueryBuilder.prepareNextPage();
        Backendless.Data.of(BackendlessUser.class).loadRelations(currentUserId, savedHousesQueryBuilder, new AsyncCallback<List<HousesResponse>>() {

            @Override
            public void handleResponse(List<HousesResponse> response) {


                savedHousesResponse.addAll(response);
                footerAdapter.clear();
                if (response.size() > 0) {
                    mainFastAdapter.add(response);
                } else {
                    Toast.makeText(SavedItems.this, "No more items", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Log.d("myLogsSvdHseLdMrReqFail", fault.toString());


            }
        });


    }


    void refreshSavedHouses() {


        savedHousesQueryBuilder.setPageSize(4).setOffset(0);


        Backendless.Data.of(BackendlessUser.class).loadRelations(currentUserId, savedHousesQueryBuilder, new AsyncCallback<List<HousesResponse>>() {

            @Override
            public void handleResponse(List<HousesResponse> response) {

                //perform the sequence of actions for a refreshed load
                savedHousesResponse.clear();
                savedHousesResponse.addAll(response);
                mainFastAdapter.clear();
                mainRecyclerView.clearOnScrollListeners();
                mainRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
                mainFastAdapter.add(response);
                endlessRecyclerOnScrollListener.resetPageCount();


                Log.d("myLogsSvdHseReqOR", response.toString());


                //stop the refreshing animation
                mainSwipeRefresh.setRefreshing(false);


            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Log.d("myLogsSvdHseReqORFail", fault.toString());

            }
        });


    }


}



/*08-12 19:50:07.114 7998-7998/com.example.dell.muhingalayoutprototypes D/myLogsSvdHseReq: [HousesResponse{owner = 'Admin',img_4 = 'null',img_5 = 'null',mian_image_reference = 'null',created = '1544717523264',description = 'amazing 10th position',ownerId = 'null',img_2 = 'null',title = '10th house',img_3 = 'null',land = 'null',price = '28899',___class = 'real_estate',for_sale = 'null',updated = '1565174982069',objectId = '3F367CEC-60B0-976F-FF6E-A17BA8BD0A00',location = 'null',rent = 'false'}, HousesResponse{owner = 'Admin',img_4 = 'null',img_5 = 'null',mian_image_reference = 'null',created = '1543418223135',description = '4 bedroom condo for rent in bujumbura. It has 2 floors, a bathroom, and a jacuzzi. The rent is worth it just for the view alone and the apartment is very pet friendly. All rent must be paid six months in advance. ',ownerId = 'null',img_2 = 'null',title = 'Single kitchen apartment',img_3 = 'null',land = 'null',price = '2400',___class = 'real_estate',for_sale = 'null',updated = '1565175004452',objectId = 'D80208BB-89A4-2093-FF91-18F056AB9500',location = 'null',rent = 'false'}, HousesResponse{owner = 'Admin',img_4 = 'null',img_5 = 'null',mian_image_reference = 'null',created = '1543419142496',description = 'very large house',ownerId = 'null',img_2 = 'null',title = '3 bedrooms 2 bathrooms',img_3 = 'null',land = 'null',price = '34566',___class = 'real_estate',for_sale = 'null',updated = '1565175007524',objectId = 'E937498F-00C4-A3EF-FF3D-B1D672D44700',location = 'null',rent = 'false'}]
 */