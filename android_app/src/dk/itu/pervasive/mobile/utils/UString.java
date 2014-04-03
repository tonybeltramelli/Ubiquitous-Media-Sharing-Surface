package dk.itu.pervasive.mobile.utils;

import dk.itu.pervasive.mobile.data.DataManager;
import dk.itu.pervasive.mobile.utils.dataStructure.URLInformation;

/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public class UString
{
	public static URLInformation getUrlInformation(String rawUrl)
	{
		String ip = rawUrl.substring(0, rawUrl.indexOf(":"));
		int port = Integer.valueOf(rawUrl.substring(rawUrl.indexOf(":") + 1, rawUrl.indexOf("/") != -1 ? rawUrl.indexOf("/") : rawUrl.length()));
		
		return new URLInformation(ip, port);
	}
}
