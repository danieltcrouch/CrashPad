package com.application.crashpad;

import java.util.UUID;

import android.location.Location;

public class Property
{
	private UUID mId;
	private String mName;
	private String mDescription;
	private Location mLocation;
	
	public Property()
	{
		mId = UUID.randomUUID();
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
	
	public double getProximityToLocation(Location otherLoc)
	{
		double result = 0;
		
		//Distance Formula
		result = (mLocation.getLongitude() - otherLoc.getLongitude())*(mLocation.getLongitude() - otherLoc.getLongitude());
		result += (mLocation.getLatitude() - otherLoc.getLatitude())*(mLocation.getLatitude() - otherLoc.getLatitude());
		result = Math.sqrt(result); 
		
		return result;
	}
}
