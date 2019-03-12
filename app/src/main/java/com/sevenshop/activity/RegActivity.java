package com.sevenshop.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sevenshop.R;
import com.sevenshop.data.DataManager;
import com.sevenshop.data.dao.User;
import com.sevenshop.utils.ListUtils;
import com.sevenshop.utils.StringUtils;
import com.sevenshop.utils.ToastUtils;
import com.sevenshop.widget.ClearEditText;
import com.sevenshop.widget.EnjoyshopToolBar;

import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * Describe: 注册activity
 */

public class RegActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;
    @BindView(R.id.txtCountry)
    TextView         mTxtCountry;
    @BindView(R.id.edittxt_phone)
    ClearEditText    mEtxtPhone;
    @BindView(R.id.edittxt_pwd)
    ClearEditText    mEtxtPwd;

    private String phone;
    private String pwd;

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_reg;
    }


    @Override
    protected void init() {
        initToolBar();
    }


    private void initToolBar() {

        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCode();
            }
        });
    }

    /**
     * 获取手机号 密码等信息
     */
    private void getCode() {

        phone = mEtxtPhone.getText().toString().trim().replaceAll("\\s*", "");
        pwd = mEtxtPwd.getText().toString().trim();

        checkPhoneNum();
    }

    /**
     * 对手机号进行验证
     * 是否合法  是否已经注册
     */
    private void checkPhoneNum() {

        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showSafeToast(RegActivity.this, "请输入手机号码");
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showSafeToast(RegActivity.this, "请设置密码");
            return;
        }

        if (!StringUtils.isMobileNum(phone)) {
            ToastUtils.showSafeToast(RegActivity.this, "请核对手机号码");
            return;
        }

        if (!StringUtils.isPwdStrong(pwd)) {
            ToastUtils.showSafeToast(RegActivity.this, "密码太短,请重新设置");
            return;
        }

        queryUserData();

    }

    /**
     * 查询手机号是否已经注册了
     * <p>
     * 注意注意: 在商业项目中,这里只需要请求注册接口即可.手机号是否存在由后台岗位同事判断
     */
    private void queryUserData() {

        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereContains("username",phone);
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> object, BmobException e) {
                if (ListUtils.isEmpty(object)) {
                    jumpRegSecondUi();
                } else {
                    ToastUtils.showSafeToast(RegActivity.this, "手机号已被注册");
                }
            }
        });
    }

    /**
     * 跳转到注册界面二
     */

    private void jumpRegSecondUi() {
        Intent intent = new Intent(this, RegSecondActivity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("pwd", pwd);
        startActivity(intent);
        finish();
    }

}

