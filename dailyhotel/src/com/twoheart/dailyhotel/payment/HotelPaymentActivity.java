package com.twoheart.dailyhotel.payment;

import static com.twoheart.dailyhotel.AppConstants.CHECKIN;
import static com.twoheart.dailyhotel.AppConstants.DETAIL;
import static com.twoheart.dailyhotel.AppConstants.LOGIN;
import static com.twoheart.dailyhotel.AppConstants.PREFERENCE_AUTO_LOGIN;
import static com.twoheart.dailyhotel.AppConstants.PREFERENCE_HOTEL_DAY;
import static com.twoheart.dailyhotel.AppConstants.PREFERENCE_HOTEL_IDX;
import static com.twoheart.dailyhotel.AppConstants.PREFERENCE_HOTEL_MONTH;
import static com.twoheart.dailyhotel.AppConstants.PREFERENCE_HOTEL_NAME;
import static com.twoheart.dailyhotel.AppConstants.PREFERENCE_HOTEL_YEAR;
import static com.twoheart.dailyhotel.AppConstants.PREFERENCE_IS_LOGIN;
import static com.twoheart.dailyhotel.AppConstants.PREFERENCE_USER_ID;
import static com.twoheart.dailyhotel.AppConstants.PREFERENCE_USER_PWD;
import static com.twoheart.dailyhotel.AppConstants.RESERVE;
import static com.twoheart.dailyhotel.AppConstants.REST_URL;
import static com.twoheart.dailyhotel.AppConstants.SAVED_MONEY;
import static com.twoheart.dailyhotel.AppConstants.SHARED_PREFERENCES_NAME;
import static com.twoheart.dailyhotel.AppConstants.USER_ALIVE;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.asynctask.GeneralHttpTask;
import com.twoheart.dailyhotel.asynctask.ParameterElement;
import com.twoheart.dailyhotel.asynctask.onCompleteListener;
import com.twoheart.dailyhotel.utils.LoadingDialog;
import com.twoheart.dailyhotel.utils.Switch;

public class HotelPaymentActivity extends ActionBarActivity implements OnClickListener{
	
	private static final String TAG = "HotelPaymentActivity";
	
	private static final int HOTEL_PAYMENT_ACTIVITY = 1;
	
	private TextView tv_checkin, tv_checkout, tv_original_price, tv_credit, tv_price;
	private Button btn_payment;
	private Switch btn_on_off;
	
	private Boolean isBonus = false;	// 적립금 사용
	private Boolean isFullBonus = false;   // 모든 금액을 적립금으로 할때 
	
	private String hotel_name;
	private String year;
	private String month;
	private String day;
	private String hotel_idx;
	private String booking_idx;
	
	private String credit;
	private String original_price;
	
	private boolean isPayment = false;	// 결제 버튼 누르면 true, session listener 재사용위해
	
	private int reservCnt;		//결제 전과 결제후 cnt 비교를 통해 다시 한번 결제된지 검증
	
	private SharedPreferences prefs;
	
	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;
	
	// Jason | Google analytics
	@Override
	public void onStart() {
		super.onStart();
		HashMap<String, String> hitParameters = new HashMap<String, String>();
		hitParameters.put(Fields.HIT_TYPE, "appview");
		hitParameters.put(Fields.SCREEN_NAME, "Payment View");
		
		mGaTracker.send(hitParameters);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_hotel_payment);
		loadResource();
		
		// Google analytics
		mGaInstance = GoogleAnalytics.getInstance(this);
		mGaTracker = mGaInstance.getTracker("UA-43721645-1");
		
		prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		
		hotel_idx = prefs.getString(PREFERENCE_HOTEL_IDX, null);
		hotel_name = prefs.getString(PREFERENCE_HOTEL_NAME, null);
		year = prefs.getString(PREFERENCE_HOTEL_YEAR, null);
		month = prefs.getString(PREFERENCE_HOTEL_MONTH, null);
		day = prefs.getString(PREFERENCE_HOTEL_DAY, null);
		
		// back arrow
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setIcon(R.drawable.dh_ic_menu_back);
		Drawable myDrawable;
		Resources res = getResources();
		try {
		   myDrawable = Drawable.createFromXml(res, res.getXml(R.drawable.dh_actionbar_background));
		   getSupportActionBar().setBackgroundDrawable(myDrawable);
		} catch (Exception ex) {
		   Log.e("Error", "Exception loading drawable"); 
		}
		
		setTitle(hotel_name);
		
