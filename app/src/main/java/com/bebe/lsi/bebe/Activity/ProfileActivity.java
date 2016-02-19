package com.bebe.lsi.bebe.Activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
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
import com.bebe.lsi.bebe.VO.BabyListVO;
import com.handstudio.android.hzgrapherlib.animation.GraphAnimation;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout bg,addbtn ,graph;
    private final String TAG = "ProfileActivity";
    private HorizontalScrollView scrollView;
    private TextView baby_name,baby_month,currentheight,avgheight;
    private EditText babyname_edit,babybirth;
    private Button boy,girl;
    public float[] BoyGraph = {50.8f, 55.2f, 59.2f, 62.6f, 65.3f, 67.0f, 69.1f, 70.3f,
            71.9f, 73.5f, 74.5f, 76.2f , 77.7f , 78.5f , 79.3f , 80.2f , 81.0f , 81.8f , 82.7f ,
            83.5f , 84.3f , 85.2f , 86.0f , 87.0f , 88.0f}; // 이 배열들이 점이 찍힐 값임

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("BeBe");
        setSupportActionBar(toolbar);

        ArrayList<BabyListVO> list = selectToBaby();
        createInstance(list);
    }

    /***
     *
     * Preference의 nickname 정보를 통해 DB에서 아이정보를 ArrayList로 가져옴
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private ArrayList<BabyListVO> selectToBaby() {
        String nickname = getPreference("Login", "nickname", null);
        ArrayList<BabyListVO> list = null;
        list = getBabyList(nickname);
        if(getBabyUpdate(list)){
            Log.i(TAG , "getBabyUpdate Success");
        }
        return list;
    }

    private void viewInitializer(){
        bg = (RelativeLayout)findViewById(R.id.bg);
        addbtn = (RelativeLayout)findViewById(R.id.addbtn);
        graph = (RelativeLayout)findViewById(R.id.graph);
        scrollView = (HorizontalScrollView)findViewById(R.id.scrollView);
        baby_name = (TextView)findViewById(R.id.baby_name);
        baby_month = (TextView)findViewById(R.id.baby_month);
        currentheight = (TextView)findViewById(R.id.currentheight);
        avgheight = (TextView)findViewById(R.id.avgheight);
        babyname_edit = (EditText)findViewById(R.id.babyname);
        babybirth = (EditText)findViewById(R.id.babybirth);
        boy = (Button)findViewById(R.id.boy);
        girl = (Button)findViewById(R.id.girl);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void createInstance(ArrayList<BabyListVO> list) {
        viewInitializer();
        setScrollView(list);
        addbtn.setOnClickListener(this);
        CreateGraph();

        try {

            SharedPreferences pref = getSharedPreferences("baby", MODE_PRIVATE);
            int number = Integer.parseInt(pref.getString("number", "0")) == 0 ? 0 : Integer.parseInt(pref.getString("number", "0")) - 1;

            Drawable drawable = (Drawable) new BitmapDrawable(getResources(), list.get(number).getImage());
            bg.setBackground(drawable);

            baby_name.setText(list.get(number).getName());
            baby_month.setText(list.get(number).getMonth() == null ? "1 개월" : list.get(number).getMonth() + "개월");
            if (list.get(number).getHeight().length != 0)
                currentheight.setText("현재 키 : " + list.get(number).getHeight()[list.get(number).getHeight().length-1] + "cm");
            else
                currentheight.setText("현재 키 : 0cm");
            babyname_edit.setText(list.get(number).getName());
            babybirth.setText(list.get(number).getBirth());

            SharedPreferences.Editor editor = pref.edit();
            editor.putString("babyname", list.get(number).getName());
            editor.commit();

            CreateGraph();

            StaticMethod.GraphReDrawing = true;

        }catch (Exception e){}
    }

    private void CreateGraph() {
        GraphManager manager = new GraphManager();
        graph.removeAllViews();
        graph.addView(new MyTextureView(getApplicationContext(),
                manager.getGraph(getApplicationContext())));
    }


    /***
     *  아이의 명수만큼 동적으로 아이이미지 버튼을 생성
     * @param list 아이의 모든정보를 담은 ArrayList
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setScrollView(final ArrayList<BabyListVO> list){
        scrollView.removeAllViews();
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(params);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        for(int i=0;i<list.size();i++){
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(180,180);
            params1.setMargins(30, 30, 30, 30);

            Button btn = new Button(getApplicationContext());
            btn.setLayoutParams(params1);
            btn.setBackground(new BitmapDrawable(getResources(), list.get(i).getImage()));
            btn.setText(list.get(i).getName());
            btn.setTag(list.get(i).getNumber());
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int index = Integer.parseInt(v.getTag().toString())-1;
                    SharedPreferences pref = getSharedPreferences("baby",MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("number", v.getTag().toString());
                    editor.putString("babyname", list.get(index).getName());
                    editor.commit();

                    Drawable drawable = (Drawable) new BitmapDrawable(getResources(), list.get(index).getImage());
                    bg.setBackground(drawable);

                    baby_name.setText(list.get(index).getName());
                    baby_month.setText(list.get(index).getMonth()==null?"1 개월":list.get(index).getMonth()+ "개월");
                    avgheight.setText("/ 평균키 :" + BoyGraph[Integer.parseInt(list.get(index).getMonth()==null?"1":list.get(index).getMonth().trim())] + "cm");
                    if(list.get(index).getHeight().length != 0)
                        currentheight.setText("현재 키 : " + list.get(index).getHeight()[list.get(index).getHeight().length-1] + "cm");
                    else
                        currentheight.setText("현재 키 : 0cm");
                    babyname_edit.setText(list.get(index).getName());
                    babybirth.setText(list.get(index).getBirth());

                    CreateGraph();

                    StaticMethod.GraphReDrawing = true;
                }
            });
            linearLayout.addView(btn);
        }
        scrollView.addView(linearLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.boy :
                boy.setBackground(getResources().getDrawable(R.drawable.genderbtn_pressed));
                boy.setTextColor(getResources().getColor(R.color.ColorPrimary));
                girl.setBackground(getResources().getDrawable(R.drawable.genderbtn_unpressed));
                girl.setTextColor(getResources().getColor(R.color.textColorPrimary));
                break;
            case R.id.girl :
                girl.setBackground(getResources().getDrawable(R.drawable.genderbtn_pressed));
                girl.setTextColor(getResources().getColor(R.color.ColorPrimary));
                boy.setBackground(getResources().getDrawable(R.drawable.genderbtn_unpressed));
                boy.setTextColor(getResources().getColor(R.color.textColorPrimary));
                break;
            case R.id.addbtn:
                Intent in = new Intent(getApplicationContext() , AddBabyActivity.class);
                startActivityForResult(in, 1001);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1001){
            ArrayList<BabyListVO> list = selectToBaby();
            setScrollView(list);
        }
    }

    private String getPreference(String tag , String tagData , String defaultData){
        return getSharedPreferences(tag, MODE_PRIVATE).getString(tagData , defaultData);
    }

    /***
     *
     * @param nickname Preference 에서 가져온 nickname
     * @return DB에서 검색한 아이의 정보 List 리턴
     */
    private ArrayList<BabyListVO> getBabyList(String nickname){

        ArrayList<BabyListVO> list = new ArrayList<>();

        DBHelper helper = DBHelper.open(getApplicationContext());
        Cursor cursor = helper.getBabyDatas(nickname);

        while(cursor.moveToNext()){
            BabyListVO vo = new BabyListVO();
            vo.setName(cursor.getString(cursor.getColumnIndex("name")));
            vo.setBirth(cursor.getString(cursor.getColumnIndex("birth")));
            vo.setGender(cursor.getString(cursor.getColumnIndex("gender")));
            Bitmap bitmap = BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndex("image")));
            vo.setImage(bitmap);
            vo.setNumber(cursor.getInt(cursor.getColumnIndex("number")));
            list.add(vo);
        }
        return list;
    }

    /***
     *
     * @param list nickname에 따른 모든 아이의 리스트
     * @return 성공여부
     */
    private boolean getBabyUpdate(ArrayList<BabyListVO> list){
        boolean isSuccess = false;

        DBHelper helper = DBHelper.open(getApplicationContext());
        for(int i=0;i<list.size();i++){
            Cursor c = helper.getBabyMonth(list.get(i).getName());
            float[] array = new float[c.getCount()];
            String month = null;
            int q=0;

            while(c.moveToNext()){
                isSuccess = true;
                array[q] = c.getFloat(c.getColumnIndex("height"));
                month = c.getString(c.getColumnIndex("month"));
                q++;
            }
            list.get(i).setHeight(array);
            list.get(i).setMonth(month);
        }
        return isSuccess;
    }

}
