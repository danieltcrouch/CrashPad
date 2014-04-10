package com.application.crashpad;

import android.support.v4.app.Fragment;

public class ReviewBookedPropertyActivity extends SingleFragmentActivity
{
	@Override
	protected Fragment createFragment()
	{
		//FIX
		//UUID propId = (UUID)getIntent().getSerializableExtra(ReviewPropertyFragment.EXTRA_PROP_ID);
		//return ReviewPropertyFragment.newInstance(propId);

		String username = (String)getIntent().getSerializableExtra(ReviewBookedPropertyFragment.EXTRA_PROP_USER);
		String name = (String)getIntent().getSerializableExtra(ReviewBookedPropertyFragment.EXTRA_PROP_NAME);
		String description = (String)getIntent().getSerializableExtra(ReviewBookedPropertyFragment.EXTRA_PROP_DESC);
		Double longitude = (Double)getIntent().getSerializableExtra(ReviewBookedPropertyFragment.EXTRA_PROP_LONG);
		Double latitude = (Double)getIntent().getSerializableExtra(ReviewBookedPropertyFragment.EXTRA_PROP_LAT);
		String dateStart = (String)getIntent().getSerializableExtra(ReviewBookedPropertyFragment.EXTRA_RENT_DAT_S);
		String dateEnd = (String)getIntent().getSerializableExtra(ReviewBookedPropertyFragment.EXTRA_RENT_DAT_E);
		String code = (String)getIntent().getSerializableExtra(ReviewBookedPropertyFragment.EXTRA_RENT_CODE);
		return ReviewBookedPropertyFragment.newInstance(username, name, description, longitude, latitude, dateStart, dateEnd, code);
	}
}
