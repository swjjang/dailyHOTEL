package com.daily.dailyhotel.entity;

import android.content.Context;
import android.graphics.drawable.Animatable;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import java.io.IOException;

public class StayOutboundDetailImage extends BaseDetailImage
{
    private ImageMap mImageMap;

    public StayOutboundDetailImage()
    {

    }

    @Override
    public void setImage(Context context, SimpleDraweeView simpleDraweeView)
    {
        if (context == null || simpleDraweeView == null)
        {
            return;
        }

        String url;

        if (ScreenUtils.getScreenWidth(context) >= ScreenUtils.DEFAULT_STAYOUTBOUND_XXHDPI_WIDTH)
        {
            if (DailyTextUtils.isTextEmpty(mImageMap.bigUrl) == true)
            {
                url = mImageMap.smallUrl;
            } else
            {
                url = mImageMap.bigUrl;
            }
        } else
        {
            if (DailyTextUtils.isTextEmpty(mImageMap.mediumUrl) == true)
            {
                url = mImageMap.smallUrl;
            } else
            {
                url = mImageMap.mediumUrl;
            }
        }

        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>()
        {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable)
            {
                if (imageInfo == null)
                {
                    return;
                }

                setImageViewHeight(context, simpleDraweeView, imageInfo.getWidth(), imageInfo.getHeight());
            }

            @Override
            public void onFailure(String id, Throwable throwable)
            {
                if (throwable instanceof IOException == true)
                {
                    if (url.equalsIgnoreCase(mImageMap.bigUrl) == true)
                    {
                        mImageMap.bigUrl = null;
                    } else if (url.equalsIgnoreCase(mImageMap.mediumUrl) == true)
                    {
                        mImageMap.mediumUrl = null;
                    } else
                    {
                        // 작은 이미지를 로딩했지만 실패하는 경우.
                        return;
                    }

                    simpleDraweeView.setImageURI(mImageMap.smallUrl);
                }
            }
        };

        DraweeController draweeController = Fresco.newDraweeControllerBuilder()//
            .setControllerListener(controllerListener).setUri(url).build();

        simpleDraweeView.setController(draweeController);
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
