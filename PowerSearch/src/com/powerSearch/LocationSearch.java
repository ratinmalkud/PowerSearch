package com.powerSearch;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;

import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class LocationSearch extends Activity {
	/*
	 * Obtains the latitude and longitude from the intent.
	 * Creates a CameraPosition object and sets it to centre on the obtained latitude and longitiude.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_search);
	
		Intent intent = getIntent();
		double lat = intent.getDoubleExtra("Lat", 37.4);
		double lng = intent.getDoubleExtra("Lng", -122.1);
		GoogleMap gm = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		gm.setMyLocationEnabled(true);
	
		/*
		 * Display the mapfragment with the correct position
		 */
		
		LatLng position = new LatLng(lat, lng);
				CameraPosition cameraPosition = new CameraPosition.Builder()
		    .target(position)      // Sets the center of the map to Mountain View
		    .zoom(17)                   // Sets the zoom
		    .bearing(90)                // Sets the orientation of the camera to east
		    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
		    .build();                   // Creates a CameraPosition from the builder
		gm.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_search, menu);
		return true;
	}
	
}
