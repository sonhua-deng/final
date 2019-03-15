package com.sevenshop.activity;



import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.sevenshop.R;
import com.sevenshop.fragment.HotFragment;
import com.sevenshop.widget.EnjoyshopToolBar;

import butterknife.BindView;

public class ManagerActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;

    private int mType;

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_publish_manager;
    }

    @Override
    protected void init() {
        mType = getIntent().getIntExtra("type",1);
        initToolBar();
        HotFragment fragment = HotFragment.newInstance(mType);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager. beginTransaction();
        transaction.replace(R.id.right_layout,fragment);
        transaction.commit();
    }
    private void initToolBar() {
        if (mType == HotFragment.PUBLISH_MANAGER) {
            mToolBar.setTitle("发布管理");
        }
        if (mType == HotFragment.COLLECT_MANAGER) {
            mToolBar.setTitle("收藏管理");
        }
        if (mType == HotFragment.BUY_MANAGER) {
            mToolBar.setTitle("买入管理");
        }
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
