package com.sevenshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sevenshop.SevenShopApplication;
import com.sevenshop.R;
import com.sevenshop.bean.Goods;
import com.sevenshop.bean.HotGoods;
import com.sevenshop.utils.CartShopProvider;
import com.sevenshop.utils.GlideUtils;
import com.sevenshop.utils.ListUtils;
import com.sevenshop.utils.ToastUtils;

import java.util.List;

/**
 * Describe: 热卖商品的适配器
 */

public class HotGoodsAdapter extends RecyclerView.Adapter<HotGoodsAdapter.ViewHolder> implements
        View.OnClickListener {

    private List<Goods> mDatas;
    private LayoutInflater          mInflater;
    CartShopProvider provider;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener = null;

    public HotGoodsAdapter(List<Goods> datas, Context context) {
        this.mDatas = datas;
        this.mContext = context;
        provider = new CartShopProvider(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mInflater = LayoutInflater.from(parent.getContext());
        View view = mInflater.inflate(R.layout.template_cart, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HotGoodsAdapter.ViewHolder holder, int position) {
        final Goods data = getData(position);
        holder.textTitle.setText(data.getTitle());
        holder.textPrice.setText("￥" + data.getCurrenPrice());
        holder.itemView.setTag(position);
        if (!ListUtils.isEmpty(data.getPhotoUrls()) && data.getPhotoUrls().get(0)!=null) {
            holder.ivView.setImageURI(data.getPhotoUrls().get(0));
        }

        if (data.getPulisher().getNickName() != null && !data.getPulisher().getNickName().isEmpty()) {
            holder.textUser.setText(data.getPulisher().getNickName());
        }
        if (data.getPulisher().getLogo_url()!=null && !data.getPulisher().getLogo_url().isEmpty()) {
            holder.ivAvatar.setImageURI(data.getPulisher().getLogo_url());
        }
    }


    private Goods getData(int position) {
        return mDatas.get(position);
    }

    public List<Goods> getDatas() {
        return mDatas;
    }

    public void clearData() {
        mDatas.clear();
        notifyItemRangeRemoved(0, mDatas.size());
    }

    public void addData(List<Goods> datas) {
        addData(0, datas);
    }

    public void addSearchData(List<Goods> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void addData(int position, List<Goods> datas) {
        if (datas != null && datas.size() > 0) {
            mDatas.addAll(datas);
            notifyItemRangeChanged(position, mDatas.size());
        }

    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView ivView;
        SimpleDraweeView ivAvatar;
        TextView  textTitle;
        TextView  textPrice;
        TextView  textUser;


        public ViewHolder(View itemView) {
            super(itemView);

            ivView = (SimpleDraweeView) itemView.findViewById(R.id.iv_view);
            textTitle = (TextView) itemView.findViewById(R.id.text_title);
            textPrice = (TextView) itemView.findViewById(R.id.tv_price);
            ivAvatar = (SimpleDraweeView) itemView.findViewById(R.id.avatar);
            textUser = (TextView) itemView.findViewById(R.id.tv_user);
        }
    }


    /**
     * item的点击事件
     */
    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * 暴露给外面,以便于调用
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


}
