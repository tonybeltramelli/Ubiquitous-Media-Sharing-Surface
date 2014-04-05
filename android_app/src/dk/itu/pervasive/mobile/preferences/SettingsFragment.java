package dk.itu.pervasive.mobile.preferences;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import dk.itu.pervasive.mobile.R;
import dk.itu.pervasive.mobile.data.DataManager;

/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public class SettingsFragment extends PreferenceFragment implements OnPreferenceClickListener, OnPreferenceChangeListener
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
		
		findPreference(DataManager.PREF_KEY_SAVE).setOnPreferenceClickListener(this);
		findPreference(DataManager.PREF_KEY_USERNAME).setOnPreferenceChangeListener(this);
		findPreference(DataManager.PREF_KEY_EMAIL).setOnPreferenceChangeListener(this);
		findPreference(DataManager.PREF_KEY_SERVER_ADDRESS).setOnPreferenceChangeListener(this);
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference)
	{
		DataManager.getInstance().saveData();
		return true;
	}
	
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue)
	{
		DataManager.getInstance().saveData();
		return true;
	}
}
