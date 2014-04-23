package dk.itu.pervasive.mobile.signalR;

import android.os.Binder;

/**
 * Created by centos on 4/23/14.
 */
public class SignalRServiceBinder extends Binder {


    private SignalRService _service;

    public SignalRServiceBinder(SignalRService service)
    {
        _service = service;
    }

    public SignalRService getService()
    {
        return _service;
    }
}
