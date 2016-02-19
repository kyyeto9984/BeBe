package com.bebe.lsi.bebe.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bebe.lsi.bebe.DB.DBHelper;
import com.bebe.lsi.bebe.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddBabyActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText babyname;
    private TextView babybirth;
    private ImageView camera_bg;
    private Button boy,girl,addbtn;
    private boolean isBoy = true;
    private String murl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_baby);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("BeBe");
        setSupportActionBar(toolbar);
        createInstance();
    }

    private void createInstance() {

        camera_bg = (ImageView)findViewById(R.id.camera_bg);
        babyname = (EditText)findViewById(R.id.babyname);
        babybirth = (TextView)findViewById(R.id.babybirth);
        boy = (Button)findViewById(R.id.boy);
        girl = (Button)findViewById(R.id.girl);
        addbtn = (Button)findViewById(R.id.addbtn);
        camera_bg.setOnClickListener(this);
        boy.setOnClickListener(this);
        girl.setOnClickListener(this);
        addbtn.setOnClickListener(this);
        babybirth.setOnClickListener(this);
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

        if (id == R.id.action_settings){

        }

        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.camera_bg :
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 100);
                break;
            case R.id.boy :
                boy.setBackground(getResources().getDrawable(R.drawable.genderbtn_pressed));
                boy.setTextColor(getResources().getColor(R.color.ColorPrimary));
                girl.setBackground(getResources().getDrawable(R.drawable.genderbtn_unpressed));
                girl.setTextColor(getResources().getColor(R.color.textColorPrimary));
                isBoy = true;
                break;
            case R.id.girl :
                girl.setBackground(getResources().getDrawable(R.drawable.genderbtn_pressed));
                girl.setTextColor(getResources().getColor(R.color.ColorPrimary));
                boy.setBackground(getResources().getDrawable(R.drawable.genderbtn_unpressed));
                boy.setTextColor(getResources().getColor(R.color.textColorPrimary));
                isBoy = false;
                break;
            case R.id.addbtn :
                SharedPreferences login = getSharedPreferences("Login",MODE_PRIVATE);
                String nickname = login.getString("nickname",null);
                SharedPreferences pref = getSharedPreferences("baby", MODE_PRIVATE);
                String urls = pref.getString("image", null);
                if(murl != null && !babyname.getText().toString().equals("") && !babybirth.getText().toString().equals("")){
                    String gender = null;
                    gender = isBoy==true?"남자":"여자";
                    DBHelper helper = DBHelper.open(getApplicationContext());
                    Log.i("이미지",murl);
                    long cnt = helper.insertColumn(babyname.getText().toString(), babybirth.getText().toString(), murl , nickname , gender);
                    if(cnt != -1) {
                        Toast.makeText(getApplicationContext(), "데이터가 추가되었습니다.", Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("babyname", babyname.getText().toString());
                        editor.commit();
                        finish();
                    }
                }else{
                     Toast.makeText(getApplicationContext() , "데이터를 입력해주세요", Toast.LENGTH_LONG).show();
                 }
                break;
            case R.id.babybirth :
                GregorianCalendar calendar = new GregorianCalendar();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(AddBabyActivity.this, dateSetListener, year, month, day).show();
                break;
        }
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            String msg = String.format("%d.%d.%d", year,monthOfYear+1, dayOfMonth);
            babybirth.setText(msg);
        }
    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (data != null) {
                String name_Str = getImageNameToUri(AddBabyActivity.this, data.getData());
                murl = name_Str;
                Bitmap mImageBitmap = (setImageDecode(AddBabyActivity.this, murl, true));
                Drawable drawable = new BitmapDrawable(getResources(), mImageBitmap);
                camera_bg.setBackground(drawable);
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

    public Bitmap setImageDecode(Activity activity, String myImageUrl, boolean isFirst) {
        Bitmap bitmap = null;
        try {
            Display currentDisplay = activity.getWindowManager().getDefaultDisplay();
            int dw = currentDisplay.getWidth();
            int dh = currentDisplay.getHeight();
            // 이미지가 아니라 이미지의 치수를 로드한다.
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options(); // 이걸로 비트맵의 사이즈등 옵션을 지정할수있음
            bmpFactoryOptions.inJustDecodeBounds = true;
            //inJustDecodeBounds값이 true로 설정되면 decoder가 bitmap object에 대해 메모리를 할당하지 않고, 따라서 bitmap을 반환하지도 않는다.
            //다만 options fields는 값이 채워지기 때문에 Load 하려는 이미지의 크기를 포함한 정보들을 얻어올 수 있다
            // -> 비트맵이 너무큰데 그 비트맵의 정보를 가져오고싶을때 씀
            // -> 비트맵은 가져오지않지만 비트맵의 정보를 가져올수있음
            bitmap = BitmapFactory.decodeFile(myImageUrl, bmpFactoryOptions); // 비트맵의 옵션을넣어서 디코드함
            int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) dh);
            // BitmapFactory.Option.outHeight = isJustDecodeBounds로 가져온 이미지의 height값 / 현재화면의 height
            // Math.ceil = > 반올림 메소드
            int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) dw);
            // 두 비율 다 1보다 크면 이미지의 가로, 세로 중 한쪽이 화면보다 크다.
            if (heightRatio == 0 && widthRatio == 0) {
                if (!isFirst) {
                    Toast.makeText(activity.getApplicationContext(), "사진을 찍어주세요.", Toast.LENGTH_LONG).show();
                }
            }
            if (heightRatio > 1 && widthRatio > 1) { //
                if (heightRatio > widthRatio) {// 높이 비율이 더 커서 그에 따라 맞춘다.
                    bmpFactoryOptions.inSampleSize = heightRatio;
                } else {// 너비 비율이 더 커서 그에 따라 맞춘다.
                    bmpFactoryOptions.inSampleSize = widthRatio;
                }
            }
            // 실제로 디코딩한다.
            bmpFactoryOptions.inJustDecodeBounds = false;
            bmpFactoryOptions.inDither = false;
            /*
            inDither는 이미지를 깔끔하게 처리해주는 옵션이라고 하는데
            어떤 블로거가 false를 하니 이미지 로드시간과 이미지가 더 깔끔하게 처리해주는거같다고함
             */
            bitmap = BitmapFactory.decodeFile(myImageUrl, bmpFactoryOptions);
            // 이미지를 상황에 맞게 회전시킨다
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
                Log.i("asdasd", "OutOfMemoryError MyCamera =" + ex.getMessage());
            }
        }
        return bitmap;
    }
}
