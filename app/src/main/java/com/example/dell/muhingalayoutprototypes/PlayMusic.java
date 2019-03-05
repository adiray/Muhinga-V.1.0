package com.example.dell.muhingalayoutprototypes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Objects;


public class PlayMusic extends AppCompatActivity {


    //miscellaneous objects
    String songTitle, artistName, songFileReference, albumName, coverImage;

    //initialize the views
    TextView songTitleTv, artistNameTv;
    ImageButton playButton, downloadButton, pauseButton, stopButton;
    ImageView coverImageImgView;

    //Media player objects
    MediaPlayer mediaPlayer = null;
    Integer songPosition;
    Boolean isPaused = false;


    //Fetch downloader objects
    private Fetch fetch;
    FetchConfiguration fetchConfiguration;
    String url, fileName, fileType, downloadLocation;
    FetchListener fetchListener;

    //file downloading objects

    private static final int  REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;



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


        //assign the text to the textViews
        songTitleTv.setText(songTitle);
        artistNameTv.setText(artistName);


        //set the cover image
        Glide.with(this).load(coverImage).into(coverImageImgView);


        //set onClickListeners to the control buttons
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSong();
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
                downloadSong();
            }
        });


        getWritePermission();


    }

    public void playSong() {


        playButton.setBackgroundColor(getResources().getColor(R.color.my_color_alternative_shade));
        pauseButton.setBackgroundColor(getResources().getColor(R.color.my_color_bg));
        stopButton.setBackgroundColor(getResources().getColor(R.color.my_color_bg));


        if (!isPaused) {
            mediaPlayer = new android.media.MediaPlayer();
            mediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
            try {

                mediaPlayer.setDataSource(songFileReference);

            } catch (IllegalArgumentException | SecurityException | IllegalStateException e) {

                android.widget.Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", android.widget.Toast.LENGTH_LONG).show();

            } catch (java.io.IOException e) {

                e.printStackTrace();

            }

            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                    mediaPlayer.start();


                }
            });
        } else {

            mediaPlayer.seekTo(songPosition);
            mediaPlayer.start();
            isPaused = false;


        }


    }


    public void pauseSong() {

        if (mediaPlayer != null) {
            songPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            isPaused = true;
            playButton.setBackgroundColor(getResources().getColor(R.color.my_color_bg));
            stopButton.setBackgroundColor(getResources().getColor(R.color.my_color_bg));
            pauseButton.setBackgroundColor(getResources().getColor(R.color.my_color_alternative_shade));

        }


    }


    public void stopSong() {

        if (mediaPlayer != null) {

            playButton.setBackgroundColor(getResources().getColor(R.color.my_color_bg));
            pauseButton.setBackgroundColor(getResources().getColor(R.color.my_color_bg));
            stopButton.setBackgroundColor(getResources().getColor(R.color.my_color_alternative_shade));

            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPaused = false;

        }


    }


    public void downloadSong() {

        downloadLocation= ExternalStorageUtil.getPublicExternalStorageBaseDir(Environment.DIRECTORY_DOWNLOADS);

        File myFile = new File(downloadLocation,songTitle + "." + fileType);

        fetchConfiguration = new FetchConfiguration.Builder(this)
                .setDownloadConcurrentLimit(3)
                .build();

        fetch = Fetch.Impl.getInstance(fetchConfiguration);
        fileType = songFileReference.substring(songFileReference.length() - 4);
        fileName = myFile.getAbsolutePath();
        // fileName = "/downloads/" + songTitle + "." + fileType;
        final Request request = new Request(songFileReference, fileName);
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);
        //request.addHeader("clientKey", "SD78DF93_3947&MVNGHE1WONG"); //don't know what this does so its commented out for now

        fetch.enqueue(request, new Func<Request>() {
            @Override
            public void call(@NotNull Request updatedRequest) {
                //Request was successfully enqueued for download.
                Log.d("myLogsDownloadRequest", "everything seems fine" + updatedRequest.getFile() + updatedRequest.getFileUri());
                Log.d("myLogsDownloadRequest2",updatedRequest.getUrl());


            }
        }, new Func<Error>() {
            @Override
            public void call(@NotNull Error error) {
                //An error occurred enqueuing the request.
                Log.d("myLogsDownloadRequestF", "something went wrong" + error);

            }
        });


        fetchListener = new FetchListener() {
            @Override
            public void onAdded(@NotNull Download download) {


            }

            @Override
            public void onQueued(@NotNull Download download, boolean b) {

                if (request.getId() == download.getId()) {

                    Toast.makeText(PlayMusic.this, download.getFile() + " " + "is queued!", Toast.LENGTH_LONG).show();
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

                    Toast.makeText(PlayMusic.this, download.getFile() + " download has been completed!", Toast.LENGTH_LONG).show();
                    Log.d("myLogsDLC", "file is completed");


                }


            }

            @Override
            public void onError(@NotNull Download download, @NotNull Error error, @Nullable Throwable throwable) {

                if (request.getId() == download.getId()) {

                    Toast.makeText(PlayMusic.this, "everything is horrible!", Toast.LENGTH_LONG).show();
                    Log.d("myLogsDLE", "there is an error");
                    //Log.d("myLogsDLE", error.getHttpResponse().getErrorResponse());

                    Log.d("myLogsDLE", throwable.getMessage());
                    Log.d("myLogsDLE", throwable.getCause().toString());


                }


            }

            @Override
            public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int i) {

            }

            @Override
            public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int i) {

                Log.d("myLogsDLS", "file has started downloading");

            }

            @Override
            public void onProgress(@NotNull Download download, long l, long l1) {

                Log.d("myLogsDLP", "downloaded so far: "+ download.getProgress());


            }

            @Override
            public void onPaused(@NotNull Download download) {

            }

            @Override
            public void onResumed(@NotNull Download download) {

            }

            @Override
            public void onCancelled(@NotNull Download download) {

            }

            @Override
            public void onRemoved(@NotNull Download download) {

            }

            @Override
            public void onDeleted(@NotNull Download download) {

            }
        };

        fetch.addListener(fetchListener);

        //ToDo ADD THIS TO A METHOD THAT DETERMINES WHEN THE USER IS DONE WITH THE DOWNLOAD
        //Remove listener when done.
        //fetch.removeListener(fetchListener);

    }


    public void getWritePermission(){

        try {
            if (ExternalStorageUtil.isExternalStorageMounted()){

                //check if the app has permission to write to external storage or not
                int writeExternalStoragePermission = ContextCompat.checkSelfPermission(PlayMusic.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int readExternalStoragePermission = ContextCompat.checkSelfPermission(PlayMusic.this, Manifest.permission.READ_EXTERNAL_STORAGE);

                //if write permission is not yet granted
                if (writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED){
                    //request user to grant write external storage permission
                    ActivityCompat.requestPermissions(PlayMusic.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);

                }

                //if read permission is not yet granted
                if (readExternalStoragePermission!= PackageManager.PERMISSION_GRANTED){
                    //request user to grant write external storage permission
                    ActivityCompat.requestPermissions(PlayMusic.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);

                }




            }
        }catch (Exception ex){


            Log.d("myLogsWritePermissionF", "something went wrong " + ex.getMessage());


        }




    }



}
