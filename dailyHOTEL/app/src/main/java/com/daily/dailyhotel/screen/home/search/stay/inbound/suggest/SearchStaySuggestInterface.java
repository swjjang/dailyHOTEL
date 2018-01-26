package com.daily.dailyhotel.screen.home.search.stay.inbound.suggest;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.StaySuggest;

import java.util.List;

public interface SearchStaySuggestInterface extends BaseDialogViewInterface
{
    void setSuggestsVisible(boolean visible);

    void setSuggests(List<StaySuggest> staySuggestList);

    void setSuggest(String suggest);

    void showKeyboard();

    void hideKeyboard();

    void setEmptySuggestsVisible(boolean visible);

    void setProgressBarVisible(boolean visible);

    void setRecentlySuggests(List<StaySuggest> staySuggestList);

    void setPopularAreaSuggests(List<StaySuggest> staySuggestList);

    void setKeywordEditText(String text);
}
