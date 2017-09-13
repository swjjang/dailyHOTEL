package com.daily.dailyhotel.screen.home.stay.outbound.search;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.Suggest;

import java.util.List;

public interface StayOutboundSearchSuggestViewInterface extends BaseDialogViewInterface
{
    void setSuggestsVisible(boolean visible);

    void setSuggests(List<Suggest> suggestList);

    void setSuggest(String suggest);

    void showKeyboard();

    void hideKeyboard();

    void setEmptySuggestsVisible(boolean visible);

    void setProgressBarVisible(boolean visible);

    void setRecentlySuggests(List<Suggest> suggestList);
}
