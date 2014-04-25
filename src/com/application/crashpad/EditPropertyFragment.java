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
public class EditPropertyFragment extends Fragment
{
	public static final String EXTRA_PROP_ID = "com.application.crashpad.property_id";
	private static final String EDIT_URL = "http://taz.harding.edu/~dcrouch1/crashpad/edit_prop.php";
	private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private Property mProperty;
	private EditText mNameEditText;
	private EditText mAddressEditText;
	private EditText mDescriptionEditText;
	private EditText mCodeEditText;
	private Button mConfirmChangesButton;

    private Location mLocation;
    private LocationManager mLocationManager;
	private ProgressDialog mProgressDialog;
    private boolean mTaskRunning;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		PropertyList propertyList;
        propertyList = PropertyList.get(getActivity());
		mProperty = propertyList.getProperty(getActivity().getIntent().getIntExtra(EXTRA_PROP_ID, 0));
	}
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_edit_property, parent, false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			if (NavUtils.getParentActivityName(getActivity()) != null)
			{
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
			}
        }
		
		mNameEditText = (EditText)view.findViewById(R.id.edit_name);
		mNameEditText.setHint(mProperty.getName());
		
		mAddressEditText = (EditText)view.findViewById(R.id.edit_address);
		mDescriptionEditText = (EditText)view.findViewById(R.id.edit_description);
		mCodeEditText = (EditText)view.findViewById(R.id.edit_code);
		
		mConfirmChangesButton = (Button)view.findViewById(R.id.update_info_button);
		mConfirmChangesButton.setOnClickListener(new View.OnClickListener()
		{			
			@Override
			public void onClick(View v)
			{
				String originalName = mProperty.getName();
				String originalAddress = mProperty.getAddress();
				String originalDescription = mProperty.getDescription();

				String name = mNameEditText.getText().toString();
				String addr = mAddressEditText.getText().toString();
				String desc = mDescriptionEditText.getText().toString();
				String code = mCodeEditText.getText().toString();

				//Checks if anything has changed
				if (!(	(originalDescription.equals(desc) || desc.length() == 0) &&
						(originalAddress.equals(addr) || addr.length() == 0) &&
						(originalName.equals(name) || name.length() == 0) &&
						code.length() == 0))
				{
					if (!originalAddress.equals(addr) && addr.length() != 0)
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
								new EditProp().execute();
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
						new EditProp().execute();
					}
				}
			}
		});

		mLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		return view;	
	}
	
	class EditProp extends AsyncTask<String, String, String>
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
            double lng;
            double lat;
            
            //Ignores blank field
            String name = (mNameEditText.getText().toString().length() != 0)?
            		mNameEditText.getText().toString() : mProperty.getName();
            String description = (mDescriptionEditText.getText().toString().length() != 0)?
            		mDescriptionEditText.getText().toString() : mProperty.getDescription();
            String code = (mCodeEditText.getText().toString().length() != 0)?
            		mCodeEditText.getText().toString() : mProperty.getCode();
            String address = mAddressEditText.getText().toString();
            if (address.length() != 0)
            {
            	lng = mLocation.getLongitude();
            	lat = mLocation.getLatitude();
            }
            else
            {
            	address = mProperty.getAddress();
            	lng = mProperty.getLocation().getLongitude();
            	lat = mProperty.getLocation().getLatitude();
            }

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", AccountCurrent.get(getActivity()).getPresentAccount().getUsername()));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("address", address));
            params.add(new BasicNameValuePair("description", description));
            params.add(new BasicNameValuePair("longitude", Double.toString(lng)));
            params.add(new BasicNameValuePair("latitude", Double.toString(lat)));
            params.add(new BasicNameValuePair("code", code));
            params.add(new BasicNameValuePair("id", Double.toString(mProperty.getId())));

            try
            {
            	JSONParser jParser = new JSONParser();
                JSONObject json = jParser.makeHttpRequest(EDIT_URL, "POST", params);
                success = json.getInt(TAG_SUCCESS);

                if (success == 1)
                {
                	mProperty.setName(name);
                	mProperty.setAddress(address);
                	mProperty.setDescription(description);
                	mProperty.setCode(code);
            		PropertyList propertyList = PropertyList.get(getActivity());
            		propertyList.removeProperty(mProperty.getId());
            		propertyList.addProperty(mProperty);
            		
            		Intent i = new Intent(getActivity(), ReviewPropertyActivity.class);
                    i.putExtra(ReviewPropertyFragment.EXTRA_PROP_ID, mProperty.getId());
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
        mProgressDialog.setMessage("Editing Property...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
	}
}
