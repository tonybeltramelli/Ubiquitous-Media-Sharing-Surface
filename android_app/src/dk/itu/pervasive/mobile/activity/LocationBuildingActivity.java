package dk.itu.pervasive.mobile.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.eclipsesource.json.JsonObject;

import dk.itu.pervasive.mobile.R;
import dk.itu.pervasive.mobile.data.DataManager;
import dk.itu.pervasive.mobile.httprequest.HTTPRequestTask;
import dk.itu.pervasive.mobile.httprequest.RequestDelegate;
import dk.itu.pervasive.mobile.utils.Config;

public class LocationBuildingActivity extends APrefActivity implements RequestDelegate
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_building);
		
		HTTPRequestTask blipRequestController = new HTTPRequestTask(this);
		blipRequestController.execute(Config.BLIP_GET_ALL_LOCATIONS_URL);
		
		_registerToJCAFServer(true);
	}
	
	public void enterLeaveITU(View view)
	{
		Button button = (Button) view;
		
		if (button.getText() == getResources().getText(R.string.leave_itu_text))
		{
			button.setText(R.string.enter_itu_text);
			_registerToJCAFServer(false);
		} else if (button.getText() == getResources().getText(R.string.enter_itu_text))
		{
			button.setText(R.string.leave_itu_text);
			_registerToJCAFServer(true);
		}
	}
	
	private void _registerToJCAFServer(Boolean toRegister)
	{
		JsonObject data = new JsonObject();
		data.add(Config.DATA_KEY_NAME, DataManager.getInstance().getUsername());
		data.add(Config.DATA_KEY_USER_ID, DataManager.getInstance().getEmail() + "-"
				+ DataManager.getInstance().getBluetoothId());
		data.add(Config.DATA_KEY_BLUETOOTH_ID, DataManager.getInstance().getBluetoothId());
		data.add(Config.DATA_KEY_ACTION, toRegister ? Config.DATA_VALUE_ENTERING : Config.DATA_VALUE_LEAVING);
		
		HTTPRequestTask blipRequestController = new HTTPRequestTask(this);
		blipRequestController.execute(DataManager.getInstance().getServerAddress(), data.toString());
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.location_map, menu);
		return true;
	}
	
	@Override
	public void onRequestSuccess(JsonObject[] result)
	{
		if (result != null)
		{
			ListView listView = (ListView) findViewById(R.id.listView);
			ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.list_row);
			
			for (int i = 0; i < result.length; i++)
			{	
				Log.wtf("room", result[i].toString());
				
				listAdapter.add(result[i].get("location-name").asString());
			}
			
			listView.setAdapter(listAdapter);
		}
	}
}
