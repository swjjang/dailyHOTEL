package com.twoheart.dailyhotel.util.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.twoheart.dailyhotel.R;

public class LoadingDialog {
	
	public static Dialog mLoadingDialog = null;
	
	public static void showLoading(final Activity activity) {
		if (mLoadingDialog == null) {
			mLoadingDialog = new Dialog(activity, R.style.TransDialog);
			ProgressBar pb = new ProgressBar(activity);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLoadingDialog.addContentView(pb, params);
			mLoadingDialog.setCancelable(true);
		}
		mLoadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				activity.onBackPressed();
				hideLoading();				
			}
		});
		mLoadingDialog.show();
	}
	
	public static void hideLoading() {
		if(mLoadingDialog != null) {
			mLoadingDialog.dismiss();
			mLoadingDialog = null;
		}
	}
}
