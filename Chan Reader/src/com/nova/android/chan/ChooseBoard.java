package com.nova.android.chan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;



public class ChooseBoard extends Activity {
	
	ArrayList<HashMap<String, String>> boardList;
	ListView lstBoards;
	SimpleAdapter lstBoardsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

    	/* 
    	 * don't comment these, you moron.
    	 */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_board);

    	
    	String URL = "https://boards.4chan.org/q/";
    	
    	boardList = new ArrayList<HashMap<String,String>>();
    	lstBoards = (ListView) findViewById(R.id.listView1);
    	
    	
    	
    	// JSoup attempt.
        try {
            Document doc = Jsoup
            		.connect(URL)
            		.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:16.0) Gecko/20120821 Firefox/16.0")
            		.get();
            
            //Log.i("DOC", doc.toString());
            Element bnd = doc.getElementById("boardNavDesktop");
            Elements boards = bnd.getElementsByTag("a");
            
            
            lstBoardsAdapter = new SimpleAdapter(
            		this,
            		boardList,
            		R.layout.board_item,		//android.R.layout.two_line_list_item,

            		new String[]{
            				"line1",
            				"line2",
            				"line3"
            		},
            		new int[] {
            				R.id.text1,
            				R.id.text2,
            				R.id.text3
            		});
            

            
            lstBoards.setAdapter(lstBoardsAdapter);
            
            
            /*******************POPULATE LIST*******************************************************************************/

            
            
            String boardName;
            String boardURL;
            String boardURLRel;
            int boardURLRelPos;
            HashMap<String, String> boardListItem;
            
            for (Element element : boards) {
            	boardName = element.attr("title");
            	boardURL = element.attr("abs:href");
            	boardURLRel = element.attr("href");
            	
            	boardURLRelPos = boardURLRel.lastIndexOf("/", boardURLRel.length()-2);
            	
            	//
				//Log.i("boardname",boardName);
			
            	if(
            			!boardName.equals("")
            			&& boardURLRelPos > -1
            			&& boardURLRel.lastIndexOf("/") == boardURLRel.length()-1
            	) {
            		boardURLRel = boardURLRel.substring(boardURLRel.lastIndexOf("/", boardURLRel.length()-2));
            		
            		if (boardURLRel.length() < 5) {
            		
            		// each new item must be a new hashmap. Can't keep re-using the same one, then every item
            		// would be the last item. :(
            		boardListItem= new HashMap<String, String>();
					boardListItem.put("line1", boardURLRel);	// Use two key-value pairs to fill both of these
					boardListItem.put("line2", boardName);	// ... line1 AND line2. Both will be used later.
					boardListItem.put("line3", boardURL);
					boardList.add(boardListItem);
	
					//Log.i("bli1", boardListItem.get("line1"));
					//Log.i("bli2", boardListItem.get("line2"));
            		} else {
            			Log.i("bli", "avoided a too-long url (like rs.4chan.org).");	
            		}
            	} else {
            		Log.i("bli", "avoided bad board name.");
            	}
			}
            
            // update list items
			lstBoardsAdapter.notifyDataSetChanged();
			lstBoards.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View view,	// in this case, the View is the entire item that was selected, which contains text1, text2, text3, etc.
						int index, long arg) {
					TextView v = (TextView) view.findViewById(R.id.text3);
					Toast.makeText(getApplicationContext(), "full URL Is " + v.getText(), Toast.LENGTH_SHORT).show();
					// TODO Auto-generated method stub
					
				}
			});
			
        } catch (IOException e) {
        	Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_choose_board, menu);
        return true;
    }
    
    
    public void lstBoards_onClick(View view) {
    	String absoluteURL = (String) ((TextView)view.findViewById(R.id.text3)).getText();
    	
    	Toast.makeText(this, "clicked on the view with abs url of " + absoluteURL, Toast.LENGTH_SHORT).show();
    }
    
}
