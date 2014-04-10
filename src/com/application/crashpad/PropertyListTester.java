package com.application.crashpad;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class PropertyListTester
{
	private static PropertyListTester sPropertyListTester;
	
	private ArrayList<Property> mProperties;
	
	private PropertyListTester(Context appContext)
	{
		mProperties = new ArrayList<Property>();
		
		//FIX
		//Delete this class when done
		
		for (double i = 0; i < 10; i++)
		{
			Property p = new Property();
			p.setName("Property #" + (int)i);
			p.setDescription("This is a really pleasant property, just off the coast of Location " + (int)i);
			
			Location temp = new Location(LocationManager.NETWORK_PROVIDER);
			temp.setLongitude(-91.724623 + (i * 0.05));
			temp.setLatitude(35.248738 - (i * 0.05));
			p.setLocation(temp);
			
			if (i == 2)
			{
				p.setDateTaken((new GregorianCalendar()).getTime());
			}
			else if (i == 4)
			{
				temp.setLatitude(34.0731);
				temp.setLongitude(-118.3994);
				p.setLocation(temp);
			}
			
			mProperties.add(p);
		}
	}
	
	public static PropertyListTester get(Context c)
	{
		if (sPropertyListTester == null)
		{
			sPropertyListTester = new PropertyListTester(c.getApplicationContext());
		}
		
		return sPropertyListTester;
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
	
	public ArrayList<Property> getProperties(Location loc, int distance, Date date)
	{
		ArrayList<Property> result = new ArrayList<Property>();
		
		for (Property prop : mProperties)
		{
			if (prop.getProximityToLocation(loc) <= distance &&
					prop.openAtDate(date))
			{
				result.add(prop);
			}
		}
		
		return result;
	}
}
