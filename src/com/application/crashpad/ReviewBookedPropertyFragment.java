package com.application.crashpad;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
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
	public static final String EXTRA_PROP_ID = "com.application.crashpad.property_id";
	public static final String EXTRA_RENT_DAT_S = "com.application.crashpad.rental_date_start";
	public static final String EXTRA_RENT_DAT_E = "com.application.crashpad.rental_date_end";

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
		
		PropertyList propertyList;
        propertyList = PropertyList.get(getActivity());
		mProperty = propertyList.getProperty(getActivity().getIntent().getIntExtra(EXTRA_PROP_ID, 0));

		mRental = new Rental();
		mRental.setDateStart((String)getActivity().getIntent().getSerializableExtra(EXTRA_RENT_DAT_S));
		mRental.setDateEnd((String)getActivity().getIntent().getSerializableExtra(EXTRA_RENT_DAT_E));
		mRental.setPropId(mProperty.getId());
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
		mDateRented.setText(" " + mRental.getDateStart() + " - " + mRental.getDateEnd());
		
		mAccessCode = (TextView)v.findViewById(R.id.access_code);
		mAccessCode.setText(" " + (mRental.presentlyRenting()? mProperty.getCode() : "Not Presently Staying"));
		
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
}