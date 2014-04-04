import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Server
{
	public static void main(String[] args) throws IOException
	{
		ServerSocket serverSocket = null;
		
		int counter = 0;
		final int MAX = 5;
		
		try
		{
			serverSocket = new ServerSocket(2500);
		} catch (IOException ex)
		{
			System.out.println("Can't setup server on this port number. ");
		}
		
		Socket socket = null;
		InputStream is = null;
		
		int bufferSize = 0;
		
		DataInputStream input = null;
		BufferedReader reader;
		
		try
		{
			socket = serverSocket.accept();
			socket.setKeepAlive(false);
		} catch (IOException ex)
		{
			System.out.println("Can't accept client connection. ");
		}
		
		try
		{
			is = socket.getInputStream();
			input = new DataInputStream(is);
			
			//reader = new BufferedReader(is);
			bufferSize = socket.getReceiveBufferSize();
			System.out.println("Buffer size: " + bufferSize);
		} catch (IOException ex)
		{
			System.out.println("Can't get socket input stream. ");
		}
		
		while (counter < MAX)
		{
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			
			System.out.println("New buffer size: " + bufferSize);
			
			try
			{
				fos = new FileOutputStream("output_" + Math.random() * 1000 + ".jpg");
				bos = new BufferedOutputStream(fos);
			} catch (FileNotFoundException ex)
			{
				System.out.println("File not found. ");
			}
			
			byte[] bytes = new byte[bufferSize];
			/*byte[] fileSizeBytes = new byte[8];
 			
			is.read(fileSizeBytes);
			
			ByteBuffer b = ByteBuffer.wrap(fileSizeBytes);*/
			
			System.out.println("file size : "+input.readLong());
			
			int count;
			while ((count = is.read(bytes)) != -1)
			{
				//System.out.println("read byte : " + count);
				
				bos.write(bytes, 0, count);
			}
			
			bos.flush();
			bos.close();
			
			counter++;
		}
		
		is.close();
		socket.close();
		serverSocket.close();
	}
}