package com.twoheart.dailyhotel.util;

import android.os.Debug;
import android.util.Log;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

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
    private static final boolean DEBUG = Constants.DEBUG;

    /**
     * TAG
     */
    private static final String TAG = "DAILYHOTEL";

    /**
     * 최종적으로 Debug 정보를 출력 및 DB에 저장<br>
     *
     * @param tag     Tag
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

                    case Log.ASSERT:
                        Log.println(0, tag, msg);
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
     * print
     * </PRE>
     * <p/>
     * pinkred 2013. 4. 11. 오후 6:17:07
     *
     * @param format 포맷
     * @param args   Args
     */
    public static void print(String format, Object... args)
    {

        String message = "";
        try
        {
            message = String.format(format, args);
        } catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
        }

        print(TAG, message, Log.DEBUG, getCallerInfo());
    }

    /**
     * <PRE>
     * print
     * </PRE>
     * <p/>
     * pinkred 2013. 4. 11. 오후 6:17:24
     *
     * @param message 메시지
     */
    public static void print(String message)
    {
        print(TAG, message, Log.DEBUG, getCallerInfo());
    }

    /**
     * <PRE>
     * print
     * </PRE>
     * <p/>
     * pinkred 2013. 4. 11. 오후 6:17:33
     *
     * @param message 메시지
     * @param method  메소드
     */
    public static void print(String message, int method)
    {
        print(TAG, message, method, getCallerInfo());
    }

    /**
     * <PRE>
     * 디버그
     * </PRE>
     * <p/>
     * pinkred 2013. 4. 11. 오후 6:17:43
     *
     * @param message 메시지
     */
    public static void d(String message)
    {
        String caller = getCallerInfo(); // 호출한 Class와 Method를 찾아서 로그에 넣어준다(직전
        // Class와
        // Method)
        print(TAG, message, Log.DEBUG, caller);
    }

    public static void d(String prefix, String[] message)
    {
        if (null == message)
        {
            return;
        }

        String caller = getCallerInfo(); // 호출한 Class와 Method를 찾아서 로그에 넣어준다(직전
        // Class와
        // Method)
        int length = message.length;

        for (int i = 0; i < length; i++)
        {
            print(TAG, prefix + " : " + message[i], Log.DEBUG, caller);
        }
    }

    /**
     * <PRE>
     * 에러
     * </PRE>
     * <p/>
     * pinkred 2013. 4. 11. 오후 6:17:57
     *
     * @param message 메시지
     */
    public static void e(String message)
    {

        String caller = getCallerInfo(); // 호출한 Class와 Method를 찾아서 로그에 넣어준다(직전
        // Class와
        // Method)
        print(TAG, message, Log.ERROR, caller);
    }

    /**
     * <PRE>
     * 정보
     * </PRE>
     * <p/>
     * pinkred 2013. 4. 11. 오후 6:18:04
     *
     * @param message 메시지
     */
    public static void i(String message)
    {
        String caller = getCallerInfo(); // 호출한 Class와 Method를 찾아서 로그에 넣어준다(직전
        // Class와
        // Method)
        print(TAG, message, Log.INFO, caller);
    }

    /**
     * <PRE>
     * Verbose
     * </PRE>
     * <p/>
     * pinkred 2013. 4. 11. 오후 6:18:15
     *
     * @param message 메시지
     */
    public static void v(String message)
    {
        String caller = getCallerInfo(); // 호출한 Class와 Method를 찾아서 로그에 넣어준다(직전
        // Class와
        // Method)
        print(TAG, message, Log.VERBOSE, caller);
    }

    /**
     * <PRE>
     * 경고
     * </PRE>
     * <p/>
     * pinkred 2013. 4. 11. 오후 6:18:54
     *
     * @param message 메시지
     */
    public static void w(String message)
    {
        String caller = getCallerInfo(); // 호출한 Class와 Method를 찾아서 로그에 넣어준다(직전
        // Class와
        // Method)
        print(TAG, message, Log.WARN, caller);
    }

    /**
     * <PRE>
     * Map Data를 Debug Print
     * </PRE>
     * <p/>
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
     * <p/>
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
     * <p/>
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
     * <PRE>
     * printJSON
     * </PRE>
     * <p/>
     * pinkred 2013. 4. 11. 오후 6:21:37
     *
     * @param json JSONObject
     */
    public static void printJSON(JSONObject json)
    {
        if (json != null)
        {
            printJSON(json.toString(), Log.INFO);
        }
    }

    /**
     * <PRE>
     * printJSON
     * </PRE>
     * <p/>
     * pinkred 2013. 4. 11. 오후 6:21:47
     *
     * @param json   JSONObject
     * @param method 메소드
     */
    public static void printJSON(JSONObject json, int method)
    {
        if (json != null)
        {
            printJSON(json.toString(), method);
        }
    }

    /**
     * <PRE>
     * printJSON
     * </PRE>
     * <p/>
     * pinkred 2013. 4. 11. 오후 6:21:56
     *
     * @param json String
     */
    public static void printJSON(String json)
    {
        printJSON(json, Log.INFO);
    }

    /**
     * <PRE>
     * JSON String을 포맷하여 여러줄에 출력
     * </PRE>
     * <p/>
     * jmkim9 2011. 12. 1. 오후 5:43:58
     *
     * @param message 메시지
     * @param method  메소드
     */
    public static void printJSON(String message, int method)
    {
        synchronized (ExLog.class)
        {
            String token = "\n";

            // Line별로 DB에 저장시 속도 저하로 한번에 DB에 저장한다음 Line별로 화면에 출력
            if (!DEBUG)
            {
                return;
            }

            try
            {
                String str = getPretty(message);

                StringTokenizer st = new StringTokenizer(str, token); // token으로
                // 스트링을 자른다
                while (st.hasMoreTokens()) // 토큰이 더 있을동안
                {
                    // print(tag, st.nextToken(), Log.DEBUG, caller);
                    // Log.d(tag, st.nextToken());
                    print(TAG, st.nextToken(), method, getCallerInfo());

                }
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }
    }

    public static String getPretty(String jsonString)
    {

        final String INDENT = "    ";
        StringBuffer prettyJsonSb = new StringBuffer();

        int indentDepth = 0;
        String targetString = null;
        for (int i = 0; i < jsonString.length(); i++)
        {
            targetString = jsonString.substring(i, i + 1);
            if (targetString.equals("{") || targetString.equals("["))
            {
                prettyJsonSb.append(targetString).append('\n');
                indentDepth++;
                for (int j = 0; j < indentDepth; j++)
                {
                    prettyJsonSb.append(INDENT);
                }
            } else if (targetString.equals("}") || targetString.equals("]"))
            {
                prettyJsonSb.append('\n');
                indentDepth--;
                for (int j = 0; j < indentDepth; j++)
                {
                    prettyJsonSb.append(INDENT);
                }
                prettyJsonSb.append(targetString);
            } else if (targetString.equals(","))
            {
                prettyJsonSb.append(targetString);
                prettyJsonSb.append('\n');
                for (int j = 0; j < indentDepth; j++)
                {
                    prettyJsonSb.append(INDENT);
                }
            } else
            {
                prettyJsonSb.append(targetString);
            }

        }

        return prettyJsonSb.toString();

    }

    /**
     * <PRE>
     * printMemory
     * </PRE>
     * <p/>
     * pinkred 2013. 4. 11. 오후 6:22:21
     */
    @SuppressWarnings("unused")
    public static void printMemory()
    {
        // if(!_DEBUG_)
        // {
        // return;
        // }

        try
        {
            final double MB = 1024.0 * 1024.0; // 1048576.0
            DecimalFormat df = new DecimalFormat("##0.000");
            String percent = df.format(0.023); // 결과 percent : 2.3%
            // VM에서 사용 가능한 최대 메모리
            print(TAG, "-----------------------------------------------------------------", Log.DEBUG, getCallerInfo()); // 전체 메모리 크기
            print(TAG, "MaxMemory\t:\t" + df.format((Runtime.getRuntime().maxMemory() / MB)) + " MB", Log.DEBUG, getCallerInfo()); // App에서 사용
            // 가능한 최대
            // 메모리
            print(TAG, "TotalMemory\t:\t" + df.format((Runtime.getRuntime().totalMemory() / MB)) + " MB", Log.DEBUG, getCallerInfo());
            print(TAG, "FreeMemory\t:\t" + df.format((Runtime.getRuntime().freeMemory() / MB)) + " MB", Log.DEBUG, getCallerInfo()); // 전체
            // 메모리
            // 크기
            print(TAG, "-----------------------------------------------------------------", Log.DEBUG, getCallerInfo()); // 할당된 메모리 크기
            print(TAG, "NativeHeapSize\t\t:\t" + df.format((Debug.getNativeHeapSize() / MB)) + " MB", Log.DEBUG, getCallerInfo()); // 할당되지 않은 메모리
            // 크기
            print(TAG, "NativeHeapAllocated\t:\t" + df.format((Debug.getNativeHeapAllocatedSize() / MB)) + " MB", Log.DEBUG, getCallerInfo());
            print(TAG, "NativeHeapFreeSize\t:\t" + df.format((Debug.getNativeHeapFreeSize() / MB)) + " MB", Log.DEBUG, getCallerInfo());
            print(TAG, "-----------------------------------------------------------------", Log.DEBUG, getCallerInfo());
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
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
