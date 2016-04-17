package com.snail.education.ui.me.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.snail.education.R;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.result.SEUserResult;
import com.snail.education.ui.MainActivity;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserLoginActivity extends Activity {

    private ImageView backIV;
    private EditText phoneET;
    private EditText passET;
    private Button loginBtn;
    private TextView forgetTV;

    public static int PASS_FOREGT = 0x0101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_login);

        backIV = (ImageView) findViewById(R.id.iv_back);
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        phoneET = (EditText) findViewById(R.id.et_phone);
        passET = (EditText) findViewById(R.id.et_pass);

        forgetTV = (TextView) findViewById(R.id.tv_forget);
        forgetTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserLoginActivity.this, UserPasswordActivity.class);
                startActivityForResult(intent, PASS_FOREGT);
            }
        });

        loginBtn = (Button) findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        loginBtn.setEnabled(false);
        SEAuthManager am = SEAuthManager.getInstance();
        am.authWithUsernamePassword(phoneET.getText().toString(), passET.getText().toString(), new Callback<SEUserResult>() {
            @Override
            public void success(SEUserResult result, Response response) {
                loginBtn.setEnabled(true);
                int isVisitor = getIntent().getIntExtra("isVisitor", 0);
                if (isVisitor == 1) {
                    Intent intent = getIntent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userInfo", result.data);
                    intent.putExtras(bundle);
                    setResult(1, intent);
                    finish();
                } else {
                    Intent intent = new Intent(UserLoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                loginBtn.setEnabled(true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PASS_FOREGT && resultCode == RESULT_OK) {
            phoneET.setText(data.getStringExtra("phone"));
            passET.setText(data.getStringExtra("password"));
        }
    }
}
