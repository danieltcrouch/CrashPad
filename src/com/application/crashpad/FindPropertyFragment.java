package com.application.crashpad;

import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FindPropertyFragment extends Fragment
{
	public static final String EXTRA_PROP_ID = "com.application.crashpad.property_id";
	
	private Property mProperty;
	private NotificationCompat.Builder mBuilder;
	private NotificationManager mNotificationManager;
	private int mId;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		UUID propId = (UUID)getArguments().getSerializable(EXTRA_PROP_ID);
		mProperty = PropertyList.get(getActivity()).getProperty(propId);
		
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
		
		TextView propertyName = (TextView)v.findViewById(R.id.property_name);
		propertyName.setText(mProperty.getName());
		
		Button rentButton = (Button)v.findViewById(R.id.property_rent);
		rentButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				/*Load our own webpage with special data
					which sends OK or CANCEL back
				Pushes Notification*/
				
				mId = 0;
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
				mNotificationManager.notify(mId, mBuilder.build());
			}
		});
		
		//Move this to Account Page
		/*Button contactLeaserButton = (Button)v.findViewById(R.id.contact_renter);
		contactLeaserButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent send = new Intent(Intent.ACTION_SEND);
				send.setType("text/plain");
				final Intent i = Intent.createChooser(send, "Select method of contact");
				startActivity(i);
			}
		});*/
		
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
	
	public static FindPropertyFragment newInstance(UUID propId)
	{
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_PROP_ID, propId);
		
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
}
