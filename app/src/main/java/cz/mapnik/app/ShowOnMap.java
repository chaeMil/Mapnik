package cz.mapnik.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

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

import at.markushi.ui.CircleButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowOnMap extends Activity implements OnMapReadyCallback {

    private LatLng guess;
    private LatLng location;
    private boolean rightAnswer;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.custom_font_regular))
                .setFontAttrId(R.attr.fontPath)
                .build());

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        CircleButton doneButton = (CircleButton) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GuessActivity.nextGuess(ShowOnMap.this);
                finish();
            }
        });

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


        map.getUiSettings().setMapToolbarEnabled(false);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(false);
        //map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setScrollGesturesEnabled(true);

        if(!rightAnswer) {
            Marker guessMarker = map.addMarker(new MarkerOptions()
                    .title(getResources().getString(R.string.guess))
                    .position(guess));
            guessMarker.showInfoWindow();
        }

        Marker locMarker = map.addMarker(new MarkerOptions()
                .title(getResources().getString(R.string.right_location))
                .snippet(getIntent().getExtras().getString("rightAnswer"))
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
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location,13));
    }
}
