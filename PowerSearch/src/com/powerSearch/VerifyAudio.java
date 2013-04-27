package com.powerSearch;

import java.io.IOException;
import java.util.Locale;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class VerifyAudio extends Activity implements OnInitListener{
	MediaPlayer player;
	String savedAt = "/sdcard/toSearch.3gpp";
	String toSearch;
	final static int CHECK_CODE = 1;
	TextToSpeech myTTS;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verify_audio);
		
		Intent intent = getIntent();
		toSearch = intent.getStringExtra(AudioSearch.searchPhrase);
	//	toSearch = AudioSearch.getToSearch();
		
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, CHECK_CODE);
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
	
		
	/*
	 * http://android-developers.blogspot.com/2009/09/introduction-to-text-to-speech-in.html
	 */
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
	    if (requestCode == CHECK_CODE) {
	        if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
	            // success, create the TTS instance
	            myTTS = new TextToSpeech(this, this);
	        } else {
	            // missing data, install it
	            Intent installIntent = new Intent();
	            installIntent.setAction(
	                TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
	            startActivity(installIntent);
	        }
	    }
	}
	
	public void playRecording(View view){
		Toast toast = Toast.makeText(getBaseContext(), toSearch, Toast.LENGTH_SHORT);
		toast.show();
		
		myTTS.setLanguage(Locale.UK);
		myTTS.speak(toSearch, TextToSpeech.QUEUE_FLUSH, null);
	}
	
	public void recordAgain(View view){
		Intent intent = new Intent(this, AudioSearch.class);
		startActivity(intent);
	}
	
	public void performAudioSearch(View view){
		Intent search = new Intent(Intent.ACTION_WEB_SEARCH);  
		search.putExtra(SearchManager.QUERY, toSearch);  
		startActivity(search);
	}

	@Override
	public void onInit(int arg0) {
		// TODO Auto-generated method stub
		
	}
}