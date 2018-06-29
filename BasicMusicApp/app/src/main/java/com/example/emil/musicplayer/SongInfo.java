package com.example.emil.musicplayer;

/**
 * Created by Emil on 06/09/2017.
 */

public class SongInfo {

    public String SongName, artistName, SongURL;

    public SongInfo() {

    }

    public SongInfo(String songName, String artistName, String songURL) {
        this.SongName = songName;
        this.artistName = artistName;
        this.SongURL = songURL;
    }

    public String getSongName() {
        return SongName;
    }

    public void setSongName(String songName) {
        SongName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSongURL() {
        return SongURL;
    }

    public void setSongURL(String songURL) {
        SongURL = songURL;
    }
}