package com.application.crashpad;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//Uses AsyncTask
public class CreatePropertyFragment extends Fragment
{
	private static final String ADD_URL = "http://taz.harding.edu/~dcrouch1/crashpad/add_prop.php";
	private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

	private EditText mNameEditText;
	private EditText mAddressEditText;
	private EditText mDescriptionEditText;
	private Button mConfirmAccountCreate;

    private Location mLocation;
    private LocationManager mLocationManager;
	private ProgressDialog mProgressDialog;
    private boolean mTaskRunning;
		
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_create_property, parent, false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			if (NavUtils.getParentActivityName(getActivity()) != null)
			{
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
        }

		mNameEditText = (EditText)view.findViewById(R.id.new_name);
		mAddressEditText = (EditText)view.findViewById(R.id.new_address);
		mDescriptionEditText = (EditText)view.findViewById(R.id.new_description);
		
		mConfirmAccountCreate = (Button)view.findViewById(R.id.prop_create_button);
		mConfirmAccountCreate.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String name = mNameEditText.getText().toString();
				String addr = mAddressEditText.getText().toString();
				String desc = mDescriptionEditText.getText().toString();

				if (desc.length() != 0 && addr.length() != 0 && name.length() != 0)
				{
					Geocoder geocoder = new Geocoder(getActivity());
					String location = mAddressEditText.getText().toString();
					
					try
					{
						List<Address> addresses = geocoder.getFromLocationName(location, 1);
						if (addresses != null && !addresses.isEmpty())
						{
							Address address = addresses.get(0);
							mLocation.setLatitude(address.getLatitude());
							mLocation.setLongitude(address.getLongitude());
							new CreateProp().execute();
						}
						else
						{
							Toast.makeText(getActivity(), "Unable to find location.", Toast.LENGTH_LONG).show();
						}
					}
					catch (Exception e)
					{
						Toast.makeText(getActivity(), "Having trouble finding location.\nCheck GPS.", Toast.LENGTH_SHORT).show(); 
					}
				}
				else
				{
					Toast.makeText(getActivity(), R.string.creation_error_toast, Toast.LENGTH_SHORT).show();
				}
			}
		});

		mLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		return view;
	}
	
	class CreateProp extends AsyncTask<String, String, String>
	{
		boolean failure = false;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            showProgressDialog();
            mTaskRunning = true;
        }

		@Override
		protected String doInBackground(String... args)
		{
            int success;
            String name = mNameEditText.getText().toString();
			String addr = mAddressEditText.getText().toString();
			String desc = mDescriptionEditText.getText().toString();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", AccountCurrent.get(getActivity()).getPresentAccount().getUsername()));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("address", addr));
            params.add(new BasicNameValuePair("description", desc));
            params.add(new BasicNameValuePair("longitude", Double.toString(mLocation.getLongitude())));
            params.add(new BasicNameValuePair("latitude", Double.toString(mLocation.getLatitude())));

            try
            {
                JSONParser jsonParser = new JSONParser();
                JSONObject json = jsonParser.makeHttpRequest(ADD_URL, "POST", params);
                success = json.getInt(TAG_SUCCESS);

                if (success == 1)
                {
    				Intent i = new Intent(getActivity(), ReviewPropertyListActivity.class);
    				startActivity(i);
    				
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
            catch (Exception e)
            {
            	e.printStackTrace();
            	return "Network Problems\nCheck WiFi Connection";
            }

            return null;
		}
		
        protected void onPostExecute(String message)
        {
        	mTaskRunning = false;
        	mProgressDialog.dismiss();
            if (message != null)
            {
            	Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
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
        mProgressDialog.setMessage("Adding Property...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
	}
}
