package com.snail.education.ui.course.pay;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.manager.SECourseManager;
import com.snail.education.protocol.manager.SERestManager;
import com.snail.education.protocol.manager.SEUserManager;
import com.snail.education.protocol.model.SECart;
import com.snail.education.protocol.model.SECourse;
import com.snail.education.protocol.model.SECourseDetail;
import com.snail.education.protocol.model.SEOrder;
import com.snail.education.protocol.model.SEUser;
import com.snail.education.protocol.result.SEAddCartResult;
import com.snail.education.protocol.result.SEOrderResult;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.protocol.service.SECourseService;
import com.snail.education.protocol.service.SEInformationService;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.education.ui.course.RelativeCourseAdapter;
import com.snail.svprogresshud.SVProgressHUD;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private SEUser currentuser;
    private ListView cartLV;
    private TextView totalPrice;

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
                pay(order);
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
        String orderInfo = getOrderInfo(order.getBody(), "该测试商品的详细描述", order.getMoney());

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
    public String getOrderInfo(String subject, String body, String price) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

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

}

