package com.enjoyshop.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.activity.GoodsListActivity;
import com.enjoyshop.activity.SearchActivity;
import com.enjoyshop.adapter.GridAdapter;
import com.enjoyshop.adapter.HomeCatgoryAdapter;
import com.enjoyshop.bean.BannerBean;
import com.enjoyshop.bean.HomeCampaignBean;
import com.enjoyshop.contants.Contants;
import com.enjoyshop.contants.HttpContants;
import com.enjoyshop.data.HomeGridInfo;
import com.enjoyshop.helper.DividerItemDecortion;
import com.enjoyshop.widget.EnjoyshopToolBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.library.tabstrip.PagerSlidingTabStrip;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;


/**
 * <pre>
 *     desc   : 首页fragment
 *     version: 1.1
 * </pre>
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;
    @BindView(R.id.banner)
    Banner  mBanner;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.home_gridView)
    GridView gridView1;
    @BindView(R.id.tab_strip)
    PagerSlidingTabStrip mTabStrip;

    private HomeCatgoryAdapter mAdatper;
    private List<String>           images = new ArrayList<>();
    private List<String>           titles = new ArrayList<>();
    private List<HomeCampaignBean> datas  = new ArrayList<>();
    private Gson                   gson   = new Gson();
    private List<HomeGridInfo> pageOneData = new ArrayList<>();

    @Override
    protected void init() {

        initView();
        requestBannerData();     //请求轮播图数据
        //requestCampaignData();     //请求商品详情数据
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.fragment_home;
    }


    @Override
    public void onStart() {
        super.onStart();
        mBanner.startAutoPlay();
    }


    private void initView() {
        mToolBar.setOnClickListener(this);
        //设置banner样式
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        //设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader());

        String[] gridTitles = getResources().getStringArray(R.array.home_bar_labels);
        TypedArray typedArray = getResources().obtainTypedArray(R.array.home_bar_icon);
        for (int i = 0; i < gridTitles.length; i++) {
            pageOneData.add(new HomeGridInfo(typedArray.getResourceId(i,0),gridTitles[i]));
        }


        gridView1.setAdapter(new GridAdapter(getActivity(),pageOneData));
        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        FragmentManager fragmentManager = getChildFragmentManager();
        mViewPager.setAdapter(new MyPagerAdapter(fragmentManager));
        mViewPager.setCurrentItem(0);
        //当ViewPager的onPagerChangeListener回调时，PagerSlidingTabStrip也一起随之变动
        //具体做法都已封装到了PagerSlidingTabStrip.setViewPager()方法里，使用时调用实例如下
        mTabStrip.setViewPager(mViewPager);
    }


    /**
     * 轮播图数据
     */
    private void setBannerData() {
        //设置图片集合
        mBanner.setImages(images);
        //设置标题集合（当banner样式有显示title时）
        mBanner.setBannerTitles(titles);

        //设置指示器位置（当banner模式中有指示器时）
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        mBanner.start();
    }

    /**
     * 自定义适配器
     */
    class MyPagerAdapter extends FragmentPagerAdapter {
        //根据需求定义构造方法，方便外部调用
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        //设置每页的标题
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "平台";
            } else {
                return "个人";
            }
        }
        //设置每一页对应的fragment
        @Override
        public Fragment getItem(int position) {
            return new HotFragment();
        }
        //fragment的数量
        @Override
        public int getCount() {
            return 2;
        }
    }
//    /**
//     * 首页商品数据
//     */
//
//    private Long defaultId = 0L;
//
//    private void setRecyclerViewData() {
//
//        for (int i = 0; i < datas.size(); i++) {
//            if (i % 2 == 0) {
//                //左边样式的item
//                datas.get(i).setItemType(HomeCampaignBean.ITEM_TYPE_LEFT);
//            } else {
//                //右边样式的item
//                datas.get(i).setItemType(HomeCampaignBean.ITEM_TYPE_RIGHT);
//            }
//        }
//
//        mAdatper = new HomeCatgoryAdapter(datas);
//        mRecyclerView.setAdapter(mAdatper);
//        mRecyclerView.addItemDecoration(new DividerItemDecortion());
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mAdatper.addHeaderView(viewHeader);
//
//        mAdatper.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                HomeCampaignBean campaign = (HomeCampaignBean) adapter.getData().get(position);
//                Intent intent = new Intent(getContext(), GoodsListActivity.class);
//                intent.putExtra(Contants.COMPAINGAIN_ID, campaign.getId());
//                startActivity(intent);
//            }
//        });
//
//        mAdatper.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
//            @Override
//            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//
//                HomeCampaignBean bean = (HomeCampaignBean) adapter.getData().get(position);
//                Long oneId = bean.getCpOne().getId();
//                Long twoId = bean.getCpTwo().getId();
//                Long threeId = bean.getCpThree().getId();
//
//
//                switch (view.getId()){
//                    case R.id.imgview_big:
//                        defaultId=oneId;
//                        break;
//                    case R.id.imgview_small_top:
//                        defaultId=twoId;
//                        break;
//                    case R.id.imgview_small_bottom:
//                        defaultId=threeId;
//                        break;
//                }
//
//                Intent intent = new Intent(getContext(), GoodsListActivity.class);
//                intent.putExtra(Contants.COMPAINGAIN_ID, defaultId);
//                startActivity(intent);
//
//            }
//        });
//
//    }


    /**
     * 请求轮播图的数据
     */
    private void requestBannerData() {

        OkHttpUtils.get().url(HttpContants.HOME_BANNER_URL)
                .addParams("type", "1")
                .build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                Type collectionType = new TypeToken<Collection<BannerBean>>() {
                }.getType();
                Collection<BannerBean> enums = gson.fromJson(response, collectionType);
                Iterator<BannerBean> iterator = enums.iterator();
                while (iterator.hasNext()) {
                    BannerBean bean = iterator.next();
                    titles.add(bean.getName());
                    images.add(bean.getImgUrl());
                }

                setBannerData();
            }
        });
    }


//    /**
//     * 商品分类数据
//     */
//    private void requestCampaignData() {
//
//        OkHttpUtils.get().url(HttpContants.HOME_CAMPAIGN_URL)
//                .addParams("type", "1")
//                .build().execute(new StringCallback() {
//
//            @Override
//            public void onError(Call call, Exception e, int id) {
//
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//
//                Type collectionType = new TypeToken<Collection<HomeCampaignBean>>() {
//                }.getType();
//                Collection<HomeCampaignBean> enums = gson.fromJson(response,
//                        collectionType);
//                Iterator<HomeCampaignBean> iterator = enums.iterator();
//                while (iterator.hasNext()) {
//                    HomeCampaignBean bean = iterator.next();
//                    datas.add(bean);
//                }
//
//                setRecyclerViewData();
//            }
//        });
//    }


    @Override
    public void onStop() {
        super.onStop();
        mBanner.stopAutoPlay();
    }

    //跳转到搜索界面
    @Override
    public void onClick(View v) {
        startActivity(new Intent(getContext(), SearchActivity.class));
    }

    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(EnjoyshopApplication.sContext).load(path).into(imageView);
        }
    }
}
