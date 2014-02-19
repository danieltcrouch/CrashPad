package com.application.crashpad;

import java.util.UUID;

import android.support.v4.app.Fragment;

public class PropertyActivity extends SingleFragmentActivity
{
	@Override
	protected Fragment createFragment()
	{
		UUID propId = (UUID)getIntent().getSerializableExtra(PropertyFragment.EXTRA_PROP_ID);
		
		return PropertyFragment.newInstance(propId);
	}
}
