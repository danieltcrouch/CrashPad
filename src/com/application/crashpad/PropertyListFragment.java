package com.application.crashpad;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class PropertyListFragment extends ListFragment
{
    private ArrayList<Property> mProperties;
    private boolean mSubtitleVisible;
    private Button addPropertyButton;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        
        mProperties = PropertyList.get(getActivity()).getProperties();
        PropertyAdapter adapter = new PropertyAdapter(mProperties);
        setListAdapter(adapter);
        setRetainInstance(true);
        mSubtitleVisible = false;        
    }
    
    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
    	View v = super.onCreateView(inflater, parent, savedInstanceState);
        
        return v;
    }

    public void onListItemClick(ListView l, View v, int position, long id)
    {
        Property c = ((PropertyAdapter)getListAdapter()).getItem(position);

        Intent i = new Intent(getActivity(), CrashPadPagerActivity.class);
        i.putExtra(PropertyFragment.EXTRA_PROP_ID, c.getId());
        startActivityForResult(i, 0);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        ((PropertyAdapter)getListAdapter()).notifyDataSetChanged();
    }
    
    private class PropertyAdapter extends ArrayAdapter<Property>
    {
        public PropertyAdapter(ArrayList<Property> Properties)
        {
            super(getActivity(), android.R.layout.simple_list_item_1, Properties);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_property, null);
            }
            
            Property p = getItem(position);

            return convertView;
        }
    }
}