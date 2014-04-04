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
public class TOREMOVE_SocketConnection extends AsyncTask<String, Void, Void>
{
	public static String SEND = "SEND";
	public static String RECEIVE = "RECEIVE";
	public static String CREATE = "CREATE";
	//
	private RequestDelegate _delegate;
	private String _imagePath;
	private int _index;
    private String _type;
    private Socket _socket;
	
    public TOREMOVE_SocketConnection(RequestDelegate delegate)
	{
    	_delegate = delegate;
	}
    
	public TOREMOVE_SocketConnection(RequestDelegate delegate, Socket socket, String imagePath, int index)
	{
		_delegate = delegate;
		_socket = socket;
		_imagePath = imagePath;
		_index = index;
	}
	
	@Override
	protected Void doInBackground(String... types)
	{
        _type = types[0];

		if (_type == TOREMOVE_SocketConnection.SEND)
		{
			_send();
		} else if (_type == TOREMOVE_SocketConnection.RECEIVE)
		{
			_receive();
		} else if (_type == TOREMOVE_SocketConnection.CREATE)
		{
			_create();
		}
		
		return null;
	}

    private void _create()
	{
    	_socket = _createSocket();
	}

	@Override
    protected void onPostExecute(Void aVoid) {
		/*
        if( _type == SocketConnection.SEND )
        {
            _delegate.onRequestSendSuccess(_index + 1);
        } else if( _type == SocketConnection.RECEIVE )
        {
        	_delegate.onRequestReceiveSuccess();
        }  else if( _type == SocketConnection.CREATE )
        {
        	_delegate.onRequestCreateSuccess(_socket);
        }*/
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
		try
		{
			File imageFile = new File(_imagePath);
			
			long fileSize = imageFile.length();
			if (fileSize > Integer.MAX_VALUE)
			{
				Log.wtf("SocketConnection", "File is too large.");
			}
			
			Log.i("TAG", "sending image : "+fileSize);
			
			byte[] bytes = new byte[(int) fileSize];
			
			FileInputStream fis = new FileInputStream(imageFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			BufferedOutputStream out = new BufferedOutputStream(_socket.getOutputStream());
			
			out.write(String.valueOf(fileSize).getBytes());
			
			int count;
			while ((count = bis.read(bytes)) > 0)
			{
				out.write(bytes, 0, count);
				Log.i("TAG", "sending data");
			}
			
			out.flush();
			//out.close();
			fis.close();
			bis.close();
			
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
