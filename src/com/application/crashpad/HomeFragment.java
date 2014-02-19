package com.application.crashpad;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HomeFragment extends Fragment //implements
	//GooglePlayServicesClient.ConnectionCallbacks,
	//GooglePlayServicesClient.OnConnectionFailedListener
{
	public static final String EXTRA_HOME_ID = "com.application.crashpad.home_id";
	
	private Button mFindButton;
	private TextView mCurrentLocationText;
	private Location mCurrentLocation;
	//private LocationClient mLocationClient;
	//private PropertyFinder mPropertyFinder;
	//private ArrayList<Location> mAllLocations;

//    private BroadcastReceiver mLocationReceiver = new LocationReceiver()
//    {
//        @Override
//        protected void onLocationReceived(Context context, Location loc)
//        {
//        	mCurrentLocation = loc;
//        }
//        
//        @Override
//        protected void onProviderEnabledChanged(boolean enabled)
//        {
//            int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;
//            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
//        }
//    };
    
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//mLocationClient = new LocationClient(this, this, this);
		//setRetainInstance(true);
		//mPropertyFinder = PropertyFinder.get(getActivity());
	}
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_home, parent, false);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			if (NavUtils.getParentActivityName(getActivity()) != null)
			{
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
        }

		mFindButton = (Button)v.findViewById(R.id.home_find);
		mCurrentLocationText = (TextView)v.findViewById(R.id.current_location);
		mFindButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (mCurrentLocation != null)
				{
					//mCurrentLocation = mLocationClient.getLastLocation();
					mCurrentLocationText.setText("latitude: " + Double.toString(mCurrentLocation.getLatitude()) +
							"\nlongitude: " + Double.toString(mCurrentLocation.getLongitude()));
					
				}
			}
		});
		
		return v;
	}
	
	@Override
    public void onStart()
	{
        super.onStart();
        //mLocationClient.connect();
    }
	
    @Override
    public void onStop()
    {
        //mLocationClient.disconnect();
        super.onStop();
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode != Activity.RESULT_OK)
		{
			return;
		}
	}
}
