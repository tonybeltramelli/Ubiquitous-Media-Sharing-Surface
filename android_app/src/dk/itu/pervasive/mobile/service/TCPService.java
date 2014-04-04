package dk.itu.pervasive.mobile.service;

import java.io.IOException;
import java.net.Socket;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import dk.itu.pervasive.mobile.R;
import dk.itu.pervasive.mobile.activity.MainActivity;
import dk.itu.pervasive.mobile.gallery2.ImageManager2;
import dk.itu.pervasive.mobile.socket.RequestDelegate;
import dk.itu.pervasive.mobile.socket.SocketConnection;

/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public class TCPService extends Service implements RequestDelegate
{
	private final int NOTIFICATION = R.string.tcp_service_started;
	private final IBinder _binder = new TCPServiceBinder(this);
	
	private NotificationManager _notificationManager;
	private SocketConnection _socketConnection;
	private Socket _socket;
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return _binder;
	}
	
	@Override
	public void onCreate()
	{
		_notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		_showNotification();
		
		_socketConnection = new SocketConnection(this);
		_socketConnection.execute(SocketConnection.CREATE);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return START_STICKY;
	}
	
	@Override
	public void onDestroy()
	{
		Toast.makeText(this, R.string.tcp_service_stopped, Toast.LENGTH_SHORT).show();
	}
	
	private void _showNotification()
	{
		Notification notification = new Notification();
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
		
		notification = new Notification.Builder(this).setTicker(getText(R.string.tcp_service_started))
				.setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.itu_black_white_logo)
				.setContentTitle(getText(R.string.tcp_service_started))
				.setContentText(getText(R.string.tcp_service_started)).setContentIntent(contentIntent)
				.getNotification();
		
		_notificationManager.notify(NOTIFICATION, notification);
	}
	
	@Override
	public void onRequestSendSuccess(int index)
	{
		if (index == ImageManager2.getInstance().getImagePaths().size())
		{
			try
			{
				Log.i("TAG", "closing socket");
				_socket.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			return;
		}
		
		_socketConnection = null;
		_socketConnection = new SocketConnection(this, _socket, ImageManager2.getInstance().getImagePaths().get(index), index);
		_socketConnection.execute(SocketConnection.SEND);
	}
	
	@Override
	public void onRequestCreateSuccess(Socket socket)
	{
		_socket = socket;
		
		onRequestSendSuccess(0);
	}
	
	@Override
	public void onRequestReceiveSuccess()
	{	
	}
}
