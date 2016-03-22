package com.twoheart.dailyhotel.util;

import java.util.ArrayList;
import java.util.List;

public class DailyRecentSearches
{
    private static final int MAX_KEYWORD = 10;

    private List<String> mKeywordList;

    public DailyRecentSearches(String text)
    {
        mKeywordList = new ArrayList<>();

        paserText(text);
    }

    public void addString(String text)
    {
        if(Util.isTextEmpty(text) == true)
        {
            return;
        }

        boolean result = contains(text);

        if (result == true)
        {
            remove(text);
        }

        mKeywordList.add(0, text);
    }

    public List<String> getList()
    {
        return mKeywordList;
    }

    public int size()
    {
        return mKeywordList.size();
    }

    public void clear()
    {
        mKeywordList.clear();
    }

    public String toString()
    {
        if (mKeywordList.size() == 0)
        {
            return "";
        }

        StringBuilder result = new StringBuilder();

        for (String keyword : mKeywordList)
        {
            if (result.length() != 0)
            {
                result.append('_');
            }

            result.append(keyword);
        }

        return result.toString();
    }

    private boolean remove(String text)
    {
        if(Util.isTextEmpty(text) == true)
        {
            return false;
        }

        for (String keyword : mKeywordList)
        {
            if (keyword.equalsIgnoreCase(text) == true)
            {
                return mKeywordList.remove(keyword);
            }
        }

        return false;
    }

    private boolean contains(String text)
    {
        if(Util.isTextEmpty(text) == true)
        {
            return false;
        }

        for (String keyword : mKeywordList)
        {
            if (keyword.equalsIgnoreCase(text) == true)
            {
                return true;
            }
        }

        return false;
    }

    private void paserText(String text)
    {
        if(Util.isTextEmpty(text) == true)
        {
            return;
        }

        String[] keywords = text.split("\\_");

        for (String keyword : keywords)
        {
            mKeywordList.add(keyword);
        }
    }
}
