package com.bebe.lsi.bebe.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bebe.lsi.bebe.Adapter.GridAdapter;
import com.bebe.lsi.bebe.DB.DBHelper;
import com.bebe.lsi.bebe.R;
import com.bebe.lsi.bebe.StaticMethod;
import com.bebe.lsi.bebe.VO.GridItemVO;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class InputActivity extends AppCompatActivity implements View.OnClickListener{

    private static final long ANIM_DURATION = 700;
    private View layout;
    private Toolbar toolbar;
    private GridView gridlist;
    private ImageView camera;
    private ImageView gallery;
    private String murl;
    private Uri imageUri;
    private GridAdapter Adapater;
    private ArrayList<GridItemVO> list = new ArrayList<>();
    private EditText edit;
    private EditText baby_height_edit;
    private Button input_btn;

    @Override
    public void onBackPressed() {
        setupExitAnimations();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        layout = (View)findViewById(R.id.layout);
        edit = (EditText)findViewById(R.id.edit);
        setupEnterAnimations();
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("BeBe");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        camera = (ImageView)findViewById(R.id.camera);
        camera.setOnClickListener(this);
        gallery = (ImageView)findViewById(R.id.gallery);
        gallery.setOnClickListener(this);
        gridlist = (GridView)findViewById(R.id.gridlist);
        baby_height_edit = (EditText)findViewById(R.id.baby_height_edit);
        input_btn = (Button)findViewById(R.id.input_btn);
        input_btn.setOnClickListener(this);
        getDBList(false);
        Adapater = new GridAdapter(getApplicationContext() , list , getLayoutInflater());
        gridlist.setAdapter(Adapater);
        gridlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String month = list.get(position).getMonth();;
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(InputActivity.this);
                alert_confirm.setMessage("정말로 삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBHelper helper = DBHelper.open(getApplicationContext());
                                boolean isDelete = helper.deleteGraph(month);
                                if(isDelete){
                                    getDBList(isDelete);
                                    Toast.makeText(getApplicationContext(), "성공적으로 삭제되었습니다.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });
    }

    private void setupEnterAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition enterTransition = getWindow().getSharedElementEnterTransition();
            enterTransition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    animateRevealShow(layout);
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                }

                @Override
                public void onTransitionPause(Transition transition) {
                }

                @Override
                public void onTransitionResume(Transition transition) {
                }
            });
        }
    }

    private void getDBList(boolean isDelete) {
        if (isDelete) {
            selectDB();
            Adapater.updateReceiptsList(list);
        } else {
            selectDB();
        }
    }

    private void setupExitAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition sharedElementReturnTransition = getWindow().getSharedElementReturnTransition();
            sharedElementReturnTransition.setStartDelay(ANIM_DURATION);


            Transition returnTransition = getWindow().getReturnTransition();
            returnTransition.setDuration(ANIM_DURATION);
            returnTransition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    animateRevealHide(layout);
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                }

                @Override
                public void onTransitionPause(Transition transition) {
                }

                @Override
                public void onTransitionResume(Transition transition) {
                }
            });
        }
    }

    private void animateRevealShow(View viewRoot) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
            int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
            int finalRadius = viewRoot.getWidth();//Math.max(viewRoot.getWidth(), viewRoot.getHeight());

            Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, finalRadius, 0);
            viewRoot.setVisibility(View.VISIBLE);
            anim.setDuration(ANIM_DURATION);
            anim.start();
        }
    }

    private void animateRevealHide(final View viewRoot) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
            int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
            int initialRadius = viewRoot.getWidth();

            Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, initialRadius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    finish();
                }
            });
            anim.setDuration(ANIM_DURATION);
            anim.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.camera :
                Intent in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
            case R.id.input_btn :
                int index = edit.getText().toString().indexOf("개");
                String resultMonth = edit.getText().toString().substring(0, index);
                String resultHeight =  baby_height_edit.getText().toString().trim();
                String  name = getSharedPreferences("baby", MODE_PRIVATE).getString("babyname", null);
                DBHelper helper = DBHelper.open(getApplicationContext());
                Date date = new Date();
                SharedPreferences pref = getSharedPreferences("Login",MODE_PRIVATE);
                String nickname = pref.getString("nickname",null);
                SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
                String mydate = format.format(date);
                long result = helper.insertGraphData(name , resultHeight , resultMonth , mydate , nickname);
                if(result != -1){
                    Toast.makeText(getApplicationContext(), "아이정보입력이 완료되었습니다."  ,Toast.LENGTH_LONG).show();
                    selectDB();
                    Adapater.updateReceiptsList(list);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            int index = edit.getText().toString().indexOf("개");
            String resultData = edit.getText().toString().substring(0 , index);
            Log.i("asdasd", resultData);
            Intent in = new Intent(InputActivity.this , HeightActivity.class);
            in.putExtra("image", murl);
            in.putExtra("month",resultData);
            startActivity(in);
        } else if (requestCode == 100) {
            if (data != null) {
                int index = edit.getText().toString().indexOf("개");
                String resultData = edit.getText().toString().substring(0 , index);
                Log.i("asdasd", resultData);
                String name_Str = getImageNameToUri(InputActivity.this, data.getData());
                murl = name_Str;
                Intent in = new Intent(InputActivity.this , HeightActivity.class);
                in.putExtra("image",murl);
                in.putExtra("month",resultData);
                startActivityForResult(in, 2002);
            }
        }else if(requestCode == 2002){
            selectDB();
            Adapater.updateReceiptsList(list);
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

    private void selectDB(){
        list = new ArrayList<>();
        String name = getSharedPreferences("baby", MODE_PRIVATE).getString("babyname", null);
        DBHelper helper = DBHelper.open(getApplicationContext());
        Cursor cursor = helper.getHeightArray(name);
        while (cursor.moveToNext()) {
            GridItemVO vo = new GridItemVO();
            vo.setMonth(cursor.getString(cursor.getColumnIndex("month")));
            vo.setHeight(cursor.getString(cursor.getColumnIndex("height")));
            list.add(vo);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                StaticMethod.GraphReDrawing = true;
                finish();
        }
        return true;
    }
}
