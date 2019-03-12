package com.sevenshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sevenshop.R;
import com.sevenshop.data.HomeGridInfo;

import java.util.List;

public class GridAdapter extends BaseAdapter {

    private Context mContext;
    private List<HomeGridInfo> mData;

    public GridAdapter(Context context, List<HomeGridInfo> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.item_gridview, null);
        ImageView gridIcon = (ImageView) mView.findViewById(R.id.grid_icon);
        TextView gridTitle = (TextView) mView.findViewById(R.id.grid_title);

        gridIcon.setImageResource(mData.get(i).getGridIcon());
        gridTitle.setText(mData.get(i).getGridTitle());
        return mView;
    }
}
