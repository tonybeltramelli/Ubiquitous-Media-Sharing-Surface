package dk.itu.pervasive.mobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import dk.itu.pervasive.mobile.R;
import dk.itu.pervasive.mobile.data.DataManager;
import dk.itu.pervasive.mobile.preferences.CustomPreference;

/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public abstract class APrefActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		DataManager.getInstance().setContext(this);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_settings:
				_displaySettings();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	protected void _displaySettings()
	{
		Intent intent = new Intent(this, CustomPreference.class);
	    startActivity(intent);
	}
}
