package com.daily.dailyhotel.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.Sticker;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewImageLoopViewpagerDataBinding;
import com.twoheart.dailyhotel.util.Util;

import java.io.IOException;
import java.util.List;

public class DailyImageLoopView extends ConstraintLayout implements ViewPager.OnPageChangeListener
{
    DailyViewImageLoopViewpagerDataBinding mViewDataBinding;
    private ImageViewPagerAdapter mImageViewPagerAdapter;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;

    public DailyImageLoopView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyImageLoopView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyImageLoopView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_image_loop_viewpager_data, this, true);

        mViewDataBinding.imageLoopViewPager.setOnPageChangeListener(this);
        mViewDataBinding.viewpagerIndicator.setOnPageChangeListener(this);
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener)
    {
        mOnPageChangeListener = listener;
    }

    public void setImageList(List<DetailImageInformation> imageList)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (imageList == null || imageList.size() == 0)
        {
            setLineIndicatorVisible(false);
            return;
        } else if (imageList.size() == 1)
        {
            setLineIndicatorVisible(false);
        } else
        {
            setLineIndicatorVisible(true);
        }

        setCaption(imageList.get(0).caption);

        if (mImageViewPagerAdapter == null)
        {
            mImageViewPagerAdapter = new ImageViewPagerAdapter(getContext());
        }

        mImageViewPagerAdapter.setData(imageList);
        mViewDataBinding.imageLoopViewPager.setAdapter(mImageViewPagerAdapter);
        mViewDataBinding.viewpagerIndicator.setViewPager(mViewDataBinding.imageLoopViewPager);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setTransGradientTopView(String transitionName)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.transGradientTopView.setTransitionName(transitionName);
    }

    public void setStickerUrl(String url)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(url) == true)
        {
            mViewDataBinding.stickerSimpleDraweeView.setVisibility(View.GONE);
        } else
        {
            mViewDataBinding.stickerSimpleDraweeView.setVisibility(View.VISIBLE);
        }

        DraweeController controller = Fresco.newDraweeControllerBuilder().setControllerListener(new BaseControllerListener<ImageInfo>()
        {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable)
            {
                ViewGroup.LayoutParams layoutParams = mViewDataBinding.stickerSimpleDraweeView.getLayoutParams();

                int screenWidth = ScreenUtils.getScreenWidth(getContext());
                if (screenWidth > Sticker.DEFAULT_SCREEN_WIDTH && screenWidth < Sticker.LARGE_SCREEN_WIDTH)
                {
                    layoutParams.width = (int) (Sticker.MEDIUM_RATE * imageInfo.getWidth());
                    layoutParams.height = (int) (Sticker.MEDIUM_RATE * imageInfo.getHeight());
                } else
                {
                    layoutParams.width = imageInfo.getWidth();
                    layoutParams.height = imageInfo.getHeight();
                }

                mViewDataBinding.stickerSimpleDraweeView.setLayoutParams(layoutParams);
            }
        }).setUri(Uri.parse(url)).build();

        mViewDataBinding.stickerSimpleDraweeView.setController(controller);
    }

    public void setLineIndicatorVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (visible == true)
        {
            mViewDataBinding.moreIconView.setVisibility(View.VISIBLE);
            mViewDataBinding.viewpagerIndicator.setVisibility(View.VISIBLE);
        } else
        {
            mViewDataBinding.moreIconView.setVisibility(View.INVISIBLE);
            mViewDataBinding.viewpagerIndicator.setVisibility(View.INVISIBLE);
        }
    }

    public void setStickerVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.stickerSimpleDraweeView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public int getPosition()
    {
        if (mViewDataBinding == null)
        {
            return 0;
        }

        return mViewDataBinding.imageLoopViewPager.getCurrentItem();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
        if (mOnPageChangeListener != null)
        {
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position)
    {
        if (mImageViewPagerAdapter != null)
        {
            setCaption(mImageViewPagerAdapter.getImageInformation(position).caption);
        }

        if (mOnPageChangeListener != null)
        {
            mOnPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {
        if (mOnPageChangeListener != null)
        {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        mViewDataBinding.imageLoopViewPager.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    public int getPageScrollX()
    {
        if (mViewDataBinding == null)
        {
            return 0;
        }

        return mViewDataBinding.imageLoopViewPager.getScrollX();
    }

    public int getPageScrollY()
    {
        if (mViewDataBinding == null)
        {
            return 0;
        }

        return mViewDataBinding.imageLoopViewPager.getScrollY();
    }

    private void setCaption(String caption)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(caption) == false)
        {
            mViewDataBinding.descriptionTextView.setVisibility(View.VISIBLE);
            mViewDataBinding.descriptionTextView.setText(caption);
        } else
        {
            mViewDataBinding.descriptionTextView.setVisibility(View.INVISIBLE);
        }
    }

    private class ImageViewPagerAdapter extends PagerAdapter
    {
        private Context mContext;
        private List<DetailImageInformation> mImageList;

        public ImageViewPagerAdapter(Context context)
        {
            mContext = context;
        }

        public void setData(List<DetailImageInformation> imageList)
        {
            mImageList = imageList;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            final int width = ScreenUtils.getScreenWidth(mContext);
            final int height = ScreenUtils.getRatioHeightType4x3(width);
            final com.facebook.drawee.view.SimpleDraweeView imageView = new com.facebook.drawee.view.SimpleDraweeView(mContext);

            if (mImageList == null || mImageList.size() == 0 || position < 0)
            {
                imageView.setBackgroundResource(R.color.default_background);
                imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
                imageView.setTag(imageView.getId(), position);
                imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
                container.addView(imageView, 0, layoutParams);

                return imageView;
            }

            if (position < mImageList.size())
            {
                imageView.setBackgroundResource(R.color.default_background);
                imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
                imageView.setTag(imageView.getId(), position);
                imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

                DetailImageInformation detailImageInformation = mImageList.get(position);

                if (detailImageInformation == null || detailImageInformation.getImageMap() == null)
                {
                    return imageView;
                }

                ImageMap imageMap = detailImageInformation.getImageMap();
                String url;

                if (ScreenUtils.getScreenWidth(mContext) >= ScreenUtils.DEFAULT_STAYOUTBOUND_XXHDPI_WIDTH)
                {
                    if (DailyTextUtils.isTextEmpty(imageMap.bigUrl) == true)
                    {
                        url = imageMap.smallUrl;
                    } else
                    {
                        url = imageMap.bigUrl;
                    }
                } else
                {
                    if (DailyTextUtils.isTextEmpty(imageMap.mediumUrl) == true)
                    {
                        url = imageMap.smallUrl;
                    } else
                    {
                        url = imageMap.mediumUrl;
                    }
                }

                ControllerListener controllerListener = new BaseControllerListener<ImageInfo>()
                {
                    @Override
                    public void onFailure(String id, Throwable throwable)
                    {
                        if (throwable instanceof IOException == true)
                        {
                            if (url.equalsIgnoreCase(imageMap.bigUrl) == true)
                            {
                                imageMap.bigUrl = null;
                            } else if (url.equalsIgnoreCase(imageMap.mediumUrl) == true)
                            {
                                imageMap.mediumUrl = null;
                            } else
                            {
                                // 작은 이미지를 로딩했지만 실패하는 경우.
                                return;
                            }

                            if (DailyTextUtils.isTextEmpty(imageMap.smallUrl) == false)
                            {
                                imageView.setImageURI(imageMap.smallUrl);
                            }
                        }
                    }
                };

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
                container.addView(imageView, 0, layoutParams);

                DraweeController draweeController = Fresco.newDraweeControllerBuilder()//
                    .setControllerListener(controllerListener).setUri(url).build();

                imageView.setController(draweeController);
            } else
            {
                Util.restartApp(mContext);
            }

            return imageView;
        }

        @Override
        public int getItemPosition(Object object)
        {
            return POSITION_NONE;
        }

        public DetailImageInformation getImageInformation(int position)
        {
            if (mImageList == null)
            {
                return null;
            }

            return mImageList.get(position);
        }

        @Override
        public int getCount()
        {
            if (mImageList != null)
            {
                if (mImageList.size() == 0)
                {
                    return 1;
                } else
                {
                    return mImageList.size();
                }
            } else
            {
                return 1;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView((View) object);
        }
    }
}
