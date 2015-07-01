package com.example.sony.newsworld;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends Activity implements OnMapReadyCallback {

    GoogleMap unimap;
    float zoomLevel =3;
    //LatLng ltlng;
    float lat=10, lng=80;
    String streetName="";

    TextView place, news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zoomLevel = 1;
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        place = (TextView)findViewById(R.id.place);
        news = (TextView)findViewById(R.id.news);

    }

    public void Clicked(View v){
        moveMap(v.getTag().toString());
        // Toast.makeText(this, v.getTag().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        unimap = map;
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        moveMap("");
        addHeatMap();
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

        new RequestLocation().execute("http://maps.googleapis.com/maps/api/geocode/json?latlng=+" + lat + "," + lng + "+&sensor=true");

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


    private ArrayList<LatLng> readItems() throws JSONException {
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        //InputStream inputStream = getResources().openRawResource(resource);
        //String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray("[\n"+
                "{\"lat\" : 13.5107885, \"lng\" : 76.9810854} ,\n" +
                "{\"lat\" : 13.1112066, \"lng\" : 77.7309023} ,\n" +
                "{\"lat\" : 13.0055222, \"lng\" : 77.167853} ,\n" +
                "{\"lat\" : 25.2741133, \"lng\" : 79.0068423} ,\n" +
                "{\"lat\" : 16.3848547, \"lng\" : 80.4822656} ,\n" +
                "{\"lat\" : 20.1071181, \"lng\" : 78.8750064} ,\n" +
                "{\"lat\" : 17.5480969, \"lng\" : 79.4365298} ,\n" +
                "{\"lat\" : 14.63238, \"lng\" : 79.8677 } ,\n" +
                "{\"lat\" : -37.8361, \"lng\" : 144.845 } ,\n" +
                "{\"lat\" : -38.4034, \"lng\" : 144.192 } ,\n" +
                "{\"lat\" : -38.7597, \"lng\" : 143.67 } ,\n" +
                "{\"lat\" : -36.9672, \"lng\" : 141.083 }\n" +
                "]");
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            list.add(new LatLng(lat, lng));
        }
        return list;
    }
    private void addHeatMap() {
        List<LatLng> list = null;

        // Get the data: latitude/longitude positions of police stations.
        try {
            list = readItems();
        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        }

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .build();

        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay mOverlay = unimap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    public class RequestLocation extends AsyncTask<String, String, String> {

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
                e.printStackTrace();
            }
            Log.i("Result", streetName);
            place.setText(streetName);
            if (streetName.length()>0){
                try {
                    String[] tokens = streetName.replaceAll("\\d", "").replaceAll(" ", "").split(",", -1);
                    //Log.e("String", tokens[tokens.length - 1] + tokens[tokens.length - 2]);
                    String url="http://content.guardianapis.com/search?api-key=test&q=" + tokens[tokens.length - 1] +",%20"+ tokens[tokens.length - 2];
                    Log.i("URL", url);
                    new RequestNews().execute(url);
                }catch (Exception e){

                }
            }
            //Do anything with response..
        }
    }

    public class RequestNews extends AsyncTask<String, String, String> {

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
            String newstext="";
            List<String> list = new ArrayList<String>();
            try {
                JSONObject json = new JSONObject(result);
                for(int i=0; i<json.getJSONObject("response").getJSONArray("results").length(); i++){
                    list.add(json.getJSONObject("response").getJSONArray("results").getJSONObject(i).getString("webTitle"));
                    newstext+="<br/>&#8226;"+list.get(i);
                }
                //Log.d("List", list.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            news.setText(Html.fromHtml(newstext));
            //Do anything with response..
        }
    }
}

