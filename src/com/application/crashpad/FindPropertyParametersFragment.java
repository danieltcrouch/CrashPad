package com.application.crashpad;

import java.io.IOException;
import java.util.Calendar;
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
	public static final String FORMAT_DATE = "EEE, MMM d, yy";
	public static final String DIALOG_DATE = "date";
	private static final int REQUEST_DATE_START = 0;
	private static final int REQUEST_DATE_END = 1;
	
	private int mDistance;
	private boolean mLocChanged;
	private Date mDateStart;
	private Date mDateEnd;
	private Button mSearchButton;
	private Button mChangeDateStartButton;
	private Button mChangeDateEndButton;
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
		
		//FIX
		//Only checks for Start Date

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
		
		mLocChanged = false;
		mLocationEditText = (EditText)view.findViewById(R.id.location);
		mLocationEditText.addTextChangedListener(new TextWatcher()
		{
			public void afterTextChanged(Editable s)
			{
				//FIX
				//After you've begun typing, can you get it to default back to myLocation?
				mLocChanged = true;
				Geocoder geocoder = new Geocoder(getActivity());
				String location = mLocationEditText.getText().toString();
				
				try
				{
					List<Address> addresses = geocoder.getFromLocationName(location, 1);
					if (addresses != null && !addresses.isEmpty())
					{
						Address address = addresses.get(0);
						mCurrentLocation.setLatitude(address.getLatitude());
						mCurrentLocation.setLongitude(address.getLongitude());
					}
					else
					{
						Toast.makeText(getActivity(), "Unable to find location.", Toast.LENGTH_SHORT).show();
					}
				}
				catch (IOException e)
				{
					Toast.makeText(getActivity(), "Having trouble finding location...", Toast.LENGTH_LONG).show(); 
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
				//FIX
				//When getting info from EditText on screen, be sure it gets most recent
				//May be fixed here, but check other fragments
				
				mDistance = 10;
				if (isNumeric(mDistanceEditText.getText().toString()))
				{
					mDistance = Integer.parseInt(mDistanceEditText.getText().toString());
				}
				
		        Calendar calendar = Calendar.getInstance();
				calendar.setTime(mDateStart);
				
				Intent i = new Intent(getActivity(), FindPropertyListActivity.class);
		        i.putExtra(FindPropertyListFragment.EXTRA_PARA_LONG, Double.toString(mCurrentLocation.getLongitude()));
		        i.putExtra(FindPropertyListFragment.EXTRA_PARA_LAT, Double.toString(mCurrentLocation.getLatitude()));
		        i.putExtra(FindPropertyListFragment.EXTRA_PARA_DIS, Integer.toString(mDistance));				
		        i.putExtra(FindPropertyListFragment.EXTRA_PARA_DAY, Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
		        i.putExtra(FindPropertyListFragment.EXTRA_PARA_MON, Integer.toString(calendar.get(Calendar.MONTH)));
		        i.putExtra(FindPropertyListFragment.EXTRA_PARA_YEAR, Integer.toString(calendar.get(Calendar.YEAR)));
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