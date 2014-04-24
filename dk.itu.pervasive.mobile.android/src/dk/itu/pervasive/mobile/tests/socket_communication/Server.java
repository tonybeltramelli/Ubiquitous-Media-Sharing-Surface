package dk.itu.pervasive.mobile.tests.socket_communication;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
	public static void main(String[] args) throws IOException
	{
		ServerSocket serverSocket = null;
		
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
		
		try
		{
			socket = serverSocket.accept();
			socket.setKeepAlive(false);
		} catch (IOException ex)
		{
			System.out.println("Can't accept client connection. ");
		}
		
		BufferedReader stream = new BufferedReader(new InputStreamReader(socket.getInputStream() , "UTF-16"));

		stream.readLine();
		
		try
		{
			Thread.sleep(1000);
			
			is = socket.getInputStream();
			bufferSize = socket.getReceiveBufferSize();
			System.out.println("Buffer size: " + bufferSize);
		} catch (Exception ex)
		{
			System.out.println("Can't get socket input stream. ");
		}	
		
		/*
		while (true)
		{*/
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			
			try
			{
				fos = new FileOutputStream("output_" + Math.random() * 1000 + ".jpg");
				bos = new BufferedOutputStream(fos);
			} catch (FileNotFoundException ex)
			{
				System.out.println("File not found. ");
			}
			
			byte[] bytes = new byte[bufferSize]; //new byte[1024];
			
			int count;
			while ((count = is.read(bytes)) != -1)
			{
				bos.write(bytes, 0, count);
				System.out.println("read " + count);
			}
			
			/*
			try
			{
				String msg = "[{\"action\":\"1\"}]\n";
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-16"));
				
				writer.write(msg);
				writer.flush();
			} catch (Exception e)
			{
				e.printStackTrace();
			}*/
			
			bos.flush();
			bos.close();
		//}
		
		is.close();
		socket.close();
		serverSocket.close();
	}
}