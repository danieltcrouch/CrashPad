package com.application.crashpad;

import android.annotation.TargetApi;
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
	private TextView propertyName;
	private TextView propertyInfo;
	
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