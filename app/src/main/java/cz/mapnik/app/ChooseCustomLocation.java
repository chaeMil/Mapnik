package cz.mapnik.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

import at.markushi.ui.CircleButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ChooseCustomLocation extends Activity implements OnMapReadyCallback {

    private CircleButton doneButton;
    private LatLng customLocation;
    private CircleButton myLocationButton;

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

        final MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        doneButton = (CircleButton) findViewById(R.id.doneButton);
        doneButton.setVisibility(View.GONE);
        myLocationButton = (CircleButton) findViewById(R.id.myLocationButton);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location loc = new Location("loc");
                loc.setLatitude(customLocation.latitude);
                loc.setLongitude(customLocation.longitude);
                App.setStartingPoint(loc);
                App.CurrentGame.COURSE = Mapnik.CUSTOM_LOCATION;
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
        map.setMyLocationEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setScrollGesturesEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                customLocation = latLng;

                map.clear();
                map.addMarker(new MarkerOptions()
                        .position(latLng));

                doneButton.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInUp)
                        .duration(400)
                        .playOn(doneButton);
            }
        });

        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                myLocationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Location userLocation = map.getMyLocation();
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), 10));

                    }
                });

                if (myLocationButton.getVisibility() != View.VISIBLE) {
                    myLocationButton.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.SlideInRight).duration(400).playOn(myLocationButton);
                }
            }
        });
    }
}
