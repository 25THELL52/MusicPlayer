package com.example.musicplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.SeekBar;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Action;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class MyPlayerService extends Service {

    private static final int NOTIFICATION_ID = 91857496;
    private static final String CHANNEL_ID = "AS8452269";
    private static final String CHANNEL_NAME = "playingmusicchannel";
    int s;
    int index = 0;
    boolean ispause;
    Boolean isplaying = false;
    int length;
    List<MusicFile> listOfSongs;
    MyApplication application;
    SeekBar seekBar;
    int progress;

    boolean isOnRepeat = false;
    boolean isOnshuffle = false;


    @Override
    public void onCreate() {
        application = (MyApplication) this.getApplication();
        listOfSongs = application.scanDeviceForMp3Files();

        s = listOfSongs.size();
        Log.i("index", String.valueOf(s));
        super.onCreate();
    }


    public MyPlayerService() {
    }

    public MediaPlayer getMp() {
        return application.getMp();
    }

    public void setMp(MediaPlayer mp) {
        application.setMp(mp);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent != null) {


            if (intent.getAction() == "destroy") {
                if (getMp() != null) getMp().stop();
            }


            if (intent.getAction() == "stop") {
                Log.i("lifecycle", "onStop in service");

                if (getMp() != null) {
                    if (isplaying) {
                        //onPause();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            makeNotification();
                        }
                        //onPlay();
                    }
                }
            }


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

        if (intent.getAction() == "seekBarProgressChanged") {

            progress = intent.getIntExtra("progress", 0);
            onSeekBarProgressChanged(progress);


        }
        if (intent.getAction() == "shuffle") {

            isOnshuffle = !isOnshuffle;
            if (isOnshuffle) onShuffle();


        }
        if (intent.getAction() == "repeat") {

            onRepeat();


        }


        Log.i("index", String.valueOf(index));

        return super.

                onStartCommand(intent, flags, startId);

    }

    private void onRepeat() {
        isOnRepeat = !isOnRepeat ;
    }

    private void onShuffle() {

        //if (isplaying) {

        if(listOfSongs.size()>0) {
            int randomIndex = getRandomIndex();

            if (getMp() != null) {
                getMp().stop();
                application.setMp(null);
                playsong(randomIndex);

            } else {
                playsong(randomIndex);

            }

            ispause = false;
            isplaying = true;
        }
    }


        //}

    private int getRandomIndex() {
        Random randomInt = new Random();
        int randomIndex = randomInt.nextInt(listOfSongs.size() - 1);
        return randomIndex;
    }

    private void onSeekBarProgressChanged(int progress) {
        if (getMp() != null) {
            if (isplaying) {
                getMp().seekTo(progress);
            } else length = progress;
        }

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
                    //.setContentTitle("              Music player")
                    //.setContentText("playing: " + listOfSongs.get(index).getTitle())
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.tune)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setWhen(0)
                    .setColor(ContextCompat.getColor(getApplicationContext(), R.color.purple_700))
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(1, 2, 3, 4));

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
            //builder.setContentIntent(pendingprevious);
            PendingIntent pendingplay = PendingIntent.getService(this, 0, intentplay, 0);
            //builder.setContentIntent(pendingplay);
            PendingIntent pendingpause = PendingIntent.getService(this, 0, intentpause, 0);
            //builder.setContentIntent(pendingpause);
            PendingIntent pendingnext = PendingIntent.getService(this, 0, intentnext, 0);
            //builder.setContentIntent(pendingnext);


            Action action =
                    new Action.Builder(R.drawable.ic_baseline_skip_previous_24, "previous", pendingprevious)
                            .build();
            builder.addAction(action);

            builder.addAction(new Action.Builder(R.drawable.ic_baseline_play_circle_outline_24, "play", pendingplay).build());
            builder.addAction(new Action.Builder(R.drawable.ic_baseline_pause_circle_outline_24, "pause", pendingpause).build());
            builder.addAction(new Action.Builder(R.drawable.ic_baseline_skip_next_24, "next", pendingnext).build());


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
                    new Notification.Action.Builder(R.drawable.ic_baseline_skip_previous_24, "previous", pendingprevious)
                            .build();
            builder.addAction(action);

            builder.addAction(new Notification.Action.Builder(R.drawable.ic_baseline_play_circle_outline_24, "play", pendingplay).build());
            builder.addAction(new Notification.Action.Builder(R.drawable.ic_baseline_pause_circle_outline_24, "pause", pendingpause).build());
            builder.addAction(new Notification.Action.Builder(R.drawable.ic_baseline_skip_next_24, "next", pendingnext).build());


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

        if(isOnshuffle) index = getRandomIndex();
         else {
            index++;
            if (index == s) {
                index = 0;

            }
        }

        if (getMp() != null) {
            getMp().stop();

            application.setMp(null);

        }


        playsong(index);
        ispause = false;
        isplaying = true;


    }

    private void onPlaySongAtIndex(int index) {

        if (getMp() != null) {
            getMp().stop();
            application.setMp(null);
        }

        playsong(index);

        ispause = false;
        isplaying = true;
    }

    private void onPlay() {

        if (!isplaying) {
            if (ispause && getMp() != null) {

                getMp().seekTo(length);
                getMp().start();


            } else {
                application.setMp(null);
                playsong(index);
            }
            ispause = false;
            isplaying = true;


        }

    }

    private void onPlayPreviousSong() {
         if(isOnshuffle) index=getRandomIndex();

         else {
             if (index == 0) {
                 index = s;
             }
             index--;
         }

        if (getMp() != null) {
            getMp().stop();

            application.setMp(null);
        }

        playsong(index);
        ispause = false;
        isplaying = true;

    }

    private void onPause() {


        if (getMp() != null) {
            if (getMp().isPlaying()) {
                getMp().pause();
                length = getMp().getCurrentPosition();
                ispause = true;
                isplaying = false;
            }
        }
    }

    public void playsong(int i) {


        try {
            application.setMp(new MediaPlayer());
            //Log.i("listofsongsclonefirst.e", listOfSongs.get(i));
            /*
            AssetFileDescriptor afd = getAssets().openFd(listOfSongs.get(i) + ".mp3");
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

             */
            String filePath = listOfSongs.get(i).getPath();
            File file = new File(filePath);
            FileInputStream inputStream = new FileInputStream(file);
            getMp().setDataSource(inputStream.getFD());
            inputStream.close();

            getMp().setOnCompletionListener(mp1 -> {
                if(isOnRepeat){
                    playsong(index);

                }
                else if(isOnshuffle)
                {index = getRandomIndex();
                playsong(index);
                }


                else {
                    index++;

                    if (index == s) {
                        index = 0;
                    }
                    playsong(index);

                }
            });
            getMp().setOnPreparedListener(mp1 -> {
                onPrepared(mp1);
            });
            getMp().prepare();
            //mp.start();
            sendIntent(i);


        } catch (IOException ioException) {
            ioException.printStackTrace();
        }


        if (index < s) {
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

    public void sendIntent(int index) {


        Intent intent = new Intent();
        intent.setAction("initializeSeekBar");


        sendBroadcast(intent);
    }
}