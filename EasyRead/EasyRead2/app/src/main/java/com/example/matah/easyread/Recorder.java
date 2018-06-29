package com.example.matah.easyread;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by matah on 17/10/30.
 */

public class Recorder extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , TextToSpeech.OnInitListener, TextWatcher,SensorEventListener {
    private Locale currentSpokenLang = Locale.US;
    private NavigationView navigationView;
    private DatabaseReference mDatabase;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private float acelVal;
    private float acelLast;
    private float shake;
    private Button button;
    private TextView textView;
    private Locale locSpanish = new Locale("es", "MX");
    private Locale locRussian = new Locale("ru", "RU");
    private Locale locPortuguese = new Locale("pt", "BR");
    private Locale locDutch = new Locale("nl", "NL");

    private Locale[] languages = {locDutch, Locale.FRENCH, Locale.GERMAN, Locale.ITALIAN,locPortuguese ,locRussian , locSpanish};
    private FirebaseAuth firebaseAuth;
    private TextToSpeech textToSpeech;

    private int spinnerIndex = 0;

    private Spinner languageSpinner;

    private String[] arrayOfTranslations;
    private ArrayList<ArrayList<String>> spokenText;
    private int lines = 0;

    EditText wordsEntered;
    EditText wordsEnteredNew;

