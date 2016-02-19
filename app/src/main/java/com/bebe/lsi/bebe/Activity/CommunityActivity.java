package com.bebe.lsi.bebe.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bebe.lsi.bebe.R;

public class CommunityActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView community_freeboard;
    private ImageView community_qna;
    private ImageView community_event;
    private ImageView community_publicbuy;
    private ImageView community_market;
    private ImageView community_meeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("BeBe");
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.community_ColorPrimaryDark));
        }
        community_freeboard = (ImageView)findViewById(R.id.community_freeboard);
        community_qna = (ImageView)findViewById(R.id.community_qna);
        community_event = (ImageView)findViewById(R.id.community_event);
        community_publicbuy = (ImageView)findViewById(R.id.community_publicbuy);
        community_market = (ImageView)findViewById(R.id.community_market);
        community_meeting = (ImageView)findViewById(R.id.community_meeting);
        community_freeboard.setOnClickListener(this);
        community_qna.setOnClickListener(this);
        community_event.setOnClickListener(this);
        community_publicbuy.setOnClickListener(this);
        community_market.setOnClickListener(this);
        community_meeting.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_community, menu);
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

    @Override
    public void onClick(View v) {
        Intent in = null;
        switch (v.getId()){
            case R.id.community_freeboard :
                in = new Intent(Intent.ACTION_VIEW , Uri.parse("http://bebeapp.modoo.at/?link=etszaxqe"));
                startActivity(in);
                break;
            case R.id.community_qna :
                in = new Intent(Intent.ACTION_VIEW , Uri.parse("http://bebeapp.modoo.at/?link=d0fjl3yd"));
                startActivity(in);
                break;
            case R.id.community_event :
                in = new Intent(Intent.ACTION_VIEW , Uri.parse("http://bebeapp.modoo.at/?link=dhjzxj17"));
                startActivity(in);
                break;
            case R.id.community_publicbuy :
                in = new Intent(Intent.ACTION_VIEW , Uri.parse("http://bebeapp.modoo.at/?link=aufwdrot"));
                startActivity(in);
                break;
            case R.id.community_market :
                //in = new Intent(Intent.ACTION_VIEW , Uri.parse("http://bebeapp.modoo.at/?link=etszaxqe"));
                break;
            case R.id.community_meeting :
               //in = new Intent(Intent.ACTION_VIEW , Uri.parse("http://bebeapp.modoo.at/?link=etszaxqe"));
                break;
        }
    }
}
