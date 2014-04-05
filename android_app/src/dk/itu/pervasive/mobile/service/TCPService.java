package dk.itu.pervasive.mobile.service;

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
import dk.itu.pervasive.mobile.socket.SocketCreatingTask;
import dk.itu.pervasive.mobile.socket.SocketReceivingTask;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public class TCPService extends Service implements RequestDelegate
{
	private final int NOTIFICATION = R.string.tcp_service_started;
	private final IBinder _binder = new TCPServiceBinder(this);

	private NotificationManager _notificationManager;
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
		
		_createSocket();
	}
	
	private void _createSocket()
	{
		//will execute onRequestCreateSuccess
		SocketCreatingTask socketTask = new SocketCreatingTask(this);
		socketTask.execute();

//        SocketReceivingTask task = new SocketReceivingTask(null , this);
//        new Thread(task).start();
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
	public void onRequestSendSuccess()
	{
		//TODO
		//_socket.close();
	}
	
	@Override
	public void onRequestCreateSuccess(Socket socket)
	{
		_socket = socket;
        //if initialization worked start the receiver to wait for the request to send images
        SocketReceivingTask task = new SocketReceivingTask(_socket , this);
        new Thread(task).start();

		//will execute onRequestSendSuccess
//		SocketSendingTask socketTask = new SocketSendingTask(this, _socket);
//		socketTask.execute();
	}
	
	@Override
	public void onRequestReceiveSuccess()
	{
        //TODO
        //start here the sending process when notified by the receiver
		//TODO
		//_socket.close();
	}

    @Override
    public void onRequestFailure() {

        if( _socket != null )
            if( !_socket.isClosed()){
                Log.i("NET" , "TCP service on failure. Closing socket");
                try {
                    _socket.close();
                } catch (IOException e) { }
            }
    }

    @Override
    public void onReceivedImageSuccess(String path) {
        ImageManager2.getInstance().insertImageToGallery(path);
    }
}
