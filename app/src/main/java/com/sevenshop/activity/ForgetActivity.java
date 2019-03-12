package com.sevenshop.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.sevenshop.R;
import com.sevenshop.bean.User;
import com.sevenshop.utils.ListUtils;
import com.sevenshop.utils.StringUtils;
import com.sevenshop.utils.ToastUtils;
import com.sevenshop.widget.ClearEditText;
import com.sevenshop.widget.EnjoyshopToolBar;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class ForgetActivity extends BaseActivity  {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;
    @BindView(R.id.etxt_pwd_new)
    ClearEditText mEtPwNew;
    @BindView(R.id.etxt_pwd)
    ClearEditText    mEtPw;
    @BindView(R.id.etxt_phone)
    ClearEditText    mEtPhone;
    String phone;

    String pw_new;

    String pw;
    @Override
    protected void init() {
        initToolBar();
        phone = getIntent().getStringExtra("phone");
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_forget;
    }

    private void initToolBar() {

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetActivity.this.finish();
            }
        });
    }
    @OnClick({R.id.btn_login})
    public void viewclick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                getCode();
                break;
        }
    }

    private void getCode() {

        phone = mEtPhone.getText().toString().trim().replaceAll("\\s*", "");
        pw_new = mEtPwNew.getText().toString().trim();
        pw = mEtPw.getText().toString().trim();
        checkPhoneNum();
    }

    /**
     * 对手机号进行验证
     * 是否合法  是否已经注册
     */
    private void checkPhoneNum() {

        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showSafeToast(ForgetActivity.this, "请输入手机号码");
            return;
        }

        if (TextUtils.isEmpty(pw_new)) {
            ToastUtils.showSafeToast(ForgetActivity.this, "请输入密码");
            return;
        }

        if (!StringUtils.isMobileNum(phone)) {
            ToastUtils.showSafeToast(ForgetActivity.this, "请核对手机号码");
            return;
        }

        if (!StringUtils.isPwdStrong(pw_new)) {
            ToastUtils.showSafeToast(ForgetActivity.this, "密码太短,请重新设置");
            return;
        }

        if (pw.equals(pw_new)) {
            ToastUtils.showSafeToast(ForgetActivity.this, "前后密码不匹配");
            return;
        }
        queryUserData();
    }
    private void jumpRegSecondUi() {
        Intent intent = new Intent(this, RegSecondActivity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("pwd", pw);
        intent.putExtra("forget",true);
        startActivity(intent);
        finish();
    }

    private void queryUserData() {

        BmobQuery<com.sevenshop.data.dao.User> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereContains("username",phone);
        bmobQuery.findObjects(new FindListener<com.sevenshop.data.dao.User>() {
            @Override
            public void done(List<com.sevenshop.data.dao.User> object, BmobException e) {
                if (! ListUtils.isEmpty(object)) {
                    jumpRegSecondUi();
                } else {
                    ToastUtils.showSafeToast(ForgetActivity.this, "手机号没有被注册");
                    Intent intent = new Intent(ForgetActivity.this,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
