package dk.itu.pervasive.mobile.service;

import android.os.Binder;

/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public class TCPServiceBinder extends Binder
{
	private TCPService _service;

	public TCPServiceBinder(TCPService service)
	{
		_service = service;
	}
	
	public TCPService getService()
	{
		return _service;
	}
}
