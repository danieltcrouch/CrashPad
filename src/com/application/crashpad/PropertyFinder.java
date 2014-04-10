package com.application.crashpad;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class PropertyFinder
{
    private static final String TAG = "PropertyFinder";
    private static final String TEST_PROVIDER = "TEST_PROVIDER";
    public static final String ACTION_LOCATION = "com.application.crashpad.ACTION_LOCATION";
        
    private static PropertyFinder sRunManager;
    private Context mAppContext;
    private LocationManager mLocationManager;
    
    private PropertyFinder(Context appContext)
    {
        mAppContext = appContext;
        mLocationManager = (LocationManager)mAppContext.getSystemService(Context.LOCATION_SERVICE);
    }
    
    public static PropertyFinder get(Context c)
    {
        if (sRunManager == null)
        {
            sRunManager = new PropertyFinder(c.getApplicationContext());
        }
        return sRunManager;
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate)
    {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
    }

    public void startLocationUpdates()
    {
        String provider = LocationManager.GPS_PROVIDER;
        if (mLocationManager.getProvider(TEST_PROVIDER) != null && 
                mLocationManager.isProviderEnabled(TEST_PROVIDER))
        {
            provider = TEST_PROVIDER;
        }
        Log.d(TAG, "Using provider " + provider);

        Location lastKnown = mLocationManager.getLastKnownLocation(provider);
        if (lastKnown != null)
        {
            lastKnown.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnown);
        }
        
        PendingIntent pi = getLocationPendingIntent(true);
        mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
    }
    
    public void stopLocationUpdates()
    {
        PendingIntent pi = getLocationPendingIntent(false);
        if (pi != null) {
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }
    
    public boolean isTrackingRun()
    {
        return getLocationPendingIntent(false) != null;
    }
    
    private void broadcastLocation(Location location)
    {
        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mAppContext.sendBroadcast(broadcast);
    }
}
