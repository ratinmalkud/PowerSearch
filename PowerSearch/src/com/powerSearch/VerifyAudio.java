package com.powerSearch;

import java.io.IOException;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class VerifyAudio extends Activity {
	MediaPlayer player;
	String savedAt = "/sdcard/toSearch.3gpp";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verify_audio);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.verify_audio, menu);
		return true;
	}
	
	public void goHome(View view){
		Intent intent = new Intent(this, HomeScreen.class);
		startActivity(intent);
	}
	
	public void playRecording(View view){
		if (player != null) 
		{
			try {
				player.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		player = new MediaPlayer();
		try {
			player.setDataSource(savedAt);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			player.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		player.start();
	}
	
	public void recordAgain(View view){
		Intent intent = new Intent(this, AudioSearch.class);
		startActivity(intent);
	}
	
	public void performAudioSearch(View view){
		
	}
}
