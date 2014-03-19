package com.twoheart.dailyhotel.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;

public class ErrorFragment extends Fragment implements OnClickListener {

	private MainActivity mHostActivity;
	private Button btnRetry;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_error, null);
		mHostActivity = (MainActivity) getActivity();
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
				Toast.makeText(mHostActivity, "네트워크 상태를 확인해 주세요",
						Toast.LENGTH_SHORT).show();
				return;
			} else {
				mHostActivity.removeFragment(this);
				
			}

		}
	}
}
