package com.twoheart.dailyhotel.screen.information.bonus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Bonus;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.List;

public class BonusListActivity extends BaseActivity
{
    public static final String KEY_BUNDLE_ARGUMENTS_CREDITLIST = "credit_list";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        List<Bonus> list;

        if (intent != null)
        {
            list = intent.getParcelableArrayListExtra(KEY_BUNDLE_ARGUMENTS_CREDITLIST);
        } else
        {
            Util.restartApp(this);
            return;
        }

        setContentView(R.layout.activity_credit_list);

        initToolbar();
        initLayout(list);
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.act_credit_history), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void initLayout(List<Bonus> list)
    {
        ListView listView = (ListView) findViewById(R.id.listview_credit);
        EdgeEffectColor.setEdgeGlowColor(listView, getResources().getColor(R.color.over_scroll_edge));

        if (list != null && list.size() != 0)
        {
            BonusListAdapter adapter = new BonusListAdapter(BonusListActivity.this, 0, list);
            listView.setAdapter(adapter);
        }
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(BonusListActivity.this).recordScreen(Screen.BONUS_LIST, null);

        super.onStart();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }
}
