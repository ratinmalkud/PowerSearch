package com.powerSearch;

import android.app.Activity;
import android.app.ProgressDialog;

class QueryProgressDialog extends ProgressDialog {

		private boolean isShowing;
		
		public QueryProgressDialog(Activity activity) {
			super(activity);
			this.isShowing=false;
		}
		
		public void pdShowing(){
			this.isShowing=true;
		}
		
		public void pdDismissed(){
			this.isShowing=false;
		}
		
		public boolean isShowing(){
			return isShowing;
		}

	}
	