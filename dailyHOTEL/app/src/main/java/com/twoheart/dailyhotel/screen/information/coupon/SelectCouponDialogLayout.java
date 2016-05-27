package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2016. 5. 26..
 */
public class SelectCouponDialogLayout extends BaseLayout implements View.OnClickListener
{

    public interface OnEventListener extends OnBaseEventListener
    {
        void setResult(Coupon coupon);

        void onCouponDownloadClick(int position);
    }

    private View mDialogLayout;
    private DailyTextView mMessageTextView;
    private View mOneButtonLayout;
    private View mTwoButtonLayout;
    private View mConfirmView;
    private View mNegativeView;
    private View mPositiveView;


    private RecyclerView mRecyclerView;
    private SelectCouponAdapter mListAdapter;

    private Window mWindow;


    public SelectCouponDialogLayout(Context context, Window window, OnBaseEventListener listener)
    {
        super(context, listener);

        mContext = context;
        mWindow = window;
    }


    @Override
    protected void initLayout(View view)
    {
        updateWindowHeight();
        initListView(view);

        mDialogLayout = view.findViewById(R.id.dialogLayout);

        mMessageTextView = (DailyTextView) view.findViewById(R.id.messageTextView);
        mOneButtonLayout = view.findViewById(R.id.oneButtonLayout);
        mTwoButtonLayout = view.findViewById(R.id.twoButtonLayout);
        mNegativeView = view.findViewById(R.id.negativeTextView);
        mPositiveView = view.findViewById(R.id.positiveTextView);
        mConfirmView = view.findViewById(R.id.confirmTextView);

        mPositiveView.setEnabled(false);

        mNegativeView.setOnClickListener(this);
        mPositiveView.setOnClickListener(this);
        mConfirmView.setOnClickListener(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);


        updateDialogLayout(false);
        updateLayout(null);

        // 하단 테스트 코드
        setData(setDummyData());
    }


    private void updateWindowHeight()
    {
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        //
        //        if (listSize > 0)
        //        {
        //            height = Util.getLCDHeight(mContext) - Util.dpToPx(mContext, 96);
        //        } else
        //        {
        //            height = WindowManager.LayoutParams.WRAP_CONTENT;
        //        }

        WindowManager.LayoutParams params = mWindow.getAttributes();
        params.width = Util.getLCDWidth(mContext) - Util.dpToPx(mContext, 24);
        params.height = height;

        mWindow.setAttributes(params);
    }

