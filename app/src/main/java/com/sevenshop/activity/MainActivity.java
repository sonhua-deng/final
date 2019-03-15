package com.sevenshop.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.bumptech.glide.load.engine.Resource;
import com.sevenshop.R;
import com.sevenshop.bean.MessageEvent;
import com.sevenshop.bean.Tab;
import com.sevenshop.bean.User;
import com.sevenshop.fragment.HomeFragment;
import com.sevenshop.fragment.MineFragment;
import com.sevenshop.fragment.ShopCartFragment;
import com.sevenshop.utils.ToastUtils;
import com.sevenshop.widget.FragmentTabHost;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Describe:整个app的主入门
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private FragmentTabHost mTabhost;
    private LayoutInflater  mInflater;
    private List<Tab> mTabs = new ArrayList<>();
    private ShopCartFragment shopCartFragment;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void init() {
        ImageView imageView = findViewById(R.id.iv_post);
        imageView.setOnClickListener(this);
        initTab();
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_main;
    }

    private void initTab() {

        Tab tab_home = new Tab(HomeFragment.class, R.string.home, R.drawable.selector_icon_home);
//        Tab tab_hot = new Tab(HotFragment.class, R.string.hot, R.drawable.selector_icon_hot);
//        Tab tab_category = new Tab(CategoryFragment.class, R.string.catagory, R.drawable
//                .selector_icon_category);
//        Tab tab_shop = new Tab(ShopCartFragment.class, R.string.cart, R.drawable
//                .selector_icon_cart);
       // Tab tab_publish = new Tab(PhotoFragment.class, R.string.publish, R.drawable.icon_publish);
        Tab tab_mine = new Tab(MineFragment.class, R.string.mine, R.drawable.selector_icon_mine);

        mTabs.add(tab_home);
        //mTabs.add(tab_publish);
//        mTabs.add(tab_category);
//        mTabs.add(tab_shop);
        mTabs.add(tab_mine);

        mInflater = LayoutInflater.from(this);
        mTabhost = (FragmentTabHost) this.findViewById(android.R.id.tabhost);
        mTabhost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        int i = 0;
        for (Tab tab : mTabs) {
            TabHost.TabSpec tabSpec = mTabhost.newTabSpec(getString(tab.getTitle()));
            tabSpec.setIndicator(buildIndicator(tab,i));
            tabSpec.getTag();
            i++;
            mTabhost.addTab(tabSpec, tab.getFragment(), null);
        }


        mTabhost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        mTabhost.setCurrentTab(0);           //默认选中第0个
    }



    private View buildIndicator(Tab tab, int i) {

        View view = mInflater.inflate(R.layout.tab_indicator, null);
        ImageView img = (ImageView) view.findViewById(R.id.icon_tab);
        img.setImageResource(tab.getIcon());
        return view;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getType() == 0) {
            mTabhost.setCurrentTab(1);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    //控制物理返回键
    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                ToastUtils.showSafeToast(MainActivity.this, "再点一次退出轻松购");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_post) {
            Intent intent = new Intent(MainActivity.this,PublishActivity.class);
            startActivity(intent);
        }
    }

    /**
     * dp 转 px
     *
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpVal * scale + 0.5f);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mTabhost.setCurrentTab(0);           //默认选中第0个
        if (intent.getBooleanExtra("islogin",false)) {
            MineFragment mineFragment = (MineFragment) getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.mine));
            mineFragment.update();
        }
    }

}
