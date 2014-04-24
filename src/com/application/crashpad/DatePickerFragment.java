package com.application.crashpad;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

public class DatePickerFragment extends DialogFragment
{
	public static final String EXTRA_DATE = "com.application.crashpad.date";
	
	private Date mDate;
	private int mYear;
	private int mMonth;
	private int mDay;
	
	public static DatePickerFragment newInstance(Date date)
	{
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_DATE, date);
		
		DatePickerFragment fragment = new DatePickerFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	private void sendResult(int resultCode)
	{
		if (getTargetFragment() == null)
		{
			return;
		}
		
		Intent i = new Intent();
		i.putExtra(EXTRA_DATE, mDate);
		
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		mDate = (Date)getArguments().getSerializable(EXTRA_DATE);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH);
		mDay = calendar.get(Calendar.DAY_OF_MONTH);
		
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);
		
		DatePicker datePicker = (DatePicker)v.findViewById(R.id.dialog_date_picker);
		//datePicker.setMinDate(new Date().getTime());
		datePicker.init(mYear, mMonth, mDay, new OnDateChangedListener()
		{
			public void onDateChanged(DatePicker view, int year, int month, int day)
			{				
				GregorianCalendar now = new GregorianCalendar();
				Date chosen = new GregorianCalendar(year, month, day).getTime();
				Date today = new GregorianCalendar(
						now.get(Calendar.YEAR), 
						now.get(Calendar.MONTH), 
						now.get(Calendar.DAY_OF_MONTH)
						).getTime();
				
				//Checks if date is old
				if (!chosen.before(today))
				{
					mDate = chosen;
					getArguments().putSerializable(EXTRA_DATE, mDate);
				}
				else
				{
					view.updateDate(mYear, mMonth, mDay);
				}
			}
		});
		
		return new AlertDialog.Builder(getActivity())
			.setView(v)
			.setTitle("Date Picker")
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					sendResult(Activity.RESULT_OK);
				}
			})
			.create();
	}
}
