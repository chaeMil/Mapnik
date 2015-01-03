package cz.mapnik.app;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowOnMap extends Activity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        GuessActivity.nextGuess(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Bundle extras = getIntent().getExtras();

        LatLng guess = new LatLng(extras.getDouble("guessLatitude"),
                extras.getDouble("guessLongitude"));

        LatLng location = new LatLng(extras.getDouble("locLatitude"),
                extras.getDouble("locLongitude"));

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(guess, 13));

        map.addMarker(new MarkerOptions()
                .title("Guess")
                .snippet("Your guess")
                .position(guess));

        map.addMarker(new MarkerOptions()
                .title("Location")
                .snippet("Right location")
                .position(location));
    }
}
