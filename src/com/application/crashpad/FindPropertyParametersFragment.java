package com.application.crashpad;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FindPropertyParametersFragment extends Fragment
{
	public static final String EXTRA_PARAMETERS_ID = "com.application.crashpad.parameters_id";
	
	private Button mSearchButton;
	private TextView mCurrentLocationText;
	private Location mCurrentLocation;
	private LocationManager mLocationManager;
	private LocationListener mLocationListener;
    
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_parameters, parent, false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			if (NavUtils.getParentActivityName(getActivity()) != null)
			{
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
        }

		
		mSearchButton = (Button)view.findViewById(R.id.search);
		mSearchButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final FragmentTransaction ft = getFragmentManager().beginTransaction(); 
				ft.replace(R.id.fragmentContainer, new FindPropertyListFragment())
					.addToBackStack(null).commit();
			}
		});

		mCurrentLocationText = (TextView)view.findViewById(R.id.current_location);
		mLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new LocationListener()
		{
		    public void onLocationChanged(Location location)
		    {
		    	mCurrentLocation = location;

				//TEMP
				if (mCurrentLocation != null)
				{
					mCurrentLocationText.setText("latitude: " + Double.toString(mCurrentLocation.getLatitude()) +
							"\nlongitude: " + Double.toString(mCurrentLocation.getLongitude()));
				}
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}
		    public void onProviderEnabled(String provider) {}
		    public void onProviderDisabled(String provider) {}
		};

		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100000, 10, mLocationListener);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100000, 10, mLocationListener);
		
		return view;
	}
}