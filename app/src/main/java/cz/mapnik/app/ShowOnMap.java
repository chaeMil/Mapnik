package cz.mapnik.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class ShowOnMap extends Activity implements OnMapReadyCallback {

    private LatLng guess;
    private LatLng location;
    private boolean rightAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();

        guess = new LatLng(extras.getDouble("guessLatitude"),
                extras.getDouble("guessLongitude"));

        location = new LatLng(extras.getDouble("locLatitude"),
                extras.getDouble("locLongitude"));

        rightAnswer = extras.getBoolean("rightAnswer");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        GuessActivity.nextGuess(this);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap map) {


        map.setMyLocationEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(true);

        if(!rightAnswer) {
            Marker guessMarker = map.addMarker(new MarkerOptions()
                    .title("Guess")
                    .snippet("Your guess")
                    .position(guess));
            guessMarker.showInfoWindow();
        }

        Marker locMarker = map.addMarker(new MarkerOptions()
                .title("Location")
                .snippet("Right location")
                .position(location));
        locMarker.showInfoWindow();

        if(!rightAnswer) {
            PolylineOptions line = new PolylineOptions()
                    .add(guess)
                    .add(location)
                    .width(4)
                    .color(Color.RED);

            map.addPolyline(line);
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(guess,5));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location,10));
    }
}
