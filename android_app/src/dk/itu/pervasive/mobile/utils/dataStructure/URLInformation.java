package dk.itu.pervasive.mobile.utils.dataStructure;
/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public class URLInformation
{
	private String _ip;
	private int _port;
	
	public URLInformation(String ip, int port)
	{
		_ip = ip;
		_port = port;
	}
	
	public String getIp()
	{
		return _ip;
	}
	
	public int getPort()
	{
		return _port;
	}
}