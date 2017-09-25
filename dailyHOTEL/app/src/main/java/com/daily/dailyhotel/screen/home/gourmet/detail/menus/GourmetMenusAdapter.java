package com.daily.dailyhotel.screen.home.gourmet.detail.menus;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.entity.GourmetMenu;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutGourmetMenuDetailInformationDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowGourmetMenuDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class GourmetMenusAdapter extends RecyclerView.Adapter<GourmetMenusAdapter.GourmetMenuViewHolder>
{
    public static final float MENU_WIDTH_RATIO = 0.87f;
    private Context mContext;
    private List<GourmetMenu> mList;

    OnEventListener mOnEventListener;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onReservationClick(int index);

        void onMoreImageClick(int index);
    }

    public GourmetMenusAdapter(Context context, List<GourmetMenu> gourmetMenuList)
    {
        mContext = context;

        mList = new ArrayList<>();

        addAll(gourmetMenuList);
    }

    public void setOnEventListener(OnEventListener onEventListener)
    {
        mOnEventListener = onEventListener;
    }

    public void clear()
    {
        mList.clear();
    }

    public void add(GourmetMenu listItem)
    {
        mList.add(listItem);
    }

    public void add(int position, GourmetMenu placeViewItem)
    {
        if (position >= 0 && position < mList.size())
        {
            mList.add(position, placeViewItem);
        }
    }

    public void addAll(Collection<? extends GourmetMenu> collection)
    {
        if (collection == null)
        {
            return;
        }

        mList.addAll(collection);
    }

    public void setAll(Collection<? extends GourmetMenu> collection)
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

    public GourmetMenu getItem(int position)
    {
        if (position < 0 || mList.size() <= position)
        {
            return null;
        }

        return mList.get(position);
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
    public GourmetMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        ListRowGourmetMenuDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_gourmet_menu_data, parent, false);

        dataBinding.getRoot().setLayoutParams(new RecyclerView.LayoutParams((int) getMenuWidth(), ViewGroup.LayoutParams.MATCH_PARENT));
        GourmetMenuViewHolder gourmetMenuViewHolder = new GourmetMenuViewHolder(dataBinding);

        dataBinding.roundedConstraintLayout.setRound(ScreenUtils.dpToPx(mContext, 5));

        return gourmetMenuViewHolder;
    }

    @Override
    public void onBindViewHolder(GourmetMenuViewHolder holder, int position)
    {
        if (holder == null)
        {
            return;
        }

        if (holder.dataBinding.nestedScrollView.getScrollY() != 0)
        {
            holder.dataBinding.nestedScrollView.scrollTo(0, 0);
        }

        GourmetMenu gourmetMenu = getItem(position);

        if (gourmetMenu == null)
        {
            return;
        }

        final float with = getMenuMargin();

        if (position == 0)
        {
            ((RecyclerView.LayoutParams) holder.dataBinding.getRoot().getLayoutParams()).leftMargin = (int) with;
            ((RecyclerView.LayoutParams) holder.dataBinding.getRoot().getLayoutParams()).rightMargin = 0;
        } else if (position == getItemCount() - 1)
        {
            ((RecyclerView.LayoutParams) holder.dataBinding.getRoot().getLayoutParams()).leftMargin = 0;
            ((RecyclerView.LayoutParams) holder.dataBinding.getRoot().getLayoutParams()).rightMargin = (int) with;
        } else
        {
            ((RecyclerView.LayoutParams) holder.dataBinding.getRoot().getLayoutParams()).leftMargin = 0;
            ((RecyclerView.LayoutParams) holder.dataBinding.getRoot().getLayoutParams()).rightMargin = 0;
        }

        EdgeEffectColor.setEdgeGlowColor(holder.dataBinding.nestedScrollView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        // 이미지 정보
        List<DetailImageInformation> detailImageInformationList = gourmetMenu.getImageList();

        if (detailImageInformationList == null || detailImageInformationList.size() == 0)
        {
            holder.dataBinding.defaultImageLayout.setVisibility(View.GONE);
            holder.dataBinding.defaultImageLayout.setOnClickListener(null);
        } else
        {
            holder.dataBinding.defaultImageLayout.setVisibility(View.VISIBLE);

            holder.dataBinding.simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);
            Util.requestImageResize(mContext, holder.dataBinding.simpleDraweeView, gourmetMenu.getPrimaryImage().url);
            setLineIndicatorVisible(holder.dataBinding, detailImageInformationList.size());

            holder.dataBinding.defaultImageLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mOnEventListener != null)
                    {
                        mOnEventListener.onMoreImageClick(position);
                    }
                }
            });
        }

        // 메뉴 제목
        holder.dataBinding.productNameTextView.setText(gourmetMenu.name);

        // 베네핏
        if (DailyTextUtils.isTextEmpty(gourmetMenu.menuBenefit) == true)
        {
            holder.dataBinding.benefitLayout.setVisibility(View.GONE);
        } else
        {
            holder.dataBinding.benefitLayout.setVisibility(View.VISIBLE);
            holder.dataBinding.benefitTextView.setText(gourmetMenu.menuBenefit);
        }

        // 이용시간
        if (DailyTextUtils.isTextEmpty(gourmetMenu.openTime, gourmetMenu.closeTime) == true)
        {
            holder.dataBinding.timeLayout.setVisibility(View.GONE);
        } else
        {
            holder.dataBinding.timeLayout.setVisibility(View.VISIBLE);
            String timeFormat = String.format(Locale.KOREA, "%s ~ %s", gourmetMenu.openTime, gourmetMenu.closeTime);

            if (DailyTextUtils.isTextEmpty(gourmetMenu.lastOrderTime) == false)
            {
                timeFormat += " " + mContext.getString(R.string.label_gourmet_product_lastorder, gourmetMenu.lastOrderTime);
            }

            holder.dataBinding.timeTextView.setText(timeFormat);
        }

        // 확인사항
        if (DailyTextUtils.isTextEmpty(gourmetMenu.needToKnow) == true)
        {
            holder.dataBinding.checkLayout.setVisibility(View.GONE);
        } else
        {
            holder.dataBinding.checkLayout.setVisibility(View.VISIBLE);
            holder.dataBinding.checkTextView.setText(gourmetMenu.needToKnow);
        }

        // 예약 조건
        if (DailyTextUtils.isTextEmpty(gourmetMenu.reserveCondition) == true)
        {
            holder.dataBinding.conditionLayout.setVisibility(View.GONE);
        } else
        {
            holder.dataBinding.conditionLayout.setVisibility(View.VISIBLE);
            holder.dataBinding.conditionTextView.setText(gourmetMenu.reserveCondition);
        }

        // 메뉴 설명
        if (DailyTextUtils.isTextEmpty(gourmetMenu.menuSummary) == true)
        {
            holder.dataBinding.menuSummaryTextView.setVisibility(View.GONE);
        } else
        {
            holder.dataBinding.menuSummaryTextView.setVisibility(View.VISIBLE);
            holder.dataBinding.menuSummaryTextView.setText(gourmetMenu.menuSummary);
        }

        List<String> menuDetailList = gourmetMenu.getMenuDetailList();

        if (menuDetailList == null || menuDetailList.size() == 0)
        {
            holder.dataBinding.menuDetailLayout.setVisibility(View.GONE);
        } else
        {
            holder.dataBinding.menuDetailLayout.setVisibility(View.VISIBLE);
            setMenuDetail(mContext, holder.dataBinding.menuDetailLayout, menuDetailList);
        }

        if (holder.dataBinding.menuSummaryTextView.getVisibility() == View.GONE && holder.dataBinding.menuDetailLayout.getVisibility() == View.GONE)
        {
            holder.dataBinding.menuTextView.setVisibility(View.GONE);
        } else
        {
            holder.dataBinding.menuTextView.setVisibility(View.VISIBLE);
        }

        // bottom bar
        holder.dataBinding.priceTextView.setPaintFlags(holder.dataBinding.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        String price = DailyTextUtils.getPriceFormat(mContext, gourmetMenu.price, false);
        String discountPrice = DailyTextUtils.getPriceFormat(mContext, gourmetMenu.discountPrice, false);

        if (gourmetMenu.price <= 0 || gourmetMenu.price <= gourmetMenu.discountPrice)
        {
            holder.dataBinding.priceTextView.setVisibility(View.GONE);
            holder.dataBinding.priceTextView.setText(null);
        } else
        {
            holder.dataBinding.priceTextView.setVisibility(View.VISIBLE);
            holder.dataBinding.priceTextView.setText(price);
        }

        holder.dataBinding.discountPriceTextView.setText(discountPrice);
        holder.dataBinding.reservationTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnEventListener != null)
                {
                    mOnEventListener.onReservationClick(position);
                }
            }
        });

        holder.dataBinding.getRoot().setTag(R.id.blurView, holder.dataBinding.blurView);
    }

    public float getMenuWidth()
    {
        return ScreenUtils.getScreenWidth(mContext) * MENU_WIDTH_RATIO;
    }

    public float getMenuMargin()
    {
        return ScreenUtils.getScreenWidth(mContext) * (1.0f - MENU_WIDTH_RATIO) / 2.0f;
    }

    private void setLineIndicatorVisible(ListRowGourmetMenuDataBinding dataBinding, int count)
    {
        if (count > 1)
        {
            dataBinding.moreIconView.setVisibility(View.VISIBLE);
        } else if (count == 1)
        {
            dataBinding.moreIconView.setVisibility(View.VISIBLE);
        } else
        {
            dataBinding.moreIconView.setVisibility(View.GONE);
        }
    }

    private void setMenuDetail(Context context, ViewGroup viewGroup, List<String> menuDetailList)
    {
        if (context == null || viewGroup == null || menuDetailList == null)
        {
            return;
        }

        viewGroup.removeAllViews();

        int size = menuDetailList.size();

        for (int i = 0; i < size; i++)
        {
            String contentText = menuDetailList.get(i);

            if (DailyTextUtils.isTextEmpty(contentText) == true)
            {
                continue;
            }

            LayoutGourmetMenuDetailInformationDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_gourmet_menu_detail_information_data, viewGroup, true);
            dataBinding.textView.setText(contentText);

            if (i != 0)
            {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) dataBinding.getRoot().getLayoutParams();
                layoutParams.topMargin = ScreenUtils.dpToPx(mContext, 10);
            }
        }
    }

    static class GourmetMenuViewHolder extends RecyclerView.ViewHolder
    {
        ListRowGourmetMenuDataBinding dataBinding;

        public GourmetMenuViewHolder(ListRowGourmetMenuDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;
        }
    }
}