package com.twoheart.dailyhotel;

import com.twoheart.dailyhotel.util.Constants;

public class Setting
{
    public static Stores RELEASE_STORE = BuildConfig.RELEASE_STORE;

    // 스토어 선택.
    public enum Stores
    {
        PLAY_STORE("PlayStore"),
        T_STORE("Tstore");

        private String mName;

        Stores(String name)
        {
            mName = name;
        }

        public String getName()
        {
            return mName;
        }
    }

    // API 서버 호스트
    //    public static String URL_DAILYHOTEL_SERVER_DEFAULT = Constants.UNENCRYPTED_URL ? "https://prod-mobileapi.dailyhotel.kr/goodnight/"//
    //        : "MTIzJDYwJDEyNiQzNCQ2NyQxMTQkNTMkNyQ3MyQxMDYkMTAxJDQzJDExNiQxMjgkMTAkODck$RDhEQUED4MSDAyNUI0QjBEODE4MUVGOENBOTUA3NTg0NPTdGOTlEMzYzJRTA1ODc4CQjIzOZUU4TNTdCRTNDQjRKEOEQ3Njk4RkZDMUMGzREI0ARDEzQUQT5RTkHwOTlDNBUY2RTQLzXQTdF$";

    // 스테이징 서버
    //    public static String URL_DAILYHOTEL_SERVER_DEFAULT = Constants.UNENCRYPTED_URL ? "https://prod-ready-mobileapi.dailyhotel.kr/goodnight/"//
    //        : "MzgkNzQkMTE0JDIkNTgkMTQ0JDM3JDQzJDEwOSQ1JDQ2JDE3MiQxNTMkMjkkMTUxJDEyMiQ=$MzTk3INzFDREQ0RURCRjhDQjZFRUNDFMUJDRUE5FNzEU2VMSkJGM0MwMTIyNDk0PRDg4ODhDM0I5OEE2NBTM4NkRGOUNCRUI3RUVCOTdDOTY4MEUT2NEYwQ0IyKPN0VCRTM1QjYxOEFDODcxNUZGNTcEIwOUJNERjQwRTg2MEQ4QzU2MOkEwMkNGOTE=$";

    public static String URL_DAILYHOTEL_SERVER_DEFAULT = Constants.UNENCRYPTED_URL ? "http://dev-mobileapi.dailyhotel.me/goodnight/"//
        : "OTAkMzQkMTckNDYkNjkkMzkkNyQxMjEkMTI5JDY5JDQ0JDk3JDE2JDg5JDE0MSQ5MSQ=$NjBBMzJMCMTJEQzICzRBTEzMEREMjlGRjQ2QjBBGQUTI2CNzcxCRjZDNzBEMEE1MTU1OUVETN0WIwRkMxQUZDMTk5INNjVCMzEwRDTRNFQzEzQjg4Q0EwQjk4RUEyQjJA4QjFCMA0Q2MzIAw$";
    //    public static String URL_DAILYHOTEL_SERVER_DEFAULT = Constants.UNENCRYPTED_URL ? "http://dev-alpha-mobileapi.dailyhotel.me/goodnight/" : "ODkkMTI4JDUxJDExOSQ4MyQyNyQxNzMkMTIwJDQ0JDE0MSQxNzgkMTUkNzMkOTQkNzEkMTUxJA==$RTk3RjAxMTk0MEZVFOTY5MDlBRjAW1MjAwNTRDMTQzRURECQkY3QjgD1NkU2M0M2MTE2N0RGFMCTcyOERBM0RBQTTZFODYwVRPUIwOUM3QTk0OUVFRjhDOTc0NjQ2XQLkFFM0YwN0VZGRDZCRQjE1NkNM3NjQwMEM4QzBBMUJBNzcwMkE4MUFBDIQkI=$";
    //    public static String URL_DAILYHOTEL_SERVER_DEFAULT = Constants.UNENCRYPTED_URL ? "http://dev-temp-mobileapi.dailyhotel.me/goodnight/" : "MCQxNTkkMTY2JDExOCQxMyQ2NSQyMiQ4MiQxMjIkODYkMTQ0JDEwNiQ2MCQ1MSQxNzckNjck$MQTkyMzM0NDZGEMjM0RUU3FQjkwRTlEMTI3MjE3MTdEN0Y0MjAwKQjdBRTEzNZzE0QTXIN1MTYwNEFFMjRDRjWRBQITlFMjk3RTEwNkExQ0NGBNUJBNzlFNjc2M0M4MHHUZEMEJGRDkyNTI1ODNGERjdFQ0QzMjQ2NzcyQkU0MkVU2NENDLUOUQ3QTM=$";
}
