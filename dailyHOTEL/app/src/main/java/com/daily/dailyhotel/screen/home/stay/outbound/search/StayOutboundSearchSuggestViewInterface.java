package com.daily.dailyhotel.screen.home.stay.outbound.search;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.StayOutboundSuggest;

import java.util.List;

public interface StayOutboundSearchSuggestViewInterface extends BaseDialogViewInterface
{
    void setSuggestsVisible(boolean visible);

    void setSuggests(List<StayOutboundSuggest> stayOutboundSuggestList);

    void setSuggest(String suggest);

    void showKeyboard();

    void hideKeyboard();

    void setEmptySuggestsVisible(boolean visible);

    void setProgressBarVisible(boolean visible);

    void setRecentlySuggests(List<StayOutboundSuggest> stayOutboundSuggestList);

    void setPopularAreaSuggests(List<StayOutboundSuggest> stayOutboundSuggestList);

    void setKeywordEditHint(String hint);

    void setKeywordEditText(String text);
}
