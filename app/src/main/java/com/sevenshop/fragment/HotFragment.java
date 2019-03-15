package com.sevenshop.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.sevenshop.R;
import com.sevenshop.activity.GoodsDetailsActivity;
import com.sevenshop.adapter.HotGoodsAdapter;
import com.sevenshop.bean.Collect;
import com.sevenshop.bean.Goods;
import com.sevenshop.bean.HotGoods;
import com.sevenshop.bean.User;
import com.sevenshop.contants.HttpContants;
import com.sevenshop.utils.ListUtils;
import com.sevenshop.utils.LogUtil;
import com.sevenshop.utils.ToastUtils;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import okhttp3.Call;

import static com.zhy.http.okhttp.log.LoggerInterceptor.TAG;


/**
 * <pre>

 *     desc   : 热卖商品fragment
 *     version: 2.0
 * </pre>
 */
public class HotFragment extends BaseFragment {

    public static final int MAIN_TAB = 0;
    public static final int PUBLISH_MANAGER = 1;
    public static final int COLLECT_MANAGER   = 2;
    public static final int BUY_MANAGER   = 3;
    public static final int  SEARCH_RESULT   = 4;


    private static final int STATE_NORMAL = 0;
    private static final int STATE_REFREH = 1;
    private static final int STATE_MORE   = 2;
    private              int state        = STATE_NORMAL;       //正常情况

    private int limit = 10; // 每页的数据是10条
    private int curPage = 0; // 当前页的编号，从0开始
    String lastTime = null;
    int lastMoney = 0;
    @BindView(R.id.recyclerview)
    RecyclerView          mRecyclerView;
    @BindView(R.id.refresh_view)
    MaterialRefreshLayout mRefreshLaout;

    private int  currPage  = 1;     //当前是第几页
    private int  totalPage = 1;    //一共有多少页
    private int  pageSize  = 10;     //每页数目
    private Gson mGson     = new Gson();
    private List<Goods> datas;
    private HotGoodsAdapter         mAdatper;
    private String mShellType;
    private int mType;
    private String mSearch;
    private String mGoodsShellType = "全部";
    private String mGoodsType = "全部";
    private int mPriceOrder = 0;