    private Record current;
    private MyBooks book;
    private String instace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        wordsEnteredNew = (EditText) findViewById(R.id.et_wordsNewBook);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    current = book.GetThisRecord();
                    current.SetRecord(instace);
                    book.SetThisRecord(current);
                    mDatabase.child(book.GetBookTitle().toString()).child(current.GetRecord().substring(0,10)).setValue(current.GetRecord());
                    Snackbar.make(view, "Text Saved", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                catch(Exception ex){
                    Snackbar.make(view, "Enter a new Book First", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        acelVal = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;
        button = (Button) findViewById(R.id.btn_speak);
        shake = 0.00f;
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        textView = (TextView)header.findViewById(R.id.textView);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null)
        {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            textView.setText("Welcome, "+user.getEmail());
            navigationView.getMenu().findItem(R.id.nav_reg_LogIn).setVisible(false);
        }
        navigationView.setNavigationItemSelectedListener(this);
        languageSpinner = (Spinner) findViewById(R.id.lang_spinner);
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                currentSpokenLang = languages[index];

                spinnerIndex = index;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        textToSpeech = new TextToSpeech(this,this);

        spokenText = new ArrayList<ArrayList<String>>();

        wordsEntered = (EditText) findViewById(R.id.et_words);
        wordsEntered.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    }
    public void onNewBook(View view){
        if (wordsEnteredNew.getText() == null){
            Snackbar.make(view, "Make a new Book first", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        else{
            book = new MyBooks(wordsEnteredNew.getText().toString());
        }
    }
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onDestroy() {
        if (textToSpeech != null)
        {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
    // Calls for the AsyncTask to execute when the translate button is clicked
    public void onTranslateText(View view) {

        EditText translateEditText = (EditText) findViewById(R.id.et_words);

        // If the user entered words to translate then get the JSON data
        if(!isEmpty(translateEditText)){

            Toast.makeText(this, "Getting Translations",
                    Toast.LENGTH_LONG).show();

            // Calls for the method doInBackground to execute
            new Recorder.GetXMLData().execute();

        } else {

            // Post an error message if they didn't enter words
            Toast.makeText(this, "Enter Words to Translate",
                    Toast.LENGTH_SHORT).show();

        }

    }

    // Check if the user entered words to translate
    // Returns false if not empty
    protected boolean isEmpty(EditText editText){

        // Get the text in the EditText convert it into a string, delete whitespace
        // and check length
        return editText.getText().toString().trim().length() == 0;

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent search;
        switch (id){
            case R.id.nav_reg_LogIn:
                Intent regLog;
                regLog = new Intent(this,LoginActivity.class);
                startActivity(regLog);
                this.finish();
                break;
            case R.id.nav_logOut:
                Intent logout;
                logout = new Intent(this,LoginActivity.class);
                startActivity(logout);
                firebaseAuth.signOut();
                this.finish();
                break;
            case R.id.nav_recorder:

                search = new Intent(this,Recorder.class);
                startActivity(search);
                break;
            case R.id.nav_search:

                search = new Intent(this,BookApiCalls.class);
                startActivity(search);
                break;
            case R.id.nav_myBook:
                search = new Intent(this,MyBooks.class);
                startActivity(search);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        acelLast = acelVal;
        acelVal = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = acelVal - acelLast;
        shake = shake * 0.9f + delta;

        if (shake > 12) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    ExceptSpeechInput(button);
                }
            }, 10);
    }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    class GetXMLData extends AsyncTask<Void, Void, Void> {

        String stringToPrint = "";

        @Override
        protected Void doInBackground(Void... voids) {

            String xmlString = "";

            String wordsToTranslate = "";

            EditText translateEditText = (EditText)findViewById(R.id.et_words);

            wordsToTranslate = translateEditText.getText().toString();

            wordsToTranslate = wordsToTranslate.replace(" ", "+");

            DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());

            HttpPost httpPost = new HttpPost("http://newjustin.com/translateit.php?action=xmltranslations&english_words=" + wordsToTranslate);

            httpPost.setHeader("Content-type", "text/xml");

            InputStream inputStream = null;

            try {

                HttpResponse response = httpClient.execute(httpPost);

                HttpEntity entity = response.getEntity();

                inputStream = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);

                StringBuilder sb = new StringBuilder();

                String line = null;

                while ((line = reader.readLine()) != null) {

                    sb.append(line);


                }

                xmlString = sb.toString();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(true);

                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(xmlString));

                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if ((eventType == XmlPullParser.START_TAG) && (!xpp.getName().equals("translations"))) {

                        stringToPrint = stringToPrint + xpp.getName() + " : ";


                    } else if (eventType == XmlPullParser.TEXT) {

                        stringToPrint = stringToPrint + xpp.getText() + "\n";

                    }

                    eventType = xpp.next();

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            TextView translateTextView = (TextView) findViewById(R.id.translate_text_view);

            // Make the TextView scrollable
            translateTextView.setMovementMethod(new ScrollingMovementMethod());

            // Eliminate the "language :" part of the string for the
            // translations
            String stringOfTranslations = stringToPrint.replaceAll("\\w+\\s:", "#");

            // Store the translations into an array
            arrayOfTranslations = stringOfTranslations.split("#");

            translateTextView.setText(stringToPrint);

        }
    }

    /// TextToSpeach
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS)
        {
            int result = textToSpeech.setLanguage(currentSpokenLang);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            {
                Toast.makeText(this, "Language not supproted!" , Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this, "Text to Speach failed" , Toast.LENGTH_LONG).show();

        }
    }
    public void readTheText(View view){
    try{
        textToSpeech.setLanguage(currentSpokenLang);
        if (arrayOfTranslations.length >= 9){
            textToSpeech.speak(arrayOfTranslations[spinnerIndex+4],
                    TextToSpeech.QUEUE_FLUSH, null);
        }
        else{
            Toast.makeText(this, "Translate Text First" , Toast.LENGTH_LONG).show();
        }
    }catch (Exception ex){

    }


    }
    // Converts speech to text
    public void ExceptSpeechInput(View view) {

        // Starts an Activity that will convert speech to text
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

    // The results of the speech recognizer are sent here
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        // 100 is the request code sent by startActivityForResult
        if((requestCode == 100) && (data != null) && (resultCode == RESULT_OK)){
            String message = wordsEntered.getText().toString().replace(". ", "  ");
            // Store the data sent back in an ArrayList
            spokenText.add((data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)));
            String fullSentance = (spokenText.get(lines).get(0) + "  ").toLowerCase();
            String toUpperSentace = fullSentance.substring(0,1).toUpperCase() + fullSentance.substring(1).toLowerCase();
            instace = (message.replace("  ",". ") +  toUpperSentace.replace("  ",". "));


            // Put the spoken text in the EditText
            wordsEntered.setText(instace);

            lines++;
        }

    }
}
