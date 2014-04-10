package com.application.crashpad;

import android.content.Context;

public class AccountCurrent
{
	private static AccountCurrent sAccountCurrent;
	private Account mUser;
	
	private AccountCurrent(Context appContext)
	{
		mUser = new Account();
	}
	
	public static AccountCurrent get(Context c)
	{
		if (sAccountCurrent == null)
		{
			sAccountCurrent = new AccountCurrent(c.getApplicationContext());
		}
		
		return sAccountCurrent;
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
