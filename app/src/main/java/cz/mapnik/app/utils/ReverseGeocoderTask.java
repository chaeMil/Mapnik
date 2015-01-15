package cz.mapnik.app.utils;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.util.List;

// Reverse geocoding may take a long time to return so we put it in AsyncTask.
public class ReverseGeocoderTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "ReverseGeocoder";
    public static interface Callback {
        public void onComplete(String location);
    }
    private Geocoder mGeocoder;
    private double mLat;
    private double mLng;
    private Callback mCallback;
    public ReverseGeocoderTask(Geocoder geocoder, double lat, double lng, Callback callback) {
        mGeocoder = geocoder;
        mLat = lat;
        mLng = lng;
        mCallback = callback;
    }
    @Override
    protected String doInBackground(Void... params) {
        String value = null;
        try {
            List<Address> address =
                    mGeocoder.getFromLocation(mLat, mLng, 1);
            StringBuilder sb = new StringBuilder();
            for (Address addr : address) {
                int index = addr.getMaxAddressLineIndex();
                sb.append(addr.getAddressLine(index));
            }
            value = sb.toString();
        } catch (IOException ex) {
            //value = MenuHelper.EMPTY_STRING;
            Log.e(TAG, "Geocoder exception: ", ex);
        } catch (RuntimeException ex) {
            //value = MenuHelper.EMPTY_STRING;
            Log.e(TAG, "Geocoder exception: ", ex);
        }
        return value;
    }
    @Override
    protected void onPostExecute(String location) {
        mCallback.onComplete(location);
    }
}

