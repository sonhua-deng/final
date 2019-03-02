package com.enjoyshop.activity;

import android.text.TextUtils;
import android.widget.TextView;

import com.enjoyshop.R;

import butterknife.BindView;

/**
 * <pre>
 *   desc   : 搜索结果
 * </pre>
 */

public class SearchResultActivity extends BaseActivity {

    @BindView(R.id.tv_result)
    TextView mTextView;

    @Override
    protected void init() {

        String search = getIntent().getStringExtra("search");
        if (!TextUtils.isEmpty(search)) {
            mTextView.setText(search);
        }

    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_search_result;
    }
}
