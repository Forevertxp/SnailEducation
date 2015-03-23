package com.snail.education.protocol.manager;

import android.content.Context;
import android.widget.Toast;

import com.snail.education.app.SEConfig;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.model.SEMsg;
import com.snail.education.protocol.model.SEUser;
import com.snail.education.protocol.model.SEUserInfo;
import com.snail.education.protocol.result.SEMsgResult;
import com.snail.education.protocol.result.SEUserInfoResult;
import com.snail.education.protocol.result.SEUserResult;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.protocol.service.SEMsgService;
import com.snail.education.protocol.service.SEUserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class SEMsgManager {

    private static SEMsgManager s_instance;
    private SEMsgService msgService;
    private ArrayList<SEMsg> msgList = new ArrayList<SEMsg>();
    private int uid = 0;

    private SEMsgManager() {
        msgService = SERestManager.getInstance().create(SEMsgService.class);
    }

    public static SEMsgManager getInstance() {
        if (s_instance == null) {
            s_instance = new SEMsgManager();
        }

        return s_instance;
    }

    public SEMsgService getMsgService() {
        return msgService;
    }

    public ArrayList<SEMsg> getMsgList() {
        return msgList;
    }

    public void refresh(int page, final SECallBack callback) {
        if (SEAuthManager.getInstance().getAccessUser() != null) {
            uid = Integer.valueOf(SEAuthManager.getInstance().getAccessUser().getId());
        }
        msgService.fetchMsg(uid, page, new Callback<SEMsgResult>() {
            @Override
            public void success(SEMsgResult result, Response response) {
                if (result.state) {
                    msgList = result.data;
                }
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

