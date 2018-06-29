package com.example.matah.easyread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by matah on 17/10/31.
 */

public class FetchImage extends AsyncTask<String, Void, Bitmap> {
    private Bitmap image;
    public FetchImage(Bitmap img){
        image = img;
    }
    @Override
    protected Bitmap doInBackground(String... urls) {
        InputStream getUrl = null;
        Bitmap bmp = null;
        try {
            getUrl = new java.net.URL(urls[0]).openStream();
            bmp = BitmapFactory.decodeStream(getUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bmp != null){
            Log.d("wjat", bmp.toString());
            return bmp;
        }
        return null;
    }
    protected void onPostExecute(Bitmap s) {
        image = s;
    }
    public Bitmap getBitmap()
    {
        return image;
    }
}