package dk.itu.pervasive.mobile.signalR;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import dk.itu.pervasive.mobile.R;
import dk.itu.pervasive.mobile.activity.MainActivity;
import dk.itu.pervasive.mobile.data.DataManager;
import dk.itu.pervasive.mobile.gallery2.ImageManager2;
import dk.itu.pervasive.mobile.signalR.tasks.CreateConnectionTask;
import dk.itu.pervasive.mobile.signalR.tasks.ReceiveImageTask;
import dk.itu.pervasive.mobile.signalR.tasks.SendImageTask;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

import java.io.File;
import java.util.List;

/**
 * Created by centos on 4/23/14.
 */
public class SignalRService extends Service implements SignalRCallbacks {

    public static final String SIGNAL_R_SERVER_HUB = "Listener";
    public static final String SIGNAL_R_INIT_COM_METHOD = "Register";
    public static final String SIGNAL_R_SEND_IMAGE_METADATA_METHOD = "SendImageMetadata";

    private final int NOTIFICATION = R.string.tcp_service_started;
    private final IBinder _binder = new SignalRServiceBinder(this);

    private NotificationManager _notificationManager;
    private int _imageIndex = 0;
    private List<String> _imagePaths;

    //signalR objects
    HubConnection _hubConnection;
    HubProxy _hubProxy;

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
        new CreateConnectionTask(this).execute();
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
    public void onConnectionFailure() {
        Log.i("SIGNAL", "Connection failed");

        try {
            if (_hubConnection != null){
                _hubConnection.disconnect();
                _hubConnection.stop();
            }
        } catch (Exception e) {

        }
        _hubConnection = null;
        _hubProxy = null;
        _imageIndex = 0;

        MainActivity mainActivity = (MainActivity) DataManager.getInstance().getContext();

        if (mainActivity != null) {
            if (mainActivity.isActivityRunning())
                createConnection();
        }
    }

    @Override
    public void onConnectionSucceded(HubConnection connection, HubProxy hubProxy) {
        Log.i("SIGNAL", "Connection succeeded");
        _hubConnection = connection;
        _hubProxy = hubProxy;
        setUpConnectionListeners();
    }

    @Override
    public void onReceiveImage(String imagePath) {
        Log.i("SIGNAL", "Image received with success : " + imagePath);
        ImageManager2.getInstance().insertImageToGallery(imagePath);
        _imagePaths = ImageManager2.getInstance().getImagePaths();
    }

    private void setUpConnectionListeners(){
        _hubConnection.closed(new Runnable() {
            @Override
            public void run() {
                onConnectionFailure();
            }
        });

        _hubConnection.error(new ErrorCallback() {
            @Override
            public void onError(Throwable throwable) {
                onConnectionFailure();
            }
        });

        _hubProxy.subscribe(this);
    }

    //called from the server SignalR
    public void RequestGallery(){
        Log.i("SIGNAL" , "Request galley received");
        _imageIndex = 0;
        sendImageData();
    }

    //called from the server SignalR
    public void SendSuccess(){
        Log.i("SIGNAL" , "Success message received");
        sendImageData();
    }

    //called from the server SignalR
    public void SendImageMetadata(String fileName , int imageSize ){
        Log.i("SIGNAL" , "Server is about to send an image : " + fileName + " size : " + imageSize);
        new ReceiveImageTask(this , fileName).execute();
    }

    //called from the server SignalR
    public void Disconnect(){
        Log.i("SIGNAL", "Server send disconnect request");
        _hubConnection.disconnect();
    }

    private void sendImageData(){

        if( _imageIndex >= _imagePaths.size() ) {
            Log.i("SIGNAL" , "All images sent. Returning");
            return;
        }

        sendImageMetadataToServer(_imagePaths.get(_imageIndex));

        Log.i("SIGNAL" , "Trying to sent image : " + _imagePaths.get(_imageIndex));
        new SendImageTask(_imagePaths.get(_imageIndex)).execute();
        _imageIndex++;
    }

    private void sendImageMetadataToServer(String filePath){
        File imageFile = new File(filePath);

        _hubProxy.invoke(SIGNAL_R_SEND_IMAGE_METADATA_METHOD ,
                imageFile.getName() ,
                imageFile.length());
    }
}
