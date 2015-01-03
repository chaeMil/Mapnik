package cz.mapnik.app.utils;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.Random;

/**
 * Created by chaemil on 2.1.15.
 */
public class Map {

    public static LatLng getRandomNearbyLocation(double x0, double y0, int radius) {
        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(y0);

        double foundLongitude = new_x + x0;
        double foundLatitude = y + y0;

        Log.d("foundLatitude", String.valueOf(foundLatitude));
        Log.d("foundLongitude", String.valueOf(foundLongitude));

        return new LatLng(foundLatitude, foundLongitude);
        //System.out.println("Longitude: " + foundLongitude + "  Latitude: " + foundLatitude );
    }
}
