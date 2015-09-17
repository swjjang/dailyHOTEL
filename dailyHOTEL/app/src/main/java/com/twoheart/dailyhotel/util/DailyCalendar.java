/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p/>
 * GlobalFont
 * <p/>
 * 뷰그룹을 인자로 받아 해당 뷰그룹에 포함되어 있는 모든 글자들(텍스트뷰 폰
 * 트 속성)을 미리 정한 커스텀 폰트로 설정한다. 이 유틸 클래스를 사용하기
 * 이전에 미리 사용할 폰트를 설정해두어야 한다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-04-01
 */
package com.twoheart.dailyhotel.util;

import java.util.Calendar;
import java.util.Locale;

public abstract class DailyCalendar extends Calendar
{
    /**
     *
     */
    private static final long serialVersionUID = 3121525284347676874L;

    public static Calendar getInstance()
    {
        // TimeZone은 시간위치 , Locale은 지역
        return Calendar.getInstance(Locale.KOREA);
    }
}
