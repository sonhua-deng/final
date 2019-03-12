package com.sevenshop.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sevenshop.R;
import com.sevenshop.bean.Category;

import java.util.List;

/**
 * <pre>
 *     desc   : 分类一级菜单.
 *     version: 1.0
 * </pre>
 */


public class CategoryAdapter extends BaseQuickAdapter<Category, BaseViewHolder> {

    public CategoryAdapter(List<Category> datas) {
        super(R.layout.template_single_text, datas);
    }

    @Override
    protected void convert(BaseViewHolder holder, Category item) {
        holder.setText(R.id.textView, item.getName());
    }
}
