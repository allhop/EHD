package com.nova.download.all;

import com.nova.download.all.tools.PreferenceTools;

import android.os.Bundle;
import android.app.Activity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class SingleDownloadActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// Required to access any of the stuff shown on the form, er,
    	// I mean activity.
        setContentView(R.layout.activity_single_download);
        super.onCreate(savedInstanceState);
        
        Globals.lstItems       = (ListView)findViewById(R.id.lstItems);
        Globals.lstItems.setAdapter(Globals.lstItemAdapter);

        // this is where parsing links used to happen!

        // keep this; it might be set when I'm not looking.

        // If necessary, create the list adapter and set it to the
        // currently-being-used list view.
        if (Globals.lstItemAdapter==null)
	        Globals.lstItemAdapter = new SimpleAdapter(
	        		getBaseContext(), Globals.downloadItems, R.layout.download_item,
	        		new String[]{
	        				"name",
	        				"description",
	        				"url",	
	        				"folder"
	        		} ,
	        		new int[] {
	        				R.id.lblItemName,
	        				R.id.lblItemDescription,
	        				R.id.lblItemURL,
	        				R.id.lblItemFolder
	        		}
	        );
        Globals.lstItems.setAdapter(Globals.lstItemAdapter);
        Globals.lstItemAdapter.notifyDataSetChanged();
        //show("Number of items dl'ed: " + Globals.downloadItems.size());

        
        //((ListView)findViewById(R.id.lstItems)).setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.context_menu_single_file)));
        // Register items list for receiving a context menu...
        ListView lstItems = ((ListView)findViewById(R.id.lstItems));
        registerForContextMenu(lstItems);


    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_single_download, menu);
        return true;
    }
    

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.mnuRemoveAll:
            	Globals.downloadItems.clear();
            	Globals.lstItemAdapter.notifyDataSetChanged();
            	PreferenceTools.savePreferences();
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenuInfo menuInfo) {
    	// TODO Auto-generated method stub
    	
    	super.onCreateContextMenu(menu, v, menuInfo);
    	getMenuInflater().inflate(R.menu.site_item_context_menu, menu);
    	
    	/*
    	// This isn't necessary here but I'll leave it in just because it shows how to get
    	// the resultant item.
    	// also, I could have used item.getMenuInfo... ironically this routine probably
    	// passes too many variables.
        final AdapterContextMenuInfo adapterMenuInfo = (AdapterContextMenuInfo) menuInfo;
        int pos = adapterMenuInfo.position;
        show("Position is " + pos);
        */
    }
    
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	// TODO Auto-generated method stub
    	switch(item.getItemId()) {
    		case R.id.ctx_DeleteSingleSite:
    			
    	        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	        int index = info.position;
    	        Globals.downloadItems.remove(index);
    	        Globals.lstItemAdapter.notifyDataSetChanged();
    	        PreferenceTools.savePreferences();
    	        
    			return true;
    		default:
    	    	return super.onContextItemSelected(item);
    	}
    }
    
    // helper methods!

    /**
     * Keep the very last part of a string, such as the name of the file it points to. <br>
     * Adapted from http://stackoverflow.com/questions/6319847/javascript-regex-to-get-rid-of-last-part-of-url-after-the-last-slash
     * @param url The full-sized URL to parse from
     * @return The last part of the URL, usually the filename
     */
    public String keepLastPart(String url) {
    	int lastSlashIndex = url.lastIndexOf("/");
    	if(lastSlashIndex > url.indexOf("/")+1) {	// if not in http://
    		return url.substring(lastSlashIndex+1);	// cut it off??
    	} else {
    		return url;
    	}
    }
    
    /**
     * Display a Toast message with the text of your choice.
     * @param text
     */
    public void show(String text) {
    	Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
    }


}
