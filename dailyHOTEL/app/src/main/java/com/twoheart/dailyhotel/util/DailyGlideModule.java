package com.twoheart.dailyhotel.util;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.GlideModule;

public class DailyGlideModule implements GlideModule
{
    @Override
    public void applyOptions(Context context, GlideBuilder builder)
    {
        if (Util.isOverAPI11() == true && Util.getLCDWidth(context) > 800)
        {
            builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
            builder.setDiskCache(new InternalCacheDiskCacheFactory(context, 1024 * 1024 * 10));
        } else
        {
            builder.setDecodeFormat(DecodeFormat.PREFER_RGB_565);
            builder.setDiskCache(new InternalCacheDiskCacheFactory(context, 1024 * 1024 * 5));
        }
    }

    @Override
    public void registerComponents(Context context, Glide glide)
    {
    }
}
