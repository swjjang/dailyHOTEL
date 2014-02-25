package com.twoheart.dailyhotel;

import static com.twoheart.dailyhotel.AppConstants.PREFERENCE_SELECTED_MENU;
import static com.twoheart.dailyhotel.AppConstants.SHARED_PREFERENCES_NAME;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.twoheart.dailyhotel.hotel.HotelListFragment;

public class MainActivity extends BaseActivity implements ActionBar.OnNavigationListener {

	private static final String TAG = "MainActivity";
	
	private Fragment content;
	
	private boolean isMenuItem = false;
	private String itemStr = null;
	
	private TextView	title;
	
	private SharedPreferences prefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		//actionbar setting
//		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
//		View customView = LayoutInflater.from(this).inflate(
//	            R.layout.actionbar_background, null);
//		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
//	            ActionBar.LayoutParams.MATCH_PARENT,
//	            ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setIcon(R.drawable.dh_ic_menu);
//		getSupportActionBar().setCustomView(R.layout.actionbar_background);
		
		
		// actionbar background color setting
		Drawable myDrawable;
		Resources res = getResources();
		try {
		   myDrawable = Drawable.createFromXml(res, res.getXml(R.drawable.dh_actionbar_background));
		   getSupportActionBar().setBackgroundDrawable(myDrawable);
		} catch (Exception ex) {
		   Log.e(TAG, "Exception loading drawable : " + ex.toString()); 
		}
		
		loadResource();
		
		// 맨 처음은 호텔리스트
		prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		SharedPreferences.Editor ed = prefs.edit();
		ed.putString(PREFERENCE_SELECTED_MENU, "hotel");
		ed.commit();
		
		HotelListFragment hotelListFrag = new HotelListFragment();
		setContentView(R.layout.content_frame);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, hotelListFrag)
		.commit();
	}
    
	public void loadResource() {
		title = (TextView) findViewById(R.id.tv_actionbar_title);
	}
	
	public void changeTitle(String str) {
		getSupportActionBar().setTitle("   " + str);
	}
	
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
    	return false;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	if(isMenuItem) {
    		if(itemStr.equals("dummy")) {
    			menu.add(0,2,0,"").
	    		setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    		} else {
	    		menu.add(0,1,0,itemStr).
	    		setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    		}
    	}
    	return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch (item.getItemId()) {
		case android.R.id.home:
//			toggle();
			return true;
		case 1:
//			getSlidingMenu().showSecondaryMenu();
		case 2:
			return false;
		}
    	return super.onMenuItemSelected(featureId, item);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    }
    
    public void addMenuItem(String str) {
    	isMenuItem = true;
    	itemStr = str;
    	supportInvalidateOptionsMenu();
    }
    
    public void hideMenuItem() {
    	isMenuItem = false;
    	supportInvalidateOptionsMenu();
    }
    
    // 선택된 menu에 맞게 Fragment 변경
 	// MenuFragment에서 호출됨
 	public void switchContent(Fragment fragment) {
 		content = fragment;
 		getSupportFragmentManager()
 		.beginTransaction()
 		.replace(R.id.content_frame, fragment)
 		.commitAllowingStateLoss();
// 		getSlidingMenu().showContent();
 	}
 	
 	//TODO: BACK 버튼 눌림 이벤트 발생 시 사이드 바 토글 기능 제거
 	
 	@Override
 	public boolean onKeyUp(int keyCode, KeyEvent event) {
 		if(keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
 		return super.onKeyUp(keyCode, event);
 	}
 	
 	@Override
 	public boolean onKeyDown(int keyCode, KeyEvent event) {
 		if(keyCode == KeyEvent.KEYCODE_BACK) {
// 			if(getSlidingMenu().isMenuShowing()) {
// 				if(getSlidingMenu().isSecondaryMenuShowing())
// 					toggle();
// 				else
// 					finish();
// 			}
//			else
//				showMenu();
			return false;
 		} else
 			return super.onKeyDown(keyCode, event);
 	}
}
