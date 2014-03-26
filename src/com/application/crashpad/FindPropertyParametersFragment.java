package com.application.crashpad;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FindPropertyParametersFragment extends Fragment
{
	public static final String DIALOG_DATE = "date";
	private static final int REQUEST_DATE = 0;
	
	private int mDistance;
	private boolean mLocChanged;
	private Date mDate;
	private Button mSearchButton;
	private Button mChangeDateButton;
	private TextView mDateTextView;
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
					  Toast.makeText(getActivity(), "Unable to find location.", Toast.LENGTH_LONG).show();
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
		
		mDateTextView = (TextView)view.findViewById(R.id.prompt_date);
		mDate = new Date();
		updateDate();
		
		mChangeDateButton = (Button)view.findViewById(R.id.change_date);
		mChangeDateButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DatePickerFragment dialog = DatePickerFragment.newInstance(mDate);
				dialog.setTargetFragment(FindPropertyParametersFragment.this, REQUEST_DATE);
				dialog.show(fm, DIALOG_DATE);
			}
		});
		
		mSearchButton = (Button)view.findViewById(R.id.search);
		mSearchButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
		        Calendar calendar = Calendar.getInstance();
				calendar.setTime(mDate);
				
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
		
		return view;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode != Activity.RESULT_OK)
		{
			return;
		}
		
		if (requestCode == REQUEST_DATE)
		{
			Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			mDate = date;
			updateDate();
		}
	}
	
	private void updateDate()
	{
		SimpleDateFormat dFormat = new SimpleDateFormat("E, MMM. dd, yyyy");
		mDateTextView.setText("Start Date - " + dFormat.format(mDate));
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