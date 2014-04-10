package com.application.crashpad;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.location.Location;

public class Rental
{
	private String mDateStart;
	private String mDateEnd;
	private String mCode;
	private Location mLocation;
	
	public Rental()
	{
		//
	}
		
	public String getDateStart()
	{
		return mDateStart;
	}

	public void setDateStart(String dateStart)
	{
		mDateStart = dateStart;
	}

	public String getDateEnd()
	{
		return mDateEnd;
	}

	public void setDateEnd(String dateEnd)
	{
		mDateEnd = dateEnd;
	}

	public String getCode()
	{
		return mCode;
	}

	public void setCode(String code)
	{
		mCode = code;
	}

	public Location getLocation()
	{
		return mLocation;
	}

	public void setLocation(Location location)
	{
		mLocation = location;
	}
	
	public boolean presentlyRenting()
	{
		boolean result = false;
		Date present = new Date();
		Date dateStart = new Date();
	    Date dateEnd = new Date();
		
	    //FIX
	    //Won't work for day-of
		try
		{
		    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
		    dateStart =  df.parse(mDateStart);
		    dateEnd =  df.parse(mDateEnd);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		if (present.before(dateEnd) && present.after(dateStart))
		{
			result = true;
		}
		
		return result;
		
		//Delete when done
		/*Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		calendar1.setTime(date);
		for (Date dateTaken : mDatesTaken)
		{
			calendar2.setTime(dateTaken);
			if (calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH) &&
				calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
				calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR))
			{
				result = false;
				break;
			}
		}*/
	}
}
