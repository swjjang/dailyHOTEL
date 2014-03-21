package com.twoheart.dailyhotel.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.activity.SignupActivity;
import com.twoheart.dailyhotel.util.Constants;

public class NoLoginFragment extends Fragment implements OnClickListener, Constants {
	private final static String TAG = "NoLoginFragment";
	
	private MainActivity mHostActivity;
	private Button btnLogin, btnSignup;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_no_login, null);
		mHostActivity = (MainActivity) getActivity();
		
		mHostActivity.setActionBar("로그인하세요");
		
		btnLogin = (Button) view.findViewById(R.id.btn_no_login_login);
		btnSignup = (Button) view.findViewById(R.id.btn_no_login_signup);
		btnLogin.setOnClickListener(this);
		btnSignup.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == btnLogin.getId()) {
			Intent i = new Intent(mHostActivity, LoginActivity.class);
			startActivityForResult(i, CODE_REQUEST_FRAGMENT_NOLOGIN);
			mHostActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.hold);
			
		} else if(v.getId() == btnSignup.getId()) {
			Intent i = new Intent(mHostActivity, SignupActivity.class);
			startActivityForResult(i, CODE_REQUEST_FRAGMENT_NOLOGIN);
			mHostActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.hold);
		}
	}
	
	// 로그인하고 돌아왔을때 fragment switch
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == CODE_REQUEST_FRAGMENT_NOLOGIN) {
			if(resultCode == getActivity().RESULT_OK) {
				mHostActivity.replaceFragment(mHostActivity.getFragment(mHostActivity.INDEX_CREDIT_FRAGMENT));
			}
		}
	}
	
}
