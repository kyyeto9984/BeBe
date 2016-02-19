package com.bebe.lsi.bebe.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bebe.lsi.bebe.DB.DBHelper;
import com.bebe.lsi.bebe.R;
import com.bebe.lsi.bebe.StaticMethod;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HeightActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView image, up, down;
    private FrameLayout layout;
    private Bitmap mImageBitmap;
    private int y;
    private ImageView selectImage;
    private Button childok;
    private Button finish;
    private int Adult_UpY;
    private int Adult_DownY;
    private int Children_UpY;
    private int Children_DownY;
    private boolean isChildren = false;
    private int resultAdultHeight = 0;
    private int resultChildrenHeight;
    private String Adult_Height = "";
    private int ResultChildrenHeightData;
    private boolean isFinish = false;
    private boolean isSecount = false;
    private String getmonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("어른의 키를 맞춰주세요");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        image = (ImageView) findViewById(R.id.image);
        Intent in = getIntent();
        Bundle b = in.getExtras();
        String myurl = b.getString("image");
        getmonth = b.getString("month");
        Log.i("asdasd", myurl);
        mImageBitmap = (setImageDecode(HeightActivity.this, myurl, true));
        image.setImageBitmap(mImageBitmap);
        up = (ImageView) findViewById(R.id.up);
        down = (ImageView) findViewById(R.id.down);
        up.setClickable(true);
        down.setClickable(true);
        up.setOnTouchListener(touchListener);
        down.setOnTouchListener(touchListener);
    }


    View.OnTouchListener touchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            y = (int) event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: // 터치를 했을 때
                    if (selectImage == null) { // 선택한 이미지가 없으면
                        selectImage = (ImageView) v; // 선택한 이미지에 자이미지를 넣음
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE: // 드래그를 했을 때
                    if (selectImage == (ImageView) v) { // 선택한 이미지가 자일 때
                        selectImage.setY((y - 440)); // 자이미지의 y축값을 드래그하는 값에 -440을 빼서 넣는다
                        // -440이 가장 오차가 적음
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP: // 터치를 탰을때
                    if (selectImage != null) { // 선택한 이미지에 자가 없을 때
                        if (!isChildren) { // 첫번째 아이키를 안쟀을 때
                            // 아이키는 총 두번 재야함 아이의 머리쪽 , 아이의 발쪽
                            if (v.getId() == R.id.up) { // 내가 터치한 컴포넌트의 아이디 값이 위에있는 자일 때
                                Adult_UpY = (int) selectImage.getY(); // 어른의 위쪽의 좌표 변수에 Y좌표를 넣음
                            } else { // 아닐때 밑에 자일때
                                Adult_DownY = (int) selectImage.getY(); // 어른의 아래쪽의 좌표 변수에 Y좌표를 넣음
                            }
                            if (Adult_UpY != 0 && Adult_DownY != 0) { // 만약 어른의 키를 재는걸 성공했을 때
                                resultAdultHeight = Adult_DownY - Adult_UpY; //어른의 위쪽좌표에서 아래쪽 좌표를 뺀값을 변수에 넣음
                            }
                        } else { // 첫번째 아이키를 쟀을 때
                            if (v.getId() == R.id.up) { //내가 터치한 컴포넌트의 아이디 값이 위에있는 자일 때
                                Children_UpY = (int) selectImage.getY(); //  아이의 위쪽의 좌표 변수에 Y좌표를 넣음
                            } else { // 아닐때 밑에 자일때
                                Children_DownY = (int) selectImage.getY(); // 아이의 아래쪽의 좌표 변수에 Y좌표를 넣음
                            }
                            if (Children_DownY == 0) Children_DownY = Adult_DownY; // 만약 아이의 아래쪽 좌표 변수의 값이 0 일떄
                            // -> 어른의키를재고 만약 어른의키를 재지 않으면 어른의키값을 아이키값을 넣음
                            if (Children_UpY != 0 && Children_DownY != 0) { // 아이의 키를 재는게 성공했을 때
                                resultChildrenHeight = Children_DownY - Children_UpY; // 아이의 위쪽좌표에서 아래쪽 좌표를 뺀값을 변수에 넣음
                            }
                        }
                        selectImage = null; // 터치를 떘을 때 자 이미지를 없앰
                        return true;
                    }
                    break;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_height, menu);
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
            if (!isFinish) {
                if(isSecount){
                    Log.i("asdasd", "resultAdultHeight="+resultAdultHeight + " : " + resultChildrenHeight + " :  " + Adult_Height);
                    if (resultAdultHeight != 0 && resultChildrenHeight != 0 && !Adult_Height.equals("")) {
                        ResultChildrenHeightData = ((resultChildrenHeight * Integer.parseInt(Adult_Height)) / resultAdultHeight);
                        Log.i("asdasd", "resultAdultHeight="+ResultChildrenHeightData);
                        checkBabyMonth(ResultChildrenHeightData);
                        Toast.makeText(getApplicationContext(), "신장체크가 완료되었습니다", Toast.LENGTH_LONG).show();
                        StaticMethod.GraphReDrawing = true;
                        finish();
                    }
                }else{
                    Log.i("asdasd", "resultAdultHeight="+resultAdultHeight);
                    MaterialDialog builder = new MaterialDialog.Builder(HeightActivity.this)
                            .autoDismiss(false)
                            .title("어른의 키를 입력해주세요")
                            .widgetColor(getResources().getColor(R.color.ColorPrimaryDark))
                            .inputMaxLength(30, R.color.material_blue_grey_950)
                            .inputType(InputType.TYPE_CLASS_TEXT)
                            .input("어른의 키를 입력해주세요", "", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    String data = input.toString();
                                    Toast.makeText(getApplicationContext(), "아이의 키를 맞춰주세요.", Toast.LENGTH_LONG).show();
                                    getSupportActionBar().setTitle("아이의 키를 맞춰주세요");
                                    isChildren = true;
                                    Adult_Height = data;
                                    Log.i("asdasd", "resultAdultHeight="+resultAdultHeight);
                                    dialog.dismiss();
                                }
                            }).negativeText("취소").callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                    dialog.dismiss();
                                }
                            }).show();
                    isSecount = true;
                    return false;
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkBabyMonth(int height) {
        DBHelper dbHelper = DBHelper.open(getApplicationContext());
        SharedPreferences pref = getSharedPreferences("baby", MODE_PRIVATE);
        String name = pref.getString("babyname", null);
        String nickname = getSharedPreferences("Login", MODE_PRIVATE).getString("nickname",null);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        String formatDate = format.format(date);
        dbHelper.insertGraphData(name, height + "", getmonth.trim(), formatDate ,nickname);
        Cursor cursor = dbHelper.getGraphArray(name);
        Log.i("asdasd",  "결과 = "+cursor.getCount());
        while (cursor.moveToNext()){
            Log.i("asdsad" , "while");
        }
    }

    public Bitmap setImageDecode(Activity activity, String myImageUrl, boolean isFirst) {
        Bitmap bitmap = null;
        try {
            Display currentDisplay = activity.getWindowManager().getDefaultDisplay();
            int dw = currentDisplay.getWidth();
            int dh = currentDisplay.getHeight();
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeFile(myImageUrl, bmpFactoryOptions);
            int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) dh);
            int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) dw);
            if (heightRatio == 0 && widthRatio == 0) {
                if (!isFirst) {
                    Toast.makeText(activity.getApplicationContext(), "사진을 찍어주세요.", Toast.LENGTH_LONG).show();
                }
            }
            if (heightRatio > 1 && widthRatio > 1) {
                if (heightRatio > widthRatio) {
                    bmpFactoryOptions.inSampleSize = heightRatio;
                } else {
                    bmpFactoryOptions.inSampleSize = widthRatio;
                }
            }
            bmpFactoryOptions.inJustDecodeBounds = false;
            bmpFactoryOptions.inDither = false;
            bitmap = BitmapFactory.decodeFile(myImageUrl, bmpFactoryOptions);
            ExifInterface exif = new ExifInterface(myImageUrl);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifDegree = exifOrientationToDegrees(exifOrientation);
            bitmap = rotate(bitmap, exifDegree);
        } catch (Exception e) {
            Toast.makeText(activity.getApplicationContext(), "오류발생", Toast.LENGTH_LONG).show();
        }
        return bitmap;
    }

    public int exifOrientationToDegrees(int exifOrientation){
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90){
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180){
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270){
            return 270;
        }
        return 0;
    }

    public Bitmap rotate(Bitmap bitmap, int degrees){
        if(degrees != 0 && bitmap != null){
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try{
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted){
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex){
                Log.i("asdasd", "OutOfMemoryError MyCamera ="+ex.getMessage());
            }
        }
        return bitmap;
    }
}