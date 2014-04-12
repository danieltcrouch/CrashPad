package com.application.crashpad;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ReviewPropertyFragment extends Fragment
{
	//public static final String EXTRA_PROP_ID = "com.application.crashpad.property_id";
	public static final String EXTRA_PROP_USER = "com.application.crashpad.property_user";
	public static final String EXTRA_PROP_NAME = "com.application.crashpad.property_name";
	public static final String EXTRA_PROP_DESC = "com.application.crashpad.property_desc";
	public static final String EXTRA_PROP_LONG = "com.application.crashpad.property_long";
	public static final String EXTRA_PROP_LAT = "com.application.crashpad.property_lat";

	private Property mProperty;
	private TextView propertyName;
	private TextView propertyInfo;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
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
	}
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_property_review, parent, false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			if (NavUtils.getParentActivityName(getActivity()) != null)
			{
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
        }

		String userName = AccountCurrent.get(getActivity()).getPresentAccount().getUsername();
		propertyName = (TextView)v.findViewById(R.id.property_name);
		propertyName.setText(userName + "'s " + mProperty.getName());
		propertyInfo = (TextView)v.findViewById(R.id.property_info);
		propertyInfo.setText(mProperty.getDescription());
		
		Button editButton = (Button)v.findViewById(R.id.edit_property_button);
		editButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				//TEMP
				String targetUrl = "https://www.google.com";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(targetUrl));
				startActivity(i);		
			}
		});
		
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
	
	//Consider Removing
	//Look at FindPropertyListFragmant
	public static ReviewPropertyFragment newInstance(
			String username, String name, String description, Double longitude, Double latitude)
	{
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_PROP_USER, username);
		args.putSerializable(EXTRA_PROP_NAME, name);
		args.putSerializable(EXTRA_PROP_DESC, description);
		args.putSerializable(EXTRA_PROP_LONG, longitude);
		args.putSerializable(EXTRA_PROP_LAT, latitude);
		
		ReviewPropertyFragment fragment = new ReviewPropertyFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	/*public static ReviewPropertyFragment newInstance(UUID propId)
	{
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_PROP_ID, propId);
		
		ReviewPropertyFragment fragment = new ReviewPropertyFragment();
		fragment.setArguments(args);
		
		return fragment;
	}*/
}