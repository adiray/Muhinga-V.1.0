package com.example.dell.muhingalayoutprototypes;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.vince.easysave.EasySave;
import br.vince.easysave.SaveAsyncCallback;
import cn.aigestudio.datepicker.bizs.calendars.DPCManager;
import cn.aigestudio.datepicker.bizs.decors.DPDecor;
import cn.aigestudio.datepicker.views.DatePicker;

public class BookVenue extends AppCompatActivity {

    //views
    DatePicker picker;
    Button saveBookingButton;


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

    //date picker arrays and others
    List<String> bookedDays = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_venue);

        recieveIntents();

        initializeViews();

        retrieveUserId();

        retrieveBookings();

        //initializeCalender();


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


        picker = findViewById(R.id.datePicker);
        // show current month view
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        picker.setDate(year, month + 1);  // the month index starts from zero

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

            //loadingView.start();
            Integer startDateInt = startDateTime.getDayOfMonth();
            Integer endDateInt = endDateTime.getDayOfMonth(), monthInt = startDateTime.getMonthOfYear(), yearInt = startDateTime.getYear();
            Log.d("myLogsBookVenue", "startDate n endDate not null: " + startDateInt + " ," + endDateInt + " ," + yearInt + " ," + monthInt);

            //uploadBooking(startDateInt, endDateInt, monthInt, yearInt, approved);


        }


    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    void retrieveBookings() {


        // loadingView.start();

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        String whereClause = "venues[bookings]" +
                ".objectId='" + venueId + "'";
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of("bookings").find(queryBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> response) {

                if (!response.isEmpty()) {

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


                                stringBuilder.append(year);
                                stringBuilder.append("-");
                                stringBuilder.append(month);
                                stringBuilder.append("-");
                                stringBuilder.append(c);
                                fullDate = stringBuilder.toString();
                                Log.d("myLogsBookVenue", "full retrieved Date : " + fullDate);

                                bookedDays.add(fullDate);


                                stringBuilder.delete(0, stringBuilder.length());
                            }









                            //loadingView.stop();

                        }


                    }


                    Log.d("myLogsBookVenue", "Booked days array list : " + bookedDays.toString());
                    initializeCalender();



                }


            }

            @Override
            public void handleFault(BackendlessFault fault) {

                //loadingView.stop();

                Log.d("myLogsBookVenue", "failed to retrieve bookings. ERROR : " + fault.toString());


            }
        });

    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    void initializeCalender() {





        //set pre selected days (these are the booked days)

        List<String> tmp = new ArrayList<>();
        tmp.add("2019-7-1");
        tmp.add("2019-7-8");
        tmp.add("2019-7-16");

            DPCManager.getInstance().setDecorBG(bookedDays);

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
                StringBuilder result = new StringBuilder();
                Iterator iterator = date.iterator();
                while (iterator.hasNext()) {
                    result.append(iterator.next());
                    if (iterator.hasNext()) {
                        result.append("\n");
                    }
                }
                Toast.makeText(BookVenue.this, result.toString(), Toast.LENGTH_LONG).show();
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





