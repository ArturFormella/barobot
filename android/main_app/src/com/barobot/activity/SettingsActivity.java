package com.barobot.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.text.TextUtils;

import java.util.List;

import com.barobot.R;
import com.barobot.common.Initiator;
import com.barobot.hardware.Arduino;
import com.barobot.hardware.devices.BarobotConnector;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

	//	setFullScreen();
		setupSimplePreferencesScreen();
	}
	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	private void setupSimplePreferencesScreen() {
		if (!isSimplePreferences(this)) {
			return;
		}

		// Add 'general' preferences.
		addPreferencesFromResource(R.xml.pref_general);
		
		bindPreferenceSummaryToValue(findPreference("NEED_GLASS"));
		bindPreferenceSummaryToValue(findPreference("WATCH_GLASS"));

		bindPreferenceSummaryToValue(findPreference("ALLOW_LIGHT_CUP"));
		
		bindPreferenceSummaryToValue(findPreference("MAX_GLASS_CAPACITY"));
		bindPreferenceSummaryToValue(findPreference("SSERVER_ALLOW_CONFIG"));

		bindPreferenceSummaryToValue(findPreference("ALLOW_LANGBAR"));

		bindPreferenceSummaryToValue(findPreference("DRIVER_X_SPEED"));
		bindPreferenceSummaryToValue(findPreference("DRIVER_Y_SPEED"));	
		bindPreferenceSummaryToValue(findPreference("DRIVER_CALIB_X_SPEED"));
		
		bindPreferenceSummaryToValue(findPreference("SERVOZ_POUR_TIME"));
		bindPreferenceSummaryToValue(findPreference("SERVOZ_UP_POS"));
		bindPreferenceSummaryToValue(findPreference("SERVOZ_UP_LIGHT_POS"));
		bindPreferenceSummaryToValue(findPreference("SERVOZ_DOWN_POS"));
		bindPreferenceSummaryToValue(findPreference("SERVOZ_TEST_POS"));
		bindPreferenceSummaryToValue(findPreference("SERVOZ_PAC_POS"));
		bindPreferenceSummaryToValue(findPreference("SERVOZ_PAC_TIME_WAIT"));
		bindPreferenceSummaryToValue(findPreference("SERVOZ_PAC_TIME_WAIT_VOL"));
		bindPreferenceSummaryToValue(findPreference("SERVOY_REPEAT_TIME"));
		bindPreferenceSummaryToValue(findPreference("GLASS_DIFF"));
		bindPreferenceSummaryToValue(findPreference("LIGH_GLASS_DIFF"));
		bindPreferenceSummaryToValue(findPreference("NEED_HALL_X"));	

/*
		// Add 'notifications' preferences, and a corresponding header.
		PreferenceCategory fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle(R.string.pref_header_notifications);
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_notification);

		// Add 'data and sync' preferences, and a corresponding header.
		fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle(R.string.pref_header_data_sync);
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_data_sync);

		// Bind the summaries of EditText/List/Dialog/Ringtone preferences to
		// their values. When their values change, their summaries are updated
		// to reflect the new value, per the Android Design guidelines.
		bindPreferenceSummaryToValue(findPreference("example_text"));
		bindPreferenceSummaryToValue(findPreference("example_list"));
		bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
		bindPreferenceSummaryToValue(findPreference("sync_frequency"));
	 * 
	 * */
	}


	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();
	//		Initiator.logger.i( this.getClass().getName()+"OnPreferenceChangeListener1", preference.getKey()+"/"+ stringValue);

			if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference.setSummary(index >= 0 ? listPreference.getEntries()[index]: null);
				
			} else if (preference instanceof CheckBoxPreference) {
				if("true".equals(stringValue) || "1".equals(stringValue)){
					stringValue = "1";
				}else{
					stringValue = "0";
				}
		//		Initiator.logger.i( this.getClass().getName()+"OnPreferenceChangeListener2", preference.getKey()+"/"+ stringValue);
				
			} else if (preference instanceof RingtonePreference) {
				// For ringtone preferences, look up the correct display value
				// using RingtoneManager.
				if (TextUtils.isEmpty(stringValue)) {
					// Empty values correspond to 'silent' (no ringtone).
					preference.setSummary(R.string.pref_ringtone_silent);
				} else {
					Ringtone ringtone = RingtoneManager.getRingtone(
							preference.getContext(), Uri.parse(stringValue));

					if (ringtone == null) {
						// Clear the summary if there was a lookup error.
						preference.setSummary(null);
					} else {
						// Set the summary to reflect the new ringtone display
						// name.
						String name = ringtone
								.getTitle(preference.getContext());
						preference.setSummary(name);
					}
				}

			} else {
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			Arduino.getInstance().barobot.state.set(preference.getKey(), stringValue);
			return true;
		}
	};

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 *
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		String value = Arduino.getInstance().barobot.state.get(preference.getKey(), "");
		preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
		if(preference instanceof CheckBoxPreference){
			CheckBoxPreference i = (CheckBoxPreference)preference;
		//	Initiator.logger.i( SettingsActivity.class.getSimpleName()+"bindPreferenceSummaryToValue", value );
			if( !value.isEmpty() && "1".equals(value)){
				i.setChecked( true );
			}else{
				i.setChecked( false );
			}
			i.setDefaultValue(value);
		}else{
			preference.setDefaultValue(value);
		}
		sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,value);
	}

	/**
	 * This fragment shows general preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class GeneralPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("example_text"));
			bindPreferenceSummaryToValue(findPreference("example_list"));
		}
	}

	/**
	 * This fragment shows notification preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class NotificationPreferenceFragment extends
			PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_notification);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
		}
	}

	/**
	 * This fragment shows data and sync preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class DataSyncPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_data_sync);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("sync_frequency"));
		}
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context) {
		return !isXLargeTablet(context);
	}

	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}
}
