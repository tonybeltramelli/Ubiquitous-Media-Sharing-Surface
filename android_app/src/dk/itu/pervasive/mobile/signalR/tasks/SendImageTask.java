package dk.itu.pervasive.mobile.signalR.tasks;

import android.os.AsyncTask;
import android.util.Log;
import dk.itu.pervasive.mobile.data.DataManager;
import dk.itu.pervasive.mobile.utils.UString;
import dk.itu.pervasive.mobile.utils.dataStructure.URLInformation;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by centos on 4/23/14.
 */
public class SendImageTask extends AsyncTask<Void, Void, Void> {

    String _imagePath;
    private static final int BUFFER_SIZE = 8192;

    public SendImageTask(String imagePath) {
        _imagePath = imagePath;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Socket socket = createConnection();
        if (socket != null)
            sendImage(socket, _imagePath);

        return null;
    }

    private void sendImage(Socket socket, String imagePath) {

        BufferedOutputStream out = null;
        BufferedInputStream bis = null;
        try {
            File file = new File(imagePath);

            long fileSize = file.length();

            byte[] bytes = new byte[BUFFER_SIZE];
            bis = new BufferedInputStream(new FileInputStream(file));
            out = new BufferedOutputStream(socket.getOutputStream());

            int count;
            while ((count = bis.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }

            out.flush();
            bis.close();
            socket.close();

            Log.i("SIGNAL" , "Image sent with success");
        } catch (Exception e) {
            Log.i("SIGNAL" , "Failed to send image");
            try{
                bis.close();
                socket.close();
            }
            catch (Exception ee){

            }
        }
    }

    private Socket createConnection() {
        URLInformation urlInformation = UString.getUrlInformation(DataManager.getInstance().getSurfaceAddress());
        Socket socket = new Socket();

        try {
            socket.connect(new InetSocketAddress(urlInformation.getIp()
                    , urlInformation.getPort() + 1) , 1000);
            Log.i("SIGNAL", "Socket opened for sending image");
            return socket;
        } catch (IOException e) {
            Log.i("SIGNAL", "Could not open socket for sending image");
            return null;
        }

    }
}
