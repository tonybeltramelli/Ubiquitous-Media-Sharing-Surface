package dk.itu.pervasive.mobile.socket;

import java.io.IOException;
import java.net.Socket;

import dk.itu.pervasive.mobile.data.DataManager;
import dk.itu.pervasive.mobile.utils.UString;
import dk.itu.pervasive.mobile.utils.dataStructure.URLInformation;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public class SocketCreatingTask extends AsyncTask<String, Void, Void>
{
	private RequestDelegate _delegate;
	private Socket _socket;
	
	public SocketCreatingTask(RequestDelegate delegate)
	{
		_delegate = delegate;
	}

	@Override
	protected Void doInBackground(String... params)
	{
		_socket = _createSocket();
		
		return null;
	}
	
	private Socket _createSocket()
	{
		Socket socket = null;
		
		try
		{
			URLInformation urlInformation = UString.getUrlInformation(DataManager.getInstance().getSurfaceAddress());
			
			socket = new Socket(urlInformation.getIp(), urlInformation.getPort());
			socket.setKeepAlive(false);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return socket;
	}
	
	@Override
    protected void onPostExecute(Void result)
	{
        _delegate.onRequestCreateSuccess(_socket);
        
        super.onPostExecute(result);
    }
}
