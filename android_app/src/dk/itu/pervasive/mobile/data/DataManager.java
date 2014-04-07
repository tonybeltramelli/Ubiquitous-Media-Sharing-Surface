package dk.itu.pervasive.mobile.data;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import dk.itu.pervasive.mobile.R;

import java.io.FileOutputStream;

/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public class DataManager
{
	public static final String PREF_KEY_SAVE = "save";
	public static final String PREF_KEY_USERNAME = "username";
	public static final String PREF_KEY_SURFACE_ADDRESS = "surfaceAddress";
	public static final String PREF_KEY_STICKER_ID = "stickerID";
	
	private static DataManager _instance = null;
	
	private Activity _context;
	private String _username = "";
	private String _surfaceAddress = "";
	private String _stickerID = "";
	
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
		_username = PreferenceManager.getDefaultSharedPreferences(_context).getString(PREF_KEY_USERNAME, _context.getResources().getString(R.string.preference_user_name_default));
		_surfaceAddress = PreferenceManager.getDefaultSharedPreferences(_context).getString(PREF_KEY_SURFACE_ADDRESS, _context.getResources().getString(R.string.preference_surface_address_default));
		_stickerID = PreferenceManager.getDefaultSharedPreferences(_context).getString(PREF_KEY_STICKER_ID, _context.getResources().getString(R.string.preference_sticker_id_default));
		
		Log.wtf("save data", _username + ", " + _surfaceAddress + ", " + _stickerID);
	}
	
	public String getPathFromUri(Uri uri)
	{
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = _context.getContentResolver().query(uri, projection, null, null, null);
		
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		
		return cursor.getString(column_index);
	}
	
	public void saveImage(String imageName, byte[] bytes)
	{
		FileOutputStream fos;
		try
		{
			fos = _context.openFileOutput(imageName, Context.MODE_PRIVATE);
			fos.write(bytes);
			fos.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void displayMessage(final String message)
	{
		_context.runOnUiThread(new Runnable() {
		    public void run() {
		        Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
		    }
		});
	}
	
	public String getUsername()
	{
		return _username;
	}
	
	public String getSurfaceAddress()
	{
		return _surfaceAddress;
	}
	
	public String getStickerID()
	{
		return _stickerID;
	}
	
	public void setContext(Activity context)
	{
		_context = context;
		
		saveData();
	}

    public Context getContext(){
        return _context;
    }
}
