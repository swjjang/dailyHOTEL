/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * GlobalFont
 * 
 * 뷰그룹을 인자로 받아 해당 뷰그룹에 포함되어 있는 모든 글자들(텍스트뷰 폰
 * 트 속성)을 미리 정한 커스텀 폰트로 설정한다. 이 유틸 클래스를 사용하기
 * 이전에 미리 사용할 폰트를 설정해두어야 한다.
 *
 * @since 2014-04-01
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel.util;

import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.DailyHotel;

public class GlobalFont {

	public static void apply(ViewGroup root) {
		for (int i = 0; i < root.getChildCount(); i++) {
			View child = root.getChildAt(i);

			if (child instanceof TextView) {
				TextView fontTextView = ((TextView) child);
				
				fontTextView.setPaintFlags(((TextView) child).getPaintFlags()
						| Paint.SUBPIXEL_TEXT_FLAG);
				
				if (fontTextView.getTypeface() != null)
					if (fontTextView.getTypeface().equals(DailyHotel.getBoldTypeface()))
						continue;
				
				fontTextView.setTypeface(DailyHotel.getTypeface());
				fontTextView.invalidate();
			} else if (child instanceof ViewGroup)
				apply((ViewGroup) child);

		}

	}

}
