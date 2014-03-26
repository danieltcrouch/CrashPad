package com.application.crashpad;

import android.support.v4.app.Fragment;

public class ReviewPropertyActivity extends SingleFragmentActivity
{
	@Override
	protected Fragment createFragment()
	{
		//UUID propId = (UUID)getIntent().getSerializableExtra(ReviewPropertyFragment.EXTRA_PROP_ID);
		//return ReviewPropertyFragment.newInstance(propId);

		String username = (String)getIntent().getSerializableExtra(ReviewPropertyFragment.EXTRA_PROP_USER);
		String name = (String)getIntent().getSerializableExtra(ReviewPropertyFragment.EXTRA_PROP_NAME);
		String description = (String)getIntent().getSerializableExtra(ReviewPropertyFragment.EXTRA_PROP_DESC);
		Double longitude = (Double)getIntent().getSerializableExtra(ReviewPropertyFragment.EXTRA_PROP_LONG);
		Double latitude = (Double)getIntent().getSerializableExtra(ReviewPropertyFragment.EXTRA_PROP_LAT);
		return ReviewPropertyFragment.newInstance(username, name, description, longitude, latitude);
	}
}
