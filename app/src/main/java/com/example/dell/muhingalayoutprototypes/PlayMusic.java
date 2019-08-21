package com.example.dell.muhingalayoutprototypes;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener;
import com.bumptech.glide.Glide;
import com.theanilpaudel.rotatinganimation.Rotation;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;
import com.tonyodev.fetch2core.Func;
import com.varunest.sparkbutton.SparkButton;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.dell.muhingalayoutprototypes.AppNotifications.MUSIC_DL_CHANNEL_ID;


public class PlayMusic extends AppCompatActivity {


    //miscellaneous objects
    String songTitle, artistName, songFileReference, albumName, coverImage;
    Boolean isPauseClicked = false;


    //notification objects
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mNotificationBuilder;
    int downloadProgressNotificationId = 1;
    //String channelId1 = "1", channelName1= "channel1";
    String downloadTitle;


    //app bar objects
    Toolbar playMusicMainToolBar;


  /*  //notification objects
    private NotificationManagerCompat notificationManager;
    String downloadTitle;
    NotificationReceiver musicBroadcastReceiver;
*/


    //timer and seek bar objects
    Integer newTime;  //this is the time to which the user seeks the song
    Boolean isSeekUsedWhilePaused = false;  //if the song is paused while the user seeks then this boolean is set to true otherwise false
    final Handler updateTimerHandler = new Handler(); //this handler is used to update the time and the seek bar
    final Runnable updateTimerRunnable = new Runnable() { //this runnable is used to update the timer and tha seek bar. it is passed into the updateTimerHandle
        @Override
        public void run() {

            if (!isStopped) {
                songSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                getElapsedTime();
                updateTimerHandler.postDelayed(this, 1000);
            }


        }
    };


    //initialize the views
    TextView songTitleTv, artistNameTv, elapsedTimeTv, songDurationTv;
    ImageButton playButton, downloadButton, pauseButton, stopButton, replayButton;
    CircleImageView coverImageImgView;
    //ImageView coverImageImgView;
    SeekBar songSeekBar;
    Rotation rotation;
    SparkButton likeButton;

    //Media player objects
    MediaPlayer mediaPlayer = null;
    Integer songPausePosition;
    Boolean isPaused = false, isStopped = false, isPlayPressed = false;
    Uri songURI;
    Boolean isOnCompletionListenerAttached = false;


