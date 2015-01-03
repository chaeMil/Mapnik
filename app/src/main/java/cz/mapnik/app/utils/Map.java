package cz.mapnik.app.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by chaemil on 2.1.15.
 */
public class Map {

    public static LatLng getRandomNearbyLocation(double latitude, double longitude, int radius) {
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
        double new_x = x / Math.cos(latitude);

        double foundLongitude = new_x + longitude;
        double foundLatitude = y + latitude;

        //Log.d("foundLatitude", String.valueOf(foundLatitude));
        //Log.d("foundLongitude", String.valueOf(foundLongitude));

        return new LatLng(foundLatitude, foundLongitude);
        //System.out.println("Longitude: " + foundLongitude + "  Latitude: " + foundLatitude );
    }

    public static List<Address> getAddressFromLatLng(Activity a, double lat, double lng,
                                                     int numberOfLocations) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(a, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat,lng,numberOfLocations);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    public static Location getLastKnownLocation(Context c) {
        // Get the location manager
        LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider -> use
        // default
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        return locationManager.getLastKnownLocation(provider);
    }
}
