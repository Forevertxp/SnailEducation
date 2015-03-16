package com.snail.education.protocol.manager;

import android.content.Context;
import android.widget.Toast;

import com.snail.education.app.SEConfig;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.model.SEUser;
import com.snail.education.protocol.model.SEUserInfo;
import com.snail.education.protocol.result.SEUserInfoResult;
import com.snail.education.protocol.result.SEUserResult;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.protocol.service.SEUserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class SEUserManager {


    private final static String USERMANAGER_CONFIG_FILENAME = "usermanager.dat";

    private static SEUserManager s_instance;
    private SEUserService _userService;
    private SEUserInfo userInfo;
    private SEUserResult regResult;

    private SEUserManager() {
        _userService = SERestManager.getInstance().create(SEUserService.class);
    }

    public static SEUserManager getInstance() {
        if (s_instance == null) {
            s_instance = new SEUserManager();
        }

        return s_instance;
    }

    public SEUserService getUserService() {
        return _userService;
    }

    public SEUserInfo getUserInfo() {
        return userInfo;
    }

    public SEUserResult getRegResult() {
        return regResult;
    }

    public void logout() {
        SEAuthManager.getInstance().clearAccessUser();
        try {
            FileOutputStream fos = SEConfig.getInstance().getContext().openFileOutput(
                    USERMANAGER_CONFIG_FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(null);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void regUser(String sn, String user, String pass, String repass, final SECallBack callback) {
        _userService.regUser(sn, user, pass, repass, new Callback<SEUserResult>() {
            @Override
            public void success(SEUserResult result, Response response) {
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

                regResult = result;

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


    public void refreshCurrentUserInfo(final SECallBack callback) {
        SEAuthManager am = SEAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            callback.failure(new ServiceError(-1, "尚未验证身份"));
            return;
        }
        if (SEAuthManager.getInstance().getAccessUser() != null) {
            final int uid = Integer.valueOf(SEAuthManager.getInstance().getAccessUser().getId());
            _userService.fetchInformation(uid, new Callback<SEUserInfoResult>() {
                @Override
                public void success(SEUserInfoResult result, Response response) {
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

                    userInfo = result.data;

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
    }

    public void uploadAvatar(final String imagePath, final SECallBack callback) {
        SEAuthManager am = SEAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            callback.failure(new ServiceError(-1, "尚未验证身份"));
            return;
        }

        SEUser currentUser = SEAuthManager.getInstance().getAccessUser();
        TypedFile avatarImageTypedFile = null;
        File avatarImageFile = new File(imagePath);
        avatarImageTypedFile = new TypedFile("application/octet-stream", avatarImageFile);
        _userService.updateUserIcon(currentUser.getId(), avatarImageTypedFile, new Callback<SEUserResult>() {
            @Override
            public void success(SEUserResult result, Response response) {
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

                regResult = result;

                if (callback != null) {
                    callback.success();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void modifyUserMe(final String updatedNickname, final String updatedSignature, final String updateName, final String updateMail,
                             final SECallBack callback) {
        SEAuthManager am = SEAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            callback.failure(new ServiceError(-1, "尚未验证身份"));
            return;
        }

        SEUser currentUser = SEAuthManager.getInstance().getAccessUser();

        String nickname = updatedNickname;
        if (nickname.equals("")) {
            nickname = currentUser.getNickname();
        }

        String signature = updatedSignature;
        if (signature.equals("")) {
            signature = currentUser.getSay();
        }

        String name = updateName;
        if (name.equals("")) {
            name = currentUser.getName();
        }

        String mail = updateMail;
        if (mail.equals("")) {
            mail = currentUser.getMail();
        }

        _userService.updateUser(currentUser.getId(), nickname, signature, name, mail, new Callback<SEUserResult>() {
            @Override
            public void success(SEUserResult result, Response response) {
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

                regResult = result;

                if (callback != null) {
                    callback.success();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (callback != null) {
                    callback.failure(new ServiceError(error));
                }
            }
        });
    }
}

