package com.snail.education.protocol.manager;


import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.model.SEInfoDetail;
import com.snail.education.protocol.model.SEInformation;
import com.snail.education.protocol.result.SEInfoDetailResult;
import com.snail.education.protocol.result.SEInfoPraiseResult;
import com.snail.education.protocol.result.SEInformationResult;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.protocol.service.SEInformationService;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by tianxiaopeng on 15-1-6.
 */
public class SEInformationManager {

    private static SEInformationManager s_instance;
    private ArrayList<SEInformation> informationList = new ArrayList<SEInformation>();
    private SEInfoDetail infoDetail;
    private String praiseCount;
    private SEInformationService informationService;

    private SEInformationManager() {
        informationService = SERestManager.getInstance().create(SEInformationService.class);
    }

    public static SEInformationManager getInstance() {
        if (s_instance == null) {
            s_instance = new SEInformationManager();
        }
        return s_instance;
    }

    public ArrayList<SEInformation> getInformation() {
        return informationList;
    }

    public SEInfoDetail getInfoDetail() {
        return infoDetail;
    }

    public String getPraiseCount() {
        return praiseCount;
    }

    public SEInformationService getInformationService() {
        return informationService;
    }


    public void refreshInformation(int limit, final SECallBack callback) {
        getInformationService().fetchInformation(1, 50, limit, 6, new Callback<SEInformationResult>() {
            @Override
            public void success(SEInformationResult result, Response response) {
                if (result.state) {
                    informationList = result.data;
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

    public void loadMoreInformation(int page, int limit, final SECallBack callback) {
        getInformationService().fetchInformation(page, 50, limit, 6, new Callback<SEInformationResult>() {
            @Override
            public void success(SEInformationResult result, Response response) {
                if (result.state) {
                    informationList = result.data;
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
     * 资讯正文
     *
     * @param infoID
     * @param callback
     */
    public void fetchInformationDetail(int infoID, final SECallBack callback) {
        getInformationService().fetchInformationDetail(infoID, new Callback<SEInfoDetailResult>() {
            @Override
            public void success(SEInfoDetailResult result, Response response) {
                if (result.state) {
                    infoDetail = result.data;
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
     * 点赞
     *
     * @param infoID
     * @param callback
     */
    public void praise(int infoID, final SECallBack callback) {
        getInformationService().fetchInformationPraise(infoID, new Callback<SEInfoPraiseResult>() {
            @Override
            public void success(SEInfoPraiseResult result, Response response) {
                praiseCount = result.zan;
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

