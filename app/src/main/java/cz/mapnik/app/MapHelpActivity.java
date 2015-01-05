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

/**
 * Created by chaemil on 5.1.15.
 */
public class MapHelpActivity extends Activity implements OnMapReadyCallback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_activity);

        Bundle extras = getIntent().getExtras();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        CircleButton doneButton = (CircleButton) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {

        map.getUiSettings().setMapToolbarEnabled(false);
        map.setMyLocationEnabled(false);
        map.getUiSettings().setAllGesturesEnabled(false);
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        LatLng loc = new LatLng(App.startingPoint.getLatitude(),App.startingPoint.getLongitude());

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,50));
        map.addMarker(new MarkerOptions()
                .position(loc)
                .title(getString(R.string.right_location)));
    }
}
