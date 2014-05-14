/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * ErrorFragment (오류 화면)
 * 
 * 네트워크 문제 등 오류가 발생했을 시 보여지는 화면이다. 이 화면은 메인 화
 * 면 단위(MainActivity)에서 사용되는 작은 화면 단위(Fragment)이다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.ui.BaseFragment;

public class ErrorFragment extends BaseFragment implements OnClickListener {

	private Button btnRetry;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_error, container, false);
		mHostActivity.setActionBar("dailyHOTEL");

		btnRetry = (Button) view.findViewById(R.id.btn_error);
		btnRetry.setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == btnRetry.getId()) {

			// network 연결이 안되있으면
			if (!VolleyHttpClient.isAvailableNetwork()) {
				showToast("와이파이 또는 데이터 네트워크의 연결 상태를 확인해주세요", Toast.LENGTH_SHORT, true);
				return;
			} else {
				int index = ((MainActivity) mHostActivity).indexLastFragment;
				((MainActivity) mHostActivity).replaceFragment(((MainActivity) mHostActivity).getFragment(index));
//				mHostActivity.removeFragment(this);
				
			}

		}
	}
}
