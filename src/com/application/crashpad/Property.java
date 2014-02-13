package com.application.crashpad;

import java.util.UUID;

public class Property
{
	private UUID mId;
	private String mName;
	
	public Property()
	{
		//Generate Unique Identifier
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
}
