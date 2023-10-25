package com.example.musicplayer;

public class MusicFile {

    private String title ;
    private String artist ;
    private String path ;
    private String album  ;
    private String songDuration;

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
}
