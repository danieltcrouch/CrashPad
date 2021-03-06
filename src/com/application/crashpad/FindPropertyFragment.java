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
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//Uses AsyncTask
public class FindPropertyFragment extends Fragment
{
	private static final String RENT_URL = "http://taz.harding.edu/~dcrouch1/crashpad/rent.php";
	private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
	private static final int REQUEST_DATE_START = 0;
	private static final int REQUEST_DATE_END = 1;
    
	public static final String EXTRA_PROP_ID = "com.application.crashpad.property_id";
	public static final String EXTRA_OWNER = "com.application.crashpad.property_owner";
	public static final String FORMAT_DATE = "EEE, MMM d, yyyy";
	public static final String DIALOG_DATE = "date";
	
	private Property mProperty;
	private String mName;
	private Date mDateStart;
	private Date mDateEnd;
	private Button mChangeDateStartButton;
	private Button mChangeDateEndButton;
	private Button mRentButton;
	private TextView mPropertyName;
	private TextView mPropertyAddress;
	private TextView mPropertyDescription;
	//private NotificationCompat.Builder mBuilder;
	//private NotificationManager mNotificationManager;
	
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
		mName = getActivity().getIntent().getStringExtra(EXTRA_OWNER);
		
		setHasOptionsMenu(true);
	}
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_property_find, parent, false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			if (NavUtils.getParentActivityName(getActivity()) != null)
			{
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
        }

		mPropertyName = (TextView)view.findViewById(R.id.property_name);
		mPropertyName.setText(mName + "'s " + mProperty.getName());
		
		mPropertyAddress = (TextView)view.findViewById(R.id.property_address);
		mPropertyAddress.setText(mProperty.getAddress());

		mPropertyDescription = (TextView)view.findViewById(R.id.property_description);
		mPropertyDescription.setText(mProperty.getDescription());
		
		mDateStart = new Date();
		mChangeDateStartButton = (Button)view.findViewById(R.id.start_date_button);
		mChangeDateStartButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DatePickerFragment dialog = DatePickerFragment.newInstance(mDateStart);
				dialog.setTargetFragment(FindPropertyFragment.this, REQUEST_DATE_START);
				dialog.show(fm, DIALOG_DATE);
			}
		});

		mDateEnd = new Date();
		mChangeDateEndButton = (Button)view.findViewById(R.id.end_date_button);
		mChangeDateEndButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DatePickerFragment dialog = DatePickerFragment.newInstance(mDateEnd);
				dialog.setTargetFragment(FindPropertyFragment.this, REQUEST_DATE_END);
				dialog.show(fm, DIALOG_DATE);
			}
		});
		
		mRentButton = (Button)view.findViewById(R.id.rent_property_button);
		mRentButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				if (mProperty.openAtDates(mDateStart, mDateEnd))
				{
					new RentProperty().execute();
				}
				else
				{
					Toast.makeText(getActivity(), "Sorry, " + mProperty.getName() + " is already booked during these dates.",
							Toast.LENGTH_SHORT).show();
				}
				
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
		
		updateDate();
		
		return view;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode != Activity.RESULT_OK)
		{
			return;
		}
		
		if (requestCode == REQUEST_DATE_START)
		{
			mDateStart = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			updateDate();
		}
		else if (requestCode == REQUEST_DATE_END)
		{
			mDateEnd = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			updateDate();
		}
	}

	private void updateDate()
	{
		mChangeDateStartButton.setText(DateFormat.format(FORMAT_DATE, mDateStart));
		mChangeDateEndButton.setText(DateFormat.format(FORMAT_DATE, mDateEnd));
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
            showProgressDialog();
            mTaskRunning = true;
        }

		@Override
		protected String doInBackground(String... args)
		{			
            int success;
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
		    
            String renter = AccountCurrent.get(getActivity()).getPresentAccount().getUsername();
            String owner = mProperty.getUsername();
            String dateStart = df.format(mDateStart);
            String dateEnd = df.format(mDateEnd);
            String propId = Integer.toString(mProperty.getId());

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("dateStart", dateStart));
            params.add(new BasicNameValuePair("dateEnd", dateEnd));
            params.add(new BasicNameValuePair("owner", owner));
            params.add(new BasicNameValuePair("renter", renter));
            params.add(new BasicNameValuePair("propId", propId));

            try
            {
            	JSONParser jsonParser = new JSONParser();
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
            catch (Exception e)
            {
            	e.printStackTrace();
            	return "Network Problems\nCheck WiFi Connection";
            }

            return null;
		}
		
        protected void onPostExecute(String file_url)
        {
        	mTaskRunning = false;
        	mProgressDialog.dismiss();
            if (file_url != null)
            {
            	Toast.makeText(getActivity(), file_url, Toast.LENGTH_LONG).show();
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
        mProgressDialog.setMessage("Renting Property...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
	}
}
