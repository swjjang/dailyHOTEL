package com.twoheart.dailyhotel.util;

import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;

import android.content.Context;

public class KakaoLinkManager implements Constants{
	
	private static KakaoLinkManager instance = null;
	
	public static KakaoLinkManager getInstance(Context context) {
		if (instance == null) instance = new KakaoLinkManager(context);
		return instance;
	}
	
	private KakaoLink kkLink;
	private KakaoTalkLinkMessageBuilder kkMsgBuilder;
	private Context mContext;
	
	private KakaoLinkManager(Context context) {
		try {
			mContext = context;
			kkLink = KakaoLink.getKakaoLink(mContext);
			kkMsgBuilder = kkLink.createKakaoTalkLinkMessageBuilder();
			
		} catch (KakaoParameterException e) {
			e.printStackTrace();
		}
	}
	
	public void sendInviteMsgKakaoLink(String text) {
		try {
			kkMsgBuilder.addText(text);
//			kkMsgBuilder.addImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT2hI7MCmNNAANaPdi3TNR63X_WzAKPhT-d5ONn8lYGP2gONQPnlAKyivg", 300, 200);
			kkMsgBuilder.addAppButton("æ€¿∏∑Œ ¿Ãµø");
			kkLink.sendMessage(kkMsgBuilder.build(), mContext);
		} catch (KakaoParameterException e) {
			e.printStackTrace();
		}
	}
	
	
}
