package com.sevenshop.activity;

import android.view.View;

import com.sevenshop.R;
import com.sevenshop.widget.EnjoyshopToolBar;

import butterknife.BindView;

/**
 * Describe: 我的订单
 */

public class MyOrdersActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;

    @Override
    protected void init() {
        initToolBar();
    }


    @Override
    protected int getContentResourseId() {
        return R.layout.activity_myorder;
    }

    private void initToolBar() {
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyOrdersActivity.this.finish();
            }
        });
    }
}
