package com.application.crashpad;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import android.location.Location;

public class Property
{
	private UUID mId;
	private String mUsername;
	private String mName;
	private String mDescription;
	private Location mLocation;
	private ArrayList<Date> mDatesTaken;
	
	public Property()
	{
		mId = UUID.randomUUID();
		mDatesTaken = new ArrayList<Date>();
	}
	
	public Property(String name, String des, Location loc)
	{
		mId = UUID.randomUUID();
		mName = name;
		mDescription = des;
		mLocation = loc;
		mDatesTaken = new ArrayList<Date>();
	}
	
	@Override
	public String toString()
	{
		return mName;
	}

	public UUID getId()
	{
		return mId;
	}	

	public void setId(UUID id)
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
	
	public boolean openAtDate(Date date)
	{
		boolean result = true;

		Calendar calendar1 = Calendar.getInstance();
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
		}
		
		return result;
	}
}
