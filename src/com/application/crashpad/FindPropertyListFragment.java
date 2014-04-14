package com.application.crashpad;

import java.util.ArrayList;
import java.util.Date;

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
	public static final String EXTRA_PARA_LOC = "com.application.crashpad.parameter_loc";
	public static final String EXTRA_PARA_DIST = "com.application.crashpad.parameter_dis";
	public static final String EXTRA_PARA_DATE_S = "com.application.crashpad.parameter_date_start";
	public static final String EXTRA_PARA_DATE_E = "com.application.crashpad.parameter_date_end";
	
	private static final String GET_PROPS_URL = "http://taz.harding.edu/~dcrouch1/crashpad/get_props_find.php";
    private static final String TAG_PROPS = "props";
    private static final String TAG_USER = "username";
    private static final String TAG_NAME = "name";
    private static final String TAG_DESC = "description";
    private static final String TAG_ADDR = "address";
    private static final String TAG_LONG = "longitude";
    private static final String TAG_LAT = "latitude";
    private static final String TAG_ID = "id";
    
	private Date mDateStart;
	private Date mDateEnd;
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

		mLoc = (Location)getActivity().getIntent().getParcelableExtra(EXTRA_PARA_LOC);
		mDist = getActivity().getIntent().getIntExtra(EXTRA_PARA_DIST, 0);
		mDateStart = (Date)getActivity().getIntent().getSerializableExtra(EXTRA_PARA_DATE_S);
		mDateEnd = (Date)getActivity().getIntent().getSerializableExtra(EXTRA_PARA_DATE_E);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {        
        Property p = ((propertyAdapter)getListAdapter()).getItem(position);
        Intent i = new Intent(getActivity(), FindPropertyActivity.class);
        i.putExtra(FindPropertyFragment.EXTRA_PROP_ID, p.getId());
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

        try
        {
        	//FIX
        	//Ignore Properties by user
        	
            //FIX
            //if you can make it where this uses parameters, you can delete "getJSONFromUrl"
            //	param might be a liberal distance to check against longitude only, just to narrow results down a little
            //JSONObject json = jParser.makeHttpRequest(GET_PROPS_URL, "POST", params);
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.getJSONFromUrl(GET_PROPS_URL);
            mProperties = json.getJSONArray(TAG_PROPS);
            
            for (int i = 0; i < mProperties.length(); i++)
            {
                JSONObject o = mProperties.getJSONObject(i);

                String username = o.getString(TAG_USER);
                String name = o.getString(TAG_NAME);
                String address = o.getString(TAG_ADDR);
                String longitude = o.getString(TAG_LONG);
                String latitude = o.getString(TAG_LAT);
                String description = o.getString(TAG_DESC);
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

                //FIX
                //Properties do not know what dates they're taken without access to "rentals"
                if (p.openAtDates(mDateStart, mDateEnd) && p.getProximityToLocation(mLoc) <= mDist)
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

