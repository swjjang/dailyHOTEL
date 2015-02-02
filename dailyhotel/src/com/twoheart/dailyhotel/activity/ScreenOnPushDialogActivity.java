package com.twoheart.dailyhotel.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
/**
 * 
 * 푸시가 왔는데 현재 단말이 켜진 상태일 경우 인텐트하는 다이얼로그형 액티비티
 * @author jangjunho
 *
 */
public class ScreenOnPushDialogActivity extends Activity implements OnClickListener, Constants{

	private TextView tvMsg;
	private ImageView btnClose;
	private TextView tvTitle;
	private MixpanelAPI mMixpanel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_screen_on_push_dialog);
		mMixpanel = MixpanelAPI.getInstance(getApplicationContext(), "791b366dadafcd37803f6cd7d8358373");

		String msg = getIntent().getStringExtra(NAME_INTENT_EXTRA_DATA_PUSH_MSG);
		int type = getIntent().getIntExtra(NAME_INTENT_EXTRA_DATA_PUSH_TYPE, -1);
		String hotelName = getIntent().getStringExtra("hotelName");
		String paidPrice = getIntent().getStringExtra("paidPrice");
		
//		msg = msg.replace("]", "]\n");
		
		tvMsg = (TextView)findViewById(R.id.tv_screen_on_push_dialog_msg);
		
		if (type == PUSH_TYPE_NOTICE) {
			tvMsg.setText(msg);
		} else if (type == PUSH_TYPE_ACCOUNT_COMPLETE) {
			SharedPreferences pref = this.getSharedPreferences(NAME_DAILYHOTEL_SHARED_PREFERENCE, Context.MODE_PRIVATE);
			SimpleDateFormat dateFormat = new  SimpleDateFormat("yyMMDDHHmmss", java.util.Locale.getDefault());
			Date date = new Date();
			String strDate = dateFormat.format(date);
			int userIdx = Integer.parseInt(pref.getString(KEY_PREFERENCE_USER_IDX, "0"));
			String userIdxStr = String.format("%07d", userIdx);
			String transId = strDate + userIdxStr;
			
			RenewalGaManager.getInstance(getApplicationContext()).
			purchaseComplete(
					transId, 
					hotelName, 
					"unidentified", 
					Double.parseDouble(paidPrice)
					);
			
			SimpleDateFormat dateFormat2 = new  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
			strDate = dateFormat2.format(date);
			
			mMixpanel.getPeople().identify(userIdxStr);
			
			JSONObject properties = new JSONObject();
			try {
				properties.put("hotelName", hotelName);
				properties.put("datetime", strDate); // 거래 시간 = 연-월-일T시:분:초
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			mMixpanel.getPeople().trackCharge(Double.parseDouble(paidPrice), properties); // price = 결제 금액
			
			JSONObject props = new JSONObject();
			try {
				props.put("hotelName", hotelName);
				props.put("price", Double.parseDouble(paidPrice));
				props.put("datetime", strDate);
				props.put("userId", userIdxStr);
				props.put("tranId", transId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			mMixpanel.track("transaction", props);
			
			int index = msg.lastIndexOf("]");
			StringBuffer sb = new StringBuffer(msg); 
			String result = sb.replace( index, index+1, "]\n" ).toString();
			
			tvMsg.setText(result);
		}
//		int index = msg.lastIndexOf("]");
//		StringBuffer sb = new StringBuffer(msg); 
//
//		String result = sb.replace( index, index+1, "]\n" ).toString();
		
		
//		tvMsg.setText(result);

		btnClose = (ImageView)findViewById(R.id.iv_screen_on_push_dialog_close);
		btnClose.setOnClickListener(this);

		String title = null;
		switch (type) {
			case PUSH_TYPE_NOTICE:
				title = getString(R.string.dialog_notice2);
				break;
			case PUSH_TYPE_ACCOUNT_COMPLETE:
				title = getString(R.string.dialog_title_payment);
				break;
		}
		
		tvTitle = (TextView)findViewById(R.id.tv_screen_on_push_dialog_title);
		tvTitle.setText(title);
	}

	@Override
	public void onClick(View v) { 
		android.util.Log.e("ACCOUNT_COMPLETE_SCREEN_ON_DIALOG", "closed");
		finish(); 
	}
	
	@Override
	protected void onDestroy() {
		mMixpanel.flush();
		super.onDestroy();
	}
}
