package com.application.crashpad;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ReviewPropertyListFragment extends ListFragment
{
	private static final String GET_PROPS_URL = "http://taz.harding.edu/~dcrouch1/crashpad/get_props_review.php";
    private static final String TAG_PROPS = "props";
    private static final String TAG_USER = "username";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADDR = "address";
    private static final String TAG_LONG = "longitude";
    private static final String TAG_LAT = "latitude";

	private ArrayList<Property> mPropertyList;
	private JSONArray mProperties;
	private ProgressDialog pDialog;

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
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.find_prop_list, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_item_new_prop:
            	//TEMP
            	String targetUrl = "https://www.google.com";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(targetUrl));
				startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        Property p = ((propertyAdapter)getListAdapter()).getItem(position);
        Intent i = new Intent(getActivity(), ReviewPropertyActivity.class);
        //i.putExtra(FindPropertyFragment.EXTRA_PROP_ID, p.getId());
        i.putExtra(ReviewPropertyFragment.EXTRA_PROP_USER, p.getUsername());
        i.putExtra(ReviewPropertyFragment.EXTRA_PROP_NAME, p.getName());
        i.putExtra(ReviewPropertyFragment.EXTRA_PROP_DESC, p.getDescription());
        i.putExtra(ReviewPropertyFragment.EXTRA_PROP_LONG, p.getLocation().getLongitude());
        i.putExtra(ReviewPropertyFragment.EXTRA_PROP_LAT, p.getLocation().getLatitude());
        startActivity(i);
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
		new LoadProperties().execute();
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
    
    public class LoadProperties extends AsyncTask<Void, Void, Boolean>
    {
    	@Override
		protected void onPreExecute()
    	{
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Loading Properties...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
    	
        @Override
        protected Boolean doInBackground(Void... arg0)
        {
            updateJSONdata();           
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            super.onPostExecute(result);
                        
            propertyAdapter adapter = new propertyAdapter(mPropertyList);
            setListAdapter(adapter);
            setHasOptionsMenu(true);
            setRetainInstance(true);
            
            pDialog.dismiss();
        }
    }
    
    public void updateJSONdata()
    {
    	String pUsername = PresentAccount.get(getActivity()).getPresentAccount().getName();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", pUsername));
        
        mPropertyList = new ArrayList<Property>();
        JSONParser jParser = new JSONParser();
        JSONObject json = jParser.makeHttpRequest(GET_PROPS_URL, "POST", params);
        //JSONObject json = jParser.getJSONFromUrl(GET_PROPS_URL);

        try
        {
            mProperties = json.getJSONArray(TAG_PROPS);
            for (int i = 0; i < mProperties.length(); i++)
            {
                JSONObject c = mProperties.getJSONObject(i);

                String username = c.getString(TAG_USER);
                String name = c.getString(TAG_NAME);
                String address = c.getString(TAG_ADDR);
                String longitude = c.getString(TAG_LONG);
                String latitude = c.getString(TAG_LAT);
                
                Property p = new Property();
                p.setUsername(username);
                p.setName(name);
                p.setDescription(address);
                
    			Location loc = new Location(LocationManager.NETWORK_PROVIDER);
    			loc.setLongitude(Double.parseDouble(longitude));
    			loc.setLatitude(Double.parseDouble(latitude));
                p.setLocation(loc);
                
                mPropertyList.add(p);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}