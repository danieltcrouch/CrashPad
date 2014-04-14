package com.application.crashpad;

import android.support.v4.app.Fragment;

public class ReviewPropertyActivity extends SingleFragmentActivity
{
	@Override
	protected Fragment createFragment()
	{
		return new ReviewPropertyFragment();
	}
}
