package com.guide.xiaoguo.weilee.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.guide.xiaoguo.weilee.R;
import com.guide.xiaoguo.weilee.mode.UserInfo;
import com.guide.xiaoguo.weilee.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private EditText user;
    private EditText pwd;
    private Button submit_btn;
    private Boolean save_flg = false;
    private Tools tools;
    private String result;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private UserInfo userInfo;
    Thread thread;
    RadioButton rb_save_pwd;
    boolean isTrue = false;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        InitView();
        Event_DealWith();
    }

    public void InitView() {
        user = findViewById(R.id.user);
        pwd = findViewById(R.id.pwd);
        submit_btn = findViewById(R.id.submit_btn);
        rb_save_pwd = findViewById(R.id.rb_save_pwd);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        if (sp.getBoolean("save_flag", false)) {
            String username = sp.getString("user", null);
            String password = sp.getString("password", null);
            user.setText(username);
            pwd.setText(password);
            rb_save_pwd.setChecked(true);
        } else {
            user.setText("");
            pwd.setText("");
            rb_save_pwd.setChecked(false);
        }
        userInfo = (UserInfo) getApplication();
    }

    public void Event_DealWith() {
        rb_save_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isTrue = !isTrue;
                rb_save_pwd.setChecked(isTrue);
                Log.i("111111111", "onClick: " + rb_save_pwd.isChecked());
            }
        });
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("submit", "onClick: 1111");
                login(LoginActivity.this);
            }
        });
    }

    public void login(final Context context) {
        sp = getSharedPreferences("config", MODE_PRIVATE);
        editor = sp.edit();
        tools = new Tools();
        pd = ProgressDialog.show(LoginActivity.this, "登陆", "登录中，请稍后...");
        save_flg = rb_save_pwd.isChecked();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("type", "dl");
                    jsonObject.put("account", user.getText().toString().trim());
                    jsonObject.put("pwd", pwd.getText().toString().trim());
                    String json = String.valueOf(jsonObject);
                    result = tools.post(userInfo.getUrl(), json);
                    Log.i("------------", "run: " + result);
                    JSONObject Json = new JSONObject(result);
                    String msg = Json.getString("message");
                    if (msg.equals("success")) {
                        userInfo.setAccount(user.getText().toString());
                        userInfo.setAccount_ID(Json.getString("user_id"));
                        userInfo.setCompany_ID(Json.getString("com_id"));
                        Log.i("company——ID", "run: " + userInfo.getCompany_ID());
                        userInfo.setUserName(Json.getString("name"));
                        userInfo.setCompanyName(Json.getString("com_name"));

                        if (save_flg) {
                            editor.putBoolean("save_flag", true);
                            editor.putString("user", user.getText().toString());
                            editor.putString("password", pwd.getText().toString());
                            editor.commit();
                        } else {
                            editor.putBoolean("save_flag", false);
                            editor.putString("user", "");
                            editor.putString("password", "");
                            editor.commit();
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        pd.dismiss();
                        finish();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(context, "请检查用户名与密码是否输入有误！", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (thread != null)
            thread.interrupt();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (thread != null) {
                thread.interrupt();
            }
            if (pd != null) {
                pd.dismiss();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
