package com.sevenshop.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sevenshop.SevenShopApplication;
import com.sevenshop.R;
import com.sevenshop.activity.LoginActivity;
import com.sevenshop.activity.MainActivity;
import com.sevenshop.activity.ManagerActivity;
import com.sevenshop.activity.MoneyActivity;
import com.sevenshop.activity.ProfileSettingActivity;
import com.sevenshop.bean.MessageEvent;
import com.sevenshop.bean.Money;
import com.sevenshop.bean.User;
import com.sevenshop.contants.Contants;
import com.sevenshop.utils.GlideUtils;
import com.sevenshop.utils.ListUtils;
import com.sevenshop.utils.ToastUtils;
import com.sevenshop.widget.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * <pre>
 *     desc   : 我的 fragment
 *     version: 1.0
 * </pre>
 */
public class MineFragment extends BaseFragment {

    @BindView(R.id.img_head)
    CircleImageView mImageHead;
    @BindView(R.id.txt_username)
    TextView        mTxtUserName;
    @BindView(R.id.btn_logout)
    Button          mbtnLogout;

    @BindView(R.id.tv_money)
    TextView mTextMoney;

    private Money mMoney;
    private boolean isLogin = false;

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        if (BmobUser.isLogin()) {
            User user = BmobUser.getCurrentUser(User.class);
            showUser(user);
            isLogin = true;
        } else {
            Intent intent2 = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent2);
            isLogin = false;
        }
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.fragment_mine;
    }


    @OnClick({R.id.txt_username,R.id.me_user_tv_cart,R.id.me_item_money, R
            .id.img_head, R.id.btn_logout,R.id.me_user_tv_favorite,R.id.me_user_tv_history})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.me_user_tv_cart:

                Intent intent2 = new Intent(getActivity(), ManagerActivity.class);
                intent2.putExtra("type",HotFragment.PUBLISH_MANAGER);
                startActivity(intent2);
                break;
            case R.id.me_user_tv_favorite:
                intent2 = new Intent(getActivity(), ManagerActivity.class);
                intent2.putExtra("type",HotFragment.COLLECT_MANAGER);
                startActivity(intent2);
                break;

            case R.id.me_user_tv_history:
                intent2 = new Intent(getActivity(), ManagerActivity.class);
                intent2.putExtra("type",HotFragment.BUY_MANAGER);
                startActivity(intent2);
                break;
            case R.id.txt_username:
            case R.id.img_head:
                if (!BmobUser.isLogin()) {
                    intent2 = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent2);
                    isLogin = false;
                } else {
                    intent2 = new Intent(getActivity(), ProfileSettingActivity.class);
                    MineFragment.this.startActivityForResult(intent2,0);
                }
                break;
            case R.id.btn_logout:
                if (BmobUser.isLogin()) {
                    BmobUser.logOut();
                    showUser(null);
                }
                break;
            case R.id.me_item_money:
                intent2 = new Intent(getActivity(), MoneyActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }

    private void showUser(User user) {
        if (user != null) {
            if (user.getUsername().isEmpty()) {
                mTxtUserName.setText("点击编辑昵称");
            } else {
                mTxtUserName.setText(user.getUsername());
            }
            getMoney();
            GlideUtils.portrait(getContext(), user.getLogo_url(), mImageHead);
        } else {
            mbtnLogout.setText("请登录");
            mTxtUserName.setText("请登录");
            GlideUtils.portrait(getContext(), null, mImageHead);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            update();
        }
    }

    public void update() {
            User user = BmobUser.getCurrentUser(User.class);
            showUser(user);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getType() == 2) {
            mTextMoney.setText(event.getMoney()+"");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isLogin) {
            if (BmobUser.isLogin()) {
                isLogin = true;
                update();
            } else {
                Intent intent2 = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent2);
                isLogin = false;
            }
        }
    }

    private  void getMoney() {
        BmobQuery<Money> categoryBmobQuery = new BmobQuery<>();
        categoryBmobQuery.addWhereEqualTo("user", BmobUser.getCurrentUser(User.class));
        categoryBmobQuery.include("author");
        categoryBmobQuery.findObjects(new FindListener<Money>() {
            @Override
            public void done(List<Money> object, BmobException e) {
                if (e == null) {
                    if (!ListUtils.isEmpty(object)) {
                        mMoney = object.get(0);
                        mTextMoney.setText(mMoney.getMoney()+"");
                    }
                } else {
                }
            }
        });
    }
}
