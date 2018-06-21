package com.twoheart.dailyhotel.network.factory;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.ExLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * https://github.com/marcinOz/Retrofit2JSONConverterFactory
 * <p>
 * Created by MarcinOz on 2016-04-21.
 * Copyright (C) 2016 OKE Poland Sp. z o.o. All rights reserved.
 */
public class JSONResponseBodyConverters
{
    private JSONResponseBodyConverters()
    {
    }

    static final class JSONObjectResponseBodyConverter implements Converter<ResponseBody, JSONObject>
    {
        static final JSONObjectResponseBodyConverter INSTANCE = new JSONObjectResponseBodyConverter();

        @Override
        public JSONObject convert(ResponseBody value) throws IOException
        {
            try
            {
                return new JSONObject(value.string());
            } catch (JSONException e)
            {
                ExLog.e(e.toString());
                Crashlytics.log("JSONObject : " + value.string());
                Crashlytics.logException(e);
            }
            return null;
        }
    }

    static final class JSONArrayResponseBodyConverter implements Converter<ResponseBody, JSONArray>
    {
        static final JSONArrayResponseBodyConverter INSTANCE = new JSONArrayResponseBodyConverter();

        @Override
        public JSONArray convert(ResponseBody value) throws IOException
        {
            try
            {
                return new JSONArray(value.string());
            } catch (JSONException e)
            {
                ExLog.e(e.toString());
                Crashlytics.logException(e);
            }
            return null;
        }
    }
}
