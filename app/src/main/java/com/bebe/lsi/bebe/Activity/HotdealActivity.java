package com.bebe.lsi.bebe.Activity;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bebe.lsi.bebe.Adapter.HotdealAdatper;
import com.bebe.lsi.bebe.Adapter.HotpickAdatper;
import com.bebe.lsi.bebe.R;

public class HotdealActivity extends AppCompatActivity {

    private int[] hotdeal = new int[]{
            R.drawable.hotdeal_deal1,
            R.drawable.hotdeal_deal2};

    private int[] hotpick = new int[]{
            R.drawable.hotpic_pic1,
            R.drawable.hotpic_pic2,
            R.drawable.hotpic_pic3};

    private ViewPager hotdeal_pager;
    private ViewPager hotpick_pager;
    private HotdealAdatper hotdeal_Adapter;
    private HotpickAdatper hotpick_Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotdeal);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("BeBe");
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.hotdeal_ColorPrimaryDark));
        }
        hotdeal_pager = (ViewPager)findViewById(R.id.hotdeal_pager);
        hotpick_pager = (ViewPager)findViewById(R.id.hotpick_pager);
        hotdeal_Adapter = new HotdealAdatper(getApplicationContext() , hotdeal);
        hotpick_Adapter = new HotpickAdatper(getApplicationContext() , hotpick);
        hotdeal_pager.setAdapter(hotdeal_Adapter);
        hotpick_pager.setAdapter(hotpick_Adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hotdeal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
