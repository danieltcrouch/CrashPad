package com.application.crashpad;

import java.util.ArrayList;

import android.content.Context;

public class PropertyList
{
	private static PropertyList sPropertyList;
	
	private ArrayList<Property> mProperties;
	
	private PropertyList(Context appContext)
	{
		mProperties = new ArrayList<Property>();
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
	
	public Property getProperty(int id)
	{
		for (Property p : mProperties)
		{
			if (p.getId() == id)
			{
				return p;
			}
		}
		
		return null;
	}
	
	public void setProperties(ArrayList<Property> props)
	{
		mProperties = props;
	}
	
	public ArrayList<Property> getProperties()
	{
		return mProperties;
	}
}
