package com.application.crashpad;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReviewBookedPropertyFragment extends Fragment
{
	//FIX
	//public static final String EXTRA_PROP_ID = "com.application.crashpad.property_id";
	public static final String EXTRA_PROP_USER = "com.application.crashpad.property_user";
	public static final String EXTRA_PROP_NAME = "com.application.crashpad.property_name";
	public static final String EXTRA_PROP_DESC = "com.application.crashpad.property_desc";
	public static final String EXTRA_PROP_LONG = "com.application.crashpad.property_long";
	public static final String EXTRA_PROP_LAT = "com.application.crashpad.property_lat";
	public static final String EXTRA_RENT_DAT_S = "com.application.crashpad.rental_date_start";
	public static final String EXTRA_RENT_DAT_E = "com.application.crashpad.rental_date_end";
	public static final String EXTRA_RENT_CODE = "com.application.crashpad.rental_code";

	private Property mProperty;
	private Rental mRental;
	private TextView mPropertyName;
	private TextView mPropertyInfo;
	private TextView mDateRented;
	private TextView mAccessCode;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		//FIX
		//UUID propId = (UUID)getArguments().getSerializable(EXTRA_PROP_ID);
    	//Should add ID attribute to table so that we can actually use the above^
		mProperty = new Property();
		mProperty.setUsername((String)getArguments().getSerializable(EXTRA_PROP_USER));
		mProperty.setName((String)getArguments().getSerializable(EXTRA_PROP_NAME));
		mProperty.setDescription((String)getArguments().getSerializable(EXTRA_PROP_DESC));

		Location loc = new Location(LocationManager.NETWORK_PROVIDER);
		loc.setLongitude((Double)getArguments().getSerializable(EXTRA_PROP_LONG));
		loc.setLatitude((Double)getArguments().getSerializable(EXTRA_PROP_LAT));
		mProperty.setLocation(loc);

		mRental = new Rental();
		mRental.setDateStart((String)getArguments().getSerializable(EXTRA_RENT_DAT_S));
		mRental.setDateEnd((String)getArguments().getSerializable(EXTRA_RENT_DAT_E));
		mRental.setCode((String)getArguments().getSerializable(EXTRA_RENT_CODE));
		mRental.setLocation(loc);
	}
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_property_booked, parent, false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			if (NavUtils.getParentActivityName(getActivity()) != null)
			{
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
        }

		mPropertyName = (TextView)v.findViewById(R.id.property_name);
		mPropertyName.setText(mProperty.getUsername() + "'s " + mProperty.getName());
		
		mPropertyInfo = (TextView)v.findViewById(R.id.property_info);
		mPropertyInfo.setText(mProperty.getDescription());
		
		mDateRented = (TextView)v.findViewById(R.id.dates_rented);
		mDateRented.setText(mRental.getDateStart() + " - " + mRental.getDateEnd());
		
		mAccessCode = (TextView)v.findViewById(R.id.access_code);
		mAccessCode.setText(mRental.presentlyRenting()? mRental.getCode() : "Not Presently Staying");
		
		return v;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode != Activity.RESULT_OK)
		{
			return;
		}
	}
	
	//FIX
	//For all situations like this, try saving in a class PropList, and return based on UUID
	public static ReviewBookedPropertyFragment newInstance(
			String username, String name, String description, Double longitude, Double latitude,
			String dateStart, String dateEnd, String code)
	{
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_PROP_USER, username);
		args.putSerializable(EXTRA_PROP_NAME, name);
		args.putSerializable(EXTRA_PROP_DESC, description);
		args.putSerializable(EXTRA_PROP_LONG, longitude);
		args.putSerializable(EXTRA_PROP_LAT, latitude);
		args.putSerializable(EXTRA_RENT_DAT_S, dateStart);
		args.putSerializable(EXTRA_RENT_DAT_E, dateEnd);
		args.putSerializable(EXTRA_RENT_CODE, code);
		
		ReviewBookedPropertyFragment fragment = new ReviewBookedPropertyFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	//FIX
	//Delete when Done
	/*public static ReviewPropertyFragment newInstance(UUID propId)
	{
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_PROP_ID, propId);
		
		ReviewPropertyFragment fragment = new ReviewPropertyFragment();
		fragment.setArguments(args);
		
		return fragment;
	}*/
}