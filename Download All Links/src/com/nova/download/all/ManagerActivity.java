
package com.nova.download.all;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.nova.download.all.tools.PreferenceTools;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ManagerActivity extends Activity {
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// gotta do this before we can actually find view items b ID.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        
        findViewById(R.id.btnStop).setVisibility(View.GONE);
        
        Globals.sharedPreferences = getSharedPreferences(getString(R.string.app_package), MODE_PRIVATE);

        // Restore instance, if necessary.
        // I THINK that would be done/called here...
        Log.i("ManagerActivity", "this is where we should be restoring preferences.");
        PreferenceTools.restorePreferences();

        
    	Globals.lstSitesView = (ListView) findViewById(R.id.listView1);
    	Globals.lstSitesAdapter = new SimpleAdapter(
        		this,
        		Globals.downloadSites,
        		R.layout.site_item,		//android.R.layout.two_line_list_item,

        		new String[]{
        				"name",
        				"url",
        		},
        		new int[] {
        				R.id.site_name,
        				R.id.url
        		});
    	Globals.lstSitesView.setAdapter(Globals.lstSitesAdapter);
    	
    	

                
    	Globals.lstSitesView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
	    		Intent editIntent = new Intent(getApplicationContext(), EditActivity.class);	// where to go
	    		editIntent.putExtra(android.content.Intent.EXTRA_TEXT, "number=" + String.valueOf(position));	// add "add" descriptor... want to add not modify.
	    		startActivity(editIntent);
				
			}
		});
    	
    	findViewById(R.id.btnAdd).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addItemClicked();
			}
		});
    	
        findViewById(R.id.btnStart).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getBaseContext(), "Start button clicked", Toast.LENGTH_SHORT).show();

				((Button)findViewById(R.id.btnAdd)).setVisibility(View.GONE);
				((Button)findViewById(R.id.btnStart)).setVisibility(View.GONE);
				((Button)findViewById(R.id.btnStop)).setVisibility(View.VISIBLE);
				
				// parse links here instead... because because.
		        // by this time the arraylist should be defined too. :)
		        
		        if (Globals.downloadItems==null)
		        	Globals.downloadItems = new ArrayList<HashMap<String,String>>();
		        
		        
		        // Link parsing, all happens here. :)
		        int numLinks=0;
		        int numImages=0;
		        
		        for (HashMap<String, String> downloadSite : Globals.downloadSites) {
		        	
		        	String url = downloadSite.get("url");			// site's base URL, used by parser not individual downloads themselves
		        	String siteName = downloadSite.get("name");		// for saving in the single item listbox, under "description"
		        	String regex = downloadSite.get("regex");		// regex for checking URLs against
		        	String folder = downloadSite.get("folder");		// folder name to save downloads in
		        	boolean parseLinks  = Boolean.parseBoolean(downloadSite.get("scrapeUrl"));
		           	boolean parseImages = Boolean.parseBoolean(downloadSite.get("scrapeImg"));
		        	
		        	
		            //String url = "http://wondrouspics.com/cute-kittens-pictures/";
		        	//String regex = "([^\\s]+(\\.(?i)(bmp|gif|jpg|png|psd|pspimage|tga|tif))$)";	
		            // many thx to http://www.mkyong.com/regular-expressions/how-to-validate-image-file-extension-with-regular-expression/comment-page-1/#comment-85956        
		            
		            Log.i("SingleDownload", "Fetching" +url+"...");

		            Document doc=null;
		    		try {
		    			doc = Jsoup.connect(url).get();
		    		} catch (IOException e) {
		    			show("Cannot connect to url " + url);
		    			Log.e("SingleDownload","Got stuck trying to make a connection.");
		    			e.printStackTrace();
		    			finish();
		    			return;
		    			//return;	 (same as finish(); ?)
		    		}
		    		
		    		
		            Elements links = doc.select("a[href]");
		            Elements images = doc.select("img[src]");


		            Log.i("SingleDownload", "Fetching" +url+"...");
		            
		            if(parseLinks) {
			            Log.i("SingleDownload", "Links:");
			            for (Element link : links) {
			            	if (regex.equals("") || link.attr("abs:href").matches(regex)) {
			            		Log.i("SingleDownload", " * a: <"+link.attr("abs:href")+">");
			            		
			            		HashMap<String, String> temp = new HashMap<String, String>();
			            		String downloadUrl = link.attr("abs:href");
			            		String downloadName = keepLastPart(downloadUrl);
			            		
			            		temp.put("name",        downloadName);
			            		temp.put("description", siteName);
			            		temp.put("folder",      folder);
			            		temp.put("url",         downloadUrl);
			            		
			            		Globals.downloadItems.add(temp);
			            		
			            		//print("Is picture");
			            		numLinks++;
			            	}
			            }
		            } else {
		            	Log.i("SingleDownload", "Link parsing skipped.");
		            }

		            if(parseImages) {
			            Log.i("SingleDownload", "Images:");
			            for (Element image : images) {
			            	
			            	if (regex.equals("") || image.attr("abs:src").matches(regex)) {
			            		Log.i("SingleDownload", " * a: <"+image.attr("abs:src")+">");

			            		HashMap<String, String> temp = new HashMap<String, String>();
			            		String downloadUrl = image.attr("abs:src");
			            		String downloadName = keepLastPart(downloadUrl);
			            		
			            		temp.put("name",        downloadName);
			            		temp.put("description", siteName);
			            		temp.put("folder",      folder);
			            		temp.put("url",         downloadUrl);
			            		
			            		Globals.downloadItems.add(temp);

			            		
			            		numImages++;
			            	}
			
			            }
		            } else {
		            	Log.i("SingleDownload", "Image parsing skipped.");
		            }
		            
					
				}
		        
		        if((numLinks+numImages)> 0) {
		        	show("Links added: " + numLinks
		        			+ "\nImages added: " + numImages);
		        }
		        
		        show("Number of items in arraylist: " + Globals.downloadItems.size());

				
				startActivity(new Intent(getBaseContext(), SingleDownloadActivity.class));
			}
		});
        
        findViewById(R.id.btnStop).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getBaseContext(), "Stop button clicked", Toast.LENGTH_SHORT).show();

				// opposite of start button, actually...
				((Button)findViewById(R.id.btnAdd)).setVisibility(View.VISIBLE);
				((Button)findViewById(R.id.btnStart)).setVisibility(View.VISIBLE);
				((Button)findViewById(R.id.btnStop)).setVisibility(View.GONE);
			}
		});


    }
    
    
    
    /*
	 * Temporary: Put a little data into the main list adapter. This is because I'm so #($*&#*% tired of
	 * having to type one in every time... lol. If my program even used it yet...
	 */
    @SuppressWarnings("unused")
	private void tmpAddTestSites() {
    	
		HashMap<String, String> temp = new HashMap<String, String>();
		String siteName = "Test site";
		String siteURL  = "http://test.com";
		String siteRegex = "(test)";
		boolean scrapeImg = true;
		boolean scrapeUrl = true;
		
		temp.put("name",  siteName);														// Add to the pair
		temp.put("url",   siteURL);
		temp.put("regex", siteRegex);
		temp.put("scrapeImg", String.valueOf(scrapeImg));
		temp.put("scrapeUrl" , String.valueOf(scrapeUrl));
		
		// DO NOT USE GETBOOLEAN. EVER.
		// USE PARSEBOOLEAN.
		
		Globals.downloadSites.add(temp);
		Globals.lstSitesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	PreferenceTools.savePreferences();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	PreferenceTools.restorePreferences();
    }
    
    
    /* 
     * Options Menu stuff. Most of this I don't really want to keep, because
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_manager, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.menu_add:
    		addItemClicked();
			break;
		case R.id.menu_show_queue:
			startActivity(new Intent(getBaseContext(), SingleDownloadActivity.class));
			break;
		default:
			break;
		}
    	
    	
    	return super.onOptionsItemSelected(item);
    }
    
    public void btnAddItem_Click(View v) {
    	showToast("add clicked");
    }
    
    
    private void showToast(String msg){
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
    
    private void addItemClicked() {
		
		Intent addIntent = new Intent(this, EditActivity.class);	// where to go
		addIntent.putExtra(android.content.Intent.EXTRA_TEXT, "add");					// add "add" descriptor... want to add not modify.
		startActivity(addIntent);

    }
    
    
    public void show(String text) {
    	Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
    }

    
    public String keepLastPart(String url) {
    	int lastSlashIndex = url.lastIndexOf("/");
    	if(lastSlashIndex > url.indexOf("/")+1) {	// if not in http://
    		return url.substring(lastSlashIndex+1);	// cut it off??
    	} else {
    		return url;
    	}
    }
    
    

}
