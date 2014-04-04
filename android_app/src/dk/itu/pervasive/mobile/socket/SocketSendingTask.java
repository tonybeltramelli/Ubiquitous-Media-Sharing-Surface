package dk.itu.pervasive.mobile.socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.eclipsesource.json.JsonObject;

import dk.itu.pervasive.mobile.gallery2.ImageManager2;
import dk.itu.pervasive.mobile.utils.Constants;

/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public class SocketSendingTask extends AsyncTask<String, Void, Void>
{
	private RequestDelegate _delegate;
	private Socket _socket;
	
	public SocketSendingTask(RequestDelegate delegate, Socket socket)
	{
		_delegate = delegate;
		_socket = socket;
	}
	
	@Override
	protected Void doInBackground(String... arg0)
	{
		_send(0);
		
		return null;
	}
	
	private void _send(int index)
	{
		List<String> images = ImageManager2.getInstance().getImagePaths();
		
		if (index == images.size()) return;
			
		_sendMessage(images.get(index));
		_sendImage(index);
	}
	
	private void _sendImage(int index)
	{
		try
		{
			File imageFile = new File(ImageManager2.getInstance().getImagePaths().get(index));
			
			long fileSize = imageFile.length();
			if (fileSize > Integer.MAX_VALUE)
			{
				Log.wtf("_sendImage", "File is too large.");
			}
			
			Log.wtf("_sendImage", "Sending image size : "+fileSize);
			
			byte[] bytes = new byte[(int) fileSize];
			
			FileInputStream fis = new FileInputStream(imageFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			BufferedOutputStream out = new BufferedOutputStream(_socket.getOutputStream());
			
			out.write(String.valueOf(fileSize).getBytes());
			
			int count;
			while ((count = bis.read(bytes)) > 0)
			{
				out.write(bytes, 0, count);
				Log.wtf("_sendImage", "Sending data");
			}
			
			out.flush();
			//out.close();
			fis.close();
			bis.close();
			
			_send(index + 1);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void _sendMessage(String imagePath)
	{
		JsonObject json = new JsonObject();
		json.add(Constants.Action.ACTION, Constants.Action.SEND);
		
		File imageFile = new File(imagePath);
		
		long fileSize = imageFile.length();
		if (fileSize > Integer.MAX_VALUE)
		{
			Log.wtf("SocketConnection", "File is too large.");
		}
		
		json.add(Constants.NAME, imageFile.getName());
		json.add(Constants.SIZE, fileSize);
		
		String jsonString = "[" + json.toString() + "] \n";
		
		Log.wtf("_sendJSONAction", jsonString);
	}
	
	@Override
	protected void onPostExecute(Void result)
	{
		_delegate.onRequestSendSuccess();
		
		super.onPostExecute(result);
	}
}
