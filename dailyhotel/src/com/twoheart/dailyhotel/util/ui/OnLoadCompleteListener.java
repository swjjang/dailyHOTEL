/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * OnLoadCompleteListener
 * 
 * 모든 로딩 작업이 완료되어 사용자에게 보여줄 수 있는 준비가 된 상태를 알
 * 려주는 인터페이스이다. Activity에 종속된 Fragment에서의 작업의 완료
 * 를 Activity 및 다른 Fragment에 일괄적으로 알리기 위해 설계됐다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel.util.ui;

import android.support.v4.app.Fragment;

public interface OnLoadCompleteListener {
	public void onLoadComplete(Fragment fragment, boolean isSucceed);
	
}
