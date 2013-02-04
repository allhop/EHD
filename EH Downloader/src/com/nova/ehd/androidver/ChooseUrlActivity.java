package com.nova.ehd.androidver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import com.nova.ehd.error.EHDParseException;
import com.nova.ehd.java.GetFrom;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;



public class ChooseUrlActivity extends Activity {
	
	DownloadFile runningTask = null;
	
	int timeoutMillis = 2000;
	
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		if(runningTask != null) {
			outState.putString("finalURL", runningTask.finalURL);
		}
	};
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if(savedInstanceState.containsKey("finalURL")) {
			if(runningTask!= null) {
				runningTask.finalURL = savedInstanceState.getString("finalURL");
			}
		}
		
	};
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i("ChooseURL", "Starting!");
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_url);
        
        Button btnDownloadUrl = ((Button)findViewById(R.id.btnDownloadUrl));
        
        btnDownloadUrl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("onclick","onclick initialized");
				
				// Fix status bar! Set to nothing before showing.
				new Thread(new Runnable() {
					
					@Override
					public void run() {
			        	ProgressBar prgDownloads = (ProgressBar)findViewById(R.id.prgDownloads);
			        	prgDownloads.setMax(100);
			        	prgDownloads.setProgress(0);
			        	prgDownloads.setIndeterminate(true);
						
					}
				}).start();
	        	

				
				
				EditText txtFolder = (EditText)findViewById(R.id.txtFolder);
				EditText txtUrl = (EditText)findViewById(R.id.txtUrl);
				
				String nextURL = txtUrl.getText().toString();
				String folder  = txtFolder.getText().toString();
				runningTask = (DownloadFile) new DownloadFile().execute(nextURL, folder);
				
				((Button)findViewById(R.id.btnDownloadUrl)).setVisibility(View.GONE);
				((Button)findViewById(R.id.btnCancel)).setVisibility(View.VISIBLE);

				Log.i("Try",">> Trying from " + nextURL);
				
			}
		});
        
        Button btnCancel = (Button)findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (runningTask != null) {
					Log.i("Sending error", "Trying for cancel.");
					boolean cancelSuccess = runningTask.cancel(false);
					Log.i("Sending error", "Cancelled: " + String.valueOf(cancelSuccess));
					clearProgress();
					
					((Button)findViewById(R.id.btnCancel)).setVisibility(View.GONE);
					((Button)findViewById(R.id.btnDownloadUrl)).setVisibility(View.VISIBLE);

					
				} else {
					Log.i("Sending error", "No object to cancel.");
				}
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_choose_url, menu);
        return true;
    }
    
    public void setFinalURL(String finalURL) {
    	Log.i("Final url", finalURL);
    	TextView txtUrl = (TextView)findViewById(R.id.txtUrl);
    	
    	if(!finalURL.equals("")) {
    		Log.i("Final url set?", "YES.");
        	txtUrl.setText(finalURL);
    	}
    }
    
    public void setProgress(final int current, final int total) {
    	runOnUiThread(new Runnable() {
    		@Override
    		public void run() {
    	    	// Edit progress linear layout to show
    	    	LinearLayout llProgress = (LinearLayout)findViewById(R.id.llProgress);
    	    	llProgress.setVisibility(View.VISIBLE);
    	    	
    	    	// Edit text boxes appropriately
    	    	TextView txtCurrent = (TextView)findViewById(R.id.txtCurrent);
	        	TextView txtAll= (TextView)findViewById(R.id.txtAll);
	        	txtCurrent.setText(String.valueOf(current));
	        	txtAll.setText(String.valueOf(total));
	        	
	        	ProgressBar prgDownloads = (ProgressBar)findViewById(R.id.prgDownloads);
	        	Log.w("PROGRESS", current + "/" + total);
	        	prgDownloads .setIndeterminate(false);
	        	prgDownloads.setProgress(current);
	        	prgDownloads.setMax(total);

    		}
    	});
    }
    
    public void setLittleProgress(final int current, final int total) {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ProgressBar prgCurrentDownload = (ProgressBar)findViewById(R.id.prgCurrentDownload);
				prgCurrentDownload.setIndeterminate(false);
				prgCurrentDownload.setMax(total);
				prgCurrentDownload.setProgress(current);
				
			}
		});
    }
    
    public void clearProgress() {
    	// Edit progress linear layout to HIDE
    	new Thread(new Runnable() {
			@Override
			public void run() {
	        	ProgressBar prBar = (ProgressBar)findViewById(R.id.prgDownloads);
	        	prBar.setIndeterminate(true);
	        	
		    	LinearLayout llProgress = (LinearLayout)findViewById(R.id.llProgress);
		    	llProgress.setVisibility(View.INVISIBLE);
		    	
		    	ProgressBar prgCurrentDownload = (ProgressBar)findViewById(R.id.prgCurrentDownload);
		    	prgCurrentDownload.setIndeterminate(true);
		    	
			}
		}).run();
    	
    }

    /**
     * Asynchronous task to download a set of files.
     * @author pmesyk
     *
     */
    class DownloadFile extends AsyncTask<String, Integer, String> {
    	//String mFileToDownload=null;
    	//String mFolder=null;
    	String exstPath=null;				// Path to the external SD.
    	
    	String finalURL = "";				// the last URL that was loaded. Set into the textbox sometimes.
    	
    	
        @Override
        protected String doInBackground(String... sUrl) {
        	
        	//boolean shouldIPause = false;												// decides whether you should pause while executing
			HashMap<String, String> downloaditems = new HashMap<String, String>();		// Informations returned from the EHentai2File routine about what to DL, cookies, etc.

        	String nextURL = sUrl[0];
        	String folder = sUrl[1];
        	
        	// Make FOLDER.
            File exst = Environment.getExternalStorageDirectory();
            exstPath = exst.getPath();

            
            // Checks to make sure a proper path could be created.
            // This should keep weird folder names from being selected.
            String newPath = exstPath + "/ehd/" + folder;
            Log.i("new path", newPath);
            File ehd = new File(newPath);
            ehd.mkdirs();
            boolean success = ehd.exists();
            if (success==false)
            	return "Folder " + newPath + " could not be created."; 
            


            // Keeps track of present, and last, pages.
            int thisPage=0;
            int lastPage=0;
            
            Log.i("StorageState", Environment.getExternalStorageState());
            Log.i("new path success", String.valueOf(success));
            
			if (isCancelled()) return "Cancelled";

			while (!nextURL.equals("")) {
				
				if (isCancelled()) return "Cancelled";
				try {
					downloaditems = GetFrom.EHentai2FileAndURL(nextURL, folder);
				} catch (EHDParseException e1) {
					// TODO Auto-generated catch block
					Log.e("DL ERROR",e1.getMessage());
					downloaditems = null;
				}
				
				

				if (downloaditems==null) {
					if (thisPage==lastPage && thisPage != 0 && lastPage != 0) {
						return "Download complete!";
					} else {
						return "Downloading error";
					}
				}

				
				// Set progress, only if there's things to set!
				if (downloaditems.get("thisPage") != null &&						
						downloaditems.get("lastPage")!=null)
				{
					thisPage = Integer.valueOf(downloaditems.get("thisPage"));
					lastPage = Integer.valueOf(downloaditems.get("lastPage"));
					
					// Set progress using my user-defined function
					ChooseUrlActivity.this.setProgress(thisPage, lastPage);
				}
				
				
				
				if (isCancelled()) return "Cancelled";

				if (
					downloaditems.get("imageLocation")!= null
						&& downloaditems.get("nextUrl")!= null)
				{
					Log.i("Downloading", downloaditems.get("imageLocation"));
					// for now...
					downloadOneFile(folder, downloaditems.get("imageLocation"));
					nextURL = downloaditems.get("nextUrl");
				} else {
					nextURL = "";
				}
				
				
				// Set final url :3
				if(!nextURL.equals("")) finalURL = nextURL;
				
				
				// This causes the thing to stop for 1 sec.
				try {
					//System.out.println("Sleeping...");
					if (isCancelled()) return "Cancelled";
					Thread.sleep(timeoutMillis);
					if (isCancelled()) return "Cancelled";
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//end try

			}//end while

        	return "Done.";
        	
        	
        }//end DoInBackground
        
        
        /**
         * 
         * @param aFolder
         * @param aFileToDownload
         */
		public void downloadOneFile(String aFolder, String aFileToDownload) {
        	
			String fileToDownload = exstPath+"/ehd/" + aFolder + "/" + GetFrom.Url2Name(aFileToDownload);
			
            // Check if a file exists before going anywhere.
			// If the file exists, WE DO NOT DOWNLOAD IT AGAIN!
			// THIS IS THE RULE OF HAVING MULTIPLE FOLDERS IN WHICH
			// TO DOWNLOAD DELIGHTFUL THINGS LIKE THIS.
            File fileToDownloadExistsCheck = new File(fileToDownload);
            if (fileToDownloadExistsCheck.exists()) {
            	Log.i("downloadOneFile", "File already exists; skipping :)");
            	return;
            }

			
            try {
            	
            	URL url = new URL(aFileToDownload);
            	URLConnection connection = url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0");
            	

                int lengthOfFile = connection.getContentLength();						// file length
                InputStream input = new BufferedInputStream(url.openStream(), 8192);	// read with 8k buffer	(yay InputStream interface!)
                OutputStream output = new FileOutputStream(fileToDownload);				// write to file		(yay OutputStream interface!)
                
                int count;							// count of read data
                byte data[] = new byte[1024];		// 1kb
                long total = 0;						// total read
                
                // read data, and then make sure read data isn't -1 (a fail). If data read, continue.
                Log.i("downloadOneFile", "Starting download of " + fileToDownload);
                while((count = input.read(data)) != -1) {
                	total += count; 
                	// publishing the progress here...
                	//Log.i("buffered load", ""+(int)((total*100)/lengthOfFile));
                	ChooseUrlActivity.this.setLittleProgress((int)((total*100)/lengthOfFile), 100);
                	
                	// write to file
                	output.write(data, 0, count);
                	
                	// Cancellation possibility WITHIN the actual program.
                	// No word if this'll wreck partial files.
                	if(isCancelled()) {
                		return;
                	}
                	
                }
                
                // Flush output here.
                output.flush();
                
                // Recycling.
                input.close();
                output.close();
            	
            	

            } catch (Exception e) {
            	if(e==null || Log.class==null || e.getMessage()==null) {
            		Log.e("Error!", "Can't even print the #$@*& message.");
            	} else {
            		Log.e("Error!", e.getMessage());
            	}
            	e.printStackTrace();
            }//end try
            return;
        }//end DownloadOneFile
		
		
        
        @Override
        protected void onPostExecute(String result) {
        	finalizeThings();
        	
        	super.onPostExecute(result);
        	
        	Log.i("onPostExecute","reached.");
        	Log.i("onPostExecute","result: " + result);
        	
        	Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        	
        	Log.i("Final URL", finalURL);
        }
        
        @Override
        protected void onCancelled() {
        	finalizeThings();
        	
        	super.onCancelled();
        	
        	Log.i("onPostExecute","reached.");
        	Log.i("onPostExecute","result: " + "none");
        	
        	Toast.makeText(getApplicationContext(), "Download cancelled.", Toast.LENGTH_SHORT).show();

        	Log.i("Final URL", finalURL);

        }
        
        void finalizeThings() {
        	ChooseUrlActivity.this.setFinalURL(finalURL);
        	ChooseUrlActivity.this.clearProgress();
        }
    }

}



