package dk.itu.pervasive.mobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import dk.itu.pervasive.mobile.R;
import dk.itu.pervasive.mobile.data.DataManager;

public class MainActivity extends APrefActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		_displayInformation();
	}
	
	private void _displayInformation()
	{
		TextView usernameTextView = (TextView) findViewById(R.id.usernameTextview);
		usernameTextView.setText(getResources().getString(R.string.label_user_name) + " " + DataManager.getInstance().getUsername());
		
		TextView emailTextView = (TextView) findViewById(R.id.emailTextview);
		emailTextView.setText(getResources().getString(R.string.label_email) + " " + DataManager.getInstance().getEmail());
		
		TextView bluetoothTextView = (TextView) findViewById(R.id.bluetoothTextview);
		bluetoothTextView.setText(getResources().getString(R.string.label_bluetooth_id) + " " + DataManager.getInstance().getBluetoothId());
	}
	
	public void changeInformation(View view)
	{
		_displaySettings();
	}
	
	public void displayMap(View view)
	{
		Intent intent = new Intent(this, LocationMapActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onResume()
	{
		_displayInformation();
		
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
