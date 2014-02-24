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
	
	public String getName()
	{
		return mName;
	}

	public void setName(String name)
	{
		mName = name;
	}

	public void setId(UUID id)
	{
		mId = id;
	}

	public UUID getId()
	{
		return mId;
	}	
}
