package com.atlanticomnibus.controlsheet;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * A vwry simple adapter for the ViewPager
 */
public class SimplePagerAdapter extends PagerAdapter {

    private Context mContext;
    private final List<Integer> layoutCollection;

    public SimplePagerAdapter(Context context, List<Integer> layouts) {
        mContext=context;
        layoutCollection = layouts;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout;
        layout=(ViewGroup) inflater.inflate(layoutCollection.get(position), collection, false);
        collection.addView(layout);
        return layout;
    }

    @Override
    public int getCount() {
        return layoutCollection.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

}
