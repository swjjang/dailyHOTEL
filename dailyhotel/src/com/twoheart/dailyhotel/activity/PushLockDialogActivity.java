package com.twoheart.dailyhotel.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.GlobalFont;
import com.twoheart.dailyhotel.util.WakeLock;

/**
 * 화면이 OFF 상태일때 GCM 메시지를 받는 경우 카카오톡 처럼 푸시 다이얼로그가 팝업됨.
 * @author jangjunho
 *
 */
public class PushLockDialogActivity extends Activity implements OnClickListener,Constants{
	
	private Button btnOkButton;
	private Button btnCancelButton;
	private TextView tvMsg;
//	private TextView tvTitle;
	
	private String mMsg;
	private int mType;
	//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_push_lock_dialog_gcm);
		
		mMsg = getIntent().getStringExtra(NAME_INTENT_EXTRA_DATA_PUSH_MSG);
		mType = getIntent().getIntExtra(NAME_INTENT_EXTRA_DATA_PUSH_TYPE, -1);
		
//		String title = null;
//		if (mType == PUSH_TYPE_NOTICE) {
//			title = "알림";
//		} else if (mType == PUSH_TYPE_ACCOUNT_COMPLETE) {
//			title = mMsg.substring(0, mMsg.indexOf("]")+1);
//		}
		
		String content = mMsg.substring(mMsg.indexOf("]")+1);
		
//		tvTitle = (TextView) findViewById(R.id.tv_push_lock_dialog_title);
//		tvTitle.setText(title);
		
		tvMsg = (TextView) findViewById(R.id.tv_push_lock_dialog_msg);
		tvMsg.setText(content);
		
		btnOkButton = (Button) findViewById(R.id.btn_push_lock_dialog_show);
		btnCancelButton = (Button) findViewById(R.id.btn_push_lock_dialog_close);
		
		btnOkButton.setOnClickListener(this);
		btnCancelButton.setOnClickListener(this);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
		        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
		        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		
		WakeLock.releaseWakeLock();
		
		GlobalFont.apply((ViewGroup) getWindow().getDecorView());
		
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == btnOkButton.getId()) {
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.setClass(this, MainActivity.class);
	        intent.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_TYPE, mType); // 메인엑티비티 -> 예약확인리스트 -> 최신 예약 클릭, 
			
			startActivity(intent);
			finish();
			
		} else if(v.getId() == btnCancelButton.getId()) {
			finish();
		}
	}
	
}
