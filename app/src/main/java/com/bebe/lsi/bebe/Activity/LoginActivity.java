package com.bebe.lsi.bebe.Activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bebe.lsi.bebe.Http.WebServerConnect;
import com.bebe.lsi.bebe.R;
import com.dd.processbutton.iml.ActionProcessButton;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    // 네이버로그인 API
    private OAuthLogin mOAuthLoginModule;
    private final String OAUTH_CLIENT_ID = "UhIWIWfwsSjT6EARYCWq";
    private final String OAUTH_CLIENT_SECRET = "3FLLoy3N4J";
    private final String OAUTH_CLIENT_NAME = "MyBeBe";
    private final String OAUTH_CALLBACK_INTENT = "com.lsj.cococ.mybebe.LoginActivity";
    private OAuthLoginButton mOAuthLoginButton;
    private String accessToken;
    private String tokenType;
    // 개인정보
    private String email;
    private String nickname;
    private String age;
    private String gender;
    private String birthday;
    private String PhoneNumber;
    // UI
    private ActionProcessButton login_btn; // Login Button
    private EditText user_email; // Login
    private EditText password; // Login
    private ScrollView register_layout; // Register Layout
    private LinearLayout Login_layout; //  Login Layout
    private Button register_btn; // Register Text (Button)
    private ImageView title;
    private CheckBox idsave;
    private CheckBox autoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createInstance();
        setTranslucentStatus(true);
        PhoneNumber = getPhoneNumber();
        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(LoginActivity.this, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME, OAUTH_CALLBACK_INTENT);
        mOAuthLoginButton = (OAuthLoginButton) findViewById(R.id.buttonOAuthLoginImg);
        mOAuthLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler); // 네이버 로그인 핸들러실행
                mOAuthLoginModule.startOauthLoginActivity(LoginActivity.this, mOAuthLoginHandler);
            }
        });
    }

    private void createInstance(){
        login_btn = (ActionProcessButton)findViewById(R.id.login_btn);
        user_email = (EditText)findViewById(R.id.user_email);
        password = (EditText)findViewById(R.id.password);
        register_btn = (Button)findViewById(R.id.register_btn);
        register_layout = (ScrollView)findViewById(R.id.register);
        Login_layout = (LinearLayout)findViewById(R.id.Login);
        title = (ImageView)findViewById(R.id.title);
        idsave = (CheckBox)findViewById(R.id.idsave);
        idsave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SharedPreferences pref = getSharedPreferences("Login", getApplicationContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("idsave", true);
                    editor.commit();
                }else{
                    SharedPreferences pref = getSharedPreferences("Login", getApplicationContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("idsave" , false);
                    editor.putString("idsaveValue", null);
                    editor.commit();
                }
            }
        });
        autoLogin = (CheckBox)findViewById(R.id.autoLogin);
        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SharedPreferences pref = getSharedPreferences("Login", getApplicationContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("autoLogin" , true);
                    editor.commit();
                }else{
                    SharedPreferences pref = getSharedPreferences("Login", getApplicationContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("autoLogin" , false);
                    editor.commit();
                }
            }
        });
        register_btn.setOnClickListener(this);
        login_btn.setOnClickListener(this);
        SharedPreferences pref = getSharedPreferences("Login" , getApplicationContext().MODE_PRIVATE);
        boolean isIdSave = pref.getBoolean("idsave", false);
        String userEmailValue = pref.getString("idsaveValue", null);
        if(isIdSave){
            if(userEmailValue != null) {
                idsave.setChecked(true);
                user_email.setText(userEmailValue);
            }
        }
    }

    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                accessToken = mOAuthLoginModule.getAccessToken(getApplicationContext());
                tokenType = mOAuthLoginModule.getTokenType(getApplicationContext());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                            XmlPullParser parser = factory.newPullParser();
                            URL url = new URL("https://apis.naver.com/nidlogin/nid/getUserProfile.xml");
                            HttpURLConnection conn = null;
                            conn = (HttpURLConnection)url.openConnection();
                            conn.setRequestProperty("User-Agent","curl/7.12.1 (i686-redhat-linux-gnu) libcurl/7.12.1 OpenSSL/0.9.7a zlib/1.2.1.2 libidn/0.5.6");
                            conn.setRequestProperty("Host","apis.naver.com");
                            conn.setRequestProperty("Pragma","no-cache");
                            conn.setRequestProperty("Accept","*/*");
                            conn.setRequestProperty("Authorization", tokenType+" "+accessToken);
                            conn.setDoOutput(true);
                            conn.setDoInput(true);
                            OutputStream osw = conn.getOutputStream();
                            osw.write(1024);
                            osw.flush();

                            InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                            parser.setInput(tmp); // xmlPullParser에 InputStream 을 연결
                            int eventType = parser.getEventType(); // 이벤트타입?을 받아와서
                            while(eventType != XmlPullParser.END_DOCUMENT){ // xml document가 끝났는지체크?

                                switch (eventType){
                                    case XmlPullParser.START_TAG:
                                        String tagName = parser.getName();
                                        if(tagName.equals("email")){
                                            email = parser.nextText();
                                        }else if(tagName.equals("nickname")){
                                            nickname = parser.nextText();
                                        }else if(tagName.equals("age")){
                                            age = parser.nextText();
                                        }else if(tagName.equals("gender")){
                                            gender = parser.nextText();
                                        }else if(tagName.equals("birthday")){
                                            birthday = parser.nextText();
                                        }
                                        break;
                                    case XmlPullParser.END_TAG:
                                        break;
                                }
                                eventType = parser.next();
                            }
                            StringBuilder RegisterCheckURL = new StringBuilder();
                            RegisterCheckURL.append("http://bebe.dothome.co.kr/register.php?");
                            RegisterCheckURL.append("nickname="+nickname+"&email="+email+"&number="+PhoneNumber+"&age="+age+"&gender="+gender+"&birth="+birthday);
                            conn.disconnect();
                            Message msg = registerHandler.obtainMessage();
                            Bundle b = new Bundle();
                            WebServerConnect connection = new WebServerConnect();
                            connection.execute(RegisterCheckURL.toString());
                            String data = connection.get();
                            Log.i("D2DD" , "data = "+ data);
                            b.putString("result", data);
                            msg.setData(b);
                            registerHandler.sendMessage(msg);
                        }catch(Exception e){
                            Log.i("D2DD", "exception by .. " + e.getMessage().toString());
                        }
                    }
                }).start();
            } else {
                mOAuthLoginButton.setVisibility(View.VISIBLE);
            }
        };
    };

    private Handler registerHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            Bundle b  = msg.getData();
            String result = b.getString("result");
            if(result.equals("duplication numberCnt = '1'")){
                SharedPreferences pref = getSharedPreferences("Login", getApplicationContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("Login", "Naver");
                editor.putString("nickname", nickname);
                editor.putString("email", email);
                editor.commit();
                Toast.makeText(getApplicationContext() , "로그인 성공" , Toast.LENGTH_LONG).show();
                finish();
            }else {
                SharedPreferences pref = getSharedPreferences("Login", getApplicationContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("Login", "Naver");
                editor.putString("nickname", nickname);
                editor.putString("email", email);
                editor.commit();
                Toast.makeText(getApplicationContext() , "로그인 성공" , Toast.LENGTH_LONG).show();
                finish();
            }
        }
    };

    private String getPhoneNumber() { // 핸드폰 번호 가져오는거
        TelephonyManager systemService = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String PhoneNumber = systemService.getLine1Number();
        PhoneNumber = PhoneNumber.substring(PhoneNumber.length()-10,PhoneNumber.length());
        PhoneNumber="0"+PhoneNumber;
        PhoneNumber = PhoneNumberUtils.formatNumber(PhoneNumber);
        return PhoneNumber;
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
        public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_btn :
                Intent in = new Intent(LoginActivity.this , RegisterActivity.class);
                startActivity(in);
                break;
            case R.id.login_btn :
                login_btn.setText("LOADING");
                login_btn.setProgress(0);
                StringBuilder builder = new StringBuilder();
                builder.append("http://bebe.dothome.co.kr/login.php?");
                if(!user_email.getText().equals("") && !password.getText().equals("")){
                    SharedPreferences pref = getSharedPreferences("Login" , getApplicationContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("idsaveValue" , user_email.getText().toString());
                    editor.commit();
                    login_btn.setProgress(10);
                    builder.append("user_email=");
                    builder.append(user_email.getText().toString());
                    builder.append("&password=");
                    builder.append(password.getText().toString());
                    login_btn.setProgress(30);
                }else{
                    login_btn.setProgress(0);
                    login_btn.setMode(ActionProcessButton.Mode.ENDLESS);
                }
                login_btn.setProgress(50);
                WebServerConnect connect = new WebServerConnect();
                connect.execute(builder.toString());
                String data = null;
                login_btn.setProgress(70);
                try {
                   data = connect.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if(!data.equals("fail")){
                    SharedPreferences pref = getSharedPreferences("Login" , MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    try {
                        editor.putString("nickname",new String(data.getBytes(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    editor.commit();
                    login_btn.setProgress(100);
                    login_btn.setMode(ActionProcessButton.Mode.ENDLESS);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.finish();
                }else{
                    login_btn.setProgress(-1);
                    login_btn.setMode(ActionProcessButton.Mode.ENDLESS);
                    login_btn.setText("ERROR");
                }
                break;
            /*case R.id.birth_register :
                GregorianCalendar calendar = new GregorianCalendar();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(LoginActivity.this, dateSetListener, year, month, day).show();

                break;*/
        }
    }

   /* private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {


        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            String msg = String.format("%d / %d / %d", year,monthOfYear+1, dayOfMonth);
            birth_register.setText(msg);
        }
    };*/
}
