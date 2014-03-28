package dk.itu.pervasive.mobile.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import dk.itu.pervasive.mobile.data.DataManager;

/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public class SocketThread extends Thread implements Runnable
{
	private Socket _socket;
	private SocketAddress _socketAddress;
	
	public SocketThread()
	{
		_socket = new Socket();
		
		String rawServerAddress = DataManager.getInstance().getSurfaceAddress();
		
		String ip = rawServerAddress.substring(0, rawServerAddress.indexOf(":"));
		int port = Integer.valueOf(rawServerAddress.substring(rawServerAddress.indexOf(":") + 1,
				rawServerAddress.indexOf("/") != -1 ? rawServerAddress.indexOf("/") : rawServerAddress.length()));
		
		_socketAddress = new InetSocketAddress(ip, port);
	}
	
	@Override
	public synchronized void start()
	{
		super.start();
	}
	
	@Override
	public void run()
	{
		try
		{
			System.out.println("here");
			_socket.connect(_socketAddress);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void interrupt()
	{
		try {
			_socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		_socket = null;
        
		super.interrupt();
	}
	
}
