package com.gian1200.games.streetdom.activities;

import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.gian1200.games.streetdom.Application;
import com.gian1200.games.streetdom.R;
import com.gian1200.games.streetdom.activities.fragments.ListBuyableMissionsFragment;
import com.gian1200.games.streetdom.activities.fragments.ListCompletedMissionsFragment;
import com.gian1200.games.streetdom.activities.fragments.ListIncompletedMissionsFragment;

public class MissionsActivity extends FragmentActivity {

	ListView missionsList;
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	private Locale locale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((Application) getApplication()).refreshLanguage();
		locale = getResources().getConfiguration().locale;
		setContentView(R.layout.activity_missions);
		mViewPager = (ViewPager) findViewById(R.id.missions_pager);
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(1);
		PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.missions_pager_tab_strip);
		pagerTabStrip.setTabIndicatorColorResource(R.color.edwin_green_light);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.missions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivityForResult(
					new Intent(this, SettingsActivity.class),
					getResources().getInteger(
							R.integer.request_code_settings_activity));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!locale.equals(getResources().getConfiguration().locale)) {
			finish();
			startActivity(getIntent());
			overridePendingTransition(0, 0);
			return;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (getResources().getInteger(R.integer.result_code_erase_data) == resultCode) {
			setResult(resultCode);
			finish();
			return;
		}
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment listMissionsFragment = null;
			switch (position) {
			case 0:
				listMissionsFragment = new ListCompletedMissionsFragment();
				break;
			case 1:
				listMissionsFragment = new ListIncompletedMissionsFragment();
				break;
			case 2:
				listMissionsFragment = new ListBuyableMissionsFragment();
				break;
			}
			return listMissionsFragment;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale locale = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.completed_missions).toUpperCase(
						locale);
			case 1:
				return getString(R.string.incompleted_missions).toUpperCase(
						locale);
			case 2:
				return getString(R.string.new_missions).toUpperCase(locale);
			default:
				return getString(R.string.missions).toUpperCase(locale);
			}
		}
	}
}
