package cz.mapnik.app;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import at.markushi.ui.CircleButton;


public class ChooseCustomLocation extends Activity implements OnMapReadyCallback {

    private CircleButton doneButton;
    private LatLng customLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        final MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        doneButton = (CircleButton) findViewById(R.id.doneButton);
        doneButton.setVisibility(View.GONE);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location loc = new Location("loc");
                loc.setLatitude(customLocation.latitude);
                loc.setLongitude(customLocation.longitude);
                App.setStartingPoint(loc);
                Intent i = new Intent(ChooseCustomLocation.this, ChooseDiameter.class);
                startActivity(i);
            }
        });

        TextView mapLabel = (TextView) findViewById(R.id.mapLabel);
        mapLabel.setText(getString(R.string.custom_location).toUpperCase());
        mapLabel.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setMyLocationEnabled(false);
        map.getUiSettings().setAllGesturesEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setScrollGesturesEnabled(true);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                customLocation = latLng;

                map.clear();
                map.addMarker(new MarkerOptions()
                        .position(latLng));

                doneButton.setVisibility(View.VISIBLE);
            }
        });
    }
}
