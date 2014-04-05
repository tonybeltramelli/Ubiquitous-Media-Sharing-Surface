package dk.itu.pervasive.mobile.httprequest;

import com.eclipsesource.json.JsonObject;

/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public interface RequestDelegate
{
	public void onRequestSuccess(JsonObject[] result);
}
