package cz.mapnik.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

import static cz.mapnik.app.utils.Map.getRandomNearbyLocation;


public class GuessActivity extends ActionBarActivity implements OnStreetViewPanoramaReadyCallback
        /*LocationListener*/ {

    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private String provider;
    private TextView latitudeField;
    private TextView longitudeField;
    private boolean hasUserLocation = false;
    private double lat;
    private double lng;
    private StreetViewPanoramaView mSvpView;

   /*protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }*/

    @Override
    public void onStreetViewPanoramaReady(final StreetViewPanorama panorama) {
        if(hasUserLocation) {
            //panorama.setPosition(new LatLng(lat, lng));
            //panorama.setPosition(getRandomNearbyLocation(lng,lat,2000));
            //fetchAnswerData(getApplicationContext(),String.valueOf(lng+","+lat));
            panorama.setPosition(getRandomNearbyLocation(lng, lat, 5000),500);
            panorama.setStreetNamesEnabled(false);
            panorama.setUserNavigationEnabled(false);
            panorama.setOnStreetViewPanoramaChangeListener(new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
                @Override
                public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {
                    Log.d("panoramaLocation", panorama.getLocation().toString());
                }
            });

        }
        //panorama.setPosition(new LatLng(-33.87365, 151.20689));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        latitudeField = (TextView) findViewById(R.id.latitude);
        longitudeField = (TextView) findViewById(R.id.longtitude);

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (!hasUserLocation) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    Log.d("lat", String.valueOf(lat));
                    Log.d("lng", String.valueOf(lng));
                    latitudeField.setText(String.valueOf(lat));
                    longitudeField.setText(String.valueOf(lng));
                    hasUserLocation = true;

                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        locationListener.onLocationChanged(location);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            //onLocationChanged(location);
        } else {
            latitudeField.setText("Location not available");
            longitudeField.setText("Location not available");
        }

        //buildGoogleApiClient();

        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }

    /* Request updates at startup */
    /*@Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }*/

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        //locationManager.removeUpdates(this);
    }

    /*@Override
    public void onLocationChanged(Location location) {
        float lat = (float) location.getLatitude();
        float lng = (float) location.getLongitude();
        Log.d("lat", String.valueOf(lat));
        Log.d("lng", String.valueOf(lng));
        latitudeField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lng));
    }*/

    /*@Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_guess, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            finish();
            Intent i = new Intent(this, GuessActivity.class);
            startActivity(i);
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
