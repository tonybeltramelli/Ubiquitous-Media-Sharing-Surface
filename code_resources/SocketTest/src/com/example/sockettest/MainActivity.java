package com.example.sockettest;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	   private static final int SELECT_PICTURE = 1;

	    private String selectedImagePath;
	    private ImageView img;

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);
	        System.out.println("34");
	        img = (ImageView) findViewById(R.id.ivPic);
	        System.out.println("36");
	        ((Button) findViewById(R.id.bBrowse))
	                .setOnClickListener(new OnClickListener() {
	                    public void onClick(View arg0) {
	                        System.out.println("40");
	                        Intent intent = new Intent();
	                        intent.setType("image/*");
	                        intent.setAction(Intent.ACTION_GET_CONTENT);
	                        startActivityForResult(
	                                Intent.createChooser(intent, "Select Picture"),
	                                SELECT_PICTURE);
	                        System.out.println("47");
	                    }
	                });
	        ;
	        System.out.println("51");
	        Button send = (Button) findViewById(R.id.bSend);
	        final TextView status = (TextView) findViewById(R.id.tvStatus);

	        send.setOnClickListener(new View.OnClickListener() {

	            @Override
	            public void onClick(View arg0) {

	                	TCPTask task = new TCPTask();
	                	task.selectedImagePath = selectedImagePath;
	                	task.execute();


	            }
	        });
	    }
	    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        if (resultCode == RESULT_OK) {
	            if (requestCode == SELECT_PICTURE) {
	                Uri selectedImageUri = data.getData();
	                selectedImagePath = getPath(selectedImageUri);
	                TextView path = (TextView) findViewById(R.id.tvPath);
	                path.setText("Image Path : " + selectedImagePath);
	                img.setImageURI(selectedImageUri);
	            }
	        }
	    }

	    public String getPath(Uri uri) {
	        String[] projection = { MediaStore.Images.Media.DATA };
	        Cursor cursor = managedQuery(uri, projection, null, null, null);
	        int column_index = cursor
	                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        cursor.moveToFirst();
	        return cursor.getString(column_index);
	    }	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	
	class TCPTask extends AsyncTask<Void, Void, Void> {
		public String selectedImagePath;

		


		@Override
		protected Void doInBackground(Void... params) {
			Socket socket = null;
			try {

                socket = new Socket("10.25.252.182", 8888); 
                System.out.println("Connecting...");
                File myFile = new File (selectedImagePath); 
                byte [] mybytearray  = new byte [(int)myFile.length()];
                FileInputStream fis = new FileInputStream(myFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                bis.read(mybytearray,0,mybytearray.length);
                OutputStream os = socket.getOutputStream();
                System.out.println("Sending...");
                os.write(mybytearray,0,mybytearray.length);
                os.flush();
				
			
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

		}
	}


}
