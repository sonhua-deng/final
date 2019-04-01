package com.sevenshop.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.sevenshop.R;
import com.sevenshop.bean.User;
import com.sevenshop.utils.GlideUtils;
import com.sevenshop.utils.StringUtils;
import com.sevenshop.widget.CircleImageView;
import com.sevenshop.widget.EnjoyshopToolBar;
import com.sevenshop.widget.MDDialog;
import com.sevenshop.widget.SimpleSettingItemView;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class ProfileSettingActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;

    @BindView(R.id.img_head)
    CircleImageView mImageView;

    @BindView(R.id.rl_name)
    SimpleSettingItemView mRlNickName;

    @BindView(R.id.rl_sexy)
    SimpleSettingItemView mRlSexy;

    @BindView(R.id.fl_hometown)
    SimpleSettingItemView mRlHomeTown;

    @BindView(R.id.rl_signature)
    SimpleSettingItemView mRlSignature;


    private User user ;

    private static final int REQUEST_CODE_GALLERY = 0x10;// 图库选取图片标识请求码
    private static final int CROP_PHOTO = 0x12;// 裁剪图片标识请求码
    private static final int STORAGE_PERMISSION = 0x20;// 动态申请存储权限标识

    private File imageFile = null;// 声明File对象
    private Uri imageUri = null;// 裁剪后的图片uri
    private String path = "";

    private boolean isHeadChange = false;
    private boolean isNickNameChange =false;
    private boolean isSexyChange =false;
    private boolean isSignatureChange =false;
    private boolean isHometownChanage = false;

    String nickname;
    String mHeadUri;
    String mSexy;
    String mSignature;
    String mHometown;

    String hUri;
    @Override
    protected void init() {
        initToolBar();
        if (BmobUser.isLogin()) {
            user = BmobUser.getCurrentUser(User.class);
            showUI();
        } else {
            finish();
        }
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_profile_setting;
    }

    private void initToolBar() {

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            upload();
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.img_head, R
            .id.rl_name, R.id.rl_sexy,R.id.fl_hometown,R.id.rl_signature})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.img_head:
                requestStoragePermission();
                gallery();
                break;
            case R.id.rl_name:
                showUpdateNickNameDialog(user.getNickName());
                break;
            case R.id.rl_signature:
                showUpdateSignureDialog(user.getSignature());
                break;
            case R.id.fl_hometown:
                showUpdateHometownDialog(user.getHometown());
                break;
            case R.id.rl_sexy:
                showGenderChoice();
                break;
            default:
                break;
        }
    }
    private void showGenderChoice() {
        MDDialog.newBuilder()
                .list()
                .cancelOutside(true)
                .items(R.array.gender)
                .listListener(new MDDialog.OnDialogListClickListener() {
                    @Override
                    public void onItemClick(MDDialog dialog, View view, int position, CharSequence charSequence) {
                        String genderInfo = "";
                        if (position == 0) {
                            genderInfo = getResources().getStringArray(R.array.gender)[0];
                            mRlSexy.getRightTextView().setText(getResources().getStringArray(R.array.gender)[0]);
                        } else if (position == 1) {
                            genderInfo = getResources().getStringArray(R.array.gender)[1];
                            mRlSexy.getRightTextView().setText(getResources().getStringArray(R.array.gender)[1]);
                        }
//						else if (position == 2) {
////							genderInfo = UserInfoStruct.GENDER_UNKNOWN;
////							mRlSexyView.getRightTextView().setText(getResources().getStringArray(R.array.gender)[2]);
////						}
                        if (!TextUtils.equals(genderInfo, user.getGender())) {
                            mSexy = genderInfo;
                            isSexyChange = true;
                        }
                        dialog.dismiss();
                    }
                }).build().showWithActivity(this);
    }
    private void showUpdateSignureDialog(String signature) {
        Log.d(TAG, "showUpdateSignureDialog(),signature:" + signature);
        if (TextUtils.isEmpty(signature) || TextUtils.isEmpty(signature.trim())) {
            signature = "";
        }
        MDDialog.newBuilder()
                .content("输入你的个性签名")
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .inputRangeRes(0, 80, getResources().getColor(android.R.color.holo_red_light))
                .cancelOutside(false)
                .positive("提交")
                .negative("取消")
                .listener(new MDDialog.OnDialogClickListener() {
                    @Override
                    public void onPositiveClick(MDDialog dialog) {
                        dialog.dismissAllowingStateLoss();
                    }

                    @Override
                    public void onNegativeClick(MDDialog dialog) {
                        if (dialog.getInputEditText() != null) {
                            ProfileSettingActivity.this.hideKeyboard(dialog.getInputEditText());
                        }
                        dialog.dismissAllowingStateLoss();
                    }
                })
//                .lifeListener(new EmptyDialogLifeListener() {
//                    @Override
//                    public void onCreate(MDDialog dialog, View view) {
//                        final EditText editText = dialog.getInputEditText();
//                        if (editText != null) {
//                            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                                @Override
//                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                                    if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                                        return true;
//                                    }
//                                    return false;
//                                }
//                            });
//                        }
//                    }
//                })
                .preInsert("个性签名",
                        signature,
                        true,
                        new MDDialog.OnDialogInputListener() {
                            @Override
                            public void onInput(MDDialog dialog, CharSequence input) {
                                if (dialog != null && dialog.getInputEditText() != null) {
                                    ProfileSettingActivity.this.hideKeyboard(dialog.getInputEditText());
                                }
//                                if (mUserInfoStruct == null) {
//                                    showToast(R.string.setting_modify_fail, Toast.LENGTH_SHORT);
//                                    return;
//                                }

                                String inputStr = input.toString();
                                if (!TextUtils.isEmpty(inputStr)) {
                                    inputStr = inputStr.replace("\n", "");
                                }
                                if (TextUtils.isEmpty(inputStr) || TextUtils.isEmpty(inputStr.trim())) {
                                    inputStr = "";
                                }
                                if(TextUtils.isEmpty(inputStr)) {
                                    mRlSignature.getRightTextView().setText("");
                                    mRlSignature.getRightTextView().setHint("输入用户签名");
                                } else {
                                    mRlSignature.getRightTextView().setText(inputStr);
                                }
                                // update new nick name to server
                                if (!TextUtils.equals(inputStr, user.getSignature())) {
                                    mSignature = inputStr;
                                    isSignatureChange = true;
                                }
                            }
                        }).build().showWithActivity(this);
    }

    private void showUpdateHometownDialog(String hometown) {
        Log.d(TAG, "showUpdateHometown(),nickName:" + hometown);
        MDDialog.newBuilder()
                .input()
                .content("输入地址")
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .inputRangeRes(0, 100, getResources().getColor(android.R.color.holo_red_light))
                .cancelOutside(false)
                .positive("确定")
                .negative("取消")
                .listener(new MDDialog.OnDialogClickListener() {
                    @Override
                    public void onPositiveClick(MDDialog dialog) {
                        dialog.dismissAllowingStateLoss();
                    }

                    @Override
                    public void onNegativeClick(MDDialog dialog) {
                        if (dialog.getInputEditText() != null) {
                            ProfileSettingActivity.this.hideKeyboard(dialog.getInputEditText());
                        }
                        dialog.dismissAllowingStateLoss();
                    }
                })
//                .lifeListener(new EmptyDialogLifeListener() {
//                    @Override
//                    public void onCreate(MDDialog dialog, View view) {
//                        EditText editText = dialog.getInputEditText();
//                        if (editText != null) {
//                            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                                @Override
//                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                                    if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                                        return true;
//                                    }
//                                    return false;
//                                }
//                            });
//                        }
//                    }
//                })
                .preInsert("设置用户地址",
                        hometown,
                        true,
                        new MDDialog.OnDialogInputListener() {
                            @Override
                            public void onInput(MDDialog dialog, CharSequence input) {
                                if (dialog != null && dialog.getInputEditText() != null) {
                                    ProfileSettingActivity.this.hideKeyboard(dialog.getInputEditText());
                                }
//                                if (mUserInfoStruct == null) {
//                                    showToast(R.string.setting_modify_fail, Toast.LENGTH_SHORT);
//                                    return;
//                                }

                                String hometown = input.toString().trim().replace("\n", "");
                                if (!TextUtils.equals(hometown, user.getHometown())) {
                                    mRlHomeTown.getRightTextView().setText(hometown);
                                    mHometown = hometown ;
                                    isHometownChanage = true;
                                }
                            }
                        }).build().showWithActivity(this);
    }

    private void showUpdateNickNameDialog(String nickName) {
        Log.d(TAG, "showUpdateNickNameDialog(),nickName:" + nickName);
        MDDialog.newBuilder()
                .input()
                .content("输入你的昵称")
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRangeRes(1, 16, getResources().getColor(android.R.color.holo_red_light))
                .cancelOutside(false)
                .positive("提交")
                .negative("取消")
                .preInsert("用户昵称", nickName, false, new MDDialog.OnDialogInputListener() {
                    @Override
                    public void onInput(@NonNull MDDialog dialog, CharSequence input) {
                        if (dialog.getInputEditText() != null) {
                            ProfileSettingActivity.this
                                    .hideKeyboard(dialog.getInputEditText());
                        }
//                        if (mUserInfoStruct == null) {
//                            showToast(R.string.setting_modify_fail, Toast.LENGTH_SHORT);
//                            return;
//                        }

                        if (!TextUtils.isEmpty(input)) {
                            String name = input.toString().trim().replace("\n", "");
                            if (TextUtils.isEmpty(name)) {
                                return;
                            }
                            mRlNickName.getRightTextView().setText(name);
                            // update new nick name to server
                            if (!TextUtils.equals(name, user.getNickName())) {
                                nickname = name;
                                isNickNameChange = true;
                            }
                        }
                    }
                })
                .listener(new MDDialog.OnDialogClickListener() {
                    @Override
                    public void onPositiveClick(MDDialog dialog) {
                        dialog.dismissAllowingStateLoss();
                    }

                    @Override
                    public void onNegativeClick(MDDialog dialog) {
                        if (dialog.getInputEditText() != null) {
                            ProfileSettingActivity.this.hideKeyboard(dialog.getInputEditText());
                        }
                        dialog.dismissAllowingStateLoss();
                    }
                })
                .build().showWithActivity(this);
    }

    private void showUI() {
        if (StringUtils.isEmpty(user.getSignature())) {
            mRlSignature.getRightTextView().setHint("输入用户签名");
        } else {
            mRlSignature.getRightTextView().setText(user.getSignature());
        }
        if (StringUtils.isEmpty(user.getHometown())) {
            mRlHomeTown.getRightTextView().setHint("输入用户地址");
        } else {
            mRlHomeTown.getRightTextView().setText(user.getHometown());
        }
        if (StringUtils.isEmpty(user.getGender())) {
            mRlSexy.getRightTextView().setHint("输入用户性别");
        } else {
            mRlSexy.getRightTextView().setText(user.getGender());
        }
        if (StringUtils.isEmpty(user.getNickName())) {
            mRlNickName.getRightTextView().setHint("输入用户昵称");
        } else {
            mRlNickName.getRightTextView().setText(user.getNickName());
        }

        GlideUtils.portrait(this, user.getLogo_url(), mImageView);
    }

    /**
     * 图库选择图片
     */
    private void gallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // 以startActivityForResult的方式启动一个activity用来获取返回的结果
        startActivityForResult(intent, REQUEST_CODE_GALLERY);

    }

    /**
     * 接收#startActivityForResult(Intent, int)调用的结果
     * @param requestCode 请求码 识别这个结果来自谁
     * @param resultCode    结果码
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){// 操作成功了

            switch (requestCode){

                case REQUEST_CODE_GALLERY:// 图库选择图片

                    Uri uri = data.getData();// 获取图片的uri

                    Intent intent_gallery_crop = new Intent("com.android.camera.action.CROP");
                    intent_gallery_crop.setDataAndType(uri, "image/*");

                    // 设置裁剪
                    intent_gallery_crop.putExtra("crop", "true");
                    intent_gallery_crop.putExtra("scale", true);
                    // aspectX aspectY 是宽高的比例
                    intent_gallery_crop.putExtra("aspectX", 1);
                    intent_gallery_crop.putExtra("aspectY", 1);
                    // outputX outputY 是裁剪图片宽高
                    intent_gallery_crop.putExtra("outputX", 400);
                    intent_gallery_crop.putExtra("outputY", 400);

                    intent_gallery_crop.putExtra("return-data", false);

                    // 创建文件保存裁剪的图片
                    createImageFile();
                    imageUri = Uri.fromFile(imageFile);

                    if (imageUri != null){
                        intent_gallery_crop.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        intent_gallery_crop.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    }

                    startActivityForResult(intent_gallery_crop, CROP_PHOTO);

                    break;

                case CROP_PHOTO:// 裁剪图片

                    try{

                        if (imageUri != null){
                            uploadFile(hUri);
                            GlideUtils.portrait(this, imageUri.toString(), mImageView);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;

            }

        }
    }

    /**
     * Android6.0后需要动态申请危险权限
     * 动态申请存储权限
     */
    private void requestStoragePermission() {

        int hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.e("TAG","开始" + hasCameraPermission);
        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED){
            // 拥有权限，可以执行涉及到存储权限的操作
            Log.e("TAG", "你已经授权了该组权限");
        }else {
            // 没有权限，向用户申请该权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e("TAG", "向用户申请该组权限");
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
            }
        }

    }

    /**
     * 动态申请权限的结果回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // 用户同意，执行相应操作
                Log.e("TAG","用户已经同意了存储权限");
            }else {
                // 用户不同意，向用户展示该权限作用
            }
        }

    }

    /**
     * 创建File保存图片
     */
    private void createImageFile() {

        try{

            if (imageFile != null && imageFile.exists()){
                imageFile.delete();
            }
            // 新建文件
            imageFile = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + "galleryDemo.jpg");
            hUri = imageFile.getAbsolutePath();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void upload() {
        final User user = BmobUser.getCurrentUser(User.class);
        if (isHometownChanage) {
            user.setHometown(mHometown);
        }
        if (isNickNameChange) {
            user.setNickName(nickname);
        }
        if (isSignatureChange) {
            user.setSignature(mSignature);
        }
        if (isSexyChange) {
            user.setGender(mSexy);
        }
        if (isHeadChange) {
            user.setLogo_url(mHeadUri);
        }
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                BmobUser.fetchUserInfo(new FetchUserInfoListener<BmobUser>() {
                    @Override
                    public void done(BmobUser user, BmobException e) {
                    }
                });
                setResult(1);
                ProfileSettingActivity
                        .this.finish();
            }
        });
    }
    private void uploadFile(String picPath) {

        final BmobFile bmobFile = new BmobFile(new File(picPath));
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    isHeadChange = true;
                    mHeadUri = bmobFile.getFileUrl().toString();
                }else{

                }

            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });
    }
}
