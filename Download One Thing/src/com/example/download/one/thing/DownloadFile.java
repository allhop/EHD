package com.example.download.one.thing;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream.GetField;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

// usually, subclasses of AsyncTask are declared inside the activity class.
// that way, you can easily modify the UI thread from here
class DownloadFile extends AsyncTask<String, Integer, String> {
    @Override
    protected String doInBackground(String... sUrl) {
        try {
        	
        	for (int i = 0; i < sUrl.length; i++) {
                URL url = new URL(sUrl[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // this will be useful so that you can show a typical 0-100% progress bar
                int fileLength = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream());
                //File outputDirectory = new File(Environment.getDataDirectory().getPath()+"/DownAll/");
                //outputDirectory.mkdirs();
                
                if ( (new File(Environment.getExternalStorageDirectory().getPath()+"/DownAll/")).mkdirs() )		// create directory path for downloading all files!
                {
                	Log.i("DownloadFile", "Directory created");
                } else {
                	Log.i("DownloadFile","Directory not created");
                }
                
                
                java.io.File fileExists = new java.io.File(Environment.getExternalStorageDirectory().getPath()+"/DownAll/","file.jpg");
                if (!fileExists.exists()) {
	
	                
	                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/DownAll/file.jpg" );	// not sure if I did this right.
	
	                byte data[] = new byte[1024];
	                long total = 0;
	                int count;
	                while ((count = input.read(data)) != -1) {
	                    total += count;
	                    // publishing the progress....
	                    publishProgress((int) (total * 100 / fileLength));
	                    output.write(data, 0, count);
	                }
	
	                output.flush();
	                output.close();
	                input.close();
                } else {
                	Log.i("DownloadFile", "Not creating a file that already exists, sorry.");
                }
        	}
        } catch (Exception e) {
        	Log.e("DownloadFile", "broken");
        	Log.e("Error", e.getMessage());
        	e.printStackTrace();
			
		}
        return null;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity.mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        MainActivity.mProgressDialog.setProgress(progress[0]);
    }
    
    @Override
    protected void onPostExecute(String result) {
    	Log.i("DownloadFile", "Done download.");
    	super.onPostExecute(result);
    	
    	MainActivity.mProgressDialog.dismiss();
    }
    
}