    public static HotFragment newInstance(Context context,int position,int type) {
        HotFragment fragment = new HotFragment();
        Bundle bundle = new Bundle();
        bundle.putString("shellType",context.getResources().getStringArray(R.array.shell_type)[position]);
        bundle.putInt("type",type);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static HotFragment newInstance(int type) {
        HotFragment fragment = new HotFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type",type);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static HotFragment newInstance(int type,String search) {
        HotFragment fragment = new HotFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type",type);
        bundle.putString("search",search);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.fragment_hot;
    }

    @Override
    protected void init() {
        initRefreshLayout();     //控件初始化
        if (getArguments() != null) {
            mType = getArguments().getInt("type");
            mShellType = getArguments().getString("shellType","null");
            mSearch = getArguments().getString("search","手机");
        }
        getData(0,STATE_REFREH);
    }


//    //跳转到搜索界面
//    @OnClick({R.id.toolbar})
//    public void searchView(View view) {
//        startActivity(new Intent(getContext(), SearchActivity.class));
//    }


    private void initRefreshLayout() {

        mRefreshLaout.setLoadMore(true);
        mRefreshLaout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refreshData();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                if (currPage <= totalPage) {
                    loadMoreData();
                } else {
                    ToastUtils.showSafeToast(getContext(),"没有更多数据啦");
                    mRefreshLaout.finishRefreshLoadMore();
                }
            }
        });
    }

    /**
     * 加载更多
     */
    private void loadMoreData() {
        state = STATE_MORE;
        getData(curPage, STATE_MORE);
    }

    /**
     * 刷新
     */
    public void refreshData() {
        state = STATE_REFREH;
        currPage = 1;
        getData(0,STATE_REFREH);
    }

    public void reData(String rearch,String type, String shell,int price) {
        mSearch = rearch;
        mGoodsType = type;
        mGoodsShellType = shell;
        mPriceOrder = price;
        refreshData();
    }

    public void getCollectData() {
            BmobQuery<Collect> bmobQuery = new BmobQuery<>();
            bmobQuery.addWhereEqualTo("collecter", BmobUser.getCurrentUser(User.class));
            bmobQuery.include("goods,collecter,goods.pulisher,goods.buyer");
            bmobQuery.findObjects(new FindListener<Collect>() {
                @Override
                public void done(List<Collect> object, BmobException e) {
                    if (e == null) {
                        List<Goods> list = new ArrayList<>();
                        if (! ListUtils.isEmpty(object)) {
                            for (Collect o: object) {
                                list.add(o.getGoods());
                            }
                        }
                        datas = list;
                        showData();
                    }
                }
            });
    }


    private void getSearchData(int page, final int actionType) {
        BmobQuery<Goods> query = new BmobQuery<>();
        // 按时间降序查询
        if(mPriceOrder == 0) {
            query.order("-createdAt");
        } else if (mPriceOrder == 1) {
            query.order("-currenPrice");
        } else {
            query.order("currenPrice");
        }

        int count = 0;
        // 如果是加载更多
        if (actionType == STATE_MORE) {
            if(mPriceOrder == 0) {
                // 处理时间查询
                Date date = null;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    date = sdf.parse(lastTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // 只查询小于等于最后一个item发表时间的数据
                query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
                // 跳过之前页数并去掉重复数据
            } else if (mPriceOrder == 1) {
                query.addWhereLessThanOrEqualTo("currenPrice", lastMoney);
            } else {
                query.addWhereGreaterThan("currenPrice", lastMoney);
            }
            query.setSkip(page * count + 1);
        } else {
            // 下拉刷新
            page = 0;
            query.setSkip(page);
        }
        if (!mGoodsShellType.equals("全部")) {
            query.addWhereEqualTo("shellType",mGoodsShellType);
        }
        if (!mGoodsType.equals("全部")) {
            query.addWhereEqualTo("type",mGoodsType);
        }
        // 设置每页数据个数
        query.setLimit(limit);
        // 查找数据
        query.include("pulisher,buyer");
        query.findObjects(new FindListener<Goods>() {
            @Override
            public void done(List<Goods> list1, BmobException e) {
                if (e == null) {
                    List<Goods> list = new ArrayList<>();
                    for (Goods g: list1) {
                        if (isMatch(g.toString())) {
                            list.add(g);
                        }
                    }
                    if (list.size() > 0) {

                        if (actionType == STATE_REFREH) {
                            // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                            curPage = 0;
                            // 获取最后时间
                            lastTime = list.get(list.size() - 1).getCreatedAt();
                            lastMoney = list.get(list.size() - 1).getCurrenPrice();
                        }

                        datas = list;
                        showData();

                        // 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
                        curPage++;
//					 showToast("第"+(page+1)+"页数据加载完成");
                    } else if (actionType == STATE_MORE) {
                        ToastUtils.showSafeToast(mContext,"没有更多数据了");
                    } else if (actionType == STATE_REFREH) {
                        ToastUtils.showSafeToast(mContext,"没有更多数据了");
                    }
                } else {

                }
            }
        });
    }
    private boolean isMatch(String s2) {
        Pattern p = Pattern.compile(mSearch);
        Matcher m = p.matcher(s2);
        return m.find();
    }

        /**
         * 分页获取数据
         *
         * @param page
         *            页码
         * @param actionType
         *            ListView的操作类型（下拉刷新、上拉加载更多）
         */
    private void getData(int page, final int actionType) {
        if(mType == COLLECT_MANAGER) {
            getCollectData();
            return;
        }
        if (mType == SEARCH_RESULT) {
            getSearchData(page,actionType);
            return;
        }
        BmobQuery<Goods> query = new BmobQuery<>();
        // 按时间降序查询
        query.order("-createdAt");
        int count = 0;
        // 如果是加载更多
        if (actionType == STATE_MORE) {
            // 处理时间查询
            Date date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(lastTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // 只查询小于等于最后一个item发表时间的数据
            query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
            // 跳过之前页数并去掉重复数据
            query.setSkip(page * count + 1);
        } else {
            // 下拉刷新
            page = 0;
            query.setSkip(page);
        }
        if (mType == MAIN_TAB) {
            query.addWhereEqualTo("shellType",mShellType);
        } else if (mType == PUBLISH_MANAGER) {
            query.addWhereEqualTo("pulisher",BmobUser.getCurrentUser(User.class));
        }else if (mType == BUY_MANAGER) {
            query.addWhereEqualTo("buyer",BmobUser.getCurrentUser(User.class));
        }
        // 设置每页数据个数
        query.setLimit(limit);
        query.include("pulisher,buyer");
        // 查找数据
        query.findObjects(new FindListener<Goods>() {
            @Override
            public void done(List<Goods> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {

                        if (actionType == STATE_REFREH) {
                            // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                            curPage = 0;
                            // 获取最后时间
                            lastTime = list.get(list.size() - 1).getCreatedAt();
                        }

                        datas = list;
                        showData();

                        // 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
                        curPage++;
//					 showToast("第"+(page+1)+"页数据加载完成");
                    } else if (actionType == STATE_MORE) {
                        ToastUtils.showSafeToast(mContext,"没有更多数据了");
                    } else if (actionType == STATE_REFREH) {
                        ToastUtils.showSafeToast(mContext,"没有更多数据了");
                    }
                } else {

                }
            }
        });
    }
    /**
     * 展示数据
     */
    private void showData() {
        switch (state) {
            case STATE_NORMAL:
                mAdatper = new HotGoodsAdapter(datas, getContext());
                mRecyclerView.setAdapter(mAdatper);
                mRecyclerView.setLayoutManager( new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//                mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
//                        DividerItemDecoration.HORIZONTAL));
                break;
            case STATE_REFREH:
                mAdatper.clearData();
                if (mType == HotFragment.SEARCH_RESULT) {
                    mAdatper.addSearchData(datas);
                } else {
                    mAdatper.addData(datas);
                }
                mRecyclerView.scrollToPosition(0);
                mRefreshLaout.finishRefresh();
                break;
            case STATE_MORE:
                mAdatper.addData(mAdatper.getDatas().size(), datas);
                mRecyclerView.scrollToPosition(mAdatper.getDatas().size());
                mRefreshLaout.finishRefreshLoadMore();
                break;
        }


        mAdatper.setOnItemClickListener(new HotGoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //借助currPage 和pageSize 可以实现默认情况和刷新时,都可以使用
                Goods listBean = mAdatper.getDatas().get(position);
                Intent intent = new Intent(getContext(), GoodsDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Bundle bundle = new Bundle();
                bundle.putSerializable("itemClickGoods", (Serializable) listBean);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
