package com.nova.download.all.tools;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.SharedPreferences;
import android.util.Log;

import com.nova.download.all.Globals;

public class PreferenceTools {

	public static void savePreferences() {
	    SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
	    // ex. editor.putString(key, value);
	    
	    String sitesList = JSONTools.hmal2JsonArray(Globals.downloadSites).toString();	// download sites
	    
	    //Log.i("HMAL", sitesList);
	    if(sitesList.length() > 2 ) {
		    editor.putString("sites",  sitesList);
		    Log.i("HMAL", "sitesList Saved.");
	    } else {
	    	Log.i("HMAL", "sitesList too short to save.");
	    }
	    editor.commit();

	    // Save list of items to download!
	    
	    String itemsList = JSONTools.hmal2JsonArray(Globals.downloadItems).toString();	// download items
	    if(itemsList.length() > 2 ) {
	    	editor.putString("items", itemsList);
		    Log.i("HMAL", "itemsList Saved.");
	    } else {
		    Log.i("HMAL", "itemsList too short to save.");
	    }
	    editor.commit();

	}
	
	public static void restorePreferences() {
		
		Log.i("restorePreferences", "restore started");
		
		String sitesList = Globals.sharedPreferences.getString("sites", "THERE IS NO JSON");	// e.g. key, default value
		
		
		// Don't do anything if THERE IS NO JSON.
		if(sitesList.equals("THERE IS NO JSON")) {
			Log.i("ManagerActivity", "No JSON to parse. Leaving.");
			
		} else if(sitesList.length() <= 2) {
			Log.i("SitesList", "Too short to restore. Leaving.");
		} else {
			ArrayList<HashMap<String, String>> tempDownloadSites = new ArrayList<HashMap<String,String>>();
			
			try {
				 tempDownloadSites = JSONTools.jsonArray2hmal(new JSONArray(sitesList));
				// @PJM Aug 27, 2012 -- thx Jon Williams CDSC for teh code.
				Globals.downloadSites.clear();
				Globals.downloadSites.addAll(tempDownloadSites);
				Log.i("restorePreferences", "Restored " + Globals.downloadSites.size() + " sites.");
				if(Globals.lstSitesAdapter!=null)
					Globals.lstSitesAdapter.notifyDataSetChanged();

				Log.i("HMAL", "sitesList restored.");
			} catch (JSONException e) {
				Log.e("restorePreferences", "Couldn't convert string to JSON Array.");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		

		// Restore list of download items.
		String itemsList = Globals.sharedPreferences.getString("items", "THERE IS NO JSON");	// e.g. key, default value
		if(sitesList.equals("THERE IS NO JSON")) {
			Log.i("ManagerActivity", "No JSON to parse. Leaving.");
		} else if(sitesList.length() <= 2) {
			Log.i("SitesList", "Too short to restore. Leaving.");
		} else {
			ArrayList<HashMap<String, String>> tempDownloadItems = null;
			try {
				tempDownloadItems = JSONTools.jsonArray2hmal(new JSONArray(itemsList));
				if (Globals.downloadItems == null)
					Globals.downloadItems = new ArrayList<HashMap<String,String>>();
				Globals.downloadItems.clear();
				Globals.downloadItems.addAll(tempDownloadItems);
				if(Globals.lstItemAdapter!= null)					// ...this might be a problem.
					Globals.lstItemAdapter.notifyDataSetChanged();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e("restorePreferences", "itemsList couldn't get JSON.");
				e.printStackTrace();
			}
			
		}
			

		Log.i("restorePreferences", "restore finished");
		
		
	}


}
