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
	
	private PropertyList(Context appContext)
	{
		mProperties = new ArrayList<Property>();
		
		//TEMP
		//http://www.harding.edu/fmccown/classes/comp250-s14/notes/notes24.html
		//MySQL, H-Number for password
		
		for (double i = 0; i < 10; i++)
		{
			Property p = new Property();
			p.setName("Property #" + (int)i);
			p.setDescription("This is a really pleasant property, just off the coast of Location " + (int)i);
			Location temp = new Location(LocationManager.NETWORK_PROVIDER);
			temp.setLongitude(-91.724623 + (i * 0.05));
			temp.setLatitude(35.248738 - (i * 0.05));
			p.setLocation(temp);
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
	
	public ArrayList<Property> getProperties()
	{
		return mProperties;
	}
	
	public ArrayList<Property> getProperties(Location loc, int distance)
	{
		ArrayList<Property> result = new ArrayList<Property>();
		
		for (Property prop : mProperties)
		{
			if (prop.getProximityToLocation(loc) <= distance)
			{
				result.add(prop);
			}
		}
		
		return result;
	}
}
