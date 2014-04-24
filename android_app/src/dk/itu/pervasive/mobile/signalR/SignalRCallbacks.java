package dk.itu.pervasive.mobile.signalR;

import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

/**
 * Created by centos on 4/23/14.
 */
public interface SignalRCallbacks {
    public void onConnectionFailure();
    public void onConnectionSucceded(HubConnection connection , HubProxy hubProxy);
    public void onReceiveImage(String imagePath);
}
