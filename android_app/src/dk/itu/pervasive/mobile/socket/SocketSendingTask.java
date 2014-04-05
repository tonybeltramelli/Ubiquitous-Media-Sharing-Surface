package dk.itu.pervasive.mobile.socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Random;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.eclipsesource.json.JsonObject;

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
	protected Void doInBackground(String... args)
	{
		//_sendMessage(args[0]);
		//_sendImage(args[0]);
		
		_receive();
		
		return null;
	}
	
	private void _receive()
	{
		File imageFile = null;
        try {

        	Log.wtf("_receive", "start");

            imageFile = createFileInSdCard("output.jpg");
            
            InputStream is = _socket.getInputStream();
			int bufferSize = _socket.getReceiveBufferSize();
			
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			
			try
			{
				fos = new FileOutputStream(imageFile);
				bos = new BufferedOutputStream(fos);
			} catch (FileNotFoundException ex)
			{
				Log.wtf("_receive", "File not found. ");
			}
			
			byte[] bytes = new byte[bufferSize]; //new byte[1024];
			
			int count;
			while ((count = is.read(bytes)) != -1)
			{
				bos.write(bytes, 0, count);
				Log.wtf("_receive", "read " + count);
			}
			
			_delegate.onReceivedImageSuccess(imageFile.getAbsolutePath());

			bos.flush();
			//bos.close();
			is.close();
			//_socket.close();
            
            Log.wtf("handleImageReceiving", "Save image");       
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private File createFileInSdCard( String fileName ){

        String fullImagePath = Environment.getExternalStoragePublicDirectory("Download").toString() + "/" + Math.round(Math.random() * 1000) + fileName;

        Log.wtf("create", fullImagePath);
        
        return new File(fullImagePath);
    }
	
	private void _sendImage(String imagePath)
	{
		Log.wtf("_sendImage", imagePath);
		
		try
		{
			File file = new File(imagePath);
	        
	        long fileSize = file.length();
	        if (fileSize > Integer.MAX_VALUE) {
	        	Log.wtf("_sendImage", "File is too large.");
	        }
	        
	        Log.wtf("_sendImage", "Sending image size : " + fileSize);
	        
	        byte[] bytes = new byte[(int) fileSize];
	        FileInputStream fis = new FileInputStream(file);
	        BufferedInputStream bis = new BufferedInputStream(fis);
	        BufferedOutputStream out = new BufferedOutputStream(_socket.getOutputStream());
	
	        int count;
	        while ((count = bis.read(bytes)) != -1)
	        {
	            out.write(bytes, 0, count);
	        }
	
	        out.flush();
	        //out.close();
	        fis.close();
	        bis.close();
	        
	        Log.wtf("_sendImage", "End sending");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void _sendMessage(String imagePath)
	{
		Log.wtf("_sendMessage", imagePath);
		
		JsonObject json = new JsonObject();
		json.add(Constants.Action.ACTION, Constants.Action.SEND);
		
		File imageFile = new File(imagePath);
		
		long fileSize = imageFile.length();
		if (fileSize > Integer.MAX_VALUE)
		{
			Log.wtf("_sendMessage", "File is too large.");
		}
		
		json.add(Constants.NAME, imageFile.getName());
		json.add(Constants.SIZE, fileSize);
		
		String jsonString = "[" + json.toString() + "] \n";
		
		Log.wtf("_sendMessage json", jsonString);
		
		try
		{
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream(), "UTF-16"));
			writer.write(jsonString);
			writer.flush();	
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onPostExecute(Void result)
	{
		_delegate.onRequestSendSuccess();
		
		super.onPostExecute(result);
	}
}
