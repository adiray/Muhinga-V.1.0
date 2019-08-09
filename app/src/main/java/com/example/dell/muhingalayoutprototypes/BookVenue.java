package com.example.dell.muhingalayoutprototypes;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener;
import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import br.vince.easysave.EasySave;
import br.vince.easysave.SaveAsyncCallback;
import cn.aigestudio.datepicker.bizs.calendars.DPCManager;
import cn.aigestudio.datepicker.bizs.decors.DPDecor;
import cn.aigestudio.datepicker.views.DatePicker;

public class BookVenue extends AppCompatActivity {

    //views
    DatePicker picker;
    ProgressBar progressBar;
    Toolbar bookVenueToolbar;


    //strings and dates and ints for the upload
    Date startDate, endDate;
    String firstDateString, lastDateString;
    Integer firstDateInt, lastDateInt, selectedYear, selectedMonth;
    DateTime startDateTime, endDateTime;


    //others
    // Date startDate, endDate;
    //Boolean startDateClicked = false, endDateClicked = false, firstDateClicked = false, secondDateClicked = false, approved = false;
    //Integer clickedMonth, clickedYear;


    //Venue info
    String venueId;

    //clicked date cache key
    String clickedStartDate = "clickedStartDate", clickedEndDate = "clickedEndDate";

    //Date initialDate, currentDate;  //previously clicked date and the date the user has just clicked respectively
    // DateTime initialDateTime, currentDateTime;


    //user details
    String cachedUserId, currentUserId;
    BackendlessUser currentUser;
    String currentUserJsonString;

    //miscellaneous
    Map classWideBookingObject = new HashMap();

    //date picker arrays and others
    ArrayList<String> bookedDays = new ArrayList<>();
    String userSelectedDays = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_venue);

        recieveIntents();

        initializeViews();

        retrieveUserId();

        initializeCalender();


    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    void recieveIntents() {

        Intent intent = getIntent();
        venueId = intent.getStringExtra(VenuesDetails.EXTRA_VENUE_ID);
        currentUserJsonString = intent.getStringExtra(MainActivity.EXTRA_GLOBAL_USER);
        bookedDays = intent.getStringArrayListExtra(VenuesDetails.EXTRA_VENUE_BOOKINGS);

    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    void initializeViews() {


        progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);


        //TOOLBAR
        bookVenueToolbar = findViewById(R.id.book_venue_activity_action_bar);
        //tool bar

        bookVenueToolbar.setTitle("Book Venue");
        Objects.requireNonNull(bookVenueToolbar.getOverflowIcon()).setColorFilter(getResources().getColor(R.color.my_color_white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(bookVenueToolbar);


    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */

    void initializeCalender() {

        picker = findViewById(R.id.datePicker);
        // show current month view
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        picker.setDate(year, month + 1);  // the month index starts from zero


        //set pre selected days (these are the booked days)

        List<String> tmp = new ArrayList<>(); //this arraylist seems redundant but is important to make sure the booked dates display
        tmp = bookedDays;
        tmp.add("2019-7-1");


        DPCManager.getInstance().setDecorBG(tmp);

        picker.setDPDecor(new DPDecor() {
            @Override
            public void drawDecorBG(Canvas canvas, Rect rect, Paint paint) {
                paint.setColor(Color.RED);
                canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2F, paint);
            }
        });


        picker.setOnDateSelectedListener(new DatePicker.OnDateSelectedListener() {
            @Override
            public void onDateSelected(List<String> date) {

                if (!date.isEmpty()) {
                    StringBuilder result = new StringBuilder();
                    Iterator iterator = date.iterator();
                    while (iterator.hasNext()) {
                        result.append(iterator.next());
                        if (iterator.hasNext()) {
                            result.append("\n");
                        }
                    }

                    userSelectedDays = result.toString();


                    //preparing the booking data for upload
                    firstDateString = date.get(0);
                    lastDateString = date.get(date.size() - 1);

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                    try {
                        startDate = formatter.parse(firstDateString);
                    } catch (ParseException e) {

                        Log.d("myLogsBookVenue", "failed to parse  start date: " + e.getMessage());

                        e.printStackTrace();
                    }

                    try {
                        endDate = formatter.parse(lastDateString);
                    } catch (ParseException e) {

                        Log.d("myLogsBookVenue", "failed to parse end date: " + e.getMessage());

                        e.printStackTrace();
                    }


                    if (startDate != null && endDate != null) {

                        startDateTime = new DateTime(startDate);
                        endDateTime = new DateTime(endDate);

                        firstDateInt = startDateTime.getDayOfMonth();
                        lastDateInt = endDateTime.getDayOfMonth();
                        selectedMonth = startDateTime.getMonthOfYear();
                        selectedYear = startDateTime.getYear();


                    }
                }


                displayConfirmationDialog();
                // Toast.makeText(BookVenue.this, result.toString(), Toast.LENGTH_LONG).show();
            }
        });


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


    void displayConfirmationDialog() {


        new TTFancyGifDialog.Builder(BookVenue.this)
                .setTitle("Confirm Dates")
                .setMessage("Confirm the booking dates?")
                .setPositiveBtnText("CONFIRM")
                .setPositiveBtnBackground("#22b573")
                .setNegativeBtnText("CANCEL")
                .setNegativeBtnBackground("#FF3D00")
                .setGifResource(R.drawable.sign_in_opt_one)      //pass your gif, png or jpg
                .isCancellable(false)
                .OnPositiveClicked(new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {

                        progressBar.setVisibility(View.VISIBLE);
                        uploadBooking(firstDateInt, lastDateInt, selectedMonth, selectedYear, false);

                    }
                })
                .OnNegativeClicked(new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {

                    }
                })
                .build();


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
                Log.d("myLogsBookVenue", "new booking uploaded: " + response.toString());
                //adding a user relation to the bookings table
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
                                        //myCalendar.refreshCalendar();
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(BookVenue.this, "Booking successful!", Toast.LENGTH_LONG).show();

                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Log.d("myLogsBookVenue", "this venue 'bookings' relation upload failed: " + fault.toString());
                                        progressBar.setVisibility(View.GONE);

                                    }
                                });
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Log.d("myLogsBookVenue", "new booking 'venue' relation upload failed: " + fault.toString());
                                progressBar.setVisibility(View.GONE);

                            }
                        });
                        //todo this is wea u stopped  upload booking to venue table relation
                        //todo send the 1:N relation from the venues table to booking table
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.d("myLogsBookVenue", "new boking 'user' upload failed: " + fault.toString());
                        progressBar.setVisibility(View.GONE);

                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.d("myLogsBookVenue", "new booking upload failed: " + fault.toString() + fault.getMessage());
                progressBar.setVisibility(View.GONE);

            }
        });

    }
    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */




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





