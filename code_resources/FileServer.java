import java.io.*;
import java.net.*;

public class FileServer {

    public static void main(String[] args) throws IOException {
        int filesize = 6022386; // filesize temporary hardcoded

        long start = System.currentTimeMillis();
        int bytesRead;
        int current = 0;

        // create socket
        ServerSocket servsock = new ServerSocket(8888);
        while (true) {
            System.out.println("Waiting...");

            Socket sock = servsock.accept();
            System.out.println("Accepted connection : " + sock);

            // receive file
            byte[] mybytearray = new byte[filesize];
            InputStream is = sock.getInputStream();
            FileOutputStream fos = new FileOutputStream(
                    "C:\\Users\\mi\\Desktop\\Test.jpg"); // destination
                                                                    // path and
                                                                    // name of
                                                                    // file
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bytesRead = is.read(mybytearray, 0, mybytearray.length);
            current = bytesRead;


            do {
                bytesRead = is.read(mybytearray, current,
                        (mybytearray.length - current));
                if (bytesRead >= 0)
                    current += bytesRead;
            } while (bytesRead > -1);

            bos.write(mybytearray, 0, current);
            bos.flush();
            long end = System.currentTimeMillis();
            System.out.println(end - start);
            bos.close();

            sock.close();
        }
    }

}