package dk.itu.pervasive.mobile.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;
import dk.itu.pervasive.mobile.R;

/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public class TCPServiceConnection implements ServiceConnection
{
	private TCPService _boundService;
	private Activity _delegate;
	
	public TCPServiceConnection(Activity delegate)
	{
		_delegate = delegate;
	}
	
	public void onServiceConnected(ComponentName className, IBinder service)
	{
		_boundService = ((TCPServiceBinder) service).getService();
		
		Toast.makeText(_delegate, R.string.tcp_service_connected, Toast.LENGTH_SHORT).show();
	}
	
	public void onServiceDisconnected(ComponentName className)
	{
		_boundService = null;
		
		Toast.makeText(_delegate, R.string.tcp_service_disconnected, Toast.LENGTH_SHORT).show();
	}
}
