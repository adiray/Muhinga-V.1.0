package com.example.dell.muhingalayoutprototypes;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class AppNotifications extends Application {

    //create the channel IDs
    public static final String MUSIC_DL_CHANNEL_ID = "channel1"; //Channel for music downloads

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();

    }

    private void createNotificationChannels(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel musicDlChannel = new NotificationChannel(
                    MUSIC_DL_CHANNEL_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );

            musicDlChannel.setDescription("These notification show the progress of sog downloads");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(musicDlChannel);






        }


    }


}
