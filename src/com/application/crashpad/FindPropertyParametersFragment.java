package com.application.crashpad;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.Toast;

public class FindPropertyParametersFragment extends Fragment
{	
	private int mDistance;
	private Button mSearchButton;
	private EditText mDistanceEditText;
	private EditText mLocationEditText;
	private Location mCurrentLocation;
	private Location mSearchLocation;
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

		mDistanceEditText = (EditText)view.findViewById(R.id.distance);
		
		mLocationEditText = (EditText)view.findViewById(R.id.location);
		mLocationEditText.addTextChangedListener(new TextWatcher()
		{
			public void afterTextChanged(Editable s)
			{
				Geocoder geocoder = new Geocoder(getActivity());
				String location = mLocationEditText.getText().toString();
				
				try
				{
					List<Address> addresses = geocoder.getFromLocationName(location, 1);
					if (addresses != null && !addresses.isEmpty())
					{
						Address address = addresses.get(0);
						mSearchLocation.setLatitude(address.getLatitude());
						mSearchLocation.setLongitude(address.getLongitude());
					}
					else
					{
						Toast.makeText(getActivity(), "Unable to find location.", Toast.LENGTH_LONG).show();
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					Toast.makeText(getActivity(), "Having trouble finding location.\nCheck GPS.", Toast.LENGTH_SHORT).show();
				}
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
		});
		
		mSearchButton = (Button)view.findViewById(R.id.search_button);
		mSearchButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (mLocationEditText.getText().toString().length() == 0)
				{
					mSearchLocation = mCurrentLocation;
				}
				
				mDistance = 10;
				if (isNumeric(mDistanceEditText.getText().toString()))
				{
					mDistance = Integer.parseInt(mDistanceEditText.getText().toString());
				}
				
				Intent i = new Intent(getActivity(), FindPropertyListActivity.class);
				i.putExtra(FindPropertyListFragment.EXTRA_PARA_LOC, mSearchLocation);
				i.putExtra(FindPropertyListFragment.EXTRA_PARA_DIST, mDistance);
		        startActivity(i);
			}
		});

		mLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new LocationListener()
		{
		    public void onLocationChanged(Location location)
		    {
		    	mCurrentLocation = location;
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}
		    public void onProviderEnabled(String provider) {}
		    public void onProviderDisabled(String provider) {}
		};

		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100000, 10, mLocationListener);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100000, 10, mLocationListener);
		mSearchLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				
		return view;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode != Activity.RESULT_OK)
		{
			return;
		}
	}
	
	public static boolean isNumeric(String str)  
	{  
		try
		{  
			Double.parseDouble(str);  
		}  
		catch(NumberFormatException e)  
		{  
			return false;  
		}
		
		return true;  
	}
}