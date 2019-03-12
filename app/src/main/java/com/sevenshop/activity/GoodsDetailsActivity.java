package com.sevenshop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.sevenshop.SevenShopApplication;
import com.sevenshop.R;
import com.sevenshop.bean.HotGoods;
import com.sevenshop.bean.User;
import com.sevenshop.contants.HttpContants;
import com.sevenshop.helper.SharePresenter;
import com.sevenshop.utils.CartShopProvider;
import com.sevenshop.utils.LogUtil;
import com.sevenshop.utils.ToastUtils;
import com.sevenshop.widget.EnjoyshopToolBar;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import okhttp3.Call;

/**
 * Describe: 商品详情
 */

public class GoodsDetailsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;
    @BindView(R.id.webView)
    WebView      mWebView;


    @Override
    protected int getContentResourseId() {
        return R.layout.activity_goods_detail;
    }

    @Override
    protected void init() {

        initData();
        initToolBar();
    }


    private void initData() {



    }

    /**
     * 初始化标题栏
     */
    private void initToolBar() {

        mToolBar.setNavigationOnClickListener(this);
        mToolBar.setRightButtonText("分享");
        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePresenter.getInstance().showShareDialogOnBottom
                        (0, GoodsDetailsActivity.this, "计算机书籍",
                                "第二行代码", "0");
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar:
                this.finish();
                break;
        }
    }

    private void addToFavorite() {


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
