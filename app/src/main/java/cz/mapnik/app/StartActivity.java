package cz.mapnik.app;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import cz.mapnik.app.utils.Map;

/**
 * Created by chaemil on 3.1.15.
 */
public class StartActivity extends ActionBarActivity {

    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        Location location = Map.getLastKnownLocation(getApplicationContext());
        App.startingPoint = location;

        if(App.userAddress == null) {
            App.userAddress = Map.getAddressFromLatLng(this, location.getLatitude(),
                    location.getLongitude(),1).get(0).getAddressLine(0);
        }

        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StartActivity.this, GuessActivity.class);
                startActivity(i);
            }
        });

    }
}
