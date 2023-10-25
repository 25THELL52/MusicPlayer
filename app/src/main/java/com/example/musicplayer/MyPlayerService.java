package com.example.musicplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Action;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class MyPlayerService extends Service {

    private static final int NOTIFICATION_ID = 91857496 ;
    private static final String CHANNEL_ID = "AS8452269" ;
    private static final String CHANNEL_NAME = "playingmusicchannel" ;
    int s;
    MediaPlayer mp = null;
    int index = 0;
    boolean ispause;
     Boolean isplaying = false;
    int length;
    List<MusicFile> listOfSongs;
    MyApplication application;



    @Override
    public void onCreate() {
        application= (MyApplication) this.getApplication();
        listOfSongs = application.scanDeviceForMp3Files();
        s = listOfSongs.size();
        Log.i("index", String.valueOf(s));

        super.onCreate();
    }


    public MyPlayerService() {
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent != null) {



            if(intent.getAction() == "destroy")
            { if(mp!=null) mp.stop();}



            if(intent.getAction() == "stop")
            {        Log.i("lifecycle","onStop in service");

                if(mp!=null){
                if(isplaying) {
                    //onPause();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        makeNotification();
                    }
                    //onPlay();
                }
            }}


            if (intent.getAction() == "pause") {

                onPause();

            }


            if (intent.getAction() == "previous") {

                onPlayPreviousSong();


            }
            if (intent.getAction() == "next") {

                onPlaynextsong();

            }
            if (intent.getAction() == "playsongatindex") {

                index = intent.getIntExtra("index", 0);
                onPlaySongAtIndex(index);

            }
        }
        if (intent.getAction() == "play") {

            onPlay();

        }


        Log.i("index",String.valueOf(index));

        return super.

    onStartCommand(intent, flags, startId);

}
    //@RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void makeNotification() {
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            makeNotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Music player")
                    .setContentText("playing: " + listOfSongs.get(index))
                    .setAutoCancel(true)

              .setSmallIcon(R.drawable.tune)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setWhen(0)
                   .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(1, 2, 3,4))
                    ;

            //when notification pause button clicked it pauses the music

            Intent intentprevious = new Intent(this, MyPlayerService.class);
            intentprevious.setAction("previous");
            Intent intentplay = new Intent(this, MyPlayerService.class);
            intentplay.setAction("play");
            Intent intentpause = new Intent(this, MyPlayerService.class);
            intentpause.setAction("pause");
            Intent intentnext = new Intent(this, MyPlayerService.class);
            intentnext.setAction("next");




            PendingIntent pendingprevious = PendingIntent.getService(this, 0, intentprevious, 0);
            builder.setContentIntent(pendingprevious);
            PendingIntent pendingplay = PendingIntent.getService(this, 0, intentplay, 0);
            builder.setContentIntent(pendingplay);
            PendingIntent pendingpause = PendingIntent.getService(this, 0, intentpause, 0);
            builder.setContentIntent(pendingpause);
            PendingIntent pendingnext = PendingIntent.getService(this, 0, intentnext, 0);
            builder.setContentIntent(pendingnext);


            Action action =
                    new Action.Builder(R.drawable.ic_baseline_skip_previous_24, "previous",pendingprevious)
                            .build();
            builder.addAction(action);

            builder.addAction(new Action.Builder(R.drawable.ic_baseline_play_circle_outline_24, "play",pendingplay).build());
            builder.addAction(new Action.Builder(R.drawable.ic_baseline_pause_circle_outline_24, "pause",pendingpause).build());
            builder.addAction(new Action.Builder(R.drawable.ic_baseline_skip_next_24, "next",pendingnext).build());





            // when notification clicked it jumps you into the activity :

            Intent intent2 = new Intent(MyPlayerService.this, MainActivity.class);
            PendingIntent pending = PendingIntent.getActivity(this, 0, intent2, 0);
            builder.setContentIntent(pending);



            // build notification

            Notification notification = builder.build();
            NotificationManager manager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID, notification);

        } else {

            Notification.Builder builder = new Notification.Builder(this)
                    .setContentTitle("Music player")
                    .setContentText("playing: " + listOfSongs.get(index).getTitle())
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.tune)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setStyle(new Notification.MediaStyle());



                  ;


            //when notification pause button clicked it pauses the music
            //



            Intent intentprevious = new Intent(this, MyPlayerService.class);
            intentprevious.setAction("previous");
            Intent intentplay = new Intent(this, MyPlayerService.class);
            intentplay.setAction("play");
            Intent intentpause = new Intent(this, MyPlayerService.class);
            intentpause.setAction("pause");
            Intent intentnext = new Intent(this, MyPlayerService.class);
            intentnext.setAction("next");




            PendingIntent pendingprevious = PendingIntent.getService(this, 0, intentprevious, 0);
            builder.setContentIntent(pendingprevious);
            PendingIntent pendingplay = PendingIntent.getService(this, 0, intentplay, 0);
            builder.setContentIntent(pendingplay);
            PendingIntent pendingpause = PendingIntent.getService(this, 0, intentpause, 0);
            builder.setContentIntent(pendingpause);
            PendingIntent pendingnext = PendingIntent.getService(this, 0, intentnext, 0);
            builder.setContentIntent(pendingnext);


            Notification.Action action =
                    new Notification.Action.Builder(R.drawable.ic_baseline_skip_previous_24, "previous",pendingprevious)
                            .build();
            builder.addAction(action);

            builder.addAction(new Notification.Action.Builder(R.drawable.ic_baseline_play_circle_outline_24, "play",pendingplay).build());
            builder.addAction(new Notification.Action.Builder(R.drawable.ic_baseline_pause_circle_outline_24, "pause",pendingpause).build());
            builder.addAction(new Notification.Action.Builder(R.drawable.ic_baseline_skip_next_24, "next",pendingnext).build());






            // when notification clicked, it jumps you into the activity :


            Intent intent3 = new Intent(MyPlayerService.this, MainActivity.class);
            PendingIntent pending = PendingIntent.getActivity(this, 0, intent3, 0);
            builder.setContentIntent(pending);



            // build notification



            Notification notification = builder.build();
            NotificationManager manager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID, notification);

        }


    }

    // Helper method to create a notification channel for
    // Android 8.0+


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void makeNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(
                channel);
    }

    private void onPlaynextsong() {
        index++;

        if (mp != null) {
            mp.stop();

            mp = null;
        }

        if (index == s) {
            index = 0;

        }
        playsong(index);
        ispause = false;
        isplaying = true;


    }

    private void onPlaySongAtIndex(int index) {

        if (mp != null) {
            mp.stop();
            mp = null;
        }

        playsong(index);

        ispause = false;
        isplaying = true;
    }

    private void onPlay() {

        if (!isplaying) {
            if (ispause && mp != null) {

                    mp.seekTo(length);
                    mp.start();

            } else {
                mp = null;
                playsong(index);
            }
            ispause = false;
            isplaying = true;
        }
    }

    private void onPlayPreviousSong() {
        if (mp != null) {
        mp.stop();

        mp = null;
    }

        if (index == 0) {
            index = s;
        }
        playsong(index - 1);
        index--;
        ispause = false;
        isplaying = true;

    }

    private void onPause() {


        if (mp != null) {
            if (mp.isPlaying()) {
                mp.pause();
                length = mp.getCurrentPosition();
                ispause = true;
                isplaying = false;
            }
        }
    }

    public void playsong(int i) {


        try {
            mp = new MediaPlayer();

            //Log.i("listofsongsclonefirst.e", listOfSongs.get(i));
            /*
            AssetFileDescriptor afd = getAssets().openFd(listOfSongs.get(i) + ".mp3");
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

             */


            String filePath = listOfSongs.get(i).getPath();
            File file = new File(filePath);
            FileInputStream inputStream = new FileInputStream(file);
            mp.setDataSource(inputStream.getFD());
            inputStream.close();

            mp.setOnCompletionListener(mp1 -> {

                index++;

                if (index == s) {
                    index = 0;
                }
                playsong(index); });
            mp.setOnPreparedListener(mp1 -> {onPrepared(mp1);});
            mp.prepare();
            //mp.start();



        } catch (IOException ioException) {
            ioException.printStackTrace();
        }


        if(index < s ) {
                Log.i("playing: ", listOfSongs.get(index) + ".mp3");
            }
            //Log.i("playing: ", listOfSongs.get(i) + ".mp3");

        }



    public void fillarray(List<String> listOfSongs) {


        try {

            Scanner scan = new Scanner(getAssets().open("lover_ts.txt"));
            while (scan.hasNextLine()) {
                listOfSongs.add(scan.nextLine());
            }
            scan.close();

            for (int i = 0; i < listOfSongs.size(); i++) {
                Log.i("added :", listOfSongs.get(i));
            }


        } catch (IOException e) {
            Log.i("message", "from catch");
            e.printStackTrace();
        }

    }

    public void onPrepared(MediaPlayer mp1) {
        mp1.start();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");


    }
}