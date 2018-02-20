package com.daily.dailyhotel.screen.home.campaigntag.gourmet;

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
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.view.DailySearchToolbarView;
import com.facebook.imagepipeline.nativecode.NativeBlurFilter;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityPlaceCampaignTagListDataBinding;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2017. 8. 4..
 */

public class GourmetCampaignTagListView //
    extends BaseDialogView<GourmetCampaignTagListView.OnEventListener, ActivityPlaceCampaignTagListDataBinding> //
    implements GourmetCampaignTagListInterface
{
    ImageView mBlurImageView;

    GourmetCampaignListAdapter mRecyclerAdapter;

    public GourmetCampaignTagListView(BaseActivity activity, OnEventListener listener)
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

        viewDataBinding.toolbarView.setOnToolbarListener(new DailySearchToolbarView.OnToolbarListener()
        {
            @Override
            public void onTitleClick()
            {
                getEventListener().onCalendarClick();
            }

            @Override
            public void onBackClick()
            {
                getEventListener().onBackClick();
            }

            @Override
            public void onSelectedRadiusPosition(int position)
            {

            }
        });

        viewDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge));

        if (mRecyclerAdapter == null)
        {
            mRecyclerAdapter = new GourmetCampaignListAdapter(getContext(), new ArrayList<>(), mOnEventListener);
        }

        if (DailyPreference.getInstance(getContext()).getTrueVRSupport() > 0)
        {
            mRecyclerAdapter.setTrueVREnabled(true);
        }

        if (Util.supportPreview(getContext()) == true)
        {
            mRecyclerAdapter.setOnLongClickListener(mOnLongClickListener);
        }

        mRecyclerAdapter.setOnWishClickListener(mOnWishClickListener);

        viewDataBinding.recyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    @Override
    public void setData(ArrayList<PlaceViewItem> placeViewItemList, GourmetBookDateTime gourmetBookDateTime)
    {
        if (mRecyclerAdapter == null)
        {
            return;
        }

        if (getViewDataBinding() != null)
        {
            int resultCount = 0;
            if (placeViewItemList != null && placeViewItemList.size() > 0)
            {
                for (PlaceViewItem placeViewItem : placeViewItemList)
                {
                    if (PlaceViewItem.TYPE_ENTRY == placeViewItem.mType)
                    {
                        resultCount++;
                    }
                }
            }

            setResultCount(resultCount);
        }

        mRecyclerAdapter.setAll(placeViewItemList);

        // 리스트를 최상단으로 다시 올린다.
        getViewDataBinding().recyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void setCalendarText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setSubTitleText(text);
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
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        if (mBlurImageView != null)
                        {
                            mBlurImageView.setBackgroundDrawable(null);
                            mBlurImageView.setVisibility(View.GONE);
                        }
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

    @Override
    public PlaceViewItem getItem(int position)
    {
        if (getViewDataBinding() == null || mRecyclerAdapter == null)
        {
            return null;
        }

        return mRecyclerAdapter.getItem(position);
    }

    @Override
    public void notifyWishChanged(int position, boolean wish)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().recyclerView.post(new Runnable()
        {
            @Override
            public void run()
            {
                GourmetCampaignListAdapter.GourmetViewHolder gourmetViewHolder = (GourmetCampaignListAdapter.GourmetViewHolder) getViewDataBinding().recyclerView.findViewHolderForAdapterPosition(position);

                if (gourmetViewHolder != null)
                {
                    gourmetViewHolder.gourmetCardView.setWish(wish);
                }
            }
        });
    }

    private void setResultCount(int count)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (count > 0)
        {
            getViewDataBinding().resultCountTextView.setText(getString(R.string.label_searchresult_resultcount, count));
            getViewDataBinding().resultCountTextView.setVisibility(View.VISIBLE);
        } else
        {
            getViewDataBinding().resultCountTextView.setVisibility(View.GONE);
        }
    }

    public interface OnEventListener extends OnBaseEventListener
    {
        void onCalendarClick();

        void onResearchClick();

        void onCallClick();

        void onPlaceClick(int position, View view, PlaceViewItem placeViewItem, int count);

        void onPlaceLongClick(int position, View view, PlaceViewItem placeViewItem, int count);

        void onWishClick(int position, PlaceViewItem placeViewItem);
    }

    private GourmetCampaignListAdapter.OnEventListener mOnEventListener = new GourmetCampaignListAdapter.OnEventListener()
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
                getEventListener().onPlaceClick(position, view, placeViewItem, mRecyclerAdapter.getItemCount());
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
                getEventListener().onPlaceLongClick(position, v, placeViewItem, mRecyclerAdapter.getItemCount());
            }

            return true;
        }
    };

    protected View.OnClickListener mOnWishClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
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
                getEventListener().onWishClick(position, placeViewItem);
            }
        }
    };
}
