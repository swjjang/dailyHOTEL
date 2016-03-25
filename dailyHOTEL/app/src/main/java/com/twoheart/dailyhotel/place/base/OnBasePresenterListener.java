package com.twoheart.dailyhotel.place.base;

import com.android.volley.VolleyError;

public interface OnBasePresenterListener
{
    void onErrorResponse(VolleyError volleyError);

    void onError(Exception e);

    void onErrorMessage(String message);
}
