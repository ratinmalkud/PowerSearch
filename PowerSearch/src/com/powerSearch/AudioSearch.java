package com.powerSearch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AudioSearch extends Activity {
	MediaRecorder recorder;
	MediaPlayer player;
	final int REQUEST_SPEECH = 13;
	public final static String searchPhrase = "This is the search phrase";
	ArrayList < String > toSearch;
	
	Button startButton;
	Button stopButton;
	ImageView home;
	
	String saveAt = "/sdcard/toSearch.3gpp";
	/*
	public static String getToSearch() {
		return toSearch;
	}

	public void setToSearch(String toSearch) {
		AudioSearch.toSearch = toSearch;
	}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_search);
		
		startButton = (Button)findViewById(R.id.startRecordingButton);
		stopButton = (Button)findViewById(R.id.stopRecordingButton);
		home = (ImageView)findViewById(R.id.homeFromAudio1);
		
		stopButton.setVisibility(View.INVISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.audio_search, menu);
		return true;
	}
	
	public void goHome(View view){
		Intent intent = new Intent(this, HomeScreen.class);
		startActivity(intent);
	}
	/*
	public void startRecording(View view){
		
		if(recorder != null){
			recorder.release();
		}
		
		stopButton.setVisibility(View.VISIBLE);
		//startButton.setClickable(false);
		
		File outFile = new File(saveAt);
		if(outFile.exists())
		{
			outFile.delete();
		}
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(saveAt);
		try {
			recorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		recorder.start();
	}
	*/
	
	public void startRecording(View view){
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-UK");
		startActivityForResult(intent, REQUEST_SPEECH);
	}
	
	 protected void onActivityResult(int requestCode, int resultCode, Intent data){
		 super.onActivityResult(requestCode,resultCode,data);
		 
		 if(resultCode == RESULT_OK){
			 switch(requestCode){
			 case REQUEST_SPEECH:{
				 toSearch = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				 
				 Intent intent = new Intent(this, VerifyAudio.class);
				 intent.putExtra(searchPhrase, toSearch.get(0));
				 startActivity(intent);
			 }
			 }
		 }
	 }
	 
	public void stopRecording(View view){
		//startButton.setClickable(true);
		
		if (recorder != null){ 
			recorder.stop();
		}
		
		if (recorder != null) 
		{
			recorder.release();
		}
		
		Intent intent = new Intent(this, VerifyAudio.class);
		startActivity(intent);
	}

}
