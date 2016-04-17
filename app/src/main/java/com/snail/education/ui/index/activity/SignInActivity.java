package com.snail.education.ui.index.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.snail.education.R;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.manager.SERestManager;
import com.snail.education.protocol.model.SEUser;
import com.snail.education.protocol.result.SESignListResult;
import com.snail.education.protocol.result.SESignResult;
import com.snail.education.protocol.service.SEIndexService;
import com.snail.education.sharesdk.ShareModel;
import com.snail.education.sharesdk.SharePopupWindow;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.education.ui.index.calender.CalendarAdapter;
import com.snail.svprogresshud.SVProgressHUD;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignInActivity extends SEBaseActivity implements OnGestureListener, PlatformActionListener, Callback {

    private GestureDetector gestureDetector = null;
    private CalendarAdapter calV = null;
    private GridView gridView = null;
    private TextView topText = null;
    private static int jumpMonth = 0;      //每次滑动，增加或减去一个月,默认为0（即显示当前月）
    private static int jumpYear = 0;       //滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
    private int year_c = 0;
    private int month_c = 0;
    private int day_c = 0;
    private String currentDate = "";
    private Bundle bd = null;//发送参数
    private Bundle bun = null;//接收参数
    private String ruzhuTime;
    private String lidianTime;
    private String state = "";

    private SEIndexService indexService;
    private SEUser user;
    private TextView shareText;
    private TextView signInCountText;
    private ImageView signIV;

    private String text = "MBA联考通";
    private String imageurl = "http://api.nowthinkgo.com/upload/ad/001.jpg";
    private String title = "MBA联考通——MBA考试神器";
    private String url = "http://www.snailedu.com/test";

    private SharePopupWindow share;

    public SignInActivity() {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        currentDate = sdf.format(date);  //当期日期
        year_c = Integer.parseInt(currentDate.split("-")[0]);
        month_c = Integer.parseInt(currentDate.split("-")[1]);
        day_c = Integer.parseInt(currentDate.split("-")[2]);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        setTitleText("签到");

        indexService = SERestManager.getInstance().create(SEIndexService.class);
        user = SEAuthManager.getInstance().getAccessUser();

        shareText = (TextView) findViewById(R.id.shareText);
        signInCountText = (TextView) findViewById(R.id.signInCount);
        shareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

        bd = new Bundle();//out
        bun = getIntent().getExtras();//in


        if (bun != null && bun.getString("state").equals("ruzhu")) {
            state = bun.getString("state");
            System.out.println("%%%%%%" + state);
        } else if (bun != null && bun.getString("state").equals("lidian")) {

            state = bun.getString("state");
            System.out.println("|||||||||||" + state);
        }

        gestureDetector = new GestureDetector(this);
//		bd=new Bundle();
        calV = new CalendarAdapter(this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c);
        addGridView();
        gridView.setAdapter(calV);

        topText = (TextView) findViewById(R.id.tv_month);
        addTextToTopTextView(topText);

        signIV = (ImageView) findViewById(R.id.iv_sign);
        signIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        initSignInfo();
    }

    private void initSignInfo() {
        if (user == null)
            return;
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        indexService.fetchSignInfo(user.getId(), year, month, new retrofit.Callback<SESignListResult>() {
            @Override
            public void success(SESignListResult result, Response response) {
                if (result.state) {
                    signInCountText.setText("你已经坚持学习了 " + result.count + " 天");
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void signIn() {
        if (user == null)
            return;
        indexService.userSign(user.getId(), new retrofit.Callback<SESignResult>() {
            @Override
            public void success(SESignResult result, Response response) {
                if (result.state) {
                    signInCountText.setText("你已经坚持学习了 " + result.count + " 天");
                } else {
                    SVProgressHUD.showInViewWithoutIndicator(SignInActivity.this, result.msg, 2.0f);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void share() {

        share = new SharePopupWindow(SignInActivity.this);
        share.setPlatformActionListener(SignInActivity.this);
        ShareModel model = new ShareModel();
        model.setImageUrl(imageurl);
        model.setText(text);
        model.setTitle(title);
        model.setUrl(url);
        share.initShareParams(model);
        share.showShareWindow();
        // 显示窗口 (设置layout在PopupWindow中显示的位置)
        share.showAtLocation(SignInActivity.this.findViewById(R.id.main_share),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (share != null) {
            share.dismiss();
        }
    }

    @Override
    public void onCancel(Platform arg0, int arg1) {
        Message msg = new Message();
        msg.what = 0;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onComplete(Platform plat, int action,
                           HashMap<String, Object> res) {
        Message msg = new Message();
        msg.arg1 = 1;
        msg.arg2 = action;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onError(Platform arg0, int arg1, Throwable arg2) {
        Message msg = new Message();
        msg.what = 1;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        int what = msg.what;
        if (what == 1) {
            Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
        }
        if (share != null) {
            share.dismiss();
        }
        return false;
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        int gvFlag = 0;         //每次添加gridview到viewflipper中时给的标记
        if (e1.getX() - e2.getX() > 120) {
            //像左滑动
            addGridView();   //添加一个gridView
            jumpMonth++;     //下一个月

            calV = new CalendarAdapter(this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c);
            gridView.setAdapter(calV);
            addTextToTopTextView(topText);
            gvFlag++;

            return true;
        } else if (e1.getX() - e2.getX() < -120) {
            //向右滑动
            addGridView();   //添加一个gridView
            jumpMonth--;     //上一个月

            calV = new CalendarAdapter(this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c);
            gridView.setAdapter(calV);
            gvFlag++;
            addTextToTopTextView(topText);

            return true;
        }
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return this.gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    //添加头部的年份 闰哪月等信息
    public void addTextToTopTextView(TextView view) {
        StringBuffer textDate = new StringBuffer();
        textDate.append(calV.getShowYear()).append("年").append(
                calV.getShowMonth()).append("月").append("\t");
        view.setText(textDate);
        view.setTextColor(Color.WHITE);
        view.setTypeface(Typeface.DEFAULT_BOLD);
    }

    //添加gridview
    private void addGridView() {

        gridView = (GridView) findViewById(R.id.gridview);

        gridView.setOnTouchListener(new OnTouchListener() {
            //将gridview中的触摸事件回传给gestureDetector
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return SignInActivity.this.gestureDetector.onTouchEvent(event);
            }
        });

        gridView.setOnItemClickListener(new OnItemClickListener() {
            //gridView中的每一个item的点击事件

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                //点击任何一个item，得到这个item的日期(排除点击的是周日到周六(点击不响应))
                int startPosition = calV.getStartPositon();
                int endPosition = calV.getEndPosition();
                if (startPosition <= position + 7 && position <= endPosition - 7) {
                    String scheduleDay = calV.getDateByClickItem(position).split("\\.")[0];  //这一天的阳历
                    //String scheduleLunarDay = calV.getDateByClickItem(position).split("\\.")[1];  //这一天的阴历
                    String scheduleYear = calV.getShowYear();
                    String scheduleMonth = calV.getShowMonth();
//	                  Toast.makeText(CalendarActivity.this, scheduleYear+"-"+scheduleMonth+"-"+scheduleDay, 2000).show();
                    ruzhuTime = scheduleMonth + "月" + scheduleDay + "日";
                    lidianTime = scheduleMonth + "月" + scheduleDay + "日";
                    Intent intent = new Intent();
                    if (state.equals("ruzhu")) {

                        bd.putString("ruzhu", ruzhuTime);
                        System.out.println("ruzhuuuuuu" + bd.getString("ruzhu"));
                    } else if (state.equals("lidian")) {

                        bd.putString("lidian", lidianTime);
                    }
//	                intent.setClass(CalendarActivity.this, HotelActivity.class);
//	                intent.putExtras(bd);
//	                startActivity(intent);
//	                finish();
                }
            }

        });
    }

}
