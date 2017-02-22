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
    public static String URL_DAILYHOTEL_SERVER_DEFAULT = Constants.UNENCRYPTED_URL ? "https://mobileapi.dailyhotel.kr/goodnight/" : "ODQkNCQ4JDEwMCQ2NCQzNSQxMDckMTE2JDEyMyQyNCQxMDgkODckMCQxMDYkODAkMTIxJA==$TNEExAN0JQFRjMyRUNCRkI2RDHIyRkEyMTY5MAEU3M0JEM0FCNDlEODMyOEZGRjZFQjZhCM0RCMkEyOUIMyNjE5NEGQ3GNEZCNTgwMEEwNORjEyOQXThBRDczJOTDUxMjAKxRTEyMDAxNDg3$";
}
