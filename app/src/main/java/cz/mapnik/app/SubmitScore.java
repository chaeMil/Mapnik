package cz.mapnik.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;

import cz.mapnik.app.utils.Map;
import cz.mapnik.app.utils.PlayGames;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static cz.mapnik.app.utils.Basic.connectionProblemToast;

/**
 * Created by chaemil on 16.1.15.
 */
public class SubmitScore extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;
    private TextView courseText;
    private TextView scoreText;
    private Button showLeaderboard;

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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void prepareUi(final String course, final String courseName) {

        Thread showCourseName = new Thread(){
            public void run(){
                try{
                    sleep(300);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            YoYo.with(Techniques.DropOut)
                                    .duration(900)
                                    .playOn(courseText);
                            if(!course.equals(Mapnik.CUSTOM_LOCATION)) {
                                courseText.setText(courseName);
                                courseText.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        };

        Thread showScore = new Thread(){
            public void run(){
                try{
                    sleep(800);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            YoYo.with(Techniques.DropOut)
                                    .duration(900)
                                    .playOn(scoreText);
                            scoreText.setVisibility(View.VISIBLE);
                        }
                    });
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        };

        showCourseName.start();
        showScore.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_score);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.custom_font_regular))
                .setFontAttrId(R.attr.fontPath)
                .build());

        // Create the Google Api Client with access to Plus and Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        mGoogleApiClient.connect();

        Bundle extras = getIntent().getExtras();
        String course =  extras.getString(Mapnik.COURSE);
        String courseName = extras.getString(Mapnik.COURSE_NAME);
        int diameter = extras.getInt(Mapnik.DIAMETER);
        int score = extras.getInt(Mapnik.SCORE);

        courseText = (TextView) findViewById(R.id.courseText);

        scoreText = (TextView) findViewById(R.id.scoreText);
        scoreText.setText(String.valueOf(score));

        prepareUi(course, courseName);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_submit_score, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent i = new Intent(SubmitScore.this, StartActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Bundle extras = getIntent().getExtras();
        int score = extras.getInt(Mapnik.SCORE);

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                finish();
                Intent i = new Intent(SubmitScore.this, StartActivity.class);
                startActivity(i);
            break;
            case R.id.share_score:
                Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_score)
                            + ": " + score);
                    sendIntent.setType("text/plain");
                startActivity(sendIntent);
            break;
            case R.id.action_achievements:
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), 1);
                } else {
                    connectionProblemToast(getApplicationContext());
                }
                break;
            case R.id.action_leaderboard:
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    startActivityForResult(Games.Leaderboards
                            .getAllLeaderboardsIntent(mGoogleApiClient), 1);
                } else {
                    connectionProblemToast(getApplicationContext());
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {

        Bundle extras = getIntent().getExtras();
        final String course =  extras.getString(Mapnik.COURSE);
        int score = extras.getInt(Mapnik.SCORE);
        int diameter = extras.getInt(Mapnik.DIAMETER);

        showLeaderboard = (Button) findViewById(R.id.showLeaderboard);
        showLeaderboard.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInUp)
                .duration(700)
                .playOn(showLeaderboard);

        PlayGames.submitHighScore(mGoogleApiClient,
                getString(R.string.leaderboard_global_high_score), score);

        if(!course.equals(Mapnik.CUSTOM_LOCATION)) {

            PlayGames.submitHighScore(mGoogleApiClient, course, score);

            showLeaderboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                            course), 1);
                }
            });
        } else {
            showLeaderboard.setVisibility(View.GONE);
        }
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

                PlayGames.signinDisabledByUser(SubmitScore.this).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }
}
