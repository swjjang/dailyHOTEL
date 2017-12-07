package com.twoheart.dailyhotel.screen.mydaily.bonus;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Bonus;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;
import java.util.List;

public class BonusLayout extends BaseLayout
{
    private TextView mBonusTextView;
    private View mListTopLine;
    private ListView mListView;
    private View mFooterView;

    private BonusListAdapter mBonusListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onBonusGuide();
    }

    public BonusLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view);

        mBonusTextView = view.findViewById(R.id.bonusTextView);

        mListTopLine = view.findViewById(R.id.listTopLine);
        mListTopLine.setVisibility(View.INVISIBLE);

        mListView = view.findViewById(R.id.listView);

        View header = LayoutInflater.from(mContext).inflate(R.layout.list_row_bonus_header, mListView, false);
        mListView.addHeaderView(header);

        TextView guideTextView = header.findViewById(R.id.guideTextView);
        guideTextView.setPaintFlags(guideTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        guideTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onBonusGuide();
            }
        });

        mFooterView = LayoutInflater.from(mContext).inflate(R.layout.list_row_bonus_footer, mListView, false);
        mListView.addFooterView(mFooterView);
    }

    private void initToolbar(View view)
    {
        DailyToolbarView dailyToolbarView = view.findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.actionbar_title_credit_frag);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        });
    }

    public void setBonus(int bonus)
    {
        mBonusTextView.setText(DailyTextUtils.getPriceFormat(mContext, bonus, false));
    }

    public void setData(List<Bonus> list)
    {
        EdgeEffectColor.setEdgeGlowColor(mListView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        if (mBonusListAdapter == null)
        {
            mBonusListAdapter = new BonusListAdapter(mContext, 0, new ArrayList<>());
        } else
        {
            mBonusListAdapter.clear();
        }

        if (list != null && list.size() != 0)
        {
            if (mListView.getFooterViewsCount() > 0)
            {
                mListView.removeFooterView(mFooterView);
            }

            mBonusListAdapter.addAll(list);
        }

        mListTopLine.setVisibility(View.VISIBLE);
        mListView.setAdapter(mBonusListAdapter);
    }
}