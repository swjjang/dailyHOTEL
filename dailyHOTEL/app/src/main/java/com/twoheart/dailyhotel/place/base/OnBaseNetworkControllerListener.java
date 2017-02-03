package com.twoheart.dailyhotel.place.base;

import com.twoheart.dailyhotel.network.model.ErrorBuilder;

import retrofit2.Call;
import retrofit2.Response;

public interface OnBaseNetworkControllerListener
{
    void onError(Throwable e);

    void onErrorPopupMessage(int msgCode, String message);

    void onErrorToastMessage(String message);

    void onErrorResponse(Call call, Response response);

    void onError(ErrorBuilder errorBuilder);
}
