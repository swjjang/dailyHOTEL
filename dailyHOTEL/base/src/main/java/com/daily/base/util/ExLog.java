package com.daily.base.util;

import android.util.Log;

import com.daily.base.BuildConfig;

import java.util.Iterator;
import java.util.Map;

/**
 * 기본 Log 출력에 DB에 Debug Log 기록<BR>
 * Debug 정보를 특정 Web에 올리는 기능을 포함(DB 파일 첨부) -> 예정...
 *
 * @author pinkred
 */
public class ExLog
{
    /**
     * DEBUG
     */
    private static final boolean DEBUG = BuildConfig.DEBUG;

    /**
     * CANCEL_TAG
     */
    private static final String TAG = "DAILY";

    /**
     * 최종적으로 Debug 정보를 출력 및 DB에 저장<br>
     *
     * @param tag     CANCEL_TAG
     * @param message 메시지
     * @param method  Log.VERBOSE, Log.DEBUG, Log.INFO, Log.WARN, Log.ERROR,
     *                Log.ASSERT
     * @param caller  ClassName.MethodName() Line 000
     */
    private synchronized static void print(String tag, String message, int method, String caller)
    {
        try
        {
            if (DEBUG)
            {
                if (message == null)
                {
                    return;
                }

                String msg = caller + " : " + message;

                switch (method)
                {
                    case Log.DEBUG:
                        Log.d(tag, msg);
                        break;

                    case Log.ERROR:
                        Log.e(tag, msg);
                        break;

                    case Log.INFO:
                        Log.i(tag, msg);
                        break;

                    case Log.VERBOSE:
                        Log.v(tag, msg);
                        break;

                    case Log.WARN:
                        Log.w(tag, msg);
                        break;

                    default:
                        break;
                } // switch

                // DB에 저장
                // DBWrite(tag, message, method, caller);
                // logWriteFile(message);
            } // if
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Method //
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * <PRE>
     * 디버그
     * </PRE>
     * <p>
     * pinkred 2013. 4. 11. 오후 6:17:43
     *
     * @param message 메시지
     */
    public static void d(String message)
    {
        if (DEBUG == false)
        {
            return;
        }

        String caller = getCallerInfo(); // 호출한 Class와 Method를 찾아서 로그에 넣어준다(직전
        // Class와
        // Method)
        print(TAG, message, Log.DEBUG, caller);
    }

    /**
     * <PRE>
     * 에러
     * </PRE>
     * <p>
     * pinkred 2013. 4. 11. 오후 6:17:57
     *
     * @param message 메시지
     */
    public static void e(String message)
    {
        if (DEBUG == false)
        {
            return;
        }

        String caller = getCallerInfo(); // 호출한 Class와 Method를 찾아서 로그에 넣어준다(직전
        // Class와
        // Method)
        print(TAG, message, Log.ERROR, caller);
    }

    /**
     * <PRE>
     * 정보
     * </PRE>
     * <p>
     * pinkred 2013. 4. 11. 오후 6:18:04
     *
     * @param message 메시지
     */
    public static void i(String message)
    {
        if (DEBUG == false)
        {
            return;
        }

        String caller = getCallerInfo(); // 호출한 Class와 Method를 찾아서 로그에 넣어준다(직전
        // Class와
        // Method)
        print(TAG, message, Log.INFO, caller);
    }

    /**
     * <PRE>
     * Verbose
     * </PRE>
     * <p>
     * pinkred 2013. 4. 11. 오후 6:18:15
     *
     * @param message 메시지
     */
    public static void v(String message)
    {
        if (DEBUG == false)
        {
            return;
        }

        String caller = getCallerInfo(); // 호출한 Class와 Method를 찾아서 로그에 넣어준다(직전
        // Class와
        // Method)
        print(TAG, message, Log.VERBOSE, caller);
    }

    /**
     * <PRE>
     * 경고
     * </PRE>
     * <p>
     * pinkred 2013. 4. 11. 오후 6:18:54
     *
     * @param message 메시지
     */
    public static void w(String message)
    {
        if (DEBUG == false)
        {
            return;
        }

        String caller = getCallerInfo(); // 호출한 Class와 Method를 찾아서 로그에 넣어준다(직전
        // Class와
        // Method)
        print(TAG, message, Log.WARN, caller);
    }

    /**
     * <PRE>
     * Map Data를 Debug Print
     * </PRE>
     * <p>
     * jmkim9 2011. 10. 24. 오후 5:52:06
     *
     * @param tag    태그
     * @param title  타이틀
     * @param map    맵
     * @param method 메소드
     * @param caller 호출자
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void printMap(String tag, String title, Map map, int method, String caller)
    {
        // String message = "";

        if (!DEBUG)
        {
            return;
        }

        try
        {
            if (map.size() < 1)
            {
                return;
            }

            // inefficient use of keySet iterator instead of entrySet iterator
            // for (Map.Entry<String, String> entry : map.entrySet())
            // {
            // System.out.println(entry.getKey() + "/" + entry.getValue());
            // }

            Iterator<Map.Entry> iterator = map.entrySet().iterator();

            // Box는 120byte로 맞춤
            print(tag, "▶" + title + " ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", method, caller);
            // Log.d(tag, "▶▶▶" + title +
            // " ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
            // message += "▶▶▶" + title +
            // " ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓" +
            // "\n";

            // Performance - Inefficient use of keySet iterator instead of
            // entrySet
            // iterator
            // Plugin: findbugs Key: WMI_WRONG_MAP_ITERATOR
            // This method accesses the value of a Map entry, using a key that
            // was
            // retrieved from a keySet iterator. It is more efficient to use an
            // iterator
            // on the entrySet of the map, to avoid the Map.get(key) lookup.

            while (iterator.hasNext())
            {
                Map.Entry entry = iterator.next();

                String key = (String) entry.getKey();

                String str = key + " : " + entry.getValue().toString();
                print(tag, "▶" + title + " ┃" + str, method, caller);
                // str = str + fillString(120 - str.getBytes().length - 4, " ");
                // // 뒤에
                // Space 삽입

                // print(tag, "▶" + title + " ┃" + str + "┃", method, caller);
                // Log.d(tag, "▶▶▶" + title + " ┃" + str + "┃");
                // message += "▶▶▶" + title + " ┃" + str + "┃" + "\n";
            }

            print(tag, "▶" + title + " ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", method, caller);
            // Log.d(tag, "▶▶▶" + title +
            // " ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
            // message += "▶▶▶" + title +
            // " ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛" +
            // "\n";

            // DB에 저장(전체 Header의 내용을 한개의 row에 저장)
            // writeDB(tag, message, Log.DEBUG, caller);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    /**
     * <PRE>
     * printMap
     * </PRE>
     * <p>
     * pinkred 2013. 4. 11. 오후 6:20:34
     *
     * @param title 타이틀
     * @param map   Map
     */
    @SuppressWarnings("rawtypes")
    public static void printMap(String title, Map map)
    {
        printMap(TAG, title, map, Log.DEBUG, getCallerInfo());
    }

    /**
     * <PRE>
     * printMap
     * </PRE>
     * <p>
     * pinkred 2013. 4. 11. 오후 6:20:48
     *
     * @param title  타이틀
     * @param map    Map
     * @param method 메소드
     */
    @SuppressWarnings("rawtypes")
    public static void printMap(String title, Map map, int method)
    {
        printMap(TAG, title, map, method, getCallerInfo());
    }

    /**
     * 이전 호출한 클래스 메소드에 대한 정보를 return
     *
     * @return String 결과
     */
    private static String getCallerInfo()
    {
        // 전전 호출 메소드에 대한 정보
        // w, d, i, v, e method를 호출한 클래스, 메소드 정보
        StackTraceElement el = new Throwable().fillInStackTrace().getStackTrace()[2];
        // Class Full Name
        // String caller = el.getClassName() + "." + el.getMethodName() +
        // "() Line " +
        // el.getLineNumber();

        // Class Shot Name
        String className = el.getClassName();
        className = className.substring(className.lastIndexOf('.') + 1);

        return className + "." + el.getMethodName() + "() Line " + el.getLineNumber();
    }
}
