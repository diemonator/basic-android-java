package com.example.matah.easyread;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Locale;

import static com.example.matah.easyread.R.id.tvTitleResponce;

/**
 * Created by matah on 17/10/31.
 */

public class BookApiCalls extends AppCompatActivity{
    private static final String TAG = BookApiCalls.class.getSimpleName();
    private EditText mBookRequest;
    private TextView mTitle;
    private LinearLayout m_li;
    private ListView listCards;
    private int Count;
    private FetchBook f;
    private ArrayList<Card> cardsArrayList;
    private Locale currentSpokenLang = Locale.US;

    private DatabaseReference mDatabase;

    private Locale locSpanish = new Locale("es", "MX");
    private Locale locRussian = new Locale("ru", "RU");
    private Locale locPortuguese = new Locale("pt", "BR");
    private Locale locDutch = new Locale("nl", "NL");

    private Locale[] languages = {locDutch, Locale.FRENCH, Locale.GERMAN, Locale.ITALIAN, Locale.ITALIAN,locPortuguese, locRussian, locSpanish};

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);
        mBookRequest = (EditText) findViewById(R.id.request);
        mTitle = (TextView) findViewById(R.id.tvTitleResponce);
        m_li = (LinearLayout) findViewById(R.id.email_login_form);
        listCards = (ListView) findViewById(R.id.listView);
        f = new FetchBook();
        cardsArrayList = new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    listCards.removeAllViewsInLayout();
                    cardsArrayList.clear();
                }catch (Exception ex)
                {

                }
            }
        });
        listCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listCards.getItemAtPosition(position);
                Card  str = (Card) o; //As you are using Default String Adapter
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(((Card) o).getPriviewLink()));
                startActivity(browserIntent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    public void searchBooks(View view){
        String queryString = mBookRequest.getText().toString();
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() && queryString.length() !=0){
            new FetchBook(m_li,this,cardsArrayList,listCards).execute(queryString);
            Count = queryString.length();
            mTitle.setText("Press To Clear");

        }else{
            if (queryString.length() == 0){
                mTitle.setText("");
                Toast.makeText(this,"Please enter a search term",Toast.LENGTH_LONG);
            }else{
                mTitle.setText("");
                Toast.makeText(this,"Please check your network connection",Toast.LENGTH_LONG);
            }
        }
    }
    public void searchBooksVoice(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Use a language model based on free-form speech recognition
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Recognize speech based on the default speech of device
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        // Prompt the user to speak
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_input_phrase));

        try{

            startActivityForResult(intent, 100);

        } catch (ActivityNotFoundException e){

            Toast.makeText(this, getString(R.string.stt_not_supported_message), Toast.LENGTH_LONG).show();
        }

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        // 100 is the request code sent by startActivityForResult
        if((requestCode == 100) && (data != null) && (resultCode == RESULT_OK)){
            String queryString = (data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)).get(0);
            mBookRequest.setText(queryString);
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected() && queryString.length() !=0){
                new FetchBook(m_li,this,cardsArrayList,listCards).execute(queryString);
                Count = queryString.length();
                mTitle.setText("Press To Clear");

            }else{
                if (queryString.length() == 0){
                    mTitle.setText("");
                    Toast.makeText(this,"Please enter a search term",Toast.LENGTH_LONG);
                }else{
                    mTitle.setText("");
                    Toast.makeText(this,"Please check your network connection",Toast.LENGTH_LONG);
                }
            }
        }

    }
}
