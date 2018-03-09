package com.guide.xiaoguo.weilee.adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.guide.xiaoguo.weilee.R;

public class UpdatepwdDialog extends Dialog {

  public EditText new_pwd;
  public EditText repeat_pwd;
  public Button commit_pwd;
  public Button cancel_pwd;

    Context  context;
    private View.OnClickListener mClickListener;
    public UpdatepwdDialog(Context context, View.OnClickListener clickListener) {
        super(context);
        this.context = context;
        this.mClickListener = clickListener;
    }

    public UpdatepwdDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.pwddialog);
        new_pwd = findViewById(R.id.new_pwd);
        repeat_pwd = findViewById(R.id.repeat_pwd);
        commit_pwd = findViewById(R.id.commit_pwd);
        cancel_pwd = findViewById(R.id.cancel_pwd);

        Window dialogWindow = this.getWindow();

        WindowManager m = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        // p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.8
        dialogWindow.setAttributes(p);

        commit_pwd.setOnClickListener(mClickListener);
        cancel_pwd.setOnClickListener(mClickListener);

        this.setCancelable(true);
    }
}
