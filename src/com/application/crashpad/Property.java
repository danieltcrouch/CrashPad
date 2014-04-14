package com.application.crashpad;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.location.Location;

public class Property
{
	private int mId;
	private String mUsername;
	private String mName;
	private String mDescription;
	private String mAddress;
	private String mCode;
	
	private Location mLocation;
	private ArrayList<Date> mDatesTaken;
	
	public Property()
	{
		mDatesTaken = new ArrayList<Date>();
	}
	
	@Override
	public String toString()
	{
		return mName;
	}

	public int getId()
	{
		return mId;
	}	

	public void setId(int id)
	{
		mId = id;
	}
	
	public String getUsername()
	{
		return mUsername;
	}

	public void setUsername(String username)
	{
		mUsername = username;
	}
	
	public String getName()
	{
		return mName;
	}

	public void setName(String name)
	{
		mName = name;
	}

	public String getDescription()
	{
		return mDescription;
	}	

	public void setDescription(String des)
	{
		mDescription = des;
	}

	public String getAddress()
	{
		return mAddress;
	}	

	public void setAddress(String addr)
	{
		mAddress = addr;
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

	public void setLocation(Location loc)
	{
		mLocation = loc;
	}

	public ArrayList<Date> getDatesTaken()
	{
		return mDatesTaken;
	}
	
	public void setDatesTaken(ArrayList<Date> dates)
	{
		mDatesTaken = dates;
	}

	public void setDateTaken(Date date)
	{
		mDatesTaken.add(date);
	}

	public void setDateRangeTaken(Date dateStart, Date dateEnd)
	{
		Calendar calCheck = Calendar.getInstance();
		calCheck.setTime(dateStart);
		
		do
		{
			setDateTaken(calCheck.getTime());
			calCheck.add(Calendar.DAY_OF_YEAR, 1);
		} while (calCheck.before(dateEnd));
		setDateTaken(dateEnd);
	}
	
	public boolean openAtDate(Date date)
	{
		boolean result = true;

		Calendar calDate = Calendar.getInstance();
		Calendar calCheck = Calendar.getInstance();
		calDate.setTime(date);
		
		for (Date dateTaken : mDatesTaken)
		{
			calCheck.setTime(dateTaken);
			if (calDate.get(Calendar.DAY_OF_MONTH) == calCheck.get(Calendar.DAY_OF_MONTH) &&
				calDate.get(Calendar.MONTH) == calCheck.get(Calendar.MONTH) &&
				calDate.get(Calendar.YEAR) == calCheck.get(Calendar.YEAR))
			{
				result = false;
				break;
			}
		}
		
		return result;
	}
	
	public boolean openAtDates(Date dateStart, Date dateEnd)
	{
		boolean result = true;
		
		Calendar calCheck = Calendar.getInstance();
		calCheck.setTime(dateStart);
		
		do
		{
			if (!openAtDate(calCheck.getTime()))
			{
				result = false;
				break;
			}
			calCheck.add(Calendar.DAY_OF_YEAR, 1);
		} while (calCheck.before(dateEnd));
		
		if (!openAtDate(calCheck.getTime()))
		{
			result = false;
		}
		
		return result;
	}
	
	public double getProximityToLocation(Location otherLoc)
	{
		final double EARTH_RADIUS = 3963.1676;
		
		double result = 0;

		//Haversine Formula
		double lat2 = Math.toRadians(mLocation.getLatitude());
		double lat1 = Math.toRadians(otherLoc.getLatitude());
		double long2 = Math.toRadians(mLocation.getLongitude());
		double long1 = Math.toRadians(otherLoc.getLongitude());
		
		double dLon = long2 - long1;
		double dLat = lat2 - lat1;
		double temp = Math.pow(Math.sin(dLat/2),2) +
				Math.cos(lat2) * Math.cos(lat1) *
				Math.pow(Math.sin(dLon/2),2);
		result  = 2 * Math.atan2(Math.sqrt(temp),Math.sqrt(1-temp));
		result *= EARTH_RADIUS;

		return result;
	}
}
