package dk.itu.pervasive.mobile.data;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public class DataManager
{
	public static final String PREF_KEY_SAVE = "save";
	public static final String PREF_KEY_USERNAME = "username";
	public static final String PREF_KEY_EMAIL = "email";
	public static final String PREF_KEY_SERVER_ADDRESS = "serverAddress";
	
	private static DataManager _instance = null;
	
	private Activity _context;
	private String _username = "";
	private String _email = "";
	private String _serverAddress = "";
	
	private DataManager()
	{
	}
	
	public static DataManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new DataManager();
		}
		
		return _instance;
	}
	
	public void saveData()
	{
		_username = PreferenceManager.getDefaultSharedPreferences(_context).getString(PREF_KEY_USERNAME, "");
		_email = PreferenceManager.getDefaultSharedPreferences(_context).getString(PREF_KEY_EMAIL, "");
		_serverAddress = PreferenceManager.getDefaultSharedPreferences(_context).getString(PREF_KEY_SERVER_ADDRESS, "");
		
		Log.wtf("save data", _username + ", " + _email + ", " + _serverAddress);	
	}
	
	public String getUsername()
	{
		return _username;
	}
	
	public String getEmail()
	{
		return _email;
	}
	
	public String getServerAddress()
	{
		return _serverAddress;
	}
	
	public void setContext(Activity context)
	{
		_context = context;
		
		saveData();
	}
}
