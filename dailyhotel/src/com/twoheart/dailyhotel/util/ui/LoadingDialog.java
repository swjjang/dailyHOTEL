/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * LoadingDialog
 * 
 * Activity 전체를 핸들하는 로딩 다이얼로그 창이다. 로딩 작업을 수행하는
 * 동안 로딩 다이얼로그 창을 띄우며 취소시 Activity의 onBackPressed
 * 메서드도 같이 수행된다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel.util.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.twoheart.dailyhotel.R;

public class LoadingDialog {
	private static Dialog loadingDialog = null;

	public static synchronized void showLoading(final Activity activity) {
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
							hideLoading();
							activity.onBackPressed();

						}
					});
			loadingDialog.show();

		} else {
			hideLoading();
			showLoading(activity);
		}
	}

	public static void hideLoading() {
		if (loadingDialog != null) {
			loadingDialog.dismiss();
			loadingDialog = null;
		}
	}
}
