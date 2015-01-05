package cz.mapnik.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import at.markushi.ui.CircleButton;


public class MapHelpActivity extends Activity implements OnMapReadyCallback {
    private double locLatitude;
    private double locLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_activity);

        Bundle extras = getIntent().getExtras();
        locLatitude = extras.getDouble("locLatitude");
        locLongitude = extras.getDouble("locLongitude");

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        CircleButton doneButton = (CircleButton) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {

        map.getUiSettings().setMapToolbarEnabled(false);
        map.setMyLocationEnabled(false);
        map.getUiSettings().setAllGesturesEnabled(false);
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        LatLng loc = new LatLng(locLatitude,locLongitude);

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,17));
        map.addMarker(new MarkerOptions()
                .position(loc)
                .title(getString(R.string.right_location)));
    }
}
