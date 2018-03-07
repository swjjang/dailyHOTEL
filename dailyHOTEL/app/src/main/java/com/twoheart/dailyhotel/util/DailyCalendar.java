package com.twoheart.dailyhotel.util;

import com.daily.base.util.DailyTextUtils;

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
    public static final long DAY_MILLISECOND = 3600 * 24 * 1000;

    public static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZZZZ";

    // 하단에 나오는 포맷이외에는 지원하지 않음. 추가적으로 필요한 포맷은 동일한 포맷터 위치 뒤로 이동한다. 갯수가 많은것 부터 작은것 순서대로 넣는다. (yyyy -> yy, MM -> M)
    private static final String[] DATE_FORMATS = {"yyyy", "yy", "MM", "M", "dd", "d", "EEE", "HH", "H", "mm", "ss", "ZZZZZ"};

    public static Calendar getInstance()
    {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT+09:00"), Locale.KOREA);
    }

    public static Calendar getInstance(String dateTime, String format) throws ParseException
    {
        Calendar calendar = DailyCalendar.getInstance();
        calendar.setTime(DailyCalendar.convertDate(dateTime, format));

        return calendar;
    }

    /**
     * @param dateString    ISO-8601만 가능
     * @param isClearTField true인 경우 모든 시간 필드는 초기화 한다.
     * @return
     * @throws Exception
     */
    public static Calendar getInstance(String dateString, boolean isClearTField) throws Exception
    {
        Calendar calendar = getInstance();
        calendar.setTime(DailyCalendar.convertStringToDate(dateString));

        if (isClearTField == true)
        {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }

        return calendar;
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
        if (DailyTextUtils.isTextEmpty(dateString, srcFormat) == true)
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

    public static long clearTField(long millis)
    {
        Calendar calendar = DailyCalendar.getInstance();
        calendar.setTimeInMillis(millis);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    /**
     * 이 메소드는 기존과 동일하지만 타임존을 받지 않아서 dateString에 존재하는 Time존을 사용하도록 한다.
     *
     * @param dateString ISO-8601 포맷이 아니면 지원 하지 않음.
     * @return
     * @throws ParseException
     * @throws NullPointerException
     */
    public static Date convertStringToDate(final String dateString) throws ParseException, NullPointerException
    {
        if (DailyTextUtils.isTextEmpty(dateString) == true)
        {
            throw new NullPointerException("dateString is empty");
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DailyCalendar.ISO_8601_FORMAT, Locale.KOREA);

        return simpleDateFormat.parse(dateString);
    }

    public static Date convertDate(final String dateString, final String format, TimeZone timeZone) throws ParseException, NullPointerException
    {
        if (DailyTextUtils.isTextEmpty(dateString) == true)
        {
            throw new NullPointerException("dateString is empty");
        }

        if (DailyTextUtils.isTextEmpty(format) == true)
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

    /**
     * @param calendar
     * @param dateString ISO8601타입이 아니면 안받아줌.
     */
    public static void setCalendarDateString(Calendar calendar, String dateString) throws Exception
    {
        if (calendar == null || DailyTextUtils.isTextEmpty(dateString) == true)
        {
            return;
        }

        calendar.setTime(DailyCalendar.convertStringToDate(dateString));
    }

    public static void setCalendarDateString(Calendar calendar, String dateString, int afterDay) throws Exception
    {
        if (calendar == null || DailyTextUtils.isTextEmpty(dateString) == true)
        {
            return;
        }

        setCalendarDateString(calendar, dateString);

        calendar.add(Calendar.DAY_OF_MONTH, afterDay);
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
        if (DailyTextUtils.isTextEmpty(format) == true)
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
                    formatStringBuilder.replace(start, end, String.format(Locale.KOREA, "%04d", calendar.get(Calendar.YEAR)));
                    break;

                // yy
                case 1:
                    formatStringBuilder.replace(start, end, String.format(Locale.KOREA, "%02d", (calendar.get(Calendar.YEAR) % 100)));
                    break;

                // MM
                case 2:
                    formatStringBuilder.replace(start, end, String.format(Locale.KOREA, "%02d", (calendar.get(Calendar.MONTH) + 1)));
                    break;

                // M
                case 3:
                    formatStringBuilder.replace(start, end, String.format(Locale.KOREA, "%d", (calendar.get(Calendar.MONTH) + 1)));
                    break;

                // dd
                case 4:
                    formatStringBuilder.replace(start, end, String.format(Locale.KOREA, "%02d", calendar.get(Calendar.DAY_OF_MONTH)));
                    break;

                // d
                case 5:
                    formatStringBuilder.replace(start, end, String.format(Locale.KOREA, "%d", calendar.get(Calendar.DAY_OF_MONTH)));
                    break;

                // EEE 1 : 일, 2 : 월 ... 7 : 토
                case 6:
                    formatStringBuilder.replace(start, end, String.valueOf(weeks[calendar.get(Calendar.DAY_OF_WEEK) - 1]));
                    break;

                // HH
                case 7:
                    formatStringBuilder.replace(start, end, String.format(Locale.KOREA, "%02d", calendar.get(Calendar.HOUR_OF_DAY)));
                    break;

                // H
                case 8:
                    formatStringBuilder.replace(start, end, String.format(Locale.KOREA, "%d", calendar.get(Calendar.HOUR_OF_DAY)));
                    break;

                // mm
                case 9:
                    formatStringBuilder.replace(start, end, String.format(Locale.KOREA, "%02d", calendar.get(Calendar.MINUTE)));
                    break;

                // ss
                case 10:
                    formatStringBuilder.replace(start, end, String.format(Locale.KOREA, "%02d", calendar.get(Calendar.SECOND)));
                    break;

                // ZZZZZ
                case 11:
                    formatStringBuilder.replace(start, end, GMT9);
                    break;
            }
        }

        return formatStringBuilder.toString();
    }

    public static long compareDateTime(String dateTime1, String dateTime2) throws ParseException, NullPointerException
    {
        Date date1 = DailyCalendar.convertDate(dateTime1, ISO_8601_FORMAT, null);
        Date date2 = DailyCalendar.convertDate(dateTime2, ISO_8601_FORMAT, null);

        return date1.getTime() - date2.getTime();
    }

    public static int compareDateDay(String dateTime1, String dateTime2) throws ParseException, NullPointerException
    {
        Date date1 = DailyCalendar.convertDate(dateTime1, "yyyy-MM-dd", null);
        Date date2 = DailyCalendar.convertDate(dateTime2, "yyyy-MM-dd", null);

        return (int) ((date1.getTime() - date2.getTime()) / DAY_MILLISECOND);
    }

    /**
     * @param dateTime
     * @param weeks    [1,2,3,4,5,6,7]
     * @return
     * @throws ParseException
     */
    public static String searchClosedDayOfWeek(String dateTime, char[] weeks) throws ParseException
    {
        if (dateTime == null || weeks == null)
        {
            return null;
        }

        Calendar calendar = DailyCalendar.getInstance();
        calendar.setTime(DailyCalendar.convertDate(dateTime, DailyCalendar.ISO_8601_FORMAT));

        int findWeek = 0;

        // 만일 여기서 무한 루프 돌면 말도 안되는 현상
        while (findWeek == 0)
        {
            int currentWeek = calendar.get(Calendar.DAY_OF_WEEK);

            for (char week : weeks)
            {
                int dayOfWeek = week - '0';

                if (dayOfWeek < Calendar.SUNDAY || dayOfWeek > Calendar.SATURDAY)
                {
                    findWeek = -1;
                    break;
                }

                if (currentWeek <= dayOfWeek)
                {
                    findWeek = 1;
                    calendar.add(Calendar.DAY_OF_MONTH, dayOfWeek - currentWeek);
                    break;
                }
            }

            if (findWeek == 0)
            {
                calendar.add(Calendar.DAY_OF_MONTH, Calendar.SATURDAY - currentWeek + 1);
            }
        }

        return findWeek == 1 ? DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT) : null;
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
