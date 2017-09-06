package com.daily.dailyhotel.screen.common.images;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.BaseDetailImage;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.entity.StayOutboundDetailImage;
import com.daily.dailyhotel.parcel.DetailImageInformationParcel;
import com.daily.dailyhotel.parcel.StayOutboundDetailImageParcel;
import com.twoheart.dailyhotel.R;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ImageListActivity extends BaseActivity<ImageListPresenter>
{
    static final String INTENT_EXTRA_DATA_TITLE = "title";
    static final String INTENT_EXTRA_DATA_IMAGE_LIST = "imageList";
    static final String INTENT_EXTRA_DATA_INDEX = "index";
    static final String INTENT_EXTRA_DATA_CALL_BY_SCREEN = "callByScreen";



    public static Intent newInstance(Context context, String title, List<? extends BaseDetailImage> arrayList
        , int index, String callByScreen)
    {
        Intent intent = new Intent(context, ImageListActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);

        ParameterizedType stringListType = (ParameterizedType) arrayList.getClass().getGenericSuperclass();
        Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];

//
//
        ArrayList<StayOutboundDetailImageParcel> imageList = new ArrayList<>();
//
//        if (arrayList.getClass(). != null)
//        {
//            for (StayOutboundDetailImage stayOutboundDetailImage : arrayList)
//            {
//                imageList.add(new StayOutboundDetailImageParcel(stayOutboundDetailImage));
//            }
//        }

        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_LIST, imageList);
        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(INTENT_EXTRA_DATA_CALL_BY_SCREEN, callByScreen);

        return intent;
    }

//    public static Intent newInstance(Context context, String title, List<DetailImageInformation> arrayList, int index)
//    {
//        Intent intent = new Intent(context, ImageListActivity.class);
//        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);
//
//        ArrayList<DetailImageInformationParcel> imageList = new ArrayList<>();
//
//        if (arrayList != null)
//        {
//            for (DetailImageInformation detailImageInformation : arrayList)
//            {
//                imageList.add(new DetailImageInformationParcel(detailImageInformation));
//            }
//        }
//
//        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_LIST, imageList);
//        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
//
//        return intent;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected ImageListPresenter createInstancePresenter()
    {
        return new ImageListPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();
    }
}
