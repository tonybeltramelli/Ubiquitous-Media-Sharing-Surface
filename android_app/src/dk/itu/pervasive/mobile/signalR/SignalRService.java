package dk.itu.pervasive.mobile.signalR;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import dk.itu.pervasive.mobile.R;
import dk.itu.pervasive.mobile.activity.MainActivity;
import dk.itu.pervasive.mobile.data.DataManager;
import dk.itu.pervasive.mobile.gallery2.ImageManager2;
import dk.itu.pervasive.mobile.utils.UString;
import dk.itu.pervasive.mobile.utils.dataStructure.URLInformation;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by centos on 4/23/14.
 */
public class SignalRService extends Service implements SignalRCallbacks {

    public static final String SIGNAL_R_SERVER_HUB = "MessageHub";
    public static final String SIGNAL_R_INIT_COM_METHOD = "InitCommunication";
    public static final String SIGNAL_R_MESSAGE_RECEIVED_HANDLER = "MessageReceived";


    private final int NOTIFICATION = R.string.tcp_service_started;
    private final IBinder _binder = new SignalRServiceBinder(this);

    private NotificationManager _notificationManager;
    private int _imageIndex = 0;
    private List<String> _imagePaths;

    //signalR objects
    HubConnection _hubConnection;
    HubProxy _hubProxy;
    SignalRFuture<Void> _signalRFuture;

    @Override
    public IBinder onBind(Intent intent) {
        return _binder;
    }

    @Override
    public void onCreate() {
        _notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        _showNotification();

        _imagePaths = ImageManager2.getInstance().getImagePaths();

        createConnection();
    }

    private void createConnection() {
        //TODO
        URLInformation urlInformation = UString.getUrlInformation(DataManager.getInstance().getSurfaceAddress());
        String host = urlInformation.getIp() + ":" + urlInformation.getPort();
        _hubConnection = new HubConnection(host);
        _hubProxy = _hubConnection.createHubProxy(SIGNAL_R_SERVER_HUB);

        _signalRFuture = _hubConnection.start();
        try {
            _signalRFuture.get();
            initCommunication();
            setUpConnectionListeners();
        } catch (InterruptedException e) {
            onRequestFailure();
        } catch (ExecutionException e) {
            onRequestFailure();
        }

    }

    private void setUpConnectionListeners() {
        _hubProxy.on(SIGNAL_R_MESSAGE_RECEIVED_HANDLER , new SubscriptionHandler() {
            @Override
            public void run() {

            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        _notificationManager.cancel(NOTIFICATION);
        //TODO
        Toast.makeText(this, R.string.tcp_service_stopped, Toast.LENGTH_SHORT).show();
    }

    private void _showNotification() {
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
    public void onRequestFailure() {
        _hubConnection.stop();

        _imageIndex = 0;

        MainActivity mainActivity = (MainActivity) DataManager.getInstance().getContext();

        if (mainActivity != null) {
            if (mainActivity.isActivityRunning())
                createConnection();
        }

    }

    public void initCommunication() throws ExecutionException, InterruptedException {
        _hubProxy.invoke(SIGNAL_R_INIT_COM_METHOD, DataManager.getInstance().getStickerID()).get();
    }
}
