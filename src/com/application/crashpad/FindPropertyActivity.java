package com.application.crashpad;

import android.support.v4.app.Fragment;

public class FindPropertyActivity extends SingleFragmentActivity
{
	@Override
	protected Fragment createFragment()
	{
		//UUID propId = (UUID)getIntent().getSerializableExtra(FindPropertyFragment.EXTRA_PROP_ID);
		//return FindPropertyFragment.newInstance(propId);

		String username = (String)getIntent().getSerializableExtra(FindPropertyFragment.EXTRA_PROP_USER);
		String name = (String)getIntent().getSerializableExtra(FindPropertyFragment.EXTRA_PROP_NAME);
		String description = (String)getIntent().getSerializableExtra(FindPropertyFragment.EXTRA_PROP_DESC);
		Double longitude = (Double)getIntent().getSerializableExtra(FindPropertyFragment.EXTRA_PROP_LONG);
		Double latitude = (Double)getIntent().getSerializableExtra(FindPropertyFragment.EXTRA_PROP_LAT);
		return FindPropertyFragment.newInstance(username, name, description, longitude, latitude);
	}
}
