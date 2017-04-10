package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DailyHintSpinner extends AppCompatSpinner
{
    int mResourceId;

    public DailyHintSpinner(Context context)
    {
        super(context);
    }

    public DailyHintSpinner(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DailyHintSpinner(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void setHintLayout(int resourceId)
    {
        mResourceId = resourceId;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter)
    {
        if (adapter == null)
        {
            super.setAdapter(null);
        } else
        {
            final SpinnerAdapter spinnerAdapter = newProxy(adapter);
            super.setAdapter(spinnerAdapter);
        }

        try
        {
            final Method m = AdapterView.class.getDeclaredMethod("setNextSelectedPositionInt", int.class);
            m.setAccessible(true);
            m.invoke(this, -1);

            final Method n = AdapterView.class.getDeclaredMethod("setSelectedPositionInt", int.class);
            n.setAccessible(true);
            n.invoke(this, -1);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    protected SpinnerAdapter newProxy(SpinnerAdapter obj)
    {
        return (SpinnerAdapter) java.lang.reflect.Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class[]{SpinnerAdapter.class}, new SpinnerAdapterProxy(obj));
    }


    /**
     * Intercepts getView() to display the prompt if position < 0
     */
    protected class SpinnerAdapterProxy implements InvocationHandler
    {

        protected SpinnerAdapter mSpinnerAdapter;
        protected Method getView;


        protected SpinnerAdapterProxy(SpinnerAdapter spinnerAdapter)
        {
            this.mSpinnerAdapter = spinnerAdapter;
            try
            {
                this.getView = SpinnerAdapter.class.getMethod("getView", int.class, View.class, ViewGroup.class);
            } catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        public Object invoke(Object proxy, Method m, Object[] args) throws Throwable
        {
            try
            {
                return m.equals(getView) && (Integer) (args[0]) < 0 ? getView((Integer) args[0], (View) args[1], (ViewGroup) args[2]) : m.invoke(mSpinnerAdapter, args);
            } catch (InvocationTargetException e)
            {
                throw e.getTargetException();
            } catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        protected View getView(int position, View convertView, ViewGroup parent) throws IllegalAccessException
        {

            if (position < 0)
            {
                final TextView v = (TextView) ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(mResourceId, parent, false);
                v.setText(getPrompt());
                return v;
            }
            return mSpinnerAdapter.getView(position, convertView, parent);
        }
    }
}