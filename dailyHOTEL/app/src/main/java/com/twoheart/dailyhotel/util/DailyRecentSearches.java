package com.twoheart.dailyhotel.util;

import com.twoheart.dailyhotel.model.Keyword;

import java.util.ArrayList;
import java.util.List;

public class DailyRecentSearches
{
    public static final int MAX_KEYWORD = 10;
    private static final char ICON_DELIMITER = ':';
    private static final String KEYWORD_DELIMITER = "^+^\n";

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
            result.append(String.format("%d%c%s", keyword.icon, ICON_DELIMITER, keyword.name));
            result.append(KEYWORD_DELIMITER);
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

        // 호텔/고메 이름에 문자열이 있는 경우가 있는데 관련해서 수정이 필요하다
        if (text.indexOf(KEYWORD_DELIMITER) == -1)
        {
            return;
        }

        int count = 0;
        int startIndex = 0;

        while ((startIndex = text.indexOf(KEYWORD_DELIMITER, startIndex)) > 0)
        {
            startIndex += KEYWORD_DELIMITER.length();
            count++;
        }

        String[] keywords = new String[count];
        int endIndex = 0;
        startIndex = 0;

        for (int i = 0; i < count; i++)
        {
            endIndex = text.indexOf(KEYWORD_DELIMITER, startIndex);

            keywords[i] = text.substring(startIndex, endIndex);

            startIndex = endIndex + KEYWORD_DELIMITER.length();
        }

        String[] values;

        for (String keyword : keywords)
        {
            values = keyword.split(String.format("\\%c", ICON_DELIMITER));

            try
            {
                mKeywordList.add(new Keyword(Integer.parseInt(values[0]), values[1]));
            } catch (NumberFormatException e)
            {
                ExLog.d(e.toString());
            }
        }
    }
}
