package com.twoheart.dailyhotel.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class DailyCalendar
{
    public static final long NINE_HOUR_MILLISECOND = 3600 * 9 * 1000;

    public static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZZZZ";

    // 하단에 나오는 포맷이외에는 지원하지 않음. 추가적으로 필요한 포맷은 동일한 포맷터 위치 뒤로 이동한다. 갯수가 많은것 부터 작은것 순서대로 넣는다. (yyyy -> yy, MM -> M)
    private static final String[] DATE_FORMATS = {"yyyy", "yy", "MM", "M", "dd", "d", "EEE", "HH", "mm", "ss", "ZZZZZ"};

    public static Calendar getInstance()
    {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT+09:00"), Locale.KOREA);
    }

    //    /**
    //     * yyyy-MM-dd'T'HH:mm:ssZZZZZ
    //     *
    //     * @param date
    //     * @return
    //     */
    //    public static String getISO8601Format(Date date)
    //    {
    //        if (date == null)
    //        {
    //            throw new NullPointerException("date is null");
    //        }
    //
    //        Calendar calendar = DailyCalendar.getInstance();
    //        calendar.setTimeInMillis(date.getTime());
    //
    //        int year = calendar.get(Calendar.YEAR);
    //        int month = calendar.get(Calendar.MONTH) + 1;
    //        int day = calendar.get(Calendar.DAY_OF_MONTH);
    //        int hour = calendar.get(Calendar.HOUR_OF_DAY);
    //        int minute = calendar.get(Calendar.MINUTE);
    //        int second = calendar.get(Calendar.SECOND);
    //        //        int week = calendar.get(Calendar.DAY_OF_WEEK); // 1 : 일, 2 : 월 ... 7 : 토요일
    //
    //        return String.format("%04d-%02d-%02dT%02d:%02d:%02d+09:00", year, month, day, hour, minute, second);
    //    }

    public static String convertDateFormatString(String dateString, String srcFormat, String dstFormat) throws ParseException, NullPointerException
    {
        if (Util.isTextEmpty(dateString, srcFormat) == true)
        {
            throw new NullPointerException("dateString, srcFormat is empty");
        }

        Date date = DailyCalendar.convertDate(dateString, srcFormat);

        return format(date, dstFormat);
    }

    public static Date convertDate(final String dateString, final String format) throws ParseException, NullPointerException
    {
        return convertDate(dateString, format, TimeZone.getTimeZone("GMT+09:00"));
    }

    public static Date convertDate(final String dateString, final String format, TimeZone timeZone) throws ParseException, NullPointerException
    {
        if (Util.isTextEmpty(dateString) == true)
        {
            throw new NullPointerException("dateString is empty");
        }

        if (Util.isTextEmpty(format) == true)
        {
            throw new NullPointerException("format is empty");
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.KOREA);

        if (timeZone != null)
        {
            simpleDateFormat.setTimeZone(timeZone);
        } else
        {
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+09:00"));
        }

        return simpleDateFormat.parse(dateString);
    }

    public static String format(String dateString, String format)
    {
        String convertString = null;

        try
        {
            convertString = format(DailyCalendar.convertDate(dateString, DailyCalendar.ISO_8601_FORMAT).getTime(), format, TimeZone.getTimeZone("GMT+09:00"));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return convertString;
    }

    public static String format(Date date, final String format)
    {
        return format(date.getTime(), format, TimeZone.getTimeZone("GMT+09:00"));
    }

    /**
     * 예외 케이스는 지원하지 않음.
     * 중복 허용하지 않음.
     *
     * @param milliseconds
     * @param format
     * @param timeZone
     * @return
     */
    public static String format(final long milliseconds, final String format, TimeZone timeZone)
    {
        if (Util.isTextEmpty(format) == true)
        {
            return "";
        }

        StringBuilder formatStringBuilder = new StringBuilder(format.replaceAll("\'T\'", "T"));
        TreeMap<Integer, Integer> treeMap = searchFormat(formatStringBuilder);

        if (treeMap.size() == 0)
        {
            return formatStringBuilder.toString();
        }

        final String GMT9 = "+09:00";
        final char[] weeks = {'일', '월', '화', '수', '목', '금', '토'};

        Calendar calendar = DailyCalendar.getInstance();

        if (timeZone != null)
        {
            calendar.setTimeZone(timeZone);
        }

        calendar.setTimeInMillis(milliseconds);

        int start, end;

        for (Map.Entry<Integer, Integer> entry : treeMap.descendingMap().entrySet())
        {
            start = entry.getKey();
            end = start + DATE_FORMATS[entry.getValue()].length();

            switch (entry.getValue())
            {
                // yyyy
                case 0:
                    formatStringBuilder.replace(start, end, String.format("%04d", calendar.get(Calendar.YEAR)));
                    break;

                // yy
                case 1:
                    formatStringBuilder.replace(start, end, String.format("%02d", (calendar.get(Calendar.YEAR) % 100)));
                    break;

                // MM
                case 2:
                    formatStringBuilder.replace(start, end, String.format("%02d", (calendar.get(Calendar.MONTH) + 1)));
                    break;

                // M
                case 3:
                    formatStringBuilder.replace(start, end, String.format("%d", (calendar.get(Calendar.MONTH) + 1)));
                    break;

                // dd
                case 4:
                    formatStringBuilder.replace(start, end, String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
                    break;

                // d
                case 5:
                    formatStringBuilder.replace(start, end, String.format("%d", calendar.get(Calendar.DAY_OF_MONTH)));
                    break;

                // EEE 1 : 일, 2 : 월 ... 7 : 토
                case 6:
                    formatStringBuilder.replace(start, end, String.valueOf(weeks[calendar.get(Calendar.DAY_OF_WEEK) - 1]));
                    break;

                // HH
                case 7:
                    formatStringBuilder.replace(start, end, String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)));
                    break;

                // mm
                case 8:
                    formatStringBuilder.replace(start, end, String.format("%02d", calendar.get(Calendar.MINUTE)));
                    break;

                // ss
                case 9:
                    formatStringBuilder.replace(start, end, String.format("%02d", calendar.get(Calendar.SECOND)));
                    break;

                // ZZZZZ
                case 10:
                    formatStringBuilder.replace(start, end, GMT9);
                    break;
            }
        }

        return formatStringBuilder.toString();
    }

    private static TreeMap<Integer, Integer> searchFormat(StringBuilder stringBuilder)
    {
        TreeMap<Integer, Integer> treeMap = new TreeMap<>();

        if (stringBuilder == null || stringBuilder.length() == 0)
        {
            return treeMap;
        }

        // 현재 포맷터 중에 최대 길이는 5개 이다.
        final String[] REPLACE_X = {"X", "XX", "XXX", "XXXX", "XXXXX"};
        int start, end, length;

        for (int i = 0; i < DATE_FORMATS.length; i++)
        {
            start = stringBuilder.indexOf(DATE_FORMATS[i]);

            if (start < 0)
            {
                continue;
            }

            length = DATE_FORMATS[i].length();
            end = start + length;

            stringBuilder.replace(start, end, REPLACE_X[length - 1]);

            treeMap.put(start, i);
        }

        return treeMap;
    }
}
