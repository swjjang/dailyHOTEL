package com.daily.dailyhotel.screen.home.campaigntag.stay;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.view.DailyCampaignTagTitleView;
import com.facebook.imagepipeline.nativecode.NativeBlurFilter;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityPlaceCampaignTagListDataBinding;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by iseung-won on 2017. 8. 4..
 */

public class StayCampaignTagListView //
    extends BaseDialogView<StayCampaignTagListView.OnEventListener, ActivityPlaceCampaignTagListDataBinding> //
    implements StayCampaignTagListInterface
{
    private ImageView mBlurImageView;

    private StayCampaignListAdapter mRecyclerAdapter;

    public StayCampaignTagListView(BaseActivity activity, OnEventListener listener)
    {
        super(activity, listener);
    }

    @Override
    protected void setContentView(ActivityPlaceCampaignTagListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initLayout(viewDataBinding);
    }

    private void initLayout(ActivityPlaceCampaignTagListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.campaignTitleLayout.setOnEventListener(new DailyCampaignTagTitleView.OnEventListener()
        {
            @Override
            public void onCalendarClick()
            {
                getEventListener().onCalendarClick();
            }

            @Override
            public void onBackClick()
            {
                getEventListener().onBackClick();
            }
        });

        viewDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge));

        if (mRecyclerAdapter == null)
        {
            mRecyclerAdapter = new StayCampaignListAdapter(getContext(), new ArrayList<>(), mOnEventListener);
        }

        if (DailyPreference.getInstance(getContext()).getTrueVRSupport() > 0)
        {
            mRecyclerAdapter.setTrueVREnabled(true);
        }

        if (Util.supportPreview(getContext()) == true)
        {
            mRecyclerAdapter.setOnLongClickListener(mOnLongClickListener);
        }

        viewDataBinding.recyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().campaignTitleLayout.setTitleText(title);
    }

    //    @Override
    //    public void setResultCount(int resultCount)
    //    {
    //        if (getViewDataBinding() == null)
    //        {
    //            return;
    //        }
    //
    //        getViewDataBinding().campaignTitleLayout.setResultCount(resultCount);
    //    }

    @Override
    public void setData(ArrayList<PlaceViewItem> placeViewItemList, StayBookingDay stayBookingDay)
    {
        if (mRecyclerAdapter == null)
        {
            return;
        }

        if (getViewDataBinding() != null && placeViewItemList != null && placeViewItemList.size() > 0)
        {
            int resultCount = 0;
            for (PlaceViewItem placeViewItem : placeViewItemList)
            {
                if (PlaceViewItem.TYPE_ENTRY == placeViewItem.mType)
                {
                    resultCount++;
                }
            }

            getViewDataBinding().campaignTitleLayout.setResultCount(resultCount);
        }

        mRecyclerAdapter.setPlaceBookingDay(stayBookingDay);
        mRecyclerAdapter.setAll(placeViewItemList);
        mRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void setCalendarText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().campaignTitleLayout.setCalendarText(text);
    }

    @Override
    public boolean getBlurVisibility()
    {
        if (mBlurImageView == null)
        {
            return false;
        }

        return mBlurImageView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setBlurVisibility(Activity activity, boolean visible)
    {
        if (activity == null)
        {
            return;
        }

        if (visible == true)
        {
            if (mBlurImageView == null)
            {
                mBlurImageView = new ImageView(activity);
                activity.getWindow().addContentView(mBlurImageView//
                    , new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                mBlurImageView.setImageResource(R.color.black_a40);
                mBlurImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            mBlurImageView.setVisibility(View.VISIBLE);

            Bitmap bitmap = ScreenUtils.takeScreenShot(activity);

            if (bitmap == null)
            {
                if (mBlurImageView != null)
                {
                    mBlurImageView.setBackgroundDrawable(null);
                    mBlurImageView.setVisibility(View.GONE);
                }
            } else
            {
                Observable.just(ScreenUtils.takeScreenShot(activity)).subscribeOn(Schedulers.io()).map(new Function<Bitmap, Bitmap>()
                {
                    @Override
                    public Bitmap apply(@io.reactivex.annotations.NonNull Bitmap bitmap) throws Exception
                    {
                        try
                        {
                            NativeBlurFilter.iterativeBoxBlur(bitmap, 2, 60);
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }

                        return bitmap;
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Bitmap>()
                {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Bitmap bitmap) throws Exception
                    {
                        mBlurImageView.setBackgroundDrawable(new BitmapDrawable(getContext().getResources(), bitmap));
                    }
                });
            }
        } else
        {
            if (mBlurImageView != null)
            {
                mBlurImageView.setBackgroundDrawable(null);
                mBlurImageView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setListScrollTop()
    {
        if (getViewDataBinding().recyclerView == null || getViewDataBinding().recyclerView.getChildCount() == 0)
        {
            return;
        }

        getViewDataBinding().recyclerView.scrollToPosition(0);
    }

    public interface OnEventListener extends OnBaseEventListener
    {
        void onCalendarClick();

        void onResearchClick();

        void onCallClick();

        void onPlaceClick(View view, PlaceViewItem placeViewItem, int count);

        void onPlaceLongClick(View view, PlaceViewItem placeViewItem, int count);
    }

    private StayCampaignListAdapter.OnEventListener mOnEventListener = new StayCampaignListAdapter.OnEventListener()
    {
        @Override
        public void onItemClick(View view)
        {
            if (getViewDataBinding() == null)
            {
                return;
            }

            int position = getViewDataBinding().recyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                return;
            }

            PlaceViewItem placeViewItem = mRecyclerAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                if (getEventListener() == null)
                {
                    return;
                }

                getEventListener().onPlaceClick(view, placeViewItem, mRecyclerAdapter.getItemCount());
            }
        }

        @Override
        public void onEmptyChangeDateClick()
        {
            getEventListener().onCalendarClick();
        }

        @Override
        public void onEmptyResearchClick()
        {
            getEventListener().onResearchClick();
        }

        @Override
        public void onEmptyCallClick()
        {
            getEventListener().onCallClick();
        }
    };

    private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener()
    {
        @Override
        public boolean onLongClick(View v)
        {
            if (getViewDataBinding() == null)
            {
                return false;
            }

            int position = getViewDataBinding().recyclerView.getChildAdapterPosition(v);
            if (position < 0)
            {
                return false;
            }

            PlaceViewItem placeViewItem = mRecyclerAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                if (getEventListener() == null)
                {
                    return false;
                }

                getEventListener().onPlaceLongClick(v, placeViewItem, mRecyclerAdapter.getItemCount());
            }

            return true;
        }
    };

}
