package dk.itu.pervasive.mobile.socket;

import java.net.Socket;


/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public interface RequestDelegate
{
	public void onRequestSendSuccess();
	public void onRequestCreateSuccess(Socket socket);
	public void onRequestReceiveSuccess();
    public void onRequestFailure();
    public void onReceivedImageSuccess(String path);
}
