package com.twoheart.dailyhotel.fragment;

import static com.twoheart.dailyhotel.util.AppConstants.BONUS_ALL;
import static com.twoheart.dailyhotel.util.AppConstants.LOGIN;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_AUTO_LOGIN;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_IS_LOGIN;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_USER_ID;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_USER_PWD;
import static com.twoheart.dailyhotel.util.AppConstants.REST_URL;
import static com.twoheart.dailyhotel.util.AppConstants.SAVED_MONEY;
import static com.twoheart.dailyhotel.util.AppConstants.SHARED_PREFERENCES_NAME;
import static com.twoheart.dailyhotel.util.AppConstants.USERINFO;
import static com.twoheart.dailyhotel.util.AppConstants.USER_ALIVE;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.obj.Credit;
import com.twoheart.dailyhotel.obj.Parameter;
import com.twoheart.dailyhotel.util.KakaoLink;
import com.twoheart.dailyhotel.util.network.GeneralHttpTask;
import com.twoheart.dailyhotel.util.network.OnCompleteListener;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;

public class CreditFragment extends Fragment implements OnClickListener {
	
	private static final String TAG = "CreditFragment";
	
	private View view;
	private Button btn_invite;
	private TextView tv_bonus, tv_recommender_code;
	private Button btnCredit;
	private String code;
	
	private ArrayList<Credit> list;
	
	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;
	
	SharedPreferences prefs;
	
	// Jason | Google analytics
		@Override
	public void onStart() {
		super.onStart();
		HashMap<String, String> hitParameters = new HashMap<String, String>();
		hitParameters.put(Fields.HIT_TYPE, "appview");
		hitParameters.put(Fields.SCREEN_NAME, "Saving View");
		
		mGaTracker.send(hitParameters);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_credit, null);
		prefs = view.getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		loadResource();
		
		// Google analytics
		mGaInstance = GoogleAnalytics.getInstance(view.getContext());
		mGaTracker = mGaInstance.getTracker("UA-43721645-1");

		// ActionBar Setting
		MainActivity activity = (MainActivity)view.getContext();
		activity.changeTitle("적립금");
//		activity.hideMenuItem();
//		activity.addMenuItem("적립내역");
		
		// Right Sliding setting
//		activity.getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
		
		LoadingDialog.showLoading(view.getContext());
		new GeneralHttpTask(sessionListener, view.getContext()).execute(REST_URL + USER_ALIVE);
		
