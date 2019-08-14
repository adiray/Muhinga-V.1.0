package com.example.dell.muhingalayoutprototypes;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.LoadRelationsQueryBuilder;
import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.FooterAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.listeners.EventHook;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavedItems extends AppCompatActivity {


    //other items
    Boolean onRefreshing = false, isHouses = false, isLand = false, isVenues = false;
    String deletableHouseId = "not found";
    List<Map<String, String>> deletableHousesList = new ArrayList<>();


    Integer deletedItemPosition;

    //backendless
    //LoadRelationsQueryBuilder<HousesResponse> savedHousesQueryBuilder;
    LoadRelationsQueryBuilder<Map<String, Object>> savedHousesQueryBuilder;
    Map<String, Object> savedHousesResponseMap = new HashMap<>();
    String globalCurrentUserJson, selectedCategory, currentUserId;
    BackendlessUser currentUser;

    //FAB
    FloatingActionButton loadMoreFAB;


    //toolBar
    Toolbar mainToolBar;


    //recycler view objects
    RecyclerView mainRecyclerView;
    SwipeRefreshLayout mainSwipeRefresh;

    //response objects
    ArrayList<SavedHousesResponse> savedHousesResponse = new ArrayList<>();

    //create our FastAdapter which will manage everything
    FastItemAdapter<SavedHousesResponse> mainFastAdapter;
    // FooterAdapter<ProgressItem> footerAdapter = new FooterAdapter<>();
    //  EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;


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


        //FAB
        loadMoreFAB = findViewById(R.id.saved_items_load_more_fab);


        //swipe to refresh
        mainSwipeRefresh = findViewById(R.id.saved_items_rec_view_swipe_refresh);


        //recycler view
        mainRecyclerView = findViewById(R.id.saved_items_activity_rec_view);
        mainRecyclerView.setHasFixedSize(true);
        mainRecyclerView.setLayoutManager(new GridLayoutManager(SavedItems.this, 1, 1, false));


        //initialize our FastAdapter which will manage everything
        mainFastAdapter = new FastItemAdapter<>();


        //initialize the endless scroll listener
     /*   endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(footerAdapter) {
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

*/


        //add on click listeners to the views
        loadMoreFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadMoreSavedHouses();


            }
        });


        //add on click listeners to the views
        mainFastAdapter.withEventHook(new ClickEventHook<SavedHousesResponse>() {


            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {

                if (viewHolder instanceof SavedHousesResponse.savedHousesViewHolder) {
                    return ((SavedHousesResponse.savedHousesViewHolder) viewHolder).deleteItem;
                }

                return super.onBind(viewHolder);
            }

            @Override
            public void onClick(View view, int i, FastAdapter<SavedHousesResponse> fastAdapter, SavedHousesResponse item) {


                Toast.makeText(SavedItems.this, "On click works: " + item.getLocation(), Toast.LENGTH_LONG).show();
                deletableHouseId = fastAdapter.getItem(i).getObjectId();
                deletedItemPosition = i;
                Log.d("myLogsDelHseId", deletableHouseId);
                deleteSavedHouse();


            }


        });


        //just add an `EventHook` to your `FastAdapter` by implementing either a `ClickEventHook`, `LongClickEventHook`, `TouchEventHook`, `CustomEventHook`
       /* mainFastAdapter.withEventHook(new ClickEventHook<SavedHousesResponse>() {

            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                //return the views on which you want to bind this event
                if (viewHolder instanceof SavedHousesResponse.ViewHolder) {
                    return ((RecyclerView.ViewHolder) viewHolder).view;
                }
                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<SampleItem> fastAdapter, SampleItem item) {
                //react on the click event
            }
        });*/


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
        // mainRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);


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

        savedHousesQueryBuilder = LoadRelationsQueryBuilder.ofMap();
        savedHousesQueryBuilder.setRelationName("saved_houses");
        savedHousesQueryBuilder.setPageSize(4).setOffset(0);


        Backendless.Data.of(BackendlessUser.class).loadRelations(currentUserId, savedHousesQueryBuilder, new AsyncCallback<List<Map<String, Object>>>() {
            @Override
            public void handleResponse(List<Map<String, Object>> response) {

                savedHousesResponse = createSavedHouseObjectArraylist(response);
                if (!savedHousesResponse.isEmpty()) {

                    mainFastAdapter.add(savedHousesResponse);
                    // mainRecyclerView.setAdapter(footerAdapter.wrap(mainFastAdapter));
                    mainRecyclerView.setAdapter(mainFastAdapter);


                } else {


                    Log.d("myLogsSvdHseReq", "saved house array is empty");


                }


                Log.d("myLogsSvdHseReq", response.toString());

            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Log.d("myLogsSvdHseReqFail", fault.toString());


            }
        });


    }


    ArrayList<SavedHousesResponse> createSavedHouseObjectArraylist(List<Map<String, Object>> response) {

        ArrayList<SavedHousesResponse> savedHouses = new ArrayList<>();


        int count = response.size() - 1;


        String mianImageReference, img2, img3, img4, img5, title, description, phone, price, viewingDates, objectId, location;
        Boolean forSale = false, rent = false;
        Map<String, Object> currentItem = new HashMap<>();

        SavedHousesResponse savedHousesResponse;


        for (int i = 0; i <= count; i++) {

            currentItem = response.get(i);

            mianImageReference = (String) currentItem.get("mian_image_reference");
            img2 = (String) currentItem.get("img2");
            img3 = (String) currentItem.get("img3");
            img4 = (String) currentItem.get("img4");
            img5 = (String) currentItem.get("img5");
            title = (String) currentItem.get("title");
            description = (String) currentItem.get("description");
            phone = (String) currentItem.get("phone");
            price = (String) currentItem.get("price");
            viewingDates = (String) currentItem.get("viewing_dates");
            objectId = (String) currentItem.get("objectId");
            location = (String) currentItem.get("Location");
            forSale = (Boolean) currentItem.get("for_sale");
            rent = (Boolean) currentItem.get("Rent");

            if (rent == null) {
                rent = false;
            } else if (null == forSale) {
                forSale = false;
            }


            savedHousesResponse = new SavedHousesResponse(mianImageReference, img2, img3, img4, img5, title, description, phone, price, viewingDates, objectId, location, forSale, rent);

            savedHouses.add(savedHousesResponse);


        }

        if (!savedHouses.isEmpty()) {

            Log.d("myLogsSvdHse", savedHouses.get(0).toString());


        }

        return savedHouses;


    }

    void requestSavedVenues() {
    }

    void requestSavedLand() {
    }


    void loadMoreSavedHouses() {

        savedHousesQueryBuilder.prepareNextPage();


        Backendless.Data.of(BackendlessUser.class).loadRelations(currentUserId, savedHousesQueryBuilder, new AsyncCallback<List<Map<String, Object>>>() {
            @Override
            public void handleResponse(List<Map<String, Object>> response) {


                //footerAdapter.clear();
                if (response.size() > 0) {

                    savedHousesResponse.clear();
                    savedHousesResponse.addAll(createSavedHouseObjectArraylist(response));
                    mainFastAdapter.add(savedHousesResponse);
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


        Backendless.Data.of(BackendlessUser.class).loadRelations(currentUserId, savedHousesQueryBuilder, new AsyncCallback<List<Map<String, Object>>>() {
            @Override
            public void handleResponse(List<Map<String, Object>> response) {

                //perform the sequence of actions for a refreshed load
                savedHousesResponse.clear();
                savedHousesResponse.addAll(createSavedHouseObjectArraylist(response));
                mainFastAdapter.clear();
                //  mainRecyclerView.clearOnScrollListeners();
                // mainRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
                mainFastAdapter.add(savedHousesResponse);
                // endlessRecyclerOnScrollListener.resetPageCount();


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


    void deleteSavedHouse() {

        Map<String, String> deletableHouse = new HashMap<>();
        deletableHouse.put("objectId", deletableHouseId);
        deletableHousesList.add(deletableHouse);



        /*Backendless.Persistence.of( "TABLE-NAME" ).deleteRelation(
                                             Map parentObject,
                                             String relationColumnName,
                                             Collection<Map> children,
                                             AsyncCallback<Integer> callback );
*/

        Backendless.Data.of(BackendlessUser.class).deleteRelation(currentUser, "saved_houses", deletableHousesList, new AsyncCallback<Integer>() {
            @Override
            public void handleResponse(Integer response) {

                Log.d("myLogsDelHse", "items Deleted: " + response);


                if (!(deletedItemPosition == null)) {

                    mainFastAdapter.remove(deletedItemPosition);

                }else{

                    refreshSavedHouses();
                    Log.d("myLogsDelHse", "deleted item position is null: refresh view ");


                }


                // refreshSavedHouses();


            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Log.d("myLogsDelHse", "saved house items deletion failed: " + fault.toString());

            }
        });


    }


}



/*08-12 19:50:07.114 7998-7998/com.example.dell.muhingalayoutprototypes D/myLogsSvdHseReq: [HousesResponse{owner = 'Admin',img_4 = 'null',img_5 = 'null',mian_image_reference = 'null',created = '1544717523264',description = 'amazing 10th position',ownerId = 'null',img_2 = 'null',title = '10th house',img_3 = 'null',land = 'null',price = '28899',___class = 'real_estate',for_sale = 'null',updated = '1565174982069',objectId = '3F367CEC-60B0-976F-FF6E-A17BA8BD0A00',location = 'null',rent = 'false'}, HousesResponse{owner = 'Admin',img_4 = 'null',img_5 = 'null',mian_image_reference = 'null',created = '1543418223135',description = '4 bedroom condo for rent in bujumbura. It has 2 floors, a bathroom, and a jacuzzi. The rent is worth it just for the view alone and the apartment is very pet friendly. All rent must be paid six months in advance. ',ownerId = 'null',img_2 = 'null',title = 'Single kitchen apartment',img_3 = 'null',land = 'null',price = '2400',___class = 'real_estate',for_sale = 'null',updated = '1565175004452',objectId = 'D80208BB-89A4-2093-FF91-18F056AB9500',location = 'null',rent = 'false'}, HousesResponse{owner = 'Admin',img_4 = 'null',img_5 = 'null',mian_image_reference = 'null',created = '1543419142496',description = 'very large house',ownerId = 'null',img_2 = 'null',title = '3 bedrooms 2 bathrooms',img_3 = 'null',land = 'null',price = '34566',___class = 'real_estate',for_sale = 'null',updated = '1565175007524',objectId = 'E937498F-00C4-A3EF-FF3D-B1D672D44700',location = 'null',rent = 'false'}]
 */



