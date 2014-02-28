package com.application.crashpad;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FindPropertyListFragment extends ListFragment
{
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
        
        mProperties = PropertyList.get(getActivity()).getProperties();
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

