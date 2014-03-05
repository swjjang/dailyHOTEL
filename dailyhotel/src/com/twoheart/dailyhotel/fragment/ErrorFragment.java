package com.twoheart.dailyhotel.fragment;

import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_IS_LOGIN;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_SELECTED_MENU;
import static com.twoheart.dailyhotel.util.AppConstants.SHARED_PREFERENCES_NAME;
import android.content.SharedPreferences;
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
import com.twoheart.dailyhotel.R.id;
import com.twoheart.dailyhotel.R.layout;
import com.twoheart.dailyhotel.util.AvailableNetwork;

public class ErrorFragment extends Fragment implements OnClickListener{
	
	private View view;
	
	private Button btn_error;
	private SharedPreferences prefs;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		((MainActivity) getActivity()).setActionBar(false);
		
		view = inflater.inflate(R.layout.fragment_error, null);
		btn_error = (Button) view.findViewById(R.id.btn_error);
		btn_error.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == btn_error.getId()) {
			
			// network 연결이 안되있으면
			if(!checkNetwork()) {
				Toast.makeText(view.getContext(), "네트워크 상태를 확인해 주세요", Toast.LENGTH_SHORT).show();
				return;
			}

			prefs = view.getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
			String str = prefs.getString(PREFERENCE_SELECTED_MENU, null);
			if(str.equals("hotel")) {
				MainActivity activity = (MainActivity) view.getContext();
				activity.switchContent(new HotelListFragment());
			} else if(str.equals("booking")) {
				MainActivity activity = (MainActivity) view.getContext();
				activity.switchContent(new BookingListFragment());
			} else if(str.equals("credit")) {
				MainActivity activity = (MainActivity) view.getContext();
				if(checkLogin())	//로그인상태
					activity.switchContent(new CreditFragment());
				else		// 로그아웃 상태
					activity.switchContent(new NoLoginFragment());
			}
			
		}
	}
	
	private boolean checkLogin() {
		prefs = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		return prefs.getBoolean(PREFERENCE_IS_LOGIN, false);
	}
	
	public Boolean checkNetwork() {
		boolean result = false;
		
	    AvailableNetwork net_status = AvailableNetwork.getInstance();  
        
	    switch (net_status.getNetType(view.getContext())) {
		    case AvailableNetwork.NET_TYPE_WIFI:  
		    	//WIFI 연결상태
		    	result = true;
		        break;  
		    case AvailableNetwork.NET_TYPE_3G:  
		    	// 3G 혹은 LTE연결 상태
		    	result = true;
		        break;  
		    case AvailableNetwork.NET_TYPE_NONE: 
		    	result = false;
		    	break;
	    }
	    return result;
	}

}
