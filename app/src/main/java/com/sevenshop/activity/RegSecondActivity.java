package com.sevenshop.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sevenshop.R;
import com.sevenshop.bean.User;
import com.sevenshop.utils.CountTimerView;
import com.sevenshop.utils.ListUtils;
import com.sevenshop.utils.ToastUtils;
import com.sevenshop.widget.ClearEditText;
import com.sevenshop.widget.EnjoyshopToolBar;
import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import dmax.dialog.SpotsDialog;

/**
 * Describe: 接收验证码的注册界面,即注册界面二
 */

public class RegSecondActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;

    @BindView(R.id.txtTip)
    TextView mTxtTip;

    @BindView(R.id.btn_reSend)
    Button mBtnResend;

    @BindView(R.id.edittxt_code)
    ClearEditText mEtCode;

    private String phone;
    private String pwd;
    private boolean forget = false;
    private SpotsDialog dialog;
    private Gson mGson = new Gson();

    @Override
    protected void init() {

        initToolBar();
        dialog = new SpotsDialog(this);
        forget = getIntent().getBooleanExtra("forget",false);
        phone = getIntent().getStringExtra("phone");
        pwd = getIntent().getStringExtra("pwd");
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_reg_second;
    }

    /**
     * 标题栏 完成
     */
    private void initToolBar() {
        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCode();
            }
        });
    }

    /**
     * 提交验证码
     */
    private void submitCode() {

        String vCode = mEtCode.getText().toString().trim();

        if (TextUtils.isEmpty(vCode)) {
            ToastUtils.showSafeToast(RegSecondActivity.this, "请填写验证码");
        }

        if (!"1234".equals(vCode)) {
            ToastUtils.showSafeToast(RegSecondActivity.this, "验证码不准确,请重新获取");
        } else {
            if (!forget) {
                addUser();
            } else {
                updatePw();
            }
        }

    }
    private void updatePw() {
        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("username",phone);
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(final List<User> object, BmobException e) {
                if (e == null) {
                    if (!ListUtils.isEmpty(object) && object.get(0) != null)
                        object.get(0).setPassword(pwd);
                    object.get(0).update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                object.get(0).signUp(new SaveListener<User>() {
                                    @Override
                                    public void done(User user, BmobException e) {
                                        if (e == null) {
                                            user.login(new SaveListener<User>() {
                                                @Override
                                                public void done(User bmobUser, BmobException e) {
                                                    if (e == null) {
                                                        ToastUtils.showSafeToast(RegSecondActivity.this, "更新成功");
//                                                        Intent intent = new Intent(RegSecondActivity.this, MainActivity.class);
//                                                        intent.putExtra("islogin",true);
//                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                    }
                                                }
                                            });
                                        } else {
                                            ToastUtils.showSafeToast(RegSecondActivity.this, "更新密码失败");
                                        }
                                    }
                                });
                            } else {
                                ToastUtils.showSafeToast(RegSecondActivity.this, "更新密码失败");
                            }
                        }
                    });
                }
            }
        });
    }
    @OnClick(R.id.btn_reSend)
    public void getVcode(View view) {
        mTxtTip.setText("验证码为:  1234");

        //倒计时
        CountTimerView timerView = new CountTimerView(mBtnResend);
        timerView.start();

        ToastUtils.showSafeToast(RegSecondActivity.this, "验证码为: 1234");
    }

    private void addUser() {
        final User user = new User();
        user.setUsername(phone);
        user.setPassword(pwd);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    final User u = new User();
                    //此处替换为你的用户名
                    u.setUsername(phone);
                    //此处替换为你的密码
                    u.setPassword(pwd);
                    u.login(new SaveListener<User>() {
                        @Override
                        public void done(User bmobUser, BmobException e) {
                            if (e == null) {
                                ToastUtils.showSafeToast(RegSecondActivity.this, "注册成功");
//                                Intent intent = new Intent(RegSecondActivity.this, MainActivity.class);
//                                intent.putExtra("islogin",true);
//                                startActivity(intent);
                                finish();
                            } else {
                            }
                        }
                    });
                } else {
                    ToastUtils.showSafeToast(RegSecondActivity.this, "注册失败");
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
