package com.twoheart.dailyhotel.util;

import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.DailyHotel;

public class GlobalFont {
	
	public static void apply(ViewGroup root) {
		for (int i=0; i<root.getChildCount(); i++) {
			View child = root.getChildAt(i);
			
			if (child instanceof TextView) {
				((TextView) child).setTypeface(DailyHotel.getTypeface());
				((TextView) child).setPaintFlags(((TextView) child).getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
				child.invalidate();
			} else if (child instanceof ViewGroup)
				apply((ViewGroup) child); 
			
		}
		
	}
	
}
