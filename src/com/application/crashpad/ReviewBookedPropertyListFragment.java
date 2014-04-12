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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

//Uses AsyncTask
public class ReviewBookedPropertyListFragment extends ListFragment
{
	private static final String GET_PROPS_B_URL = "http://taz.harding.edu/~dcrouch1/crashpad/get_props_booked.php";
    private static final String TAG_PROPS = "props";
    private static final String TAG_RENTS = "rentals";
    private static final String TAG_USER = "username";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADDR = "address";
    private static final String TAG_LONG = "longitude";
    private static final String TAG_LAT = "latitude";
    private static final String TAG_DAT_S = "dateStart";
    private static final String TAG_DAT_E= "dateEnd";
    private static final String TAG_CODE = "code";

	private ArrayList<Property> mPropertyList;
	private ArrayList<Rental> mRentalList;
	private JSONArray mProperties;
	private JSONArray mRentals;
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
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        Property p = ((propertyAdapter)getListAdapter()).getItem(position);
        //FIX
        //Is this the best place and way to do this?
        Rental r = mRentalList.get(0);
        for (int i = 0; i < mRentalList.size(); i++)
        {
        	if (p.getProximityToLocation(mRentalList.get(i).getLocation()) < .00001)
        	{
        		r = mRentalList.get(i);
        	}
        }

        Intent i = new Intent(getActivity(), ReviewBookedPropertyActivity.class);
        //FIX
        //i.putExtra(FindPropertyFragment.EXTRA_PROP_ID, p.getId());
        i.putExtra(ReviewBookedPropertyFragment.EXTRA_PROP_USER, p.getUsername());
        i.putExtra(ReviewBookedPropertyFragment.EXTRA_PROP_NAME, p.getName());
        i.putExtra(ReviewBookedPropertyFragment.EXTRA_PROP_DESC, p.getDescription());
        i.putExtra(ReviewBookedPropertyFragment.EXTRA_PROP_LONG, p.getLocation().getLongitude());
        i.putExtra(ReviewBookedPropertyFragment.EXTRA_PROP_LAT, p.getLocation().getLatitude());
        i.putExtra(ReviewBookedPropertyFragment.EXTRA_RENT_DAT_S, r.getDateStart());
        i.putExtra(ReviewBookedPropertyFragment.EXTRA_RENT_DAT_E, r.getDateEnd());
        i.putExtra(ReviewBookedPropertyFragment.EXTRA_RENT_CODE, r.getCode());
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
            showProgressDialog();
            mTaskRunning = true;
		}
    	
        @Override
        protected Boolean doInBackground(Void... arg0)
        {
        	//FIX
        	//Is the separation necessary?
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

        	mTaskRunning = false;
            mProgressDialog.dismiss();
        }
    }
    
    public void updateJSONdata()
    {
    	//FIX
    	//In other non-list AsyncTasks, params are inside try/catch
    	String pUsername = AccountCurrent.get(getActivity()).getPresentAccount().getUsername();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", pUsername));
        
        mPropertyList = new ArrayList<Property>();
        mRentalList = new ArrayList<Rental>();
        JSONParser jParser = new JSONParser();
        JSONObject json = jParser.makeHttpRequest(GET_PROPS_B_URL, "POST", params);

        try
        {
            mRentals = json.getJSONArray(TAG_RENTS);
            for (int i = 0; i < mRentals.length(); i++)
            {
                JSONObject c = mRentals.getJSONObject(i);

                String dateStart = c.getString(TAG_DAT_S);
                String dateEnd = c.getString(TAG_DAT_E);
                String longitude = c.getString(TAG_LONG);
                String latitude = c.getString(TAG_LAT);
                String code = c.getString(TAG_CODE);
                
                Rental r = new Rental();
                r.setDateStart(dateStart);
                r.setDateEnd(dateEnd);
                r.setCode(code);
                
    			Location loc = new Location(LocationManager.NETWORK_PROVIDER);
    			loc.setLongitude(Double.parseDouble(longitude));
    			loc.setLatitude(Double.parseDouble(latitude));
                r.setLocation(loc);
                
                mRentalList.add(r);
            }

            mProperties = json.getJSONArray(TAG_PROPS);
            for (int i = 0; i < mProperties.length(); i++)
            {
                JSONObject o = mProperties.getJSONObject(i);

                String username = o.getString(TAG_USER);
                String name = o.getString(TAG_NAME);
                String address = o.getString(TAG_ADDR);
                String longitude = o.getString(TAG_LONG);
                String latitude = o.getString(TAG_LAT);
                
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