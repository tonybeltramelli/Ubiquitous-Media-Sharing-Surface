package dk.itu.pervasive.mobile.signalR.tasks;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import dk.itu.pervasive.mobile.data.DataManager;
import dk.itu.pervasive.mobile.signalR.SignalRCallbacks;
import dk.itu.pervasive.mobile.utils.UString;
import dk.itu.pervasive.mobile.utils.dataStructure.URLInformation;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

/**
 * Created by centos on 4/24/14.
 */
public class ReceiveImageTask extends AsyncTask<Void , Void , Void> {

    private static final int BUFFER_SIZE = 8192;

    public static final String SD_PATH = Environment.getExternalStoragePublicDirectory("Download").toString() + "/";

    private String _imageName;

    private SignalRCallbacks _callbacks;

    public ReceiveImageTask(SignalRCallbacks callbacks , String imageName ){
        _callbacks = callbacks;
        _imageName = imageName;
    }

    @Override
    protected Void doInBackground(Void... params) {

        File imageFile = new File(SD_PATH + new Random().nextInt() + _imageName);
        //could not create file in the sdcard return
        if (imageFile == null){
            Log.i("SIGNAL" , "Could not create file to save image");
            return null;
        }

        Socket socket = createConnection();
        if (socket == null)
            return null;

        receiveImage(socket , imageFile);

        return null;
    }

    private void receiveImage(Socket socket , File imageFile ){
        BufferedInputStream inputStream;
        BufferedOutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(socket.getInputStream());
            outputStream = new BufferedOutputStream(new FileOutputStream(imageFile));

            byte[] buffer = new byte[BUFFER_SIZE];

            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            socket.close();
            _callbacks.onReceiveImage(imageFile.getAbsolutePath());
        } catch (Exception e) {
            Log.i("SIGNAL", "There was an error while reading the image from the server");
            try {
                outputStream.close();
                if (!socket.isClosed())
                    socket.close();
            } catch (IOException e1) {
            }
            imageFile.delete();
        }
    }

    private Socket createConnection() {
        URLInformation urlInformation = UString.getUrlInformation(DataManager.getInstance().getSurfaceAddress());

        Socket socket = new Socket();
        //connect to the server port + 1
        try {
            socket.connect(new InetSocketAddress(urlInformation.getIp(), urlInformation.getPort() + 1), 1000);
            Log.i("SIGNAL", "Socket opened for image receiving");
            return socket;
        } catch (IOException e) {
            Log.i("SIGNAL", "Could not open socket for image receiving");
            return null;
        }
    }
}
