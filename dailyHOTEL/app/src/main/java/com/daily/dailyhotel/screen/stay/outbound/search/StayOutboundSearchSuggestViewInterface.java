package com.daily.dailyhotel.screen.stay.outbound.search;

import com.daily.base.BaseViewInterface;
import com.daily.dailyhotel.entity.Suggest;

import java.util.List;

public interface StayOutboundSearchSuggestViewInterface extends BaseViewInterface
{
    void setSuggestsVisibility(boolean visibility);

    void setSuggests(List<Suggest> suggestList);

    void setSuggest(String suggest);

    void showKeyboard();

    void hideKeyboard();
}
