package com.snail.education.ui.me.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.snail.education.R;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.manager.SERestManager;
import com.snail.education.protocol.result.MCCommonResult;
import com.snail.education.protocol.result.SEPasswordResult;
import com.snail.education.protocol.service.SEUserService;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.svprogresshud.SVProgressHUD;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserPasswordActivity extends SEBaseActivity {

    private EditText phoneET, codeET, passET;
    private Button submitBtn, codeBtn;

    private String user, code, pass;

    private CountDownTimer mDownTimer = new CountDownTimer(60000, 1000) {

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            codeBtn.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            codeBtn.setText("验证");
            codeBtn.setClickable(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_password);
        setTitleText("修改密码");

        phoneET = (EditText) findViewById(R.id.et_name);
        codeET = (EditText) findViewById(R.id.et_code);
        passET = (EditText) findViewById(R.id.et_password);

        codeBtn = (Button) findViewById(R.id.btn_code);
        codeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(phoneET.getText().toString())) {
                    SVProgressHUD.showInViewWithoutIndicator(UserPasswordActivity.this, "请输入手机号", 2.0f);
                    return;
                }
                getCode();
            }
        });
        submitBtn = (Button) findViewById(R.id.btn_submit);

        RelativeLayout regLL = (RelativeLayout) findViewById(R.id.regLL);
        regLL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(phoneET.getWindowToken(), 0);
                phoneET.clearFocus();
                return false;
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
                    codeET.setText("");
                }
                changeColor();
            }
        });

        codeET.addTextChangedListener(textWatcher);
        passET.addTextChangedListener(textWatcher);


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
        code = codeET.getText().toString().trim();
        if ((user != null) && (!user.equals("")) && (code != null) && (!code.equals(""))
                && (pass != null) && (!pass.equals(""))) {
            submitBtn.setClickable(true);
            submitBtn.setTextColor(Color.LTGRAY);
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reMakePass(user, code, pass);
                }
            });
        } else {
            submitBtn.setClickable(false);
            submitBtn.setTextColor(Color.WHITE);
        }
    }

    private void getCode() {
        codeBtn.setClickable(false);
        mDownTimer.start();
        SEAuthManager am = SEAuthManager.getInstance();
        am.requestSMSAuthCode(phoneET.getText().toString().trim(), new Callback<MCCommonResult>() {
            @Override
            public void success(MCCommonResult result, Response response) {
                if (!result.apicode.equals("10000")) {
                    SVProgressHUD.showInViewWithoutIndicator(UserPasswordActivity.this, result.message, 2.0f);
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    private void reMakePass(String user, String code, String pass) {
        if (user.length() != 11) {
            SVProgressHUD.showInViewWithoutIndicator(UserPasswordActivity.this, "请输入手机号", 2.0f);
            return;
        }
        if (code.length() < 4) {
            SVProgressHUD.showInViewWithoutIndicator(UserPasswordActivity.this, "验证码错误", 2.0f);
            return;
        }
        SVProgressHUD.showInView(this, "请稍后...", true);
        SEUserService userService = SERestManager.getInstance().create(SEUserService.class);
        userService.reMakePass(user, code, pass, new Callback<SEPasswordResult>() {
            @Override
            public void success(SEPasswordResult result, Response response) {
                SVProgressHUD.dismiss(UserPasswordActivity.this);
                if (result.apicode.equals("10000")) {
                    Intent intent = getIntent();
                    intent.putExtra("phone", phoneET.getText().toString().trim());
                    intent.putExtra("password", passET.getText().toString().trim());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    SVProgressHUD.showInViewWithoutIndicator(UserPasswordActivity.this, result.message, 2.0f);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.dismiss(UserPasswordActivity.this);
                SVProgressHUD.showInViewWithoutIndicator(UserPasswordActivity.this, "网络异常", 2.0f);
            }
        });
    }
}
