package cz.mapnik.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.filippudak.ProgressPieView.ProgressPieView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import at.markushi.ui.CircleButton;
import cz.mapnik.app.utils.Basic;
import cz.mapnik.app.utils.Map;
import cz.mapnik.app.utils.PlayGames;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static cz.mapnik.app.utils.Map.getRandomNearbyLocation;


public class GuessActivity extends ActionBarActivity implements OnStreetViewPanoramaReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static final int ANSWER_RADIUS = 1000;
    private static final double WRONG_ANSWER_LATLNG_CORRECTION = 0.08;
    private static final int GUESS_RADIUS = App.CurrentGame.CURRENT_DIAMETER;
    //private static final int GUESS_SNAP_RADIUS = GUESS_RADIUS / 10;
    private static final int GUESS_SNAP_RADIUS = 500;
    private static final int MAX_RETRY_VALUE = 5;
    private static final int GAME_MAX_ROUNDS = 10;
    private static final int TIME_BONUS_COUNTDOWN_SECONDS = 30;
    private static final double TIME_BONUS_MAX = 4.0;
    private static final double TIME_BONUS_VALUE = 500;
    private static int COUNTDOWN_TIME;

    private static final String UNNAMED_RD = "Unnamed Rd";

    private static GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 9001;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;

    private StreetViewPanoramaFragment streetViewPanoramaFragment;
    private String provider = null;
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
    private String[] answers = null;
    private LatLng wrongAnswer1Location;
    private LatLng wrongAnswer2Location;
    private double panLatitude;
    private double panLongitude;
    private CircleButton guessButton;
    private RelativeLayout debugValues;
    private ProgressPieView countdown;
    private static MyCount timer;
    private RelativeLayout timeBonusWrapper;
    private CircleButton helpButton;
    private RelativeLayout helpsWrapper;
    private TextView helpsText;
    private ProgressWheel progressBar;
    private Geocoder geocoder;
    private List<Address> panAddress;
    private RelativeLayout downloadingAnswersWrapper;
    private TextView courseName;
    private int currentGuessRadius;

    public static interface Callback {
        public void onComplete(String[] answers);
    }

    // Reverse geocoding may take a long time to return so we put it in AsyncTask.
    public class ReverseGeocoderTask extends AsyncTask<Void, Void, String[]> {

        private static final String TAG = "ReverseGeocoder";
        private Geocoder mGeocoder;
        private double mLat;
        private double mLng;
        private Callback mCallback;
        public ReverseGeocoderTask(Geocoder geocoder, double lat, double lng, Callback callback) {
            mGeocoder = geocoder;
            mLat = lat;
            mLng = lng;
            mCallback = callback;
        }
        @Override
        protected String[] doInBackground(Void... params) {
            String[] value = null;

            if (!mGeocoder.isPresent()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext())
                        .setTitle(getString(R.string.geocoder_error))
                        .setMessage(getString(R.string.geocoder_is_not_present))
                        .setCancelable(true);
                builder.create().show();
            }
            try {
                GuessActivity.rightAnswer = mGeocoder.getFromLocation(mLat, mLng, 1)
                        .get(0).getAddressLine(0);

                wrongAnswer1Location = Map.getRandomNearbyLocation(panLatitude, panLongitude,
                        ANSWER_RADIUS);

                wrongAnswer2Location = Map.getRandomNearbyLocation(
                        panLatitude + Basic.randDouble(-WRONG_ANSWER_LATLNG_CORRECTION,
                                WRONG_ANSWER_LATLNG_CORRECTION),
                        panLongitude + Basic.randDouble(-WRONG_ANSWER_LATLNG_CORRECTION,
                                WRONG_ANSWER_LATLNG_CORRECTION),
                        ANSWER_RADIUS);

                App.log("wrongAnswer1Location", String.valueOf(wrongAnswer1Location));
                App.log("wrongAnswer2Location", String.valueOf(wrongAnswer2Location));

                wrongAnswer1 = mGeocoder.getFromLocation(wrongAnswer1Location.latitude,
                        wrongAnswer1Location.longitude, 1).get(0).getAddressLine(0);
                wrongAnswer2 = mGeocoder.getFromLocation(wrongAnswer2Location.latitude,
                        wrongAnswer2Location.longitude, 1).get(0).getAddressLine(0);

                if (wrongAnswer1.equals(UNNAMED_RD)) {
                    wrongAnswer1 = getString(R.string.unnamed_rd);
                }
                if (wrongAnswer2.equals(UNNAMED_RD)) {
                    wrongAnswer2 = getString(R.string.unnamed_rd);
                }

                return new String[]{wrongAnswer1, wrongAnswer2, rightAnswer};

            }catch(IOException | RuntimeException ex){

                Log.e(TAG, "Geocoder exception: ", ex);
            }

            return value;
        }
        @Override
        protected void onPostExecute(String[] answers) {
            mCallback.onComplete(answers);
        }
    }

    @Override
    public void onStreetViewPanoramaReady(final StreetViewPanorama panorama) {
        if(hasUserLocation) {

            if (App.CurrentGame.COURSE.equals(getString(R.string.leaderboard_3k_player_location))
                || App.CurrentGame.COURSE.equals(getString(R.string.leaderboard_5k_player_location))
                || App.CurrentGame.COURSE.equals(getString(R.string.leaderboard_10k_player_location))) {
                currentGuessRadius = GUESS_RADIUS / 10 * App.CurrentGame.CURRENT_ROUND;
            }
            else {
                currentGuessRadius = GUESS_RADIUS;
            }

            panorama.setPosition(getRandomNearbyLocation(lat, lng, currentGuessRadius), GUESS_SNAP_RADIUS);
            App.log("guessRadius", String.valueOf(currentGuessRadius)+"m");
            panorama.setStreetNamesEnabled(false);
            panorama.setUserNavigationEnabled(false);
            panorama.setPanningGesturesEnabled(false); //before panorama is ready
            panorama.setZoomGesturesEnabled(false); //before panorama is ready
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
                            App.log("panoramaLocation", "not fixed on road, restarting activity ["
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

                            App.CurrentGame.CURRENT_ROUND += 1;

                            App.retryCount = 0;

                            panLatitude = panorama.getLocation().position.latitude;
                            panLongitude = panorama.getLocation().position.longitude;

                            ReverseGeocoderTask getPanAddressAndCreateAnswers = new ReverseGeocoderTask(
                                    geocoder, panLatitude, panLongitude, new Callback() {
                                @Override
                                public void onComplete(String[] answers) {
                                    createAnswers(answers);
                                    prepareUI(panorama);
                                }
                            });
                            getPanAddressAndCreateAnswers.execute();


                            if (App.DEBUG) {
                                panoramaLatitude.setText(String.valueOf(panLatitude));
                                panoramaLongitude.setText(String.valueOf(panLongitude));

                                App.log("panoramaLatitude", String.valueOf(panLatitude));
                                App.log("panoramaLongitude", String.valueOf(panLongitude));

                                panoramaAddress.setText(panAddress.get(0).getAddressLine(0));
                                panoramaAddress2.setText(panAddress.get(0).getAddressLine(1));
                            }
                        }
                    }
                }
            });

        }
    }

    public void prepareUI(StreetViewPanorama panorama) {
        guessButton.setVisibility(View.VISIBLE);
        if(App.CurrentGame.CURRENT_GAME_HELPS > 0) {
            helpsWrapper.setVisibility(View.VISIBLE);
            helpsText.setText(String.valueOf(App.CurrentGame.CURRENT_GAME_HELPS));
        }
        timeBonusWrapper.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        panorama.setPanningGesturesEnabled(true);
        panorama.setZoomGesturesEnabled(true);

        courseName.setText(App.CurrentGame.COURSE_NAME);
        courseName.setVisibility(View.VISIBLE);

        COUNTDOWN_TIME = TIME_BONUS_COUNTDOWN_SECONDS;

        timer = new MyCount(TIME_BONUS_COUNTDOWN_SECONDS * 1000, 1000);
        timer.start();
    }

    public void createAnswers(String[] a) {
        answers = a;
    }

    public static int calculateScore(int validity, int metersFromActualLocation,
                              int metersFromPlayerPosition, int timeBonus) {

        double bonus;

        if (validity >= 2) {
            if (timeBonus <= 1) {
                bonus = 0;
            } else {
                bonus = ((300.0 / (double) TIME_BONUS_COUNTDOWN_SECONDS) * (double) timeBonus) * 0.02;
                if (bonus > TIME_BONUS_MAX) {
                    bonus = TIME_BONUS_MAX;
                }
            }
        }
        else {
            bonus = 0;
        }

        double bonusValue = bonus * TIME_BONUS_VALUE;

        App.CurrentGame.ACTUAL_TIME_BONUS = (int) bonusValue;
        App.log("bonus", String.valueOf(bonusValue));

        double score = (double) validity * ((double) metersFromPlayerPosition
                - (double) metersFromActualLocation) + bonusValue;

        return (int) score;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.custom_font_regular))
                .setFontAttrId(R.attr.fontPath)
                .build());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < 21) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // create our manager instance after the content view is set
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setTintColor(getResources().getColor(R.color.bright_green));
        }


        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.bright_green));
        }

        // Create the Google Api Client with access to Plus and Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                        // add other APIs and scopes here as needed
                .build();

        mGoogleApiClient.connect();


        streetViewPanoramaFragment = (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        debugValues = (RelativeLayout) findViewById(R.id.debugValues);
        progressBar = (ProgressWheel) findViewById(R.id.progressBar);
        guessButton = (CircleButton) findViewById(R.id.guessButton);
        helpButton = (CircleButton) findViewById(R.id.helpButton);
        helpsWrapper = (RelativeLayout) findViewById(R.id.helpsWrapper);
        helpsText = (TextView) findViewById(R.id.helpsText);
        userLatitude = (TextView) findViewById(R.id.userLatitude);
        userLongitude = (TextView) findViewById(R.id.userLongitude);
        userAddress = (TextView) findViewById(R.id.userAddress);
        panoramaLatitude = (TextView) findViewById(R.id.panoramaLatitude);
        panoramaLongitude = (TextView) findViewById(R.id.panoramaLongitude);
        panoramaAddress = (TextView) findViewById(R.id.panoramaAddress);
        panoramaAddress2 = (TextView) findViewById(R.id.panoramaAddress2);
        countdown = (ProgressPieView) findViewById(R.id.countdown);
        timeBonusWrapper = (RelativeLayout) findViewById(R.id.timeBonusWrapper);
        downloadingAnswersWrapper = (RelativeLayout) findViewById(R.id.downloading_answers_wrapper);
        courseName = (TextView) findViewById(R.id.courseName);

        geocoder = new Geocoder(getApplicationContext());

        Location location = App.startingPoint;

        if(App.DEBUG) {
            debugValues.setVisibility(View.VISIBLE);
            userAddress.setText(App.userAddress);
        }


        guessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answers != null) {
                    displayGuessDialog(answers);
                } else {
                    App.log("ReverseGeocoderTask was unable to get answers: ", "downloading again");

                    downloadingAnswersWrapper.setVisibility(View.VISIBLE);

                    ReverseGeocoderTask getAnswers = new ReverseGeocoderTask(geocoder, panLatitude,
                            panLongitude, new Callback() {
                        @Override
                        public void onComplete(String[] answers) {
                            createAnswers(answers);
                            downloadingAnswersWrapper.setVisibility(View.GONE);
                            if (answers != null) {
                                displayGuessDialog(answers);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.unable_to_download_answers),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    getAnswers.execute();
                }
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimer();
                App.CurrentGame.CURRENT_GAME_HELPS -= 1;
                if (timer != null) {
                    timer.onFinish();
                }
                helpsWrapper.setVisibility(View.GONE);
                Intent i = new Intent(GuessActivity.this, MapHelpActivity.class);
                i.putExtra("locLatitude",panLatitude);
                i.putExtra("locLongitude",panLongitude);
                startActivity(i);
            }
        });

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (!hasUserLocation) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    App.log("lat", String.valueOf(lat));
                    App.log("lng", String.valueOf(lng));
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
    }

    public void displayGuessDialog(String[] answers) {
        Basic.shuffleArray(answers);
        App.log("rightAnswerIndex", String.valueOf(Arrays.asList(answers).indexOf(rightAnswer)));
        int rightAnswerIndex = Arrays.asList(answers).indexOf(rightAnswer);
        createGuessDialog(GuessActivity.this, answers, rightAnswerIndex).show();
    }

    public Dialog exitDialog(ActionBarActivity a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setMessage(R.string.exit_game)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        App.resetCurrentGameOptions();
                        stopTimer();
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    public Dialog guessResultDialog(ActionBarActivity a, String message, final boolean rightAnswer,
                                    final String rightLocation,
                                    final double guessLatitude, final double guessLongitude) {

        if(App.CurrentGame.ACTUAL_TIME_BONUS > 1) {
            Toast.makeText(getApplicationContext(), getString(R.string.time_bonus).toUpperCase() +
                    " +" + App.CurrentGame.ACTUAL_TIME_BONUS,
                    Toast.LENGTH_SHORT).show();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setTitle(R.string.guess_result)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nextGuess(GuessActivity.this);
                    }
                })
                .setNeutralButton(getString(R.string.show_on_map), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(GuessActivity.this, ShowOnMap.class);
                        i.putExtra("rightAnswer", rightAnswer);
                        i.putExtra("locLatitude", panLatitude);
                        i.putExtra("locLongitude", panLongitude);
                        i.putExtra("guessLatitude", guessLatitude);
                        i.putExtra("guessLongitude", guessLongitude);
                        i.putExtra("rightLocation", rightLocation);
                        startActivity(i);
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

                            boolean isAnswerRight = false;
                            int validityOfAnswer = 0;

                            double distanceFromGuess = 0;

                            String selectedAnswer = Arrays.asList(answers).get(selected);
                            double distance = 0;
                            Location wrongLoc = new Location("wrongLoc");
                            Location actualLoc = new Location("actualLoc");
                            Location guessLocation = null;

                            actualLoc.setLatitude(panLatitude);
                            actualLoc.setLongitude(panLongitude);

                            String message = null;

                            if (selected == rightAnswerIndex || selectedAnswer.equals(rightAnswer)) {
                                message = getString(R.string.right_answer);
                                isAnswerRight = true;
                                validityOfAnswer = 3;
                                guessLocation = actualLoc;

                            } else if (rightAnswer.contains(selectedAnswer
                                    .replaceAll("\\d", "")                                       //remove digits from address
                                    .replaceAll("\\s+$", ""))                                   //strip spaces
                                    || selectedAnswer.contains(rightAnswer
                                    .replaceAll("\\d", "")                                       //remove digits from address
                                    .replaceAll("\\s+$", ""))) {                                //strip spaces

                                message = getString(R.string.almost_right) + " " + rightAnswer;

                                isAnswerRight = false;
                                validityOfAnswer = 2;
                                guessLocation = actualLoc;

                            } else {

                                if (Arrays.asList(answers).get(selected).equals(wrongAnswer1)) {
                                    wrongLoc.setLatitude(wrongAnswer1Location.latitude);
                                    wrongLoc.setLongitude(wrongAnswer1Location.longitude);
                                } else {
                                    wrongLoc.setLatitude(wrongAnswer2Location.latitude);
                                    wrongLoc.setLongitude(wrongAnswer2Location.longitude);
                                }

                                guessLocation = wrongLoc;

                                distance = actualLoc.distanceTo(wrongLoc);
                                distanceFromGuess = Math.floor(distance + 0.5d);

                                if (distance > 500) {
                                    message = getString(R.string.wrong_answer) + " " + rightAnswer;
                                    validityOfAnswer = 0;
                                    isAnswerRight = false;
                                } else {
                                    message = getString(R.string.almost_right) + " " + rightAnswer;
                                    validityOfAnswer = 1;
                                    isAnswerRight = false;
                                }
                            }
                            if (!isAnswerRight) {

                                message += "\n\n" + getString(R.string.guess_distance) + " " +
                                        String.valueOf((long) distanceFromGuess) + "m";

                                App.CurrentGame.GUESSES_IN_ROW = 0;
                            } else {
                                App.CurrentGame.GUESSES_IN_ROW += 1;
                            }

                            double metersFromPlayerPosition = App.startingPoint
                                    .distanceTo(guessLocation);

                            int addScore = calculateScore(validityOfAnswer,
                                    (int) distanceFromGuess,
                                    (int) metersFromPlayerPosition, COUNTDOWN_TIME);

                            App.CurrentGame.CURRENT_SCORE += addScore;

                            App.log("addingToScore",
                                    "validity: " + String.valueOf(validityOfAnswer)
                                            + " distanceFromGuess: " + String.valueOf((int) distanceFromGuess)
                                            + " metersFromPlayerPosition: " + String.valueOf((int) metersFromPlayerPosition)
                                            + " timeBonus: " + String.valueOf(COUNTDOWN_TIME)
                                            + " score: " + String.valueOf(addScore));

                            stopTimer();

                            guessResultDialog(GuessActivity.this, message, isAnswerRight, rightAnswer,
                                    wrongLoc.getLatitude(), wrongLoc.getLongitude()).show();
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

    public static void nextGuess(Activity a) {
        a.finish();
        stopTimer();
        Intent i = new Intent(a, GuessActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (App.CurrentGame.CURRENT_ROUND < GAME_MAX_ROUNDS + 1) {
            a.startActivity(i);
            a.overridePendingTransition(R.animator.card_in, R.animator.card_out);
        }
        else {
            App.log("finished game with score:", String.valueOf(App.CurrentGame.CURRENT_SCORE));

            Intent submitScore = new Intent(a, SubmitScore.class);
                submitScore.putExtra("score", App.CurrentGame.CURRENT_SCORE);
                submitScore.putExtra("course", App.CurrentGame.COURSE);
                submitScore.putExtra("diameter", App.CurrentGame.CURRENT_DIAMETER);
                submitScore.putExtra("courseName", App.CurrentGame.COURSE_NAME);

            a.startActivity(submitScore);

            App.resetCurrentGameOptions();
            a.finish();
        }
    }

    @Override
    public void onBackPressed() {
        exitDialog(GuessActivity.this).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_guess, menu);

        //show score
        TextView score = new TextView(getApplicationContext());
            score.setText((getString(R.string.score) + " " + App.CurrentGame.CURRENT_SCORE)
                    .toUpperCase());
            score.setPadding(0,0,Basic.dpToPx(getApplicationContext(),15),0);
            score.setTextColor(getResources().getColor(android.R.color.white));
            score.setTypeface(null, Typeface.BOLD);
            score.setShadowLayer(4,0,0,getResources().getColor(android.R.color.black));

        menu.add(0,0,1,R.string.score).setActionView(score)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        //show current round
        TextView rounds = new TextView(getApplicationContext());
            rounds.setText((getString(R.string.rounds) + " "
                    + App.CurrentGame.CURRENT_ROUND + " / " + GAME_MAX_ROUNDS).toUpperCase());
            rounds.setPadding(0,0,Basic.dpToPx(getApplicationContext(),15),0);
            rounds.setTextColor(getResources().getColor(android.R.color.white));
            rounds.setShadowLayer(4,0,0,getResources().getColor(android.R.color.black));

        menu.add(0,0,1,R.string.rounds).setActionView(rounds)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);



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

    public static void stopTimer() {
        COUNTDOWN_TIME = 1;
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Games.setViewForPopups(mGoogleApiClient, getWindow().getDecorView()
                .findViewById(android.R.id.content));

        //achievements

        if (App.CurrentGame.GUESSES_IN_ROW == 3) {
            PlayGames.unlockAchievement(mGoogleApiClient,
                    getString(R.string.achievement_3_in_a_row));
        }

        if (App.CurrentGame.GUESSES_IN_ROW == 5) {
            PlayGames.unlockAchievement(mGoogleApiClient,
                    getString(R.string.achievement_5_in_a_row));
        }

        //achievements end

        App.log("guesses in row: ", String.valueOf(App.CurrentGame.GUESSES_IN_ROW));
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                PlayGames.signinDisabledByUser(GuessActivity.this).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInflow) {
            mAutoStartSignInflow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }

        // Put code here to display the sign-in button
    }

    private class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            Toast.makeText(getApplicationContext(), getString(R.string.time_bonus_is_out),
                    Toast.LENGTH_LONG).show();
            timeBonusWrapper.setVisibility(View.GONE);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            COUNTDOWN_TIME -= 1;
            double progress = (100.0 / (double) TIME_BONUS_COUNTDOWN_SECONDS) * (double) COUNTDOWN_TIME;
            countdown.setProgress((int) progress);
            countdown.setText(String.valueOf(COUNTDOWN_TIME));
        }
    }
}
