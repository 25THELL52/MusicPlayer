package com.example.musicplayer;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MyApplication extends Application {



    public MyApplication() {

    }





     List<MusicFile> scanDeviceForMp3Files(){

        //String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION
        };
        //final String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";
         List<MusicFile> mp3Files = new ArrayList<>();

         Cursor  cursor = null;
        try {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            cursor = getContentResolver().query(uri, projection, null, null, null);
            if( cursor != null){

                while( cursor.moveToNext() ){
                    String title = cursor.getString(0);
                    String artist = cursor.getString(1);
                    String path = cursor.getString(2);
                    String album  = cursor.getString(3);
                    String songDuration = cursor.getString(4);


                    MusicFile musicFile = new MusicFile(title,artist,path,album,songDuration);
                    mp3Files.add(musicFile);
                }

            }

            // print to see list of mp3 files
            for( MusicFile file : mp3Files) {
                Log.i("TAG", "file:/"+file.getPath() + file.getTitle());

            }
            Log.i("TAG", String.valueOf(mp3Files.size()));


        } catch (Exception e) {
            Log.e("TAG", e.toString());
        }finally{
            if( cursor != null){
                ((Cursor) cursor).close();
            }
        }
        return mp3Files;
    }

/*
    public static void filllistOfSongs(Context context){

        try {
            Scanner scan;


            scan = new Scanner(context.getAssets().open("lover_ts.txt"));
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



 */
    @Override
    public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {
        super.grantUriPermission(toPackage, uri, modeFlags);
    }
}
