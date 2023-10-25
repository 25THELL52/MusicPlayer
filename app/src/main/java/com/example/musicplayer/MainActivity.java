package com.example.musicplayer;

import android.Manifest;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // MediaPlayer mp;
    ListView playlist;
    ImageButton play;
    ImageButton previous;
    ImageButton pause;
    ImageButton next;
    List<MusicFile> musicFiles;
    List<String> listOfSongTitles;
    MyApplication application ;
//public static List<String> listOfSongsClone;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        play = findViewById(R.id.play);
        playlist = findViewById(R.id.playlist);
        previous = findViewById(R.id.previous);
        pause = findViewById(R.id.pause);
        next = findViewById(R.id.next);

        //PERMISSIONS :
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                return;
            }
        }


        application= (MyApplication) this.getApplication();
        musicFiles = application.scanDeviceForMp3Files();
        listOfSongTitles = getSongTitles() ;

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

    }

    private List<String> getSongTitles() {
        List<String> list = new ArrayList<>();
        for(MusicFile file : musicFiles)
        {
            list.add(file.getTitle());
        }
        return list;
    }

    private void playsongatindex(int position) {
        Intent intent = new Intent(this, MyPlayerService.class);
        intent.setAction("playsongatindex");
        intent.putExtra("index",position);
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

    private void sendIntent(String action) {

        Intent intent = new Intent(this, MyPlayerService.class);
        intent.setAction(action);
        startService(intent);


    }

    @Override
    protected void onDestroy() {
        //sendIntent("destroy");
        Log.i("lifecycle","onDestroy in activity");

        super.onDestroy();

    }

    @Override
    protected void onPause() {
        //sendIntent("isplaying?");

        super.onPause();
    }

    @Override
    protected void onStop() {

            sendIntent("stop");
            Log.i("lifecycle", "onStop in activity");

        super.onStop();
    }


}
