package cz.mapnik.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import cz.mapnik.app.utils.Map;

public class StartActivity extends ActionBarActivity {

    private Button startButton;
    private Location location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

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

    public Dialog startDialog(ActionBarActivity a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setTitle(getString(R.string.start_game))
                .setMessage(getString(R.string.choose_location))
                .setPositiveButton(getString(R.string.select_city),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(StartActivity.this, SelectCity.class);
                        startActivity(i);
                    }
                })
                .setNegativeButton(getString(R.string.use_my_location),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        location = Map.getLastKnownLocation(getApplicationContext());
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
                            Intent i = new Intent(StartActivity.this, GuessActivity.class);
                            startActivity(i);
                        }
                    }
                });
        return builder.create();
    }
}
