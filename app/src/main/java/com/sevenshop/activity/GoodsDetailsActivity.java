package com.sevenshop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sevenshop.SevenShopApplication;
import com.sevenshop.R;
import com.sevenshop.adapter.DetailPhotosAdapter;
import com.sevenshop.bean.Collect;
import com.sevenshop.bean.Goods;
import com.sevenshop.bean.MessageEvent;
import com.sevenshop.bean.Money;
import com.sevenshop.bean.User;
import com.sevenshop.utils.ListUtils;
import com.sevenshop.utils.ToastUtils;
import com.sevenshop.widget.EnjoyshopToolBar;
import com.sevenshop.widget.MDDialog;
import com.youth.banner.loader.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Describe: 商品详情
 */

public class GoodsDetailsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;


    @BindView(R.id.tv_time)
    TextView  mTvTime;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.current_price)
    TextView  mTvCurrentPrice;

    @BindView(R.id.start_price)
    TextView  mTvStartPriice;

    @BindView(R.id.tv_title)
    TextView mTvTitle;

    @BindView(R.id.tv_desc)
    TextView mTvDesc;

    @BindView(R.id.avatar)
    SimpleDraweeView mIvAvatar;

    @BindView(R.id.tv_user)
    TextView mTvUser;

    @BindView(R.id.tv_collect)
    TextView mTvCollect;

    @BindView(R.id.tv_buy)
    TextView mTvBuy;


    private Goods mGoods;
    private boolean mIsCollect;
    private String mObjectId;
    private boolean mIsBuy;

    private int mCurPrice;

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_goods_detail;
    }

    @Override
    protected void init() {

        initData();
        initToolBar();
        showUI();
       DetailPhotosAdapter mAdatper = new DetailPhotosAdapter(mGoods.getPhotoUrls(), this);
        mRecyclerView.setAdapter(mAdatper);
        mRecyclerView.setLayoutManager( new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

//        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();

    }


    private void showUI() {
        if(mGoods.getPublishType().equals("平台出售")) {
            startH();
            changeTime();
            mTvBuy.setText("点击拍卖");
            mTvCurrentPrice.setText("当前价格: "+mGoods.getCurrenPrice());
            mTvStartPriice.setText("起始价格: "+mGoods.getStartPrice());
        } else {
            mTvTime.setVisibility(View.GONE);
            mTvCurrentPrice.setText("当前价格: "+mGoods.getCurrenPrice());
            mTvStartPriice.setVisibility(View.GONE);
        }
        if (mGoods.isShell()) {
            mTvBuy.setVisibility(View.GONE);
        }
        mTvTitle.setText(mGoods.getTitle());
        mTvDesc.setText(mGoods.getDes());
        mIvAvatar.setImageURI(mGoods.getPulisher().getLogo_url());
        mTvUser.setText(mGoods.getPulisher().getNickName());
        if (BmobUser.isLogin()) {
            isCollect();
        } else {
            mTvCollect.setText("点击收藏");
            mIsCollect = false;
        }

    }

    /**
     * 轮播图数据
     */


    private void initData() {
        mGoods = (Goods) getIntent().getSerializableExtra("itemClickGoods");
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


    @OnClick({R.id.tv_collect,R.id.tv_buy})
    public void onClick(View view) {
        if (!BmobUser.isLogin()) {
            Intent intent2 = new Intent(this, LoginActivity.class);
            startActivity(intent2);
            return;
        }
        switch (view.getId()) {
            case R.id.tv_collect:
                if (!mIsCollect) {
                    collect();
                } else {
                    cancelCollect();
                }
                break;
            case R.id.tv_buy:
                if(mGoods.getPublishType().equals("平台出售")) {
                    showUpdateNickNameDialog();
                } else {
                    buy();
                }
                break;
        }
    }
    private void showUpdateNickNameDialog() {

        MDDialog.newBuilder()
                .input()
                .content("输入拍卖金额")
                .inputType(InputType.TYPE_CLASS_NUMBER )
                .inputRangeRes(1, 16, getResources().getColor(android.R.color.holo_red_light))
                .cancelOutside(false)
                .hasNegative(true)    // 是否显示取消
                .hasPositive(true)    // 是否显示确定
                .positive("提交")
                .negative("取消")
                .preInsert("不得低于当前价格", "", false, new MDDialog.OnDialogInputListener() {
                    @Override
                    public void onInput(@NonNull MDDialog dialog, CharSequence input) {
                        if (dialog.getInputEditText() != null) {
                            GoodsDetailsActivity.this
                                    .hideKeyboard(dialog.getInputEditText());
                        }
//                        if (mUserInfoStruct == null) {
//                            showToast(R.string.setting_modify_fail, Toast.LENGTH_SHORT);
//                            return;
//                        }

                        if (!TextUtils.isEmpty(input)) {
                            String name = input.toString().trim().replace("\n", "");
                            if (TextUtils.isEmpty(name)) {
                                return;
                            }
                            mCurPrice = (int)Integer.parseInt(name);
                        }
                    }
                })
                .listener(new MDDialog.OnDialogClickListener() {
                    @Override
                    public void onPositiveClick(MDDialog dialog) {
                        if (mCurPrice<=mGoods.getCurrenPrice()) {
                            ToastUtils.showSafeToast(GoodsDetailsActivity.this,"不得低于当前价格");
                        } else {
                            Goods goods = new Goods();
                            goods.setBuyer(BmobUser.getCurrentUser(User.class));
                            goods.setCurrenPrice(mCurPrice);
                            goods.update(mGoods.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        ToastUtils.showSafeToast(GoodsDetailsActivity.this,"拍卖成功");
                                        mTvCurrentPrice.setText("当前价格: "+mCurPrice);
                                    } else {
                                        ToastUtils.showSafeToast(GoodsDetailsActivity.this,"拍卖失败");
                                    }
                                }
                            });
                        }
                        dialog.dismissAllowingStateLoss();
                    }

                    @Override
                    public void onNegativeClick(MDDialog dialog) {
                        if (dialog.getInputEditText() != null) {
                            GoodsDetailsActivity.this.hideKeyboard(dialog.getInputEditText());
                        }
                        dialog.dismissAllowingStateLoss();
                    }
                })
                .build().showWithActivity(this);
    }

    private void buy() {
            BmobQuery<Money> categoryBmobQuery = new BmobQuery<>();
            categoryBmobQuery.addWhereEqualTo("user", BmobUser.getCurrentUser(User.class));
            categoryBmobQuery.include("author");
            categoryBmobQuery.findObjects(new FindListener<Money>() {
                @Override
                public void done(List<Money> object, BmobException e) {
                    if (e == null) {
                        if (!ListUtils.isEmpty(object)) {
                            if (object.get(0).getMoney()>= mGoods.getCurrenPrice()) {
                                updatePulisherMoney();
                                updateMoney(object.get(0).getObjectId(),object.get(0).getMoney() - mGoods.getCurrenPrice());
                                Goods goods = new Goods();
                                goods.setBuyer(BmobUser.getCurrentUser(User.class));
                                goods.setShell(true);
                                goods.update(mGoods.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            ToastUtils.showSafeToast(GoodsDetailsActivity.this,"购买成功");
                                            mTvBuy.setVisibility(View.GONE);
                                        } else {
                                            ToastUtils.showSafeToast(GoodsDetailsActivity.this,"购买失败");
                                        }
                                    }
                                });
                            } else {
                                ToastUtils.showSafeToast(GoodsDetailsActivity.this,"余额不足");
                            }
                        }
                    } else {
                        ToastUtils.showSafeToast(GoodsDetailsActivity.this,"购买失败");
                    }
                }
            });
    }

    private void updatePulisherMoney() {
        BmobQuery<Money> categoryBmobQuery = new BmobQuery<>();
        categoryBmobQuery.addWhereEqualTo("user", mGoods.getPulisher());
        categoryBmobQuery.include("author");
        categoryBmobQuery.findObjects(new FindListener<Money>() {
            @Override
            public void done(List<Money> object, BmobException e) {
                if (e == null) {
                    if (!ListUtils.isEmpty(object)) {
                       updateMoney(object.get(0).getObjectId(),object.get(0).getMoney() + mGoods.getCurrenPrice());
                    }
                } else {

                }
            }
        });
    }
