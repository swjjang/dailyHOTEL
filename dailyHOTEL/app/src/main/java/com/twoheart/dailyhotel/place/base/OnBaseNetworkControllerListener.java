package com.twoheart.dailyhotel.place.base;

import com.android.volley.VolleyError;

public interface OnBaseNetworkControllerListener
{
    void onErrorResponse(VolleyError volleyError);

    void onError(Exception e);

    void onErrorPopupMessage(int msgCode, String message);

    void onErrorToastMessage(String message);
}
