package com.application.crashpad;

import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

public class CrashPadPagerActivity extends FragmentActivity
{
    ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        final ArrayList<Property> crimes = PropertyList.get(this).getProperties();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm)
        {
            @Override
            public int getCount()
            {
                return crimes.size();
            }
            @Override
            public Fragment getItem(int pos)
            {
                UUID crimeId =  crimes.get(pos).getId();
                return PropertyFragment.newInstance(crimeId);
            }
        }); 

        UUID crimeId = (UUID)getIntent().getSerializableExtra(PropertyFragment.EXTRA_PROP_ID);
        for (int i = 0; i < crimes.size(); i++)
        {
            if (crimes.get(i).getId().equals(crimeId))
            {
                mViewPager.setCurrentItem(i);
                break;
            } 
        }
    }
}
