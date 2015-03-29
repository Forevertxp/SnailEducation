package com.snail.education.protocol.manager;

import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.model.SEAdvertisement;
import com.snail.education.protocol.model.SECourse;
import com.snail.education.protocol.model.SEIndexCount;
import com.snail.education.protocol.model.SESearch;
import com.snail.education.protocol.result.SEAdResult;
import com.snail.education.protocol.result.SECourseResult;
import com.snail.education.protocol.result.SEIndexCountResult;
import com.snail.education.protocol.result.SESearchResult;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.protocol.service.SEIndexService;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by tianxiaopeng on 15-1-20.
 */
public class SEIndexManager {

    private static SEIndexManager s_instance;
    private ArrayList<SECourse> courseList = new ArrayList<SECourse>();  // 具体课程列表
    private ArrayList<SEAdvertisement> adList = new ArrayList<SEAdvertisement>();  // 广告栏
    private ArrayList<SESearch> searchList = new ArrayList<SESearch>(); // 搜索结果
    private SEIndexCount countInfo;
    private SEIndexService indexService;

    private SEIndexManager() {
        indexService = SERestManager.getInstance().create(SEIndexService.class);
    }

    public static SEIndexManager getInstance() {
        if (s_instance == null) {
            s_instance = new SEIndexManager();
        }
        return s_instance;
    }

    public SEIndexService getIndexService() {
        return indexService;
    }

    public ArrayList<SECourse> getCourseList() {
        return courseList;
    }

    public ArrayList<SEAdvertisement> getAdList() {
        return adList;
    }

    public SEIndexCount getCountInfo() {
        return countInfo;
    }

    public ArrayList<SESearch> getSearchList() {
        return searchList;
    }

    /**
     * 广告信息
     *
     * @param callback
     */
    public void fetchAdInfo(int type, final SECallBack callback) {
        getIndexService().fetchAdInfo(type, new Callback<SEAdResult>() {
            @Override
            public void success(SEAdResult result, Response response) {
                if (result.state) {
                    adList = result.data;
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
     * 首页机构、学员等数量信息
     *
     * @param callback
     */
    public void fetchIndexCount(final SECallBack callback) {
        getIndexService().fetchIndexCount(new Callback<SEIndexCountResult>() {
            @Override
            public void success(SEIndexCountResult result, Response response) {
                if (result.state) {
                    countInfo = result.data;
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
    public void fetchIndexCourse(int limit, final SECallBack callback) {
        getIndexService().fetchIndexCourse(limit, new Callback<SECourseResult>() {
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
     * 首页搜索
     *
     * @param callback
     */
    public void search(String opt, String key, int page, int limit, final SECallBack callback) {
        getIndexService().search(opt, key, page, limit, new Callback<SESearchResult>() {
            @Override
            public void success(SESearchResult result, Response response) {
                if (result.state) {
                    searchList = result.data;
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
