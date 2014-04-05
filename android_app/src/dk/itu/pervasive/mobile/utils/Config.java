package dk.itu.pervasive.mobile.utils;


/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public class Config
{
	public static final String BLIP_GET_ALL_LOCATIONS_URL = "http://pit.itu.dk:7331/locations/";
	public static final String BLIP_GET_LOCATION_OF_TERMINAL_URL = "http://pit.itu.dk:7331/location-of/";
	public static final String BLIP_GET_TERMINALS_IN_LOCATION_URL = "http://pit.itu.dk:7331/terminals-in/";
	//
	public static final String START_LATITUDE = "{START_LATITUDE}";
	public static final String START_LONGITUDE = "{START_LONGITUDE}";
	public static final String END_LATITUDE = "{END_LATITUDE}";
	public static final String END_LONGITUDE = "{END_LONGITUDE}";
	public static final String GOOGLE_MAP_ROUTE_URL = "http://maps.googleapis.com/maps/api/directions/json?origin="+START_LATITUDE+","+START_LONGITUDE+"&destination="+END_LATITUDE+","+END_LONGITUDE+"&sensor=false&units=metric&mode=walking";
	//
	public static final int LOCATION_UPDATE_MIN_TIME = 0;
	public static final int LOCATION_UPDATE_MIN_DISTANCE = 0;
	//
	public static final double ITU_LATITUDE = 55.659733;
	public static final double ITU_LONGITUDE = 12.590994;
	//
	public static final int MINIMUM_DISTANCE = 100;
	//
	public static final long UPDATE_INTERVAL = 5000;
	public static final long FASTEST_INTERVAL = 1000;
	//
	public static final String DATA_KEY_NAME = "name";
	public static final String DATA_KEY_USER_ID = "user_id";
	public static final String DATA_KEY_BLUETOOTH_ID = "bluetooth_id";
	public static final String DATA_KEY_ACTION = "action";
	public static final String DATA_VALUE_ENTERING = "enter";
	public static final String DATA_VALUE_LEAVING = "leave";
}
