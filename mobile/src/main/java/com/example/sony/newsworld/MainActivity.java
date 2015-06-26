package com.example.sony.newsworld;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity implements OnMapReadyCallback {

    GoogleMap unimap;
    float zoomLevel =1;
    //LatLng ltlng;
    float lat=10, lng=80;
    String streetName="";

    TextView place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zoomLevel = 1;
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        place = (TextView)findViewById(R.id.place);
    }

    public void Clicked(View v){
        moveMap(v.getTag().toString());
       // Toast.makeText(this, v.getTag().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        unimap = map;
        LatLng sydney = new LatLng(lat, lng);

        //map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel));

//        map.addMarker(new MarkerOptions()
//                .title("Sydney")
//                .snippet("The most populous city in Australia.")
//                .position(sydney));
    }

    public void moveMap(String key){
        //map.setMyLocationEnabled(true);

        if(key.equals("19")) { //up
            //Toast.makeText(this, "up", Toast.LENGTH_SHORT).show();
            lat++;
        }else if(key.equals("20")){ //down
            lat--;
           // Toast.makeText(this, "down", Toast.LENGTH_SHORT).show();

        }else if(key.equals("21")){ //left
            lng--;
            //Toast.makeText(this, "left", Toast.LENGTH_SHORT).show();

        }else if(key.equals("22")){ //right
            lng++;
            //Toast.makeText(this, "right", Toast.LENGTH_SHORT).show();

        }
        LatLng ltlng = new LatLng(lat, lng);
        unimap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltlng, zoomLevel));

        new RequestTask().execute("http://maps.googleapis.com/maps/api/geocode/json?latlng=+"+lat+","+lng+"+&sensor=true");
        //Log.i("url", "http://maps.googleapis.com/maps/api/geocode/json?latlng=+"+lat+","+lng+"+&sensor=true");
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //This is the filter
        if (event.getAction()!=KeyEvent.ACTION_UP)
            return true;
        //Toast.makeText(this, String.valueOf(event.getKeyCode()), Toast.LENGTH_SHORT).show();

        moveMap(String.valueOf(event.getKeyCode()));
        //LatLng sydney = new LatLng(-33.867, 151.206);
       // zoomLevel++;
        //map.setMyLocationEnabled(true);
       // unimap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel));

       // Log.i("key pressed", String.valueOf(event.getKeyCode()));
        return true;
        //return super.dispatchKeyEvent(event);
    }



    public class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            URL url = null;
            String json = "{'knockkock': 'null here'}";

            try {
                url = new URL(uri[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                int sc = con.getResponseCode();
                if (sc == 200) {
                    InputStream is = con.getInputStream();
                    json = readResponse(is);
                    is.close();
                }
            } catch (Exception e) {
            }

            return json;
        }

        private String readResponse(InputStream is) throws IOException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] data = new byte[2048];
            int len = 0;
            while ((len = is.read(data, 0, data.length)) >= 0) {
                bos.write(data, 0, len);
            }
            return new String(bos.toByteArray(), "UTF-8");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            streetName="";
            try {
                JSONObject json = new JSONObject(result);
                streetName=json.getJSONArray("results").getJSONObject(0).getString("formatted_address");
            } catch (JSONException e) {
                //e.printStackTrace();
            }
            Log.i("Result", streetName);
            place.setText(streetName);
            //Do anything with response..
        }
    }
}

