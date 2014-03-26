package com.application.crashpad;

import android.support.v4.app.Fragment;
import android.util.Log;

public class ReviewBookedPropertyListActivity extends SingleFragmentActivity
{
	@Override
	protected Fragment createFragment()
	{
		return new ReviewBookedPropertyListFragment();
	}
}