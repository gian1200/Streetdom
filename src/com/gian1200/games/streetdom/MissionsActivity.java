package com.gian1200.games.streetdom;

import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MissionsActivity extends Activity {

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
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
		mViewPager.setAdapter(mSectionsPagerAdapter);
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
			startActivity(new Intent(this, SettingsActivity.class));
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

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment listMissionsFragment = null;
			Bundle args = new Bundle();
			switch (position) {
			case 0:
				listMissionsFragment = new ListMissionsFragment();
				args.putParcelableArrayList("missions",
						((Application) getApplication()).completedMissions);
				break;
			case 1:
				listMissionsFragment = new ListMissionsFragment();
				args.putParcelableArrayList("missions",
						((Application) getApplication()).incompletedMissions);
				break;
			case 2:
				listMissionsFragment = new ListBuyableMissionsFragment();
				args.putParcelableArrayList("missions",
						((Application) getApplication()).buyableMissions);
				break;
			}
			listMissionsFragment.setArguments(args);
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
