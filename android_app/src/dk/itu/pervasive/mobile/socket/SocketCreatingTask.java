package dk.itu.pervasive.mobile.socket;

import android.os.AsyncTask;
import android.util.Log;
import com.eclipsesource.json.JsonObject;
import dk.itu.pervasive.mobile.data.DataManager;
import dk.itu.pervasive.mobile.utils.Constants;
import dk.itu.pervasive.mobile.utils.UString;
import dk.itu.pervasive.mobile.utils.dataStructure.URLInformation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Tony Beltramelli www.tonybeltramelli.com
 */
public class SocketCreatingTask extends AsyncTask<String, Void, Socket> {
    private RequestDelegate _delegate;
    private Socket _socket;
    private boolean _statusFlag;

    public SocketCreatingTask(RequestDelegate delegate) {
        _delegate = delegate;
    }

    @Override
    protected Socket doInBackground(String... params) {
       return _createSocket();
    }

    private Socket _createSocket() {
        try {
            URLInformation urlInformation = UString.getUrlInformation(DataManager.getInstance().getSurfaceAddress());
            if(urlInformation == null) return null;
            
            _socket = new Socket() ;
            //try to connect with timeout
            _socket.connect(new InetSocketAddress(urlInformation.getIp() , urlInformation.getPort()) , 2000);
            _socket.setKeepAlive(false);
            
            //write json in unicode format
            OutputStreamWriter os = new OutputStreamWriter(_socket.getOutputStream(), "UTF-16");

            os.write(getInitMessage());
            os.flush();
            
            _statusFlag = true;

            Log.i("NET", "Create task succeded to create connection");
            Log.i("NET" , "Create task sent initialization message : " + getInitMessage());
        } catch (IOException e) {
            Log.i("NET", "Create task failed to create connection . Socket is closing");
            try {
                if (!_socket.isClosed())
                    _socket.close();
            } catch (IOException e1) {
            }
            _statusFlag = false;
        }

        return _socket;
    }

    @Override
    protected void onPostExecute(Socket socket) {
        _socket = socket;
        if (_statusFlag)
            _delegate.onRequestCreateSuccess(_socket);
        else
            _delegate.onRequestFailure();
    }


    private String getInitMessage() {

        JsonObject json = new JsonObject();
        json.add(Constants.Action.ACTION, Constants.Action.START);
        json.add(Constants.TAG_ID, DataManager.getInstance().getStickerID());

        return "[" + json.toString() + "]\n";
    }
}
