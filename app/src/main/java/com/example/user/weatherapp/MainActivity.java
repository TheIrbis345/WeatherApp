package com.example.user.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static String LOG_TAG = "log";
    String clouds = "";
    Double temp = 0.0;
    Double pressure = 0.0;
    Double humidity = 0.0;
    Double wind = 0.0;
    String city = "";
    Button btnGet;
    TextView tvCity,tvClouds,tvTemp,tvPres,tvHum,tvWind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        btnGet = findViewById( R.id.btnGet );
        tvCity = findViewById( R.id.tvCity );
        tvClouds = findViewById( R.id.tvClouds );
        tvTemp = findViewById( R.id.tvTemp );
        tvPres = findViewById( R.id.tvPres );
        tvHum = findViewById( R.id.tvHum );
        tvWind = findViewById( R.id.tvWind );
        btnGet.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
    new ParseTask().execute();
    tvCity.setText( city );
    tvClouds.setText( clouds );
    tvTemp.setText( temp.toString() );
    tvPres.setText( pressure.toString() );
    tvHum.setText( humidity.toString() );
    tvWind.setText( wind.toString() );
    }

    private class ParseTask  extends AsyncTask<Void,Void,String> {
        HttpURLConnection conn = null;
        BufferedReader rdr = null;
        String res = "";
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?id=491687&type=like&APPID=de34d7f42404b59b678227c60e93535b");
                conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod( "GET" );
                conn.connect();
                InputStream stream = conn.getInputStream();
                StringBuffer buf =  new StringBuffer(  );
                rdr = new BufferedReader( new InputStreamReader( stream ) );
                String line;
                while ((line=rdr.readLine())!=null)
                    buf.append( line );
                res = buf.toString();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return res;
        }
        private  JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
            JSONObject subObj = jObj.getJSONObject(tagName);
            return subObj;
        }
        private  String getString(String tagName, JSONObject jObj) throws JSONException {
            return jObj.getString(tagName);
        }

        private  float getFloat(String tagName, JSONObject jObj) throws JSONException {
            return (float) jObj.getDouble(tagName);
        }

        private  int getInt(String tagName, JSONObject jObj) throws JSONException {
            return jObj.getInt(tagName);
        }
        @Override
        protected void onPostExecute(String str)
        {
            super.onPostExecute( str );
            Log.d(LOG_TAG,str);
            JSONObject mainObj = null;
            try {
                mainObj = new JSONObject( str );
                JSONArray arr=mainObj.getJSONArray( "weather" );
                JSONObject obj = arr.getJSONObject( 0 );
                clouds = obj.getString( "description" );
                JSONObject tmp = mainObj.getJSONObject( "main" );
                temp = tmp.getDouble( "temp" );
                temp=Math.floor( temp-273 );
                pressure = tmp.getDouble( "pressure" );
                pressure = Math.floor( pressure*0.75 );
                humidity = tmp.getDouble( "humidity" );
                JSONObject objWind = mainObj.getJSONObject( "wind" );
                wind = objWind.getDouble( "speed" );
                //tmp = object.getJSONObject( "sys" );
                city = mainObj.getString( "name" );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
