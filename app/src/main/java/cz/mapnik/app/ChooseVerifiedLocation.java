package cz.mapnik.app;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ChooseVerifiedLocation extends Activity {

    public static final String PRAGUE_10K_ID = "CgkIu8v476oMEAIQDQ";
    public static final String PRAGUE_10K_NAME = "Prague [10K] Verified!";
    public static final String LONDON_10K_ID = "CgkIu8v476oMEAIQDg";
    public static final String LONDON_10K_NAME = "London [10K] Verified!";
    public static final String PARIS_10K_ID = "CgkIu8v476oMEAIQDw";
    public static final String PARIS_10K_NAME = "Paris [10K] Verified!";
    public static final String BERLIN_10K_ID = "CgkIu8v476oMEAIQEQ";
    public static final String BERLIN_10K_NAME = "Berlin [10K] Verified!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verified_locations);

        ImageView prague = (ImageView) findViewById(R.id.prague);
            prague.setOnClickListener(onClickListener);
        ImageView london = (ImageView) findViewById(R.id.london);
            london.setOnClickListener(onClickListener);
        ImageView paris = (ImageView) findViewById(R.id.paris);
            paris.setOnClickListener(onClickListener);
        ImageView berlin = (ImageView) findViewById(R.id.berlin);
            berlin.setOnClickListener(onClickListener);
    }

    private void setGameParameters(Double lat, Double lng, String course, int diameter,
                                   String courseName) {
        Location startingLoc = new Location("loc");
        startingLoc.setLatitude(lat);
        startingLoc.setLongitude(lng);
        App.setStartingPoint(startingLoc);
        App.CurrentGame.COURSE = course;
        App.CurrentGame.CURRENT_DIAMETER = diameter;
        App.CurrentGame.COURSE_NAME = courseName;
    }

    private void launchGame(Activity a) {
        Intent i = new Intent(a, GuessActivity.class);
        startActivity(i);
        finish();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.prague:
                    setGameParameters(50.08270, 14.43883, PRAGUE_10K_ID, 10000, PRAGUE_10K_NAME);
                    launchGame(ChooseVerifiedLocation.this);
                    break;
                case R.id.london:
                    setGameParameters(51.50735, -0.12776, LONDON_10K_ID, 10000, LONDON_10K_NAME);
                    launchGame(ChooseVerifiedLocation.this);
                    break;
                case R.id.paris:
                    setGameParameters(48.85661, 2.35222, PARIS_10K_ID, 10000, PARIS_10K_NAME);
                    launchGame(ChooseVerifiedLocation.this);
                    break;
                case R.id.berlin:
                    setGameParameters(52.52001, 13.40495, BERLIN_10K_ID, 10000, BERLIN_10K_NAME);
                    launchGame(ChooseVerifiedLocation.this);
                    break;
            }

        }
    };

}
