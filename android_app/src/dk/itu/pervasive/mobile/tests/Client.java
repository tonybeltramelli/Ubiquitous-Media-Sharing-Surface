import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Client
{
    public static void main(String[] args) throws IOException {
    	ServerSocket serverSocket = null;
    	Socket socket = null;
    	
		try
		{
			serverSocket = new ServerSocket(2500);
		} catch (IOException ex)
		{
			System.out.println("Can't setup server on this port number. ");
		}
    	
		try
		{
			socket = serverSocket.accept();
			socket.setKeepAlive(false);
		} catch (IOException ex)
		{
			System.out.println("Can't accept client connection. ");
		}

        File file = new File("input.jpg");
        
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("File is too large.");
        }
        
        System.out.println("File size : "+fileSize);
        
        byte[] bytes = new byte[(int) fileSize];
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

        int count;
        while ((count = bis.read(bytes)) > 0)
        {
            out.write(bytes, 0, count);
            System.out.println("Send "+count);
        }

        out.flush();
        //out.close();
        fis.close();
        bis.close();
        //socket.close();
    }
}