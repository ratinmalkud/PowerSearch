package com.powerSearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HomeScreen extends Activity {

	Button location_search;
	Button image_search;
	Button text_search;
	Button audio_search;
	
	String text_input;
	double lat, lng;
	LocationManager locMgr;
	LocationListener locListener;
	
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
		
		
		
		locMgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		locListener = new LocationListener()
		{
			public void onLocationChanged(Location location)
			{
			if (location != null)
			{
				lat = location.getLatitude();
				lng = location.getLongitude();
				
				startLocation();
			}
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}};
			
			locMgr.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER,
					0, // minTime in ms
					0, // minDistance in meters
					locListener);
			
		}
	
	public void startLocation(){
		locMgr.removeUpdates(locListener);
	
		Intent intent = new Intent(this, LocationSearch.class);
		intent.putExtra("Lat", lat);
		intent.putExtra("Lng", lng);
		startActivity(intent);
	}
	
}
