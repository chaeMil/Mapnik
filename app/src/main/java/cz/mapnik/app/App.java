package cz.mapnik.app;

import android.app.Application;
import android.location.Location;

import java.util.Locale;

/**
 * Created by chaemil on 3.1.15.
 */
public class App extends Application {

    public static boolean DEBUG = true;
    public static double DEBUG_LATITUDE = 50.0897178;
    public static double DEBUG_LONGITUDE = 14.4166699;

    public static int retryCount = 0;
    public static String userAddress = null;
    public static Location startingPoint;

    public static void setStartingPoint(Location startingPoint) {
        App.startingPoint = startingPoint;
    }
}
