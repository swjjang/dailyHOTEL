package com.twoheart.dailyhotel.screen.home;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by android_sam on 2017. 1. 16..
 */

public class HomeCarouselLayout extends RelativeLayout
{
    private Context mContext;
    private DailyTextView mTitleTextView;
    private DailyTextView mCountTextView;
    private DailyTextView mViewAllTextView;
    private OnCarouselListener mCarouselListenter;
    private ArrayList<? extends Place> mPlaceList;
    private RecyclerView mRecyclerView;
    private HomeCarouselAdapter mRecyclerAdapter;

    public interface OnCarouselListener
    {
        void onViewAllClick();
    }

    public HomeCarouselLayout(Context context)
    {
        super(context);

        mContext = context;
        initLayout();
    }

    public HomeCarouselLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mContext = context;
        initLayout();
    }

    public HomeCarouselLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        mContext = context;
        initLayout();
    }

    public HomeCarouselLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        mContext = context;
        initLayout();
    }

    private void initLayout()
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_carousel_layout, this);

        mTitleTextView = (DailyTextView) view.findViewById(R.id.titleTextView);
        mCountTextView = (DailyTextView) view.findViewById(R.id.countTextView);
        mViewAllTextView = (DailyTextView) view.findViewById(R.id.viewAllTextView);

        mViewAllTextView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mCarouselListenter != null)
                {
                    mCarouselListenter.onViewAllClick();
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.setAutoMeasureEnabled(true);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.horizontalRecyclerView);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        EdgeEffectColor.setEdgeGlowColor(mRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        // 임시 테스트 데이터
        ArrayList<Stay> placeList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 6; i++)
        {
            Stay stay = new Stay();

            stay.price = Math.abs(100000 * random.nextInt());
            stay.name = "Stay " + i;
            stay.discountPrice = Math.abs(stay.price - (1000 * random.nextInt()));
            stay.districtName = "서울";
            if (i % 3 == 0)
            {
                stay.imageUrl = "https://img.dailyhotel.me/resources/images/dh_23351/01.jpg";
            } else if (i % 3 == 1)
            {
                stay.imageUrl = "https://img.dailyhotel.me/resources/images/dh_23351/02.jpg";
            } else
            {
                stay.imageUrl = "https://img.dailyhotel.me/resources/images/dh_23351/03.jpg";
            }
            placeList.add(stay);

            stay.setGrade(Stay.Grade.special2);
        }

        setData(placeList);
        // 임시 테스트 데이터 끝!
    }

    public void setCarouselListener(OnCarouselListener listener)
    {
        mCarouselListenter = listener;
    }

    public void setData(ArrayList<? extends Place> list)
    {
        if (list == null || list.size() == 0)
        {
            list = new ArrayList<>();
        }

        if (mRecyclerAdapter == null)
        {
            mRecyclerAdapter = new HomeCarouselAdapter(mContext, list, mRecyclerItemClcikListner);
            mRecyclerView.setAdapter(mRecyclerAdapter);
        } else
        {
            mRecyclerAdapter.setData(list);
            mRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private HomeCarouselAdapter.ItemClickListener mRecyclerItemClcikListner = new HomeCarouselAdapter.ItemClickListener()
    {
        @Override
        public void onItemClick(View view, int position)
        {
            // TODO : 아이템 클릭 시 이동하는 부분 생성
        }
    };
}
