package com.example.matah.easyread;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by matah on 17/10/31.
 */

public class FetchBook extends AsyncTask<String, Void, String>{

    private LinearLayout m_li;
    private Context c;
    private String[] myTextViews;
    private String[] URLS;
    private Bitmap[] imagesALL;
    private ArrayList<Card> cardsArrayList;
    private ListView list;
    private String previewLink = "";
    public FetchBook(LinearLayout li, Context c, ArrayList<Card> cards, ListView list)
    {
        m_li = li;
        this.c = c;
        cardsArrayList = cards;
        this.list = list;
    }
    public FetchBook(){}
    public int getTextViewArrayCount()
    {
        return myTextViews.length;
    }
    public void DeleteArrayList(){
       cardsArrayList.clear();
    }


    @Override
    protected String doInBackground(String... strings) {
        return NetworkUtils.getBookInfo(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        FetchImage im;
        try{
                     JSONObject jsonObject = new JSONObject(s);
                     JSONArray itemsArray = jsonObject.getJSONArray("items");
                     JSONObject images;
                     myTextViews = new String[itemsArray.length()];
                     imagesALL = new Bitmap[itemsArray.length()];
                    URLS = new String[itemsArray.length()];
                for(int i = 0; i <= itemsArray.length(); i++) {
                    myTextViews[i] = "";

                    JSONObject book = itemsArray.getJSONObject(i);
                    String title = null;
                    String publishdate = null;

                    String url = null;
                    String authors = null;
                    JSONObject volumeInfo = book.getJSONObject("volumeInfo");


                    try {
                        images = volumeInfo.getJSONObject("imageLinks");
                        authors ="\n author: "+ volumeInfo.getString("authors");
                        title ="\n title: "+ volumeInfo.getString("title");
                        publishdate ="\n publishdate: "+ volumeInfo.getString("publishedDate");
                        previewLink =volumeInfo.getString("previewLink");
                        url = images.getString("thumbnail");
                        im = (FetchImage) new FetchImage(imagesALL[i]).execute(url);
                        imagesALL[i] = im.getBitmap();
                        URLS[i] = url;
                    } catch (Exception ex) {
                        Toast.makeText(c,ex.toString(),Toast.LENGTH_LONG);
                    }
                    myTextViews[i] = (authors + title + publishdate);

                    cardsArrayList.add(new Card(URLS[i], myTextViews[i],previewLink));
                }

            }catch(Exception ex){
                Toast.makeText(c,ex.toString(),Toast.LENGTH_LONG);
             }finally {
                if (cardsArrayList.size() > 0){
                    CustomListAdapter adapter = new CustomListAdapter(c,R.layout.card_layout_main, cardsArrayList);
                    list.setAdapter(adapter);
                }
                return;
             }
    }
}
