/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twoheart.dailyhotel.util;

import android.support.v4.util.LruCache;

public class FileLruCache extends LruCache<String, String>
{
    private static FileLruCache mInstance;

    public synchronized static FileLruCache getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new FileLruCache();
        }

        return mInstance;
    }

    private FileLruCache()
    {
        // 개당 1씩으로 계산된다. 50개까지 저장함
        this(100);
    }

    private FileLruCache(int sizeInKiloBytes)
    {
        super(sizeInKiloBytes);
    }
}