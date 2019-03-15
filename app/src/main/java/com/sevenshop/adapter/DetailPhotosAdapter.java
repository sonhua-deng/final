package com.sevenshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sevenshop.R;
import com.sevenshop.bean.Goods;
import com.sevenshop.utils.CartShopProvider;
import com.sevenshop.utils.ListUtils;

import java.util.List;

/**
 * Describe: 热卖商品的适配器
 */

public class DetailPhotosAdapter extends RecyclerView.Adapter<DetailPhotosAdapter.ViewHolder> {

    private List<String> mDatas;
    private LayoutInflater          mInflater;
    CartShopProvider provider;
    private Context mContext;

    public DetailPhotosAdapter(List<String> datas, Context context) {
        this.mDatas = datas;
        this.mContext = context;
        provider = new CartShopProvider(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mInflater = LayoutInflater.from(parent.getContext());
        View view = mInflater.inflate(R.layout.detail_iv, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailPhotosAdapter.ViewHolder holder, int position) {
        final String data = getData(position);

        holder.itemView.setTag(position);
        if (data != null) {
            holder.ivView.setImageURI(data);
        }
    }


    private String getData(int position) {
        return mDatas.get(position);
    }

    public List<String> getDatas() {
        return mDatas;
    }

    public void clearData() {
        mDatas.clear();
        notifyItemRangeRemoved(0, mDatas.size());
    }

    public void addData(List<String> datas) {
        addData(0, datas);
    }

    public void addData(int position, List<String> datas) {
        if (datas != null && datas.size() > 0) {
            mDatas.addAll(datas);
            notifyItemRangeChanged(position, mDatas.size());
        }

    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView ivView;


        public ViewHolder(View itemView) {
            super(itemView);

            ivView = (SimpleDraweeView) itemView.findViewById(R.id.iv_view);
        }
    }
}
