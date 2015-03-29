package com.snail.education.protocol.manager;

import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.model.SECart;
import com.snail.education.protocol.model.SECourse;
import com.snail.education.protocol.model.SECourseCate;
import com.snail.education.protocol.model.SECourseDetail;
import com.snail.education.protocol.result.SECartResult;
import com.snail.education.protocol.result.SECourseCateResult;
import com.snail.education.protocol.result.SECourseDetailResult;
import com.snail.education.protocol.result.SECourseResult;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.protocol.service.SECourseService;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class SECourseManager {


    private static SECourseManager s_instance;
    private ArrayList<SECourseCate> courseCateList = new ArrayList<SECourseCate>();//课程类别列表
    private ArrayList<SECourse> courseList = new ArrayList<SECourse>();  // 具体课程列表
    private ArrayList<SECart> cartList = new ArrayList<SECart>(); //购物车
    private SECourseDetail courseDetail; // 课程详情
    private SECourseService courseService;

    private SECourseManager() {
        courseService = SERestManager.getInstance().create(SECourseService.class);
    }

    public static SECourseManager getInstance() {
        if (s_instance == null) {
            s_instance = new SECourseManager();
        }
        return s_instance;
    }

    public ArrayList<SECourseCate> getCourseCateList() {
        return courseCateList;
    }

    public ArrayList<SECourse> getCourseList() {
        return courseList;
    }

    public ArrayList<SECart> getCartList() {
        return cartList;
    }

    public SECourseDetail getCourseDetail() {
        return courseDetail;
    }

    public SECourseService getCourseService() {
        return courseService;
    }


    /**
     * 课程类别
     *
     * @param callback
     */
    public void refreshCourse(final SECallBack callback) {
        getCourseService().fetchCourse(new Callback<SECourseCateResult>() {
            @Override
            public void success(SECourseCateResult result, Response response) {
                if (result.state) {
                    courseCateList = result.data;
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
     * 具体课程列表
     *
     * @param callback
     */
    public void refreshCourseList(String free, int tid, int oid, int cid, final SECallBack callback) {
        getCourseService().fetchCourseList(free, tid, oid, cid, new Callback<SECourseResult>() {
            @Override
            public void success(SECourseResult result, Response response) {
                if (result.state) {
                    courseList = result.data;
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
     * 我的学习进度课程列表
     *
     * @param callback
     */
    public void refreshMyCourseList(int sta, int uid, int page, int limit, final SECallBack callback) {
        getCourseService().fetchMyCourseList(sta, uid, page, limit, new Callback<SECourseResult>() {
            @Override
            public void success(SECourseResult result, Response response) {
                if (result.state) {
                    courseList = result.data;
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
     * 具体课程信息
     *
     * @param callback
     */
    public void getCourseDetail(int id, int uid, final SECallBack callback) {
        getCourseService().getCourseDetail(id, uid, new Callback<SECourseDetailResult>() {
            @Override
            public void success(SECourseDetailResult result, Response response) {
                if (result.state) {
                    courseDetail = result.data;
                    courseList = result.other;
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
     * 购物车列表
     *
     * @param callback
     */
    public void fetchCartList(int uid, final SECallBack callback) {
        getCourseService().fetchCartList(uid, new Callback<SECartResult>() {
            @Override
            public void success(SECartResult result, Response response) {
                if (result.state) {
                    cartList = result.data;
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


