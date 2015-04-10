package com.snail.education.ui.course.pay;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.manager.SECourseManager;
import com.snail.education.protocol.manager.SERestManager;
import com.snail.education.protocol.manager.SEUserManager;
import com.snail.education.protocol.model.SECart;
import com.snail.education.protocol.model.SEOrder;
import com.snail.education.protocol.model.SEUser;
import com.snail.education.protocol.result.SEAddCartResult;
import com.snail.education.protocol.result.SEOrderResult;
import com.snail.education.protocol.result.SEWXPayInfoResult;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.protocol.service.SECourseService;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.education.ui.course.pay.weixin.Constants;
import com.snail.education.ui.course.pay.weixin.MD5;
import com.snail.education.ui.course.pay.weixin.Util;
import com.snail.education.ui.course.pay.zhifubao.CartCourseAdapter;
import com.snail.education.ui.course.pay.zhifubao.PayResult;
import com.snail.education.ui.course.pay.zhifubao.SignUtils;
import com.snail.svprogresshud.SVProgressHUD;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CoursePayActivity extends SEBaseActivity {

    //商户PID
    public static final String PARTNER = "2088611418161463";
    //商户收款账号
    public static final String SELLER = "snailedu@126.com";
    //商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMyAO9W5dpGreEpex9Ss81Ka1FuRUil98LlHuBrHvNLCy0dYC4AlxjPzjn7w6yG5qBVJ+AjTqu2wmsHXJt7Xm/JB3jqahFy/mwQCJjHMjFH/fwMUJW3eO+eO4eAjgO6xe0WCiMLREIRUO6LXoF3Tp6olyCorswokAnxLkt4g4QSZAgMBAAECgYEAxhxAWwclj28exGVXj3fQ7ShjKyX4A4wnJUcOWps/GKpvBXmNAqMVhQSg+ebo0q4p4B3ddKehwkxBUCHfXShgwC7xopEnulI2BUpBQlB17eCWaS2NabEL8hC9FnrwPiK4MXIWX3U7dv8P9qdfhWwrdgpJBHDiQzcfMMilZ6XZ/yUCQQDycD9iyfXrcsOaiLIx/mtR6J41VO4DF5+4Pt2oDX3J08OK1mv4uTWhN+dVAULcRDSKjz3tVWhlsBsVQYDCu/Z7AkEA1/C3EP5h7xQtY0dFjrDLWY0lJlXLdCtDkMlTpnONuzCjTwO5DP9u0U8BsXmTEg/b65Z5kYLDjk4GfQu5N1Du+wJBAL5nl/Cvazvaq3Mf7svC5Gi1CCQcqr20/RUIEq/cwLEVZtsQokX6t/sBW+bwEaHK03ULIPjX/iD3GZ4tDsJiOycCQDFfD/wKrUmES3xPZ0/gjB3Fb6D8LLA61A/eeAmukdEipbQDHeQi4qtobPKu4TlX9ug+Vz01sJBwtnsQmyBSmNECQQDC8D3Q4xjur8+K6LqEofwUlzkgkHavJ0oR3SlH5OJMoz2ywvEiMCffUZRt/g/iSUeeorn+RTMaCChTnPsBKmzu";
    //支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";


    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;

    private static final String TAG = "MicroMsg.SDKSample.PayActivity";

    //微信支付
    private IWXAPI api;

    private SEUser currentuser;
    private ListView cartLV;
    private TextView totalPrice;
    private Button zfbPay, wxPay;

    private int pay_type = 0; //0支付宝 1微信

    private String aLiPayCallback = SEConfig.getInstance().getAPIBaseURL() + "api/payCallback";  //支付宝回调地址
    private String wxPayCallback = SEConfig.getInstance().getAPIBaseURL() + "api/wxPayCallback";  //微信支付回调地址

    private SECourseService courseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_pay);
        setTitleText("购物车");
        cartLV = (ListView) findViewById(R.id.cartLV);
        totalPrice = (TextView) findViewById(R.id.totalPrice);

        courseService = SERestManager.getInstance().create(SECourseService.class);
        currentuser = SEAuthManager.getInstance().getAccessUser();

        String id = getIntent().getStringExtra("id");
        buyCourse(id, currentuser.getId());

        zfbPay = (Button) findViewById(R.id.zfbPay);
        zfbPay.setSelected(true);
        wxPay = (Button) findViewById(R.id.wxPay);

        zfbPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zfbPay.setSelected(true);
                wxPay.setSelected(false);
                pay_type = 0;
            }
        });

        wxPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zfbPay.setSelected(false);
                wxPay.setSelected(true);
                pay_type = 1;
            }
        });
    }

    private void buyCourse(String id, String uid) {
        courseService.buyCourse(id, uid, new Callback<SEAddCartResult>() {
            @Override
            public void success(SEAddCartResult result, Response response) {
                initCartList();
            }

            @Override
            public void failure(RetrofitError error) {
                initCartList();
            }
        });

    }

    /**
     * 具体课程信息
     */
    private void initCartList() {

        final SECourseManager courseManager = SECourseManager.getInstance();
        courseManager.fetchCartList(Integer.parseInt(currentuser.getId()), new SECallBack() {
            @Override
            public void success() {
                ArrayList<SECart> cartArrayList = courseManager.getCartList();
                if (cartArrayList.size() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CoursePayActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("提示")
                            .setMessage("你的购物车已空")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
                    return;
                }
                float total = 0;
                for (SECart cart : cartArrayList) {
                    total += Float.parseFloat(cart.getData().getPrice());
                }
                totalPrice.setText("¥" + total);
                CartCourseAdapter adapter = new CartCourseAdapter(CoursePayActivity.this, cartArrayList);
                cartLV.setAdapter(adapter);
                setListViewHeightBasedOnChildren(cartLV);

            }

            @Override
            public void failure(ServiceError error) {

            }
        });
    }

    /**
     * 根据listview内容设置其高度
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if (listItem != null) {
                // This next line is needed before you call measure or else you won't get measured height at all. The listitem needs to be drawn first to know the height.
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();

            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = (totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /*
    创建订单并跳转到支付页面
     */
    public void createOrder(View v) {
        if (currentuser == null) {
            return;
        }
        courseService.createOrder(Integer.parseInt(currentuser.getId()), new Callback<SEOrderResult>() {
            @Override
            public void success(SEOrderResult result, Response response) {
                SEOrder order = result.data;
                if (order == null) {
                    SVProgressHUD.showInViewWithoutIndicator(CoursePayActivity.this, "购物车已空", 2.f);
                    return;
                }
                //支付宝支付
                if (pay_type == 0)
                    pay(order);
                // 微信支付
                if (pay_type == 1) {
                    api = WXAPIFactory.createWXAPI(CoursePayActivity.this, Constants.APP_ID);
                    boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;  //版本是否支持
                    if (isPaySupported)
                        initWXPayInfo(order.getSn());
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(CoursePayActivity.this, "支付成功",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(CoursePayActivity.this, "支付结果确认中",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(CoursePayActivity.this, "支付失败",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(CoursePayActivity.this, "检查结果为：" + msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };


    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void pay(SEOrder order) {
        // 订单
        String orderInfo = getOrderInfo(order.getSn(), order.getBody(), "蜗牛课程", order.getMoney());

        // 对订单做RSA 签名
        String sign = sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(CoursePayActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     */
    public void check(View v) {
        Runnable checkRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(CoursePayActivity.this);
                // 调用查询接口，获取查询结果
                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();

    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * create the order info. 创建订单信息
     */
    public String getOrderInfo(String trade_no, String subject, String body, String price) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        //orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";
        // 从服务端获取到的订单号
        orderInfo += "&out_trade_no=" + "\"" + trade_no + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    public String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
                Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    public String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }


    //-----------------------------------------------------------------------------微信支付--------------------------------------------------------------------------------------
    private void initWXPayInfo(String sn) {
        courseService.fetchWXPayInfo(sn, new Callback<SEWXPayInfoResult>() {
            @Override
            public void success(SEWXPayInfoResult result, Response response) {
                if (result.retmsg.equals("ok")) {
                    nonceStr = result.noncestr;
                    timeStamp = result.timestamp;
                    new GetAccessTokenTask().execute();
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private class GetAccessTokenTask extends AsyncTask<Void, Void, GetAccessTokenResult> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(CoursePayActivity.this, getString(R.string.app_tip), getString(R.string.getting_access_token));
        }

        @Override
        protected void onPostExecute(GetAccessTokenResult result) {
            if (dialog != null) {
                dialog.dismiss();
            }

            if (result.localRetCode == LocalRetCode.ERR_OK) {
                Toast.makeText(CoursePayActivity.this, R.string.get_access_token_succ, Toast.LENGTH_LONG).show();
                Log.d(TAG, "onPostExecute, accessToken = " + result.accessToken);

                GetPrepayIdTask getPrepayId = new GetPrepayIdTask(result.accessToken);
                getPrepayId.execute();
            } else {
                Toast.makeText(CoursePayActivity.this, getString(R.string.get_access_token_fail, result.localRetCode.name()), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected GetAccessTokenResult doInBackground(Void... params) {
            GetAccessTokenResult result = new GetAccessTokenResult();

            String url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                    Constants.APP_ID, APP_SECRET);
            Log.d(TAG, "get access token, url = " + url);

            byte[] buf = Util.httpGet(url);
            if (buf == null || buf.length == 0) {
                result.localRetCode = LocalRetCode.ERR_HTTP;
                return result;
            }

            String content = new String(buf);
            result.parseFrom(content);
            return result;
        }
    }

    private class GetPrepayIdTask extends AsyncTask<Void, Void, GetPrepayIdResult> {

        private ProgressDialog dialog;
        private String accessToken;

        public GetPrepayIdTask(String accessToken) {
            this.accessToken = accessToken;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(CoursePayActivity.this, getString(R.string.app_tip), getString(R.string.getting_prepayid));
        }

        @Override
        protected void onPostExecute(GetPrepayIdResult result) {
            if (dialog != null) {
                dialog.dismiss();
            }

            if (result.localRetCode == LocalRetCode.ERR_OK) {
                Toast.makeText(CoursePayActivity.this, R.string.get_prepayid_succ, Toast.LENGTH_LONG).show();
                sendPayReq(result);
            } else {
                Toast.makeText(CoursePayActivity.this, getString(R.string.get_prepayid_fail, result.localRetCode.name()), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected GetPrepayIdResult doInBackground(Void... params) {

            String url = String.format("https://api.weixin.qq.com/pay/genprepay?access_token=%s", accessToken);
            String entity = genProductArgs();

            Log.d(TAG, "doInBackground, url = " + url);
            Log.d(TAG, "doInBackground, entity = " + entity);

            GetPrepayIdResult result = new GetPrepayIdResult();

            byte[] buf = Util.httpPost(url, entity);
            if (buf == null || buf.length == 0) {
                result.localRetCode = LocalRetCode.ERR_HTTP;
                return result;
            }

            String content = new String(buf);
            Log.d(TAG, "doInBackground, content = " + content);
            result.parseFrom(content);
            return result;
        }
    }

    /**
     * 微信公众平台商户模块和商户约定的密钥
     * <p/>
     * 注意：不能hardcode在客户端，建议genPackage这个过程由服务器端完成
     */
    private static final String PARTNER_KEY = "456587cce316b8c37271c641a650002a";

    private String genPackage(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(PARTNER_KEY); // 注意：不能hardcode在客户端，建议genPackage这个过程都由服务器端完成

        // 进行md5摘要前，params内容为原始内容，未经过url encode处理
        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();

        return URLEncodedUtils.format(params, "utf-8") + "&sign=" + packageSign;
    }

    /**
     * 微信开放平台和商户约定的密钥
     * <p/>
     * 注意：不能hardcode在客户端，建议genSign这个过程由服务器端完成
     */
    private static final String APP_SECRET = "7b557825fe2a1bedffdde3a34bc5c20e";

    /**
     * 微信开放平台和商户约定的支付密钥
     * <p/>
     * 注意：不能hardcode在客户端，建议genSign这个过程由服务器端完成
     */
    private static final String APP_KEY = "9yjurpcYhrBn7gZEpDxhcbbgCgDW7vfOfcf6TcMfe4MnBd2ED4sRo8JpluLNRcMrrwrkvNMj72R7cL8Aw1A43rySVRBtMGrZwOWhYZ9iiayjDjKG7TddseO2vAvUkx8z"; // wxd930ea5d5a258f4f 对应的支付密钥


    private static enum LocalRetCode {
        ERR_OK, ERR_HTTP, ERR_JSON, ERR_OTHER
    }

    private static class GetAccessTokenResult {

        private static final String TAG = "MicroMsg.SDKSample.PayActivity.GetAccessTokenResult";

        public LocalRetCode localRetCode = LocalRetCode.ERR_OTHER;
        public String accessToken;
        public int expiresIn;
        public int errCode;
        public String errMsg;

        public void parseFrom(String content) {

            if (content == null || content.length() <= 0) {
                Log.e(TAG, "parseFrom fail, content is null");
                localRetCode = LocalRetCode.ERR_JSON;
                return;
            }

            try {
                JSONObject json = new JSONObject(content);
                if (json.has("access_token")) { // success case
                    accessToken = json.getString("access_token");
                    expiresIn = json.getInt("expires_in");
                    localRetCode = LocalRetCode.ERR_OK;
                } else {
                    errCode = json.getInt("errcode");
                    errMsg = json.getString("errmsg");
                    localRetCode = LocalRetCode.ERR_JSON;
                }

            } catch (Exception e) {
                localRetCode = LocalRetCode.ERR_JSON;
            }
        }
    }

    private static class GetPrepayIdResult {

        private static final String TAG = "MicroMsg.SDKSample.PayActivity.GetPrepayIdResult";

        public LocalRetCode localRetCode = LocalRetCode.ERR_OTHER;
        public String prepayId;
        public int errCode;
        public String errMsg;

        public void parseFrom(String content) {

            if (content == null || content.length() <= 0) {
                Log.e(TAG, "parseFrom fail, content is null");
                localRetCode = LocalRetCode.ERR_JSON;
                return;
            }

            try {
                JSONObject json = new JSONObject(content);
                if (json.has("prepayid")) { // success case
                    prepayId = json.getString("prepayid");
                    localRetCode = LocalRetCode.ERR_OK;
                } else {
                    localRetCode = LocalRetCode.ERR_JSON;
                }

                errCode = json.getInt("errcode");
                errMsg = json.getString("errmsg");

            } catch (Exception e) {
                localRetCode = LocalRetCode.ERR_JSON;
            }
        }
    }

    private String genNonceStr() {
//        Random random = new Random();
//        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
        return nonceStr;
    }

    private long genTimeStamp() {
//        return System.currentTimeMillis() / 1000;
        return timeStamp;
    }

    /**
     * 建议 traceid 字段包含用户信息及订单信息，方便后续对订单状态的查询和跟踪
     */
    private String getTraceId() {
        return "crestxu_" + genTimeStamp();
    }

    /**
     * 注意：商户系统内部的订单号,32个字符内、可包含字母,确保在商户系统唯一
     */
    private String genOutTradNo() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private long timeStamp;
    private String nonceStr, packageValue;

    private String genSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        int i = 0;
        for (; i < params.size() - 1; i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append(params.get(i).getName());
        sb.append('=');
        sb.append(params.get(i).getValue());

        String sha1 = Util.sha1(sb.toString());
        Log.d(TAG, "genSign, sha1 = " + sha1);
        return sha1;
    }

    private String genProductArgs() {
        JSONObject json = new JSONObject();

        try {
            json.put("appid", Constants.APP_ID);
            String traceId = getTraceId();  // traceId 由开发者自定义，可用于订单的查询与跟踪，建议根据支付用户信息生成此id
            json.put("traceid", traceId);
            nonceStr = genNonceStr();
            json.put("noncestr", nonceStr);

            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair("bank_type", "WX"));
            packageParams.add(new BasicNameValuePair("body", "蜗牛课程"));
            packageParams.add(new BasicNameValuePair("fee_type", "1"));
            packageParams.add(new BasicNameValuePair("input_charset", "UTF-8"));
            packageParams.add(new BasicNameValuePair("notify_url", "http://weixin.qq.com"));
            packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
            packageParams.add(new BasicNameValuePair("partner", Constants.PARTNER_ID));
            packageParams.add(new BasicNameValuePair("spbill_create_ip", "196.168.1.1"));
            packageParams.add(new BasicNameValuePair("total_fee", "1"));
            packageValue = genPackage(packageParams);

            json.put("package", packageValue);
            timeStamp = genTimeStamp();
            json.put("timestamp", timeStamp);

            List<NameValuePair> signParams = new LinkedList<NameValuePair>();
            signParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
            signParams.add(new BasicNameValuePair("appkey", APP_KEY));
            signParams.add(new BasicNameValuePair("noncestr", nonceStr));
            signParams.add(new BasicNameValuePair("package", packageValue));
            signParams.add(new BasicNameValuePair("timestamp", String.valueOf(timeStamp)));
            signParams.add(new BasicNameValuePair("traceid", traceId));
            json.put("app_signature", genSign(signParams));

            json.put("sign_method", "sha1");
        } catch (Exception e) {
            Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }

        return json.toString();
    }

    private void sendPayReq(GetPrepayIdResult result) {

        PayReq req = new PayReq();
        req.appId = Constants.APP_ID;
        req.partnerId = Constants.PARTNER_ID;
        req.prepayId = result.prepayId;
        req.nonceStr = nonceStr;
        req.timeStamp = String.valueOf(timeStamp);
        req.packageValue = "Sign=" + packageValue;

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("appkey", APP_KEY));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
        req.sign = genSign(signParams);

        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        api.sendReq(req);
    }


}

