package com.example.musicplayer;

import java.util.Comparator;

public class MusicFile {

    private String title ;
    private String artist ;
    private String path ;
    private String album  ;
    private String songDuration;
    private Boolean isOnPlay = false;

    public void setOnPlay(Boolean onPlay) {
        isOnPlay = onPlay;
    }

    public Boolean getOnPlay() {
        return isOnPlay;
    }

    public MusicFile(String title, String artist, String path, String album, String songDuration) {
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.album = album;
        this.songDuration = songDuration;

    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getPath() {
        return path;
    }

    public String getAlbum() {
        return album;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public static class MusicFileComparator implements Comparator<MusicFile>{
        @Override
        public int compare(MusicFile o1, MusicFile o2) {
            return (o1.getTitle().toLowerCase()).compareTo(o2.getTitle().toLowerCase());
        }
    }
}
