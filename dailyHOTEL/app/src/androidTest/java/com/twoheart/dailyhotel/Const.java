package com.twoheart.dailyhotel;

import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.Review;
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

    public static final String TEST_USER_BIRTHDAY = getBirthDayText(1986, 7, 17);
    public static final String TEST_USER_NAME = "Dev_Daily";
    public static final String TEST_USER_PHONE = "01012345678";

    public static final String TEST_MODIFY_USER_BIRTHDAY = getBirthDayText(2013, 7, 17);
    public static final String TEST_MODIFY_USER_NAME = "DAILYHOTEL_TEST";

    public static final String TEST_CHECK_EMAIL_ADDRESS = "dailyhotel@dailyhotel.com";

    public static final boolean TEST_IS_FORCE_PHONE_VALIDATION = false;

    public static final boolean TEST_IS_AGREED_BENEFIT = false;
    public static final boolean TEST_IS_NOTICE_AGREE = false;
    public static final boolean TEST_IS_UPDATE_BENEFIT_AGREEMENT = false;

    public static final String TEST_FACEBOOK_USER_ID = "MzEkMjIkMzMkMzMkMzgkMjkkNDIkMzAkNDAkNTIkMjYkMzAkMzAkMTUkMjEkMyQ=$NTcE4NEZBMUQ2M0JOCMEQxZNjMdFNW0Y2BHTAQkINJIxODHQJBSMzYzQzcJ=$"; // team
    public static final String TEST_FACEBOOK_USER_GENDER = "MTYkNSQ0NSQzNiQ3JDI1JDE0JDgkMjIkMTckNDgkNTAkMTYkMzMkNDUkNiQ=$OEE1NTA0THYyOEI4OBNRjREAQDzUwNTBhDFRjEzRUUzN0JYRDMTcG2PRDUO=$";
    public static final String TEST_FACEBOOK_USER_NAME = "MTAkMjIkMTAkNDMkMzUkMjgkNDgkMTQkNyQ1MSQyOCQ1MCQyMyQ0JDM3JDQ5JA==$NTRBKQzcC5RDRXE1XMDdEMUJRENZUNSFNXzE3VREQ2IODI5MEYFFYNLUYHY=$";
    public static final String TEST_FACEBOOK_USER_EMAIL = "NTQkNiQ3NiQwJDQ3JDQyJDI0JDMyJDcxJDczJDAkNjEkNjckOTkkNjkkNSQ=$SYMEZYEMDZkzQjQxMUY2QTM0RDYdDNkMwQX0FBNUExQjAwXNUM1NITZBNTJFRkLXQxRDRMVzQzFCCRHEMxRjhFODRFFMjI4MTlEMgK==$";
    public static final String TEST_FACEBOOK_USER_INDEX = "MzQkMjIkMSQ0MyQxMyQzMCQyMCQxMiQxNiQxMCQ4JDMwJDIkNDMkNTYkNTUk$OOLTI2OEEQ4QRkZIBDRFjNBQzCJBNjHMJGRUIP3OTMyNQzRQ5RDgxMODTDY=$";

    public static final String TEST_SKIP_DELETE_CREDITCARD_NUMBER = "MTQkMTIkMzkkMTQkMzckMzIkMjQkMTIkNDkkNCQxOSQwJDI5JDEkNTUkNTUk$VGMzFDFNDBDQTZCXDNSjDSFEMTQ4NRSDcxODI1MRzI1Q0UIxMUUYyQSYGUU=$";

    public static final int TEST_NIGHTS = 1;
    public static final int TEST_PAGE_INDEX = 1;
    public static final int TEST_LIMIT_LIST_COUNT = 200;
    public static final String TEST_EASY_CARD_BILLINGKEY = "3df689cd659fc6a81175779fa41d96ffedf57133";
    public static final String TEST_PAYMENT_TYPE = "CARD";
    public static final boolean TEST_IS_SHOW_LIST_DETAIL = true;
    public static final int TEST_MIN_RATING_PERSONS = 15;
    public static final String TEST_AUTO_REFUND_CANCEL_MESSAGE = "테스트 자동 환불";

    public static final String TEST_STAY_AUTO_SEARCH_TEXT = "신라";
    public static final int TEST_STAY_PROVINCE_INDEX = 5;
    public static final String TEST_STAY_CATEGORY_CODE = Category.ALL.code;
    public static final int TEST_STAY_INDEX = 981;
    public static final int TEST_STAY_SALE_ROOM_INDEX = 64294; // stay index dependency
    public static final int TEST_STAY_PERSONS = 2;
    public static final int TEST_STAY_RESERVATION_INDEX = 1406222;

    public static final String TEST_GOURMET_AUTO_SEARCH_TEXT = "강남";
    public static final int TEST_GOURMET_PROVINCE_INDEX = 5;
    public static final int TEST_GOURMET_INDEX = 50457;
    public static final int TEST_GOURMET_TIKET_INDEX = 3075;
    public static final int TEST_GOURMET_TICKET_COUNT = 1;
    public static final int TEST_GOURMET_RESERVATION_INDEX = 49941;
    public static final String TEST_GOURMET_RESERVATION_TIME = "18:00";

    public static final String TEST_KEYWORD_COUPON_CODE = "데일리특별할인";

    public static final String TEST_STAY_ADD_REVIEW_GRADE = Review.GRADE_GOOD;
    public static final String TEST_GOURMET_ADD_REVIEW_GRADE = Review.GRADE_GOOD;
    public static final int TEST_STAY_ADD_REVIEW_ITEM_INDEX = 0;
    public static final int TEST_GOURMET_ADD_REVIEW_ITEM_INDEX = 0;
    public static final String TEST_ADD_REVIEW_DETAIL_COMMENT = "자동화 테스트 리뷰 코멘트 입니다.\n 무시하세요.";

    private static String getBirthDayText(int year, int month, int dayOfMonth)
    {
        Calendar calendar = DailyCalendar.getInstance();
        calendar.set(year, month, dayOfMonth, 0, 0, 0);

        return DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
    }
}
