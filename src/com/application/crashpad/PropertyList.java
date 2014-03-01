package com.application.crashpad;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class PropertyList
{
	private ArrayList<Property> mProperties;
	
	private static PropertyList sPropertyList;
	private Context mAppContext;
	
	private PropertyList(Context appContext)
	{
		mAppContext = appContext;
		mProperties = new ArrayList<Property>();
		
		//TEMP
		//http://www.harding.edu/fmccown/classes/comp250-s14/notes/notes24.html
		//MySQL, H-Number for password
		for (int i = 0; i < 10; i++)
		{
			Property p = new Property();
			p.setName("Property #" + i);
			p.setDescription("This is a really pleasant property, just off the coast of Location " + i);
			p.setLocation(new Location(LocationManager.NETWORK_PROVIDER));
			mProperties.add(p);
		}
	}
	
	public static PropertyList get(Context c)
	{
		if (sPropertyList == null)
		{
			sPropertyList = new PropertyList(c.getApplicationContext());
		}
		
		return sPropertyList;
	}
	
	public void addProperty(Property p)
	{
		mProperties.add(p);
	}
	
	public ArrayList<Property> getProperties()
	{
		return mProperties;
	}
	
	public Property getProperty(UUID id)
	{
		for (Property p : mProperties)
		{
			if (p.getId().equals(id))
			{
				return p;
			}
		}
		
		return null;
	}
}
