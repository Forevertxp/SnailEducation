package com.snail.education.ui.me.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snail.circularimageview.CircularImageView;
import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.manager.SEUserManager;
import com.snail.education.protocol.model.SEUser;
import com.snail.education.protocol.result.SEUserResult;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.education.ui.me.UserMeFragment;
import com.snail.imagechooser.api.ChooserType;
import com.snail.imagechooser.api.ChosenImage;
import com.snail.imagechooser.api.ImageChooserListener;
import com.snail.imagechooser.api.ImageChooserManager;
import com.snail.svprogresshud.SVProgressHUD;
import com.squareup.picasso.Picasso;

import java.io.File;

public class UserUpdateActivity extends SEBaseActivity implements ImageChooserListener {

    private final static int MENU_AVATAR_FROM_CAPTURE = 0x123;
    private final static int MENU_AVATAR_FROM_GALLERY = 0x456;

    private RelativeLayout updateRL;
    private EditText nicknameET, signatureET, realnameET, emailET;
    private TextView phoneTV;
    private Button _avatarButton;
    private CircularImageView avatarImageView;
    private Button _saveBtn;

    private SEUser _user;

    private String _updatedAvatarFilename;

    private ImageChooserManager _imageChooserManager;
    private int _chooserType;
    private String _filePath;

    private boolean needToUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_update);
        setTitleText("我的资料");
        setupViews();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        _user = SEAuthManager.getInstance().getAccessUser();
        updateUI();

    }

    public void onSave() {

        String signature = signatureET.getText().toString();
        String nickname = nicknameET.getText().toString();
        String name = realnameET.getText().toString();
        String mail = emailET.getText().toString();
        if (signature.equals("")) {
            SVProgressHUD.showInViewWithoutIndicator(this, "请填写昵称", 2);
            return;
        }

        SVProgressHUD.showInView(this, "保存中，请稍候...", true);
        SEUserManager.getInstance().modifyUserMe(nickname, signature, name, mail, new SECallBack() {
            @Override
            public void success() {
                SVProgressHUD.dismiss(UserUpdateActivity.this);
                SEUserResult regResult = SEUserManager.getInstance().getRegResult();
                if (regResult.state) {
                    SEAuthManager.getInstance().updateUserInfo(regResult.data);
                    new AlertDialog.Builder(UserUpdateActivity.this)
                            .setTitle("成功")
                            .setMessage("保存成功")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            })
                            .show();
                } else {
                    SVProgressHUD.showInViewWithoutIndicator(UserUpdateActivity.this, regResult.msg, 2.f);
                }
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
        realnameET = (EditText) findViewById(R.id.et_realname);
        emailET = (EditText) findViewById(R.id.et_email);
        phoneTV = (TextView) findViewById(R.id.tv_phone);

        avatarImageView = (CircularImageView) findViewById(R.id.AvatarImageView);
        avatarImageView.setBorderWidth(4);
        avatarImageView.setBorderColor(getResources().getColor(R.color.lightgrey));
        avatarImageView.addShadow();


        _saveBtn = (Button) findViewById(R.id.user_save);
        _saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSave();
            }
        });

        updateRL = (RelativeLayout) findViewById(R.id.updateRL);
        updateRL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(nicknameET.getWindowToken(), 0);
                nicknameET.clearFocus();
                return false;
            }
        });
    }

    private void updateUI() {
        updateTextViews();
        updateAvatarImageView();
    }

    private void updateTextViews() {
        if (_user != null) {
            nicknameET.setText(_user.getNickname());
            signatureET.setText(_user.getSay());
            realnameET.setText(_user.getName());
            emailET.setText(_user.getMail());
            phoneTV.setText(_user.getUser());
        }
    }

    private void updateAvatarImageView() {
        String avatarUrl = "";
        if (_user != null) {
            avatarUrl = _user.getIcon();
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
        } else if (!avatarUrl.equals("") && avatarUrl.indexOf("res/images/def.jpg") == -1) {
            Picasso.with(this)
                    .load(SEConfig.getInstance().getAPIBaseURL() + avatarUrl)
                    .resize(150, 150)
                    .centerCrop()
                            //.placeholder(new ColorDrawable(Color.GREEN))
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
                SEUserResult regResult = SEUserManager.getInstance().getRegResult();
                if (regResult.state) {
                    SEAuthManager.getInstance().updateUserInfo(regResult.data);
                }
            }

            @Override
            public void failure(ServiceError error) {

            }
        });
        needToUpdate = false;
    }
}
