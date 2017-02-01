package com.twoheart.dailyhotel.screen.search.collection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.model.RecommendationPlace;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.PinnedSectionRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class CollectionBaseActivity extends BaseActivity
{
    protected static final String INTENT_EXTRA_DATA_INDEX = "index";
    protected static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";
    protected static final String INTENT_EXTRA_DATA_TITLE = "title";
    protected static final String INTENT_EXTRA_DATA_SUBTITLE = "subTitle";

    PinnedSectionRecyclerView mRecyclerView;
    PlaceListAdapter mPlaceListAdapter;
    private TextView mCalendarTextView;
    protected SaleTime mStartSaleTime, mEndSaleTime;
    int mRecommendationIndex;
    private int mScrollState;
    private int mTitleLayoutHeight;

    protected abstract void initIntentTime(Intent intent);

    protected abstract void requestRecommendationPlaceList();

    protected abstract PlaceListAdapter getPlaceListAdapter(View.OnClickListener listener);

    protected abstract void onPlaceClick(View view, PlaceViewItem placeViewItem, int count);

    protected abstract String getCalendarDate();

    protected abstract void onCalendarClick();

    protected abstract void onCalendarActivityResult(int resultCode, Intent data);

    protected abstract String getSectionTitle(int count);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_collection_search);

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
            return;
        }

        initIntentTime(intent);

        mRecommendationIndex = intent.getIntExtra(INTENT_EXTRA_DATA_INDEX, -1);
        String title = intent.getStringExtra(INTENT_EXTRA_DATA_TITLE);
        String subTitle = intent.getStringExtra(INTENT_EXTRA_DATA_SUBTITLE);
        String imageUrl = intent.getStringExtra(INTENT_EXTRA_DATA_IMAGE_URL);

        if (mRecommendationIndex <= 0)
        {
            finish();
            return;
        }

        if (Util.isUsedMultiTransition() == true)
        {
            initLayout(title, subTitle, imageUrl);

            initTransition();
        } else
        {
            initLayout(title, subTitle, imageUrl);

            lockUI();

            requestCommonDateTime();
        }
    }

    private void requestCommonDateTime()
    {
        DailyMobileAPI.getInstance(this).requestCommonDateTime(mNetworkTag, new Callback<JSONObject>()
        {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
            {
                if (response != null && response.isSuccessful() && response.body() != null)
                {
                    try
                    {
                        JSONObject responseJSONObject = response.body();

                        int msgCode = responseJSONObject.getInt("msgCode");

                        if (msgCode == 100)
                        {
                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                            long currentDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("currentDateTime"), DailyCalendar.ISO_8601_FORMAT);
                            long dailyDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("dailyDateTime"), DailyCalendar.ISO_8601_FORMAT);

                            onCommonDateTime(currentDateTime, dailyDateTime);
                        } else
                        {
                            String message = responseJSONObject.getString("msg");
                            onErrorPopupMessage(msgCode, message);
                        }
                    } catch (Exception e)
                    {
                        onError(e);
                    }
                } else
                {
                    onErrorResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                onError(t);
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.RECOMMEND_LIST, null);
    }

    private void initLayout(String title, String subTitle, String titleImageUrl)
    {
        // 백이미지
        final View backImageView = findViewById(R.id.backImageView);
        backImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        final View imageViewLayout = findViewById(R.id.imageViewLayout);

        // 16:9 높이
        int height16x9 = Util.getRatioHeightType16x9(Util.getLCDWidth(this));

        // 이미지
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) imageViewLayout.findViewById(R.id.simpleDraweeView);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height16x9);
        simpleDraweeView.setLayoutParams(layoutParams);

        Util.requestImageResize(this, simpleDraweeView, titleImageUrl);

        final FrameLayout titleBoxLayout = (FrameLayout) findViewById(R.id.titleBoxLayout);
        titleBoxLayout.setPadding(Util.dpToPx(this, 15), Util.dpToPx(this, 171), Util.dpToPx(this, 15), 0);

        final View fakeBackImageView = titleBoxLayout.findViewById(R.id.fakeBackImageView);
        final View subTitleLayout = findViewById(R.id.subTitleLayout);

        final TextView titleTextView = (TextView) titleBoxLayout.findViewById(R.id.titleTextView);
        final TextView subTitleTextView = (TextView) subTitleLayout.findViewById(R.id.subTitleTextView);

        titleTextView.setText(title);
        subTitleTextView.setText(subTitle);

        mCalendarTextView = (TextView) subTitleLayout.findViewById(R.id.calendarTextView);
        mCalendarTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onCalendarClick();
            }
        });

        // 리스트
        mRecyclerView = (PinnedSectionRecyclerView) findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new ScrollingLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setShadowVisible(false);

        if (mPlaceListAdapter == null)
        {
            mPlaceListAdapter = getPlaceListAdapter(mOnItemClickListener);
        }

        mRecyclerView.setAdapter(mPlaceListAdapter);

        titleBoxLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                mTitleLayoutHeight = titleBoxLayout.getHeight() - titleBoxLayout.getPaddingTop() - titleBoxLayout.getPaddingBottom();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            private final int dp15Height = Util.dpToPx(CollectionBaseActivity.this, 15);
            private final int dp3Height = Util.dpToPx(CollectionBaseActivity.this, 3);
            private final int dp20Height = Util.dpToPx(CollectionBaseActivity.this, 20);
            private final int dp21Height = Util.dpToPx(CollectionBaseActivity.this, 21);
            private final int dp41Height = Util.dpToPx(CollectionBaseActivity.this, 41);
            private final int dp30Height = Util.dpToPx(CollectionBaseActivity.this, 30);
            private final int dp44Height = Util.dpToPx(CollectionBaseActivity.this, 44);
            private final int dp171Height = Util.dpToPx(CollectionBaseActivity.this, 171);

            private int mDragDistance;
            private boolean mIsFirstCollapse;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                int position = linearLayoutManager.findFirstVisibleItemPosition();

                switch (mScrollState)
                {
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        mDragDistance += dy;
                        break;
                }

                if (position == 0)
                {
                    mIsFirstCollapse = false;

                    View firstView = linearLayoutManager.findViewByPosition(position);
                    final float value = (float) firstView.getBottom() / firstView.getHeight();

                    imageViewLayout.setTranslationY(-imageViewLayout.getHeight() * (1.0f - value) / 2);

                    // 높이 조절 / 폭 조절
                    final int startAnimationHeight = mTitleLayoutHeight - dp44Height + dp15Height;

                    if (firstView.getBottom() >= startAnimationHeight)
                    {
                        float titleLayoutValue = (float) (firstView.getBottom() - startAnimationHeight) / (firstView.getHeight() - startAnimationHeight);

                        float titleLayoutTopPaddingValue = dp171Height - (firstView.getHeight() - startAnimationHeight) * (1.0f - titleLayoutValue);
                        titleBoxLayout.setPadding(dp15Height, (int) titleLayoutTopPaddingValue, dp15Height, dp15Height);

                        FrameLayout.LayoutParams fakeBackImageLayoutParams = (FrameLayout.LayoutParams) fakeBackImageView.getLayoutParams();
                        fakeBackImageLayoutParams.topMargin = -dp171Height;
                        fakeBackImageLayoutParams.leftMargin = -dp15Height;

                        titleTextView.setTranslationX(0);
                        titleTextView.setTranslationY(0);

                        subTitleLayout.setPadding(0, dp41Height, 0, 0);
                        subTitleTextView.setAlpha(1.0f);

                        titleBoxLayout.setTranslationY(-dp21Height);
                    } else
                    {
                        final float titleLayoutValue = (float) firstView.getBottom() / startAnimationHeight;
                        final float titleLayoutTopPaddingValue = (dp171Height - (firstView.getHeight() - startAnimationHeight)) * titleLayoutValue;
                        final int titleLayoutPaddingValue = (int) (titleLayoutValue * dp15Height);

                        titleBoxLayout.setPadding(titleLayoutPaddingValue, (int) titleLayoutTopPaddingValue, titleLayoutPaddingValue, 0);
                        titleBoxLayout.setTranslationY(-dp21Height * titleLayoutValue);

                        FrameLayout.LayoutParams fakeBackImageLayoutParams = (FrameLayout.LayoutParams) fakeBackImageView.getLayoutParams();
                        fakeBackImageLayoutParams.topMargin = -titleBoxLayout.getPaddingTop() - (int) titleBoxLayout.getTranslationY();
                        fakeBackImageLayoutParams.leftMargin = -titleLayoutPaddingValue;

                        subTitleLayout.setPadding(0, dp20Height + (int) (dp21Height * titleLayoutValue), 0, 0);

                        if (titleBoxLayout.getPaddingTop() < 5)
                        {
                            titleBoxLayout.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.white));
                        } else
                        {
                            titleBoxLayout.getChildAt(0).setBackgroundResource(R.drawable.home_pick_bg);
                        }

                        int titleTranslationX = fakeBackImageView.getRight() - titleTextView.getLeft();
                        titleTextView.setTranslationX(titleTranslationX * (1.0f - titleLayoutValue));
                        titleTextView.setTranslationY(-dp3Height * (1.0f - titleLayoutValue));

                        float alphaValue = titleLayoutValue / 4;
                        if (alphaValue > 1.0f)
                        {
                            alphaValue = 1.0f;
                        } else if (alphaValue < 0.0f)
                        {
                            alphaValue = 0.0f;
                        }
                        subTitleTextView.setAlpha(alphaValue);
                    }
                } else
                {
                    setDefaultCollapseValue();
                }
            }

            private void setDefaultCollapseValue()
            {
                if (mIsFirstCollapse == true)
                {
                    return;
                }

                mIsFirstCollapse = true;

                titleBoxLayout.setPadding(0, 0, 0, 0);
                titleBoxLayout.setTranslationY(0);
                titleBoxLayout.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.white));

                FrameLayout.LayoutParams fakeBackImageLayoutParams = (FrameLayout.LayoutParams) fakeBackImageView.getLayoutParams();
                fakeBackImageLayoutParams.topMargin = 0;
                fakeBackImageLayoutParams.leftMargin = 0;

                titleTextView.setTranslationX(fakeBackImageView.getRight() - titleTextView.getLeft());
                titleTextView.setTranslationY(-dp3Height);

                subTitleTextView.setAlpha(0.0f);
                subTitleLayout.setPadding(0, dp20Height, 0, 0);

                imageViewLayout.setTranslationY(-imageViewLayout.getHeight() / 2);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                mScrollState = newState;

                int position = linearLayoutManager.findFirstVisibleItemPosition();

                if (position == 0)
                {
                    switch (newState)
                    {
                        case RecyclerView.SCROLL_STATE_IDLE:
                            if (mDragDistance > 0)
                            {
                                if (mDragDistance > dp30Height)
                                {
                                    recyclerView.smoothScrollToPosition(1);
                                } else
                                {
                                    recyclerView.smoothScrollToPosition(0);
                                }
                            } else if (mDragDistance < 0)
                            {
                                if (Math.abs(mDragDistance) < dp30Height)
                                {
                                    recyclerView.smoothScrollToPosition(1);
                                } else
                                {
                                    recyclerView.smoothScrollToPosition(0);
                                }
                            }

                            mDragDistance = 0;
                            break;

                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            break;

                        case RecyclerView.SCROLL_STATE_SETTLING:
                            break;
                    }
                } else
                {
                    switch (newState)
                    {
                        case RecyclerView.SCROLL_STATE_IDLE:
                            if (position == 1)
                            {
                                setDefaultCollapseValue();
                            }

                        case RecyclerView.SCROLL_STATE_DRAGGING:
                        case RecyclerView.SCROLL_STATE_SETTLING:
                            mDragDistance = 0;
                            break;
                    }
                }
            }
        });
    }

    private void initTransition()
    {
        if (Util.isUsedMultiTransition() == true)
        {
            TransitionSet intransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);

            getWindow().setSharedElementEnterTransition(intransitionSet);

            TransitionSet outTransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);
            outTransitionSet.setDuration(200);

            getWindow().setSharedElementReturnTransition(outTransitionSet);
            intransitionSet.addListener(new Transition.TransitionListener()
            {
                @Override
                public void onTransitionStart(Transition transition)
                {

                }

                @Override
                public void onTransitionEnd(Transition transition)
                {
                    lockUI();

                    requestCommonDateTime();
                }

                @Override
                public void onTransitionCancel(Transition transition)
                {

                }

                @Override
                public void onTransitionPause(Transition transition)
                {

                }

                @Override
                public void onTransitionResume(Transition transition)
                {

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockUI();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_STAY_DETAIL:
            case CODE_REQUEST_ACTIVITY_GOURMET_DETAIL:
            {
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY:
                        setResult(resultCode);
                        finish();
                        break;

                    case CODE_RESULT_ACTIVITY_REFRESH:
                        lockUI();

                        requestRecommendationPlaceList();
                        break;
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_CALENDAR:
                onCalendarActivityResult(resultCode, data);

                lockUI();

                requestRecommendationPlaceList();
                break;
        }
    }

    private void onCommonDateTime(long currentDateTime, long dailyDateTime)
    {
        mStartSaleTime = new SaleTime();
        mStartSaleTime.setCurrentTime(currentDateTime);
        mStartSaleTime.setDailyTime(dailyDateTime);

        mEndSaleTime = mStartSaleTime.getClone(1);

        mCalendarTextView.setText(getCalendarDate());

        requestRecommendationPlaceList();
    }

    protected ArrayList<PlaceViewItem> makePlaceList(String imageBaseUrl, List<? extends RecommendationPlace> placeList)
    {
        ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<>();

        // 빈공간
        placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_HEADER_VIEW, null));

        if (placeList == null || placeList.size() == 0)
        {
            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, null));
        } else
        {
            // 개수 넣기
            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_SECTION, getSectionTitle(placeList.size())));

            for (RecommendationPlace place : placeList)
            {
                place.imageUrl = imageBaseUrl + place.imageUrl;
                placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, place));
            }
        }

        return placeViewItemList;
    }

    protected void onPlaceList(String imageBaseUrl, ArrayList<? extends RecommendationPlace> list)
    {
        if (isFinishing() == true)
        {
            unLockUI();
            return;
        }

        ArrayList<PlaceViewItem> placeViewItems = makePlaceList(imageBaseUrl, list);

        mPlaceListAdapter.setAll(placeViewItems);
        mPlaceListAdapter.notifyDataSetChanged();

        unLockUI();
    }

    protected View.OnClickListener mOnItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int position = mRecyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                return;
            }

            PlaceViewItem placeViewItem = mPlaceListAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                onPlaceClick(view, placeViewItem, mPlaceListAdapter.getItemCount());
            }
        }
    };

    private class ScrollingLinearLayoutManager extends LinearLayoutManager
    {
        public ScrollingLinearLayoutManager(Context context, int orientation, boolean reverseLayout)
        {
            super(context, orientation, reverseLayout);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position)
        {
            LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext())
            {
                @Override
                public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference)
                {
                    switch (snapPreference)
                    {
                        case SNAP_TO_START:
                            return boxStart - viewStart;
                        case SNAP_TO_END:
                            return boxEnd - viewEnd;
                        case SNAP_TO_ANY:
                            final int dtStart = boxStart - viewStart;
                            if (dtStart > 0)
                            {
                                return dtStart;
                            } else if (dtStart < 0)
                            {
                                return dtStart;
                            }

                            final int dtEnd = boxEnd - viewEnd;
                            if (dtEnd < 0)
                            {
                                return dtEnd;
                            }
                            break;
                        default:
                            throw new IllegalArgumentException("snap preference should be one of the" + " constants defined in SmoothScroller, starting with SNAP_");
                    }
                    return 0;
                }
            };

            smoothScroller.setTargetPosition(position);
            startSmoothScroll(smoothScroller);
        }
    }
}