package com.twoheart.dailyhotel.screen.information.bonus;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Bonus;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BonusLayout extends BaseLayout implements View.OnClickListener
{
    private TextView mBonusTextView;
    private ListView mListView;
    private View mFooterView;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onInviteFriends();

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

        View inviteFriend = view.findViewById(R.id.inviteFriendsTextView);
        inviteFriend.setOnClickListener(this);

        mBonusTextView = (TextView) view.findViewById(R.id.bonusTextView);
        mListView = (ListView) view.findViewById(R.id.listView);

        View header = LayoutInflater.from(mContext).inflate(R.layout.list_row_bonus_header, mListView, false);
        mListView.addHeaderView(header);

        TextView guideTextView = (TextView) header.findViewById(R.id.guideTextView);
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
        View toolbar = view.findViewById(R.id.toolbar);

        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(mContext, toolbar);
        dailyToolbarLayout.initToolbar(mContext.getString(R.string.actionbar_title_credit_frag), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.inviteFriendsTextView:
                ((OnEventListener) mOnEventListener).onInviteFriends();
                break;
        }
    }

    public void setBonus(int bonus)
    {
        DecimalFormat decimalFormat = new DecimalFormat("###,##0원");
        mBonusTextView.setText(decimalFormat.format(bonus));
    }

    public void setData(List<Bonus> list)
    {
        EdgeEffectColor.setEdgeGlowColor(mListView, mContext.getResources().getColor(R.color.default_over_scroll_edge));
        BonusListAdapter bonusListAdapter = null;

        if (list != null && list.size() != 0)
        {
            mListView.removeFooterView(mFooterView);
            bonusListAdapter = new BonusListAdapter(mContext, 0, list);
        } else
        {
            bonusListAdapter = new BonusListAdapter(mContext, 0, new ArrayList<Bonus>());
        }

        mListView.setAdapter(bonusListAdapter);
    }
}