package com.powerSearch;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class Text_screen1 extends Activity {

	Button start_search;
	EditText enter_string;
	ImageView go_home;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_screen1);
	
		start_search = (Button) findViewById(R.id.button_start_search);
		enter_string = (EditText) findViewById(R.id.editText_textsearch);
		go_home =(ImageView) findViewById(R.id.homeFromTextSearch1);
		
		start_search.setOnClickListener(new OnClickListener(){
			public void onClick(View V){
			//	System.out.println("IN onclick in location screen 1");
				text_search();
				finish();
			}
	});
	}
	
	public void goHome(View view){
		Intent intent = new Intent(this, HomeScreen.class);
		startActivity(intent);
	}
	
	
	private void text_search(){
		
		String search_input = enter_string.getText().toString() ;
		Intent search = new Intent(Intent.ACTION_WEB_SEARCH);  
		search.putExtra(SearchManager.QUERY, search_input);  
		startActivity(search);  
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.text_screen1, menu);
		return true;
	}

}
