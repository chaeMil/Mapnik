package cz.mapnik.app;

import android.app.Application;
import android.location.Location;

import java.util.Locale;

/**
 * Created by chaemil on 3.1.15.
 */
public class App extends Application {


    public static boolean DEBUG = false;
    public static boolean DEBUG_LOCATION = false;
    public static double DEBUG_LATITUDE = 50.0897178;
    public static double DEBUG_LONGITUDE = 14.4166699;

    public static int retryCount = 0;
    public static String userAddress = null;
    public static Location startingPoint;

    public static void resetCurrentRoundsScore() {
        CurrentGame.CURRENT_SCORE = 0;
        CurrentGame.CURRENT_ROUND = 1;
    }

    public static class CurrentGame {
        public static int CURRENT_ROUND = 1;
        public static int CURRENT_SCORE = 0;
    }

    public static void setStartingPoint(Location startingPoint) {
        App.startingPoint = startingPoint;
    }
}
