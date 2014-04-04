package dk.itu.pervasive.mobile.socket;

import android.os.AsyncTask;
import android.util.Log;
import dk.itu.pervasive.mobile.data.DataManager;
import dk.itu.pervasive.mobile.utils.UString;
import dk.itu.pervasive.mobile.utils.dataStructure.URLInformation;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;

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
    private String _type;
	
	public SocketConnection(RequestDelegate delegate, String imagePath, int index)
	{
		_delegate = delegate;
		_imagePath = imagePath;
		_index = index;
	}
	
	@Override
	protected Void doInBackground(String... types)
	{
        _type = types[0];

		if (_type == SocketConnection.SEND)
		{
			_send();
		} else if (_type == SocketConnection.RECEIVE)
		{
			_receive();
		}
		
		return null;
	}

    @Override
    protected void onPostExecute(Void aVoid) {

        if( _type == SocketConnection.SEND )
            _delegate.onRequestSuccess(_index + 1);


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
