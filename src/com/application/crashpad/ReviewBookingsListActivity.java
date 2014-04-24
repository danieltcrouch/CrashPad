package com.application.crashpad;

import android.support.v4.app.Fragment;

public class ReviewBookingsListActivity extends SingleFragmentActivity
{
	@Override
	protected Fragment createFragment()
	{
		return new ReviewBookingsListFragment();
	}
}