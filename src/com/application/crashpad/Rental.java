package com.application.crashpad;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Rental
{
	private SimpleDateFormat mFormat;
	private String mDateStart;
	private String mDateEnd;
	private int mPropId;
	private int mId;
	
	public Rental()
	{
		mFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
	}
		
	public String getDateStart()
	{
		return mDateStart;
	}

	public void setDateStart(String dateStart)
	{
		mDateStart = dateStart;
	}
	
	public Date getDateStartTime()
	{
		try
		{
		    return mFormat.parse(mDateStart);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public String getDateEnd()
	{
		return mDateEnd;
	}

	public void setDateEnd(String dateEnd)
	{
		mDateEnd = dateEnd;
	}

	public Date getDateEndTime()
	{
		try
		{
		    return mFormat.parse(mDateEnd);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
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
		
		try
		{
		    dateStart =  mFormat.parse(mDateStart);
		    dateEnd =  mFormat.parse(mDateEnd);
		    
		    //So present date will be "before" the end date
		    Calendar c = Calendar.getInstance();
		    c.setTime(dateEnd);
		    c.add(Calendar.DATE, 1);
		    dateEnd = c.getTime();
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
	
	public boolean isOver()
	{
		boolean result = false;
		Date present = new Date();
	    Date dateEnd = new Date();
		
		try
		{
		    dateEnd =  mFormat.parse(mDateEnd);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		if (present.after(dateEnd))
		{
			result = true;
		}
		
		return result;
	}
}
