package com.snail.photo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snail.photo.R;
import com.snail.photo.upload.Constants;
import com.snail.photo.upload.UploadResult;
import com.snail.photo.upload.UploadService;
import com.snail.photo.util.Bimp;
import com.snail.photo.util.FileUtils;
import com.snail.photo.util.ImageItem;
import com.snail.photo.util.PictureUtil;
import com.snail.photo.util.PublicWay;
import com.snail.photo.util.Res;
import com.snail.svprogresshud.SVProgressHUD;

import java.io.File;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


/**
 * @author king
 * @version 2014年10月18日  下午11:48:34
 * @QQ:595163260
 */
public class UploadPicActivity extends Activity {

    private GridView noScrollgridview;
    private GridAdapter adapter;
    private View parentView;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    public static Bitmap bimap;

    private ImageView backIV;
    private EditText msgET;
    private TextView sendTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Res.init(this);
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inSampleSize = 4;
        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.icon_addpic_unfocused, o);
        PublicWay.activityList.add(this);
        parentView = getLayoutInflater().inflate(R.layout.activity_selectimg, null);
        setContentView(parentView);
        Init();
    }

    public void Init() {

        backIV = (ImageView)findViewById(R.id.backIV);
        backIV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < PublicWay.activityList.size(); i++) {
                    if (null != PublicWay.activityList.get(i)) {
                        PublicWay.activityList.get(i).finish();
                    }
                }
            }
        });

        pop = new PopupWindow(UploadPicActivity.this);

        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);

        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

        pop.setWidth(LayoutParams.MATCH_PARENT);
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view
                .findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view
                .findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view
                .findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                photo();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(UploadPicActivity.this,
                        AlbumActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });

        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    Log.i("ddddddd", "----------");
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(UploadPicActivity.this, R.anim.activity_translate_in));
                    pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(UploadPicActivity.this,
                            GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });

        msgET = (EditText) findViewById(R.id.msgET);
        sendTextView = (TextView) findViewById(R.id.selectimg_send);
        sendTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doUpload();
            }
        });

    }

    Thread photoCompress = new Thread(new Runnable() {
        @Override
        public void run() {
            TypedFile[] photos = new TypedFile[9];
            for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
                String tempPath;
                // 判断照片是否有旋转
//            int degree = PictureUtil.readPictureDegree(Bimp.tempSelectBitmap.get(i).imagePath);
                Bitmap bitmap = PictureUtil.getSmallBitmap(Bimp.tempSelectBitmap.get(i).imagePath);
//            if (degree != 0)
//                bitmap = PictureUtil.rotaingImageView(degree, bitmap);
                tempPath = FileUtils.saveBitmap(bitmap, "snail_temp" + i);
                File imageFile = new File(tempPath);
                photos[i] = new TypedFile("application/octet-stream", imageFile);
            }
        }
    });

    public void doUpload() {
        if (Constants.upload_uid.equals(""))
            return;
        SVProgressHUD.showInView(this, "正在上传，请稍后...", true);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.nowthinkgo.com")
                .build();
        UploadService service = restAdapter.create(UploadService.class);
        String msg = msgET.getText().toString();
        TypedFile[] photos = new TypedFile[9];
        for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
            String tempPath;
            // 判断照片是否有旋转
//            int degree = PictureUtil.readPictureDegree(Bimp.tempSelectBitmap.get(i).imagePath);
            Bitmap bitmap = PictureUtil.getSmallBitmap(Bimp.tempSelectBitmap.get(i).imagePath);
//            if (degree != 0)
//                bitmap = PictureUtil.rotaingImageView(degree, bitmap);
            tempPath = FileUtils.saveBitmap(bitmap, "snail_temp" + i);
            File imageFile = new File(tempPath);
            photos[i] = new TypedFile("application/octet-stream", imageFile);
        }
        service.deployMsg(Constants.upload_uid, msg, photos[0], photos[1], photos[2], photos[3], photos[4], photos[5], photos[6], photos[7], photos[8], new Callback<UploadResult>() {
            @Override
            public void success(UploadResult result, Response response) {
                SVProgressHUD.dismiss(UploadPicActivity.this);
                if (result.state) {
                    Constants.upload_result = true;
                    Bimp.tempSelectBitmap.clear();
                    for (int i = 0; i < PublicWay.activityList.size(); i++) {
                        if (null != PublicWay.activityList.get(i)) {
                            PublicWay.activityList.get(i).finish();
                        }
                    }
                } else {
                    SVProgressHUD.showInViewWithoutIndicator(UploadPicActivity.this, result.msg, 2.0f);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Constants.upload_result = false;
                SVProgressHUD.dismiss(UploadPicActivity.this);
                SVProgressHUD.showInViewWithoutIndicator(UploadPicActivity.this, "上传失败", 2.0f);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SVProgressHUD.dismiss(this);
    }

    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
//            loading();
            notifyDataSetChanged();
        }

        public int getCount() {
            if (Bimp.tempSelectBitmap.size() == 9) {
                return 9;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

//        Handler handler = new Handler() {
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case 1:
//                        adapter.notifyDataSetChanged();
//                        break;
//                }
//                super.handleMessage(msg);
//            }
//        };

        // 可能会导致handler 泄露 ，by 田晓鹏
//        public void loading() {
//            new Thread(new Runnable() {
//                public void run() {
//                    while (true) {
//                        if (Bimp.max == Bimp.tempSelectBitmap.size()) {
//                            Message message = new Message();
//                            message.what = 1;
//                            handler.sendMessage(message);
//                            break;
//                        } else {
//                            Bimp.max += 1;
//                            Message message = new Message();
//                            message.what = 1;
//                            handler.sendMessage(message);
//                        }
//                    }
//                }
//            }).start();
//        }
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    private static final int TAKE_PICTURE = 0x000001;

    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {

                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    FileUtils.saveBitmap(bm, fileName);

                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(bm);
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bimap != null) {
            bimap.recycle();
            bimap = null;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            for (int i = 0; i < PublicWay.activityList.size(); i++) {
                if (null != PublicWay.activityList.get(i)) {
                    PublicWay.activityList.get(i).finish();
                }
            }
        }
        return true;
    }

}

