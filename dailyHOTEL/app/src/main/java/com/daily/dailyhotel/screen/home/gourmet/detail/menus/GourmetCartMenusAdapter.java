package com.daily.dailyhotel.screen.home.gourmet.detail.menus;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.GourmetCartMenu;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutGourmetCartMenuDataBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GourmetCartMenusAdapter extends RecyclerView.Adapter<GourmetCartMenusAdapter.GourmetCartMenuViewHolder>
{
    private Context mContext;
    private List<GourmetCartMenu> mList;

    OnEventListener mOnEventListener;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onDeleteClick(int menuIndex);

        void onMenuCountPlusClick(int menuIndex);

        void onMenuCountMinusClick(int menuIndex);
    }

    public GourmetCartMenusAdapter(Context context)
    {
        mContext = context;

        mList = new ArrayList<>();
    }

    public void setOnEventListener(OnEventListener onEventListener)
    {
        mOnEventListener = onEventListener;
    }

    public void clear()
    {
        mList.clear();
    }

    public void add(GourmetCartMenu listItem)
    {
        mList.add(listItem);
    }

    public void add(int position, GourmetCartMenu placeViewItem)
    {
        if (position >= 0 && position < mList.size())
        {
            mList.add(position, placeViewItem);
        }
    }

    public void addAll(Collection<? extends GourmetCartMenu> collection)
    {
        if (collection == null)
        {
            return;
        }

        mList.addAll(collection);
    }

    public void setAll(Collection<? extends GourmetCartMenu> collection)
    {
        clear();
        addAll(collection);
    }

    public void remove(int position)
    {
        if (mList == null || mList.size() <= position)
        {
            return;
        }

        mList.remove(position);
    }

    public GourmetCartMenu getItem(int position)
    {
        if (position < 0 || mList.size() <= position)
        {
            return null;
        }

        return mList.get(position);
    }

    public int getPosition(int menuIndex)
    {
        if (menuIndex > 0)
        {
            int size = getItemCount();

            for (int i = 0; i < size; i++)
            {
                if (mList.get(i).index == menuIndex)
                {
                    return i;
                }
            }
        }

        return -1;
    }

    @Override
    public int getItemCount()
    {
        if (mList == null)
        {
            return 0;
        }

        return mList.size();
    }

    @Override
    public GourmetCartMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutGourmetCartMenuDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_gourmet_cart_menu_data, parent, false);

        return new GourmetCartMenuViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(GourmetCartMenuViewHolder holder, int position)
    {
        if (holder == null)
        {
            return;
        }

        GourmetCartMenu gourmetCartMenu = getItem(position);

        if (gourmetCartMenu == null)
        {
            return;
        }

        // 메뉴 제목
        holder.dataBinding.menuNameTextView.setText(gourmetCartMenu.name);

        // 가격
        String discountPrice = DailyTextUtils.getPriceFormat(mContext, gourmetCartMenu.discountPrice, false);
        holder.dataBinding.discountPriceTextView.setText(discountPrice);

        //
        setMenuOrderCount(holder, position, gourmetCartMenu.count, gourmetCartMenu.minimumOrderQuantity, gourmetCartMenu.maximumOrderQuantity, gourmetCartMenu.availableTicketNumbers);

        holder.dataBinding.menuCountMinusView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                synchronized (this)
                {
                    if (mOnEventListener != null)
                    {
                        mOnEventListener.onMenuCountMinusClick(gourmetCartMenu.index);
                    }
                }
            }
        });

        holder.dataBinding.menuCountPlusView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                synchronized (this)
                {
                    if (mOnEventListener != null)
                    {
                        mOnEventListener.onMenuCountPlusClick(gourmetCartMenu.index);
                    }
                }
            }
        });

        // 이용인원
        if (gourmetCartMenu.persons > 1)
        {
            holder.dataBinding.personsTextView.setVisibility(View.VISIBLE);
            holder.dataBinding.personsTextView.setText(mContext.getString(R.string.label_persons, gourmetCartMenu.persons));
        } else
        {
            holder.dataBinding.personsTextView.setVisibility(View.GONE);
        }

        holder.dataBinding.deleteImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnEventListener != null)
                {
                    mOnEventListener.onDeleteClick(gourmetCartMenu.index);
                }
            }
        });
    }

    public void setMenuOrderCount(RecyclerView.ViewHolder viewHolder, int position, int menuOrderCount, int minimumOrderQuantity, int maximumOrderQuantity, int saleOrderQuantity)
    {
        if (viewHolder == null || position < 0)
        {
            return;
        }

        getItem(position).count = menuOrderCount;

        GourmetCartMenuViewHolder gourmetCartMenuViewHolder = (GourmetCartMenuViewHolder) viewHolder;

        if (menuOrderCount < minimumOrderQuantity)
        {
            gourmetCartMenuViewHolder.dataBinding.menuCountMinusView.setEnabled(false);
        } else
        {
            gourmetCartMenuViewHolder.dataBinding.menuCountMinusView.setEnabled(true);
        }

        if (menuOrderCount >= maximumOrderQuantity || menuOrderCount >= saleOrderQuantity)
        {
            gourmetCartMenuViewHolder.dataBinding.menuCountPlusView.setSelected(false);
        } else
        {
            gourmetCartMenuViewHolder.dataBinding.menuCountPlusView.setSelected(true);
        }

        gourmetCartMenuViewHolder.dataBinding.menuCountTextView.setText(Integer.toString(menuOrderCount));
    }

    static class GourmetCartMenuViewHolder extends RecyclerView.ViewHolder
    {
        LayoutGourmetCartMenuDataBinding dataBinding;

        public GourmetCartMenuViewHolder(LayoutGourmetCartMenuDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;
        }
    }
}