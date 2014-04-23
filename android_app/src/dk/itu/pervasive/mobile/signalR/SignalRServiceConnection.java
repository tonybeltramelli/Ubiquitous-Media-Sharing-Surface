package dk.itu.pervasive.mobile.signalR;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;
import dk.itu.pervasive.mobile.R;

/**
 * Created by centos on 4/23/14.
 */
public class SignalRServiceConnection implements ServiceConnection {

    private SignalRService _boundService;
    private Activity _delegate;

    public SignalRServiceConnection(Activity a){
        _delegate = a;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        _boundService = ((SignalRServiceBinder) service).getService();

        Toast.makeText(_delegate, R.string.tcp_service_connected, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        _boundService = null;

        Toast.makeText(_delegate, R.string.tcp_service_disconnected, Toast.LENGTH_SHORT).show();

    }
}
