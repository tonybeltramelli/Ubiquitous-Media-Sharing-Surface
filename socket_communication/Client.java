import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class Client
{
    public static void main(String[] args) throws IOException {
        Socket socket = null;
        String host = "127.0.0.1";

        socket = new Socket(host, 4444);

        File file = new File("input.jpg");
        
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("File is too large.");
        }
        
        byte[] bytes = new byte[(int) fileSize];
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

        int count;
        while ((count = bis.read(bytes)) > 0)
        {
            out.write(bytes, 0, count);
        }

        out.flush();
        out.close();
        fis.close();
        bis.close();
        socket.close();
    }
}