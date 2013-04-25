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
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
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
import android.widget.Toast;

public class ImageSearchRatinMalkud extends Activity {
	
	Uri pic;
	Button click;
	Button use;
	Button re;
	ImageView img;
	byte [] frames;
	YuvImage yuv;
	Bitmap bmp;
	int width;
	int height;
	int bpp = 12;
		
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

//	private Preview preview;

	private ImageButton snapButton;

	private ImageButton btnShowList;

	private ImageButton tutoButton;

	static IQE iqe;

	private AlertDialog ad;
	
	private QueryProgressDialog pd;

	private AtomicBoolean activityRunning = new AtomicBoolean(false);
	
//	private Preview.FrameReceiver mreceiver;
	/********************************************************************************************/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_search_ratin_malkud);
		
		img = (ImageView)findViewById(R.id.display);
		img.setVisibility(View.INVISIBLE);
		
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
	
	public void takePicture(View view){
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==0 && resultCode==Activity.RESULT_OK){
			// Now we know that our pic URI refers to the image just taken
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
		frames = new byte[((width * height * bpp) / 8)];
		
		//b is the Bitmap    
		int bytes = bmp.getWidth()*bmp.getHeight()*4; //calculate how many bytes our image consists of. Use a different value than 4 if you don't use 32bit images.

		ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
		bmp.copyPixelsToBuffer(buffer); //Move the byte data to the buffer

		byte[] array = buffer.array(); //Get the underlying array containing the data.
		
		yuv = new YuvImage(array, ImageFormat.NV21, width, height, null);
		iqe.sendMessageAtFrontOfQueue(iqe.obtainMessage(IQE.CMD_DECODE, IQE.snap, 0, yuv));
	//	processImageSnap(yuv);
	}
	
	/***********************************************************************************************/
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
			//	//pd.dismiss();
				//pd.pdDismissed();
			//	unfreezePreview();
			//	iqe.goScan();
			//	snapButton
			//	.setImageResource(R.drawable.ic_camera);
		}
	};
		handler.postDelayed(postponedToastAction, REMOTE_MATCH_MAX_DURATION);

		if (DEBUG) Log.d(TAG," snap decode message");
	iqe.sendMessageAtFrontOfQueue(iqe.obtainMessage(IQE.CMD_DECODE, IQE.snap, 0, yuv));
}
	
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

			switch (callType) {
			
			case (IQE.scan):
			//	createHistoryItem(queryId, path, IQE.scan);
				break;
			
			case (IQE.snap):
				createHistoryItem(queryId, path, IQE.snap);
				lastPostedQid = queryId;
				Log.d(TAG, "before pd");
			/*	if (SEARCH_OBJECT_REMOTE) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							
							pd.setMessage("Searching...");
							Log.d(TAG, "after pd");
						}
					});
				} */
				break;
			}
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
			
			// if the it is a barcode
			if (engine == IQE.barcode) {
				handler.post(new Runnable() {
					public void run() {
						if (callType==IQE.snap) {
							handler.removeCallbacks(postponedToastAction);
						}
						processSearchResult(qId, oNm, null, IQE.scan);
					}
				});
				return;
			}
			
			//if it is a local match
			if (engine == IQE.local){
				handler.post(new Runnable() {
					public void run() {
						if (callType==IQE.snap) {
							handler.removeCallbacks(postponedToastAction);
						}
						processSearchResult(qId, oNm, null, IQE.scan);
					}
				});
				return;
			}
			
			//if it is a remote match
			else {
			//	if (queryId.equals(lastPostedQid)) {
					handler.removeCallbacks(postponedToastAction);
			//	}
				Uri uri = null;
				// match's Metadata set as URI.
				if (objMeta != null) {

					try {
						uri = Uri.parse(objMeta);
					} catch (Exception e1) {
						uri = null;
					}
				}
				// if no Metadata : match's name set as URI.
				if (uri == null) {

					if (objName != null) {

						try {
							uri = Uri.parse(objName);
						} catch (Exception e1) {
							uri = null;
						}
					}
				}
				final Uri fUri = uri;
				handler.post(new Runnable() {
					@Override
					public void run() {
						// process and display the results
						processSearchResult(qId, oNm, fUri, callType);
					}
				});
			}
		}

		/**
		 * When no match are found, or exception occurs.
		 * 
		 * 
		 */

		@Override
		public void onNoResult(int callType, Exception e, File imgFile) {
			
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
						//	pd.dismiss();
						//	pd.pdDismissed();
						//	unfreezePreview();
						}
					});
				} else {
					Log.e(TAG, "Unable to complete search", e);
				}
				return;
			}
			// if just nothing found
			switch (callType) {
				
			case (IQE.scan):
			//	startScanning();
				break;

			case (IQE.snap):
				//displayResult(null, IQE.snap, imgFile);
				break;
			}
		}
	};
	
	private void processSearchResult(String searchId, String label, Uri uri,
			final int callType) {
/*
		HistoryItem item = null;
	
		for (Iterator<HistoryItem> iter = history.iterator();;) {
			if (!iter.hasNext()) {
				break;
			}
			item = iter.next();
			if (searchId.equals(item.id)) {
				if (DEBUG) Log.d(TAG, "" + item.id);
				if (DEBUG) Log.d(TAG, "" + searchId);
				item.label = label;
				item.uri = uri;
				break;
			} else {
				item = null;
			}
		} 

		if (item == null) {
			if (DEBUG) Log.w(TAG, "No entry found for qid: " + searchId);
		//	startScanning();
			return;
		}
		historyListAdapter.notifyDataSetChanged();
		
		if (!activityRunning.get()) {
			return;
		}
		*/
		
//		does not display the result if the query isn't the last posted
//		if (!searchId.equals(lastPostedQid)) {
//			return;
//		}
		
		
		Boolean validUri = false;
		// Try to display the resources from the Uri. //
		//********************************************//
		if (uri == null) {
			UrlValidator urlValidator = new UrlValidator();
			if (urlValidator.isValid(label)) {
				validUri = true;
				Uri Buri = Uri.parse(label);
				this.startActivity(new Intent(Intent.ACTION_VIEW, Buri));	
			}

		} else {
			validUri = processMetaUri(this, uri);
		}
		//*********************************************//
		/////////////////////////////////////////////////

		// If no Metadata available, just display the match found and the label.

	//	if (!validUri) {
		//	displayResult(item, callType, null);
	//	}
	}
	
	static boolean processMetaUri(Activity a, Uri uri) {

		if (uri != null && uri.toString().length() > 0) {
			try {
				a.startActivity(new Intent(Intent.ACTION_VIEW, uri));
				return true;
			} catch (ActivityNotFoundException e) {
				Log.w(TAG,
						"Unable to open view for this meta: " + uri.toString(),
						e);
				return false;
			}
		} else {
			return false;
		}
	}
	
	private String lastPostedQid = null;
	
	private void createHistoryItem(String qid, String path, int callType) {

		Bitmap thumb = null;

		switch (callType) {
		/*
		case (IQE.scan):
			try {
				InputStream is = this.getAssets().open(
						"iqedata/" + path + "/" + path + ".jpg");				
				Bitmap origBmp = BitmapFactory.decodeStream(is);
				thumb = transformBitmapToThumb(origBmp);
			} catch (IOException e) {
				thumb = null;
			}
			break;
			*/
		case (IQE.snap):
			
		//	Bitmap origBmp = BitmapFactory.decodeFile(path);
		//	thumb = transformBitmapToThumb(origBmp);
		//	thumb = Utils.rotateBitmap(thumb, preview.getAngle());
			
		}

	//	HistoryItem item = new HistoryItem();
	//	item.id = lastPostedQid = qid;
	//	item.label = "Searching...";
	//	item.uri = null;
	//	item.thumb = thumb;

	//	if (history.size() > MAX_ITEM_HISTORY)
	//		history.remove(0);

	//	history.add(item);

		if (DEBUG) Log.d(TAG, "History item created for qid: " + qid);
	}

	/**********************************************************************************************/
}
