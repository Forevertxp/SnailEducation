package com.snail.education.ui.me.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snail.education.R;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.manager.SEUserManager;
import com.snail.education.protocol.model.SEUser;
import com.snail.education.protocol.result.SEUserResult;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.svprogresshud.SVProgressHUD;

import java.util.Date;

public class UserRegActivity extends SEBaseActivity {

    private EditText phoneET, passET, passAgainET;
    private TextView protocolTV;
    private ImageView iv_switch_open, iv_switch_close;
    private Button regBtn;

    private String user, pass, repass;
    private boolean isAgree = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reg);
        setTitleText("注册");

        phoneET = (EditText) findViewById(R.id.et_name);
        passET = (EditText) findViewById(R.id.et_password);
        passAgainET = (EditText) findViewById(R.id.et_password_again);

        LinearLayout regLL = (LinearLayout) findViewById(R.id.regLL);
        regLL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(phoneET.getWindowToken(), 0);
                phoneET.clearFocus();
                return false;
            }
        });


        iv_switch_open = (ImageView) findViewById(R.id.iv_switch_open);
        iv_switch_close = (ImageView) findViewById(R.id.iv_switch_close);

        iv_switch_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iv_switch_open.getVisibility() == View.VISIBLE) {
                    iv_switch_open.setVisibility(View.INVISIBLE);
                    iv_switch_close.setVisibility(View.VISIBLE);
                    isAgree = false;
                } else {
                    iv_switch_open.setVisibility(View.VISIBLE);
                    iv_switch_close.setVisibility(View.INVISIBLE);
                    isAgree = true;
                }
                changeColor();
            }
        });
        iv_switch_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iv_switch_open.getVisibility() == View.VISIBLE) {
                    iv_switch_open.setVisibility(View.INVISIBLE);
                    iv_switch_close.setVisibility(View.VISIBLE);
                    isAgree = false;
                } else {
                    iv_switch_open.setVisibility(View.VISIBLE);
                    iv_switch_close.setVisibility(View.INVISIBLE);
                    isAgree = true;
                }
                changeColor();
            }
        });

        protocolTV = (TextView) findViewById(R.id.tv_protocol);
        regBtn = (Button) findViewById(R.id.btn_reg);

        protocolTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                regUser();
            }
        });

        phoneET.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                user = phoneET.getText().toString().trim();
                if (user != null && (!user.equals(""))) {

                } else {
                    passET.setText("");
                    passAgainET.setText("");
                }
                changeColor();
            }
        });

        passET.addTextChangedListener(textWatcher);
        passAgainET.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1,
                                      int arg2, int arg3) {
        }

        @Override
        public void afterTextChanged(Editable arg0) {
            changeColor();
        }
    };

    // 改变登陆按钮文字颜色
    public void changeColor() {
        user = phoneET.getText().toString().trim();
        pass = passET.getText().toString().trim();
        repass = passAgainET.getText().toString().trim();
        if ((user != null) && (!user.equals(""))
                && (pass != null) && (!pass.equals("")) && (repass != null) && (!repass.equals("")) && isAgree) {
            regBtn.setClickable(true);
            regBtn.setBackgroundResource(R.drawable.button_selector);
        } else {
            regBtn.setClickable(false);
            regBtn.setBackgroundResource(R.drawable.btn_normal);
        }
    }

    private void regUser() {
        String sn = "F" + new Date().getTime() + "d";
        String user = phoneET.getText().toString();
        String pass = passET.getText().toString();
        String repass = passAgainET.getText().toString();
        if (pass.length() < 3) {
            SVProgressHUD.showInViewWithoutIndicator(UserRegActivity.this, "密码不能小于三位", 2.f);
            return;
        }
        if (!pass.equals(repass)) {
            SVProgressHUD.showInViewWithoutIndicator(UserRegActivity.this, "两次密码不一致，请重新输入", 2.f);
            return;
        }
        SEUserManager.getInstance().regUser(sn, user, pass, repass, new SECallBack() {
            @Override
            public void success() {
                SEUserResult regResult = SEUserManager.getInstance().getRegResult();
                if (regResult.state) {
                    SEAuthManager.getInstance().updateUserInfo(regResult.data);
                    new AlertDialog.Builder(UserRegActivity.this)
                            .setTitle("成功")
                            .setMessage("注册成功")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction("com.snail.user.login");
                                    sendBroadcast(intent);
                                    finish();
                                }
                            })
                            .show();
                } else {
                    SVProgressHUD.showInViewWithoutIndicator(UserRegActivity.this, regResult.msg, 2.f);
                }

            }

            @Override
            public void failure(ServiceError error) {
                Toast.makeText(UserRegActivity.this, "请求失败 " + error.message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
