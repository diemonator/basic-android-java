package com.example.matah.easyread;

import android.service.quicksettings.Tile;

/**
 * Created by matah on 17/10/30.
 */

public class Record {
    private String recording,Title;

    public Record(){

    }
    public Record(String Title){
        this.Title = Title;
    }
    public Record(String input, String Title){
        recording = input;
        this.Title = Title;
    }
    public String GetRecord(){
        return  recording;
    }
    public String GetRecordTitle(){
        return  Title;
    }
    public void SetRecord(String input){
        recording = input;
    }
    public void AddToRecord(String input){
        recording += " "+input;
    }
}
