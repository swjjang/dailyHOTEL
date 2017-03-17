package com.twoheart.dailyhotel.place.layout;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceReviewItem;
import com.twoheart.dailyhotel.network.model.PlaceReview;
import com.twoheart.dailyhotel.network.model.PlaceReviewProgress;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;
import java.util.List;

public abstract class PlaceReviewLayout extends BaseLayout
{
    private RecyclerView mRecyclerView;
    private ReviewListAdapter mReviewListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onTermsClick();
    }

    public PlaceReviewLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);


    }

    @Override
    protected void initLayout(View view)
    {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        EdgeEffectColor.setEdgeGlowColor(mRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));
    }

    public void setReviewList(List<PlaceReviewProgress> placeReviewProgressList, List<PlaceReview> placeReviewList)
    {
        if (placeReviewProgressList == null || placeReviewProgressList.size() == 0 || placeReviewList == null || placeReviewList.size() == 0)
        {
            return;
        }

        if (mReviewListAdapter == null)
        {
            mReviewListAdapter = new ReviewListAdapter(mContext);
            mRecyclerView.setAdapter(mReviewListAdapter);
        }

        List<PlaceReviewItem> placeReviewItemList = new ArrayList<>();

        placeReviewItemList.add(new PlaceReviewItem(PlaceReviewItem.TYPE_HEADER_VIEW, placeReviewProgressList));

        for(PlaceReview placeReview : placeReviewList)
        {
            placeReviewItemList.add(new PlaceReviewItem(PlaceReviewItem.TYPE_ENTRY, placeReview));
        }

        mReviewListAdapter.setAll(placeReviewItemList);
        mReviewListAdapter.notifyDataSetChanged();
    }

    public void addReviewList(List<PlaceReview> placeReviewList)
    {
        if (mReviewListAdapter == null || placeReviewList == null || placeReviewList.size() == 0)
        {
            return;
        }

        List<PlaceReviewItem> placeReviewItemList = new ArrayList<>();

        for(PlaceReview placeReview : placeReviewList)
        {
            placeReviewItemList.add(new PlaceReviewItem(PlaceReviewItem.TYPE_ENTRY, placeReview));
        }

        mReviewListAdapter.addAll(placeReviewItemList);
        mReviewListAdapter.notifyDataSetChanged();
    }

    class ReviewListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private Context mContext;
        private LayoutInflater mInflater;
        private List<PlaceReviewItem> mPlaceReviewItemList;

        public ReviewListAdapter(Context context)
        {
            mContext = context;

            mPlaceReviewItemList = new ArrayList<>();
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public PlaceReviewItem getItem(int position)
        {
            if (position < 0 || mPlaceReviewItemList.size() <= position)
            {
                return null;
            }

            return mPlaceReviewItemList.get(position);
        }

        public void setAll(List<PlaceReviewItem> placeReviewItemList)
        {
            clear();
            addAll(placeReviewItemList);
        }

        public void addAll(List<PlaceReviewItem> placeReviewItemList)
        {
            if (placeReviewItemList == null || placeReviewItemList.size() == 0)
            {
                return;
            }

            mPlaceReviewItemList.addAll(placeReviewItemList);
        }

        public void clear()
        {
            mPlaceReviewItemList.clear();
        }

        @Override
        public int getItemViewType(int position)
        {
            return mPlaceReviewItemList.get(position).mType;
        }

        @Override
        public int getItemCount()
        {
            if (mPlaceReviewItemList == null)
            {
                return 0;
            }

            return mPlaceReviewItemList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            switch (viewType)
            {
                case PlaceReviewItem.TYPE_HEADER_VIEW:
                {
                    View view = mInflater.inflate(R.layout.list_row_place_review_header, parent, false);

                    return new HeaderViewHolder(view);
                }

                case PlaceReviewItem.TYPE_ENTRY:
                {
                    View view = mInflater.inflate(R.layout.list_row_place_review, parent, false);

                    return new ReviewViewHolder(view);
                }

                case PlaceReviewItem.TYPE_FOOTER_VIEW:
                {
                    View view = mInflater.inflate(R.layout.list_row_product_footer, parent, false);

                    return new FooterViewHolder(view);
                }
            }

            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            PlaceReviewItem placeReviewItem = getItem(position);

            if (placeReviewItem == null)
            {
                return;
            }

            switch (placeReviewItem.mType)
            {
                case PlaceReviewItem.TYPE_HEADER_VIEW:
                    onBindViewHolder((HeaderViewHolder) holder, position, placeReviewItem);
                    break;

                case PlaceReviewItem.TYPE_ENTRY:
                    onBindViewHolder((ReviewViewHolder) holder, position, placeReviewItem);
                    break;

                case PlaceReviewItem.TYPE_FOOTER_VIEW:
                    break;
            }
        }

        private void onBindViewHolder(HeaderViewHolder headerViewHolder, int position, PlaceReviewItem placeViewItem)
        {
            PlaceReview placeReview = placeViewItem.getItem();


        }

        private void onBindViewHolder(ReviewViewHolder reviewViewHolder, int position, PlaceReviewItem placeViewItem)
        {
            PlaceReviewProgress placeReviewProgress = placeViewItem.getItem();


        }

        private class HeaderViewHolder extends RecyclerView.ViewHolder
        {
            View termsView;
            LinearLayout progressBarLayout;

            public HeaderViewHolder(View view)
            {
                super(view);

                termsView = itemView.findViewById(R.id.termsView);
                progressBarLayout = (LinearLayout) itemView.findViewById(R.id.progressBarLayout);
            }
        }

        private class ReviewViewHolder extends RecyclerView.ViewHolder
        {
            TextView reviewCountTextView;
            TextView emailTextView;
            TextView dateTextView;
            TextView reviewTextView;

            public ReviewViewHolder(View view)
            {
                super(view);

                reviewCountTextView = (TextView) view.findViewById(R.id.reviewCountTextView);
                emailTextView = (TextView) view.findViewById(R.id.emailTextView);
                dateTextView = (TextView) view.findViewById(R.id.dateTextView);
                reviewTextView = (TextView) view.findViewById(R.id.reviewTextView);
            }
        }

        private class FooterViewHolder extends RecyclerView.ViewHolder
        {
            public FooterViewHolder(View view)
            {
                super(view);
            }
        }
    }

}