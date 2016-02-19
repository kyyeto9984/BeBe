package com.bebe.lsi.bebe.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bebe.lsi.bebe.R;

/**
 * Created by LSJ on 2015-08-31.
 */
public class HotdealAdatper extends PagerAdapter {

    private Context context;
    private int[] flag;
    private LayoutInflater inflater;

    public HotdealAdatper(Context context, int[] flag) {
        this.context = context;
        this.flag = flag;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public int getCount() {
        return flag.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ImageView background;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.hotdeal_item, container,
                false);
        // Locate the ImageView in viewpager_item.xml
        background = (ImageView) itemView.findViewById(R.id.hotdeal);
        // Capture position and set to the ImageView
        background.setImageResource(flag[position]);

        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((LinearLayout) object);

    }
}
