package com.bebe.lsi.bebe.Activity;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bebe.lsi.bebe.Http.WebServerConnect;
import com.bebe.lsi.bebe.R;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText nickname_register;
    private EditText email_register;
    private EditText password_register;
    private EditText re_password_register;
    private ImageView register_button;
    private final String TAG = "D2DD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTranslucentStatus(true);
        nickname_register = (EditText) findViewById(R.id.nickname_register);
        email_register = (EditText) findViewById(R.id.email_register);
        password_register = (EditText) findViewById(R.id.password_register);
        re_password_register = (EditText) findViewById(R.id.re_password_register);
        register_button = (ImageView) findViewById(R.id.register_button);
        register_button.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
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
        if(!nickname_register.getText().toString().equals("") && !email_register.getText().toString().equals("") &&
                !password_register.getText().toString().equals("") && !re_password_register.getText().toString().equals("") &&
                password_register.getText().toString().equals(re_password_register.getText().toString())){
            Toast.makeText(getApplicationContext(), "회원가입 중..", Toast.LENGTH_LONG).show();
            WebServerConnect connect = new WebServerConnect();
            StringBuffer builder = new StringBuffer();
            builder.append("http://bebe.dothome.co.kr/BeBeRegister.php?");
            builder.append("email=");
            builder.append(email_register.getText().toString());
            builder.append("&nickname=");
            builder.append(nickname_register.getText().toString());
            builder.append("&password=");
            builder.append(password_register.getText().toString());
            Log.i(TAG , builder.toString());
            connect.execute(builder.toString());
            String data = null;
            try{
                data  = connect.get();
            }catch(Exception e){
                Log.i(TAG,e.getMessage());
            }
            if(data.equals("Success")) {
                Toast.makeText(getApplicationContext(), "회원가입 성공!!", Toast.LENGTH_LONG).show();
            }else if(data.equals("duplication emailCnt = '1'")){
                Toast.makeText(getApplicationContext(), "이메일이 중복되었습니다.", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext() , "잘못입력하셨습니다" , Toast.LENGTH_LONG).show();
        }
    }
}