		LoadingDialog.showLoading(this);
		new GeneralHttpTask(sessionListener, getApplicationContext()).execute(REST_URL + USER_ALIVE);
	}

	public void loadResource() {
		tv_checkin = (TextView) findViewById(R.id.tv_hotel_payment_checkin);
		tv_checkout = (TextView) findViewById(R.id.tv_hotel_payment_checkout);
		tv_original_price = (TextView) findViewById(R.id.tv_hotel_payment_original_price);
		tv_credit = (TextView) findViewById(R.id.tv_hotel_payment_credit);
		tv_price = (TextView) findViewById(R.id.tv_hotel_payment_price);
		btn_payment = (Button) findViewById(R.id.btn_hotel_payment);
		btn_on_off = (Switch) findViewById(R.id.btn_on_off);
		btn_payment.setOnClickListener(this);
		btn_on_off.setOnClickListener(this);
	}
	
	public void dialog(String str) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	dialog.dismiss();     //닫기
		    }
		});
		alert.setMessage(str);
		alert.show();
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == btn_payment.getId()) {
			
			Log.v("Pay", "click payment button");
			
			isPayment = true;
			new GeneralHttpTask(sessionListener, getApplicationContext()).execute(REST_URL + USER_ALIVE);
			
		} else if(v.getId() == btn_on_off.getId()) {
			if(isBonus) { // 사용안함으로 변경
				
				tv_credit.setText("￦0");
				DecimalFormat comma = new DecimalFormat("###,##0");
				String str = comma.format(Integer.parseInt(original_price));
				tv_price.setText("￦" + str);
				isBonus = false;
				isFullBonus = false;
				
			} else {		// 사용함으로 변경
				DecimalFormat comma = new DecimalFormat("###,##0");
				
				String price;
				String creditStr = comma.format(Integer.parseInt(credit));
				
				if(Integer.parseInt(original_price) <= Integer.parseInt(credit)) {
					creditStr = original_price;
					price = "0";
					isFullBonus = true;
				} else {
					price = Integer.toString((Integer.parseInt(original_price) - Integer.parseInt(credit)));
					isFullBonus = false;
				}
				
				tv_credit.setText("-￦" + creditStr);
				price =  comma.format(Integer.parseInt(price));
				tv_price.setText("￦" + price);
				isBonus = true;
				
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
		super.onBackPressed();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int p_requestCode, int p_resultCode, Intent p_intentActivity) {
		
		if(p_requestCode == HOTEL_PAYMENT_ACTIVITY) {
			if(p_resultCode == RESULT_OK) {
				String result = p_intentActivity.getStringExtra("ActivityResult");
				if(result.equals("SUCCESS")) {
//					dialog("결제가 정상적으로 이루어 졌습니다");
					
					Log.d(TAG, "SUCCESS");
					
					AlertDialog.Builder alert = new AlertDialog.Builder(HotelPaymentActivity.this);
					alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
					    	dialog.dismiss();     //닫기
					    	setResult(RESULT_OK);
		    				finish();
					    }
					});
					alert.setMessage("결제가 정상적으로 이루어 졌습니다");
					alert.show();
            	} else if(result.equals("INVALID_SESSION")) {
//            		dialog("세션연결이 종료 되었습니다. 다시 시도해 주세요");
            		Log.d(TAG, "INVALID_SESSION");
            	} else if(result.equals("SOLD_OUT")) {
            		dialog("모든 객실이 판매되었습니다.\n다음에 이용해주세요.");
            		Log.d(TAG, "SOLD_OUT");
            	} else if(result.equals("PAYMENT_COMPLETE")) {
//            		new ReLoginTask(getApplicationContext()).execute();
            		LoadingDialog.showLoading(this);
            		new GeneralHttpTask(postSessionListener,getApplicationContext()).execute(REST_URL + USER_ALIVE);
            		Log.d(TAG, "PAYMENT_COMPLETE");
            	} else if(result.equals("NOT_AVAILABLE")) {
            		dialog("먼저 온 손님이 예약 중입니다.\n잠시 후 다시 시도해주세요.");
            		Log.d(TAG, "NOT_AVAILABLE");
            	} else if(result.equals("NETWORK_ERROR")) {
					dialog("네트워크 연결을 확인해 주세요");
					Log.d(TAG, "NETWORK_ERROR");
				} else if(result.equals("INVALID_DATE")) {
					Log.d(TAG, "INVALID_DATE");
				}
				
            	Log.d("result", result);
                Log.i("HotelReservation",  PaymentActivity.ACTIVITY_RESULT );
                
			} else {
				Log.i(TAG,"결과 알수 없음 (취소 또는 오류 발생)" );
			}
		}
		
		super.onActivityResult(p_requestCode, p_resultCode, p_intentActivity);
	}
	
	public void parseCreditJson(String str) {
		credit = str.trim();
		
		// checkin out data 받아옴
		new GeneralHttpTask(detailListener, getApplicationContext()).execute(REST_URL + DETAIL + hotel_idx + "/" + year + "/" + month + "/" + day);
	}
	
	public void parseLoginJson(String str) {
		try {
			JSONObject obj = new JSONObject(str);
			if ( obj.getString("login").equals("true") ) {
				// 로그인 성공
				if(isPayment) 
					new GeneralHttpTask(preCntListener, getApplicationContext()).execute(REST_URL + RESERVE);
				else			// credit 요청
					new GeneralHttpTask(creditListener, getApplicationContext()).execute(REST_URL + SAVED_MONEY);
			} else {
				// 로그인 실패
				// 메세지 출력하고 activity 종료
				Toast.makeText(getApplicationContext(), "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
				SharedPreferences.Editor ed = prefs.edit();
				ed.putBoolean(PREFERENCE_AUTO_LOGIN, false);
				ed.putBoolean(PREFERENCE_IS_LOGIN, false);
				ed.commit();
				
				isPayment = false;
				LoadingDialog.hideLoading();
				finish();
			}
		} catch (Exception e) {
			Log.d(TAG, "parseLoginJson " +  e.toString());
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void parseCheckinJson(String str) {
		try {
			JSONObject obj = new JSONObject(str);
			String checkin = obj.getString("checkin");
			String checkout = obj.getString("checkout");
			
			String in[] = checkin.split("-");
			tv_checkin.setText("20" + in[0] + ". " + in[1] + ". " + in[2] + " " + in[3] + "시");
			String out[] = checkout.split("-");
			tv_checkout.setText("20" + out[0] + ". " + out[1] + ". " + out[2] + " " + out[3] + "시");
			
			LoadingDialog.hideLoading();
			
		} catch (Exception e) {
			Log.d("parseCheckinJson", "TagDataParser" + "->" + e.toString());
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			LoadingDialog.hideLoading();
		}
	}
	
	public void parseDetailJson(String str) {
		try {
			JSONObject obj = new JSONObject(str);
			JSONArray detailArr = obj.getJSONArray("detail");
			JSONObject detailObj = detailArr.getJSONObject(0);
			
			original_price = Integer.toString(detailObj.getInt("discount"));
			
			DecimalFormat comma = new DecimalFormat("###,##0");
			tv_price.setText("￦" + comma.format(Integer.parseInt(original_price)));
			tv_original_price.setText("￦" + comma.format(Integer.parseInt(original_price)));
			booking_idx = Integer.toString(detailObj.getInt("idx"));
			
			
			// 적립금 있을면 button 눌러짐
			if(Integer.parseInt(credit) > 0) {
				btn_on_off.performClick();
			}
			
			new GeneralHttpTask(checkinListener, getApplicationContext()).execute(REST_URL + CHECKIN + booking_idx);
			
		} catch (Exception e) {
			Log.d("parseDetailJson", "TagDataParser" + "->" + e.toString());
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			LoadingDialog.hideLoading();
		}
	}
	
	public void parsePreCntJson(String str){
		str.trim();
		if(str.indexOf("none") >= 0) {
			reservCnt = 0;
			
			Intent i = new Intent(this, PaymentActivity.class);
			i.putExtra("isBonus", isBonus);
			i.putExtra("isFullBonus", isFullBonus);
			i.putExtra("credit", credit);
			i.putExtra("booking_idx", booking_idx);
			startActivityForResult(i,1);
			
		} else {
			try {
				JSONObject obj = new JSONObject(str);
				JSONArray rsvArr = obj.getJSONArray("rsv");
				
				reservCnt = rsvArr.length();
				
				Intent i = new Intent(this, PaymentActivity.class);
				i.putExtra("isBonus", isBonus);
				i.putExtra("isFullBonus", isFullBonus);
				i.putExtra("credit", credit);
				i.putExtra("booking_idx", booking_idx);
				startActivityForResult(i,1);
				
			} catch (Exception e) {
				Log.d(TAG, "parsePreCntJson " + e.toString());
				Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			}
		}
		
		Log.d("reservCnt", "reservCnt = " + Integer.toString(reservCnt));
		isPayment = false;
	}
	
	public void parsePostSessionJson(String str) {
		
	}
	
	public void parsePostLogin(String str) {
		try {
			JSONObject obj = new JSONObject(str);
			if ( obj.getString("login").equals("true") ) {
				// 로그인 성공
				new GeneralHttpTask(postCntListener, getApplicationContext()).execute(REST_URL + RESERVE);
			} else {
				// 로그인 실패
				// 메세지 출력하고 activity 종료
				LoadingDialog.hideLoading();
				Toast.makeText(getApplicationContext(), "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
				SharedPreferences.Editor ed = prefs.edit();
				ed.putBoolean(PREFERENCE_AUTO_LOGIN, false);
				ed.putBoolean(PREFERENCE_IS_LOGIN, false);
				ed.commit();
			}
		} catch (Exception e) {
			Log.d(TAG, "parseLoginJson " +  e.toString());
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	public boolean compareReservCnt(String str) {
		str.trim();
		
		int resultCnt = 0;
		
		if(str.indexOf("none") >= 0) {
			resultCnt = 0;
			
		} else {
			try {
				JSONObject obj = new JSONObject(str);
				JSONArray rsvArr = obj.getJSONArray("rsv");
				resultCnt = rsvArr.length();
				
			} catch (Exception e) {
				Log.d(TAG, "parsePostJson " + e.toString());
				Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			}
		}
		
		Log.d("compare reservCnt", "resultCnt = " + Integer.toString(resultCnt) + " reservCnt = " + Integer.toString(reservCnt));
		
		if(resultCnt > reservCnt)
			return true;
		else
			return false;
	}
	
	protected onCompleteListener sessionListener = new onCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "sessionListener onTAskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		@Override
		public void onTaskComplete(String result) {
			result = result.trim();
			if(result.equals("alive")) {		// session alive
				
				if(isPayment)
					new GeneralHttpTask(preCntListener, getApplicationContext()).execute(REST_URL + RESERVE);
				else 	// credit 요청
					new GeneralHttpTask(creditListener, getApplicationContext()).execute(REST_URL + SAVED_MONEY);
				
			} else if(result.equals("dead")){		// session dead
				// 재로그인
				
				// parameter setting
				ArrayList<ParameterElement> paramList = new ArrayList<ParameterElement>();
				paramList.add(new ParameterElement("email", prefs.getString(PREFERENCE_USER_ID, "")));
				paramList.add(new ParameterElement("pw", prefs.getString(PREFERENCE_USER_PWD, "")));
				
				// 로그인 요청
				new GeneralHttpTask(loginListener, paramList, getApplicationContext()).execute(REST_URL + LOGIN);
				
			} else {
				LoadingDialog.hideLoading();
				Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	protected onCompleteListener loginListener = new onCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "loginListener onTAskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseLoginJson(result);
		}
	};
	
	protected onCompleteListener creditListener = new onCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "creditListener onTAskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseCreditJson(result);
		}
	};
	
	protected onCompleteListener checkinListener = new onCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "creditListener onTAskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseCheckinJson(result);
		}
	};
	
	protected onCompleteListener detailListener = new onCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "creditListener onTAskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseDetailJson(result);
		}
	};
	
	protected onCompleteListener preCntListener = new onCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "preCntListener onTAskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		@Override
		public void onTaskComplete(String result) {
			parsePreCntJson(result);
		}
	};
	
	protected onCompleteListener postCntListener = new onCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "postCntListener onTAskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		@Override
		public void onTaskComplete(String result) {
			
			LoadingDialog.hideLoading();
			
			if(compareReservCnt(result)) {
				AlertDialog.Builder alert = new AlertDialog.Builder(HotelPaymentActivity.this);
				alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	dialog.dismiss();     //닫기
				    	setResult(RESULT_OK);
	    				finish();
				    }
				});
				alert.setMessage("결제가 정상적으로 이루어 졌습니다");
				alert.show();
			} else {
				dialog("결제 오류 : 관리자에게 문의하세요");
			}
		}
	};
	
	protected onCompleteListener postSessionListener = new onCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "postSessionListener onTAskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		@Override
		public void onTaskComplete(String result) {
			result = result.trim();
			if(result.equals("alive")) {		// session alive
				new GeneralHttpTask(postCntListener, getApplicationContext()).execute(REST_URL + RESERVE);
				
			} else if(result.equals("dead")){		// session dead
				// 재로그인
				
				// parameter setting
				ArrayList<ParameterElement> paramList = new ArrayList<ParameterElement>();
				paramList.add(new ParameterElement("email", prefs.getString(PREFERENCE_USER_ID, "")));
				paramList.add(new ParameterElement("pw", prefs.getString(PREFERENCE_USER_PWD, "")));
				
				// 로그인 요청
				new GeneralHttpTask(postLoginListener, paramList, getApplicationContext()).execute(REST_URL + LOGIN);
				
			} else {
				LoadingDialog.hideLoading();
				Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	protected onCompleteListener postLoginListener = new onCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "postLoginListener onTAskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		@Override
		public void onTaskComplete(String result) {
			parsePostLogin(result);
		}
	};
	
}