    //Fetch downloader objects
    private Fetch fetch;
    Integer requestId;
    FetchConfiguration fetchConfiguration;
    String url, fileName, fileType, downloadLocation;
    FetchListener fetchListener;
    Boolean isCurrentlyDownloading = false;  //used to determine whether the download button should be displayed or the cancel download button should be displayed
    File songFile;
    //file downloading objects

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);


        //Get the intent that started this activity
        Intent intent = getIntent();
        artistName = intent.getStringExtra(Album.EXTRA_PLAY_SONG_ARTIST_NAME);
        songTitle = intent.getStringExtra(Album.EXTRA_PLAY_SONG_TITLE);
        songFileReference = intent.getStringExtra(Album.EXTRA_PLAY_SONG_FILE_REFERENCE);
        albumName = intent.getStringExtra(Album.EXTRA_PLAY_SONG_ALBUM_NAME);
        coverImage = intent.getStringExtra(Album.EXTRA_PLAY_SONG_COVER_IMAGE);


        //initialize the views
        songTitleTv = findViewById(R.id.play_music_song_title);
        artistNameTv = findViewById(R.id.play_music_artist_name);
        playButton = findViewById(R.id.play_music_play_button);
        pauseButton = findViewById(R.id.play_music_pause_button);
        stopButton = findViewById(R.id.play_music_stop_button);
        downloadButton = findViewById(R.id.play_music_download_button);
        coverImageImgView = findViewById(R.id.play_music_song_cover_image);
        songSeekBar = findViewById(R.id.song_seek_bar);
        elapsedTimeTv = findViewById(R.id.song_elapsed_duration);
        songDurationTv = findViewById(R.id.song_total_duration);
        replayButton = findViewById(R.id.play_music_replay_button);
        likeButton = findViewById(R.id.play_music_like_button);


        //toolbar views
        playMusicMainToolBar = findViewById(R.id.play_music_app_bar);
        playMusicMainToolBar.setTitle("Play Music");
        Objects.requireNonNull(playMusicMainToolBar.getOverflowIcon()).setColorFilter(getResources().getColor(R.color.my_color_white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(playMusicMainToolBar);


        //assign the text to the textViews
        songTitleTv.setText(songTitle);
        artistNameTv.setText(artistName);

        //initialize the notification manager compat
        //notificationManager = NotificationManagerCompat.from(this);

        //assign the initial progress
        songSeekBar.setProgress(0);

        //assign the tittle to the download
        downloadTitle = songTitle;

        implementSeekFunctionality();


        //set the cover image
        //todo set the image to dontanimate()
        Glide.with(this).load(coverImage).into(coverImageImgView);

        //set the file type and file name. //set here because it is used later on in the download function. It can be set anywhere just before it is used.
        fileType = songFileReference.substring(songFileReference.length() - 4);


        createSongFile();  //assign the uri to the songURI value so we can determine if the song exists on the device


        //set onClickListeners to the control buttons
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  Boolean songFileExists = false;


                //  songFile = new File(songURI.getPath());
/*
                 if (songFile.exists()){
                     songFileExists =true;

                 }
*/


                if (songFile.exists() && !isCurrentlyDownloading) {

                    playSongFromStorage();
                    //   playerCleanup();

                } else {

                    playSongFromServer();
                    // playerCleanup();

                }

                if (!isOnCompletionListenerAttached) {
                    playerCleanup();
                }


            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseSong();
            }
        });


        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSong();
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (songFile.exists() && !isCurrentlyDownloading) {

                    Toast.makeText(PlayMusic.this, songFile.getName() + " " + "has already been downloaded!", Toast.LENGTH_LONG).show();

                } else {

                    if (isCurrentlyDownloading) {

                        cancelDownload();

                    } else {

                        downloadSong();
                    }


                }


            }
        });

        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                replaySong();
            }
        });


        getWritePermission();
        buildNotifications();


    }


    //handle on back pressed


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopButton.callOnClick();


    }

    //inflate the menu layout file for the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.play_music_app_bar_menu, menu);
        return true;
    }





    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */
    public void createSongFile() {

        if (ExternalStorageUtil.isExternalStorageMounted()) {  //CHECK IF EXTERNAL STORAGE IS AVAILABLE

            downloadLocation = ExternalStorageUtil.getPublicExternalStorageBaseDir(Environment.DIRECTORY_MUSIC); //GET THE FILE PATH TO THE DOWNLOAD DIRECTORY

            songFile = new File(downloadLocation, songTitle + fileType); //GET THE FILE NAME AND EXTENSION AND USE IT TO CREATE THE FILE OBJECT

        }


    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    public void buildNotifications() {

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(PlayMusic.this, MUSIC_DL_CHANNEL_ID);
        mNotificationBuilder.setContentTitle(downloadTitle)
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.save_green_filled);


    }




  /*  public void buildNotifications(){


        Notification notification = new NotificationCompat.Builder(this, MUSIC_DL_CHANNEL_ID)
                .setSmallIcon(R.drawable.save_green_filled)
                .setContentTitle(downloadTitle)
                .setContentText("Download in Progress")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .build();



    }*/


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */
    public void playSongFromStorage() {


        if (!isPlayPressed) {
            if (!isPaused) {

                songURI = Uri.fromFile(songFile);
                mediaPlayer = new android.media.MediaPlayer();
                isPlayPressed = true;
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {

                    mediaPlayer.setDataSource(this, songURI);

                } catch (IllegalArgumentException | SecurityException | IllegalStateException e) {

                    android.widget.Toast.makeText(getApplicationContext(), "Sorry! This song cannot be played.!", android.widget.Toast.LENGTH_LONG).show();

                } catch (java.io.IOException e) {

                    e.printStackTrace();

                }

                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(final MediaPlayer mediaPlayer) {

                        mediaPlayer.start();
                        isPaused = false;
                        isPauseClicked = false;
                        isStopped = false;
                        playButton.setImageResource(R.drawable.play_blue_active);
                        stopButton.setImageResource(R.drawable.stop_red_inactive);
                        pauseButton.setImageResource(R.drawable.pause_blue_inactive);

                        if (mediaPlayer.isPlaying()) {

                            Integer songDuration;
                            songDuration = mediaPlayer.getDuration();
                            songSeekBar.setMax(songDuration);
                            getTotalTime();

                            updateTimerHandler.postDelayed(updateTimerRunnable, 1000);


                        }


                    }
                });
            } else {

                if (isSeekUsedWhilePaused) {

                    mediaPlayer.seekTo(newTime);
                    mediaPlayer.start();
                    isPaused = false;
                    isPauseClicked = false;
                    isSeekUsedWhilePaused = false;
                    playButton.setImageResource(R.drawable.play_blue_active);
                    stopButton.setImageResource(R.drawable.stop_red_inactive);
                    pauseButton.setImageResource(R.drawable.pause_blue_inactive);


                } else {

                    mediaPlayer.seekTo(songPausePosition);
                    mediaPlayer.start();
                    isPaused = false;
                    isPauseClicked = false;
                    playButton.setImageResource(R.drawable.play_blue_active);
                    stopButton.setImageResource(R.drawable.stop_red_inactive);
                    pauseButton.setImageResource(R.drawable.pause_blue_inactive);


                }


            }
        }


    }

    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    public void playerCleanup() {

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                //set progress to 0


                //set timer to 0

                elapsedTimeTv.setText("0:00");
                getTotalTime();
                songSeekBar.setProgress(0);
                //stop the player
                stopButton.callOnClick();


            }

        });


        isOnCompletionListenerAttached = true;

    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */

    public void replaySong() {


        //replayButton.getPivotX();

        int halfX = replayButton.getWidth() / 2;
        int halfY = replayButton.getHeight() / 2;


        //Log.d("myLogsReplayButton", "halfX: " + halfX + "halfY: " + halfY + "centerX :" + centreX + "centerY :"+ centerY );
        //rotation =new Rotation(replayButton,0,340);

        rotation = new Rotation(replayButton, 0, 350, halfX, halfY, Rotation.ABSOLUTE, Rotation.ABSOLUTE);
        rotation.roatateView(700);
        rotation.stopRotation(350);

        stopButton.callOnClick();
        playButton.callOnClick();

    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    public void playSongFromServer() {


        //playButton.setBackgroundColor(getResources().getColor(R.color.my_color_alternative_shade));
        //pauseButton.setBackgroundColor(getResources().getColor(R.color.my_color_bg));
        //stopButton.setBackgroundColor(getResources().getColor(R.color.my_color_bg));

        if (!isPlayPressed) {
            if (!isPaused) {
                android.widget.Toast.makeText(getApplicationContext(), "The song will be streamed shortly. Please be patient!!", android.widget.Toast.LENGTH_LONG).show();
                playButton.setImageResource(R.drawable.play_blue_active);
                mediaPlayer = new android.media.MediaPlayer();
                isPlayPressed = true;
                mediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
                try {

                    mediaPlayer.setDataSource(songFileReference);

                } catch (IllegalArgumentException | SecurityException | IllegalStateException e) {

                    android.widget.Toast.makeText(getApplicationContext(), "The song can't be streamed right now. Please retry!!", android.widget.Toast.LENGTH_LONG).show();
                    playButton.setImageResource(R.drawable.play_blue_inactive);



                } catch (java.io.IOException e) {

                    e.printStackTrace();
                    playButton.setImageResource(R.drawable.play_blue_inactive);


                }

                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(final MediaPlayer mediaPlayer) {

                        mediaPlayer.start();
                        isPaused = false;
                        isPauseClicked = false;
                        isStopped = false;
                        playButton.setImageResource(R.drawable.play_blue_active);
                        stopButton.setImageResource(R.drawable.stop_red_inactive);
                        pauseButton.setImageResource(R.drawable.pause_blue_inactive);

                        if (mediaPlayer.isPlaying()) {

                            Integer songDuration;
                            songDuration = mediaPlayer.getDuration();
                            songSeekBar.setMax(songDuration);
                            getTotalTime();

                            updateTimerHandler.postDelayed(updateTimerRunnable, 1000);


                        }


                    }
                });
            } else {

                if (isSeekUsedWhilePaused) {

                    mediaPlayer.seekTo(newTime);
                    mediaPlayer.start();
                    isPaused = false;
                    isPauseClicked = false;
                    isSeekUsedWhilePaused = false;
                    playButton.setImageResource(R.drawable.play_blue_active);
                    stopButton.setImageResource(R.drawable.stop_red_inactive);
                    pauseButton.setImageResource(R.drawable.pause_blue_inactive);


                } else {

                    mediaPlayer.seekTo(songPausePosition);
                    mediaPlayer.start();
                    isPaused = false;
                    isPauseClicked = false;
                    playButton.setImageResource(R.drawable.play_blue_active);
                    stopButton.setImageResource(R.drawable.stop_red_inactive);
                    pauseButton.setImageResource(R.drawable.pause_blue_inactive);


                }


            }
        }

    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    public void implementSeekFunctionality() {

        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if (!(mediaPlayer == null)) {
                    updateTimerHandler.removeCallbacks(updateTimerRunnable);

                    if (isPaused) {
                        isSeekUsedWhilePaused = true;
                    }

                }
                //remove the handler from updating the progress bar


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


                if (!(mediaPlayer == null)) {  //remove the handler from updating the progress bar
                    updateTimerHandler.removeCallbacks(updateTimerRunnable);

                    //get the new progress from the seek bar and update the timer and media player
                    newTime = seekBar.getProgress();
                    mediaPlayer.seekTo(newTime);

                    updateTimerHandler.postDelayed(updateTimerRunnable, 1000);
                }


            }
        });


    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    public void pauseSong() {


        if (!isPauseClicked) {


            if (mediaPlayer != null) {
                songPausePosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.pause();
                isPaused = true;
                isPlayPressed = false;
                playButton.setImageResource(R.drawable.play_blue_inactive);
                stopButton.setImageResource(R.drawable.stop_red_inactive);
                pauseButton.setImageResource(R.drawable.pause_blue_active);
                // playButton.setBackgroundColor(getResources().getColor(R.color.my_color_bg));
                // stopButton.setBackgroundColor(getResources().getColor(R.color.my_color_bg));
                // pauseButton.setBackgroundColor(getResources().getColor(R.color.my_color_alternative_shade));
                isPauseClicked = true;

            }
        }


    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    public void stopSong() {

        if (mediaPlayer != null) {

            if (mediaPlayer.isPlaying()) {

                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
                isPaused = false;
                isPauseClicked = false;
                isStopped = true;
                isPlayPressed = false;
                elapsedTimeTv.setText("0:00");
                //getTotalTime();
                songSeekBar.setProgress(0);
                playButton.setImageResource(R.drawable.play_blue_inactive);
                stopButton.setImageResource(R.drawable.stop_red_active);
                pauseButton.setImageResource(R.drawable.pause_blue_inactive);


            } else {

                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
                isPaused = false;
                isPauseClicked = false;
                isStopped = true;
                isPlayPressed = false;
                playButton.setImageResource(R.drawable.play_blue_inactive);
                stopButton.setImageResource(R.drawable.stop_red_active);
                pauseButton.setImageResource(R.drawable.pause_blue_inactive);
            }


            // playButton.setBackgroundColor(getResources().getColor(R.color.my_color_bg));
            // pauseButton.setBackgroundColor(getResources().getColor(R.color.my_color_bg));
            //stopButton.setBackgroundColor(getResources().getColor(R.color.my_color_alternative_shade));
            isOnCompletionListenerAttached = false;

           /*
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPaused = false;
            isStopped = true;
            isPlayPressed = false;
*/


        }


    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    //public static final String PREFS_NAME = "AOP_PREFS";
    //public static final String PREFS_KEY = "AOP_PREFS_String";
    public void songLookUp() {


        if (ExternalStorageUtil.isExternalStorageMounted()) {  //CHECK IF EXTERNAL STORAGE IS AVAILABLE

            String musicLocation = ExternalStorageUtil.getPublicExternalStorageBaseDir(Environment.DIRECTORY_MUSIC); //GET THE FILE PATH TO THE DOWNLOAD DIRECTORY


        }

    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    public void downloadSong() {


        //THIS METHOD IS CALLED WHEN THE USER PRESSES THE DOWNLOAD SONG BUTTON


        if (ExternalStorageUtil.isExternalStorageMounted()) {  //CHECK IF EXTERNAL STORAGE IS AVAILABLE

            downloadLocation = ExternalStorageUtil.getPublicExternalStorageBaseDir(Environment.DIRECTORY_MUSIC); //GET THE FILE PATH TO THE DOWNLOAD DIRECTORY

            File myFile = new File(downloadLocation, songTitle + fileType); //GET THE FILE NAME AND EXTENSION AND USE IT TO CREATE THE FILE OBJECT


            fetchConfiguration = new FetchConfiguration.Builder(this)
                    .setDownloadConcurrentLimit(3)
                    .build();

            fetch = Fetch.Impl.getInstance(fetchConfiguration);

            fileName = myFile.getAbsolutePath();
            final Request request = new Request(songFileReference, fileName);
            request.setPriority(Priority.HIGH);
            request.setNetworkType(NetworkType.ALL);
            request.setIdentifier(1);
            // request.addHeader("clientKey", "SD78DF93_3947&MVNGHE1WONG"); //don't know what this does so its commented out for now

            fetch.enqueue(request, new Func<Request>() {
                @Override
                public void call(@NotNull Request updatedRequest) {
                    //Request was successfully enqueued for download.
                    Log.d("myLogsDownloadRequest", "everything seems fine" + updatedRequest.getFile() + " > " + updatedRequest.getFileUri());
                    Log.d("myLogsDownloadRequest2", updatedRequest.getUrl());


                }
            }, new Func<Error>() {
                @Override
                public void call(@NotNull Error error) {
                    //An error occurred enqueuing the request.
                    Log.d("myLogsDownloadRequestF", "something went wrong" + error);

                }
            });

            requestId = request.getId();  //this is used to identify the download when the user wants to pause or cancel it


            fetchListener = new FetchListener() {
                @Override
                public void onAdded(@NotNull Download download) {

                    Toast.makeText(PlayMusic.this, download.getFile() + " " + "is queued!", Toast.LENGTH_LONG).show();

                    //display the notification for the first time
                    mNotificationBuilder.setProgress(100, 0, false);
                    mNotifyManager.notify(downloadProgressNotificationId, mNotificationBuilder.build());


                }

                @Override
                public void onQueued(@NotNull Download download, boolean b) {

                    if (request.getId() == download.getId()) {

                        Log.d("myLogsDLQ", "file is queued");


                    }

                }

                @Override
                public void onWaitingNetwork(@NotNull Download download) {

                    Log.d("myLogsDLWN", "file is waiting on network");


                }

                @Override
                public void onCompleted(@NotNull Download download) {

                    if (request.getId() == download.getId()) {

                        mNotificationBuilder.setContentText("Download Complete!");
                        // Removes the progress bar
                        mNotificationBuilder.setProgress(0, 0, false);
                        mNotifyManager.notify(downloadProgressNotificationId, mNotificationBuilder.build());


                        downloadButton.setImageResource(R.drawable.download_blue_inactive);
                        isCurrentlyDownloading = false;

                        Toast.makeText(PlayMusic.this, download.getFile() + " download has been completed!", Toast.LENGTH_LONG).show();
                        Log.d("myLogsDLC", "file download is completed");


                        songURI = download.getFileUri();  //the variable that is used to play the song from external storage
                        Log.d("myLogsDLC", "SONG URI" + songURI);


                    }


                }

                @Override
                public void onError(@NotNull final Download download, @NotNull Error error, @Nullable Throwable throwable) {

                    if (request.getId() == download.getId()) {

                        Toast.makeText(PlayMusic.this, "everything is horrible!", Toast.LENGTH_LONG).show();
                        Log.d("myLogsDLE", "there is an error");
                        //Log.d("myLogsDLE", error.getHttpResponse().getErrorResponse());

                        Log.d("myLogsDLE", throwable.getMessage());
//                        Log.d("myLogsDLE", throwable.getCause().toString());
                        mNotificationBuilder.setContentText("Download Error!");
                        // Removes the progress bar
                        mNotificationBuilder.setProgress(0, 0, false);
                        mNotifyManager.notify(downloadProgressNotificationId, mNotificationBuilder.build());


                        new TTFancyGifDialog.Builder(PlayMusic.this)
                                .setTitle("Download Error")
                                .setMessage("The download encountered an error. Would you like to cancel or try to resume downloading?")
                                .setPositiveBtnText("Resume")
                                .setPositiveBtnBackground("#22b573")
                                .setNegativeBtnText("Cancel")
                                .setNegativeBtnBackground("#c1272d")
                                .setGifResource(R.drawable.error_404_travolta)      //pass your gif, png or jpg
                                .isCancellable(true)
                                .OnPositiveClicked(new TTFancyGifDialogListener() {
                                    @Override
                                    public void OnClick() {

                                        downloadSong();
                                        Toast.makeText(PlayMusic.this, "Download will be resumed", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .OnNegativeClicked(new TTFancyGifDialogListener() {
                                    @Override
                                    public void OnClick() {

                                        Toast.makeText(PlayMusic.this, "The download has been removed", Toast.LENGTH_SHORT).show();

                                        fetch.delete(requestId);

                                    }
                                })
                                .build();


                    }


                }

                @Override
                public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int i) {

                }

                @Override
                public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int i) {

                    Log.d("myLogsDLS", "file has started downloading");
                    downloadButton.setImageResource(R.drawable.cancel_red_filled);
                    isCurrentlyDownloading = true;


                }

                @Override
                public void onProgress(@NotNull Download download, long l, long l1) {

                    mNotificationBuilder.setProgress(100, download.getProgress(), false);
                    mNotifyManager.notify(downloadProgressNotificationId, mNotificationBuilder.build());
                    Log.d("myLogsDLP", "downloaded so far: " + download.getProgress());


                }

                @Override
                public void onPaused(@NotNull Download download) {

                }

                @Override
                public void onResumed(@NotNull Download download) {

                }

                @Override
                public void onCancelled(@NotNull Download download) {

                    downloadButton.setImageResource(R.drawable.download_blue_inactive);
                    isCurrentlyDownloading = false;
                    mNotificationBuilder.setContentText("Download Cancelled!");
                    // Removes the progress bar
                    mNotificationBuilder.setProgress(0, 0, false);
                    mNotifyManager.notify(downloadProgressNotificationId, mNotificationBuilder.build());


                }

                @Override
                public void onRemoved(@NotNull Download download) {

                }

                @Override
                public void onDeleted(@NotNull Download download) {

                }


            };

            fetch.addListener(fetchListener);

        } else {

            Toast.makeText(PlayMusic.this, " The storage is not available !", Toast.LENGTH_LONG).show();


        }

        //ToDo ADD THIS TO A METHOD THAT DETERMINES WHEN THE USER IS DONE WITH THE DOWNLOAD
        //Remove listener when done.
        //fetch.removeListener(fetchListener);

    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    public void cancelDownload() {


        new TTFancyGifDialog.Builder(PlayMusic.this)
                .setTitle("Download Error")
                .setMessage("Are you sure you want to cancel the download? This action can't be undone!")
                .setPositiveBtnText("Resume")
                .setPositiveBtnBackground("#22b573")
                .setNegativeBtnText("Cancel")
                .setNegativeBtnBackground("#c1272d")
                .setGifResource(R.drawable.confirm_delete_holding_nose)      //pass your gif, png or jpg
                .isCancellable(true)
                .OnPositiveClicked(new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {

                        Toast.makeText(PlayMusic.this, "Download will be resumed", Toast.LENGTH_SHORT).show();
                    }
                })
                .OnNegativeClicked(new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {


                        if (fetch != null) {

                            fetch.cancel(requestId);
                            Toast.makeText(PlayMusic.this, " The download has been cancelled!", Toast.LENGTH_LONG).show();
                            isCurrentlyDownloading = false;
                            fetch.delete(requestId);


                        }

                    }
                })
                .build();


    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    public void getWritePermission() {

        try {
            if (ExternalStorageUtil.isExternalStorageMounted()) {

                //check if the app has permission to write to external storage or not
                int writeExternalStoragePermission = ContextCompat.checkSelfPermission(PlayMusic.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int readExternalStoragePermission = ContextCompat.checkSelfPermission(PlayMusic.this, Manifest.permission.READ_EXTERNAL_STORAGE);

                //if write permission is not yet granted
                if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    //request user to grant write external storage permission
                    ActivityCompat.requestPermissions(PlayMusic.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);

                }

                //if read permission is not yet granted
                if (readExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    //request user to grant write external storage permission
                    ActivityCompat.requestPermissions(PlayMusic.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);

                }


            }
        } catch (Exception ex) {

            Log.d("myLogsWritePermissionF", "something went wrong " + ex.getMessage());


        }


    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    public void getTotalTime() {


        /* java.lang.NullPointerException: Attempt to invoke virtual method 'int android.media.MediaPlayer.getDuration()' on a null object reference
        at com.example.dell.muhingalayoutprototypes.PlayMusic.getTotalTime(PlayMusic.java:1093)
        at com.example.dell.muhingalayoutprototypes.PlayMusic.stopSong(PlayMusic.java:697)
        at com.example.dell.muhingalayoutprototypes.PlayMusic$4.onClick(PlayMusic.java:248)
        at android.view.View.callOnClick(View.java:5718)
        at com.example.dell.muhingalayoutprototypes.PlayMusic.replaySong(PlayMusic.java:518)
        at com.example.dell.muhingalayoutprototypes.PlayMusic$6.onClick(PlayMusic.java:286)*/


        Integer durationMinutes = mediaPlayer.getDuration() / 60000;
        Integer durationSeconds = (mediaPlayer.getDuration() % 60000) / 1000;

        if (durationSeconds < 10) {
            String durationString = durationMinutes + ":0" + durationSeconds;
            songDurationTv.setText(durationString);
        } else {
            String durationString = durationMinutes + ":" + durationSeconds;
            songDurationTv.setText(durationString);
        }

    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */
    public void getElapsedTime() {

        Integer durationMinutes = mediaPlayer.getCurrentPosition() / 60000;
        Integer durationSeconds = (mediaPlayer.getCurrentPosition() % 60000) / 1000;

        if (durationSeconds < 10) {

            String durationString = durationMinutes + ":0" + durationSeconds;
            elapsedTimeTv.setText(durationString);

        } else {

            String durationString = durationMinutes + ":" + durationSeconds;
            elapsedTimeTv.setText(durationString);

        }


    }

    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


}


/*

     int q = request.getId();
        fetch.cancel(q, new Func<Download>() {
           @Override
           public void call(@NotNull Download result) {

           }
       }, new Func<Error>() {
           @Override
           public void call(@NotNull Error result) {

           }
       });



//the holy five  mp = media player
 if(mp!=null) {
        if(mp.isPlaying())
            mp.stop();
        mp.reset();
        mp.release();
        mp=null;
    }


 */

/*
todo fix the seek bar listenre incase user seek without valid medai player
todo add a prompt to tell the user the song has already been downloaded  //done
TODO PROGRESS NOTIFICATION KEEPS FLICKERING
TODO WHAT IF USER SEEKS AFTER SONG IS DONE PLAYING. HANDLE PLAYER EVENTS //done
TODO AFTER SONG HAS PLAYED STOP OR PAUSE THE MEDIA PLAYER  //done
TODO AFTER THE DOWNLOAD BUTTON IS CLICKED FOR AN ALREADY DOWNLOADED SONG IT DOES NOT DOWNLOAD  //temp fix
TODO ERROR MediaPlayer: Error (1,-1004)  //done
TODO SITUATION WEA DL GOES TO ERROR FOR PLAY FROM STORAGE //done
TODO CREATE THE FILE URI STRINGS YOURSELF  //done
TODO CHANGE THE DOWNLOAD BUTTON TO A CANCEL BUTTON WHEN THE USER IS DOWNLOADING  //DONE
TODO ADD A SHARE BUTTON
TODO ADD A LIKE BUTTON  //PURCHASE BUTTON WITH PRICE AND SUPPORT ARTIST DIALOG
TODO TIME SHOWING LIKE 1:9 INSTEAD OF 1:09  //DONE
todo ERROR WHEN THE PLAY BUTTON IS PRESSED TWICE DURING A SONG PLAYING //DONE
TODO ERROR AT A CERTAIN POINT DURING THE DOWNLOAD   //FALSE ALARM
TODO CHANGE THE BACKGROUND COLOR WHEN THE USER CLICKS BUTTONS
TODO IMPLEMENT MULTITHREADING FOR FASTER DOWNLOADING
TODO IMPLEMENT CACHING FOR RECENTLY PLAYED SONGS
todo USE A RUNNABLE TO ADD THE PROGRESS UPDATE WITH A DELAY BEFORE CHECKING //done
todo HANDLE THE LIFECYCLE EVENTS FOR THE MEDIA PLAYER
TODO SET THE INITIAL SEEK BAR TO ZERO //done
todo add a timer to show duration and other details //done
todo onStop reset the UI so that the user is not confused  //done
todo work on the UI
todo get a dependency for the round image view //done
TODO ON SONG COMPLETION LITSENER AND WHTA HAPPENS WHEN A SONG COMPLETES //done
//todo set links to icons 8 and ouch pics https://icons8.com
//todo set
//todo stop button should reject input events after clicked
*/


//file:///storage/emulated/0/Music/Complicated.m4a




/* <android.support.v7.widget.CardView
        android:id="@+id/call_owner_venues_details_card"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="32dp"
        app:cardCornerRadius="4dp"

        app:cardElevation="2dp"
        app:contentPadding="10dp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/venues_details_item_description">

        <ImageView
            android:id="@+id/call_owner_venues_details"
            android:layout_width="30dp"
            android:layout_height="30dp"
            android:src="@drawable/music_icon_filled_colored" />

    </android.support.v7.widget.CardView>


    <TextView
        android:layout_width="50dp"
        android:textAlignment="center"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:text="@string/call"
        android:textSize="12sp"
        app:fontFamily="@font/montserrat_regular"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/call_owner_venues_details_card" />



    */





/*<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/my_color_bg"
    tools:context=".PlayMusic">




    <android.support.v7.widget.Toolbar
        android:id="@+id/play_music_app_bar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/my_color_primary"
        android:minHeight="64dp"
        app:titleTextColor="@color/my_color_bg"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        />




    <TextView
        android:id="@+id/play_music_song_title"
        android:layout_width="0dp"
        android:layout_height="30dp"
        android:layout_marginStart="8dp"
        android:layout_marginTop="16dp"
        android:layout_marginEnd="8dp"
        android:fontFamily="@font/montserrat_regular"
        android:text="song title"
        android:textAlignment="center"
        android:textColor="@color/my_color_secondary"
        android:textSize="12sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/play_music_song_cover_image" />


    <de.hdodenhof.circleimageview.CircleImageView
        android:id="@+id/play_music_song_cover_image"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginStart="8dp"
        android:layout_marginTop="16dp"
        android:layout_marginEnd="8dp"
        android:src="@drawable/test_default_img1"
        app:layout_constraintDimensionRatio="H,16:9"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/play_music_artist_name" />


    <ImageButton

        android:id="@+id/play_music_play_button"

        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_marginStart="8dp"
        android:layout_marginTop="8dp"
        android:background="@color/my_color_bg"
        android:scaleType="centerCrop"
        android:src="@drawable/play_black_rounded"
        app:layout_constraintEnd_toStartOf="@+id/play_music_stop_button"
        app:layout_constraintStart_toEndOf="@+id/play_music_pause_button"
        app:layout_constraintTop_toBottomOf="@+id/play_music_song_title" />


    <ImageButton
        android:id="@+id/play_music_pause_button"

        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_marginTop="8dp"
        android:background="@color/my_color_bg"
        android:scaleType="centerCrop"
        android:src="@drawable/pause_black_empty"
        app:layout_constraintEnd_toStartOf="@+id/play_music_play_button"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/play_music_song_title" />

    <ImageButton
        android:id="@+id/play_music_download_button"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_marginStart="8dp"
        android:layout_marginTop="8dp"
        android:background="@color/my_color_bg"
        android:scaleType="centerCrop"
        android:src="@drawable/download_black"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@+id/play_music_stop_button"
        app:layout_constraintTop_toBottomOf="@+id/play_music_song_title" />


    <ImageButton
        android:id="@+id/play_music_stop_button"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_marginStart="8dp"
        android:layout_marginTop="8dp"
        android:background="@color/my_color_bg"
        android:scaleType="centerCrop"
        android:src="@drawable/stop_black"
        app:layout_constraintEnd_toStartOf="@+id/play_music_download_button"
        app:layout_constraintStart_toEndOf="@+id/play_music_play_button"
        app:layout_constraintTop_toBottomOf="@+id/play_music_song_title" />

    <TextView
        android:id="@+id/play_music_artist_name"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="8dp"
        android:layout_marginTop="16dp"
        android:layout_marginEnd="8dp"
        android:fontFamily="@font/montserrat_regular"
        android:text="artist name"
        android:textAlignment="center"
        android:textColor="@color/my_color_secondary"
        android:textSize="12sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/play_music_app_bar" />

    <TextView
        android:id="@+id/song_total_duration"
        android:layout_width="wrap_content"
        android:layout_height="30dp"
        android:layout_marginTop="32dp"
        android:layout_marginEnd="16dp"
        android:fontFamily="@font/montserrat_regular"
        android:gravity="end"
        android:text="0:00"
        android:textAlignment="center"
        android:textColor="@color/my_color_secondary"
        android:textSize="12sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/play_music_play_button" />


    <TextView
        android:id="@+id/song_elapsed_duration"
        android:layout_width="wrap_content"
        android:layout_height="30dp"
        android:layout_marginStart="16dp"
        android:layout_marginTop="32dp"
        android:fontFamily="@font/montserrat_regular"
        android:text="0:00"
        android:textAlignment="center"
        android:textColor="@color/my_color_secondary"
        android:textSize="12sp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/play_music_play_button" />


    <SeekBar
        android:id="@+id/song_seek_bar"
        style="?android:attr/seekBarStyle"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="48dp"
        android:layout_marginTop="32dp"
        android:layout_marginEnd="48dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/play_music_play_button" />


    <ImageButton
        android:id="@+id/play_music_replay_button"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_marginStart="32dp"
        android:layout_marginTop="40dp"
        android:background="@color/my_color_bg"
        android:scaleType="centerCrop"
        android:src="@drawable/replay_black"
        app:layout_constraintEnd_toStartOf="@+id/play_music_like_button"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/song_seek_bar" />


    <com.varunest.sparkbutton.SparkButton
        android:id="@+id/play_music_like_button"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_marginStart="32dp"
        android:layout_marginTop="40dp"
        app:layout_constraintEnd_toStartOf="@+id/play_music_share_button"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toEndOf="@+id/play_music_replay_button"
        app:layout_constraintTop_toBottomOf="@+id/song_seek_bar"
        app:sparkbutton_activeImage="@drawable/heart_red_filled"
        app:sparkbutton_animationSpeed="1.5"
        app:sparkbutton_iconSize="40dp"
        app:sparkbutton_inActiveImage="@drawable/heart_black_thick_outline"
        app:sparkbutton_primaryColor="@color/my_color_bg"
        app:sparkbutton_secondaryColor="@color/my_color_like_button_active" />


    <ImageButton
        android:id="@+id/play_music_share_button"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_marginStart="32dp"
        android:layout_marginTop="40dp"
        android:background="@color/my_color_bg"
        android:scaleType="centerCrop"
        android:src="@drawable/share_black"
        app:layout_constraintEnd_toStartOf="@+id/play_music_buy_button"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toEndOf="@+id/play_music_like_button"
        app:layout_constraintTop_toBottomOf="@+id/song_seek_bar" />


    <ImageButton
        android:id="@+id/play_music_buy_button"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_marginStart="32dp"
        android:layout_marginTop="40dp"
        android:layout_marginEnd="32dp"
        android:background="@color/my_color_bg"
        android:scaleType="centerCrop"
        android:src="@drawable/shop_black_filled"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toEndOf="@+id/play_music_share_button"
        app:layout_constraintTop_toBottomOf="@+id/song_seek_bar" />


</android.support.constraint.ConstraintLayout>*/



/*<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">




    <item
        android:title="Liked"
        android:id="@+id/play_music_liked_songs"
        app:showAsAction="ifRoom"
        android:orderInCategory="100"
        android:icon="@drawable/heart_white_outline"
        />


    <item
        android:title="Contact artist"
        android:id="@+id/play_music_contact_artist"
        app:showAsAction="never"
        android:orderInCategory="100"
        />


</menu>*/