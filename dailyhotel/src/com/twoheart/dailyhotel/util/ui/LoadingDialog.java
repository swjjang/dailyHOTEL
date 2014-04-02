package com.twoheart.dailyhotel.util.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.twoheart.dailyhotel.R;

public class LoadingDialog {

	private static final int LOADING_DELAY = 600;
	private static final int DISMISS_DELAY = 100;
	private static Dialog loadingDialog = null;

	public static void showLoading(final Activity activity) {
		if (loadingDialog == null) {
			loadingDialog = new Dialog(activity, R.style.TransDialog);
			ProgressBar pb = new ProgressBar(activity);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			loadingDialog.addContentView(pb, params);
			loadingDialog.setCancelable(true);
			loadingDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							Handler handler = new Handler();
							handler.postDelayed(new Runnable() {

								@Override
								public void run() {
									activity.onBackPressed();

								}
							}, DISMISS_DELAY);

							hideLoading();
						}
					});
		}

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (loadingDialog != null)
					loadingDialog.show();

			}
		}, LOADING_DELAY);

	}

	public static void hideLoading() {
		if (loadingDialog != null) {
			if (loadingDialog.isShowing())
				loadingDialog.dismiss();
			loadingDialog = null;
		}
	}
}
