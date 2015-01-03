package cz.mapnik.app;

import android.app.Application;
import android.location.Location;

import java.util.Locale;

/**
 * Created by chaemil on 3.1.15.
 */
public class App extends Application {
    public static int retryCount = 0;
    public static String userAddress = null;
    public static Location startingPoint;
}
