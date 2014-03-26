package com.application.crashpad;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FindPropertyFragment extends Fragment
{
	private static final String RENT_URL = "http://taz.harding.edu/~dcrouch1/crashpad/rent.php";
	private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    
	//public static final String EXTRA_PROP_ID = "com.application.crashpad.property_id";
	public static final String EXTRA_PROP_USER = "com.application.crashpad.property_user";
	public static final String EXTRA_PROP_NAME = "com.application.crashpad.property_name";
	public static final String EXTRA_PROP_DESC = "com.application.crashpad.property_desc";
	public static final String EXTRA_PROP_LONG = "com.application.crashpad.property_long";
	public static final String EXTRA_PROP_LAT = "com.application.crashpad.property_lat";
	
	private Property mProperty;
	private TextView propertyName;
	private TextView propertyInfo;
	private NotificationCompat.Builder mBuilder;
	private NotificationManager mNotificationManager;
	private int mId;
	
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//UUID propId = (UUID)getArguments().getSerializable(EXTRA_PROP_ID);
    	//Should add ID attribute to table so that we can actually use the above^
		mProperty = new Property();
		mProperty.setUsername((String)getArguments().getSerializable(EXTRA_PROP_USER));
		mProperty.setName((String)getArguments().getSerializable(EXTRA_PROP_NAME));
		mProperty.setDescription((String)getArguments().getSerializable(EXTRA_PROP_DESC));
		
		Location loc = new Location(LocationManager.NETWORK_PROVIDER);
		loc.setLongitude((Double)getArguments().getSerializable(EXTRA_PROP_LONG));
		loc.setLatitude((Double)getArguments().getSerializable(EXTRA_PROP_LAT));
		mProperty.setLocation(loc);
		
		setHasOptionsMenu(true);
	}
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_property_find, parent, false);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			if (NavUtils.getParentActivityName(getActivity()) != null)
			{
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
        }
		
		propertyName = (TextView)v.findViewById(R.id.property_name);
		propertyName.setText(mProperty.getUsername() + "'s " + mProperty.getName());
		propertyInfo = (TextView)v.findViewById(R.id.property_info);
		propertyInfo.setText(mProperty.getDescription());
		
		Button rentButton = (Button)v.findViewById(R.id.property_rent);
		rentButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				new RentProperty().execute();
				
				/*Load our own webpage with special data
					which sends OK or CANCEL back
				Pushes Notification*/
				
				/*mId = 0;
				mBuilder = new NotificationCompat.Builder(getActivity())
				        .setSmallIcon(R.drawable.ic_launcher)
				        .setContentTitle("Rental Notification")
				        .setContentText("A property has been rented...")
				        .setAutoCancel(true);
				
		        Intent resultIntent = new Intent(getActivity(), ReviewPropertyActivity.class);
		        resultIntent.putExtra(FindPropertyFragment.EXTRA_PROP_ID, mProperty.getId());

				TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
				stackBuilder.addParentStack(ReviewPropertyActivity.class);
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent =
				        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
				
				mBuilder.setContentIntent(resultPendingIntent);
				mNotificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.notify(mId, mBuilder.build());*/
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
	
	public static FindPropertyFragment newInstance(
			String username, String name, String description, Double longitude, Double latitude)
	{
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_PROP_USER, username);
		args.putSerializable(EXTRA_PROP_NAME, name);
		args.putSerializable(EXTRA_PROP_DESC, description);
		args.putSerializable(EXTRA_PROP_LONG, longitude);
		args.putSerializable(EXTRA_PROP_LAT, latitude);
		
		FindPropertyFragment fragment = new FindPropertyFragment();
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
	
	class RentProperty extends AsyncTask<String, String, String>
	{
		boolean failure = false;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Renting Property...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

		@Override
		protected String doInBackground(String... args)
		{
            int success;
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
		    
            String renter = PresentAccount.get(getActivity()).getPresentAccount().getName();
            String owner = mProperty.getUsername();
            //FIX
            String dateStart = df.format(new Date());
            String dateEnd = df.format(new Date());
            String longitude = Double.toString(mProperty.getLocation().getLongitude());
            String latitude = Double.toString(mProperty.getLocation().getLatitude());
            
            try
            {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("longitude", longitude));
                params.add(new BasicNameValuePair("latitude", latitude));
                params.add(new BasicNameValuePair("dateStart", dateStart));
                params.add(new BasicNameValuePair("dateEnd", dateEnd));
                params.add(new BasicNameValuePair("owner", owner));
                params.add(new BasicNameValuePair("renter", renter));
                //FIX
                params.add(new BasicNameValuePair("code", "475"));

                JSONObject json = jsonParser.makeHttpRequest(RENT_URL, "POST", params);
                success = json.getInt(TAG_SUCCESS);
                
                if (success == 1)
                {
                	return json.getString(TAG_MESSAGE);
                }
                else
                {
                	return json.getString(TAG_MESSAGE);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            return null;
		}
		
        protected void onPostExecute(String file_url)
        {
            pDialog.dismiss();
            if (file_url != null)
            {
            	Toast.makeText(getActivity(), file_url, Toast.LENGTH_LONG).show();
            }
        }
	}
}
