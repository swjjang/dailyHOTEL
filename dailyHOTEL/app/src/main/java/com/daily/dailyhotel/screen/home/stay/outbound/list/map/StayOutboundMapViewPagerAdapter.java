package com.daily.dailyhotel.screen.home.stay.outbound.list.map;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.StayOutbound;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ViewpagerColumnStayDataBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StayOutboundMapViewPagerAdapter extends PagerAdapter
{
    protected Context mContext;
    protected List<StayOutbound> mStayOutboundList;
    protected OnPlaceMapViewPagerAdapterListener mOnPlaceMapViewPagerAdapterListener;

    private boolean mNightsEnabled; // 연박 여부
    private boolean mRewardEnabled;

    public interface OnPlaceMapViewPagerAdapterListener
    {
        void onStayClick(View view, StayOutbound stayOutbound);

        void onWishClick(int position, StayOutbound stayOutbound);
    }

    public StayOutboundMapViewPagerAdapter(Context context)
    {
        mContext = context;
        mStayOutboundList = new ArrayList<>();
    }

    public void setNightsEnabled(boolean enabled)
    {
        mNightsEnabled = enabled;
    }

    public void setRewardEnabled(boolean enabled)
    {
        mRewardEnabled = enabled;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        if (mStayOutboundList == null || mStayOutboundList.size() < position)
        {
            return null;
        }

        ViewpagerColumnStayDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.viewpager_column_stay_data, container, false);

        StayOutbound stayOutbound = mStayOutboundList.get(position);

        dataBinding.addressTextView.setText(stayOutbound.locationDescription);
        dataBinding.nameTextView.setText(stayOutbound.name);
        dataBinding.wishImageView.setVectorImageResource(stayOutbound.myWish ? R.drawable.vector_navibar_ic_heart_on_strokefill : R.drawable.vector_navibar_ic_heart_off_white);

        // 가격
        if (stayOutbound.nightlyRate < stayOutbound.nightlyBaseRate)
        {
            dataBinding.priceTextView.setVisibility(View.VISIBLE);
            dataBinding.priceTextView.setText(DailyTextUtils.getPriceFormat(mContext, stayOutbound.nightlyBaseRate, false));
            dataBinding.priceTextView.setPaintFlags(dataBinding.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else
        {
            dataBinding.priceTextView.setVisibility(View.INVISIBLE);
            dataBinding.priceTextView.setText(null);
        }

        dataBinding.discountPriceTextView.setText(DailyTextUtils.getPriceFormat(mContext, stayOutbound.nightlyRate, false));

        if (mNightsEnabled == true)
        {
            dataBinding.averageTextView.setVisibility(View.VISIBLE);
        } else
        {
            dataBinding.averageTextView.setVisibility(View.GONE);
        }

        // grade
        if ((int) stayOutbound.rating == 0)
        {
            dataBinding.gradeTextView.setVisibility(View.GONE);
        } else
        {
            dataBinding.gradeTextView.setVisibility(View.VISIBLE);
            dataBinding.gradeTextView.setText(mContext.getString(R.string.label_stay_outbound_filter_x_star_rate, (int) stayOutbound.rating));
        }

        // Image
        dataBinding.simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

        ImageMap imageMap = stayOutbound.getImageMap();
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

        // Reward 스티커
        dataBinding.stickerImageView.setVisibility((mRewardEnabled && stayOutbound.provideRewardSticker) ? View.VISIBLE : View.GONE);

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

                    dataBinding.simpleDraweeView.setImageURI(imageMap.smallUrl);
                }
            }
        };

        DraweeController draweeController = Fresco.newDraweeControllerBuilder()//
            .setControllerListener(controllerListener).setUri(url).build();

        dataBinding.simpleDraweeView.setController(draweeController);

        dataBinding.nameTextView.setSelected(true); // Android TextView marquee bug

        dataBinding.wishImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnPlaceMapViewPagerAdapterListener != null)
                {
                    mOnPlaceMapViewPagerAdapterListener.onWishClick(position, stayOutbound);
                }
            }
        });

        dataBinding.simpleDraweeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnPlaceMapViewPagerAdapterListener != null)
                {
                    mOnPlaceMapViewPagerAdapterListener.onStayClick(dataBinding.getRoot(), stayOutbound);
                }
            }
        });

        container.addView(dataBinding.getRoot(), 0);

        return dataBinding.getRoot();
    }

    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }

    @Override
    public int getCount()
    {
        if (mStayOutboundList != null)
        {
            return mStayOutboundList.size();
        } else
        {
            return 0;
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

    public void setData(List<StayOutbound> list)
    {
        if (mStayOutboundList == null)
        {
            mStayOutboundList = new ArrayList<>();
        }

        mStayOutboundList.clear();

        if (list != null)
        {
            mStayOutboundList.addAll(list);
        }
    }

    public StayOutbound getItem(int position)
    {
        if (mStayOutboundList == null || mStayOutboundList.size() == 0 || mStayOutboundList.size() <= position)
        {
            return null;
        }

        return mStayOutboundList.get(position);
    }

    public void clear()
    {
        if (mStayOutboundList == null)
        {
            return;
        }

        mStayOutboundList.clear();
    }

    public void setOnPlaceMapViewPagerAdapterListener(OnPlaceMapViewPagerAdapterListener listener)
    {
        mOnPlaceMapViewPagerAdapterListener = listener;
    }
}
