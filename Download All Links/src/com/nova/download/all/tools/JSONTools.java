package com.nova.download.all.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONTools {

	public static JSONArray hmal2JsonArray(ArrayList<HashMap<String, String>> hmal) {
		JSONArray resultArray = new JSONArray();
		

		try {
			for (HashMap<String, String> hashMap : hmal) {				// every hashmap in the arraylist
				JSONObject resultObject = new JSONObject();				// temporary result object
				
				Set<String> keys = hashMap.keySet();					// Every KEY in the hashmap
				for (String key : keys) {
						resultObject.put(key, hashMap.get(key));		// put key and its resulting value
				}
				
				resultArray.put(resultObject);							// Add object to array
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resultArray;												// return result array
		
	}
	
	// @PJM Aug 27, 2012
	// This might not work so well yet... just finished it and it
	// didn't help very well as a helper thing.
	public static ArrayList<HashMap<String,String>> jsonArray2hmal(JSONArray jsonArray) {
		ArrayList<HashMap<String, String>> outHmal = new ArrayList<HashMap<String,String>>();
		
		//Log.i("jsonArray2hmal", "using:" + jsonArray.toString());
		
		//JSONArray sitesListArray;
		try {
			//sitesListArray  = new JSONArray(sitesList);
			
			for (int i = 0; i < jsonArray.length(); i++) {
				String tempString = jsonArray.getString(i);
				//Log.i("tempString", tempString);
				JSONObject jsonObject = new JSONObject( tempString );							// parsed JSON String... or it better be!
				HashMap<String, String> hashMap = new HashMap<String, String>();				// A hashmap I want added to an arraylist :3
				@SuppressWarnings("unchecked")
				Iterator<String> jsonObjectElements = jsonObject.keys();						// Iterator that actually goes through the jsonobject array.
				

				while(jsonObjectElements.hasNext()) {											// Iterate through each element name
					String currentKey = jsonObjectElements.next();								// Get key as string
					hashMap.put(currentKey, jsonObject.getString(currentKey));					// pass key+value to hashmap
				}
				
				//Globals.downloadSites.add(hashMap);												// When done with hashmap, add to arraylist
				outHmal.add(hashMap);
				//Log.i("jsonArray2hmal", "Added item.");
			}
			
			return outHmal;
			//Globals.lstSitesAdapter.notifyDataSetChanged();
		} catch (JSONException e) {

			e.printStackTrace();
			return null;
		}

	}

}
