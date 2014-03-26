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
	
	//Move this to Account Page
	/*Button contactLeaserButton = (Button)v.findViewById(R.id.contact_renter);
	contactLeaserButton.setOnClickListener(new View.OnClickListener()
	{
		public void onClick(View v)
		{
			Intent send = new Intent(Intent.ACTION_SEND);
			send.setType("text/plain");
			final Intent i = Intent.createChooser(send, "Select method of contact");
			startActivity(i);
		}
	});*/
}
