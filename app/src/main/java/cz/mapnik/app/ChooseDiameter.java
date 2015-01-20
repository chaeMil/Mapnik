package cz.mapnik.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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
    private CircleButton tenKDiameterButton;
    private CircleButton fiveKDiameterButton;
    private CircleButton threeKDiameterButton;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void prepareUi() {


        Thread show10KDiameter = new Thread(){
            public void run(){
                try{
                    sleep(800);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            YoYo.with(Techniques.BounceInUp)
                                    .duration(500)
                                    .playOn(tenKDiameterButton);
                            tenKDiameterButton.setVisibility(View.VISIBLE);
                        }
                    });
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        };

        Thread show5KDiameter = new Thread(){
            public void run(){
                try{
                    sleep(1200);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            YoYo.with(Techniques.BounceInUp)
                                    .duration(500)
                                    .playOn(fiveKDiameterButton);
                            fiveKDiameterButton.setVisibility(View.VISIBLE);
                        }
                    });
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        };

        Thread show3KDiameter = new Thread(){
            public void run(){
                try{
                    sleep(1600);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            YoYo.with(Techniques.BounceInUp)
                                    .duration(500)
                                    .playOn(threeKDiameterButton);
                            threeKDiameterButton.setVisibility(View.VISIBLE);
                        }
                    });
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        };

        show3KDiameter.start();
        show5KDiameter.start();
        show10KDiameter.start();
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

        threeKDiameterButton = (CircleButton) findViewById(R.id.threeKButton);
        fiveKDiameterButton = (CircleButton) findViewById(R.id.fiveKButton);
        tenKDiameterButton = (CircleButton) findViewById(R.id.tenKButton);
        confirmButton = (CircleButton) findViewById(R.id.confirmButton);

        prepareUi();

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
                    if (App.CurrentGame.COURSE.equals(Mapnik.PLAYER_LOCATION)) {
                        switch (diameter) {
                            case 3000:
                                App.CurrentGame.COURSE =
                                        getString(R.string.leaderboard_3k_player_location);
                                App.CurrentGame.COURSE_NAME =
                                        getString(R.string.leaderboard_3k_player_location_name);
                                break;
                            case 5000:
                                App.CurrentGame.COURSE =
                                        getString(R.string.leaderboard_5k_player_location);
                                App.CurrentGame.COURSE_NAME =
                                        getString(R.string.leaderboard_5k_player_location_name);
                                break;
                            case 10000:
                                App.CurrentGame.COURSE =
                                        getString(R.string.leaderboard_10k_player_location);
                                App.CurrentGame.COURSE_NAME =
                                        getString(R.string.leaderboard_10k_player_location_name);
                                break;
                        }
                    }
                    finish();
                    startActivity(i);
                }
            }
        });

        start = new LatLng(App.startingPoint.getLatitude(), App.startingPoint.getLongitude());


    }

    public void setDiameter(MapFragment mapFragment, double radius) {
        GoogleMap map = mapFragment.getMap();

        if(diameter == 0) {
            confirmButton.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.SlideInRight)
                    .duration(400)
                    .playOn(confirmButton);
        } else {
            YoYo.with(Techniques.Tada)
                    .duration(400)
                    .playOn(confirmButton);
        }

        diameter = (int) radius;


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
        map.setOnMapClickListener(null);

        setStartingPoint(map);

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(start,10));
    }
}
