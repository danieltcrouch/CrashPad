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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HomeFragment extends Fragment
{
	public static final String EXTRA_HOME_ID = "com.application.crashpad.home_id";
	
	private Button mFindPropButton;
	private Button mReviewPropButton;
	private Button mCheckBookingsButton;
	private Button mEditAccountButton;
    
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
		View view = inflater.inflate(R.layout.fragment_home, parent, false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			if (NavUtils.getParentActivityName(getActivity()) != null)
			{
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
        }
		
		
		mFindPropButton = (Button)view.findViewById(R.id.goto_find_property_list_activity);
		mFindPropButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(getActivity(), FindPropertyListActivity.class);
                startActivity(i);
			}
		});
		
		mReviewPropButton = (Button)view.findViewById(R.id.goto_review_property_list_activity);
		mReviewPropButton.setOnClickListener(new View.OnClickListener()
		{			
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(getActivity(), ReviewPropertyListActivity.class);
                startActivity(i);
			}
		});
		
		mCheckBookingsButton = (Button)view.findViewById(R.id.goto_bookings_activity);
		mCheckBookingsButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//
			}
		});
		
		mEditAccountButton = (Button)view.findViewById(R.id.goto_account_activity);
		mEditAccountButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//
			}
		});
		
		return view;
	}
}
