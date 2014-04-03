package dk.itu.pervasive.mobile.socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.apache.commons.io.IOUtils;

import android.os.AsyncTask;
import android.util.Log;
import dk.itu.pervasive.mobile.data.DataManager;
import dk.itu.pervasive.mobile.utils.UString;
import dk.itu.pervasive.mobile.utils.dataStructure.URLInformation;

/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public class SocketConnection extends AsyncTask<String, Void, Void>
{
	public static String SEND = "SEND";
	public static String RECEIVE = "RECEIVE";
	//
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
	protected Void doInBackground(String... types)
	{
		if (types[0] == SocketConnection.SEND)
		{
			_send();
		} else if (types[0] == SocketConnection.RECEIVE)
		{
			_receive();
		}
		
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
			
			Log.wtf("SocketConnection", "Connecting...");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return socket;
	}
	
	private void _send()
	{
		Socket socket = _createSocket();
		
		try
		{
			File imageFile = new File(_imagePath);
			
			long fileSize = imageFile.length();
			if (fileSize > Integer.MAX_VALUE)
			{
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
		}
	}
	
	private void _receive()
	{
		Socket socket = _createSocket();
		
		try
		{
			InputStream is = null;
			try
			{
				is = socket.getInputStream();
				System.out.println("Buffer size: " + socket.getReceiveBufferSize());
			} catch (IOException ex)
			{
				System.out.println("Can't get socket input stream. ");
			}
					
			byte[] bytes = IOUtils.toByteArray(is);
			
			DataManager.getInstance().saveImage("output_"+Math.random() * 1000+".jpg", bytes);
			
			is.close();
			socket.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
