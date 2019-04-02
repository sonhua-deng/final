package com.sevenshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sevenshop.R;
import com.sevenshop.adapter.GridImageAdapter;
import com.sevenshop.bean.Goods;
import com.sevenshop.bean.User;
import com.sevenshop.fragment.FullyGridLayoutManager;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.sevenshop.utils.ListUtils;
import com.sevenshop.utils.ToastUtils;
import com.sevenshop.widget.EnjoyshopToolBar;
import com.sevenshop.widget.MDDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class PublishActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = PublishActivity.class.getSimpleName();
    private List<LocalMedia> selectList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GridImageAdapter adapter;
    private int maxSelectNum = 9;

    private int themeId;
    private int chooseMode = PictureMimeType.ofAll();

    private String choose;
    private String shell_type;
    private int price = 100;
    private List<String> picUrls;
    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;
    @BindView(R.id.et_des)
    EditText des;

    @BindView(R.id.et_title)
    EditText title;

    private void initToolBar() {

        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
                finish();
            }
        });
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublishActivity.this.finish();
            }
        });
    }
    @Override
    protected int getContentResourseId() {
        return R.layout.activity_publish;
    }

    @Override
    protected void init() {
        themeId = R.style.picture_default_style;
        initToolBar();
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                LocalMedia media = selectList.get(position);
                String pictureType = media.getPictureType();
                int mediaType = PictureMimeType.pictureToVideo(pictureType);
                switch (mediaType) {
                    case 1:
                        // 预览图片
                        PictureSelector.create(PublishActivity.this).themeStyle(themeId).openExternalPreview(position, selectList);
                        break;
                    case 2:
                        // 预览视频
                        PictureSelector.create(PublishActivity.this).externalPictureVideo(media.getPath());
                        break;
                    case 3:
                        // 预览音频
                        PictureSelector.create(PublishActivity.this).externalPictureAudio(media.getPath());
                        break;
                }
            }
        });
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {

            // 进入相册 以下是例子：不需要的api可以不写
            chooseMode = PictureMimeType.ofImage();
            PictureSelector.create(PublishActivity.this)
                    .openGallery(chooseMode)
                    .theme(themeId)
                    .maxSelectNum(maxSelectNum)
                    .minSelectNum(1)
                    .glideOverride(160, 160)
                    .previewEggs(true)
                    .selectionMedia(selectList)
                    .withAspectRatio(3, 4)
                    .compress(true)
                    .forResult(PictureConfig.CHOOSE_REQUEST);

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择
                    selectList = PictureSelector.obtainMultipleResult(data);
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @OnClick({R.id.rl_lei,R.id.rl_pay})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_lei:
                showChoice();
                break;
            case R.id.rl_pay:
                showType();
                break;
            default:
                break;
        }

    }
    private void showChoice() {
        MDDialog.newBuilder()
                .list()
                .cancelOutside(true)
                .items(R.array.home_bar_labels)
                .listListener(new MDDialog.OnDialogListClickListener() {
                    @Override
                    public void onItemClick(MDDialog dialog, View view, int position, CharSequence charSequence) {
                        String genderInfo = "";
                        genderInfo = getResources().getStringArray(R.array.home_bar_labels)[position];
                        choose = genderInfo;
                        TextView textView = findViewById(R.id.tv_choose);
                        textView.setText(genderInfo);
                        textView.setVisibility(View.VISIBLE);
                        dialog.dismissAllowingStateLoss();
                    }
                }).build().showWithActivity(this);
    }
    private void showType() {
        MDDialog.newBuilder()
                .list()
                .cancelOutside(true)
                .items(R.array.shell_type)
                .listListener(new MDDialog.OnDialogListClickListener() {
                    @Override
                    public void onItemClick(MDDialog dialog, View view, int position, CharSequence charSequence) {
                        if (position == 0 ) {
                            shell_type = getResources().getStringArray(R.array.shell_type)[position];
                            price = (int)(10+Math.random()*(10000));
                            TextView textView = findViewById(R.id.tv_type);
                            textView.setText(shell_type);
                            textView.setVisibility(View.VISIBLE);
                            dialog.dismissAllowingStateLoss();
                            return;
                        } else {
                            shell_type = getResources().getStringArray(R.array.shell_type)[position];
                            TextView textView = findViewById(R.id.tv_type);
                            textView.setText(shell_type);
                            textView.setVisibility(View.VISIBLE);
                            showUpdateNickNameDialog("100");
                            dialog.dismissAllowingStateLoss();
                            return;
                        }
                    }
                }).build().showWithActivity(this);
    }
    private void showUpdateNickNameDialog(String nickName) {
        Log.d(TAG, "showUpdateNickNameDialog(),nickName:" + nickName);
        MDDialog.newBuilder()
                .input()
                .content("输入个人售卖的价格")
                .inputType(InputType.TYPE_CLASS_NUMBER )
                .inputRangeRes(1, 16, getResources().getColor(android.R.color.holo_red_light))
                .cancelOutside(false)
                .hasNegative(false)    // 是否显示取消
                .hasPositive(true)    // 是否显示确定
                .positive("提交")
                .preInsert("100", nickName, false, new MDDialog.OnDialogInputListener() {
                    @Override
                    public void onInput(@NonNull MDDialog dialog, CharSequence input) {
                        if (dialog.getInputEditText() != null) {
                            PublishActivity.this
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
                            price = (int)Integer.parseInt(name);
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
                            PublishActivity.this.hideKeyboard(dialog.getInputEditText());
                        }
                        dialog.dismissAllowingStateLoss();
                    }
                })
                .build().showWithActivity(this);
    }

  public void upload() {

        if(ListUtils.isEmpty(selectList) || choose.isEmpty() || shell_type.isEmpty() ||
                des.getText() == null ||des.getText().toString().isEmpty() || title.getText() == null || title.getText().toString().isEmpty()) {
            ToastUtils.showSafeToast(this,"请输入完整内容");
            return;
        }
      if (!BmobUser.isLogin()) {
          Intent intent2 = new Intent(this, LoginActivity.class);
          startActivity(intent2);
          return;
      }
      uploadPicture();
  }
  private void uploadPicture() {
      final String[] filePaths = new String[selectList.size()];
      int i = 0;
      for (LocalMedia l: selectList) {
          filePaths[i] = l.getCompressPath();
          i++;
      }

      BmobFile.uploadBatch(filePaths, new UploadBatchListener() {

          @Override
          public void onSuccess(List<BmobFile> files,List<String> urls) {
              //1、files-上传完成后的BmobFile集合，是为了方便大家对其上传后的数据进行操作，例如你可以将该文件保存到表中
              //2、urls-上传文件的完整url地址
              if(urls.size()==filePaths.length){//如果数量相等，则代表文件全部上传完成
                  picUrls  = urls;
                  Goods goods = new Goods();
                  goods.setPulisher(BmobUser.getCurrentUser(User.class));
                  goods.setStartPrice(price);
                  goods.setCurrenPrice(price);
                  goods.setPhotoUrls(picUrls);
                  goods.setTitle(title.getText().toString());
                  goods.setShellType(shell_type);
                  goods.setType(choose);
                  goods.setDes(des.getText().toString());
                  goods.save(new SaveListener<String>() {
                      @Override
                      public void done(String objectId, BmobException e) {
                          if (e == null) {
                              ToastUtils.showSafeToast(PublishActivity.this,"发布成功");
                              PublishActivity.this.finish();
                          } else {
                              ToastUtils.showSafeToast(PublishActivity.this,"发布失败");
                          }
                      }
                  });
              }
          }

          @Override
          public void onError(int statuscode, String errormsg) {
              ToastUtils.showSafeToast(PublishActivity.this,"上传图片失败");
          }

          @Override
          public void onProgress(int curIndex, int curPercent, int total,int totalPercent) {
              //1、curIndex--表示当前第几个文件正在上传
              //2、curPercent--表示当前上传文件的进度值（百分比）
              //3、total--表示总的上传文件数
              //4、totalPercent--表示总的上传进度（百分比）
          }
      });
  }
}
