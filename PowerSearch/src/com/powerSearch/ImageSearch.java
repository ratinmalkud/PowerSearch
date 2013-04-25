package com.powerSearch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import com.iqengines.sdk.IQE;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageSearch extends Activity {
	
	/**
	 * Account settings. You can obtain the required keys after you've signed up
	 * for visionIQ.
	 */

	// Insert your API key here (find it at iengines.com --> developer center
	// --> settings).
	static final String KEY = "89b9e8216dae43f581984f9b459608be";
	// Insert your secret key here (find it at iengines.com --> developer center
	// --> settings).
	static final String SECRET = "e3e30671c89c43dbb887187b7b67f2a1";

	/**
	 * Settings.
	 */
	
	IQE iqe;
	YuvImage yuv;
	Camera cam;
	byte [] frames;
	
	// Activates the local search.
	static final boolean SEARCH_OBJECT_LOCAL = false;
	
	// Activates the barcode scanning
	static boolean SEARCH_OBJECT_BARCODE = true;
	
	// Activates the scan search.
	static boolean SEARCH_OBJECT_SCAN = true;
	
	// Activates the snap search
	static boolean SEARCH_OBJECT_SNAP = true;
	
	// Activates the remote search.
	static final boolean SEARCH_OBJECT_REMOTE = true;

	// Maximum duration of a remote search.
	static final long REMOTE_MATCH_MAX_DURATION = 10000;
	


	
	static final int MAX_ITEM_HISTORY = 20;

	static final boolean PROCESS_ASYNC = true;

	static final boolean DEBUG = true;
	
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	
	private Uri fileUri;
	
	public static final int MEDIA_TYPE_IMAGE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_search);
		
		 // create Intent to take a picture and return control to the calling application
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
	    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	    
	}
		
	public void goHomeImage(View view){
		Intent intent = new Intent(this, HomeScreen.class);
		startActivity(intent);
	}
	
	public void imageSearch(View view){
		Intent search = new Intent(Intent.ACTION_VIEW, Uri.parse("http://images.google.com/search?q=cars"));
		//String videoUrl = "http://someurl/video.mp4";
		//Intent i = new Intent(Intent.ACTION_VIEW);  
		//search.setDataAndType(Uri.parse("http://images.google.com"),"image/jpg");  
		//startActivity(search);
		search.putExtra(SearchManager.QUERY, "peacock");  
		startActivity(search); 
	}
	
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}
	
	private static File getOutputMediaFile(int type){
	  
	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "ImageSearch");
	   
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("ImageSearch", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    File mediaFile;
	        if (type == MEDIA_TYPE_IMAGE){
		        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
		        "image_name.jpg");
	    } else {
	        return null;
	    }
	    return mediaFile;
	}

	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
<<<<<<< HEAD
	        
=======
	            // Image captured and saved to fileUri specified in the Intent
	           // Toast.makeText(this, "Image saved to:\n" +data.getData(), Toast.LENGTH_LONG).show();
	        	try {
					Bitmap img = Media.getBitmap(getContentResolver(), fileUri );
					frames = new byte[((img.getWidth() * img.getHeight() * 12) / 8)];
					yuv = new YuvImage(frames, ImageFormat.NV21, img.getWidth(), img.getHeight(), null);
					Intent intent = new Intent(this, ToTest.class);
					intent.putExtra("add", fileUri);
					startActivity(intent);
					//	processImageSnap(yuv);
					
					
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
>>>>>>> branch 'master' of ssh://git@github.com/ratinmalkud/PowerSearch.git
	        } else if (resultCode == RESULT_CANCELED) {
	           
	        } else {
	           
	        }
	    }
	}
	
	private Runnable postponedToastAction;
	private void processImageSnap(final YuvImage yuv) {

		postponedToastAction = new Runnable() {
			public void run() {
				if (SEARCH_OBJECT_REMOTE) {
				Toast.makeText(
						getBaseContext(),
						"This may take a minute... We will notify you when your photo is recognized.",
						Toast.LENGTH_LONG).show();
				}
		//		pd.dismiss();
		//		pd.pdDismissed();
		//		unfreezePreview();
				iqe.goScan();
		//		snapButton
		//		.setImageResource(R.drawable.ic_camera);
			}
		};
		//handler.postDelayed(postponedToastAction, REMOTE_MATCH_MAX_DURATION);

	//	if (DEBUG) Log.d(TAG," snap decode message");
	iqe.sendMessageAtFrontOfQueue(iqe.obtainMessage(IQE.CMD_DECODE, IQE.snap, 0, yuv));
}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_search, menu);
		return true;
	}


}
