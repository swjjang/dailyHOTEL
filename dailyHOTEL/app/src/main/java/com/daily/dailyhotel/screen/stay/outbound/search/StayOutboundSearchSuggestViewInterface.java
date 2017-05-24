package com.daily.dailyhotel.screen.stay.outbound.search;

import com.daily.base.BaseViewInterface;
import com.daily.dailyhotel.entity.Suggest;

import java.util.List;

public interface StayOutboundSearchSuggestViewInterface extends BaseViewInterface
{
    void setSuggestsVisible(boolean visible);

    void setSuggests(List<Suggest> suggestList);

    void setSuggest(String suggest);

    void showKeyboard();

    void hideKeyboard();

    void setEmptySuggestsVisible(boolean visible);

    void setProgressBarVisible(boolean visible);
}