private void  updateMoney(String objectId, final int money) {
    Money category = new Money();
    category.setMoney(money);
    category.update(objectId, new UpdateListener() {
        @Override
        public void done(BmobException e) {
            if (e == null) {
                MessageEvent messageEvent =new MessageEvent(2);
                messageEvent.setType(2);
                messageEvent.setMoney(money);
                EventBus.getDefault().post(messageEvent);
            } else {

            }
        }
    });
}
    private void collect() {
        Collect collect = new Collect();
        collect.setCollecter(BmobUser.getCurrentUser(User.class));
        collect.setGoods(mGoods);
        collect.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                   mIsCollect = true;
                   mTvCollect.setText("取消收藏");
                    mObjectId = objectId;
                    ToastUtils.showSafeToast(GoodsDetailsActivity.this,"收藏成功");
                } else {
                    ToastUtils.showSafeToast(GoodsDetailsActivity.this,"收藏失败");
                }
            }
        });
    }

    private void cancelCollect() {
        Collect category = new Collect();
        category.delete(mObjectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    ToastUtils.showSafeToast(GoodsDetailsActivity.this,"取消收藏成功");
                    mTvCollect.setText("点击收藏");
                    mIsCollect = false;
                    mObjectId = "";
                } else {
                    ToastUtils.showSafeToast(GoodsDetailsActivity.this,"取消收藏失败");

                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(SevenShopApplication.sContext).load(path).into(imageView);
        }
    }

    private void isCollect() {
        BmobQuery<Collect> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("collecter", BmobUser.getCurrentUser(User.class));
        bmobQuery.include("goods,collecter");
        bmobQuery.findObjects(new FindListener<Collect>() {
            @Override
            public void done(List<Collect> object, BmobException e) {
                if (e == null) {
                    if (! ListUtils.isEmpty(object)) {
                        for (Collect o: object) {
                            if (o.getGoods().getObjectId().equals(mGoods.getObjectId())) {
                                mObjectId = o.getObjectId();
                                mTvCollect.setText("取消收藏");
                                mIsCollect = true;
                                return;
                            }
                        }
                    }
                }
                mTvCollect.setText("点击收藏");
                mIsCollect = false;
            }
        });
    }

    private Handler handler = new Handler();

    public void startH() {
        handler.postDelayed(runnable, 1000); // 开始Timer
    }

    private Runnable runnable = new Runnable() {
        public void run() {
            changeTime();
            handler.postDelayed(this, 1000);    //postDelayed(this,1000)方法安排一个Runnable对象到主线程队列中

        }

    };
    public void changeTime() {
        try {
            String times = mGoods.getCreatedAt();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(times);
            long timeCreate = date.getTime();
            long sheng =24*60*60*1000- (System.currentTimeMillis() - timeCreate);

            String sDateTime =  ""+sheng/1000/60/60+":"+sheng/1000/60%60+":"+sheng/1000%60;
            mTvTime.setText("距离结束时间剩下: "+sDateTime);
            if(sheng<=0) {
                handler.removeCallbacks(runnable);
                Goods goods = new Goods();
                goods.setShell(true);
                goods.update(mGoods.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            ToastUtils.showSafeToast(GoodsDetailsActivity.this,"购买成功");
                            mTvBuy.setVisibility(View.GONE);
                        } else {
                            ToastUtils.showSafeToast(GoodsDetailsActivity.this,"购买失败");
                        }
                    }
                });
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
