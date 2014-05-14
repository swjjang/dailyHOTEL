/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * CloseOnBackPressed
 * 
 * Android 디바이스의 Back 버튼을 이용한 임의의 어플리케이션 인스턴스 종
 * 료를 방지하기 위한 클래스이다. 이 클래스를 가지는 Activity에서는 Back
 * 버튼을 연속 두 번 눌러야지만 종료가 된다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel.util.ui;

import android.widget.Toast;

import com.twoheart.dailyhotel.MainActivity;

public class CloseOnBackPressed {
	
	private long backPressedTime = 0;
	private MainActivity mActivity;
	
	public CloseOnBackPressed(MainActivity activity) {
		mActivity = activity;
		
	}
	
	public boolean onBackPressed() {
		
		if (System.currentTimeMillis() <= backPressedTime + 2000) {
			mActivity.mToast.cancel();
			return true;
		}
		
		backPressedTime = System.currentTimeMillis();
		showGuide();
		
		return false;
		
	}
	
	private void showGuide() {
		mActivity.showToast("\'뒤로\' 버튼을 한 번 더 누르시면 앱이 종료됩니다", Toast.LENGTH_SHORT, true);
		
	}

}
