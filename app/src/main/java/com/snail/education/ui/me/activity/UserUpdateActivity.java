package com.snail.education.ui.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.snail.circularimageview.CircularImageView;
import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.bean.ProvinceBean;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.manager.SEUserManager;
import com.snail.education.protocol.model.SEUser;
import com.snail.education.protocol.result.MCUploadResult;
import com.snail.education.protocol.result.SEUserResult;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.education.ui.helper.SAXPraserHelper;
import com.snail.imagechooser.api.ChooserType;
import com.snail.imagechooser.api.ChosenImage;
import com.snail.imagechooser.api.ImageChooserListener;
import com.snail.imagechooser.api.ImageChooserManager;
import com.snail.svprogresshud.SVProgressHUD;
import com.squareup.picasso.Picasso;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class UserUpdateActivity extends SEBaseActivity implements ImageChooserListener {

    private final static int MENU_AVATAR_FROM_CAPTURE = 0x123;
    private final static int MENU_AVATAR_FROM_GALLERY = 0x456;

    private LinearLayout updateLL;
    private EditText nicknameET, signatureET, emailET;
    private TextView phoneTV, loaclTV;
    private Button _avatarButton;
    private CircularImageView avatarImageView;
    private ImageView iv_switch_man, iv_switch_woman;

    private SEUser _user;

    private String _updatedAvatarFilename;

    private ImageChooserManager _imageChooserManager;
    private int _chooserType;
    private String _filePath;
    private String _imageName; //上传成功后返回的图片名

    private boolean needToUpdate = false;
    private int sex = 1;

    private OptionsPickerView pvOptions;

    private ArrayList<ProvinceBean> options1Items = new ArrayList<ProvinceBean>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<ArrayList<String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_update);
        setTitleText("我的资料");
        setRightText("保存");
        setRightTextListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSave();
            }
        });
        setupViews();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        _user = SEAuthManager.getInstance().getAccessUser();
        updateUI();

    }

    public void onSave() {

        String signature = signatureET.getText().toString();
        String name = nicknameET.getText().toString();
        String local = loaclTV.getText().toString();
        String mail = emailET.getText().toString();
        if (name.equals("")) {
            SVProgressHUD.showInViewWithoutIndicator(this, "请填写真实姓名", 2);
            return;
        }

        SEUser currentUser = SEAuthManager.getInstance().getAccessUser();
        currentUser.setName(name);
        currentUser.setLocal(local);
        currentUser.setSex(sex + "");
        currentUser.setSign(signature);
        if (!TextUtils.isEmpty(_imageName))
            currentUser.setAvator(_imageName);
        final SEUser modifiedUser = currentUser;

        SVProgressHUD.showInView(this, "保存中，请稍候...", true);
        SEUserManager.getInstance().modifyUserMe(name, _imageName, local, sex + "", signature, new SECallBack() {
            @Override
            public void success() {
                SVProgressHUD.showInViewWithoutIndicator(UserUpdateActivity.this, "更新成功!", 2);
                Intent intent = getIntent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("userInfo", modifiedUser);
                intent.putExtras(bundle);
                setResult(1, intent);
                finish();
            }

            @Override
            public void failure(ServiceError error) {
                SVProgressHUD.showInViewWithoutIndicator(UserUpdateActivity.this, "保存出错，请检查您的网络。", 2);
            }
        });
    }

    private void setupViews() {
        _avatarButton = (Button) findViewById(R.id.AvatarButton);
        _avatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAvatar();
            }
        });

        nicknameET = (EditText) findViewById(R.id.et_nickname);
        signatureET = (EditText) findViewById(R.id.et_signature);
        loaclTV = (TextView) findViewById(R.id.tv_local);
        emailET = (EditText) findViewById(R.id.et_email);
        phoneTV = (TextView) findViewById(R.id.tv_phone);

        //选项选择器
        pvOptions = new OptionsPickerView(this);

        //省份
        try {
            options1Items = (ArrayList) getDistrictList("Province", "ProvinceName");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //城市
        List<ProvinceBean> cityList = new ArrayList<ProvinceBean>();
        try {
            cityList = getDistrictList("City", "CityName");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < options1Items.size(); i++) {
            ArrayList<String> options2Item = new ArrayList<String>();
            for (int j = 0; j < cityList.size(); j++) {
                if (cityList.get(j).getPid() == options1Items.get(i).getId())
                    options2Item.add(cityList.get(j).getName());
            }
            options2Items.add(options2Item);
        }

        pvOptions.setPicker(options1Items, options2Items, true);
        pvOptions.setTitle("选择城市");
        pvOptions.setCyclic(false, false, true);
        //监听确定选择按钮
        pvOptions.setSelectOptions(1, 1);
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText() + ","
                        + options2Items.get(options1).get(option2);
                loaclTV.setText(tx);
            }
        });
        //点击弹出选项选择器
        loaclTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pvOptions.show();
            }
        });


        avatarImageView = (CircularImageView) findViewById(R.id.AvatarImageView);
        avatarImageView.setBorderWidth(4);
        avatarImageView.setBorderColor(getResources().getColor(R.color.lightgrey));

        updateLL = (LinearLayout) findViewById(R.id.updateLL);
        updateLL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(nicknameET.getWindowToken(), 0);
                nicknameET.clearFocus();
                return false;
            }
        });


        iv_switch_man = (ImageView) findViewById(R.id.iv_switch_man);
        iv_switch_woman = (ImageView) findViewById(R.id.iv_switch_women);

        iv_switch_man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_switch_man.setVisibility(View.INVISIBLE);
                iv_switch_woman.setVisibility(View.VISIBLE);
                sex = 1;
            }
        });
        iv_switch_woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_switch_man.setVisibility(View.VISIBLE);
                iv_switch_woman.setVisibility(View.INVISIBLE);
                sex = 2;
            }
        });
    }

    private List<ProvinceBean> getDistrictList(String itemName, String attributeName) throws ParserConfigurationException, SAXException, IOException {
        //实例化一个SAXParserFactory对象
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser;
        //实例化SAXParser对象，创建XMLReader对象，解析器
        parser = factory.newSAXParser();
        XMLReader xmlReader = parser.getXMLReader();
        //实例化handler，事件处理器
        SAXPraserHelper helperHandler = new SAXPraserHelper(itemName, attributeName);
        //解析器注册事件
        xmlReader.setContentHandler(helperHandler);
        //读取文件流
        InputStream stream = null;
        if (itemName.equals("Province")) {
            stream = getResources().openRawResource(R.raw.provinces);
        } else {
            stream = getResources().openRawResource(R.raw.cities);
        }
        InputSource is = new InputSource(stream);
        //解析文件
        xmlReader.parse(is);
        return helperHandler.getList();
    }

    private void updateUI() {
        updateTextViews();
        updateAvatarImageView();
    }

    private void updateTextViews() {
        if (_user != null) {
            nicknameET.setText(_user.getName());
            signatureET.setText(_user.getSign());
            loaclTV.setText(_user.getLocal());
            emailET.setText(_user.getEmail());
            phoneTV.setText(_user.getPhone());
            if (_user.getSex()!=null&&_user.getSex().equals("1")) {
                iv_switch_man.setVisibility(View.INVISIBLE);
                iv_switch_woman.setVisibility(View.VISIBLE);
            } else {
                iv_switch_man.setVisibility(View.VISIBLE);
                iv_switch_woman.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void updateAvatarImageView() {
        String avatarUrl = "";
        if (_user != null) {
            avatarUrl = _user.getAvator();
        }

        if (_updatedAvatarFilename != null) {
            File imageFile = new File(_updatedAvatarFilename);
            int width = avatarImageView.getWidth();
            if (width <= 0) {
                width = 150;
            }
            Picasso.with(this)
                    .load(imageFile)
                    .resize(width, width)
                    .centerCrop()
                    .into(avatarImageView);
        } else if (!TextUtils.isEmpty(avatarUrl)) {
            Picasso.with(this)
                    .load(SEConfig.getInstance().getAPIBaseURL() + "/upload/" + avatarUrl)
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo)
                    .resize(150, 150)
                    .centerCrop()
                    .into(avatarImageView);
        }
    }

    private void editAvatar() {
        PopupMenu popupMenu = new PopupMenu(this, _avatarButton);
        popupMenu.getMenu().add(Menu.NONE, MENU_AVATAR_FROM_CAPTURE, 1, "相机拍摄");
        popupMenu.getMenu().add(Menu.NONE, MENU_AVATAR_FROM_GALLERY, 2, "图库选取");

        popupMenu.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case MENU_AVATAR_FROM_CAPTURE:
                                takePicture();
                                break;
                            case MENU_AVATAR_FROM_GALLERY:
                                chooseImage();
                                break;
                        }
                        return true;
                    }
                });

        popupMenu.show();
    }

    private void chooseImage() {
        _chooserType = ChooserType.REQUEST_PICK_PICTURE;
        _imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_PICK_PICTURE, "tmp", true);
        _imageChooserManager.setImageChooserListener(this);
        try {
            _filePath = _imageChooserManager.choose();
            needToUpdate = true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void takePicture() {
        _chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        _imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_CAPTURE_PICTURE, "tmp", true);
        _imageChooserManager.setImageChooserListener(this);
        try {
            _filePath = _imageChooserManager.choose();
            needToUpdate = true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK
                && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (_imageChooserManager == null) {
                reinitializeImageChooser();
            }
            _imageChooserManager.submit(requestCode, data);
        }
    }

    @Override
    public void onImageChosen(final ChosenImage image) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (image != null) {
                    _updatedAvatarFilename = image.getFileThumbnail();
                    updateAvatarImageView();
                    if (needToUpdate) {
                        updateAvatar();
                    }
                }
            }
        });
    }

    private void reinitializeImageChooser() {
        _imageChooserManager = new ImageChooserManager(this, _chooserType, "tmp", true);
        _imageChooserManager.setImageChooserListener(this);
        _imageChooserManager.reinitialize(_filePath);
    }

    @Override
    public void onError(String reason) {
        SVProgressHUD.showInViewWithoutIndicator(this, "无法选择照片，请重试。", 3.f);
    }

    /**
     * 头像更新到服务器
     */
    private void updateAvatar() {
        SEUserManager.getInstance().uploadAvatar(_updatedAvatarFilename, new SECallBack() {
            @Override
            public void success() {
                MCUploadResult uploadResult = SEUserManager.getInstance().getUploadResult();
                _imageName = uploadResult.imageName;
            }

            @Override
            public void failure(ServiceError error) {

            }
        });
        needToUpdate = false;
    }
}
