package com.example.sony.newsworld;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity implements OnMapReadyCallback {

    GoogleMap unimap;
    float zoomlevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zoomlevel = 1;
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        unimap = map;
        LatLng sydney = new LatLng(-33.867, 151.206);

        //map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,zoomlevel));

        map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Toast.makeText(this, String.valueOf(event.getKeyCode()), Toast.LENGTH_SHORT).show();

        LatLng sydney = new LatLng(-33.867, 151.206);
        zoomlevel++;
        //map.setMyLocationEnabled(true);
        unimap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,zoomlevel));

        Log.i("key pressed", String.valueOf(event.getKeyCode()));
        return true;
        //return super.dispatchKeyEvent(event);
    }


}
