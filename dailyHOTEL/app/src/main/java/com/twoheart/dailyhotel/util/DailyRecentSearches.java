package com.twoheart.dailyhotel.util;

import com.twoheart.dailyhotel.model.Keyword;

import java.util.ArrayList;
import java.util.List;

public class DailyRecentSearches
{
    public static final int MAX_KEYWORD = 10;
    private static final char ICON_DELIMITER = ':';
    private static final char KEYWORD_DELIMITER = '_';

    private List<Keyword> mKeywordList;

    public DailyRecentSearches(String text)
    {
        mKeywordList = new ArrayList<>();

        paserText(text);
    }

    public void addString(Keyword recentKeyword)
    {
        if (recentKeyword == null || Util.isTextEmpty(recentKeyword.name) == true)
        {
            return;
        }

        boolean result = contains(recentKeyword);

        if (result == true)
        {
            remove(recentKeyword);
        } else
        {
            int size = mKeywordList.size();

            if (size == MAX_KEYWORD)
            {
                mKeywordList.remove(size - 1);
            }
        }

        mKeywordList.add(0, recentKeyword);
    }

    public List<Keyword> getList()
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

        for (Keyword keyword : mKeywordList)
        {
            if (result.length() != 0)
            {
                result.append(KEYWORD_DELIMITER);
            }

            result.append(String.format("%d%c%s", keyword.icon, ICON_DELIMITER, keyword.name));
        }

        return result.toString();
    }

    private boolean remove(Keyword recentKeyword)
    {
        if (recentKeyword == null || Util.isTextEmpty(recentKeyword.name) == true)
        {
            return false;
        }

        for (Keyword keyword : mKeywordList)
        {
            if (recentKeyword.icon == keyword.icon && recentKeyword.name.equalsIgnoreCase(keyword.name) == true)
            {
                return mKeywordList.remove(keyword);
            }
        }

        return false;
    }

    private boolean contains(Keyword recentKeyword)
    {
        if (recentKeyword == null || Util.isTextEmpty(recentKeyword.name) == true)
        {
            return false;
        }

        for (Keyword keyword : mKeywordList)
        {
            if (recentKeyword.icon == keyword.icon && recentKeyword.name.equalsIgnoreCase(keyword.name) == true)
            {
                return true;
            }
        }

        return false;
    }

    private void paserText(String text)
    {
        if (Util.isTextEmpty(text) == true)
        {
            return;
        }

        String[] keywords = text.split(String.format("\\%c", KEYWORD_DELIMITER));
        String[] values;

        for (String keyword : keywords)
        {
            values = keyword.split(String.format("\\%c", ICON_DELIMITER));

            mKeywordList.add(new Keyword(Integer.parseInt(values[0]), values[1]));
        }
    }
}
