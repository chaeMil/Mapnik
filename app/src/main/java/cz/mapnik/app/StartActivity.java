package cz.mapnik.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import cz.mapnik.app.utils.Map;
import cz.mapnik.app.utils.PlayGames;

import static cz.mapnik.app.utils.Basic.isOnline;

public class StartActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final int REQUEST_ACHIEVEMENTS = 1;
    private static final int REQUEST_LEADERBOARD = 1;
    private Button startButton;
    private Location location;
    private GoogleApiClient mGoogleApiClient;

    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void connectionProblemToast() {
        Toast.makeText(getApplicationContext(), getString(R.string.connection_problem),
                Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

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

        getSupportActionBar().setTitle("");

        // Create the Google Api Client with access to Plus and Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addApi(LocationServices.API)
                        // add other APIs and scopes here as needed
                .build();

        if (isOnline(getApplicationContext())) {
            mGoogleApiClient.connect();
        } else {
            connectionProblemToast();
            finish();
        }

        App.resetCurrentGameOptions();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.bright_green));
        }

        if(App.DEBUG_LOCATION) {
            Location startingPointDebug = new Location("startingPointDebug");
            startingPointDebug.setLatitude(App.DEBUG_LATITUDE);
            startingPointDebug.setLongitude(App.DEBUG_LONGITUDE);
            App.setStartingPoint(startingPointDebug);
        }

        //Log.d("calculateTestScore", String.valueOf(GuessActivity.calculateScore(3, 0, 2914, 45)));

        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDialog(StartActivity.this).show();
            }
        });
    }

    public Dialog startDialog(Activity a) {
        final Dialog dialog = new Dialog(a);
        dialog.setContentView(R.layout.start_dialog);
        dialog.setTitle(getString(R.string.start_game));

        Button myLocationBtn = (Button) dialog.findViewById(R.id.use_my_location);
        Button chooseLocationBtn = (Button) dialog.findViewById(R.id.choose_location);
        Button customLocationBtn = (Button) dialog.findViewById(R.id.custom_location);
        Button verifiedLocationBtn = (Button) dialog.findViewById(R.id.verified_location);

        myLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (location == null) {
                    Toast.makeText(getApplicationContext(),
                            R.string.user_location_not_available,
                            Toast.LENGTH_LONG).show();
                } else {
                    App.setStartingPoint(location);
                    if (App.userAddress == null) {
                        App.userAddress = Map.getAddressFromLatLng(StartActivity.this,
                                location.getLatitude(), location.getLongitude(), 1)
                                .get(0)
                                .getAddressLine(0);
                    }
                    App.CurrentGame.COURSE = "userLocation";
                    Intent i = new Intent(StartActivity.this, ChooseDiameter.class);
                    startActivity(i);
                }
            }
        });

        chooseLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StartActivity.this, SelectCity.class);
                startActivity(i);
            }
        });

        customLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StartActivity.this, ChooseCustomLocation.class);
                startActivity(i);
            }
        });

        verifiedLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StartActivity.this, ChooseVerifiedLocation.class);
                startActivity(i);
            }
        });

        return dialog;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_start, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_achievements:
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),
                            REQUEST_ACHIEVEMENTS);
                } else {
                    connectionProblemToast();
                }
            break;
            case R.id.action_leaderboard:
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

                } else {
                    connectionProblemToast();
                }
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onConnected(Bundle bundle) {
        location = Map.getLastKnownLocation(mGoogleApiClient);
        Games.setViewForPopups(mGoogleApiClient, getWindow().getDecorView()
                .findViewById(android.R.id.content));
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
                /*BaseGameUtils.showActivityResultError(this,
                        requestCode, resultCode, R.string.signin_failure);*/

                PlayGames.signinDisabledByUser(StartActivity.this).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }
}
