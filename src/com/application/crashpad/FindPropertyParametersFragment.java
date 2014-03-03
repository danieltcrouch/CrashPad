package com.application.crashpad;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class FindPropertyParametersFragment extends Fragment
{
	private int mDistance;
	private boolean mLocChanged;
	private Button mSearchButton;
	private EditText mDistanceEditText;
	private EditText mLocationEditText;
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
		
		
		mDistance = 10;
		mDistanceEditText = (EditText)view.findViewById(R.id.distance);
		mDistanceEditText.addTextChangedListener(new TextWatcher()
		{
			public void afterTextChanged(Editable s)
			{
				if (isNumeric(mDistanceEditText.getText().toString()))
				{
					mDistance = Integer.parseInt(mDistanceEditText.getText().toString());
				}
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
		});
		
		mLocChanged = false;
		mLocationEditText = (EditText)view.findViewById(R.id.location);
		mLocationEditText.addTextChangedListener(new TextWatcher()
		{
			public void afterTextChanged(Editable s)
			{
				//
				mLocChanged = true;
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
		});
		
		mSearchButton = (Button)view.findViewById(R.id.search);
		mSearchButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(getActivity(), FindPropertyListActivity.class);
		        i.putExtra(FindPropertyListFragment.EXTRA_PARAMETER_LONG, Double.toString(mCurrentLocation.getLongitude()));
		        i.putExtra(FindPropertyListFragment.EXTRA_PARAMETER_LAT, Double.toString(mCurrentLocation.getLatitude()));
		        i.putExtra(FindPropertyListFragment.EXTRA_PARAMETER_DIS, Integer.toString(mDistance));
		        startActivity(i);
			}
		});

		mLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new LocationListener()
		{
		    public void onLocationChanged(Location location)
		    {
		    	if (!mLocChanged)
		    	{
		    		mCurrentLocation = location;
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
	
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
}