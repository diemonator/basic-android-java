package com.example.matah.easyread;

import android.graphics.Bitmap;
import android.media.Image;

/**
 * Created by matah on 17/11/01.
 */

public class Card {
    private String imgURL;
    private String title;
    private String priviewLink;

    public Card(String imgURL, String title, String priviewLink) {
        this.imgURL = imgURL;
        this.title = title;
        this.priviewLink = priviewLink;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getTitle() {
        return title;
    }

    public void setPriviewLink(String priviewLink) {
        this.priviewLink = priviewLink;
    }
    public String getPriviewLink() {
        return priviewLink;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
