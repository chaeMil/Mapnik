package cz.mapnik.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

import java.util.Arrays;
import java.util.List;

import at.markushi.ui.CircleButton;
import cz.mapnik.app.utils.Basic;
import cz.mapnik.app.utils.Map;

import static cz.mapnik.app.utils.Map.getRandomNearbyLocation;


public class GuessActivity extends ActionBarActivity implements OnStreetViewPanoramaReadyCallback
        /*LocationListener*/ {

    private static final int ANSWER_RADIUS = 1000;
    private static final double WRONG_ANSWER_LATLNG_CORRECTION = 0.08;
    private static final int GUESS_RADIUS = 5000;
    private static final int GUESS_SNAP_RADIUS = GUESS_RADIUS / 10;
    private static final int MAX_RETRY_VALUE = 10;
    private String provider;
    private TextView userLatitude;
    private TextView userLongitude;
    private boolean hasUserLocation = false;
    private double lat;
    private double lng;
    private TextView panoramaLatitude;
    private TextView panoramaLongitude;
    private TextView userAddress;
    private TextView panoramaAddress;
    private TextView panoramaAddress2;
    private static String rightAnswer;
    private String wrongAnswer1;
    private String wrongAnswer2;
    private String[] answers;
    private LatLng wrongAnswer1Location;
    private LatLng wrongAnswer2Location;
    private double panLatitude;
    private double panLongitude;
    private CircleButton guessButton;
    private RelativeLayout debugValues;


    @Override
    public void onStreetViewPanoramaReady(final StreetViewPanorama panorama) {
        if(hasUserLocation) {
            panorama.setPosition(getRandomNearbyLocation(lat, lng, GUESS_RADIUS), GUESS_SNAP_RADIUS);
            panorama.setStreetNamesEnabled(false);
            panorama.setUserNavigationEnabled(false);
            panorama.setOnStreetViewPanoramaChangeListener(new StreetViewPanorama
                    .OnStreetViewPanoramaChangeListener() {
                @Override
                public void onStreetViewPanoramaChange(StreetViewPanoramaLocation
                                                               streetViewPanoramaLocation) {
                    if(App.retryCount > MAX_RETRY_VALUE) {
                        finish();
                        App.retryCount = 0;
                        Toast.makeText(getApplicationContext(),
                                "Error, could not lock on road!\nMaybe there's no Google Street View",
                                Toast.LENGTH_LONG).show();
                    } else {
                        if (panorama.getLocation() == null) {
                            App.retryCount += 1;
                            Log.i("panoramaLocation", "not fixed on road, restarting activity ["
                                    + App.retryCount + "]");
                            finish();
                            Intent i = new Intent(GuessActivity.this, GuessActivity.class);
                            startActivity(i);
                            overridePendingTransition(0, 0);
                        } else {
                            if (App.retryCount > 0) {
                                Toast.makeText(getApplicationContext(),
                                        "not fixed on road, restarting activity [" + App.retryCount + "x]"
                                        , Toast.LENGTH_SHORT).show();
                            }
                            App.retryCount = 0;

                            panLatitude = panorama.getLocation().position.latitude;
                            panLongitude = panorama.getLocation().position.longitude;

                            panoramaLatitude.setText(String.valueOf(panLatitude));
                            panoramaLongitude.setText(String.valueOf(panLongitude));

                            Log.d("panoramaLatitude", String.valueOf(panLatitude));
                            Log.d("panoramaLongitude", String.valueOf(panLongitude));

                            List<Address> panAddress = Map.getAddressFromLatLng(GuessActivity.this,
                                    panLatitude, panLongitude, 1);

                            panoramaAddress.setText(panAddress.get(0).getAddressLine(0));
                            panoramaAddress2.setText(panAddress.get(0).getAddressLine(1));

                            answers = createAnswers(GuessActivity.this, panLatitude, panLongitude,
                                    panAddress.get(0).getAddressLine(0));
                        }
                    }
                }
            });

        }
    }

    public String[] createAnswers(ActionBarActivity a, double panLatitude,
                                        double panLongitude, String rightAnswer) {

        GuessActivity.rightAnswer = rightAnswer;

        wrongAnswer1Location = Map.getRandomNearbyLocation(panLatitude, panLongitude,
                ANSWER_RADIUS);

        wrongAnswer2Location = Map.getRandomNearbyLocation(
                panLatitude + Basic.randDouble(-WRONG_ANSWER_LATLNG_CORRECTION,WRONG_ANSWER_LATLNG_CORRECTION),
                panLongitude  + Basic.randDouble(-WRONG_ANSWER_LATLNG_CORRECTION,WRONG_ANSWER_LATLNG_CORRECTION),
                ANSWER_RADIUS);

        Log.d("wrongAnswer1Location", String.valueOf(wrongAnswer1Location));
        Log.d("wrongAnswer2Location", String.valueOf(wrongAnswer2Location));

        wrongAnswer1 = Map.getAddressFromLatLng(a, wrongAnswer1Location.latitude,
                wrongAnswer1Location.longitude,1).get(0).getAddressLine(0);
        wrongAnswer2 = Map.getAddressFromLatLng(a, wrongAnswer1Location.latitude,
                wrongAnswer2Location.longitude,1).get(0).getAddressLine(0);

        return new String[]{wrongAnswer1,wrongAnswer2,rightAnswer};
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        debugValues = (RelativeLayout) findViewById(R.id.debugValues);

        if(App.DEBUG) {
            debugValues.setVisibility(View.VISIBLE);
        }

        guessButton = (CircleButton) findViewById(R.id.guessButton);
        guessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Basic.shuffleArray(answers);
                Log.d("rightAnswerIndex", String.valueOf(Arrays.asList(answers).indexOf(rightAnswer)));
                int rightAnswerIndex = Arrays.asList(answers).indexOf(rightAnswer);
                createGuessDialog(GuessActivity.this, answers, rightAnswerIndex).show();
            }
        });

        userLatitude = (TextView) findViewById(R.id.userLatitude);
        userLongitude = (TextView) findViewById(R.id.userLongitude);
        userAddress = (TextView) findViewById(R.id.userAddress);

        panoramaLatitude = (TextView) findViewById(R.id.panoramaLatitude);
        panoramaLongitude = (TextView) findViewById(R.id.panoramaLongitude);
        panoramaAddress = (TextView) findViewById(R.id.panoramaAddress);
        panoramaAddress2 = (TextView) findViewById(R.id.panoramaAddress2);

        userAddress.setText(App.userAddress);

        Location location = App.startingPoint;

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (!hasUserLocation) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    Log.d("lat", String.valueOf(lat));
                    Log.d("lng", String.valueOf(lng));
                    userLatitude.setText(String.valueOf(lat));
                    userLongitude.setText(String.valueOf(lng));
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
            userLatitude.setText("Location not available");
            userLongitude.setText("Location not available");
        }


        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }

    public Dialog guessResultDialog(ActionBarActivity a, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setTitle(R.string.guess_result)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nextGuess();
                    }
                });
        return builder.create();
    }

    public Dialog createGuessDialog(ActionBarActivity a, final String[] answers, final int rightAnswerIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setTitle(R.string.guess_location)
                .setSingleChoiceItems(answers, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lv = ((AlertDialog) dialog).getListView();
                        lv.setTag(new Integer(which));
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lv = ((AlertDialog) dialog).getListView();
                        Integer selected = (Integer) lv.getTag();
                        if (selected != null) {

                            boolean right = false;

                            String selectedAnswer = Arrays.asList(answers).get(selected);
                            double distance;
                            Location wrongLoc = new Location("wrongLoc");
                            Location actualLoc = new Location("actualLoc");

                            actualLoc.setLatitude(panLatitude);
                            actualLoc.setLongitude(panLongitude);

                            String message = null;

                            if (selected == rightAnswerIndex || selectedAnswer.equals(rightAnswer)) {
                                /*Toast.makeText(getApplicationContext(), "right answer :)",
                                        Toast.LENGTH_SHORT).show();*/
                                message = getString(R.string.right_answer);
                                right = true;

                            } else if (rightAnswer.contains(selectedAnswer
                                    .replaceAll("\\d", "")                                       //remove digits from address
                                    .replaceAll("\\s+$", ""))                                   //strip spaces
                                    || selectedAnswer.contains(rightAnswer
                                    .replaceAll("\\d", "")                                       //remove digits from address
                                    .replaceAll("\\s+$", ""))) {                                //strip spaces

                                /*Toast.makeText(getApplicationContext(),
                                        "almost right ;)\n right answer is: " + rightAnswer,
                                        Toast.LENGTH_LONG).show();*/
                                message = getString(R.string.almost_right) + " " + rightAnswer;

                                right = false;

                            } else {

                                right = false;
                            }
                            if (!right) {
                                if (Arrays.asList(answers).get(selected).equals(wrongAnswer1)) {
                                    wrongLoc.setLatitude(wrongAnswer1Location.latitude);
                                    wrongLoc.setLongitude(wrongAnswer1Location.longitude);
                                } else {
                                    wrongLoc.setLatitude(wrongAnswer2Location.latitude);
                                    wrongLoc.setLongitude(wrongAnswer2Location.longitude);
                                }
                                distance = actualLoc.distanceTo(wrongLoc);

                                if (distance <= 500) {
                                    /*Toast.makeText(getApplicationContext(),
                                            "almost right ;)\nright answer is: " + rightAnswer,
                                            Toast.LENGTH_LONG).show();*/

                                    message = getString(R.string.almost_right) + " " + rightAnswer;
                                } else {
                                    /*Toast.makeText(getApplicationContext(),
                                            "wrong answer :(\nright answer is: " + rightAnswer,
                                            Toast.LENGTH_LONG).show();*/

                                    message = getString(R.string.wrong_answer) + " " + rightAnswer;
                                }

                                /*Toast.makeText(getApplicationContext(),
                                        getString(R.string.guess_distance) +
                                                " " + String.valueOf((long) Math.floor(distance + 0.5d)) + "m",
                                        Toast.LENGTH_LONG).show();*/

                                message += "\n" + getString(R.string.guess_distance) + " " +
                                        String.valueOf((long) Math.floor(distance + 0.5d)) + "m";


                                Log.d("distance between guess and actual location: ",
                                        String.valueOf(distance));

                            }

                            guessResultDialog(GuessActivity.this, message).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    public void nextGuess() {
        finish();
        Intent i = new Intent(this, GuessActivity.class);
        startActivity(i);
        overridePendingTransition(0,0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //locationManager.removeUpdates(this);
    }


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

        switch(id) {
            /*case R.id.action_refresh:
                nextGuess();
            break;*/
            /*case R.id.action_guess:
                Basic.shuffleArray(answers);
                Log.d("rightAnswerIndex", String.valueOf(Arrays.asList(answers).indexOf(rightAnswer)));
                int rightAnswerIndex = Arrays.asList(answers).indexOf(rightAnswer);
                createGuessDialog(GuessActivity.this, answers, rightAnswerIndex).show();
            break;*/
        }

        return super.onOptionsItemSelected(item);
    }
}
