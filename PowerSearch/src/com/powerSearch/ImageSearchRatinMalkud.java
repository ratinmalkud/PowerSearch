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
import android.content.Intent;
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

public class ImageSearchRatinMalkud extends Activity {
	
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
	 * Settings.
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
		
		searching = (TextView)findViewById(R.id.searching);
		searching.setVisibility(View.VISIBLE);
		
		iqe = new IQE(this, SEARCH_OBJECT_REMOTE,SEARCH_OBJECT_LOCAL,
				SEARCH_OBJECT_BARCODE, onResultCallback, KEY, SECRET);
		
		ContentValues values = new ContentValues();
		values.put(Media.TITLE, "My demo image");
		values.put(Media.DESCRIPTION, "Image Captured by Camera via an Intent");
		pic = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, pic);
		startActivityForResult(intent, 0);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_search_ratin_malkud, menu);
		return true;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==0 && resultCode==Activity.RESULT_OK){
			try {
				bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pic);
				startSearch();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			Intent intent = new Intent(this, HomeScreen.class);
			startActivity(intent);
		}
	}
		
	public void startSearch(){
		width = bmp.getWidth();
		height = bmp.getHeight();
		
		int [] argb = new int[width * height];
	    bmp.getPixels(argb, 0, width, 0, 0, width, height);
	    byte [] yuv = new byte[width*height*3/2];
        frames = encodeYUV420SP(yuv, argb, width, height);
		bmp.recycle();
        
        yuvs = new YuvImage(yuv, ImageFormat.NV21, width, height, null);
		
		iqe.sendMessageAtFrontOfQueue(iqe.obtainMessage(IQE.CMD_DECODE, IQE.snap, 0, yuvs));
	}
		
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
		
	private OnResultCallback onResultCallback = new OnResultCallback() {

		/**
		 * Called whenever a query is done (In the demo app, we call it on every
		 * snap queries and on Successful scan queries)
		 * 
		 * @param queryId
		 *            A {@link String}, the unique Id of the query.
		 * @param path
		 *            A {@link String}, the path of the picture associated with
		 *            the query.
		 * @param callType
		 *            An {@link Integer}, defines if it's a snap or a scan call.
		 */

		@Override
		public void onQueryIdAssigned(String queryId, String path, int callType) {
			lastPostedQid = queryId;
		}

		/**
		 * Handle the results.
		 * 
		 * @param queryId
		 *            A {@link String}, the unique Id of the query.
		 * @param objId
		 *            A {@link String}, the unique Id identifying the object on
		 *            our server. * @param objId A {@link String}, the object
		 *            label. * @param objId A {@link String}, the object
		 *            metadata. * @param objId A {@link Integer}, determines
		 *            which engine made the match (barcode, local, remote).
		 * @param callType
		 *            An {@link Integer}, defines if it's a snap or a scan call.
		 */

		@Override
		public void onResult(String queryId, String objId, String objName,
				String objMeta, int engine, final int callType) {
			
			final String qId = queryId;
			final String oNm = objName;
			result = oNm;
			
			showResult();
		//	goHome();
		//	Toast.makeText(getBaseContext(),oNm,Toast.LENGTH_LONG).show();
		}
		
		

		/**
		 * When no match are found, or exception occurs.
		 * 
		 * 
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
					//Have a dialog here that will say search not successful.
				}
				return;
			}
		}
	};

	public void onResume() {
		Log.d(TAG,"in On resume");
		super.onResume();
		activityRunning.set(true);
		iqe.resume();
		//goHome();
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
		super.onRestart();
	//	goHome();
	}

	@Override
	public void onDestroy() {
		if (DEBUG) Log.d(TAG,"onDestroy");
		iqe.destroy();
		goHome();
		super.onDestroy();
	}
	
	public void startAgain(){
		Intent intent = new Intent(this, ImageSearchRatinMalkud.class);
		startActivity(intent);
	}
	
	public void showResult(){
		searching.setVisibility(View.INVISIBLE);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ImageSearchRatinMalkud.this);
		builder.setCancelable(false);
		builder.setTitle("Image Recognized as...");
		builder.setMessage(result);
		builder.setInverseBackgroundForced(true);
		builder.setPositiveButton("Google it !", new DialogInterface.OnClickListener() {
		
			@Override
		public void onClick(DialogInterface dialog, int id){
			//perform text search here
				Intent search = new Intent(Intent.ACTION_WEB_SEARCH);  
				search.putExtra(SearchManager.QUERY, result);  
				startActivity(search);
				
			//	goHome();
		}
		})
		.setNegativeButton("Retake image", new DialogInterface.OnClickListener() {
					
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				startAgain();
			}
		});
	
			
		AlertDialog myAlert = builder.create();
		myAlert.show();
	}
	
	public void goHome(){
		Intent intent = new Intent(this, HomeScreen.class);
		startActivity(intent);
	}
}