package com.application.crashpad;

import java.util.Date;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FindPropertyParametersFragment extends Fragment
{
	public static final String FORMAT_DATE = "EEE, MMM d, yyyy";
	public static final String DIALOG_DATE = "date";
	private static final int REQUEST_DATE_START = 0;
	private static final int REQUEST_DATE_END = 1;
	
	private int mDistance;
	private Date mDateStart;
	private Date mDateEnd;
	private Button mSearchButton;
	private Button mChangeDateStartButton;
	private Button mChangeDateEndButton;
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
		
		mDateStart = new Date();
		mChangeDateStartButton = (Button)view.findViewById(R.id.start_date_button);
		mChangeDateStartButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DatePickerFragment dialog = DatePickerFragment.newInstance(mDateStart);
				dialog.setTargetFragment(FindPropertyParametersFragment.this, REQUEST_DATE_START);
				dialog.show(fm, DIALOG_DATE);
			}
		});

		mDateEnd = new Date();
		mChangeDateEndButton = (Button)view.findViewById(R.id.end_date_button);
		mChangeDateEndButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DatePickerFragment dialog = DatePickerFragment.newInstance(mDateEnd);
				dialog.setTargetFragment(FindPropertyParametersFragment.this, REQUEST_DATE_END);
				dialog.show(fm, DIALOG_DATE);
			}
		});
		
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
					Toast.makeText(getActivity(), "Having trouble finding location.\nCheck WiFi & GPS.", Toast.LENGTH_LONG).show(); 
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
				i.putExtra(FindPropertyListFragment.EXTRA_PARA_DATE_S, mDateStart);
				i.putExtra(FindPropertyListFragment.EXTRA_PARA_DATE_E, mDateEnd);
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
		
		updateDate();
		
		return view;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode != Activity.RESULT_OK)
		{
			return;
		}
		
		if (requestCode == REQUEST_DATE_START)
		{
			mDateStart = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			updateDate();
		}
		else if (requestCode == REQUEST_DATE_END)
		{
			mDateEnd = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			updateDate();
		}
	}
	
	private void updateDate()
	{
		mChangeDateStartButton.setText(DateFormat.format(FORMAT_DATE, mDateStart));
		mChangeDateEndButton.setText(DateFormat.format(FORMAT_DATE, mDateEnd));
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