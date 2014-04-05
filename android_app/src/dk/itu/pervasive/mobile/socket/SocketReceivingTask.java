package dk.itu.pervasive.mobile.socket;

import android.os.Environment;
import android.util.Log;
import com.eclipsesource.json.JsonArray;
import dk.itu.pervasive.mobile.utils.Constants;

import java.io.*;
import java.net.Socket;

/**
 * Created by centos on 4/4/14.
 */
public class SocketReceivingTask implements Runnable {

    Socket _socket;

    RequestDelegate _delegate;

    public static final String SD_PATH = Environment.getExternalStoragePublicDirectory("Download").toString() + "/";

    public SocketReceivingTask(Socket socket , RequestDelegate delegate ){
        _delegate = delegate;
        _socket = socket;
    }

    @Override
    public void run() {

        BufferedReader reader = tryToGetInputStream();
        if( reader == null ){
            //if there was an error report it and stop the thread
            _delegate.onRequestFailure();
            return;
        }

        //run forever and stop only when failure
        while( true ){
            try {
                String message = reader.readLine();
                if( message != null )
                    dispatchMessage(message);
                else
                    //if message is null the server closed the coonection
                    //throw exception to avoid duplication
                    throw new IOException();
            } catch (IOException e) {
                //if anything wrong report error and return
                Log.i("NET" , "Reader task failed to read server message. Reporting failure");
                _delegate.onRequestFailure();
                return;
            }
        }
    }


    private BufferedReader tryToGetInputStream(){
        BufferedReader reader = null;
        try {
            //open stream for unicode
            reader = new BufferedReader( new InputStreamReader(
                    _socket.getInputStream() , "UTF-16"
            ));
        } catch (IOException e) {
            Log.i("NET" , "Reader task failed to open input stream. Reporting failure");
            return null;
        }

        return reader;
    }

    private void dispatchMessage( String message ) throws IOException{ //if anything goes wrong throw exception so that it will be handled in a single point in run method
        String action = getJsonAttribute(message, Constants.Action.ACTION);
        Log.i("NET" , "Receiver received action from server : " + message );

        if( action.equals(Constants.Action.REQUEST) || action.equals(Constants.Action.SUCCESS)){//asking for images or server received image.send next
            Log.i("NET" , "Handling action \"request and success\"");
            _delegate.onRequestReceiveSuccess();
        }
        else if( action.equals(Constants.Action.SEND)){//server is sending an image.prepare to receive
            Log.i("NET" , "Handling action \"send\"");
            handleImageReceiving(message);
        }
    }

    private String getJsonAttribute(String message, String attribute){
        JsonArray jsonArray = JsonArray.readFrom(message);

        return jsonArray.get(0).asObject()
                .get(attribute).asString();
    }


    private void handleImageReceiving(String message)throws IOException{ //if anything goes wrong throw exception so that it will be handled in a single point in run method


        String fileName = getJsonAttribute(message , Constants.NAME);
        long fileSize = Long.parseLong( getJsonAttribute(message , Constants.SIZE));

        Log.i("NET" , "Receiving image from server . Name : " + fileName + " size : " + fileSize);

        String fullImagePath = SD_PATH + "surface_" + fileName;

        File imageFile = new File(fullImagePath);
        BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream(imageFile));

        InputStream is = _socket.getInputStream();

        BufferedInputStream stream = new BufferedInputStream( _socket.getInputStream());

        try{

            int count = 0;
            byte[] buffer = new byte[1024];

            int counter = 0;

            long chunks = fileSize / 1024;

            Log.i("NET" , "Chunks " + chunks);

            int lastChunk = (int)(fileSize - (chunks * 1024));

            Log.i("NET" , "last " + lastChunk);

            for( long i = 0 ; i < chunks ; i++ ){

                is.read(buffer);
                bos.write(buffer);
                bos.flush();
                counter += 1024;
            }



            is.read(buffer , 0 , lastChunk);
            bos.write(buffer,0,lastChunk);
            counter += lastChunk;
//
//            while((fileSize > 0) && (count = stream.read(buffer , 0 , (int)Math.min(buffer.length , fileSize))) != -1 ){
//
//                bos.write(buffer, 0, count);
//                counter += count;
//
//            }

            bos.flush();

            Log.i("NET" , "received size " + counter);
            bos.close();
            _delegate.onReceivedImageSuccess(fullImagePath);
        }
        catch (Exception e){
            bos.close();
            imageFile.delete();
            throw new IOException();
        }






    }


}
