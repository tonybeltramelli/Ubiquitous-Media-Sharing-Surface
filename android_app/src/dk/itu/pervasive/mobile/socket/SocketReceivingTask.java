package dk.itu.pervasive.mobile.socket;

import android.os.Environment;
import android.util.Log;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import dk.itu.pervasive.mobile.data.DataManager;
import dk.itu.pervasive.mobile.utils.Constants;
import dk.itu.pervasive.mobile.utils.UString;
import dk.itu.pervasive.mobile.utils.dataStructure.URLInformation;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

/**
 * Created by centos on 4/4/14.
 */
public class SocketReceivingTask implements Runnable {

    Socket _socket;

    RequestDelegate _delegate;

    public static final String SD_PATH = Environment.getExternalStoragePublicDirectory("Download").toString() + "/";

    public SocketReceivingTask(Socket socket, RequestDelegate delegate) {
        _delegate = delegate;
        _socket = socket;
    }

    @Override
    public void run() {

        BufferedReader reader = tryToGetInputReader();
        if (reader == null) {
            //if there was an error report it and stop the thread
            _delegate.onRequestFailure();
            return;
        }
        //run forever and stop only when failure
        while (true) {
            try {
                String message = reader.readLine();
                if (message != null) {
                    Log.i("NET", "received new line");
                    dispatchMessage(message);
                } else
                    //if message is null the server closed the connection
                    //throw exception to avoid duplication
                    throw new IOException();
            } catch (IOException e) {
                //if anything wrong report error and return
                Log.i("NET", "Reader task failed to read server message. Reporting failure");
                _delegate.onRequestFailure();
                return;
            }
        }
    }


    private BufferedReader tryToGetInputReader() {
        BufferedReader reader = null;
        try {
            //open stream for unicode
            reader = new BufferedReader(new InputStreamReader(
                    _socket.getInputStream(), "UTF-16"
            ));
        } catch (IOException e) {
            Log.i("NET", "Reader task failed to open input stream. Reporting failure");
            return null;
        }

        return reader;
    }

    private void dispatchMessage(String message) throws IOException { //if anything goes wrong throw exception so that it will be handled in a single point in run method
        Log.i("NET", "Receiver received action from server : " + message);
        String action = getJsonAttribute(message, Constants.Action.ACTION);
        if (action == null)
            return;

        if (action.equals(Constants.Action.REQUEST) || action.equals(Constants.Action.SUCCESS)) {//asking for images or server received image.send next
            Log.i("NET", "Handling action \"request and success\"");
            _delegate.onRequestReceiveSuccess();
        } else if (action.equals(Constants.Action.SEND)) {//server is sending an image.prepare to receive
            Log.i("NET", "Handling action \"send\"");
            newHandleImageReceiving(message);
        }
    }

    private String getJsonAttribute(String message, String attribute) {
        JsonArray jsonArray = null;

        //do this because the server migth send a random character at the biggining
        if (!message.startsWith("["))
            message = message.substring(message.indexOf("["));

        try {
            jsonArray = JsonArray.readFrom(message.trim());
            return jsonArray.get(0).asObject()
                    .get(attribute).asString();
        } catch (Exception e) {
            Log.i("NET", "received wrong json object");
            return null;
        }

    }


    private void newHandleImageReceiving(String message) {


        File imageFile = null;
        imageFile = createFileInSdCard(message);
        BufferedOutputStream bos = null;
        Socket receiveSocket = null;
        BufferedInputStream stream = null;
        try {
            URLInformation urlInformation = UString.getUrlInformation(DataManager.getInstance().getSurfaceAddress());
            receiveSocket = new Socket();
            //connect to the server port + 1
            try {

                receiveSocket.connect(new InetSocketAddress(urlInformation.getIp(), urlInformation.getPort() + 1), 1000);
            } catch (Exception e) {
                Log.i("NET", "cannot connect");
            }


            stream = new BufferedInputStream(receiveSocket.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(imageFile));

            byte[] buffer = new byte[4096];

            int read = 0;
            try {
                while ((read = stream.read(buffer)) != -1) {
                    bos.write(buffer, 0, read);
                }

            } catch (Exception e) {
                Log.i("NET", "cannot read");
            }

            bos.flush();
            bos.close();

//            if (imageFile.length() > 0)
                _delegate.onReceivedImageSuccess(imageFile.getAbsolutePath());
//            else
//                imageFile.delete();

            stream.close();

            sendSuccessToServer();


            receiveSocket.close();
        } catch (Exception e) {
            Log.i("NET", "skata skata");
            if (!receiveSocket.isClosed())
                try {
                    receiveSocket.close();
                } catch (IOException e1) {
                }

        }

    }


    private void handleImageReceiving(String message) throws IOException { //if anything goes wrong throw exception so that it will be handled in a single point in run method
        File imageFile = null;
        BufferedOutputStream bos = null;
        try {

            imageFile = createFileInSdCard(message);

            bos = new BufferedOutputStream(new FileOutputStream(imageFile));

            BufferedInputStream stream = new BufferedInputStream(_socket.getInputStream());

            long fileSize = Long.parseLong(getJsonAttribute(message, Constants.SIZE));

            byte[] buffer = new byte[4096];
            int counter = 0;
            int read = 0;
//
//            int chunks = (int)(fileSize / 4096);
//            int lastChunk =(int) (fileSize - (chunks * 4096));
//
//            for( int i = 0 ; i < chunks ; i++ ){
//                stream.read(buffer);
//                bos.write(buffer);
//            }
//
//            stream.read(buffer , 0 , lastChunk);
//            bos.write(buffer , 0 , lastChunk);


            while ((counter < fileSize) && (read = stream.read(buffer, 0, (int) Math.min(buffer.length, fileSize - counter))) > 0) {
                bos.write(buffer, 0, read);
                counter += read;
                Log.i("NET", "data count : " + counter);
            }

//            for (int read = stream.read(buffer); read != -1; read = stream.read(buffer)){
//                bos.write(buffer, 0, read);
//                counter += read;
//                Log.i("NET" , "data count : " + counter);
//            }

            Log.i("NET", "Finshed reading image");
            bos.flush();
            bos.close();

            sendSuccessToServer();

            _delegate.onReceivedImageSuccess(imageFile.getAbsolutePath());
        } catch (Exception e) {
            Log.i("NET", "skata");
            bos.close();
            imageFile.delete();
            throw new IOException();
        }


    }

    private void sendSuccessToServer() {

        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    _socket.getOutputStream(), "UTF-16"
            ));

            JsonObject json = new JsonObject();
            json.add(Constants.Action.ACTION, Constants.Action.SUCCESS);
            String msg = "[" + json.toString() + "]\n";

            writer.write(msg);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private File createFileInSdCard(String message) {

        String fileName = getJsonAttribute(message, Constants.NAME);
        long fileSize = Long.parseLong(getJsonAttribute(message, Constants.SIZE));

        Log.i("NET", "Receiving image from server . Name : " + fileName + " size : " + fileSize);

        String fullImagePath = SD_PATH + new Random().nextInt() + fileName;

        return new File(fullImagePath);
    }

}
