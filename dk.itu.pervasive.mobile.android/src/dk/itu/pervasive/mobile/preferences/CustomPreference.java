package dk.itu.pervasive.mobile.preferences;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public class CustomPreference extends PreferenceActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();		
	}
}
