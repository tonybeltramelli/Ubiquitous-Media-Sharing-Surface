package dk.itu.pervasive.mobile;

import com.eclipsesource.json.JsonObject;
import dk.itu.pervasive.mobile.utils.Constants;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by centos on 4/4/14.
 */
public class TestServer {


    public static void main(String[] args) {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(55556);
        } catch (IOException e) {
            System.out.println("Port taken");
            System.exit(1);

        }

        try {
            Socket socket = serverSocket.accept();

            BufferedReader stream = new BufferedReader(new InputStreamReader(socket.getInputStream() , "UTF-16"));

//            //receive start message
//            String line = stream.readLine();
//
//            System.out.println(line);



//            //send request for images
            JsonObject json = new JsonObject(); // send request object to get all images
            json.add(Constants.Action.ACTION , Constants.Action.REQUEST);
            String msg = "[" + json.toString() + "]\n";

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
               socket.getOutputStream() , "UTF-16"
            ));

            writer.write(msg);
            writer.flush();

            //send image
            File imageFile = new File("blue.jpeg");

////
            json = new JsonObject();
            json.add(Constants.Action.ACTION , Constants.Action.SEND);
            json.add(Constants.NAME , imageFile.getName());
            json.add(Constants.SIZE , String.valueOf(imageFile.length()));

            String sendAction = "[" + json.toString() + "]\n";


            writer.write(sendAction);
            writer.flush();
//
            transfer(imageFile , socket);

            Scanner sc = new Scanner(System.in);

            sc.nextLine();

            socket.getOutputStream().close();
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



    public static void transfer(final File f, Socket socket) throws IOException {
        final BufferedOutputStream outStream = new BufferedOutputStream(socket.getOutputStream());
        final BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(f));
        final byte[] buffer = new byte[1024];
        for (int read = inStream.read(buffer); read >= 0; read = inStream.read(buffer)){
            outStream.write(buffer, 0, read);
        }

        outStream.flush();
    }
}
