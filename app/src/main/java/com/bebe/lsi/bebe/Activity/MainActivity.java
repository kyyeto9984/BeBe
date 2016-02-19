package com.bebe.lsi.bebe.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bebe.lsi.bebe.DB.DBHelper;
import com.bebe.lsi.bebe.GraphClass.GraphData;
import com.bebe.lsi.bebe.GraphClass.MyLineGraph;
import com.bebe.lsi.bebe.GraphClass.MyLineGraphVO;
import com.bebe.lsi.bebe.GraphClass.MyTextureView;
import com.bebe.lsi.bebe.GraphManager;
import com.bebe.lsi.bebe.R;
import com.bebe.lsi.bebe.StaticMethod;
import com.google.android.gms.maps.LocationSource;
import com.handstudio.android.hzgrapherlib.animation.GraphAnimation;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,LocationSource.OnLocationChangedListener{

    private ImageView hotpick,baby_information,baby_diary,community,bebe_function,near_location;
    private TextView camera,gallery;
    public float[] BoyGraph = {50.8f, 55.2f, 59.2f, 62.6f, 65.3f, 67.0f, 69.1f, 70.3f,
            71.9f, 73.5f, 74.5f, 76.2f , 77.7f , 78.5f , 79.3f , 80.2f , 81.0f , 81.8f , 82.7f ,
            83.5f , 84.3f , 85.2f , 86.0f , 87.0f , 88.0f}; // 이 배열들이 점이 찍힐 값임
    private String babyname;
    private RelativeLayout layout,bg;
    private TextView currentheight , avgheight , baby_month , baby_name;
    private GraphData graphData;
    private CardView header;
    private String murl;
    private Uri imageUri;
    private String month = null;
    private String birth = null;
    private String height = null;
    private GraphManager manager;

    private Handler handler = new Handler(){
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DBHelper helper = DBHelper.open(getApplicationContext());
            babyname = getPreference("baby","babyname",null);
            baby_name.setText(babyname);
            Cursor cursor = helper.getBabyMonth(babyname);
            while(cursor.moveToNext()){
                month = cursor.getString(cursor.getColumnIndex("month"));
                height= cursor.getString(cursor.getColumnIndex("height"));
            }
            if(month!=null) {
                currentheight.setText("현재 키 : " + height + "cm");
                avgheight.setText("/ 평균키 :" + BoyGraph[Integer.parseInt(month.trim())-1] + "cm");
                baby_month.setText(month + "개월");
            }
            Cursor c= helper.getBabybirth(babyname);
            String image = null;
            while (c.moveToNext()){
                image = c.getString(c.getColumnIndex("image"));
            }
            Bitmap bit = BitmapFactory.decodeFile(image);
            bg.setBackground((Drawable)new BitmapDrawable(getResources() , bit));
            graphData = new GraphData();
            MyLineGraphVO vo = manager.getGraph(getApplicationContext());
            layout.removeAllViews();
            layout.addView(new MyTextureView(getApplicationContext(), vo));    // RelativeLayout에 그래프 View를 추가
        }
    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = new GraphManager();
        SharedPreferences pref = getSharedPreferences("Login", getApplicationContext().MODE_PRIVATE);
        boolean isAutoLogin = pref.getBoolean("autoLogin", false);
        if (!isAutoLogin) {
            Intent in = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(in);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("BeBe");
        setSupportActionBar(toolbar);

        createInstance();
        SharedPreferences.Editor editor = pref.edit();
        babyname = getPreference("baby" , "babyname", "먹방계의샛별");
        if (!babyname.equals("먹방계의샛별"))
            baby_name.setText(babyname);
        DBHelper helper = DBHelper.open(getApplicationContext());
        Cursor cursor = helper.getBabyMonth(babyname);

        while (cursor.moveToNext()) {
            month = cursor.getString(cursor.getColumnIndex("month"));
            height = cursor.getString(cursor.getColumnIndex("height"));
        }
        Cursor c = helper.getBabybirth(babyname);
        String image = null;
        while (c.moveToNext()) {
            image = c.getString(c.getColumnIndex("image"));
        }
        if (image != null){
            Bitmap bit = BitmapFactory.decodeFile(image);
            bg.setBackground((Drawable) new BitmapDrawable(getResources(), bit));
        }
        editor.putString("month", month);
        editor.commit();
        cursor = helper.getBabybirth(babyname);
        while (cursor.moveToNext()){
            birth = cursor.getString(cursor.getColumnIndex("birth"));
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        if(birth != null) {
            if(month!=null) {
                currentheight.setText("현재 키 : " + height + "cm");
                avgheight.setText("/ 평균키 :" + BoyGraph[Integer.parseInt(month.trim())] + "cm");
                baby_month.setText(month + "개월");
            }
        }
        if(month==null){
            baby_month.setText(pref.getString("babymonth", "1") + "개월");
        }
        graphData = new GraphData();
        MyLineGraphVO vo = manager.getGraph(getApplicationContext());
        layout.addView(new MyTextureView(getApplicationContext(), vo));    // RelativeLayout에 그래프 View를 추가
        UIThread.start();
    }




    private void createInstance() {
        bg = (RelativeLayout)findViewById(R.id.bg);
        hotpick = (ImageView)findViewById(R.id.hotpick);
        baby_information = (ImageView)findViewById(R.id.baby_information);
        baby_diary = (ImageView)findViewById(R.id.baby_diary);
        community = (ImageView)findViewById(R.id.community);
        bebe_function = (ImageView)findViewById(R.id.bebe_function);
        near_location = (ImageView)findViewById(R.id.near_location);
        hotpick.setOnClickListener(this);
        baby_information.setOnClickListener(this);
        baby_diary.setOnClickListener(this);
        community.setOnClickListener(this);
        bebe_function.setOnClickListener(this);
        near_location.setOnClickListener(this);
        camera = (TextView)findViewById(R.id.camera);
        camera.setOnClickListener(this);
        gallery = (TextView)findViewById(R.id.gallery);
        gallery.setOnClickListener(this);
        currentheight = (TextView)findViewById(R.id.currentheight);
        avgheight = (TextView)findViewById(R.id.avgheight);
        baby_month = (TextView)findViewById(R.id.baby_month);
        layout = (RelativeLayout)findViewById(R.id.graph);
        baby_name = (TextView)findViewById(R.id.baby_name);
        header = (CardView)findViewById(R.id.header);
        header.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_addbaby) {
            Intent in = new Intent(getApplicationContext() , AddBabyActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("title" , "BeBe Profile");
            in.putExtra("title" , bundle);
            startActivity(in);
            return true;
        }else if(id == R.id.action_notice){
            Intent in = new Intent(getApplicationContext() , ProfileActivity.class);
            startActivityForResult(in, 1002);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent in = null;
        switch (v.getId()){
            case R.id.near_location :
                in = new Intent(getApplicationContext() , PlaceActivity.class);
                startActivity(in);
                break;
            case R.id.baby_information :
                in = new Intent(getApplicationContext() , InformationActivity.class);
                startActivity(in);
                break;
            case R.id.baby_diary :
                in = new Intent(getApplicationContext() , DiaryActivity.class);
                startActivity(in);
                break;
            case R.id.hotpick :
                in = new Intent(getApplicationContext() , HotdealActivity.class);
                startActivity(in);
                break;
            case R.id.community:
                in = new Intent(getApplicationContext() , CommunityActivity.class);
                startActivity(in);
                break;
            case R.id.bebe_function:
                in = new Intent(getApplicationContext() , FunctionActivity.class);
                startActivity(in);
                break;
            case R.id.header :
                in = new Intent(getApplicationContext() , InputActivity.class);
                startActivity(in);
                break;
            case R.id.camera :
                in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String url = "/BeBe/" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                File sddir = new File( Environment.getExternalStorageDirectory().getAbsolutePath()+"/BeBe/");
                if (!sddir.exists() && !sddir.mkdirs()){
                    Log.i("asdasd", "폴더생성");
                    sddir.mkdirs();
                }
                imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
                murl = Environment.getExternalStorageDirectory() + url;
                in.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(in, 1);
                break;
            case R.id.gallery :
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 100);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1002){
            handler.sendEmptyMessage(0);
            return;
        }
        String resultData = null;
        String name = getPreference("baby","babyname", "먹방계의샛별");
        if(!name.equals("먹방계의샛별")) {
            DBHelper helper = DBHelper.open(getApplicationContext());
            Cursor cursor = helper.getBabyMonth(name);
            while (cursor.moveToNext()){
                resultData = cursor.getInt(cursor.getColumnIndex("month"))+"";
            }
        }
        if(resultData == null){
            resultData = getPreference("baby","babymonth","1");
        }else{
            resultData = (Integer.parseInt(resultData)+1)+"";
        }
        if (requestCode == 1) {
            Log.i("asdasd", resultData);
            Intent in = new Intent(getApplicationContext() , HeightActivity.class);
            in.putExtra("image", murl);
            in.putExtra("month",resultData);
            startActivity(in);
        } else if (requestCode == 100) {
            if (data != null) {
                Log.i("asdasd", resultData);
                String name_Str = getImageNameToUri(MainActivity.this, data.getData());
                murl = name_Str;
                Intent in = new Intent(getApplicationContext() , HeightActivity.class);
                in.putExtra("image",murl);
                in.putExtra("month",resultData);
                startActivityForResult(in, 2002);
            }
        }
    }

    public String getImageNameToUri(Activity activity , Uri uri){
        String[] proj = { MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE };
        Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String imgPath = cursor.getString(column_index);
        return imgPath;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private String getPreference(String tag , String tagData , String defaultData){
        return getSharedPreferences(tag, MODE_PRIVATE).getString(tagData , defaultData);
    }

    public Thread UIThread = new Thread(){
        @Override
        public void run() {
            super.run();
            while(true) {
                if(StaticMethod.GraphReDrawing) {
                    handler.sendEmptyMessage(0);
                    StaticMethod.GraphReDrawing = false;
                }
                try {
                    this.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
