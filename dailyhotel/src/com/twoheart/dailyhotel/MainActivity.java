package com.twoheart.dailyhotel;

import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_IS_LOGIN;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_SELECTED_MENU;
import static com.twoheart.dailyhotel.util.AppConstants.SHARED_PREFERENCES_NAME;

import java.security.MessageDigest;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.twoheart.dailyhotel.fragment.BookingListFragment;
import com.twoheart.dailyhotel.fragment.CreditFragment;
import com.twoheart.dailyhotel.fragment.HotelListFragment;
import com.twoheart.dailyhotel.fragment.NoLoginFragment;
import com.twoheart.dailyhotel.fragment.SettingFragment;
import com.twoheart.dailyhotel.obj.DrawerMenu;
import com.twoheart.dailyhotel.util.AppConstants;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.CloseOnBackPressed;

public class MainActivity extends BaseActivity implements OnItemClickListener {

	private static final String TAG = "MainActivity";

	private Fragment content;
	private TextView title;

	private SharedPreferences prefs;
	private CloseOnBackPressed backButtonHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setNavigationDrawer(this);
		
		title = (TextView) findViewById(R.id.tv_actionbar_title);
		backButtonHandler = new CloseOnBackPressed(this);

		// 맨 처음은 호텔리스트
		showHomeFragment();

		if (AppConstants.DEBUG) {
			printPackageKeyHash();
		}

	}
	
	public void showHomeFragment() {
		mDrawerList.setItemChecked(mMenuImages.indexOf(menuHotel), true);

		prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		SharedPreferences.Editor ed = prefs.edit();
		ed.putString(PREFERENCE_SELECTED_MENU, "hotel");
		ed.commit();

		HotelListFragment hotelListFrag = new HotelListFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, hotelListFrag).commit();
		
	}
	
	public void printPackageKeyHash() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash: getPackageName()" + getPackageName(),
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {

		Fragment newContent = null;

		switch (((DrawerMenu) (adapterView.getAdapter().getItem(position)))
				.getIcon()) {
		case R.drawable.selector_drawermenu_todayshotel:
			newContent = new HotelListFragment();
			break;

		case R.drawable.selector_drawermenu_reservation:
			newContent = new BookingListFragment();
			break;

		case R.drawable.selector_drawermenu_saving:
			if (checkLogin()) // 로그인상태
				newContent = new CreditFragment();
			else
				// 로그아웃 상태
				newContent = new NoLoginFragment();
			break;

		case R.drawable.selector_drawermenu_setting:
			newContent = new SettingFragment();
			break;
		}

		if (newContent != null) {
			mDrawerList.setSelection(position);
			switchFragment(newContent);
			mDrawerLayout.closeDrawer(mDrawerList);
		}

	}

	private boolean checkLogin() {
		return prefs.getBoolean(PREFERENCE_IS_LOGIN, false);
	}

	private void switchFragment(Fragment fragment) {
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		switchContent(fragment);
	}

	// 선택된 menu에 맞게 Fragment 변경
	// MenuFragment에서 호출됨
	public void switchContent(Fragment fragment) {
		content = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment)
				.commitAllowingStateLoss();
	}

	@Override
	public void finish() {
		if (backButtonHandler.onBackPressed())
			super.finish();

	}

}
