package com.daily.dailyhotel.screen.home.stay.outbound.detail.amenities;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityAmenityListDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowAmenityDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;
import java.util.List;

public class AmenityListView extends BaseDialogView<AmenityListView.OnEventListener, ActivityAmenityListDataBinding>//
    implements AmenityListInterface
{
    private AmenityListAdapter mAmenityListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public AmenityListView(BaseActivity baseActivity, AmenityListView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityAmenityListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        viewDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge));
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
    public void setAmenityList(ArrayList<String> amenityList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (mAmenityListAdapter == null)
        {
            mAmenityListAdapter = new AmenityListAdapter(getContext());
            getViewDataBinding().recyclerView.setAdapter(mAmenityListAdapter);
        }

        mAmenityListAdapter.setAll(amenityList);
        mAmenityListAdapter.notifyDataSetChanged();
    }

    private void initToolbar(ActivityAmenityListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setBackImageResource(R.drawable.navibar_ic_x);
        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

    class AmenityListAdapter extends RecyclerView.Adapter<AmenityListAdapter.AmenityViewHolder>
    {
        private Context mContext;
        private List<String> mAmenityList;

        public AmenityListAdapter(Context context)
        {
            mContext = context;
            setAll(null);
        }

        @Override
        public AmenityViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            ListRowAmenityDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_amenity_data, parent, false);

            return new AmenityViewHolder(dataBinding);
        }

        @Override
        public void onBindViewHolder(AmenityViewHolder holder, int position)
        {
            holder.dataBinding.amenityTextView.setText(getItem(position));

            // 마지막 아이템인 경우 하단에 여백 추가
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.dataBinding.amenityTextView.getLayoutParams();

            if (position == 0)
            {
                layoutParams.topMargin = ScreenUtils.dpToPx(mContext, 16);
                layoutParams.bottomMargin = 0;
            } else if (position == getItemCount() - 1)
            {
                layoutParams.topMargin = 0;
                layoutParams.bottomMargin = ScreenUtils.dpToPx(mContext, 12);
            } else
            {
                layoutParams.topMargin = 0;
                layoutParams.bottomMargin = 0;
            }

            holder.dataBinding.amenityTextView.setLayoutParams(layoutParams);
        }

        @Override
        public int getItemCount()
        {
            if (mAmenityList == null)
            {
                return 0;
            } else
            {
                return mAmenityList.size();
            }
        }

        public void setAll(List<String> list)
        {
            if (mAmenityList == null)
            {
                mAmenityList = new ArrayList<>();
            }

            mAmenityList.clear();

            if (list != null && list.size() > 0)
            {
                mAmenityList.addAll(list);
            }
        }

        public String getItem(int position)
        {
            if (position < 0 || mAmenityList.size() <= position)
            {
                return null;
            }

            return mAmenityList.get(position);
        }

        class AmenityViewHolder extends RecyclerView.ViewHolder
        {
            ListRowAmenityDataBinding dataBinding;

            public AmenityViewHolder(ListRowAmenityDataBinding dataBinding)
            {
                super(dataBinding.getRoot());

                this.dataBinding = dataBinding;
            }
        }
    }
}
