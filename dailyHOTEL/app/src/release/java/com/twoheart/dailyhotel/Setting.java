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
    public static String URL_DAILYHOTEL_SERVER_DEFAULT = Constants.UNENCRYPTED_URL ? "https://prod-mobileapi.dailyhotel.kr/goodnight/"//
        : "MTIzJDYwJDEyNiQzNCQ2NyQxMTQkNTMkNyQ3MyQxMDYkMTAxJDQzJDExNiQxMjgkMTAkODck$RDhEQUED4MSDAyNUI0QjBEODE4MUVGOENBOTUA3NTg0NPTdGOTlEMzYzJRTA1ODc4CQjIzOZUU4TNTdCRTNDQjRKEOEQ3Njk4RkZDMUMGzREI0ARDEzQUQT5RTkHwOTlDNBUY2RTQLzXQTdF$";
}
