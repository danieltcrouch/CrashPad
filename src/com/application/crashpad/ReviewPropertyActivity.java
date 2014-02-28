package com.application.crashpad;

import java.util.UUID;

import android.support.v4.app.Fragment;

public class ReviewPropertyActivity extends SingleFragmentActivity
{
	@Override
	protected Fragment createFragment()
	{
		UUID propId = (UUID)getIntent().getSerializableExtra(ReviewPropertyFragment.EXTRA_PROP_ID);
		return ReviewPropertyFragment.newInstance(propId);
	}
}
