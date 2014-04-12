package com.application.crashpad;

import java.util.UUID;

public class Account
{
	private UUID mId;
	private String mUsername;
	private String mName;
	private String mPassword;
	private String mEmail;
	
	public Account()
	{
		//
	}
	
	public Account(String username, String password, String email, String name)
	{
		mUsername = username;
		mPassword = password;
		mEmail = email;
		mName = name;
	}

	public UUID getId()
	{
		return mId;
	}

	public void setId(UUID id)
	{
		mId = id;
	}

	public String getUsername()
	{
		return mUsername;
	}

	public void setUsername(String username)
	{
		mUsername = username;
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
