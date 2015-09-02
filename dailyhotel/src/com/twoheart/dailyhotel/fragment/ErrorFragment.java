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

import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.BaseFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ErrorFragment extends BaseFragment implements OnClickListener
{
	private TextView btnRetry;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return null;
		}

		View view = inflater.inflate(R.layout.fragment_error, container, false);
		view.setPadding(0, Util.dpToPx(container.getContext(), 56) + 1, 0, 0);

		baseActivity.setActionBar(getString(R.string.actionbar_title_error_frag), false);

		btnRetry = (TextView) view.findViewById(R.id.btn_error);
		btnRetry.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == btnRetry.getId())
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			// network 연결이 안되있으면
			if (!VolleyHttpClient.isAvailableNetwork())
			{
				showToast(getString(R.string.toast_msg_please_chk_network_status), Toast.LENGTH_SHORT, true);
				return;
			} else
			{
				int index = ((MainActivity) baseActivity).indexLastFragment;
				((MainActivity) baseActivity).replaceFragment(((MainActivity) baseActivity).getFragment(index));

			}
		}
	}
}
