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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

//Uses AsyncTask
public class ReviewPropertyListFragment extends ListFragment
{
	private static final String GET_PROPS_URL = "http://taz.harding.edu/~dcrouch1/crashpad/get_props_review.php";
    private static final String TAG_PROPS = "props";
    private static final String TAG_USER = "username";
    private static final String TAG_NAME = "name";
    private static final String TAG_DESC = "description";
    private static final String TAG_ADDR = "address";
    private static final String TAG_LONG = "longitude";
    private static final String TAG_LAT = "latitude";
    private static final String TAG_ID = "id";

	private ArrayList<Property> mPropertyList;
	private JSONArray mProperties;
	private ProgressDialog mProgressDialog;
    private boolean mTaskRunning;

    @TargetApi(11)
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setRetainInstance(true);

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
            	//FIX
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
        i.putExtra(ReviewPropertyFragment.EXTRA_PROP_ID, p.getId());
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
    	private PropertyList mPropertyList;
    	
        public propertyAdapter(ArrayList<Property> properties)
        {
            super(getActivity(), 0, properties);
            mPropertyList = PropertyList.get(getActivity());
            mPropertyList.setProperties(properties);
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
            showProgressDialog();
            mTaskRunning = true;
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

    		//FIX
    		//Order List
			/*Collections.sort(mPropertyList, new Comparator<Property>()
			{
				@Override
				public int compare(Property  prop1, Property  prop2)
				{
					boolean greater = prop1.getProximityToLocation(mLoc) > prop2.getProximityToLocation(mLoc);
					boolean equal = prop1.getProximityToLocation(mLoc) == prop2.getProximityToLocation(mLoc);
					return  greater? 1 : equal? 0 : -1;
				}
			});*/
                        
            propertyAdapter adapter = new propertyAdapter(mPropertyList);
            setListAdapter(adapter);
            setHasOptionsMenu(true);
            setRetainInstance(true);

        	mTaskRunning = false;
            mProgressDialog.dismiss();
        }
    }
    
    public void updateJSONdata()
    {
    	String pUsername = AccountCurrent.get(getActivity()).getPresentAccount().getUsername();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", pUsername));
        
        mPropertyList = new ArrayList<Property>();

        try
        {
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.makeHttpRequest(GET_PROPS_URL, "POST", params);
            mProperties = json.getJSONArray(TAG_PROPS);
            
            for (int i = 0; i < mProperties.length(); i++)
            {
                JSONObject o = mProperties.getJSONObject(i);

                String username = o.getString(TAG_USER);
                String name = o.getString(TAG_NAME);
                String description = o.getString(TAG_DESC);
                String address = o.getString(TAG_ADDR);
                String longitude = o.getString(TAG_LONG);
                String latitude = o.getString(TAG_LAT);
                int id = o.getInt(TAG_ID);
                
                Property p = new Property();
                p.setUsername(username);
                p.setName(name);
                p.setAddress(address);
                p.setDescription(description);
                p.setId(id);
                
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
        catch (Exception e)
        {
        	e.printStackTrace();
        	//FIX
        	//No Toast
        	//return "Network Problems\nCheck WiFi Connection";
        }
    }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState)
	{
        super.onActivityCreated(savedInstanceState);
        if (mTaskRunning)
        {
        	showProgressDialog();
        }
    }
	
	@Override
    public void onDetach()
	{
        if (mProgressDialog != null && mProgressDialog.isShowing())
        {
        	mProgressDialog.dismiss();
        }
        super.onDetach();
    }
	
	private void showProgressDialog()
	{
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setMessage("Loading Properties...");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}
}