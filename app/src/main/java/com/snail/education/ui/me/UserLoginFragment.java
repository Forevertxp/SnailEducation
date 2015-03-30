package com.snail.education.ui.me;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.snail.education.R;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.ui.me.activity.UserPasswordActivity;
import com.snail.education.ui.me.activity.UserRegActivity;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserLoginFragment extends Fragment implements Callback,
        OnClickListener, PlatformActionListener {

    private static final int MSG_USERID_FOUND = 1;
    private static final int MSG_LOGIN = 2;
    private static final int MSG_AUTH_CANCEL = 3;
    private static final int MSG_AUTH_ERROR = 4;
    private static final int MSG_AUTH_COMPLETE = 5;

    private static final String KEY_CONTENT = "Fragment1:Content";
    private Bundle bundle;
    private TextView regTV;
    private EditText userText, passText;
    private Button loginBtn, findPassBtn;
    private ImageView weixinImage, qqImage, weiboImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            bundle = savedInstanceState.getBundle(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_login, container, false);
        loginBtn = (Button) v.findViewById(R.id.loginBtn);
        findPassBtn = (Button) v.findViewById(R.id.findPassBtn);
        regTV = (TextView) v.findViewById(R.id.registerTextView);
        userText = (EditText) v.findViewById(R.id.usernameEdit);
        passText = (EditText) v.findViewById(R.id.passEdit);
        weiboImage = (ImageView) v.findViewById(R.id.weiboImage);
        qqImage = (ImageView) v.findViewById(R.id.qqImage);
        weixinImage = (ImageView) v.findViewById(R.id.weixinImage);
        regTV.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        findPassBtn.setOnClickListener(this);
        weiboImage.setOnClickListener(this);
        weixinImage.setOnClickListener(this);
        qqImage.setOnClickListener(this);

        userText.addTextChangedListener(new TextWatcher() {

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
                String user = userText.getText().toString().trim();
                if (user != null && (!user.equals(""))) {

                } else {
                    passText.setText("");
                }
            }
        });

        // 点击屏幕其他地方，隐藏输入法
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(userText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(passText.getWindowToken(), 0);
                return false;
            }
        });
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(KEY_CONTENT, bundle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerTextView:
                Intent intent = new Intent(getActivity(), UserRegActivity.class);
                startActivity(intent);
                break;
            case R.id.loginBtn:
                String username = userText.getText().toString();
                String password = passText.getText().toString();
                if (username.equals("")) {
                    Toast.makeText(getActivity(), "请输入用户名", Toast.LENGTH_LONG).show();
                    return;
                }
                if (password.equals("")) {
                    Toast.makeText(getActivity(), "请输入密码", Toast.LENGTH_LONG).show();
                    return;
                }
                login(username, password);
                break;
            case R.id.findPassBtn:
                Intent passIntent = new Intent(getActivity(), UserPasswordActivity.class);
                startActivity(passIntent);
                break;
            case R.id.weiboImage:
                authorize(new SinaWeibo(getActivity()));
                break;
            case R.id.weixinImage:
                authorize(new Wechat(getActivity()));
                break;
            case R.id.qqImage:
                authorize(new QZone(getActivity()));
                break;
        }

    }

    private void login(String username, String password) {
        SEAuthManager.getInstance().authWithUsernamePassword(username, password, new SECallBack() {
            @Override
            public void success() {
                Intent intent = new Intent();
                intent.setAction("com.snail.user.login");
                getActivity().sendBroadcast(intent);
            }

            @Override
            public void failure(ServiceError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void authorize(Platform plat) {
        if (plat.isValid()) {
            String userId = plat.getDb().getUserId();
            if (!TextUtils.isEmpty(userId)) {
                UIHandler.sendEmptyMessage(MSG_USERID_FOUND, this);
                login(plat.getName(), userId, null);
                return;
            }
        }
        plat.setPlatformActionListener(this);
        plat.SSOSetting(true);
        plat.showUser(null);
    }

    public void onComplete(Platform platform, int action,
                           HashMap<String, Object> res) {
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, this);
            login(platform.getName(), platform.getDb().getUserId(), res);
        }
        System.out.println(res);
        System.out.println("------User Name ---------" + platform.getDb().getUserName());
        System.out.println("------User ID ---------" + platform.getDb().getUserId());
    }

    public void onError(Platform platform, int action, Throwable t) {
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, this);
        }
        t.printStackTrace();
    }

    public void onCancel(Platform platform, int action) {
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, this);
        }
    }

    private void login(String plat, String userId, HashMap<String, Object> userInfo) {
        Message msg = new Message();
        msg.what = MSG_LOGIN;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_USERID_FOUND: {
                Toast.makeText(getActivity(), "用户信息已存在，正在跳转登录操作...", Toast.LENGTH_SHORT).show();
            }
            break;
            case MSG_LOGIN: {

                String text = getString(R.string.logining, msg.obj);
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                System.out.println("---------------");

//				Builder builder = new Builder(this);
//				builder.setTitle(R.string.if_register_needed);
//				builder.setMessage(R.string.after_auth);
//				builder.setPositiveButton(R.string.ok, null);
//				builder.create().show();
            }
            break;
            case MSG_AUTH_CANCEL: {
                Toast.makeText(getActivity(), "auth_cancel", Toast.LENGTH_SHORT).show();
                System.out.println("-------MSG_AUTH_CANCEL--------");
            }
            break;
            case MSG_AUTH_ERROR: {
                Toast.makeText(getActivity(), "auth_error", Toast.LENGTH_SHORT).show();
                System.out.println("-------MSG_AUTH_ERROR--------");
            }
            break;
            case MSG_AUTH_COMPLETE: {
                Toast.makeText(getActivity(), "授权成功，正在跳转登录操作…", Toast.LENGTH_SHORT).show();
                System.out.println("--------MSG_AUTH_COMPLETE-------");
            }
            break;
        }
        return false;
    }
}



