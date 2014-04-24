package com.application.crashpad;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeFragment extends Fragment
{
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
		//FIX
		//Divide screen to show renting and leasing privileges
		
		//FIX
		//In this and all others, give back arrow
		View view = inflater.inflate(R.layout.fragment_home, parent, false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			if (NavUtils.getParentActivityName(getActivity()) != null)
			{
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
        }
		
		mFindPropButton = (Button)view.findViewById(R.id.find_properties_button);
		mFindPropButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(getActivity(), FindPropertyParametersActivity.class);
                startActivity(i);
			}
		});
		
		mReviewPropButton = (Button)view.findViewById(R.id.review_properties_button);
		mReviewPropButton.setOnClickListener(new View.OnClickListener()
		{			
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(getActivity(), ReviewPropertyListActivity.class);
                startActivity(i);
			}
		});
		
		mCheckBookingsButton = (Button)view.findViewById(R.id.review_bookings_button);
		mCheckBookingsButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(getActivity(), ReviewBookingsListActivity.class);
				startActivity(i);
			}
		});
		
		mEditAccountButton = (Button)view.findViewById(R.id.edit_account_button);
		mEditAccountButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(getActivity(), EditAccountActivity.class);
				startActivity(i);
			}
		});
		
		return view;
	}
}
