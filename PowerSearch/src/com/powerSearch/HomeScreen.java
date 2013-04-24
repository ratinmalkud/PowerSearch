package com.powerSearch;

import textSearch.Text_screen1;
import imageSearch.ImageSearch;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import audioSearch.AudioSearch;

public class HomeScreen extends Activity {

	Button location_search;
	Button image_search;
	Button text_search;
	Button audio_search;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);
		
		location_search = (Button) findViewById(R.id.button_location_search);
		image_search = (Button) findViewById(R.id.button_image_search);
		audio_search = (Button) findViewById(R.id.button_audio_search);
		text_search = (Button) findViewById(R.id.button_text_search);

	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_screen, menu);
		return true;
	}
	
	public void initiateAudioSearch(View view){
		Intent intent = new Intent(this, AudioSearch.class);
		startActivity(intent);
	}
	
	public void initiateImageSearch(View view){
		Intent intent = new Intent(this, ImageSearch.class);
		startActivity(intent);
	}

	public void initiateTextSearch(View V){
		Intent i = new Intent(this, Text_screen1.class);
		startActivity(i);
		}
	
	public void findMe(View view){
		Intent intent = new Intent(this, LocationSearch.class);
		startActivity(intent);
	}
}
