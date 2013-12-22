package com.gian1200.games.streetdom;

import java.util.List;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.ContextThemeWrapper;

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
			prepareLanguagePreference((ListPreference) findPreference("language"));
			prepareEraseData(findPreference("erase_data"));
			prepareLicense(findPreference("license"));
			prepareUs(findPreference("us"));
			prepareVersion(findPreference("versionname"));
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onBuildHeaders(List<Header> target) {
		if (onIsHidingHeaders() || !onIsMultiPane()) {
		} else {
			needResource = false;
			loadHeadersFromResource(R.xml.preference_headers, target);
		}
	}

	public void prepareLanguagePreference(
			final ListPreference languagePreference) {
		String[] entries = new String[languages.length];
		String[] entryValues = new String[languages.length];
		int defaultValueIndex = 0;
		for (int i = 0; i < languages.length; i++) {
			if (i == 0) {
				entries[i] = getString(R.string.language_default);
			} else {
				entries[i] = languages[i].getDisplayName();
			}
			entryValues[i] = "" + i;
			if (getResources().getConfiguration().locale.equals(languages[i])) {
				defaultValueIndex = i;
				languagePreference.setSummary(languages[i].getDisplayName());
			}
		}
		languagePreference.setEntries(entries);
		languagePreference.setEntryValues(entryValues);
		languagePreference.setValueIndex(defaultValueIndex);
		languagePreference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						((Application) getApplication()).refreshLanguage();
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
						finish();
						startActivity(getIntent());
						overridePendingTransition(0, 0);
						// }
						return true;
					}
				});
	}

	public void prepareEraseData(Preference eraseDataPreference) {
		eraseDataPreference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@TargetApi(Build.VERSION_CODES.HONEYCOMB)
					@Override
					public boolean onPreferenceClick(Preference preference) {
						AlertDialog.Builder builder;
						if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
							ContextThemeWrapper contextW = new ContextThemeWrapper(
									SettingsActivity.this,
									R.style.AppAlertDialogTheme);
							builder = new AlertDialog.Builder(contextW);
						} else {
							builder = new AlertDialog.Builder(
									SettingsActivity.this,
									R.style.AppAlertDialogTheme);
						}
						builder.setTitle(R.string.erase_data);
						builder.setMessage(R.string.erase_data_message);
						builder.setPositiveButton(android.R.string.ok,
								new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										((Application) getApplication())
												.removeData();
										setResult(getResources()
												.getInteger(
														R.integer.result_code_erase_data));
										finish();
									}
								});
						builder.setNegativeButton(android.R.string.no,
								new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// do nothing
									}
								});
						builder.show();
						return true;
					}
				});
	}

	public void prepareUs(Preference usPreference) {
		// TODO prepare Nosotros (créditos)
	}

	public void prepareLicense(Preference licensePreference) {
		// Log.d("",
		// GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this));
		// TODO prepare License
	}

	public void prepareVersion(Preference versionPreference) {
		versionPreference.setSummary(((Application) getApplication())
				.getApplicationVersionName());
		versionPreference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						Uri uri = Uri.parse(((Application) getApplication())
								.getMarketAndroidLink());
						startActivity(new Intent(Intent.ACTION_VIEW, uri));
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
	public static class DataPreferenceFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_data);
			((SettingsActivity) getActivity())
					.prepareEraseData(findPreference("erase_data"));
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class AboutPreferenceFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_about);
			((SettingsActivity) getActivity())
					.prepareLicense(findPreference("license"));
			((SettingsActivity) getActivity()).prepareUs(findPreference("us"));
			((SettingsActivity) getActivity())
					.prepareVersion(findPreference("versionname"));

		}
	}

}
