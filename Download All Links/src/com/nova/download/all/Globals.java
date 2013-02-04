package com.nova.download.all;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.SharedPreferences;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * Variables to keep track of global settings such as lists of downloads and the application
 * configuration (versus constantly redeclaring them all the time).
 * 
 * @author pmesyk
 *
 */
public class Globals {
	
	//-----------------------------------------------------------------------------------------------------------
	// Global variables that are used everywhere
	//-----------------------------------------------------------------------------------------------------------
	
	/**
	 * List of shared preferences that can be written/read. Initiated by the main screen
	 * but it should be available everywhere. 
	 */
	public static SharedPreferences sharedPreferences;
	
	//-----------------------------------------------------------------------------------------------------------
	// Globals used mostly on the main activity (ManagerActivity)
	//-----------------------------------------------------------------------------------------------------------

	/**
	 * List of the sites to download from.
	 */
	public static ArrayList<HashMap<String, String>> downloadSites = new ArrayList<HashMap<String,String>>();
	
	/**
	 * Adapter that connects downloadSites and lstSitesView.
	 */
	public static SimpleAdapter lstSitesAdapter;
	
	/**
	 * View of the list of sites to download from. Set when ManagerActivity starts.
	 */
	public static ListView lstSitesView;
	
	
	//-----------------------------------------------------------------------------------------------------------
	// Globals mostly seen only in the download items list.
	//-----------------------------------------------------------------------------------------------------------

	/**
	 * ListView of the currently downloading items.
	 */
	public static ListView lstItems;
	
	/**
	 * Adapter between lstItems and downloadItems. 
	 */
	public static SimpleAdapter lstItemAdapter;
	
	/**
	 * Collection of the names and URLs of items to download. (Most importantly URLs.)
	 * Should contain:
	 * <ul>
	 * 		<li>name -- pretty name of the file to download</li>
	 * 		<li>description -- the folder to download to</li>
	 * 		<li>url -- full location of the file</li>
	 * 		<li>folder -- local folder to store it in</li>
	 * </ul> 
	 */
	public static ArrayList<HashMap<String, String>> downloadItems = new ArrayList<HashMap<String,String>>();

	

	
}
