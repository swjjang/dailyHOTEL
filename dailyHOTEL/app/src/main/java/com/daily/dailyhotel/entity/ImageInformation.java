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

public class ImageInformation extends BaseDetailImage
{
    public String caption;
    public String url;

    public ImageInformation()
    {

    }

    @Override
    public void setImage(Context context, SimpleDraweeView simpleDraweeView)
    {
        DraweeController controller;
        BaseControllerListener<ImageInfo> baseControllerListener = new BaseControllerListener<ImageInfo>()
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
        };

        if (ScreenUtils.getScreenWidth(context) >= 720)
        {
            controller = Fresco.newDraweeControllerBuilder()//
                .setControllerListener(baseControllerListener)//
                .setUri(Uri.parse(url)).build();
        } else
        {
            final int resizeWidth = 360, resizeHeight = 240;

            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))//
                .setResizeOptions(new ResizeOptions(resizeWidth, resizeHeight))//
                .build();

            controller = Fresco.newDraweeControllerBuilder()//
                .setOldController(simpleDraweeView.getController())//
                .setImageRequest(imageRequest)//
                .setControllerListener(baseControllerListener).build();
        }

        simpleDraweeView.setController(controller);
    }
}
