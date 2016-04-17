package com.snail.education.ui.me.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.snail.education.R;
import com.snail.education.common.RoundProgressBar;
import com.snail.education.common.circleprogress.MasterLayout;
import com.snail.education.database.CourseDB;
import com.snail.education.protocol.model.MCVideo;
import com.snail.svprogresshud.SVProgressHUD;

import java.io.File;
import java.util.List;

/**
 * Created by tianxiaopeng on 16/1/5.
 */
public class DownloadAdapter extends BaseAdapter {


    private Context context;
    private List<CourseDB> courseList;

    private int downloadingProgress;//正在下载的视频的下载进度

    private HttpUtils http;
    private DbUtils db;

    public static final String SDPATH = Environment.getExternalStorageDirectory()
            .getPath() + "/SwiftData";

    public DownloadAdapter(Context context, List<CourseDB> courseList, HttpUtils http) {
        super();
        this.context = context;
        this.courseList = courseList;

        this.db = DbUtils.create(context);
        this.http = http;
    }

    @Override
    public int getCount() {
        if (courseList != null) {
            return courseList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int index) {
        if (courseList != null) {
            return courseList.get(index);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_download_mc, null);
            holder = new ViewHolder();
            holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_size = (TextView) convertView.findViewById(R.id.tv_content);
            holder.iv_start = (ImageView) convertView.findViewById(R.id.iv_start);
            holder.mv_progress = (RoundProgressBar) convertView.findViewById(R.id.circle_progress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final CourseDB course = courseList.get(position);
        holder.tv_title.setText(course.getName());
        String size = "未知";
        if (course.getSize() != 0) {
            int totalSize = (int) (course.getSize() / 1024 / 1024);
            size = totalSize + "M";
        }
        holder.tv_size.setText(size);
        if (course.getIsdone() == 1) {
            holder.mv_progress.setVisibility(View.GONE);
            holder.iv_start.setVisibility(View.GONE);
        } else {
            holder.mv_progress.setVisibility(View.VISIBLE);
            holder.mv_progress.setProgress(course.getProgress());
            holder.iv_start.setVisibility(View.VISIBLE);
        }
        course.setViewHolder(holder);
        String imageUrl = course.getThumb();
        DisplayImageOptions options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, holder.iv_avatar, options);

        holder.mv_progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 已完成下载
                if (course.getIsdone() == 1) {
                    return;
                }
                ViewHolder currentHolder = course.getViewHolder();
                if (!currentHolder.isDownloading) {
                    int downloadingCount = 0;
                    for (CourseDB c : courseList) {
                        if (c.getViewHolder() != null && c.getViewHolder().isDownloading) {
                            downloadingCount++;
                        }
                    }
                    if (downloadingCount >= 3) {
                        SVProgressHUD.showInViewWithoutIndicator(context, "同时只能下载三个", 2.0f);
                        return;
                    }
                    currentHolder.isDownloading = true;
                    currentHolder.iv_start.setBackgroundResource(R.drawable.ic_download_pause);
                    doDownload(course, course.getVideo() + ".mp4", SDPATH + "/swiftvideo" + course.getId() + ".mp4");
                } else {
                    currentHolder.iv_start.setBackgroundResource(R.drawable.ic_download_start);
                    currentHolder.isDownloading = false;
                    doStopDownload(currentHolder, course);
                }
            }
        });
        return convertView;
    }

    public void doDownload(final CourseDB course, String sourceUrl, String targetUrl) {

        if (course.getViewHolder() == null)
            return;

        HttpHandler handler = http.download(sourceUrl, targetUrl,
                true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {

                    @Override
                    public void onStart() {
                        Toast.makeText(context, "开始下载", Toast.LENGTH_SHORT).show();
                        course.getViewHolder().isDownloading = true;
                        try {
                            CourseDB c = db.findById(CourseDB.class, course.getId());
                            if (c != null) {
                                c.setIsdone(2);
                                db.update(c, "isdone");
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        course.getViewHolder().mv_progress.setProgress((int) (current * 1.0 / total * 100));
                        downloadingProgress = (int) (current * 100 / total);
                        if (course.getViewHolder().tv_size.getText().toString().equals("未知")) {
                            int totalSize = (int) (total / 1024 / 1024);
                            course.getViewHolder().tv_size.setText(totalSize + "M");
                            try {
                                CourseDB c = db.findById(CourseDB.class, course.getId());
                                if (c != null) {
                                    c.setSize(total);
                                    db.update(c, "size");
                                }
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        // 更新为已下载
                        try {
                            CourseDB c = db.findById(CourseDB.class, course.getId());
                            if (c != null) {
                                c.setIsdone(1);
                                c.setProgress(100);
                                db.update(c, "isdone", "progress");
                                // 刷新页面
                                courseList.set(getItemPosition(c), c);
                                notifyDataSetChanged();
                            }
                            course.getViewHolder().isDownloading = false;

                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        course.getViewHolder().isDownloading = false;
                        try {
                            CourseDB c = db.findById(CourseDB.class, course.getId());
                            if (c != null) {
                                c.setIsdone(0);
                                db.update(c, "isdone");
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                });
        course.setHandler(handler);
    }


    public void doStopDownload(ViewHolder viewHolder, CourseDB course) {
        //调用cancel()方法停止下载
        course.getHandler().cancel();
        if (viewHolder != null)
            viewHolder.isDownloading = false;
        try {
            CourseDB c = db.findById(CourseDB.class, course.getId());
            if (c != null) {
                c.setProgress(downloadingProgress);
                c.setIsdone(0);
                db.update(c, "progress", "isdone");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private int getItemPosition(CourseDB course) {
        int position = -1;
        for (CourseDB c : courseList) {
            position++;
            if (c.getId() == course.getId())
                return position;
        }
        return position;
    }

    public void stopDownload() {
        for (CourseDB course : courseList) {
            if (course.getHandler() != null)
                course.getHandler().cancel();
        }
    }


    public void deleteDownloadVideo(int position) {
        CourseDB video = courseList.get(position);
        if (video.getHandler() != null) {
            video.getHandler().cancel();
        }
        File file = new File(SDPATH + "/swiftvideo" + video.getId() + ".mp4");
        if (file.exists()) {
            file.delete();
        }
        try {
            db.deleteById(CourseDB.class, video.getId());
            courseList.remove(video);
            notifyDataSetChanged();

            Intent intent = new Intent("com.swiftacademy.count.changed");
            intent.putExtra("downloadCount", courseList.size());
            context.sendBroadcast(intent);

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void addDownloadingVideo(MCVideo videoInfo) {
        CourseDB course = new CourseDB();
        course.setId(Integer.parseInt(videoInfo.id));
        course.setName(videoInfo.name);
        course.setThumb(videoInfo.pic);
        course.setVideo(videoInfo.address);
        try {
            db.save(course);
            courseList.add(course);
            notifyDataSetChanged();

            Intent intent = new Intent("com.swiftacademy.count.changed");
            intent.putExtra("downloadCount", courseList.size());
            context.sendBroadcast(intent);

            //自动开始下载
//            CourseDB dc = courseList.get(getItemPosition(course));  //重新获取course 含viewholder信息
//            doDownload(dc,course.getVideo() + ".mp4", SDPATH + "/swiftvideo" + course.getId() + ".mp4");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void openVideo(int position){
        CourseDB video = courseList.get(position);
        if (video.getIsdone()==1){
            Intent it = new Intent("com.cooliris.media.MovieView");
            it.setAction(Intent.ACTION_VIEW);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Uri data = Uri.parse(SDPATH + "/swiftvideo" + video.getId() + ".mp4");
            it.setDataAndType(data, "video/mp4");
            it.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            context.startActivity(it);
        }
    }

    public class ViewHolder {
        private ImageView iv_avatar;
        private TextView tv_title;
        private TextView tv_size;
        private ImageView iv_start;
        private RoundProgressBar mv_progress;
        private boolean isDownloading;
    }
}