package com.twoheart.dailyhotel.fragment;

import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.activity.SignupActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class NoLoginFragment extends Fragment implements OnClickListener{
	private final static String TAG = "NoLoginFragment";
	private final static int NOLOGIN_FRAGMENT = 2;
	
	private View view;
	private Button btn_login, btn_signup;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_no_login, null);
		
		// ActionBar Setting
		MainActivity activity = (MainActivity)view.getContext();
		activity.changeTitle("로그인 하세요");
		activity.hideMenuItem();
		activity.addMenuItem("dummy");
		
		loadResource();
		
		
		return view;
	}
	
	public void loadResource() {
		btn_login = (Button) view.findViewById(R.id.btn_no_login_login);
		btn_signup = (Button) view.findViewById(R.id.btn_no_login_signup);
		btn_login.setOnClickListener(this);
		btn_signup.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == btn_login.getId()) {
			Intent i = new Intent(view.getContext(), LoginActivity.class);
			MainActivity activity = (MainActivity) view.getContext();
			startActivityForResult(i, NOLOGIN_FRAGMENT);
			activity.overridePendingTransition(R.anim.slide_in_right,R.anim.hold);
			
		} else if(v.getId() == btn_signup.getId()) {
			Intent i = new Intent(view.getContext(), SignupActivity.class);
			MainActivity activity = (MainActivity) view.getContext();
			startActivityForResult(i, NOLOGIN_FRAGMENT);
			activity.overridePendingTransition(R.anim.slide_in_right,R.anim.hold);
		}
	}
	
	
	// 로그인하고 돌아왔을때 fragment switch
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == NOLOGIN_FRAGMENT) {
			if(resultCode == getActivity().RESULT_OK) {
				MainActivity activity = (MainActivity) view.getContext();
				activity.switchContent(new CreditFragment());
			}
		}
	}
	
}
