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

public class PropertyFragment extends Fragment
{
	public static final String EXTRA_PROP_ID = "com.application.crashpad.property_id";
	
	private Property mProperty;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		UUID propId = (UUID)getArguments().getSerializable(EXTRA_PROP_ID);
		mProperty = PropertyList.get(getActivity()).getProperty(propId);
		
		setHasOptionsMenu(true);
	}
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_property, parent, false);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			if (NavUtils.getParentActivityName(getActivity()) != null)
			{
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
        }
		
		TextView propertyName = (TextView)v.findViewById(R.id.property_name);
		propertyName.setText(mProperty.getName());
		
		Button rentButton = (Button)v.findViewById(R.id.property_rent);
		rentButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				String targetUrl = "https://www.paypal.com/home";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(targetUrl));
				startActivity(i);				
			}
		});
		
		Button contactRenteeButton = (Button)v.findViewById(R.id.contact_renter);
		contactRenteeButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent send = new Intent(Intent.ACTION_SEND);
				send.setType("text/plain");
				final Intent i = Intent.createChooser(send, "Select method of contact");
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
	
	public static PropertyFragment newInstance(UUID propId)
	{
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_PROP_ID, propId);
		
		PropertyFragment fragment = new PropertyFragment();
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
