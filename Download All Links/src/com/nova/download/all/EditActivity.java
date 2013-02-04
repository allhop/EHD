package com.nova.download.all;

import java.util.HashMap;

import com.nova.download.all.tools.PreferenceTools;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class EditActivity extends Activity {
	
	String intent="";
	int finalNumber = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {

    	// These usually should be at the top. :3
    	// Any reference (findViewById) to any object on this form
    	// will crash, unless setContentView (I think) is run.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
    	
    	
    	Bundle extras = getIntent().getExtras();
    	intent = extras.getString(Intent.EXTRA_TEXT);	// There should be a lot of "not null" checking here. But there's not. Because I'm a moron.
    	
    	
    	String  downloadName;
    	String  downloadURL;
    	String  downloadFolder;
    	String  downloadRegex;
    	boolean chkScrapeImg;
    	boolean chkScrapeUrl;
    	
    	if(intent.startsWith("number=")) {
    		// fill information from what we've already ... umm... this.
    		finalNumber = Integer.valueOf(intent.substring(7));	// starts after "=" ends
    		HashMap<String, String> editItem = Globals.downloadSites.get(finalNumber);
    		
    		downloadName  = editItem.get("name");
    		downloadURL   = editItem.get("url");
    		downloadFolder= editItem.get("folder");
    		downloadRegex = editItem.get("regex");
    		chkScrapeImg  = Boolean.parseBoolean(editItem.get("scrapeImg"));
    		chkScrapeUrl  = Boolean.parseBoolean(editItem.get("scrapeUrl"));
    		
    		// Show Delete button (usually shown, but in case it wasn't there from before)
    		((Button)findViewById(R.id.btnDelete)).setVisibility(View.VISIBLE);
    	} else {
    		// New download
    		downloadName  = "";	// no name or URL by default
    		downloadURL   = "";
    		downloadFolder= "";
    		downloadRegex = "";
    		chkScrapeImg  = true; 	// Really are checked by default.
    		chkScrapeUrl  = true;	// Yes they are.
    		
    		// Hide Delete button (you cannot delete that which is not there!)
    		((Button)findViewById(R.id.btnDelete)).setVisibility(View.GONE);
    	}
    	
		((EditText)findViewById(R.id.txtSiteName)).setText(downloadName);
		((EditText)findViewById(R.id.txtSiteURL)).setText(downloadURL);
		((EditText)findViewById(R.id.txtDownloadFolder)).setText(downloadFolder);
		((EditText)findViewById(R.id.txtWhatToDownload)).setText(downloadRegex);
		((CheckBox)findViewById(R.id.chkScrapeImages)).setChecked(chkScrapeImg);
		((CheckBox)findViewById(R.id.chkScrapeLinks)).setChecked(chkScrapeUrl);
    	

        findViewById(R.id.btnSave).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// Create hashmap to store each of the things that need to be saved
				// and temporary variables for each of the items, found on the GUI
				HashMap<String, String> temp = new HashMap<String, String>();						// defines a key/value pair to store site name/url in
				String siteName = ((EditText)findViewById(R.id.txtSiteName)).getText().toString();	// retrieve both from edit boxes 
				String siteURL  = ((EditText)findViewById(R.id.txtSiteURL)).getText().toString();
				String downloadFolder = ((EditText)findViewById(R.id.txtDownloadFolder)).getText().toString();
				String siteRegex  = ((EditText)findViewById(R.id.txtWhatToDownload)).getText().toString();
				boolean scrapeImg = ((CheckBox)findViewById(R.id.chkScrapeImages)).isChecked();
				boolean scrapeUrl = ((CheckBox)findViewById(R.id.chkScrapeLinks)).isChecked();
				
				if(siteURL.trim().equals("")) {
					// Make sure user adds a URL at least
					Toast.makeText(getBaseContext(), "You must have a URL to download from, at least!", Toast.LENGTH_SHORT).show();
					return;
				} else {
					if(siteName.trim().equals("")) {
						// Default site name is site folder, unless there is no special folder. Then just use a URL.
						if(!downloadFolder.trim().equals("")) {
							siteName = downloadFolder;			// use download folder's name as site name
						} else {
							siteName = siteURL;					// use site's URL instead... ugliest but still makes for being unique :)
						}
						
					}
				}
				
				// Put GUI items into the hashmap
				temp.put("name",  siteName);														// Add to the pair
				temp.put("url",   siteURL);
				temp.put("folder", downloadFolder);
				temp.put("regex", siteRegex);
				temp.put("scrapeImg", String.valueOf(scrapeImg));	// convert boolean to string on-the-fly :)
				temp.put("scrapeUrl" , String.valueOf(scrapeUrl));
				
				// Add or save the map to the current list, depending on which
				// intent called this.
				if (intent.equals("add") || intent.equals("")) {
					// New download site
					Globals.downloadSites.add(temp);						// Add new site
					Log.i("EditActivity","Site added to list");
				} else if (intent.startsWith("number=")) {
					// Change a current download site
					Globals.downloadSites.set(0, temp);						// edit site
					Log.i("EditActivity","Site Edited");
				}
				
				PreferenceTools.savePreferences();
				Globals.lstSitesAdapter.notifyDataSetChanged();				// notify change
				finish();													// Leave this activity
				
			}
		});
        findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();	// leave this view without saving contents :3
			}
		});
        
        findViewById(R.id.btnDelete).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Globals.downloadSites.remove(finalNumber);
				PreferenceTools.savePreferences();
				//Globals.lstSitesAdapter.notifyDataSetChanged();
				finish();		// leave activity
			}
		});
        
        
        ((Spinner)findViewById(R.id.spnWhatToDownload)).setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				
				if (pos <= 0)		// don't change anything for the default
					return;
				
				EditText txtWhatToDownload = ((EditText)findViewById(R.id.txtWhatToDownload));			// get txtWhatToDownload text editor
				String[] downloadFilters = getResources().getStringArray(R.array.download_filters);		// get download filters
				String downloadText = downloadFilters[pos];												// find text to set
				downloadText = downloadText.substring(downloadText.lastIndexOf(" ")+1);					// remove everything before the last space, including the space (the +1).
				
				
				txtWhatToDownload.setText(downloadText);	// set text to editor
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// I'm pretty sure I can live with "nothing" being done... keep the default!
			}
		});
		
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit, menu);
        return true;
    }
    
}
