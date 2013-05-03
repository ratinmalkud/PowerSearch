package com.powerSearch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.validator.routines.UrlValidator;


import com.iqengines.sdk.IQE;
import com.iqengines.sdk.Utils;
import com.iqengines.sdk.IQE.OnResultCallback;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ImageSearchRatinMalkud extends Activity implements OnCancelListener{
	
	Uri pic;
	Button click;
	Button use;
	Button re;
	ImageView img;
	byte [] frames;
	YuvImage yuvs;
	Bitmap bmp;
	int width;
	int height;
	int bpp = 12;
	String result;
	TextView searching;
	
	static final int CAMERA_REQUEST = 0;
	static final int GALLERY_REQUEST = 1;
	static boolean cam;
	OnCancelListener listen;
	/*********************************************************************************************/
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
	 * Settings. We are only using the remote search option provided by the IQEngine library.
	 */
	
	// Activates the local search.
	static final boolean SEARCH_OBJECT_LOCAL = false;
	
	// Activates the barcode scanning
	static boolean SEARCH_OBJECT_BARCODE = false;
	
	// Activates the scan search.
	static boolean SEARCH_OBJECT_SCAN = false;
	
	// Activates the snap search
	static boolean SEARCH_OBJECT_SNAP = true;
	
	// Activates the remote search.
	static final boolean SEARCH_OBJECT_REMOTE = true;

	// Maximum duration of a remote search.
	static final long REMOTE_MATCH_MAX_DURATION = 10000;
	
	//This is used to let the app know that a search has been performed. Needed for correct back operation.
	static boolean GoogleSearchFlag = false;


	
	static final int MAX_ITEM_HISTORY = 20;

	static final boolean PROCESS_ASYNC = true;

	static final boolean DEBUG = true;

	private static final String TAG = "------------------------";

	private Handler handler;

	static IQE iqe;
	
	private String lastPostedQid = null;

	private AtomicBoolean activityRunning = new AtomicBoolean(false);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_search_ratin_malkud);
		
		Intent gotHereFrom = getIntent();
		cam = gotHereFrom.getBooleanExtra("Camera", HomeScreen.cam);
		
		if(cam){
			ContentValues values = new ContentValues();
			values.put(Media.TITLE, "My demo image");
			values.put(Media.DESCRIPTION, "Image Captured by Camera via an Intent");
			pic = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, pic);
			startActivityForResult(intent, CAMERA_REQUEST);
		}
		else{
			Intent galleryIntent = new Intent(Intent.ACTION_PICK);
			galleryIntent.setType("image/*");
			startActivityForResult(galleryIntent,GALLERY_REQUEST);
		}
		
		searching = (TextView)findViewById(R.id.searching);
		searching.setVisibility(View.VISIBLE);
		
		/*
		 *  Start the iqe object. This is used for all the image processing done by iqengine.
		 */
		
		iqe = new IQE(this, SEARCH_OBJECT_REMOTE,SEARCH_OBJECT_LOCAL,
				SEARCH_OBJECT_BARCODE, onResultCallback, KEY, SECRET);
		
		GoogleSearchFlag=false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_search_ratin_malkud, menu);
		return true;
	}
	
	/*
	 * This method is called when the user takes an image and chooses to perform search.
	 * The image is retrieved as a bitmap image and then translated to a YUV image.
	 * This YUV image is passed to the iq-engine sdk.
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( resultCode == Activity.RESULT_OK){
			if(requestCode == CAMERA_REQUEST){
			try {
				bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pic);
				startSearch();
			} catch (FileNotFoundException e) {
				Toast.makeText(getApplicationContext(),"Image not found, retake picture", Toast.LENGTH_LONG).show();
				Intent restart = new Intent(this, ImageSearchRatinMalkud.class);
				startActivity(restart);
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(),"I/O error, retake picture", Toast.LENGTH_LONG).show();
				Intent restart = new Intent(this, ImageSearchRatinMalkud.class);
				startActivity(restart);
			}
		  }
			if(requestCode == GALLERY_REQUEST){
				 Uri imageSelected = data.getData();
		            InputStream imageStream = null;
					try {
						imageStream = getContentResolver().openInputStream(imageSelected);
						 bmp = BitmapFactory.decodeStream(imageStream);
				            startSearch();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}      
			}
		}
		else{
			Intent intent = new Intent(this, HomeScreen.class);
			startActivity(intent);
		}
	}
		
	/*
	 * This method is called when the search is initiated by clicking the tick mark in the camera activity.
	 * It converts the image into a yuv image. The yuv image is used by the IQEngin sdk for carrying 
	 * out the image search. 
	 */
	
	public void startSearch(){
		width = bmp.getWidth();
		height = bmp.getHeight();
		
		int [] argb = new int[width * height];
	    bmp.getPixels(argb, 0, width, 0, 0, width, height);
	    byte [] yuv = new byte[width*height*3/2];
    /*
     *  Convert jpg to yuv image
     */
	    frames = encodeYUV420SP(yuv, argb, width, height);
		bmp.recycle();
        
        yuvs = new YuvImage(yuv, ImageFormat.NV21, width, height, null);
		
		iqe.sendMessageAtFrontOfQueue(iqe.obtainMessage(IQE.CMD_DECODE, IQE.snap, 0, yuvs));
	}
		
	/*
	 *  Converts the bitmap to yuv image. This part of the code was obtained & modified from stack overflow.
	 *  http://stackoverflow.com/questions/5960247/convert-bitmap-array-to-yuv-ycbcr-nv21
	 */
	byte[] encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        final int frameSize = width * height;

        int yIndex = 0;
        int uvIndex = frameSize;

        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                a = (argb[index] & 0xff000000) >> 24; // a is not used obviously
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff) >> 0;

                // well known RGB to YUV algorithm
                Y = ( (  66 * R + 129 * G +  25 * B + 128) >> 8) +  16;
                U = ( ( -38 * R -  74 * G + 112 * B + 128) >> 8) + 128;
                V = ( ( 112 * R -  94 * G -  18 * B + 128) >> 8) + 128;

                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                //    pixel AND every other scanline.
                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0) { 
                    yuv420sp[uvIndex++] = (byte)((V<0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[uvIndex++] = (byte)((U<0) ? 0 : ((U > 255) ? 255 : U));
                }

                index ++;
            }
        }
        return yuv420sp;
    }
	
	private Runnable postponedToastAction;
		
	/*
	 * This method is called when the IQEngine returns with a result 
	 */
	private OnResultCallback onResultCallback = new OnResultCallback() {

		@Override
		public void onQueryIdAssigned(String queryId, String path, int callType) {
		/*
		 * Used for matching query id's
		 */
			lastPostedQid = queryId;
		}

		/*
		 * If result is found with a matching qid(query id),display it
		 */
		@Override
		public void onResult(String queryId, String objId, String objName,
				String objMeta, int engine, final int callType) {
			
			final String qId = queryId;
			final String oNm = objName;
			result = oNm;
			
			showResult();
		}
		
		/*
		 * If no result found
		 */
		
		@Override
		public void onNoResult(int callType, Exception e, File imgFile) {
			
			Log.d("----------------","in no result");
			// if an exception occured
			if (e != null) {
				if (e instanceof IOException) {
					Log.w(TAG, "Server call failed", e);
					handler.post(new Runnable() {
						@Override
						public void run() {
							handler.removeCallbacks(postponedToastAction);
							Toast.makeText(
									getBaseContext(),
									"Unable to connect to the server. "
											+ "Check your intenet connection.",
									Toast.LENGTH_LONG).show();
						}
					});
				} else {
					Log.e(TAG, "Unable to complete search", e);
					Toast.makeText(getApplicationContext(), "Image search failed", Toast.LENGTH_LONG).show();
				}
				return;
			}
		}
	};

	/*
	 * iqe.resume internally starts the receiver thread. This thread listens to the server for a response.
	 */
	public void onResume() {
		Log.d(TAG,"in On resume");
		super.onResume();
		activityRunning.set(true);
		iqe.resume();
	}
	
	@Override
	public void onPause() {
		if (DEBUG) Log.d(TAG, "onPause");
		iqe.pause();
		activityRunning.set(false);
		super.onPause();
	}
	
	@Override
	protected void onRestart(){
		Log.d(TAG,"In on restart");
		super.onRestart();
		if(GoogleSearchFlag){
			Log.d(TAG,"gohome in restart");
			goHome();
		}
	}

	@Override
	public void onDestroy() {
		if (DEBUG) Log.d(TAG,"onDestroy");
		iqe.destroy();
		super.onDestroy();
	}
	
	public void startAgain(){
		Log.d(TAG,"Start again");
		Intent intent = new Intent(this, ImageSearchRatinMalkud.class);
		startActivity(intent);
	}
	
	
	/*
	 * Display the result in a dialog box. The user can select whether to carry out a further 
	 * google search.
	 */
	public void showResult(){
		searching.setVisibility(View.INVISIBLE);
		String sub;
		
		if(cam){
			sub = "Retake image";
		}
		else{
			sub = "Return to gallery";
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ImageSearchRatinMalkud.this);
		builder.setCancelable(true);
		builder.setTitle("Image Recognized as...");
		builder.setMessage(result);
		builder.setInverseBackgroundForced(true);
		builder.setPositiveButton("Google it !", new DialogInterface.OnClickListener() {
		
			@Override
		public void onClick(DialogInterface dialog, int id){
				Intent search = new Intent(Intent.ACTION_WEB_SEARCH);  
				search.putExtra(SearchManager.QUERY, result);  
				startActivity(search);
				GoogleSearchFlag=true;
			}
		})
		.setNegativeButton(sub, new DialogInterface.OnClickListener() {
					
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startAgain();
			}
		})
	//	.setOnCancelListener(listen);
		;
		
	
		AlertDialog myAlert = builder.create();
		myAlert.show();
	}
/*	
	@Override
	public void onCancel(DialogInterface dialog) {
		
		Intent intent = new Intent(this, HomeScreen.class);
		startActivity(intent);
	}		
	*/
	/*
	 * Return to home screen when the home button is clicked.
	 */
	public void goHome(){
		Intent intent = new Intent(this, HomeScreen.class);
		startActivity(intent);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		goHome();
		
	}
}
