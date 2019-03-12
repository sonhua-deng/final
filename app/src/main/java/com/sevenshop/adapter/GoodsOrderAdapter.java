package com.sevenshop.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sevenshop.SevenShopApplication;
import com.sevenshop.R;
import com.sevenshop.bean.ShoppingCart;
import com.sevenshop.utils.GlideUtils;

import java.util.List;

/**
 * <pre>

 *     desc   : 商品订单适配器
 *     version: 1.0
 * </pre>
 */


public class GoodsOrderAdapter extends BaseQuickAdapter<ShoppingCart, BaseViewHolder> {

    private List<ShoppingCart> mDatas;

    public GoodsOrderAdapter(List<ShoppingCart> datas) {
        super(R.layout.template_order_goods, datas);
        this.mDatas = datas;
    }

    @Override
    protected void convert(BaseViewHolder holder, ShoppingCart item) {
        GlideUtils.load(SevenShopApplication.sContext, item.getImgUrl(), (ImageView) holder
                .getView(R.id.iv_view));
    }


    public float getTotalPrice() {

        float sum = 0;
        if (!isNull())
            return sum;

        for (ShoppingCart cart : mDatas) {
            sum += cart.getCount() * cart.getPrice();
        }

        return sum;

    }


    private boolean isNull() {
        return (mDatas != null && mDatas.size() > 0);
    }
}
