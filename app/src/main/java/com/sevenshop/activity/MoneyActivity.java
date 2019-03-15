package com.sevenshop.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sevenshop.R;
import com.sevenshop.bean.MessageEvent;
import com.sevenshop.bean.Money;
import com.sevenshop.bean.User;
import com.sevenshop.utils.ListUtils;
import com.sevenshop.utils.ToastUtils;
import com.sevenshop.widget.ClearEditText;
import com.sevenshop.widget.EnjoyshopToolBar;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class MoneyActivity extends BaseActivity {
    @Override
    protected int getContentResourseId() {
        return R.layout.activity_money;
    }
    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;

    @BindView(R.id.tv_yue)
    TextView nowMoney;

    @BindView(R.id.edittxt_pwd)
    ClearEditText mEtMoney;


    Money mMoney;

    @Override
    protected void init() {
        initToolBar();
        initData();

    }

    private  void initData() {
        BmobQuery<Money> categoryBmobQuery = new BmobQuery<>();
        categoryBmobQuery.addWhereEqualTo("user", BmobUser.getCurrentUser(User.class));
        categoryBmobQuery.include("author");
        categoryBmobQuery.findObjects(new FindListener<Money>() {
            @Override
            public void done(List<Money> object, BmobException e) {
                if (e == null) {
                    if (!ListUtils.isEmpty(object)) {
                        mMoney = object.get(0);
                        nowMoney.setText(mMoney.getMoney()+"");
                    }
                } else {
                    ToastUtils.showSafeToast(MoneyActivity.this,"获取数据失败");
                    MoneyActivity.this.finish();
                }
            }
        });
    }

    @OnClick(R.id.btn_logout)
    public void onClick(View view) {
        if (R.id.btn_logout == view.getId()) {
            Money category = new Money();
            if (mEtMoney.getText() == null || mEtMoney.getText().toString().isEmpty()) {
                return;
            }
            category.setMoney(Integer.parseInt(mEtMoney.getText().toString()));
            category.update(mMoney.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        ToastUtils.showSafeToast(MoneyActivity.this,"充值成功");
                        MessageEvent messageEvent =new MessageEvent(2);
                        messageEvent.setType(2);
                        messageEvent.setMoney(Integer.parseInt(mEtMoney.getText().toString()));
                        EventBus.getDefault().post(messageEvent);
                        MoneyActivity.this.finish();
                    } else {
                        ToastUtils.showSafeToast(MoneyActivity.this,"充值失败");
                    }
                }
            });
        }
    }

    /**
     * 初始化标题栏
     */
    private void initToolBar() {

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