		return view;
	}
	
	public void loadResource() {
		btn_invite = (Button) view.findViewById(R.id.btn_credit_invite_frd);
		btnCredit = (Button) view.findViewById(R.id.btn_credit);
		tv_recommender_code = (TextView) view.findViewById(R.id.tv_credit_recommender_code);
		btn_invite.setOnClickListener(this);
		btnCredit.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		
		if(v.getId() == btn_invite.getId()) {
			try {
//						sendUrlLink(v);
				sendAppData(v);
			} catch (Exception e) {
				Log.d(TAG, "kakao link error " + e.toString());
			}
			
		} else if (v.getId() == btnCredit.getId()) {
			MainActivity activity = (MainActivity) view.getContext();
			
			Fragment creditListFragment = CreditListFragment.newInstance(list);
			
			activity.getSupportFragmentManager().beginTransaction()
			.add(R.id.content_frame, creditListFragment)
			.addToBackStack(null)
			.commitAllowingStateLoss();
			
//			activity.switchContent(CreditListFragment.newInstance(list));
			
		}
	}
	
	public void parseLoginJson(String str) {
		try {
			JSONObject obj = new JSONObject(str);
			if ( obj.getString("login").equals("true") ) {

				// credit 요청
				new GeneralHttpTask(creditListener, view.getContext()).execute(REST_URL + SAVED_MONEY);
				
			} else {
				// 로그인 실패
				Toast.makeText(view.getContext(), "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
				SharedPreferences.Editor ed = prefs.edit();
				ed.putBoolean(PREFERENCE_AUTO_LOGIN, false);
				ed.putBoolean(PREFERENCE_IS_LOGIN, false);
				ed.commit();
				
				LoadingDialog.hideLoading();
				Toast.makeText(view.getContext(), "다시 로그인해 주세요", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Log.d(TAG, "parseLoginJson " +  e.toString());
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void parseCreditJson(String str) {
		try {
			
			DecimalFormat comma = new DecimalFormat("###,##0");
			str = comma.format(Integer.parseInt(str.trim()));
			tv_bonus = (TextView) view.findViewById(R.id.tv_credit_money);
			tv_bonus.setText("￦" + str);
			
			new GeneralHttpTask(recommendListener, view.getContext()).execute(REST_URL + USERINFO);
			
		} catch (Exception e) {
			Log.d(TAG, "parseLoginJson " +  e.toString());
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void parseUserInfoJson(String str) {
		try {
			JSONObject obj = new JSONObject(str);
			code = obj.getString("rndnum");
			tv_recommender_code.setText("추천인 코드 : " + obj.getString("rndnum"));
			
			new GeneralHttpTask(listListener, view.getContext()).execute(REST_URL + BONUS_ALL);
			
		} catch (Exception e) {
			Log.d(TAG, "parseUserInfoJson " +  e.toString());
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void parseListJson(String str) {
		try {
			list = new ArrayList<Credit>();
			
			JSONObject obj = new JSONObject(str);
			JSONArray arr = obj.getJSONArray("history");
			for(int i=0; i<arr.length(); i++) {
				JSONObject historyObj =  arr.getJSONObject(i);
				String content = historyObj.getString("content");
				String expires = historyObj.getString("expires");
				String bonus = historyObj.getString("bonus");
				
				list.add(new Credit(content, bonus, expires));
			}
			
			setCreditList();
			LoadingDialog.hideLoading();
			
		} catch(Exception e) {
			Log.d(TAG, "parseListJson " +  e.toString());
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void setCreditList() {
//		MainActivity activity = (MainActivity) view.getContext();
//		FragmentTransaction t = activity.getSupportFragmentManager().beginTransaction();
//		activity.getSupportFragmentManager()
//		.beginTransaction()
//		.replace(R.id.menu_frame_right, CreditListFragment.newInstance(list))
//		.commitAllowingStateLoss();
	}
	
	//session check
	protected OnCompleteListener sessionListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "sessionListener onTAskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			MainActivity activity = (MainActivity) view.getContext();
			activity.switchContent(new ErrorFragment());
		}
		
		@Override
		public void onTaskComplete(String result) {
			result = result.trim();
			if(result.equals("alive")) {		// session alive
				// credit 요청
				new GeneralHttpTask(creditListener, view.getContext()).execute(REST_URL + SAVED_MONEY);
				
			} else if(result.equals("dead")){		// session dead
				// 재로그인
				
				// parameter setting
				ArrayList<Parameter> paramList = new ArrayList<Parameter>();
				paramList.add(new Parameter("email", prefs.getString(PREFERENCE_USER_ID, "")));
				paramList.add(new Parameter("pw", prefs.getString(PREFERENCE_USER_PWD, "")));
				
				// 로그인 요청
				new GeneralHttpTask(loginListener, paramList, view.getContext()).execute(REST_URL + LOGIN);
				
			} else {
				LoadingDialog.hideLoading();
				Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	protected OnCompleteListener loginListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "loginListener onTAskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			MainActivity activity = (MainActivity) view.getContext();
			activity.switchContent(new ErrorFragment());
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseLoginJson(result);
		}
	};
	
	protected OnCompleteListener creditListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "creditListener onTAskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			MainActivity activity = (MainActivity) view.getContext();
			activity.switchContent(new ErrorFragment());
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseCreditJson(result);
		}
	};
	
	protected OnCompleteListener recommendListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "creditListener onTAskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			MainActivity activity = (MainActivity) view.getContext();
			activity.switchContent(new ErrorFragment());
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseUserInfoJson(result);
		}
	};
	
	protected OnCompleteListener listListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "creditListener onTAskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			MainActivity activity = (MainActivity) view.getContext();
			activity.switchContent(new ErrorFragment());
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseListJson(result);
		}
	};
	
	public void sendUrlLink(View v) throws NameNotFoundException {
		// Recommended: Use application context for parameter.
		KakaoLink kakaoLink = KakaoLink.getLink(view.getContext());

		// check, intent is available.
		if (!kakaoLink.isAvailableIntent()) {
			alert("카카오톡이 설치되어 있지 않습니다.");			
			return;
		}

		/**
		 * @param activity
		 * @param url
		 * @param message
		 * @param appId
		 * @param appVer
		 * @param appName
		 * @param encoding
		 */
		kakaoLink.openKakaoLink(this.getActivity(), 
				"http://dailyhotel.kr", 
				"좋은 어플 추천해 드려요~\n" +
				"오늘 남은 객실만 최대 70% 할인하는 데일리호텔이에요." +
				"추천인코드 : " + code + "을 입력하면 5,000원 바로 할인!", 
				getActivity().getPackageName(), 
				getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName, 
				"데일리호텔", 
				"UTF-8");
	}
	
	/**
	 * Send App data
	 */
	public void sendAppData(View v) throws NameNotFoundException {
		ArrayList<Map<String, String>> metaInfoArray = new ArrayList<Map<String, String>>();

		// If application is support Android platform.
		Map<String, String> metaInfoAndroid = new Hashtable<String, String>(1);
		metaInfoAndroid.put("os", "android");
		metaInfoAndroid.put("devicetype", "phone");
		// Play Store
		metaInfoAndroid.put("installurl", "http://kakaolink.dailyhotel.co.kr");
		// T Store
//		metaInfoAndroid.put("installurl", "http://tsto.re/0000412421");
		metaInfoAndroid.put("executeurl", "kakaoLinkTest://starActivity");
		
		// If application is support ios platform.
		Map<String, String> metaInfoIOS = new Hashtable<String, String>(1);
		metaInfoIOS.put("os", "ios");
		metaInfoIOS.put("devicetype", "phone");
		metaInfoIOS.put("installurl", "http://kakaolink.dailyhotel.co.kr");
		metaInfoIOS.put("executeurl", "kakaoLinkTest://starActivity");
		
		// add to array
		metaInfoArray.add(metaInfoAndroid);
		metaInfoArray.add(metaInfoIOS);
		
		// Recommended: Use application context for parameter. 
		KakaoLink kakaoLink = KakaoLink.getLink(view.getContext().getApplicationContext());
		
		// check, intent is available.
		if(!kakaoLink.isAvailableIntent()) {
			alert("카카오톡이 설치되어 있지 않습니다.");			
			return;
		}
		
		String myId = prefs.getString(PREFERENCE_USER_ID, null);
		
		/**
		 * @param activity
		 * @param url
		 * @param message
		 * @param appId
		 * @param appVer
		 * @param appName
		 * @param encoding
		 * @param metaInfoArray
		 */
		
		kakaoLink.openKakaoAppLink(
				this.getActivity(), 
				"http://link.kakao.com/?test-android-app", 
				"좋은 어플 추천해 드려요~\n" +
				"오늘 남은 객실만 최대 70% 할인하는 데일리호텔이에요.\n" +
				"추천인코드 : " + code + "을 입력하면 5,000원 바로 할인!", 
				view.getContext().getPackageName(), 
				view.getContext().getPackageManager().getPackageInfo(view.getContext().getPackageName(), 0).versionName,
				"dailyHOTEL 초대 메시지",
				"UTF-8", 
				metaInfoArray);
	}
	
	private void alert(String message) {
		new AlertDialog.Builder(view.getContext())
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.app_name)
			.setMessage(message)
			.setPositiveButton(android.R.string.ok, null)
			.create().show();
	}
	
}