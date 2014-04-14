package com.application.crashpad;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Rental
{
	private String mDateStart;
	private String mDateEnd;
	private int mPropId;
	private int mId;
	
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

	public int getPropId()
	{
		return mPropId;
	}

	public void setPropId(int propId)
	{
		mPropId = propId;
	}

	public int getId()
	{
		return mId;
	}

	public void setId(int id)
	{
		mId = id;
	}
	
	public boolean presentlyRenting()
	{
		boolean result = false;
		Date present = new Date();
		Date dateStart = new Date();
	    Date dateEnd = new Date();
		
	    //FIX
	    //Won't work for day-of
	    //Actually, I'm not sure that's true. Check it.
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
		
		if (present.after(dateStart) && present.before(dateEnd))
		{
			result = true;
		}
		
		return result;
	}
}
