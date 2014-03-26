package com.twoheart.dailyhotel.util.ui;

import android.app.Activity;
import android.widget.Toast;

public class CloseOnBackPressed {
	
	private long backPressedTime = 0;
	private Toast toast;
	private Activity activity;
	
	public CloseOnBackPressed(Activity activity) {
		this.activity = activity;
		
	}
	
	public boolean onBackPressed() {
		
		if (System.currentTimeMillis() <= backPressedTime + 2000) {
			toast.cancel();
			return true;
		}
		
		backPressedTime = System.currentTimeMillis();
		showGuide();
		
		return false;
		
	}
	
	private void showGuide() {
		toast = Toast.makeText(activity, "\'뒤로\' 버튼을 한 번 더 누르시면 종료합니다.", Toast.LENGTH_SHORT);
		toast.show();
	}

}
