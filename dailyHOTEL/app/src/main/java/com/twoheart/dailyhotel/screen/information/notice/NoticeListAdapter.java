package com.twoheart.dailyhotel.screen.information.notice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Notice;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NoticeListAdapter extends ArrayAdapter<Notice>
{
    private ArrayList<Notice> mArrayList;
    private Context mContext;

    public NoticeListAdapter(Context context, int resourceId, List<Notice> list)
    {
        super(context, resourceId, list);

        mContext = context;
        addAll(list);
    }

    public void addAll(Collection<? extends Notice> collection)
    {
        if (collection == null)
        {
            return;
        }

        if (mArrayList == null)
        {
            mArrayList = new ArrayList<>();
        }

        mArrayList.addAll(collection);
    }

    @Override
    public int getCount()
    {
        if (mArrayList == null)
        {
            return 0;
        }

        return mArrayList.size();
    }

    @Override
    public Notice getItem(int position)
    {
        if (mArrayList == null)
        {
            return null;
        }

        return mArrayList.get(position);
    }

    @Override
    public void clear()
    {
        if (mArrayList == null)
        {
            return;
        }

        mArrayList.clear();

        super.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view;

        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_row_notice, parent, false);
        } else
        {
            view = convertView;
        }

        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView dateTextView = view.findViewById(R.id.dateTextView);
        View newIconView = view.findViewById(R.id.newIconView);

        Notice notice = getItem(position);

        titleTextView.setText(notice.title);

        try
        {
            dateTextView.setText(DailyCalendar.convertDateFormatString(notice.createdAt, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
        } catch (ParseException e)
        {
            Crashlytics.log("notice.createdAt: " + (notice != null ? notice.createdAt : ""));
            Crashlytics.logException(e);
            ExLog.d(e.toString());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        if (notice.isNew == true)
        {
            newIconView.setVisibility(View.VISIBLE);
        } else
        {
            newIconView.setVisibility(View.INVISIBLE);
        }

        return view;
    }
}