    private void initListView(View view)
    {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        EdgeEffectColor.setEdgeGlowColor(mRecyclerView, mContext.getResources().getColor(R.color.over_scroll_edge));

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    public void updateLayout(List<Coupon> list)
    {
        boolean isEmpty = isEmpty(list);

        updateMessageView(isEmpty);
        updateButtonLayout(isEmpty);
    }

    private boolean isEmpty(List<Coupon> list)
    {
        return (list != null && list.size() != 0) ? false : true;
    }

    private void updateDialogLayout(boolean isShow)
    {
        mDialogLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void updateButtonLayout(boolean isEmpty)
    {
        if (isEmpty == false)
        {
            mTwoButtonLayout.setVisibility(View.VISIBLE);
            mOneButtonLayout.setVisibility(View.GONE);
        } else
        {
            mTwoButtonLayout.setVisibility(View.GONE);
            mOneButtonLayout.setVisibility(View.VISIBLE);
        }
    }

    private void updateMessageView(boolean isEmpty)
    {
        int messageResId;
        if (isEmpty == false)
        {
            messageResId = R.string.message_select_coupon_selected;
        } else
        {
            messageResId = R.string.message_select_coupon_empty;
        }
        mMessageTextView.setText(messageResId);
    }


    public void setData(List<Coupon> list)
    {

        if (isEmpty(list) == false)
        {
            mListAdapter = new SelectCouponAdapter(mContext, list, mCouponItemListener);
        } else
        {
            mListAdapter = new SelectCouponAdapter(mContext, new ArrayList<Coupon>(), mCouponItemListener);
        }

        updateDialogLayout(true);
        updateLayout(list);
        mRecyclerView.setAdapter(mListAdapter);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.negativeTextView:
                mOnEventListener.finish();
                break;
            case R.id.positiveTextView:
                int selectPosition = mListAdapter.getSelectPosition();
                Coupon coupon = mListAdapter.getItem(selectPosition);

                ((OnEventListener) mOnEventListener).setResult(coupon);
                break;
            case R.id.confirmTextView:
                mOnEventListener.finish();
                break;
        }
    }

    private SelectCouponAdapter.OnCouponItemListener mCouponItemListener = new SelectCouponAdapter.OnCouponItemListener()
    {
        @Override
        public void onDownloadClick(int position)
        {
            ((OnEventListener) mOnEventListener).onCouponDownloadClick(position);
        }

        @Override
        public void updatePositiveButton()
        {
            if (mListAdapter.getSelectPosition() != -1)
            {
                mPositiveView.setEnabled(true);
            } else
            {
                mPositiveView.setEnabled(false);
            }
        }
    };

    private List<Coupon> setDummyData()
    {
        ArrayList<Coupon> list = new ArrayList<>();

        //        String      name,   int price,     String description, String expiredTime, int dueDate, int minPrice, int state, String useablePlace, String notice
        list.add(new Coupon("name_1", 10000, "테스트 쿠폰_1", "2016.05.31 ~ 2016.07.31", 15, 120, 0, "호텔, 팬션 부띠크 등등", "주의사항\n확인하기"));
        list.add(new Coupon("name_2", 3330000, "테스트 쿠폰_2", "2017.05.31 ~ 2016.07.31", 15, 33390000, 1, "호텔, 팬션 여러곳 들", "주의사항\n확인하기"));
        list.add(new Coupon("name_3", 1000, "테스트 쿠폰_3", "2016.05.31 ~ 2016.07.31", 125, 9005300, 1, "호텔 등등 여러곳 들", ""));
        //        list.add(new Coupon("name_4", 111100,       "테스트 쿠폰_4", "2016.04.31 ~ 2016.06.31", 15,          1000,           0, "호텔, 팬션 부띠크 등등 여러곳 들", "주의사항\n확인하기"));
        //        list.add(new Coupon("name_5", 500000,       "테스트 쿠폰_5", "2016.05.31 ~ 2018.07.31", 5,           10,             0, " 팬션, 부띠크", "주의사항\n확인하기"));
        //        list.add(new Coupon("name_6", 10000,        "테스트 쿠폰_6", "2010.01.01 ~ 2016.07.31", 15,          3838380,        0, "호텔, 팬션 부띠크 등등 여러곳 들", "주의사항\n확인하기"));
        //        list.add(new Coupon("name_7", 10340,        "테스트 쿠폰_7", "2016.05.31 ~ 2016.07.31", 7,           4900,           0, "호텔, 팬션 부띠크 등등 여러곳 들", "주의사항\n확인하기"));
        //        list.add(new Coupon("name_8", 230,          "테스트 쿠폰_8", "2016.05.31 ~ 2016.07.31", 15,          90560,          0, "여러곳 들", ""));
        //        list.add(new Coupon("name_9", 10000,        "테스트 쿠폰_9", "2016.05.31 ~ 2016.07.31", 15,          90333330,       1, "호텔, 팬션 부띠크 등등 여러곳 들", "주의사항\n확인하기"));
        //        list.add(new Coupon("name_10", 10000,       "테스트 쿠폰_10", "2016.05.31 ~ 2016.07.31", 15,         99999990,       0, " 부띠크", "주의사항\n확인하기"));
        //        list.add(new Coupon("name_11", 19990,       "테스트 쿠폰_11", "2016.01.02 ~ 2055.01.21", 15,         1056574500,     1, "호텔, 팬션, 부띠크 여러곳 들", "주의사항\n확인하기"));
        //        list.add(new Coupon("name_12", 100303000,   "테스트 쿠폰_12", "2016.05.31 ~ 2016.07.31", 1,          90440,          0, "호텔, 팬션, 부띠크 등등 여러곳 들", "주의사항\n확인하기"));
        //        list.add(new Coupon("name_13", 10000,       "테스트 쿠폰_13", "2000.01.11 ~ 2027.07.11", 15,         20000,          0, "호텔, 팬션 부띠크 등등 여러곳 들", "주의사항\n확인하기"));
        //        list.add(new Coupon("name_14", 51410000,    "테스트 쿠폰_14", "2016.05.31 ~ 2016.07.31", 65,         0,              1, " 팬션, 부띠크 등등 여러곳 들", ""));
        //        list.add(new Coupon("name_15", 810000,      "테스트 쿠폰_15", "2006.09.06 ~ 2016.07.30", 15,         10,             0, "호텔, 부띠크 등등 여러곳 들", "주의사항\n확인하기"));
        //        list.add(new Coupon("name_16", 9504000,     "테스트 쿠폰_16", "2016.05.31 ~ 2016.07.31", 0,          3000,           1, "호텔, 팬션, 부띠크 등등 여러곳 들", "주의사항\n확인하기"));
        return list;
    }
}
