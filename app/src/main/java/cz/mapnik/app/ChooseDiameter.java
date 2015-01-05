package cz.mapnik.app;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

public class ChooseDiameter extends Activity implements OnMapReadyCallback {

    private LatLng start;
    private int diameter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_diameter);

        final MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        CircleButton threeKDiameterButton = (CircleButton) findViewById(R.id.threeKButton);
        CircleButton fiveKDiameterButton = (CircleButton) findViewById(R.id.fiveKButton);
        CircleButton tenKDiameterButton = (CircleButton) findViewById(R.id.tenKButton);

        threeKDiameterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDiameter(mapFragment, 3000);
                diameter = 3000;
            }
        });

        fiveKDiameterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDiameter(mapFragment, 5000);
                diameter = 5000;
            }
        });

        tenKDiameterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDiameter(mapFragment, 10000);
                diameter = 10000;
            }
        });

        start = new LatLng(App.startingPoint.getLatitude(), App.startingPoint.getLongitude());


    }

    public void setDiameter(MapFragment mapFragment, double radius) {
        GoogleMap map = mapFragment.getMap();

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

        setStartingPoint(map);

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(start,10));
    }
}
