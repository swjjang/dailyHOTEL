/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twoheart.dailyhotel.util;

import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;

public class DrawableLruCache extends LruCache<String, Drawable>
{
    private static DrawableLruCache mBitmapLruCache;

    public synchronized static DrawableLruCache getInstance()
    {
        if (mBitmapLruCache == null)
        {
            mBitmapLruCache = new DrawableLruCache();
        }

        return mBitmapLruCache;
    }

    private DrawableLruCache()
    {
        this(3);
    }

    private DrawableLruCache(int sizeInKiloBytes)
    {
        super(sizeInKiloBytes);
    }
}