package com.application.crashpad;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
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
	public static final String EXTRA_PROP_ID = "com.application.crashpad.property_id";

	private Property mProperty;
	private TextView mPropertyName;
	private TextView mPropertyAddress;
	private TextView mPropertyDescription;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		PropertyList propertyList;
        propertyList = PropertyList.get(getActivity());
		mProperty = propertyList.getProperty(getActivity().getIntent().getIntExtra(EXTRA_PROP_ID, 0));
	}
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_property_review, parent, false);
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			if (NavUtils.getParentActivityName(getActivity()) != null)
			{
				actionBar.setDisplayHomeAsUpEnabled(true);
			}
        }
		
		//FIX
		//Needs to display Rental info for this property (this will come from the ListFragment)

		mPropertyName = (TextView)v.findViewById(R.id.property_name);
		mPropertyName.setText(mProperty.getName());
		
		mPropertyAddress = (TextView)v.findViewById(R.id.property_address);
		mPropertyAddress.setText(mProperty.getAddress());

		mPropertyDescription = (TextView)v.findViewById(R.id.property_description);
		mPropertyDescription.setText(mProperty.getDescription());
		
		Button editButton = (Button)v.findViewById(R.id.edit_property_button);
		editButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				//FIX
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
}