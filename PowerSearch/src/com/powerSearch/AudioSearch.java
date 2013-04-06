package com.powerSearch;

import java.io.File;
import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AudioSearch extends Activity {
	MediaRecorder recorder;
	MediaPlayer player;
	
	Button startButton;
	Button stopButton;
	ImageView home;
	
	String saveAt = "/sdcard/toSearch.3gpp";
	
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
