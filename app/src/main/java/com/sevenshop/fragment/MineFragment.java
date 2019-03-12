package com.sevenshop.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sevenshop.SevenShopApplication;
import com.sevenshop.R;
import com.sevenshop.activity.LoginActivity;
import com.sevenshop.activity.ProfileSettingActivity;
import com.sevenshop.bean.User;
import com.sevenshop.contants.Contants;
import com.sevenshop.utils.GlideUtils;
import com.sevenshop.utils.ToastUtils;
import com.sevenshop.widget.CircleImageView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;


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

    private boolean isLogin = false;

    @Override
    protected void init() {
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


    @OnClick({R.id.txt_username, R
            .id.img_head, R.id.btn_logout})
    public void onclick(View view) {
        switch (view.getId()) {
//            //收货地址
//            case R.id.txt_my_address:
//                startActivity(new Intent(getActivity(), AddressListActivity.class), true);
//                break;
//            //我的收藏
//            case R.id.txt_my_favorite:
//                startActivity(new Intent(getActivity(), MyFavoriteActivity.class), true);
//                break;
//            //我的订单
//            case R.id.txt_my_orders:
//                startActivity(new Intent(getActivity(), MyOrdersActivity.class), true);
//                break;
            case R.id.txt_username:
            case R.id.img_head:
                if (!BmobUser.isLogin()) {
                    Intent intent2 = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent2);
                    isLogin = false;
                } else {
                    Intent intent2 = new Intent(getActivity(), ProfileSettingActivity.class);
                    startActivity(intent2);
                }
                break;
            case R.id.btn_logout:
                if (BmobUser.isLogin()) {
                    BmobUser.logOut();
                    showUser(null);
                }
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
            GlideUtils.portrait(getContext(), user.getLogo_url(), mImageHead);
        } else {
            mbtnLogout.setText("请登录");
            mTxtUserName.setText("请登录");
            GlideUtils.portrait(getContext(), null, mImageHead);
        }
    }

    public void update() {
        if (BmobUser.isLogin()) {
            User user = BmobUser.getCurrentUser(User.class);
            showUser(user);
        } else {
            Intent intent2 = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent2);
            isLogin = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isLogin) {
            isLogin = true;
            update();
        }
    }
}
