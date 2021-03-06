package com.application.crashpad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class ReviewBookingsListFragment extends ListFragment
{
	private static final String GET_PROPS_B_URL = "http://taz.harding.edu/~dcrouch1/crashpad/get_props_booked.php";
    private static final String TAG_PROPS = "props";
    private static final String TAG_RENTS = "rentals";
    private static final String TAG_USER = "username";
    private static final String TAG_P_NAME = "prop_name";
    private static final String TAG_DESC = "description";
    private static final String TAG_ADDR = "address";
    private static final String TAG_LONG = "longitude";
    private static final String TAG_LAT = "latitude";
    private static final String TAG_CODE = "code";
    private static final String TAG_DAT_S = "dateStart";
    private static final String TAG_DAT_E= "dateEnd";
    private static final String TAG_NAME = "name";
    private static final String TAG_ID = "id";

	private Map<String, String> mNames;
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
        Rental r = ((rentalAdapter)getListAdapter()).getItem(position);
        Property p = mPropertyList.get(0);
        String n = p.getUsername();
        for (int i = 0; i < mPropertyList.size(); i++)
        {
        	if (mPropertyList.get(i).getId() == r.getPropId())
        	{
        		p = mPropertyList.get(i);
        		n = mNames.get(p.getUsername());
        	}
        }

        Intent i = new Intent(getActivity(), ReviewBookingsActivity.class);
        i.putExtra(ReviewBookingsFragment.EXTRA_PROP_ID, p.getId());
        i.putExtra(ReviewBookingsFragment.EXTRA_RENT_DAT_S, r.getDateStart());
        i.putExtra(ReviewBookingsFragment.EXTRA_RENT_DAT_E, r.getDateEnd());
        i.putExtra(ReviewBookingsFragment.EXTRA_OWNER, n);
        startActivity(i);
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
		new LoadProperties().execute();
    }

    private class rentalAdapter extends ArrayAdapter<Rental>
    {
    	private PropertyList mPropertyList;
    	
        public rentalAdapter(ArrayList<Rental> rentals, ArrayList<Property> properties)
        {
            super(getActivity(), 0, rentals);
            mPropertyList = PropertyList.get(getActivity());
            mPropertyList.setProperties(properties);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_rental, null);
            }

            Rental r = getItem(position);
            Property p = mPropertyList.getProperty(r.getPropId());
            
            TextView rentalName = (TextView)convertView.findViewById(R.id.rental_list_item_name);
            rentalName.setText(p.getName() + ", " + r.getDateStart());

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

    		//Order List
			Collections.sort(mRentalList, new Comparator<Rental>()
			{
				@Override
				public int compare(Rental rent1, Rental rent2)
				{
					boolean greater = rent1.getDateStartTime().before(rent2.getDateStartTime());
					boolean equal = rent1.getDateStartTime().equals(rent2.getDateStartTime());
					return  greater? 1 : equal? 0 : -1;
				}
			});
                        
            rentalAdapter adapter = new rentalAdapter(mRentalList, mPropertyList);
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
        mRentalList = new ArrayList<Rental>();
        mNames = new HashMap<String, String>();

        try
        {
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.makeHttpRequest(GET_PROPS_B_URL, "POST", params);
            mRentals = json.getJSONArray(TAG_RENTS);
            
            for (int i = 0; i < mRentals.length(); i++)
            {
                JSONObject c = mRentals.getJSONObject(i);

                String dateStart = c.getString(TAG_DAT_S);
                String dateEnd = c.getString(TAG_DAT_E);
                int propId = c.getInt(TAG_ID);
                
                Rental r = new Rental();
                r.setDateStart(dateStart);
                r.setDateEnd(dateEnd);
                r.setPropId(propId);
                
                if (!r.isOver())
                {
                	mRentalList.add(r);
                }
            }

            mProperties = json.getJSONArray(TAG_PROPS);
            for (int i = 0; i < mProperties.length(); i++)
            {
                JSONObject o = mProperties.getJSONObject(i);

                String username = o.getString(TAG_USER);
                String propName = o.getString(TAG_P_NAME);
                String description = o.getString(TAG_DESC);
                String address = o.getString(TAG_ADDR);
                String longitude = o.getString(TAG_LONG);
                String latitude = o.getString(TAG_LAT);
                String code = o.getString(TAG_CODE);
                String name = o.getString(TAG_NAME);
                int id = o.getInt(TAG_ID);

                //For keeping the name for the property page
                mNames.put(username, name);
                
                Property p = new Property();
                p.setUsername(username);
                p.setName(propName);
                p.setDescription(description);
                p.setAddress(address);
                p.setCode(code);
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