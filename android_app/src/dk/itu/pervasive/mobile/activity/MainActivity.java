package dk.itu.pervasive.mobile.activity;

import android.os.Bundle;
import android.view.Menu;
import dk.itu.pervasive.mobile.R;

public class MainActivity extends APrefActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		_bindService();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
