package com.application.crashpad;

import android.support.v4.app.Fragment;

public class CreateNewAccountActivity extends SingleFragmentActivity {
	
	@Override
	protected Fragment createFragment()
	{
		return new CreateNewAccountFragment();
	}

}
