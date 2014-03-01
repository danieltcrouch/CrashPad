package com.application.crashpad;

import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ReviewPropertyFragment extends Fragment
{
	public static final String EXTRA_PROP_ID = "com.application.crashpad.property_id";
	
	private Property mProperty;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		UUID propId = (UUID)getArguments().getSerializable(EXTRA_PROP_ID);
		mProperty = PropertyList.get(getActivity()).getProperty(propId);
		//setHasOptionsMenu(true);
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
		
		TextView propertyName = (TextView)v.findViewById(R.id.property_name);
		propertyName.setText("Your " + mProperty.getName());
		
		Button editButton = (Button)v.findViewById(R.id.property_edit);
		editButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				//			
			}
		});
		
		//TEMP
		TextView propertyInfo = (TextView)v.findViewById(R.id.property_info);
		propertyInfo.setText("Name: " + mProperty.getName() +
				"\nDescription: " + mProperty.getDescription() +
				"\nLocation: " + mProperty.getLocation().getLongitude() + ", " + mProperty.getLocation().getLatitude());
		
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
	
	public static ReviewPropertyFragment newInstance(UUID propId)
	{
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_PROP_ID, propId);
		
		ReviewPropertyFragment fragment = new ReviewPropertyFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
	{
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        } 
	}
}