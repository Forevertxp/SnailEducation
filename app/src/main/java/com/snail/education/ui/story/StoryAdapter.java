package com.snail.education.ui.story;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.snail.education.R;
import com.snail.education.common.NoScrollGridAdapter;
import com.snail.education.common.NoScrollGridView;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.manager.SEComment;
import com.snail.education.protocol.manager.SERestManager;
import com.snail.education.protocol.manager.SEStoryManger;
import com.snail.education.protocol.model.SEStory;
import com.snail.education.protocol.model.SEUser;
import com.snail.education.protocol.result.SEStoryPraiseResult;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.protocol.service.SEStoryService;
import com.snail.education.ui.story.activity.ImagePagerActivity;
import com.snail.svprogresshud.SVProgressHUD;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by tianxiaopeng on 15-1-17.
 */
public class StoryAdapter extends BaseAdapter {


    private Context context;
    private List<SEStory> storyList;

    private static int LIMIT_STORY = 5;

    public StoryAdapter(Context context) {
        super();
        this.context = context;
        updatePresentingStory(1);
    }

    @Override
    public int getCount() {
        return getStoryCount();
    }

    @Override
    public Object getItem(int index) {
        return getInformation(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_story, null);
            holder = new ViewHolder();
            holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_msg = (TextView) convertView.findViewById(R.id.tv_msg);
            holder.imageGridView = (NoScrollGridView) convertView.findViewById(R.id.imageGridView);
            holder.timeText = (TextView) convertView.findViewById(R.id.timeText);
            holder.messageText = (TextView) convertView.findViewById(R.id.messageText);
            holder.likeImage = (ImageView) convertView.findViewById(R.id.likeImage);
            holder.likeText = (TextView) convertView.findViewById(R.id.likeText);
            holder.commentListView = (ListView) convertView.findViewById(R.id.commentListView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SEStory story = storyList.get(position);
        holder.tv_title.setText(story.getUser_nickname());
        holder.tv_content.setText(story.getUser_say());
        holder.tv_msg.setText(story.getMsg());
        String imageUrl = story.getUser_icon();
        Picasso.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_launcher)
                .into(holder.iv_avatar);
        final ArrayList<String> imageUrls = story.getPics();
        if (imageUrls == null || imageUrls.size() == 0) { // 没有图片资源就隐藏GridView
            holder.imageGridView.setVisibility(View.GONE);
        } else {
            holder.imageGridView.setVisibility(View.VISIBLE);
            if (imageUrls.size() < 3) {
                holder.imageGridView.setNumColumns(imageUrls.size());
            } else {
                holder.imageGridView.setNumColumns(3);
            }
            holder.imageGridView.setAdapter(new NoScrollGridAdapter(context, imageUrls));
        }
        // 点击回帖九宫格，查看大图
        holder.imageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                imageBrower(position, imageUrls);
            }
        });
        holder.timeText.setText(story.get_time());
        if (story.get_praised() == 1) {
            holder.likeImage.setBackgroundResource(R.drawable.ic_liked);
        } else {
            holder.likeImage.setBackgroundResource(R.drawable.ic_like);
        }
        holder.likeText.setText(story.getPraise() + "");
        holder.messageText.setText(story.getRep_count() + "");
        MyBaseAdapter adapter = new MyBaseAdapter(context, story.getRep());
        holder.commentListView.setAdapter(adapter);
        setListViewHeightBasedOnChildren(holder.commentListView);
        holder.likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                praiseStory(position);
            }
        });
        return convertView;
    }

    /**
     * 打开图片查看器
     *
     * @param position
     * @param urls2
     */
    protected void imageBrower(int position, ArrayList<String> urls2) {
        Intent intent = new Intent(context, ImagePagerActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        context.startActivity(intent);
    }


    private int getStoryCount() {
        if (storyList != null) {
            return storyList.size();  //顶部滚动图占据一行
        } else {
            return 1;
        }
    }

    private SEStory getInformation(int index) {
        if (storyList != null) {
            return storyList.get(index);
        } else {
            return null;
        }
    }

    public void refresh(final SECallBack callback) {
        SEStoryManger seStoryManger = SEStoryManger.getInstance();
        seStoryManger.refreshStory(1, LIMIT_STORY, new SECallBack() {
            @Override
            public void success() {
                updatePresentingStory(1);
                notifyDataSetChanged();
                if (callback != null) {
                    callback.success();
                }
            }

            @Override
            public void failure(ServiceError error) {
                if (callback != null) {
                    callback.failure(error);
                }
            }
        });
    }

    public void loadMore(final SECallBack callback) {
        double n = storyList.size() * 1.0 / LIMIT_STORY;
        int page = (int) Math.ceil(n) + 1;
        SEStoryManger seStoryManger = SEStoryManger.getInstance();
        seStoryManger.loadMoreInformation(page, LIMIT_STORY,
                new SECallBack() {
                    @Override
                    public void success() {
                        updatePresentingStory(2);
                        notifyDataSetChanged();
                        if (callback != null) {
                            callback.success();
                        }
                    }

                    @Override
                    public void failure(ServiceError error) {
                        if (callback != null) {
                            callback.failure(error);
                        }
                    }
                });
    }

    public void refreshIfNeeded() {
        if (storyList == null || storyList.isEmpty()) {
            refresh(null);
        }
    }

    private void updatePresentingStory(int type) {
        if (type == 1) {
            // 刷新
            storyList = new ArrayList<SEStory>();
            storyList.addAll(SEStoryManger.getInstance().getStoryList());
        } else {
            //加载更多
            storyList.addAll(SEStoryManger.getInstance().getStoryList());
        }

    }

    private void praiseStory(final int position) {
        SEUser user = SEAuthManager.getInstance().getAccessUser();
        if (user == null) {
            return;
        }
        SEStoryService storyService = SERestManager.getInstance().create(SEStoryService.class);
        final SEStory story = storyList.get(position);
        storyService.praiseStory(story.getId(), user.getId(), new Callback<SEStoryPraiseResult>() {
            @Override
            public void success(SEStoryPraiseResult result, Response response) {
                if (result.state) {
                    story.setPraise(result.count);
                    if (result.sta.equals("add")) {
                        story.set_praised(1);
                    } else if (result.sta.equals("del")) {
                        story.set_praised(0);
                    }
                    storyList.set(position, story);
                    notifyDataSetChanged();
                } else {
                    SVProgressHUD.showInViewWithoutIndicator(context, result.msg, 2.0f);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.showInViewWithoutIndicator(context, error.getMessage(), 2.0f);
            }
        });
    }

    class ViewHolder {
        private ImageView iv_avatar;
        private TextView tv_title;
        private TextView tv_content;
        private TextView tv_msg;
        private NoScrollGridView imageGridView;
        private TextView timeText;
        private TextView messageText;
        private TextView likeText;
        private ImageView likeImage;
        private ListView commentListView;
    }

    public class MyBaseAdapter extends BaseAdapter {
        private int[] colors = new int[]{0xff3cb371, 0xffa0a0a0};
        private Context mContext;
        private ArrayList<SEComment> dataList;

        public MyBaseAdapter(Context context, ArrayList<SEComment> dataList) {
            this.mContext = context;
            this.dataList = dataList;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public SEComment getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_comment, null);
                holder.title = (TextView) convertView.findViewById(R.id.userText);
                holder.text = (TextView) convertView.findViewById(R.id.contentText);

                // 将holder绑定到convertView
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 最多只显示5条评论
            if (position < 6) {
                SEComment comment = getItem(position);
                // 向ViewHolder中填入的数据
                SpannableString ss = new SpannableString(comment.getUser_nickname() + " 回复 " + comment.getTo_user_nickname()+":");
                ss.setSpan(new ForegroundColorSpan(Color.argb(100,0, 154, 205)), 0, comment.getUser_nickname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new ForegroundColorSpan(Color.argb(100,0,154, 205)), comment.getUser_nickname().length() + 4, comment.getUser_nickname().length() + 4 + comment.getTo_user_nickname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.title.setText(ss);
                holder.text.setText(comment.getMsg());
            }

//            int colorPos = position % colors.length;
//            convertView.setBackgroundColor(colors[colorPos]);

            return convertView;
        }

        /**
         * ViewHolder类用以储存item中控件的引用
         */
        final class ViewHolder {
            TextView title;
            TextView text;
        }
    }

    /**
     * 根据内容设置高度
     *
     * @param mListView
     */
    public void setListViewHeightBasedOnChildren(ListView mListView) {
        MyBaseAdapter listAdapter = (MyBaseAdapter) mListView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, mListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = totalHeight + (mListView.getDividerHeight() * (listAdapter.getCount() - 1));
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        mListView.setLayoutParams(params);
    }
}

