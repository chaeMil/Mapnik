package cz.mapnik.app;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ChooseVerifiedLocation extends Activity {

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

    private void setGameParameters(Double lat, Double lng, String course, int diameter) {
        Location startingLoc = new Location("loc");
        startingLoc.setLatitude(lat);
        startingLoc.setLongitude(lng);
        App.setStartingPoint(startingLoc);
        App.CurrentGame.VERIFIED_COURSE = course;
        App.CurrentGame.CURRENT_DIAMETER = diameter;
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
                    setGameParameters(50.08270, 14.43883, "prague", 10);
                    launchGame(ChooseVerifiedLocation.this);
                    break;
                case R.id.london:
                    setGameParameters(51.50735, -0.12776, "london", 10);
                    launchGame(ChooseVerifiedLocation.this);
                    break;
                case R.id.paris:
                    setGameParameters(48.85661, 2.35222, "paris", 10);
                    launchGame(ChooseVerifiedLocation.this);
                    break;
                case R.id.berlin:
                    setGameParameters(52.52001, 13.40495, "berlin", 10);
                    launchGame(ChooseVerifiedLocation.this);
                    break;
            }

        }
    };

}
