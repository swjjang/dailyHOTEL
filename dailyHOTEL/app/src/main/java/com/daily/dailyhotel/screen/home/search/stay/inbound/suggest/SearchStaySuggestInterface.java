package com.daily.dailyhotel.screen.home.search.stay.inbound.suggest;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.Suggest;

import java.util.List;

public interface SearchStaySuggestInterface extends BaseDialogViewInterface
{
    void setSuggestsVisible(boolean visible);

    void setSuggests(List<Suggest> suggestList);

    void setSuggest(String suggest);

    void showKeyboard();

    void hideKeyboard();

    void setEmptySuggestsVisible(boolean visible);

    void setProgressBarVisible(boolean visible);

    void setRecentlySuggests(List<Suggest> suggestList);

    void setPopularAreaSuggests(List<Suggest> suggestList);

    void setKeywordEditText(String text);
}
