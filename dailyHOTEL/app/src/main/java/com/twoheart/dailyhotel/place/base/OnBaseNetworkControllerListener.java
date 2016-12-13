package com.twoheart.dailyhotel.place.base;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public interface OnBaseNetworkControllerListener
{
    void onErrorResponse(VolleyError volleyError);

    void onError(Throwable e);

    void onErrorPopupMessage(int msgCode, String message);

    void onErrorToastMessage(String message);

    void onErrorResponse(Call<JSONObject> call, Response<JSONObject> response);
}
