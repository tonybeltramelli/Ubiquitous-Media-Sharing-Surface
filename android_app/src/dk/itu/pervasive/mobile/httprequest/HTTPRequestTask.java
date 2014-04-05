package dk.itu.pervasive.mobile.httprequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import dk.itu.pervasive.mobile.data.DataManager;
import dk.itu.pervasive.mobile.utils.Config;

/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public class HTTPRequestTask extends AsyncTask<String, Integer, JsonObject[]>
{
	private RequestDelegate _delegate;
	
	public HTTPRequestTask(RequestDelegate delegate)
	{
		_delegate = delegate;
	}
	
	protected JsonObject[] doInBackground(String... types)
	{
		JsonObject[] result = null;
		
		if (types[0] == Config.BLIP_GET_ALL_LOCATIONS_URL)
		{
			result = _getAllLocations();
		} else if (types[0] == Config.GOOGLE_MAP_ROUTE_URL && types.length == 5)
		{
			result = _getRoute(types[1], types[2], types[3], types[4]);
		} else if (types[0] == DataManager.getInstance().getServerAddress() && types.length == 2)
		{
			result = _sendData(types[0], types[1]);
		}
		
		return result;
	}
	
	protected void onProgressUpdate(Integer progress)
	{
		Log.wtf("onProgressUpdate", String.valueOf(progress));
	}
	
	protected void onPostExecute(JsonObject[] result)
	{
		_delegate.onRequestSuccess(result);
	}
	
	private JsonObject[] _getAllLocations()
	{
		JsonObject[] results = null;
		
		JsonArray allLocations = _getJSONFromGetRequest(Config.BLIP_GET_ALL_LOCATIONS_URL);
		
		if (allLocations != null)
		{
			results = new JsonObject[allLocations.size()];
			
			for (int i = 0; i < allLocations.size(); i++)
			{
				JsonObject jsonObject = allLocations.get(i).asObject();
				results[i] = jsonObject;
			}
		}
		
		return results;
	}
	
	private JsonObject[] _getRoute(String startLatitude, String startLongitude, String endLatitude, String endLongitude)
	{
		JsonObject[] results = null;
		
		String url = Config.GOOGLE_MAP_ROUTE_URL;
		url = url.replace(Config.START_LATITUDE, startLatitude);
		url = url.replace(Config.START_LONGITUDE, startLongitude);
		url = url.replace(Config.END_LATITUDE, endLatitude);
		url = url.replace(Config.END_LONGITUDE, endLongitude);
		
		BufferedReader reader = _performGetRequest(url);
		
		if (reader != null)
		{
			try
			{
				JsonObject json = JsonObject.readFrom(reader);
				JsonArray steps = json.get("routes").asArray().get(0).asObject().get("legs").asArray().get(0)
						.asObject().get("steps").asArray();
				
				results = new JsonObject[steps.size()];
				
				for (int i = 0; i < steps.size(); i++)
				{
					JsonObject jsonObject = steps.get(i).asObject();
					results[i] = jsonObject;
				}
				
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return results;
	}
	
	private JsonObject[] _sendData(String url, String data)
	{
		_performPostRequest(url, data);
		
		return null;
	}
	
	private JsonArray _getJSONFromGetRequest(String urlAddress)
	{
		JsonArray json = null;
		
		BufferedReader reader = _performGetRequest(urlAddress);
		
		if (reader != null)
		{
			try
			{
				json = JsonArray.readFrom(reader);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return json;
	}
	
	private BufferedReader _performGetRequest(String urlAddress)
	{
		BufferedReader reader = null;
		
		try
		{
			URL url = new URL(urlAddress);
			Log.wtf("_performGetRequest", "Sending GET request to URL : " + urlAddress);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			
			int responseCode = connection.getResponseCode();
			Log.wtf("_performGetRequest", "Response Code : " + responseCode);
			
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return reader;
	}
	
	private void _performPostRequest(String urlAddress, String data)
	{
		try
		{
			URL url = new URL(urlAddress);
			Log.wtf("_performPostRequest", "Sending POST request to URL : " + urlAddress + ", data : " + data);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			
			String params = data;
			
			OutputStream output = connection.getOutputStream();
			output.write(params.getBytes());
			output.close();
			
			int responseCode = connection.getResponseCode();
			Log.wtf("_performPostRequest", "Response Code : " + responseCode);
			
			connection.disconnect();
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
