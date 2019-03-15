package com.sevenshop.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sevenshop.R;
import com.sevenshop.fragment.HotFragment;
import com.sevenshop.widget.ClearEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <pre>
 *   desc   : 搜索结果
 * </pre>
 */

public class SearchResultActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener {


    @BindView(R.id.toolbar_leftButton)
    ImageButton toolbarLeftButton;
    @BindView(R.id.edittxt_phone)
    ClearEditText edittxtPhone;
    @BindView(R.id.gosearch)
    ImageButton gosearch;
    @BindView(R.id.around_supplier_list_tv_product)
    TextView aroundSupplierListTvProduct;
    @BindView(R.id.around_supplier_list_product)
    LinearLayout aroundSupplierListProduct;
    @BindView(R.id.around_supplier_list_tv_sort)
    TextView aroundSupplierListTvSort;
    @BindView(R.id.around_supplier_list_sort)
    LinearLayout aroundSupplierListSort;
    @BindView(R.id.around_supplier_list_tv_activity)
    TextView aroundSupplierListTvActivity;
    @BindView(R.id.around_supplier_list_activity)
    LinearLayout aroundSupplierListActivity;


    private ListView mPopListView;
    String mSearch;

    private String mType = "全部";
    private String mShellType = "全部";
    private int priceOrder = 0;

    HotFragment mFragment;
    @Override
    protected void init() {
        mSearch = getIntent().getStringExtra("search");
        edittxtPhone.setText(mSearch);
        mFragment = HotFragment.newInstance(HotFragment.SEARCH_RESULT,mSearch);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager. beginTransaction();
        transaction.replace(R.id.right_layout,mFragment);
        transaction.commit();
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_search_result;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.type1:
                mType = "全部";
                aroundSupplierListTvProduct.setText(mType);
                break;
            case R.id.type2:
                mType = getResources().getStringArray(R.array.home_bar_labels)[0];
                aroundSupplierListTvProduct.setText(mType);
                break;
            case R.id.type3:
                mType = getResources().getStringArray(R.array.home_bar_labels)[1];
                aroundSupplierListTvProduct.setText(mType);
                break;
            case R.id.type4:
                mType = getResources().getStringArray(R.array.home_bar_labels)[2];
                aroundSupplierListTvProduct.setText(mType);
                break;
            case R.id.type5:
                mType = getResources().getStringArray(R.array.home_bar_labels)[3];
                aroundSupplierListTvProduct.setText(mType);
                break;
            case R.id.type6:
                mType = getResources().getStringArray(R.array.home_bar_labels)[4];
                aroundSupplierListTvProduct.setText(mType);
                break;
            case R.id.type7:
                mType = getResources().getStringArray(R.array.home_bar_labels)[5];
                aroundSupplierListTvProduct.setText(mType);
                break;
            case R.id.type8:
                mType = getResources().getStringArray(R.array.home_bar_labels)[6];
                aroundSupplierListTvProduct.setText(mType);
                break;
            case R.id.type9:
                mType = getResources().getStringArray(R.array.home_bar_labels)[7];
                aroundSupplierListTvProduct.setText(mType);
                break;
            case R.id.type10:
                mType = getResources().getStringArray(R.array.home_bar_labels)[8];
                aroundSupplierListTvProduct.setText(mType);
                break;
            case R.id.shelltype1:
                mShellType = "全部";
                aroundSupplierListTvSort.setText(mShellType);
                break;
            case R.id.shelltype2:
                mShellType = "平台出售";
                aroundSupplierListTvSort.setText(mShellType);
                break;
            case R.id.shelltype3:
                mShellType = "个人出售";
                aroundSupplierListTvSort.setText(mShellType);
                break;
            case R.id.price1:
                priceOrder = 0;
                aroundSupplierListTvActivity.setText("综合排序");
                break;
            case R.id.price2:
                priceOrder = 1;
                aroundSupplierListTvActivity.setText("价格降序");
                break;
            case R.id.price3:
                priceOrder = 2;
                aroundSupplierListTvActivity.setText("价格升序");
                break;
            default:
                break;
        }
        mFragment.reData(mSearch,mType,mShellType,priceOrder);
        return false;
    }


    @OnClick({R.id.toolbar_leftButton, R.id.gosearch, R.id.around_supplier_list_product, R.id.around_supplier_list_sort, R.id.around_supplier_list_activity})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar_leftButton:
                finish();
                break;
            case R.id.gosearch:
                mSearch = edittxtPhone.getText().toString();
                mFragment.reData(mSearch,mType,mShellType,priceOrder);
                break;
            case R.id.around_supplier_list_product:
                PopupMenu popup = new PopupMenu(this, view);//第二个参数是绑定的那个view
                //获取菜单填充器
                MenuInflater inflater = popup.getMenuInflater();
                //填充菜单
                inflater.inflate(R.menu.menu1, popup.getMenu());
                //绑定菜单项的点击事件
                popup.setOnMenuItemClickListener(this);
                //显示(这一行代码不要忘记了)
                popup.show();
                break;
            case R.id.around_supplier_list_sort:
                popup = new PopupMenu(this, view);//第二个参数是绑定的那个view
                //获取菜单填充器
                inflater = popup.getMenuInflater();
                //填充菜单
                inflater.inflate(R.menu.menu2, popup.getMenu());
                //绑定菜单项的点击事件
                popup.setOnMenuItemClickListener(this);
                //显示(这一行代码不要忘记了)
                popup.show();
                break;
            case R.id.around_supplier_list_activity:
                popup = new PopupMenu(this, view);//第二个参数是绑定的那个view
                //获取菜单填充器
                inflater = popup.getMenuInflater();
                //填充菜单
                inflater.inflate(R.menu.menu3, popup.getMenu());
                //绑定菜单项的点击事件
                popup.setOnMenuItemClickListener(this);
                //显示(这一行代码不要忘记了)
                popup.show();
                break;
        }
    }
}
