package com.gian1200.games.streetdom;

import java.util.List;
import java.util.Locale;

import android.annotation.TargetApi;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {

	private boolean needResource = true;
	private Locale[] languages;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		languages = ((Application) getApplication()).languages;
		((Application) getApplication()).refreshLanguage();
		if (needResource
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			addPreferencesFromResource(R.xml.preference_headers_legacy);
			prepareVersion(findPreference("versionname"));
			prepareLanguagePreference((ListPreference) findPreference("language"));
			// Log.i("", GooglePlayServicesUtil
			// .getOpenSourceSoftwareLicenseInfo(getActivity()));
		}
	}

	@Override
	public void onBuildHeaders(List<Header> target) {
		if (onIsHidingHeaders() || !onIsMultiPane()) {
		} else {
			needResource = false;
			loadHeadersFromResource(R.xml.preference_headers, target);
		}
	}

	public void prepareVersion(Preference versionPreference) {
		try {
			String versionName = getPackageManager().getPackageInfo(
					getPackageName(), 0).versionName;
			versionPreference.setSummary(versionName);
		} catch (NameNotFoundException e) {
			Log.e("", e.getMessage());
		}
	}

	public void prepareLanguagePreference(
			final ListPreference languagePreference) {
		String[] entries = new String[languages.length];
		String[] entryValues = new String[languages.length];

		for (int i = 0; i < languages.length; i++) {
			if (i == 0) {
				entries[i] = getString(R.string.language_default);
			} else {
				entries[i] = languages[i].getDisplayName();
			}
			entryValues[i] = "" + i;
			if (getResources().getConfiguration().locale.equals(languages[i])) {
				languagePreference.setDefaultValue("" + i);
				languagePreference.setSummary(languages[i].getDisplayName());
			}
		}
		languagePreference.setEntries(entries);
		languagePreference.setEntryValues(entryValues);
		languagePreference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						Locale newLocale = languages[Integer
								.parseInt((String) newValue)];
						languagePreference.setSummary(newLocale
								.getDisplayName());
						Configuration config = new Configuration();
						config.locale = newLocale;
						getResources().updateConfiguration(config,
								getResources().getDisplayMetrics());
						// if (Build.VERSION_CODES.HONEYCOMB <=
						// Build.VERSION.SDK_INT) {
						// Disabled because it blinks
						// recreate();
						// } else {
						startActivity(getIntent());
						finish();
						overridePendingTransition(0, 0);
						// }
						return true;
					}
				});
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class GeneralPreferenceFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);
			((SettingsActivity) getActivity())
					.prepareLanguagePreference((ListPreference) findPreference("language"));
		}

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class NotificationsPreferenceFragment extends
			PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_notification);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class AboutPreferenceFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_about);
			((SettingsActivity) getActivity())
					.prepareVersion(findPreference("versionname"));
		}
	}

}
