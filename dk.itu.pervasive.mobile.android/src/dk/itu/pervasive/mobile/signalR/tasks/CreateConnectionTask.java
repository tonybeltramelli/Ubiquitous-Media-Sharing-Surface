package dk.itu.pervasive.mobile.signalR.tasks;

import android.os.AsyncTask;
import dk.itu.pervasive.mobile.data.DataManager;
import dk.itu.pervasive.mobile.signalR.SignalRCallbacks;
import dk.itu.pervasive.mobile.signalR.SignalRService;
import dk.itu.pervasive.mobile.utils.UString;
import dk.itu.pervasive.mobile.utils.dataStructure.URLInformation;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

import java.util.concurrent.ExecutionException;

/**
 * Created by centos on 4/23/14.
 */
public class CreateConnectionTask extends AsyncTask<Void, Void, Boolean> {

    SignalRCallbacks _callbacks;
    HubConnection _hubConnection;
    HubProxy _hubProxy;


    public CreateConnectionTask(SignalRCallbacks callbacks) {
        _callbacks = callbacks;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        URLInformation urlInformation = UString.getUrlInformation(DataManager.getInstance().getSurfaceAddress());
        String host = urlInformation.getIp() + ":" + urlInformation.getPort();

        host = "http://" + host + "/signalr";
        _hubConnection = new HubConnection(host);
        _hubProxy = _hubConnection.createHubProxy(SignalRService.SIGNAL_R_SERVER_HUB);

        SignalRFuture signalRFuture = _hubConnection.start();
        try {
            signalRFuture.get();
            initCommunication();
        } catch (Exception e) {
            try {
                if (_hubConnection != null) {
                    _hubConnection.stop();
                }
                Thread.sleep(1000);
            } catch (Exception ee) {

            }
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result.booleanValue()) {
            _callbacks.onConnectionSucceded(_hubConnection, _hubProxy);
        } else {
            _callbacks.onConnectionFailure();
        }
    }

    private void initCommunication() throws ExecutionException, InterruptedException {
        //send the tag id as integer
        int tagId = Integer.parseInt(DataManager.getInstance().getStickerID());
        _hubProxy.invoke(SignalRService.SIGNAL_R_INIT_COM_METHOD, tagId).get();
    }
}
