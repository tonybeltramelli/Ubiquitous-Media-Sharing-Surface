package dk.itu.pervasive.mobile;

import java.io.*;
import java.net.Socket;

/**
 * Created by centos on 4/5/14.
 */
public class TestReceiver {



    public static void main( String [] args){

        try {
            Socket socket = new Socket("localhost" , 55556);


            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream() , "UTF-16"));

            input.readLine();
            input.readLine();

            FileOutputStream out = new FileOutputStream(
                    new File("skata.jpeg")
            );

            InputStream in = socket.getInputStream();

            byte[] buffer = new byte[1024];

            for (int read = in.read(buffer); read >= 0; read = in.read(buffer))
                out.write(buffer, 0, read);

            out.close();

            socket.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
