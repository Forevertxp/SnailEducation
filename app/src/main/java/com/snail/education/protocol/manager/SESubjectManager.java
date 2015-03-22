package com.snail.education.protocol.manager;

import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.model.SECart;
import com.snail.education.protocol.model.SECourse;
import com.snail.education.protocol.model.SECourseCate;
import com.snail.education.protocol.model.SECourseDetail;
import com.snail.education.protocol.model.SESubItem;
import com.snail.education.protocol.model.SESubject;
import com.snail.education.protocol.result.SECartResult;
import com.snail.education.protocol.result.SECourseCateResult;
import com.snail.education.protocol.result.SECourseDetailResult;
import com.snail.education.protocol.result.SECourseResult;
import com.snail.education.protocol.result.SESubItemResult;
import com.snail.education.protocol.result.SESubjectResult;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.protocol.service.SECourseService;
import com.snail.education.protocol.service.SESubjectService;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class SESubjectManager {


    private static SESubjectManager s_instance;
    private ArrayList<SESubject> subjectList = new ArrayList<SESubject>();  // 题库分类
    private ArrayList<SESubItem> itemList = new ArrayList<SESubItem>(); //各类别列表
    private SESubjectService subjectService;

    private SESubjectManager() {
        subjectService = SERestManager.getInstance().create(SESubjectService.class);
    }

    public static SESubjectManager getInstance() {
        if (s_instance == null) {
            s_instance = new SESubjectManager();
        }
        return s_instance;
    }

    public ArrayList<SESubject> getSubjectList() {
        return subjectList;
    }

    public ArrayList<SESubItem> getItemList() {
        return itemList;
    }

    public SESubjectService getSubjectService() {
        return subjectService;
    }

    /**
     * 题库列表
     *
     * @param callback
     */
    public void refreshSubjectList(int page, int limit, int length, final SECallBack callback) {
        getSubjectService().fetchSubject(page, limit, length, new Callback<SESubjectResult>() {
            @Override
            public void success(SESubjectResult result, Response response) {
                if (result.state) {
                    subjectList = result.data;
                    if (callback != null) {
                        callback.success();
                    }
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
     * 各子项题库列表
     *
     * @param callback
     */
    public void refreshSubItem(int page, int limit, int length, String cid, final SECallBack callback) {
        getSubjectService().fetchSubItem(page, limit, length, cid, new Callback<SESubItemResult>() {
            @Override
            public void success(SESubItemResult result, Response response) {
                if (result.state) {
                    itemList = result.data;
                    if (callback != null) {
                        callback.success();
                    }
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


