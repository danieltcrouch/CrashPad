package com.application.crashpad;

import android.content.Context;

public class PresentAccount
{
	private static PresentAccount sPresentAccount;
	private Account mUser;
	
	private PresentAccount(Context appContext)
	{
		mUser = new Account();
	}
	
	public static PresentAccount get(Context c)
	{
		if (sPresentAccount == null)
		{
			sPresentAccount = new PresentAccount(c.getApplicationContext());
		}
		
		return sPresentAccount;
	}
	
	public void setPresentAccount(Account a)
	{
		mUser = a;
	}
	
	public Account getPresentAccount()
	{
		return mUser;
	}
}
