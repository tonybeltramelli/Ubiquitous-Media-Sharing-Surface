package dk.itu.pervasive.mobile.socket;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import android.os.AsyncTask;
import android.util.Log;
import dk.itu.pervasive.mobile.data.DataManager;
import dk.itu.pervasive.mobile.utils.UString;
import dk.itu.pervasive.mobile.utils.dataStructure.URLInformation;

/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public class SocketConnection extends AsyncTask<Void, Void, Void>
{
	private String _imagePath;
	
	public SocketConnection(String imagePath)
	{
		_imagePath = imagePath;
	}
	
	@Override
	protected Void doInBackground(Void... arg0)
	{
		Socket socket = null;
		try
		{
			URLInformation urlInformation = UString.getUrlInformation(DataManager.getInstance().getSurfaceAddress());
			
			socket = new Socket(urlInformation.getIp(), urlInformation.getPort());
			
			Log.wtf("SocketConnection", "Connecting...");
			
			File imageFile = new File(_imagePath);
			
			byte[] bytesArray = new byte[(int) _imagePath.length()];
			FileInputStream fileInput = new FileInputStream(_imagePath);
			
			BufferedInputStream inputStream = new BufferedInputStream(fileInput);
			inputStream.read(bytesArray, 0, bytesArray.length);
			OutputStream outputStream = socket.getOutputStream();
			
			Log.wtf("SocketConnection", "Sending...");
						
			outputStream.write(bytesArray, 0, bytesArray.length);
			outputStream.flush();	
			
			inputStream.close();
			outputStream.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				socket.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			
		}
		return null;
	}
}
