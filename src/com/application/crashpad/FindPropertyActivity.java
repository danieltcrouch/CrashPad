package com.application.crashpad;

import java.util.UUID;

import android.support.v4.app.Fragment;

public class FindPropertyActivity extends SingleFragmentActivity
{
	@Override
	protected Fragment createFragment()
	{
		UUID propId = (UUID)getIntent().getSerializableExtra(FindPropertyFragment.EXTRA_PROP_ID);
		return FindPropertyFragment.newInstance(propId);
	}
}
