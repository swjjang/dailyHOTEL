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

    public static String URL_DAILYHOTEL_SERVER_DEFAULT = Constants.UNENCRYPTED_URL ? "https://dev-mobileapi.dailyhotel.me/goodnight/"//
        : "MzMkNTQkNzEkMzgkMTI4JDEwOCQ3MyQxMTckODAkNjckNzUkNTYkMTMxJDY2JDQ5JDEyJA==$NUU4RDc0QzM1UREY1NDRCNDQ4OEQ5MDM1QPjkxQFkMzRUI2N0MZ4QkY1MVCTI0RkY2MUSM5GMzlFQTGVTdFQjAOxMDUyMTUwMUU1NjM2REU3NkRBRTcwEQkE4QTkZ5QkM1Q0EzZRTk2BNjg1$";
}
