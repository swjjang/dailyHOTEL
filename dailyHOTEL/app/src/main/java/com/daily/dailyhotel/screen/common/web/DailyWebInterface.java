package com.daily.dailyhotel.screen.common.web;

import com.daily.base.BaseDialogViewInterface;

import java.util.Map;

import io.reactivex.Observable;

public interface DailyWebInterface extends BaseDialogViewInterface
{
    void loadUrl(String url, Map<String, String> headerMap);

    boolean canGoBack();

    void goBack();

    Observable<Boolean> smoothScrollTop();
}
