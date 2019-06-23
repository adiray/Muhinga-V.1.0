package com.example.dell.muhingalayoutprototypes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.LoadRelationsQueryBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.desai.vatsal.mydynamiccalendar.MyDynamicCalendar;
import com.desai.vatsal.mydynamiccalendar.OnDateClickListener;
import com.github.loadingview.LoadingDialog;
import com.github.loadingview.LoadingView;
import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import br.vince.easysave.EasySave;
import br.vince.easysave.LoadAsyncCallback;
import br.vince.easysave.SaveAsyncCallback;

public class BookVenue extends AppCompatActivity {

    //views
    MyDynamicCalendar myCalendar;
    Button saveBookingButton;
    LoadingView loadingView;

    //others
    Date startDate, endDate;
    DateTime startDateTime, endDateTime;
    Boolean startDateClicked = false, endDateClicked = false, firstDateClicked = false, secondDateClicked = false, approved = false;
    Integer clickedMonth, clickedYear;


    //Venue info
    String venueId;

    //clicked date cache key
    String clickedStartDate = "clickedStartDate", clickedEndDate = "clickedEndDate";

    Date initialDate, currentDate;  //previously clicked date and the date the user has just clicked respectively
    DateTime initialDateTime, currentDateTime;


    //user details
    String cachedUserId, currentUserId;
    BackendlessUser currentUser;
    String currentUserJsonString;

