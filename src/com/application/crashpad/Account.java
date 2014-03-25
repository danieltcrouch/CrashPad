package com.application.crashpad;

import java.util.UUID;

public class Account
{
	private UUID mId;
	private String mName;
	private String mPassword;
	private String mEmail;
	
	public Account()
	{
		mId = UUID.randomUUID();
	}
	
	public Account(String name, String password, String email)
	{
		mId = UUID.randomUUID();
		mName = name;
		mPassword = password;
		mEmail = email;
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

	public String getEmail()
	{
		return mEmail;
	}

	public void setEmail(String email)
	{
		mEmail = email;
	}

	public String getPassword()
	{
		return mPassword;
	}

	public void setPassword(String password)
	{
		mPassword = password;
	}	
}
