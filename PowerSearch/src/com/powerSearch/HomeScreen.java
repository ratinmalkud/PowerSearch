package com.powerSearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class HomeScreen extends Activity {

		
	double lat, lng;
	LocationManager locMgr;
	LocationListener locListener;
	boolean gps;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);
		gps = false;
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_screen, menu);
		return true;
	}

	/*
	 *  Start Audio Search 
	 */
	
	public void initiateAudioSearch(View view){
		Intent intent = new Intent(this, AudioSearch.class);
		startActivity(intent);
	}
	
	/*
	 *  Start Image Search
	 */
	
	public void initiateImageSearch(View view){
		Intent intent = new Intent(this, ImageSearchRatinMalkud.class);
		startActivity(intent);
	}

	/*
	 *  Start Text Search
	 */
	
	public void initiateTextSearch(View V){
		Intent i = new Intent(this, Text_screen1.class);
		startActivity(i);
	}
	
	/*
	 *  Decide whether to use GPS or network location for Location Search and determine the latitude
	 *  and longitude.
	 */
	
	public void determineProvider(View view){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this);
		builder.setCancelable(false);
		builder.setTitle("Select location provider");
		builder.setMessage("Please select one of the 2 providers below.\n(GPS location takes a while if indoors)");
		builder.setInverseBackgroundForced(true);
		builder.setPositiveButton("GPS", new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int id){
			gps = true;
			findMe();
		}
		})
		.setNegativeButton("Network Location", new DialogInterface.OnClickListener() {
					
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			//	startAgain();
				gps = false;
				findMe();
			}
		});
				
		AlertDialog myAlert = builder.create();
		myAlert.show();
	}
	
	/*
	 *  Start location search by using whichever service was selected before.
	 */
	
	public void findMe(){
				
		if(gps){
			locMgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			
			locListener = new LocationListener()
			{

				/*
				 * Get the current location
				 */
				public void onLocationChanged(Location location)
				{
				if (location != null)
				{
					lat = location.getLatitude();
					lng = location.getLongitude();
					
					startLocation();
				}
				}

				/*
				 * If location services are turned off, alert the user and go back to home screen
				 */
				@Override
				public void onProviderDisabled(String provider) {	
					Toast.makeText(getApplicationContext(), "Turn on network services", Toast.LENGTH_LONG).show();	
					goHome();
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
						LocationManager.GPS_PROVIDER,
						0, // minTime in ms
						0, // minDistance in meters
						locListener);
		}
		else{
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
					Toast.makeText(getApplicationContext(), "Turn on network services", Toast.LENGTH_LONG).show();	
					goHome();
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
					
	}
	
	/*
	 *  Display location on Map by stating a new activity
	 */
		
	public void startLocation(){
		locMgr.removeUpdates(locListener);
	
		Intent intent = new Intent(this, LocationSearch.class);
		intent.putExtra("Lat", lat);
		intent.putExtra("Lng", lng);
		startActivity(intent);
	}
	
	/*
	 * Goes back to homescreen, used when location services is off & user asks for location search 
	 */
	
	public void goHome(){
		Intent intent = new Intent(this, HomeScreen.class);
		startActivity(intent);
	}
	
	public void onPause() {
		Log.d("-----------", "onPause");
		super.onPause();
	}
	
	public void onDestroy(){
		super.onDestroy();
	}
	
	public void onResume(){
		super.onResume();
	}
}
