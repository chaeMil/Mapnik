package cz.mapnik.app;

import android.app.Application;
import android.location.Location;
import android.util.Log;

/**
 * Created by chaemil on 3.1.15.
 */
public class App extends Application {


    public static boolean DEBUG = false;
    public static boolean LOGGING = true;
    public static boolean DEBUG_LOCATION = false;
    public static double DEBUG_LATITUDE = 50.0897178;
    public static double DEBUG_LONGITUDE = 14.4166699;

    public static int retryCount = 0;
    public static String userAddress = null;
    public static Location startingPoint;

    public static void resetCurrentGameOptions() {
        CurrentGame.CURRENT_SCORE = 0;
        CurrentGame.CURRENT_ROUND = 1;
        CurrentGame.ACTUAL_TIME_BONUS = 1;
        CurrentGame.CURRENT_DIAMETER = 0;
        CurrentGame.CURRENT_GAME_HELPS = 3;
        CurrentGame.GUESSES_IN_ROW = 0;
        CurrentGame.COURSE = null;
        CurrentGame.COURSE_NAME = null;
        App.log("resetCurrentGameOptions","done!");
    }

    public static class CurrentGame {
        public static int CURRENT_ROUND = 1;
        public static int CURRENT_SCORE = 0;
        public static int ACTUAL_TIME_BONUS = 1;
        public static int CURRENT_DIAMETER = 0;
        public static int CURRENT_GAME_HELPS = 3;
        public static int GUESSES_IN_ROW = 0;
        public static String COURSE = null;
        public static String COURSE_NAME = null;
    }

    public static void setStartingPoint(Location startingPoint) {
        App.startingPoint = startingPoint;
    }

    public static void log(String tag, String msg) {
        if (LOGGING) {
            Log.d(tag, msg);
        }
    }
}
