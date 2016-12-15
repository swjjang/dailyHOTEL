package com.twoheart.dailyhotel;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.Calendar;

/**
 * Created by android_sam on 2016. 12. 12..
 */

public class Const implements Constants
{
    public static final String TEST_EMAIL = "MjgkMjckMjYkMTEkMjUkMTQkMzkkMyQxMyQ0OSQ5JDUzJDUyJDU2JDIxJDIk$NjLBDGMEYyHM0MTF4NFDJBUMDkzOTY5QRjAQAwPOUU3RAjEyNTcwQQZzBgE=$"; // 00
    public static final String TEST_PASSWORD = "MTUkNyQxJDQ2JDEkMjAkOCQxNSQxMiQ0NCQ0MCQ1MyQxMSQ1NCQ0JDMzJA==$QAEzQU5RjVBOFDDQzkH3NTIL2VMUI1MUNFCNTNCRTdCFQTdDYQ0REQkIUVX=$";
    public static final String TEST_MODIFY_PASSWORD = "MjIkNDQkMTMkMTQkNDckMjgkNDMkNDkkMTQkMCQ0MyQ0NCQ5JDUxJDEyJDQ5JA==$DODY3MjBDSM0DI0QYXM0NBRDRCQ0RQxMLUUyMzI1NjA0MCATUSF4QLTcJUH=$"; // build

    public static final String REGEX_EMAIL_FORMAT = "^[_a-zA-Z0-9-\\\\.]+@[\\\\.a-zA-Z0-9-]+\\\\.[a-zA-Z]+$";

    public static final String TEST_USER_BIRTHDAY = getBirthDayText(2013, 7, 17);
    public static final String TEST_USER_NAME = "DAILYHOTEL_TEST";
    public static final String TEST_CHECK_EMAIL_ADDRESS = "dailyhotel@dailyhotel.com";


    private static String getBirthDayText(int year, int month, int dayOfMonth)
    {
        Calendar calendar = DailyCalendar.getInstance();
        calendar.set(year, month, dayOfMonth, 0, 0, 0);

        return DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
    }
}
