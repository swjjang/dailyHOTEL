package com.daily.dailyhotel.entity;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;

import com.daily.base.util.ScreenUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.HashMap;
import java.util.Map;

public class DetailImageInformation
{
    public String caption;
    ImageMap mImageMap;

    public DetailImageInformation()
    {

    }

    public void setImageMap(ImageMap imageMap)
    {
        mImageMap = imageMap;
    }

    public ImageMap getImageMap()
    {
        return mImageMap;
    }
}
