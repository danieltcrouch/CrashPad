package com.application.crashpad;

import android.support.v4.app.Fragment;

public class ReviewPropertyListActivity extends SingleFragmentActivity
{
	@Override
	protected Fragment createFragment()
	{
		return new ReviewPropertyListFragment();
	}
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public void onLogoutPressed()
	{
		new AlertDialog.Builder(this)
			.setTitle("Log Out Confirmation")
			.setMessage("Are you sure you want to log out?")
			.setPositiveButton("Log Out", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					Intent i = new Intent(ReviewPropertyListActivity.this, LoginActivity.class);
		            startActivity(i);
				}
			})
			.setNegativeButton("No", null)
			.show();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch( item.getItemId())
		{
		case R.id.help_page:
			Intent i = new Intent(ReviewPropertyListActivity.this, HelpActivity.class);
			startActivity(i);
			return true;
		case R.id.logout:
			onLogoutPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}*/
}