package com.application.crashpad;

import android.support.v4.app.Fragment;

public class EditPropertyActivity extends SingleFragmentActivity
{
	@Override
	protected Fragment createFragment()
	{
		return new EditPropertyFragment();
	}
}
