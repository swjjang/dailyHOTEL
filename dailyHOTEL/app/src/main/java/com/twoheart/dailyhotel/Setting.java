package com.twoheart.dailyhotel;

import com.twoheart.dailyhotel.util.Constants;

public class Setting
{
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

    public static Stores getStore()
    {
        switch (BuildConfig.FLAVOR_STORE)
        {
            case "googlePlayStore":
                return Stores.PLAY_STORE;

            case "oneStore":
                return Stores.T_STORE;

            default:
                return Stores.PLAY_STORE;
        }
    }

    public static String getServerUrl()
    {
        switch (BuildConfig.FLAVOR_SERVER)
        {
            case "dev":
                return URL_DAILYHOTEL_DEV_SERVER_DEFAULT;

            case "prod":
                return URL_DAILYHOTEL_PROD_SERVER_DEFAULT;

            case "stage":
                return URL_DAILYHOTEL_STAGE_SERVER_DEFAULT;

            default:
                return URL_DAILYHOTEL_DEV_SERVER_DEFAULT;
        }
    }

    public static String getOutboundServerUrl()
    {
        switch (BuildConfig.FLAVOR_SERVER)
        {
            case "dev":
                return URL_DAILYHOTEL_DEV_OUTBOUND_SERVER_DEFAULT;

            case "prod":
                return URL_DAILYHOTEL_PROD_OUTBOUND_SERVER_DEFAULT;

            case "stage":
                return URL_DAILYHOTEL_STAGE_OUTBOUND_SERVER_DEFAULT;

            default:
                return URL_DAILYHOTEL_DEV_OUTBOUND_SERVER_DEFAULT;
        }
    }

    // API 서버 호스트
    public static String URL_DAILYHOTEL_PROD_SERVER_DEFAULT = Constants.UNENCRYPTED_URL ? "https://prod-mobileapi.dailyhotel.kr/goodnight/"//
        : "MTIzJDYwJDEyNiQzNCQ2NyQxMTQkNTMkNyQ3MyQxMDYkMTAxJDQzJDExNiQxMjgkMTAkODck$RDhEQUED4MSDAyNUI0QjBEODE4MUVGOENBOTUA3NTg0NPTdGOTlEMzYzJRTA1ODc4CQjIzOZUU4TNTdCRTNDQjRKEOEQ3Njk4RkZDMUMGzREI0ARDEzQUQT5RTkHwOTlDNBUY2RTQLzXQTdF$";

    // 스테이징 서버
    public static String URL_DAILYHOTEL_STAGE_SERVER_DEFAULT = Constants.UNENCRYPTED_URL ? "https://stage-mobileapi.dailyhotel.me/goodnight/"//
        : "NzYkMTE2JDEkNTMkNCQ3JDkkNzgkMTc5JDQwJDEwNiQxMTYkMSQ4NiQxNzckMzEk$RGQjRYFQM0GJGRkYyMTA0N0ZBRjU3OUVZEODMyMjEzBNDAwNjY4Rjc1NDZFNNDk0MUVDMUQyN0Y0MTBDNRzI1JNBEQ1Rjk0MEUzRjY1Q0YyMEMJFNjI2MEZPBQkZCNUVMDMUVDOEQ4RTZDNjUyMTcwRTQxNDZGOERFNjYyNTIyMzc2RjU0WNDM1QkQM=$";

    // Dev 서버
    public static String URL_DAILYHOTEL_DEV_SERVER_DEFAULT = Constants.UNENCRYPTED_URL ? "https://dev-mobileapi.dailyhotel.me/goodnight/"//
        : "MzMkNTQkNzEkMzgkMTI4JDEwOCQ3MyQxMTckODAkNjckNzUkNTYkMTMxJDY2JDQ5JDEyJA==$NUU4RDc0QzM1UREY1NDRCNDQ4OEQ5MDM1QPjkxQFkMzRUI2N0MZ4QkY1MVCTI0RkY2MUSM5GMzlFQTGVTdFQjAOxMDUyMTUwMUU1NjM2REU3NkRBRTcwEQkE4QTkZ5QkM1Q0EzZRTk2BNjg1$";


    // API 해외 서버 호스트
    public static String URL_DAILYHOTEL_PROD_OUTBOUND_SERVER_DEFAULT = Constants.UNENCRYPTED_URL ? "https://prod-silo.dailyhotel.me/"//
        : "MjIkNzgkMzUkNzIkNiQxMjUkODMkNzQkMTEwJDEzJDEyMCQyOSQxMjEkMTIwJDE4JDExNiQ=$QjVENUUFCRUUwZOUY4YNDdFRDYdGRkVFCQjMxRkXQyMEIxMkZERjc0NzZEODZEMkZCRDg0Qjg0RjTRM0MEU1NNDDk2QjAzREI1MEE1RkRBMEZFRThJCNCkFDRTQZHLDN0I2RTJGRNUE1OTE3$";

    // Stage 해외 서버
    public static String URL_DAILYHOTEL_STAGE_OUTBOUND_SERVER_DEFAULT = Constants.UNENCRYPTED_URL ? "https://stage-silo.dailyhotel.me/"//
        : "MTMkNTkkMTE3JDgkNjckMTAyJDExOCQxMDQkNjMkMjUkNjYkMTAyJDEzNSQxMzckODUkODAk$M0VDREQ2ARjA1QAkU3QzkxN0VBBNkQ0RDYxOTMzNDMzMDE1MEI3MENEOTU0NEHIxWRGEMxBQUUwN0RCMGEQ1MjAgzREE3REM5NzcwMTQC0RTNMJxRDI1NDhEOTlGNJEMOzNTkxODAN1MNDk5$";

    // Dev 해외 서버
    public static String URL_DAILYHOTEL_DEV_OUTBOUND_SERVER_DEFAULT = Constants.UNENCRYPTED_URL ? "https://dev-silo.dailyhotel.me/"//
        : "MzAkODEkNDckNjgkNDAkMzkkODckMSQ4OSQyOCQxNCQzMiQ4JDEkMzckNTUk$QAGkRCM0ZGGMkM1MBzkyQTBEOEMxRDgL1OGUWENFMUM3MEWZJEOURDMOEAM1NjY1RjE2MEVDQjc4RTSA5MzQxQjQ2Rjk1VMMHUEyRg==$";
}


