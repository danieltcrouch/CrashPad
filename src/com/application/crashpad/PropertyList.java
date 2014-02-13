package com.application.crashpad;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;

public class PropertyList
{
	private ArrayList<Property> mProperty;
	
	private static PropertyList sPropertyList;
	private Context mAppContext;
	
	private PropertyList(Context appContext)
	{
		mAppContext = appContext;
		mProperty = new ArrayList<Property>();
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
		mProperty.add(p);
	}
	
	public ArrayList<Property> getProperties()
	{
		return mProperty;
	}
	
	public Property getProperty(UUID id)
	{
		for (Property p : mProperty)
		{
			if (p.getId().equals(id))
			{
				return p;
			}
		}
		
		return null;
	}
}
