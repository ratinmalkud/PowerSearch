package com.powerSearch;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Menu;
import android.widget.ImageView;

public class ToTest extends Activity {
	
	ImageView img;
	Uri uri;
	Bitmap bmp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_to_test);
		
		img = (ImageView)findViewById(R.id.display);
		
		Intent intent = getIntent();
		uri = intent.getData();
		try {
			bmp = Media.getBitmap(getContentResolver(), uri);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		img.setImageURI(uri);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.to_test, menu);
		return true;
	}

}
