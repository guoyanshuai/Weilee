package com.guide.xiaoguo.weilee.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.guide.xiaoguo.weilee.R;
import com.guide.xiaoguo.weilee.activity.LoginActivity;
import com.guide.xiaoguo.weilee.adapter.UpdatepwdDialog;
import com.guide.xiaoguo.weilee.mode.UserInfo;
import com.guide.xiaoguo.weilee.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class Mine_Fragment extends Fragment {

    private ProgressDialog minepd;
    private Thread mine_thread;

    private EditText mine_account;
    private EditText mine_company;
    private EditText mine_name;
    private EditText mine_phone;
    private EditText mine_email;
    private EditText mine_department;
    private EditText mine_position;

    private Button mine_edit;
    private Button mine_updatepwd;
    private UserInfo userInfo;
    private Tools tools;
    private String result;

    UpdatepwdDialog updatepwdDialog;
    String new_pwd;
    private Button mine_exit;
    private View main_view;

    public Mine_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        main_view = inflater.inflate(R.layout.mine, container, false);
        InitView();
        Events_DealWith();
        return main_view;

    }
    public void InitView() {
        mine_account = main_view.findViewById(R.id.mine_account);
        mine_company = main_view.findViewById(R.id.mine_company);
        mine_name = main_view.findViewById(R.id.mine_name);
        mine_phone = main_view.findViewById(R.id.mine_phone);
        mine_email = main_view.findViewById(R.id.mine_email);
        mine_department = main_view.findViewById(R.id.mine_department);
        mine_position = main_view.findViewById(R.id.mine_position);
        mine_edit = main_view.findViewById(R.id.mine_edit);
        mine_updatepwd = main_view.findViewById(R.id.update_pwd);
        userInfo = (UserInfo) getActivity().getApplication();
        tools = new Tools();
        GetUserInformation();
        mine_exit = main_view.findViewById(R.id.mine_exit);
    }

    public void Events_DealWith() {
        mine_account.setText(userInfo.getAccount());
        mine_account.setEnabled(false);
        mine_account.setFocusableInTouchMode(false);
        mine_company.setText(userInfo.getCompanyName());
        mine_company.setEnabled(false);

        mine_name.setEnabled(false);
        mine_email.setEnabled(false);
        mine_department.setEnabled(false);
        mine_phone.setEnabled(false);

        mine_position.setEnabled(false);
        mine_company.setFocusableInTouchMode(false);
        mine_name.setFocusableInTouchMode(false);
        mine_phone.setFocusableInTouchMode(false);
        mine_email.setFocusableInTouchMode(false);
        mine_department.setFocusableInTouchMode(false);
        mine_position.setFocusableInTouchMode(false);

        mine_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mine_edit.getText().toString().trim().equals("编辑")) {
                    mine_name.setEnabled(true);
                    mine_phone.setEnabled(true);
                    mine_department.setEnabled(true);
                    mine_email.setEnabled(true);
                    mine_position.setEnabled(true);
                    mine_edit.setText("修改");
                    mine_name.setFocusableInTouchMode(true);
                    mine_phone.setFocusableInTouchMode(true);
                    mine_department.setFocusableInTouchMode(true);
                    mine_email.setFocusableInTouchMode(true);
                    mine_position.setFocusableInTouchMode(true);
                } else {
                    mine_edit.setText("编辑");
                    UpdateUserInformation();
                    mine_name.setEnabled(false);
                    mine_phone.setEnabled(false);
                    mine_department.setEnabled(false);
                    mine_email.setEnabled(false);
                    mine_position.setEnabled(false);
                }
            }
        });
        mine_updatepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatepwdDialog = new UpdatepwdDialog(getActivity(), onClickListener);
                updatepwdDialog.setTitle("修改密码");
                updatepwdDialog.setCanceledOnTouchOutside(false);
                updatepwdDialog.show();
            }
        });
        mine_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.commit_pwd:
                    new_pwd = updatepwdDialog.new_pwd.getText().toString().trim();
                    String repeat_pwd = updatepwdDialog.repeat_pwd.getText().toString().trim();
                    if(new_pwd.equals("")){
                        Toast.makeText(getActivity(), "密码不许为空!!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    if (new_pwd.equals(repeat_pwd)) {
                        UpdatePwd();
                        updatepwdDialog.dismiss();
                    } else {
                        Toast.makeText(getActivity(), "请检查两次输入的密码是否一致!", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.cancel_pwd:
                    updatepwdDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    public void UpdateUserInformation() {
        if (minepd != null) {
            minepd = null;
        }
        minepd = ProgressDialog.show(getActivity(), null, "修改中...");
        mine_thread.interrupt();
        mine_thread = null;
        mine_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("type", "yhgl");
                    jsonObject.put("user_id", userInfo.getAccount_ID());
                    jsonObject.put("name", mine_name.getText().toString().trim());
                    jsonObject.put("phone", mine_phone.getText().toString().trim());
                    jsonObject.put("email", mine_email.getText().toString().trim());
                    jsonObject.put("department", mine_department.getText().toString().trim());
                    jsonObject.put("position", mine_position.getText().toString().trim());
                    jsonObject.put("password", "");
                    String json = String.valueOf(jsonObject);
                    result = tools.post(userInfo.getUrl(), json);
                    Log.i("---------", "run: " + result);
                    JSONObject Json = new JSONObject(result);
                    String msg = Json.getString("message");
                    if (msg.equals("success")) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (minepd!=null) {
                                    minepd.dismiss();
                                    minepd = null;
                                }
                                Toast.makeText(getActivity(), "修改成功！", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (minepd!=null) {
                                    minepd.dismiss();
                                    minepd = null;
                                }
                                Toast.makeText(getActivity(), "请检查您的网络！", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (minepd!=null) {
                                minepd.dismiss();
                                minepd = null;
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {

                }
            }
        });
        mine_thread.start();
    }

    public void UpdatePwd() {
        if (minepd != null) {
            minepd = null;
        }
        minepd = ProgressDialog.show(getActivity(), null, "修改中...");
        mine_thread.interrupt();
        mine_thread = null;
        mine_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("type", "yhgl");
                    jsonObject.put("user_id", userInfo.getAccount_ID());
                    jsonObject.put("password", new_pwd);
                    String json = String.valueOf(jsonObject);
                    result = tools.post(userInfo.getUrl(), json);
                    Log.i("---------", "run: " + result);
                    JSONObject Json = new JSONObject(result);
                    String msg = Json.getString("message");
                    if (msg.equals("success")) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (minepd!=null) {
                                    minepd.dismiss();
                                    minepd = null;
                                }
                                Toast.makeText(getActivity(), "修改成功！", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (minepd!=null) {
                                    minepd.dismiss();
                                    minepd = null;
                                }
                                Toast.makeText(getActivity(), "请检查您的网络！", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (minepd!=null) {
                                minepd.dismiss();
                                minepd = null;
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {

                }
            }
        });
        mine_thread.start();
    }

    public void GetUserInformation() {
        minepd = ProgressDialog.show(getActivity(), null, "请稍后...");
        mine_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("type", "yhxx");
                    jsonObject.put("user_id", userInfo.getAccount_ID());
                    String json = String.valueOf(jsonObject);
                    result = tools.post(userInfo.getUrl(), json);
                    Log.i("---------", "run: " + result);
                    JSONObject Json = new JSONObject(result);
                    String msg = Json.getString("message");
                    if (msg.equals("success")) {
                        userInfo.setUserName(Json.getString("name"));
                        userInfo.setTel(Json.getString("phone"));
                        userInfo.setMail(Json.getString("email"));
                        userInfo.setDepartment(Json.getString("department"));
                        userInfo.setPosition(Json.getString("position"));
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (minepd!=null) {
                                    minepd.dismiss();
                                    minepd = null;
                                }
                                Toast.makeText(getActivity(), "请检查您的网络！", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (minepd!=null) {
                                minepd.dismiss();
                                minepd = null;
                            }
                            mine_name.setText(userInfo.getUserName());
                            mine_phone.setText(userInfo.getTel());
                            mine_email.setText(userInfo.getMail());
                            mine_department.setText(userInfo.getDepartment());
                            mine_position.setText(userInfo.getPosition());
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {

                }
            }
        });
        mine_thread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mine_thread != null)
            mine_thread.interrupt();
        if (minepd != null) {
            minepd.dismiss();
            minepd = null;
        }
    }
}
