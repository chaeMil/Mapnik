package cz.mapnik.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import at.markushi.ui.CircleButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChooseDiameter extends Activity implements OnMapReadyCallback {

    private LatLng start;
    private int diameter = 0;
    private CircleButton confirmButton;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_diameter);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.custom_font_regular))
                .setFontAttrId(R.attr.fontPath)
                .build());

        final MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        CircleButton threeKDiameterButton = (CircleButton) findViewById(R.id.threeKButton);
        CircleButton fiveKDiameterButton = (CircleButton) findViewById(R.id.fiveKButton);
        CircleButton tenKDiameterButton = (CircleButton) findViewById(R.id.tenKButton);
        confirmButton = (CircleButton) findViewById(R.id.confirmButton);

        threeKDiameterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDiameter(mapFragment, 3000);

            }
        });

        fiveKDiameterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDiameter(mapFragment, 5000);

            }
        });

        tenKDiameterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDiameter(mapFragment, 10000);

            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (diameter != 0) {
                    Intent i = new Intent(ChooseDiameter.this, GuessActivity.class);
                    App.CurrentGame.CURRENT_DIAMETER = diameter;
                    finish();
                    startActivity(i);
                }
            }
        });

        start = new LatLng(App.startingPoint.getLatitude(), App.startingPoint.getLongitude());


    }

    public void setDiameter(MapFragment mapFragment, double radius) {
        GoogleMap map = mapFragment.getMap();

        diameter = (int) radius;
        confirmButton.setVisibility(View.VISIBLE);

        CircleOptions circleOptions = new CircleOptions()
                .strokeColor(getResources().getColor(R.color.bright_green))
                .fillColor(getResources().getColor(R.color.bright_green_alpha))
                .center(start)
                .radius(radius);

        map.clear();

        map.addCircle(circleOptions);
        setStartingPoint(map);
    }

    public void setStartingPoint (GoogleMap map) {
        Marker startingPosition = map.addMarker(new MarkerOptions()
                .title(getString(R.string.starting_location).toUpperCase())
                .position(start));
        startingPosition.showInfoWindow();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setMyLocationEnabled(false);
        map.getUiSettings().setAllGesturesEnabled(false);

        setStartingPoint(map);

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(start,10));
    }
}
