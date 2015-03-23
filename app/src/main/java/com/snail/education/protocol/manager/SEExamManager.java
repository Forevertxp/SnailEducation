package com.snail.education.protocol.manager;

import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.model.SECart;
import com.snail.education.protocol.model.SECourseDetail;
import com.snail.education.protocol.model.SEExam;
import com.snail.education.protocol.model.SEExamDetail;
import com.snail.education.protocol.model.SERemind;
import com.snail.education.protocol.result.SEExamDetailResult;
import com.snail.education.protocol.result.SEExamResult;
import com.snail.education.protocol.result.SERemindResult;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.protocol.service.SEExamService;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class SEExamManager {


    private static SEExamManager s_instance;
    private ArrayList<SEExam> examList = new ArrayList<SEExam>();
    private SERemind remindInfo;
    private SEExamDetail examDetail; // 备考攻略正文
    private SEExamService examService;

    private SEExamManager() {
        examService = SERestManager.getInstance().create(SEExamService.class);
    }

    public static SEExamManager getInstance() {
        if (s_instance == null) {
            s_instance = new SEExamManager();
        }
        return s_instance;
    }


    public ArrayList<SEExam> getExamList() {
        return examList;
    }

    public SEExamDetail getExamDetail() {
        return examDetail;
    }

    public SEExamService getExamService() {
        return examService;
    }

    public SERemind getRemindInfo() {
        return remindInfo;
    }

    /**
     * 备考攻略列表
     *
     * @param callback
     */
    public void refreshExamList(int page, int limit, int length, final SECallBack callback) {
        getExamService().fetchExam(page, limit, length, new Callback<SEExamResult>() {
            @Override
            public void success(SEExamResult result, Response response) {
                if (result.state) {
                    examList = result.data;
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

    /**
     * 备考提醒
     *
     * @param callback
     */
    public void fetchExamTip(final SECallBack callback) {
        getExamService().examRemind("", new Callback<SERemindResult>() {
            @Override
            public void success(SERemindResult result, Response response) {
                if (result.state) {
                    remindInfo = result.data;
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

    /**
     * 备考攻略文章
     *
     * @param callback
     */
    public void fetchExamDetail(String id, final SECallBack callback) {
        getExamService().fetchExamDetail(id, new Callback<SEExamDetailResult>() {
            @Override
            public void success(SEExamDetailResult result, Response response) {
                if (result.state) {
                    examDetail = result.data;
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