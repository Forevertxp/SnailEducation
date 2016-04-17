package com.snail.education.protocol.manager;

import android.content.Context;

import com.snail.education.app.SEConfig;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.model.AccessToken;
import com.snail.education.protocol.model.SEUser;
import com.snail.education.protocol.result.AccessTokenResult;
import com.snail.education.protocol.result.MCCommonResult;
import com.snail.education.protocol.result.SEUserResult;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.protocol.result.ServiceResult;
import com.snail.education.protocol.service.AuthService;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by tianxiaopeng on 15-1-6.
 */
public class SEAuthManager {


    public final static String AUTH_CONFIG_FILENAME = "usermanager.dat";
    private final static String CLIENT_ID = "3ae125d6e9a009a6fcce3f081f4ce5ff";

    private static SEAuthManager s_instance;
    private AuthService _authService;
    private AccessToken _accessToken;
    private SEUser accessUser;

    private SEAuthManager() {
        _authService = SERestManager.getInstance().create(AuthService.class);
        load();
    }

    public static SEAuthManager getInstance() {
        if (s_instance == null) {
            s_instance = new SEAuthManager();
        }

        return s_instance;
    }

    public AccessToken getAccessToken() {
        return _accessToken;
    }

    public SEUser getAccessUser() {
        return accessUser;
    }

    public boolean isAuthenticated() {
        return _accessToken != null || accessUser != null;
    }

    public void requestSMSAuthCode(String cellphone, final Callback<MCCommonResult> callback) {
        _authService.requestSMSAuthCode(cellphone, new Callback<MCCommonResult>() {
            @Override
            public void success(MCCommonResult result, Response response) {
                if (callback != null) {
                    callback.success(result, response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (callback != null) {
                    callback.failure(error);
                }
            }
        });
    }

    /**
     * 手机号 验证码登录 （暂未使用）
     *
     * @param cellphone
     * @param authCode
     * @param callback
     */
    public void verifySMSAuthCode(String cellphone, String authCode, final SECallBack callback) {
        _authService.verifySMSAuthCode(cellphone, CLIENT_ID, authCode, new Callback<AccessTokenResult>() {
            @Override
            public void success(AccessTokenResult result, Response response) {
                if (result == null) {
                    if (callback != null) {
                        callback.failure(new ServiceError(-1, "服务器返回数据出错"));
                    }
                    return;
                }

                if (result.error != null) {
                    if (callback != null) {
                        callback.failure(result.error);
                    }
                    return;
                }

                AccessToken accessToken = result.accessToken;
                if (accessToken == null) {
                    if (callback != null) {
                        callback.failure(new ServiceError(-1, "服务器返回数据出错"));
                    }
                    return;
                }

                // 登录成功返回token
                updateAccessToken(accessToken);

                if (callback != null) {
                    callback.success();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                if (callback != null) {
                    callback.failure(new ServiceError(retrofitError));
                }
            }
        });
    }


    /**
     * 用户名 密码登录
     *
     * @param phone
     * @param password
     * @param callback
     */
    public void authWithUsernamePassword(String phone, String password, final Callback<SEUserResult> callback) {
        _authService.authWithUsernamePassword(phone, password, new Callback<SEUserResult>() {
            @Override
            public void success(SEUserResult result, Response response) {

                SEUser user = result.data;
                if (user == null) {
                    return;
                }
                // 登录成功后返回用户信息，更新本地，通用只保存token即可，这里将用户对象保存至本地
                updateUserInfo(user);

                if (callback != null) {
                    callback.success(result, response);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                if (callback != null) {
                    callback.failure(retrofitError);
                }
            }
        });
    }

    public void clearAccessToken() {
        _accessToken = null;
    }

    /**
     * 服务端登录返回token时
     *
     * @param accessToken
     */
    private void updateAccessToken(AccessToken accessToken) {
        if (accessToken == null) {
            return;
        }

        _accessToken = accessToken;
        save();
    }

    public void clearAccessUser() {
        accessUser = null;
    }

    /**
     * 服务端登录返回user信息时
     *
     * @param user
     */
    public void updateUserInfo(SEUser user) {
        accessUser = user;
        save();
    }

    private void save() {
        try {
            FileOutputStream fos = SEConfig.getInstance().getContext().openFileOutput(AUTH_CONFIG_FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
//            if (_accessToken != null)
//                oos.writeObject(_accessToken);
            oos.writeObject(accessUser);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        try {
            FileInputStream fis = SEConfig.getInstance().getContext().openFileInput(AUTH_CONFIG_FILENAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object readObject = is.readObject();
            is.close();

//            if (readObject != null && readObject instanceof AccessToken) {
//                _accessToken = (AccessToken) readObject;
//            }
            if (readObject != null && readObject instanceof SEUser) {
                accessUser = (SEUser) readObject;
            }
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        }
    }
}
