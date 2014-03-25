package com.application.crashpad;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.annotation.TargetApi;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FindPropertyListFragment extends ListFragment
{
	public static final String EXTRA_PARAMETER_LONG = "com.application.crashpad.parameter_loc";
	public static final String EXTRA_PARAMETER_LAT = "com.application.crashpad.parameter_lat";
	public static final String EXTRA_PARAMETER_DIS = "com.application.crashpad.parameter_dis";
	public static final String EXTRA_PARAMETER_DAY = "com.application.crashpad.parameter_day";
	public static final String EXTRA_PARAMETER_MON = "com.application.crashpad.parameter_mon";
	public static final String EXTRA_PARAMETER_YEAR = "com.application.crashpad.parameter_year";
	
	private ArrayList<Property> mProperties;

    @TargetApi(11)
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			if (NavUtils.getParentActivityName(getActivity()) != null)
			{
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
        }

		double longitude = Double.parseDouble(getActivity().getIntent().getStringExtra(EXTRA_PARAMETER_LONG));
		double latitude = Double.parseDouble(getActivity().getIntent().getStringExtra(EXTRA_PARAMETER_LAT));
		int distance = Integer.parseInt(getActivity().getIntent().getStringExtra(EXTRA_PARAMETER_DIS));
		int day = Integer.parseInt(getActivity().getIntent().getStringExtra(EXTRA_PARAMETER_DAY));
		int month = Integer.parseInt(getActivity().getIntent().getStringExtra(EXTRA_PARAMETER_MON));
		int year = Integer.parseInt(getActivity().getIntent().getStringExtra(EXTRA_PARAMETER_YEAR));
		
		Calendar date = new GregorianCalendar(year, month, day);
		Location loc = new Location(LocationManager.NETWORK_PROVIDER);
		loc.setLatitude(latitude);
		loc.setLongitude(longitude);

		mProperties = PropertyListTester.get(getActivity()).getProperties(loc, distance, date.getTime());
		propertyAdapter adapter = new propertyAdapter(mProperties);
		setListAdapter(adapter);
        
        //Use MySQL DB
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    { 
        Property p = ((propertyAdapter)getListAdapter()).getItem(position);
        Intent i = new Intent(getActivity(), FindPropertyActivity.class);
        i.putExtra(FindPropertyFragment.EXTRA_PROP_ID, p.getId());
        startActivity(i);
    }

    private class propertyAdapter extends ArrayAdapter<Property>
    {
        public propertyAdapter(ArrayList<Property> properties)
        {
            super(getActivity(), 0, properties);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_property, null);
            }

            Property p = getItem(position);

            TextView propertyName = (TextView)convertView.findViewById(R.id.property_list_item_name);
            propertyName.setText(p.getName());

            return convertView;
        }
    }
}

