package com.daily.dailyhotel.screen.booking.detail.map;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ViewpagerColumnStayDataBinding;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;

public class PlaceBookingDetailMapViewPagerAdapter extends PagerAdapter
{
    protected Context mContext;
    private int mNights = 1;
    protected List<Place> mPlaceList;
    protected OnPlaceMapViewPagerAdapterListener mOnPlaceMapViewPagerAdapterListener;

    public interface OnPlaceMapViewPagerAdapterListener
    {
        void onPlaceClick(View view, Place place);

        void onCloseClick();
    }

    public PlaceBookingDetailMapViewPagerAdapter(Context context)
    {
        mContext = context;
        mPlaceList = new ArrayList<>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        if (mPlaceList == null || mPlaceList.size() < position)
        {
            return null;
        }

        ViewpagerColumnStayDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.viewpager_column_stay_data, container, false);

        Place place = mPlaceList.get(position);

        String address = place.addressSummary;

        if (address.indexOf('|') >= 0)
        {
            address = address.replace(" | ", "ㅣ");
        } else if (address.indexOf('l') >= 0)
        {
            address = address.replace(" l ", "ㅣ");
        }

        dataBinding.addressTextView.setText(address);
        dataBinding.nameTextView.setText(place.name);

        // 가격
        if (place.price > 0)
        {
            dataBinding.priceTextView.setVisibility(View.VISIBLE);
            dataBinding.priceTextView.setText(DailyTextUtils.getPriceFormat(mContext, place.price, false));
            dataBinding.priceTextView.setPaintFlags(dataBinding.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else
        {
            dataBinding.priceTextView.setVisibility(View.INVISIBLE);
            dataBinding.priceTextView.setText(null);
        }

        dataBinding.discountPriceTextView.setText(DailyTextUtils.getPriceFormat(mContext, place.discountPrice, false));

        // 1박인 경우 전체가격과 1박가격이 같다.
        if (mNights <= 1)
        {
            dataBinding.averageTextView.setVisibility(View.GONE);
        } else
        {
            dataBinding.averageTextView.setVisibility(View.VISIBLE);
        }

        // grade
//        dataBinding.gradeTextView.setText(mContext.getString(R.string.label_stay_outbound_filter_x_star_rate, (int) place.rating));
        if (place instanceof Stay)
        {
            dataBinding.gradeTextView.setText(((Stay) place).getGrade().getName(mContext));
            dataBinding.gradeTextView.setBackgroundResource(((Stay) place).getGrade().getColorResId());
        } else if  (place instanceof Gourmet)
        {
            Gourmet gourmet = (Gourmet) place;
            String displayCategory;

            if (DailyTextUtils.isTextEmpty(gourmet.subCategory) == false)
            {
                displayCategory = gourmet.subCategory;
            } else
            {
                displayCategory = gourmet.category;
            }

            // grade
            if (DailyTextUtils.isTextEmpty(displayCategory) == true)
            {
                dataBinding.gradeTextView.setVisibility(View.INVISIBLE);
            } else
            {
                dataBinding.gradeTextView.setVisibility(View.VISIBLE);
                dataBinding.gradeTextView.setText(displayCategory);
            }
        }

        dataBinding.ratingBar.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
        dataBinding.ratingBar.setRating(place.satisfaction);

        // Image
        dataBinding.simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

//        ImageMap imageMap = place.getImageMap();
//        String url;
//
//        if (ScreenUtils.getScreenWidth(mContext) >= ScreenUtils.DEFAULT_STAYOUTBOUND_XXHDPI_WIDTH)
//        {
//            if (DailyTextUtils.isTextEmpty(imageMap.bigUrl) == true)
//            {
//                url = imageMap.smallUrl;
//            } else
//            {
//                url = imageMap.bigUrl;
//            }
//        } else
//        {
//            if (DailyTextUtils.isTextEmpty(imageMap.mediumUrl) == true)
//            {
//                url = imageMap.smallUrl;
//            } else
//            {
//                url = imageMap.mediumUrl;
//            }
//        }
//
//        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>()
//        {
//            @Override
//            public void onFailure(String id, Throwable throwable)
//            {
//                if (throwable instanceof IOException == true)
//                {
//                    if (url.equalsIgnoreCase(imageMap.bigUrl) == true)
//                    {
//                        imageMap.bigUrl = null;
//                    } else if (url.equalsIgnoreCase(imageMap.mediumUrl) == true)
//                    {
//                        imageMap.mediumUrl = null;
//                    } else
//                    {
//                        // 작은 이미지를 로딩했지만 실패하는 경우.
//                        return;
//                    }
//
//                    dataBinding.simpleDraweeView.setImageURI(imageMap.smallUrl);
//                }
//            }
//        };
//
//        DraweeController draweeController = Fresco.newDraweeControllerBuilder()//
//            .setControllerListener(controllerListener).setUri(url).build();
//
//        dataBinding.simpleDraweeView.setController(draweeController);

        Util.requestImageResize(mContext, dataBinding.simpleDraweeView, place.imageUrl);

        dataBinding.nameTextView.setSelected(true); // Android TextView marquee bug

        dataBinding.closeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnPlaceMapViewPagerAdapterListener != null)
                {
                    mOnPlaceMapViewPagerAdapterListener.onCloseClick();
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
                    mOnPlaceMapViewPagerAdapterListener.onPlaceClick(dataBinding.getRoot(), place);
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
        if (mPlaceList != null)
        {
            return mPlaceList.size();
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

    public void setData(List<Place> list)
    {
        if (mPlaceList == null)
        {
            mPlaceList = new ArrayList<>();
        }

        mPlaceList.clear();

        if (list != null)
        {
            mPlaceList.addAll(list);
        }
    }

    public void setNights(int nights)
    {
        mNights = nights;
    }

    public Place getItem(int position)
    {
        if (mPlaceList == null || mPlaceList.size() == 0 || mPlaceList.size() <= position)
        {
            return null;
        }

        return mPlaceList.get(position);
    }

    public void clear()
    {
        if (mPlaceList == null)
        {
            return;
        }

        mPlaceList.clear();
    }

    public void setOnPlaceMapViewPagerAdapterListener(OnPlaceMapViewPagerAdapterListener listener)
    {
        mOnPlaceMapViewPagerAdapterListener = listener;
    }
}
