package dk.itu.pervasive.mobile.socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
	private RequestDelegate _delegate;
	private String _imagePath;
	private int _index;
	
	public SocketConnection(RequestDelegate delegate, String imagePath, int index)
	{
		_delegate = delegate;
		_imagePath = imagePath;
		_index = index;
	}
	
	@Override
	protected Void doInBackground(Void... arg0)
	{
		Log.wtf("image", "send image");
		
		Socket socket = null;
		try
		{
			URLInformation urlInformation = UString.getUrlInformation(DataManager.getInstance().getSurfaceAddress());
			
			socket = new Socket(urlInformation.getIp(), urlInformation.getPort());
			socket.setKeepAlive(false);
			
			Log.wtf("SocketConnection", "Connecting...");
			
			File imageFile = new File(_imagePath);
	        
	        long fileSize = imageFile.length();
	        if (fileSize > Integer.MAX_VALUE) {
	        	Log.wtf("SocketConnection", "File is too large.");
	        }
	        
	        byte[] bytes = new byte[(int) fileSize];
	        
	        FileInputStream fis = new FileInputStream(imageFile);
	        BufferedInputStream bis = new BufferedInputStream(fis);
	        BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

	        int count;
	        while ((count = bis.read(bytes)) > 0)
	        {
	            out.write(bytes, 0, count);
	        }

	        out.flush();
	        out.close();
	        fis.close();
	        bis.close();
	        socket.close();
			
			_delegate.onRequestSuccess(_index + 1);
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
