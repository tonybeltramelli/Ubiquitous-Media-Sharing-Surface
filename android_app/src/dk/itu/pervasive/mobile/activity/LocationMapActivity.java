package dk.itu.pervasive.mobile.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eclipsesource.json.JsonObject;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import dk.itu.pervasive.mobile.R;
import dk.itu.pervasive.mobile.httprequest.HTTPRequestTask;
import dk.itu.pervasive.mobile.httprequest.RequestDelegate;
import dk.itu.pervasive.mobile.utils.Config;
import dk.itu.pervasive.mobile.utils.GMapUtils;

public class LocationMapActivity extends APrefActivity implements RequestDelegate, ConnectionCallbacks, OnConnectionFailedListener, LocationListener
{
	private GoogleMap _map;
	private ProgressBar _bar;
	private LocationClient _client;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_map);
		
		_checkPermission();
		
		_map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		_map.setMyLocationEnabled(true);
		
		_bar = (ProgressBar) findViewById(R.id.progressBar);
		
		_client = new LocationClient(this, this, this);
	}
	
	private void _loadData()
	{
		HTTPRequestTask blipRequestController = new HTTPRequestTask(this);
		
		Location userLocation = _client.getLastLocation();
		
		LatLng start = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
		LatLng end = new LatLng(Config.ITU_LATITUDE, Config.ITU_LONGITUDE);
		
		blipRequestController.execute(Config.GOOGLE_MAP_ROUTE_URL, String.valueOf(start.latitude), String.valueOf(start.longitude),
				String.valueOf(end.latitude), String.valueOf(end.longitude));
	}
	
	private void _checkPermission()
	{
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		if (!enabled)
		{
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}
	}
	
	private void _addLocationUpdates()
	{
		LocationRequest request = new LocationRequest();
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		request.setFastestInterval(Config.FASTEST_INTERVAL);
		request.setInterval(Config.UPDATE_INTERVAL);
		
		_client.requestLocationUpdates(request, this);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		_client.connect();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		
		_client.removeLocationUpdates(this);
		_client.disconnect();
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
			PolylineOptions line = new PolylineOptions().width(3).color(Color.RED);
			
			for (int i = 0; i < result.length; i++)
			{
				ArrayList<LatLng> polyline = GMapUtils.getLatLngFromEncodedPolyPoints(result[i].get("polyline").asObject().get("points").asString());
				
				for (int j = 0; j < polyline.size(); j++)
				{
					LatLng coordinate = new LatLng(polyline.get(j).latitude, polyline.get(j).longitude);
					Log.wtf("coordinate", coordinate.latitude + " - " + coordinate.longitude);
					
					line.add(coordinate);
				}
			}
			
			_bar.setVisibility(View.INVISIBLE);
			
			_map.addPolyline(line);
			_map.moveCamera(CameraUpdateFactory.newLatLngZoom(line.getPoints().get(0), 16));
		}
	}
	
	@Override
	public void onConnected(Bundle arg0)
	{
		_loadData();
		
		_addLocationUpdates();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0)
	{
		Toast.makeText(this, "Google Play Services Client : Connection Failed", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDisconnected()
	{
		Toast.makeText(this, "Google Play Services Client : Disconnected", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onLocationChanged(Location arg0)
	{
		Location userLocation = _client.getLastLocation();
		LatLng destination = new LatLng(Config.ITU_LATITUDE, Config.ITU_LONGITUDE);
		
		float[] results = new float[3];
		
		Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(), destination.latitude, destination.longitude, results);
		
		float distance = results[0];
		
		Toast.makeText(this, "Distance from ITU "+distance, Toast.LENGTH_LONG).show();
		
		if(distance < Config.MINIMUM_DISTANCE)
		{
			Intent intent = new Intent(this, LocationBuildingActivity.class);
			startActivity(intent);
		}
	}
}
