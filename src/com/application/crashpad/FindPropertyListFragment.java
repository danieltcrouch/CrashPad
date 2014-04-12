package com.application.crashpad;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
public class FindPropertyListFragment extends ListFragment
{
	public static final String EXTRA_PARA_LONG = "com.application.crashpad.parameter_loc";
	public static final String EXTRA_PARA_LAT = "com.application.crashpad.parameter_lat";
	public static final String EXTRA_PARA_DIS = "com.application.crashpad.parameter_dis";
	public static final String EXTRA_PARA_DAY = "com.application.crashpad.parameter_day";
	public static final String EXTRA_PARA_MON = "com.application.crashpad.parameter_mon";
	public static final String EXTRA_PARA_YEAR = "com.application.crashpad.parameter_year";
	
	private static final String GET_PROPS_URL = "http://taz.harding.edu/~dcrouch1/crashpad/get_props_find.php";
    private static final String TAG_PROPS = "props";
    private static final String TAG_USER = "username";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADDR = "address";
    private static final String TAG_LONG = "longitude";
    private static final String TAG_LAT = "latitude";
    
	private Calendar mDate;
	private Location mLoc;
	private int mDist;

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
		
		mDist = Integer.parseInt(getActivity().getIntent().getStringExtra(EXTRA_PARA_DIS));
		double longitude = Double.parseDouble(getActivity().getIntent().getStringExtra(EXTRA_PARA_LONG));
		double latitude = Double.parseDouble(getActivity().getIntent().getStringExtra(EXTRA_PARA_LAT));
		int day = Integer.parseInt(getActivity().getIntent().getStringExtra(EXTRA_PARA_DAY));
		int month = Integer.parseInt(getActivity().getIntent().getStringExtra(EXTRA_PARA_MON));
		int year = Integer.parseInt(getActivity().getIntent().getStringExtra(EXTRA_PARA_YEAR));
		
		mDate = new GregorianCalendar(year, month, day);
		mLoc = new Location(LocationManager.NETWORK_PROVIDER);
		mLoc.setLatitude(latitude);
		mLoc.setLongitude(longitude);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {        
        Property p = ((propertyAdapter)getListAdapter()).getItem(position);
        Intent i = new Intent(getActivity(), FindPropertyActivity.class);
        //i.putExtra(FindPropertyFragment.EXTRA_PROP_ID, p.getId());
        i.putExtra(FindPropertyFragment.EXTRA_PROP_USER, p.getUsername());
        i.putExtra(FindPropertyFragment.EXTRA_PROP_NAME, p.getName());
        i.putExtra(FindPropertyFragment.EXTRA_PROP_DESC, p.getDescription());
        i.putExtra(FindPropertyFragment.EXTRA_PROP_LONG, p.getLocation().getLongitude());
        i.putExtra(FindPropertyFragment.EXTRA_PROP_LAT, p.getLocation().getLatitude());
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
    	/*String pUsername = AccountCurrent.get(getActivity()).getPresentAccount().getName();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", pUsername));*/
        
        mPropertyList = new ArrayList<Property>();
        JSONParser jParser = new JSONParser();
        //FIX
        //if you can make it where this uses parameters, you can delete "getJSONFromUrl"
        //	param might be a liberal distance to check against longitude only, just to narrow results down a little
        //JSONObject json = jParser.makeHttpRequest(GET_PROPS_URL, "POST", params);
        JSONObject json = jParser.getJSONFromUrl(GET_PROPS_URL);

        try
        {
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
                
                if (p.openAtDate(mDate.getTime()) && p.getProximityToLocation(mLoc) <= mDist)
                {
                	mPropertyList.add(p);
                }
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

