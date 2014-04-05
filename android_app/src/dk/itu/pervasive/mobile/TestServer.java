package dk.itu.pervasive.mobile;

import com.eclipsesource.json.JsonObject;
import dk.itu.pervasive.mobile.utils.Constants;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by centos on 4/4/14.
 */
public class TestServer {


    public static void main(String[] args) {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(55555);
        } catch (IOException e) {
            System.out.println("Port taken");
            System.exit(1);

        }

        try {
            Socket socket = serverSocket.accept();

            BufferedReader stream = new BufferedReader(new InputStreamReader(socket.getInputStream() , "UTF-16"));

            String line = stream.readLine();

            System.out.println(line);


            JsonObject json = new JsonObject(); // send request object to get all images
            json.add(Constants.Action.ACTION , Constants.Action.REQUEST);
            String msg = "[" + json.toString() + "]\n";

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
               socket.getOutputStream() , "UTF-16"
            ));

            writer.write(msg);
            writer.flush();

            File imageFile = new File("blue.jpeg");

            json = new JsonObject();
            json.add(Constants.Action.ACTION , Constants.Action.SEND);
            json.add(Constants.NAME , imageFile.getName());
            json.add(Constants.SIZE , String.valueOf(imageFile.length()));

            String sendAction = "[" + json.toString() + "]\n";


            writer.write(sendAction);
            writer.flush();




            FileInputStream fis = new FileInputStream(imageFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

            byte[] bytes = new byte[1024];

//            int count;
//            int counter = 0;
//            while ((count = bis.read(bytes)) > 0)
//            {
//                counter += count;
//                out.write(bytes, 0, count);
//            }




            byte[] buffer = new byte[1024];

            int counter = 0;

            long chunks = imageFile.length() / 1024;

            int lastChunk = (int)(imageFile.length() - (chunks * 1024));

            for( long i = 0 ; i < chunks ; i++ ){
                bis.read(buffer);
                out.write(buffer);
                out.flush();
                counter += 1024;
            }

            bis.read(buffer, 0, lastChunk);
            out.write(buffer, 0, lastChunk);
            counter += lastChunk;

            System.out.println("Sent " + counter);

            out.flush();


            bis.close();

            bis.close();
            writer.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        if( !serverSocket.isClosed() )
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


    }
}