    //miscellaneous
    Map classWideBookingObject = new HashMap();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_venue);

        recieveIntents();

        initializeViews();

        retrieveUserId();

        initializeCalender();

        retrieveBookings();


    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    void recieveIntents() {

        Intent intent = getIntent();
        venueId = intent.getStringExtra(VenuesDetails.EXTRA_VENUE_ID);
        currentUserJsonString = intent.getStringExtra(MainActivity.EXTRA_GLOBAL_USER);

    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    void initializeViews() {

        loadingView = findViewById(R.id.loadingView);

        saveBookingButton = findViewById(R.id.book_venue_activity_save_booking_button);
        //saveBookingButton.setEnabled(false);
        saveBookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveBookingButtonClicked();

            }
        });


    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    void saveBookingButtonClicked() {

        //   DateTime thisStartDateTime = new DateTime(startDate), thisEndDateTime = new DateTime(endDate);

        if (startDate != null && endDate != null) {

            loadingView.start();
            Integer startDateInt = startDateTime.getDayOfMonth();
            Integer endDateInt = endDateTime.getDayOfMonth(), monthInt = startDateTime.getMonthOfYear(), yearInt = startDateTime.getYear();
            Log.d("myLogsBookVenue", "startDate n endDate not null: " + startDateInt + " ," + endDateInt + " ," + yearInt + " ," + monthInt);

            uploadBooking(startDateInt, endDateInt, monthInt, yearInt, approved);


        }


    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    void retrieveBookings() {


        loadingView.start();

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        String whereClause = "venues[bookings]" +
                ".objectId='" + venueId + "'";
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of("bookings").find(queryBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> response) {

                if (response != null) {

                    int counter = response.size();
                    Integer startDate, endDate, year, month;
                    StringBuilder stringBuilder = new StringBuilder();
                    String fullDate;

                    for (int i = 0; i < counter; i++) {

                        startDate = (Integer) response.get(i).get("start_date");
                        endDate = (Integer) response.get(i).get("end_date");
                        year = (Integer) response.get(i).get("year");
                        month = (Integer) response.get(i).get("month");


                        if (startDate != null && endDate != null && year != null && month != null) {
                            for (int c = startDate; c <= endDate; c++) {

                                stringBuilder.append(c);
                                stringBuilder.append("-");
                                stringBuilder.append(month);
                                stringBuilder.append("-");
                                stringBuilder.append(year);
                                fullDate = stringBuilder.toString();
                                Log.d("myLogsBookVenue", "full retrieved Date : " + fullDate);


                                myCalendar.addHoliday(fullDate);
                                stringBuilder.delete(0, stringBuilder.length());
                            }

                            myCalendar.refreshCalendar();
                            loadingView.stop();

                        }


                    }

                }


            }

            @Override
            public void handleFault(BackendlessFault fault) {

                loadingView.stop();

            }
        });

    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    void initializeCalender() {

        myCalendar = findViewById(R.id.book_venue_activity_calender);

        // show month view
        myCalendar.showMonthView();

        //set holidays (these are the booked days)
        myCalendar.setHolidayCellBackgroundColor(R.color.md_green_200);
        myCalendar.setHolidayCellTextColor("#d590bb");
        // set holiday date clickable true/false
        myCalendar.setHolidayCellClickable(false);
        // Add holiday  -  addHoliday(holiday_date)
        myCalendar.addHoliday("2-11-2019");
        myCalendar.addHoliday("13-11-2019");
        myCalendar.addHoliday("18-10-2019");
        myCalendar.addHoliday("10-10-2019");

        // date click listener
        myCalendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onClick(Date date) {

                Log.d("myLogsBookVenue", "Date : " + date.toString());
                String selectedDate = parseDate(date);

                if (selectedDate != null) {

                  /*  myCalendar.addHoliday(selectedDate);
                    Log.d("myLogsBookVenue", "Parsed Date : " + selectedDate);
                    myCalendar.refreshCalendar();
*/

                    dateClicked(date);


                }


            }

            @Override
            public void onLongClick(Date date) {

            }
        });

    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    void dateClicked(Date date) {

        String dateString = parseDate(date);
        currentDateTime = new DateTime(date);

        if (!secondDateClicked) {

            Log.d("myLogsBookVenue", "secondDateClicked: " + secondDateClicked.toString());


            if (!firstDateClicked) {
                cacheTheClickedDate(parseDate(date), clickedStartDate);
                startDate = date;
                startDateTime = new DateTime(startDate);
                firstDateClicked = true;
                startDateClicked = true;
                myCalendar.addEvent(parseDate(date), "8:00", "0:00", "start");
                myCalendar.refreshCalendar();
                Log.d("myLogsBookVenue", "firstDateClicked: " + firstDateClicked.toString());

            } else {

                if (currentDateTime.isAfter(startDateTime)) {

                    cacheTheClickedDate(parseDate(date), clickedEndDate);
                    endDate = date;
                    endDateTime = new DateTime(date);
                    endDateClicked = true;
                    secondDateClicked = true;
                    Duration duration = new Duration(startDateTime, endDateTime);
                    Integer days = duration.toStandardDays().getDays();
                    String parsedDate;
                    Integer dayOfMonth;
                    DateTime durationStart = startDateTime;

                    for (int i = 0; i < days; i++) {

                        durationStart = durationStart.plusDays(1);
                        dayOfMonth = durationStart.getDayOfMonth();
                        parsedDate = dayOfMonth.toString() + "-" + durationStart.getMonthOfYear() + "-" + durationStart.getYear();
                        Log.d("myLogsBookVenue", "Parsed Date : " + parsedDate + " " + i);
                        myCalendar.addEvent(parsedDate, "8:00", "0:00", "Book");
                    }
                    myCalendar.refreshCalendar();
                    Log.d("myLogsBookVenue", "current date is after start date. secondDateClicked: " + secondDateClicked.toString() + days);


                } else if (currentDateTime.isEqual(startDateTime)) {

                    myCalendar.deleteAllEvent();
                    firstDateClicked = false;
                    startDateClicked = false;
                    endDateClicked = false;
                    secondDateClicked = false;
                    Log.d("myLogsBookVenue", "Current date is equal to start date. secondDateClicked: " + secondDateClicked.toString());


                } else if (currentDateTime.isBefore(startDateTime)) {

                    myCalendar.deleteAllEvent();
                    startDate = date;
                    startDateTime = new DateTime(startDate);
                    cacheTheClickedDate(parseDate(date), clickedStartDate);
                    secondDateClicked = false;
                    myCalendar.addEvent(parseDate(startDate), "8:00", "0:00", "Book");
                    myCalendar.refreshCalendar();
                    Log.d("myLogsBookVenue", "Current date is before start date. secondDateClicked: " + secondDateClicked.toString());


                }
            }

        } else {

            if (currentDateTime.isEqual(startDateTime)) {

                Log.d("myLogsBookVenue", "beyond second click. current = start date. secondDateClicked: " + secondDateClicked.toString());

                secondDateClicked = false;
                startDate = endDate;
                startDateTime = new DateTime(startDate);
                myCalendar.deleteAllEvent();
                myCalendar.addEvent(parseDate(endDate), "8:00", "0:00", "Book");
                myCalendar.refreshCalendar();
                cacheTheClickedDate(parseDate(endDate), clickedStartDate);

            } else if (currentDateTime.isEqual(endDateTime)) {

                Log.d("myLogsBookVenue", "beyond second click. current = end date. secondDateClicked: " + secondDateClicked.toString());

                myCalendar.deleteAllEvent();
                secondDateClicked = false;
                myCalendar.addEvent(parseDate(startDate), "8:00", "0:00", "Book");
                myCalendar.refreshCalendar();

            }

        }

    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    void cacheTheClickedDate(String date, String key) {


        new EasySave(BookVenue.this).saveModelAsync(key, date, new SaveAsyncCallback<String>() {
            @Override
            public void onComplete(String s) {

            }

            @Override
            public void onError(String s) {

            }
        });


    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    String parseDate(Date date) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        return sdf.format(date);

    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    void retrieveUserId() {


        Gson gson = new Gson();
        currentUser = gson.fromJson(currentUserJsonString, BackendlessUser.class);
        currentUserId = currentUser.getObjectId();

    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    void uploadBooking(Integer startDate, Integer endDate, Integer month, Integer year, Boolean approved) {


        Map<String, Object> booking = new HashMap<>();
        booking.put("end_date", endDate);
        booking.put("start_date", startDate);
        booking.put("year", year);
        booking.put("approved", approved);
        booking.put("month", month);


        Backendless.Data.of("bookings").save(booking, new AsyncCallback<Map>() {
            @Override
            public void handleResponse(Map response) {

                Map bookingObject = new HashMap<>();
                bookingObject = response;
                classWideBookingObject = response;
                //bookingObject.put("objectId", response.get("objectId"));

                Map<String, Object> userObject = new HashMap<String, Object>();
                userObject.put("objectId", currentUserId);

                ArrayList<Map> children = new ArrayList<Map>();
                children.add(userObject);

                Log.d("myLogsBookVenue", "new bboking uploaded: " + response.toString());


                Backendless.Data.of("bookings").addRelation(bookingObject, "user", children, new AsyncCallback<Integer>() {
                    @Override
                    public void handleResponse(Integer response) {

                        Log.d("myLogsBookVenue", "new booking 'user' relation uploaded: " + response.toString());


                        Map<String, Object> venueObject = new HashMap<String, Object>();
                        venueObject.put("objectId", venueId);

                        ArrayList<Map> venues = new ArrayList<Map>();
                        venues.add(venueObject);

                        Backendless.Data.of("bookings").addRelation(classWideBookingObject, "venue", venues, new AsyncCallback<Integer>() {
                            @Override
                            public void handleResponse(Integer response) {

                                Log.d("myLogsBookVenue", "new booking 'venue' relation uploaded: " + response.toString());

                                Map<String, Object> currentVenue = new HashMap<String, Object>();
                                currentVenue.put("objectId", venueId);

                                Map<String, Object> bookingChild = new HashMap<String, Object>();
                                bookingChild.put("objectId", classWideBookingObject.get("objectId"));


                                ArrayList<Map> bookingChildren = new ArrayList<Map>();
                                bookingChildren.add(bookingChild);


                                Backendless.Data.of("venues").addRelation(currentVenue, "bookings", bookingChildren, new AsyncCallback<Integer>() {
                                    @Override
                                    public void handleResponse(Integer response) {

                                        Log.d("myLogsBookVenue", "this venue 'bookings' relation uploaded: " + response.toString());



                                        myCalendar.refreshCalendar();

                                        loadingView.stop();

                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {

                                        Log.d("myLogsBookVenue", "this venue 'bookings' relation upload failed: " + fault.toString());
                                        loadingView.stop();

                                    }
                                });


                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {

                                Log.d("myLogsBookVenue", "new booking 'venue' relation upload failed: " + fault.toString());
                                loadingView.stop();


                            }
                        });


                        //todo this is wea u stopped  upload booking to venue table relation
                        //todo send the 1:N relation from the venues table to booking table


                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.d("myLogsBookVenue", "new boking 'user' upload failed: " + fault.toString());
                        loadingView.stop();


                    }
                });


            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Log.d("myLogsBookVenue", "new booking upload failed: " + fault.toString());
                loadingView.stop();



            }
        });




/*HashMap<String, Object> parentObject = new HashMap<String, Object>();
parentObject.put( "objectId", "41230622-DC4D-204F-FF5A-F893A0324800" );

HashMap<String, Object> childObject = new HashMap<String, Object>();
childObject.put( "objectId", "3464C734-F5B8-09F1-FFD3-18647D12E700" );

ArrayList<Map> children = new ArrayList<Map>();
children.add( childObject );

Backendless.Data.of( "Person" ).addRelation( parentObject, "address:Address:n", children,
                                             new AsyncCallback<Integer>()
{
  @Override
  public void handleResponse( Integer response )
  {
    Log.i( "MYAPP", "related object has been added" );
  }

  @Override
  public void handleFault( BackendlessFault fault )
  {
    Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
  }
} );
*/


    }


}






/*
 * todo change bg color of booked dates
 *
 *
 * */










/*
 *  /*  myCalendar.setCalendarBackgroundColor("#eeeeee");
        myCalendar.setHeaderBackgroundColor(R.color.my_color_primary);
        myCalendar.setHeaderTextColor("#ffffff");
        myCalendar.setNextPreviousIndicatorColor(R.color.my_color_primary);
        myCalendar.setWeekDayLayoutBackgroundColor(R.color.md_blue_grey_200);
        myCalendar.setWeekDayLayoutTextColor("#246245");
        myCalendar.setExtraDatesOfMonthBackgroundColor(R.color.md_light_green_A100);
        myCalendar.setExtraDatesOfMonthTextColor("#756325");
        myCalendar.setDatesOfMonthBackgroundColor(R.color.md_blue_grey_200);
        myCalendar.setDatesOfMonthTextColor("#745632");
        myCalendar.setCurrentDateBackgroundColor(R.color.black);
        myCalendar.setCurrentDateTextColor("#00e600");
        myCalendar.setBelowMonthEventTextColor("#425684");
        myCalendar.setBelowMonthEventDividerColor("#635478");

        // set all saturday off(Holiday) - default value is false
        // isSaturdayOff(true/false, date_background_color, date_text_color);
        myCalendar.isSaturdayOff(true, "#ffffff", "#ff0000");

        // set all sunday off(Holiday) - default value is false
        // isSundayOff(true/false, date_background_color, date_text_color);
        myCalendar.isSundayOff(true, "#ffffff", "#ff0000");



        //set events (the dates the user has selected)
        myCalendar.setEventCellBackgroundColor(R.color.md_yellow_400);
        myCalendar.setEventCellTextColor("#425684");
        // Add event  -  addEvent(event_date, event_start_time, event_end_time, event_title)
        myCalendar.addEvent("06-10-2019", "8:00", "8:15", "Today Event 1");
        myCalendar.addEvent("05-10-2019", "8:15", "8:30", "Today Event 2");
        myCalendar.addEvent("15-10-2019", "8:30", "8:45", "Today Event 3");



        //set holidays (these are the booked days)
        myCalendar.setHolidayCellBackgroundColor(R.color.md_green_200);
        myCalendar.setHolidayCellTextColor("#d590bb");
        // set holiday date clickable true/false
        myCalendar.setHolidayCellClickable(false);
        // Add holiday  -  addHoliday(holiday_date)
        myCalendar.addHoliday("2-11-2019");
        myCalendar.addHoliday("13-11-2019");
        myCalendar.addHoliday("18-10-2019");
        myCalendar.addHoliday("10-10-2019");


        *****************************************************************************************************
        *
    Integer findTheDaysDifference(Date startDate, Date endDate) {

        Long diff = endDate.getTime() - startDate.getTime();

        Long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        return days.intValue();
    }
        *******************************************************************************************************













*/


// Date : Wed May 08 23:11:34 GMT+08:00 2019







/*void dateClicked(Date date) {

        String dateString = parseDate(date);
        int comparisonInt = 0;
        currentDate = date;


        if (initialDate != null) {
            comparisonInt = initialDate.compareTo(currentDate);
            //current is before initial pos, current after initial is negative
        }

                initialDate = currentDate;


                if (!startDateClicked) {
                cacheTheClickedDate(dateString, clickedStartDate);
                startDate = date;
                startDateClicked = true;
                myCalendar.addEvent(dateString, "8:00", "0:00", "start");
                myCalendar.refreshCalendar();
                } else {

                if (startDate != null) {

                if (comparisonInt < 0) {
//the selected date is after the start date

        cacheTheClickedDate(dateString, clickedEndDate);
        endDate = date;
        endDateClicked = true;
        StringBuilder stringBuilder = new StringBuilder();

        Integer daysDifference = findTheDaysDifference(startDate,endDate);

        for (int i= 0; i == daysDifference; i++){


                        todo hrer u stopped
                       stringBuilder.append(c);
                        stringBuilder.append("-");
                        stringBuilder.append(month);
                        stringBuilder.append("-");
                        stringBuilder.append(year);
                        fullDate = stringBuilder.toString();
                        Log.d("myLogsBookVenue", "full retrieved Date : " + fullDate);

        myCalendar.addEvent(dateString, "8:00", "0:00", "end");
        myCalendar.refreshCalendar();

        }



        } else if (comparisonInt > 0) {

        cacheTheClickedDate(dateString, clickedStartDate);
        //Integer dateDifference = findTheDaysDifference(startDate,endDate);

        startDateClicked = true;
        endDateClicked = false;
        myCalendar.deleteAllEvent();
        myCalendar.addEvent(dateString, "8:00", "0:00", "start");
        myCalendar.refreshCalendar();

        }


        }


        }*/


/*************************************************************************************************/
/*
        if (!startDateClicked) {

            initialDate = date;
            currentDate = date;
            startDate = date;
            startDateTime = new DateTime(date);
            startDateClicked = true;
            cacheTheClickedDate(dateString, clickedStartDate);
            myCalendar.addEvent(dateString, "8:00", "0:00", "start");
            myCalendar.refreshCalendar();

        } else {

            initialDate = currentDate;
            currentDate = date;
            currentDateTime = new DateTime(date);

             //todo this is where you stopped. The user presses a third button
            if (currentDate.equals(initialDate)) {
                if (currentDate.equals(startDate)) {
                    myCalendar.deleteAllEvent();
                    myCalendar.addEvent(parseDate(endDate), "8:00", "0:00", "Book");
                    myCalendar.refreshCalendar();
                } else if (currentDate.equals(endDate)) {
                    myCalendar.deleteAllEvent();
                    endDateClicked = false;
                    myCalendar.addEvent(parseDate(startDate), "8:00", "0:00", "Book");
                    myCalendar.refreshCalendar();
                }


            } else {


                if (currentDateTime.isAfter(startDateTime)) {

                    endDate = date;
                    endDateTime = new DateTime(date);
                    cacheTheClickedDate(dateString, clickedEndDate);

                    Duration duration = new Duration(startDateTime, endDateTime);
                    Integer days = duration.toStandardDays().getDays();
                    String parsedDate;
                    Integer dayOfMonth;
                    DateTime durationStart = startDateTime;

                    for (int i = 0; i == days; i++) {

                        durationStart.plusDays(i);

                        dayOfMonth = durationStart.getDayOfMonth();

                        parsedDate = dayOfMonth.toString() + "-" + durationStart.getMonthOfYear() + "-" + durationStart.getYear();

                        myCalendar.addEvent(parsedDate, "8:00", "0:00", "Book");

                    }

                    myCalendar.refreshCalendar();


                } else if (currentDateTime.isBefore(startDateTime)) {


                    startDate = date;
                    startDateTime = new DateTime(date);
                    cacheTheClickedDate(dateString, clickedStartDate);
                    myCalendar.deleteAllEvent();
                    myCalendar.addEvent(dateString, "8:00", "0:00", "start");
                    myCalendar.refreshCalendar();

                }


            }


        }

*/





