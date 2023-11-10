package com.example.musicplayer;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // MediaPlayer mp;
    ListView playlist;
    ImageButton play;
    ImageButton previous;
    ImageButton pause;
    ImageButton next;

    ImageButton shuffle, repeat;

    SeekBar seekBar;
    List<MusicFile> musicFiles;
    List<String> listOfSongTitles;
    MyApplication application;
    ReceiverMainActivity receiver;
    IntentFilter intentFilter;
    boolean isBroadcastRegistered = false;
    boolean isRepeatActive;
    boolean isShuffleActive;

    Runnable myRunnable;
    Handler handler;
    MainActivity activity;

//public static List<String> listOfSongsClone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //PERMISSIONS :
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                return;
            }

        }


        play = findViewById(R.id.play);
        playlist = findViewById(R.id.playlist);
        previous = findViewById(R.id.previous);
        pause = findViewById(R.id.pause);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekbar);
        shuffle = findViewById(R.id.shuffle);
        repeat = findViewById(R.id.repeat);

        isRepeatActive = false;
        isShuffleActive = false;

        application = (MyApplication) this.getApplication();
        //seekBar.setMax(application.getMp().getDuration());
         activity =new MainActivity() ;


        // registerReceiver so the application would be addressed by the operating system whenever it receives
// a broadcast with whose intent action matches the defined action set to the intent filter .


        {
            receiver = new ReceiverMainActivity();
            intentFilter = new IntentFilter();
            intentFilter.addAction("initializeSeekBar");
            registerReceiver(receiver, intentFilter);

        }


        application = (MyApplication) this.getApplication();
        musicFiles = application.scanDeviceForMp3Files();
        listOfSongTitles = getSongTitles();

        ArrayAdapter arrayAdapter = new ArrayAdapter(
                this, R.layout.simple_list_item_1, R.id.tv, listOfSongTitles);

        playlist.setAdapter(arrayAdapter);


        playlist.setOnItemClickListener(


                (parent, view, position, id) -> {

                    pause.setVisibility(View.VISIBLE);
                    play.setVisibility(View.INVISIBLE);
                    playsongatindex(position);

                }
        );

        play.setOnClickListener(v -> {

            onclickplay();

        });

        pause.setOnClickListener(v -> {

            onclickpause();

        });
        next.setOnClickListener(v -> {

            onclicknext();

        });
        previous.setOnClickListener(v -> {

            onclickprevious();

        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) onSeekBarProgressChanged(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        shuffle.setOnClickListener(v -> {

            isShuffleActive = !isShuffleActive;
            setShuffleImageResource((ImageButton) v);
            sendIntent("shuffle");


        });

        repeat.setOnClickListener(v -> {
            isRepeatActive = !isRepeatActive;
            setRepeatImageResource((ImageButton) v);
            sendIntent("repeat");


        });

    }

    private void setRepeatImageResource(ImageButton v) {
        if (isRepeatActive) v.setImageResource(R.drawable.baseline_repeat_active_30);
        else v.setImageResource(R.drawable.baseline_repeat_30);
    }

    private void setShuffleImageResource(ImageButton v) {
        if (isShuffleActive) v.setImageResource(R.drawable.baseline_shuffle_active_30);
        else v.setImageResource(R.drawable.baseline_shuffle_30);
    }

    private void setColor(boolean isActive, ImageButton imageButton) {
    }

    private List<String> getSongTitles() {
        List<String> list = new ArrayList<>();
        for (MusicFile file : musicFiles) {
            list.add(file.getTitle());
        }
        return list;
    }

    private void playsongatindex(int position) {


        Intent intent = new Intent(this, MyPlayerService.class);
        intent.setAction("playsongatindex");
        intent.putExtra("index", position);
        startService(intent);


    }

    private void onclickprevious() {

        pause.setVisibility(View.VISIBLE);
        play.setVisibility(View.INVISIBLE);
        sendIntent("previous");

    }

    private void onclicknext() {

        pause.setVisibility(View.VISIBLE);
        play.setVisibility(View.INVISIBLE);

        sendIntent("next");
    }

    private void onclickpause() {

        play.setVisibility(View.VISIBLE);
        pause.setVisibility(View.INVISIBLE);


        sendIntent("pause");

        //   mp.stop();

    }


    private void onclickplay() {
        pause.setVisibility(View.VISIBLE);
        play.setVisibility(View.INVISIBLE);


        sendIntent("play");

    }

    private void onSeekBarProgressChanged(int progress) {

        Intent intent = new Intent(this, MyPlayerService.class);
        intent.setAction("seekBarProgressChanged");
        intent.putExtra("progress", progress);
        startService(intent);


    }


    private void initialiseSeekbar() {


        if (handler != null) {
            handler.removeCallbacks(myRunnable);
        }
        seekBar.setMax(application.getMp().getDuration());
        handler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (application.getMp() != null) {
                        seekBar.setProgress(application.getMp().getCurrentPosition());
                        Log.i("message", "currentPosition  " + application.getMp().getCurrentPosition());
                        handler.postDelayed(this, 1000);
                    }
                } catch (Exception e) {
                    seekBar.setProgress(0);
                }
            }


        };
        handler.postDelayed(myRunnable, 0);
    }

    private void sendIntent(String action) {

        Intent intent = new Intent(this, MyPlayerService.class);
        intent.setAction(action);
        startService(intent);


    }


    public class ReceiverMainActivity extends BroadcastReceiver {

        // what the activity does when you receive a broadcast with the defined intent filter action is in the body of the following OnReceive method
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i("message", "broadcast was properly received");


            play.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.VISIBLE);
            initialiseSeekbar();

            //unregisterReceiver(receiver);
            //isBroadcastRegistered = false;
        }
    }

    @Override
    protected void onDestroy() {
        //sendIntent("destroy");
        Log.i("lifecycle", "onDestroy in activity");

        if (handler != null) {
            handler.removeCallbacks(myRunnable);
            handler = null;
        }
        if (application.getMp() != null) {
            application.getMp().stop();
            application.getMp().release();
            application.setMp(null);
        }
        super.onDestroy();

    }

    @Override
    protected void onPause() {

        if (receiver != null && isBroadcastRegistered == true) {
            unregisterReceiver(receiver);
            isBroadcastRegistered = false;

        }
        super.onPause();
    }

    @Override
    protected void onStop() {

        super.onStop();
        sendIntent("stop");
        Log.i("lifecycle", "onStop in activity");
        handler.removeCallbacks(myRunnable);
        handler = null;

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("message", "onStart()");
        if (receiver != null && !isBroadcastRegistered) {
            registerReceiver(receiver, intentFilter);
            isBroadcastRegistered = true;
        }
        if (application.getMp() != null) {
            Log.i("message", "reinitialized seekBar from onStart");
            initialiseSeekbar();
            if (application.getMp().isPlaying()) {

                play.setVisibility(View.INVISIBLE);
                pause.setVisibility(View.VISIBLE);

            } else {

                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.INVISIBLE);

            }
        }

    }

    @Override
    public void onBackPressed() {

        if (handler != null) {
            handler.removeCallbacks(myRunnable);
            handler = null;
        }
        if (application.getMp() != null) {
            application.getMp().stop();
            application.getMp().release();
            application.setMp(null);
        }
        super.onBackPressed();


    }

/*
    @Override
    protected void onRestart() {
        Log.i("message", "onRestart()");

        if(application.getMp()!= null)

        {           Log.i("message", "reinitialized seekBar from onRestart");

            initialiseSeekbar();
        }
        super.onRestart();
    }

     */
}
