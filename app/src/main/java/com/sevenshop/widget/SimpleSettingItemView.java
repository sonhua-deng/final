package com.sevenshop.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import com.sevenshop.R;
import com.sevenshop.utils.OsUtil;

/**
 * Created by KEVIN on 2015/1/31.
 */
public class SimpleSettingItemView extends FrameLayout implements View.OnFocusChangeListener, View.OnClickListener {
    private TextView mLeftText, mRightText;
    private EditText mRightEditor;
    private ImageView mEdittextDeleteBtn;
    private Space mSpace;
    private View mParent;
    private boolean mEditable;
    private int mLeftTextColor;
    private int mRightTextColor;
    private int mTextLenghtLimit = -1;


    public SimpleSettingItemView(Context context) {
        super(context);
        initView(context, false);
    }

    public SimpleSettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleSettingItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SimpleSettingItem, defStyle, 0);
        String leftText = a.getString(R.styleable.SimpleSettingItem_leftText);
        String rightText = a.getString(R.styleable.SimpleSettingItem_rightText);
		String rightHint = a.getString(R.styleable.SimpleSettingItem_rightHint);
        mEditable = a.getBoolean(R.styleable.SimpleSettingItem_editable, false);
        mLeftTextColor = a.getColor(R.styleable.SimpleSettingItem_leftTextColor, 0);
        mRightTextColor = a.getColor(R.styleable.SimpleSettingItem_rightTextColor, 0);

        initView(context, mEditable);

        if (!TextUtils.isEmpty(leftText)) {
            mLeftText.setText(leftText);
        }
        if (!TextUtils.isEmpty(rightText)) {
            if(mEditable) {
                mRightEditor.setText(rightText);
            } else {
                mRightText.setText(rightText);
            }
        }
		if(!TextUtils.isEmpty(rightHint)){
            if(mEditable) {
                mRightEditor.setHint(rightHint);
            } else {
                mRightText.setHint(rightHint);
            }
		}
        a.recycle();
    }

    private void initView(Context context, boolean editable) {
        View rootView = null;
        if (editable) {
            rootView = LayoutInflater.from(context).inflate(R.layout.item_simple_setting_item_with_edittext, this, false);
        } else {
            rootView = LayoutInflater.from(context).inflate(R.layout.item_simple_setting_item_with_textview, this, false);
        }
        mLeftText = (TextView)rootView.findViewById(R.id.tx_setting_item_left);
        if(mLeftTextColor != 0) {
            mLeftText.setTextColor(mLeftTextColor);
        }
        if (editable) {
            mRightEditor = (EditText)rootView.findViewById(R.id.tx_setting_item_right);
            mEdittextDeleteBtn = (ImageView)rootView.findViewById(R.id.iv_setting_item_text_delete);
            addTextLenghtLimint(mRightEditor);
            if(mRightTextColor != 0) {
                mRightEditor.setTextColor(mRightTextColor);
            }
            mRightEditor.setOnFocusChangeListener(this);
            mEdittextDeleteBtn.setOnClickListener(this);
        } else {
            mRightText = (TextView)rootView.findViewById(R.id.tx_setting_item_right);
            mSpace = rootView.findViewById(R.id.space);
            addTextLenghtLimint(mRightText);
            if(mRightTextColor != 0) {
                mRightText.setTextColor(mRightTextColor);
            }
        }
        //mRedPoint = rootView.findViewById(R.id.red_point);
        mParent = rootView;
        addView(rootView);
    }

    private void addTextLenghtLimint(final TextView textView) {
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mTextLenghtLimit != -1) {
                    String str = s.toString();
                    int len = str.length();
                    if (len > mTextLenghtLimit) {
                        textView.setText(str.substring(0, mTextLenghtLimit));
                        if (textView instanceof EditText) {
                            EditText editText = (EditText) textView;
                            editText.setSelection(mTextLenghtLimit);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mEditable && mRightEditor != null && mRightEditor.hasFocus()) {
                    if(!TextUtils.isEmpty(mRightEditor.getText().toString())) {
                        mEdittextDeleteBtn.setVisibility(VISIBLE);
                    } else {
                        mEdittextDeleteBtn.setVisibility(GONE);
                    }
                }
            }
        });
    }

    public void setTextLenghtLimit(int limit) {
        mTextLenghtLimit = limit;
    }

    public boolean isEditable() {
        return mEditable;
    }

    public TextView getLeftTextView() {
        return mLeftText;
    }

    public TextView getRightTextView() {
        return mRightText;
    }

    public EditText getRightEditText() {
        return mRightEditor;
    }

    public void requestEditTextFoucusAndMoveCursorEnd() {
        if (mRightEditor == null) {
            return;
        }

        mRightEditor.requestFocus();
        mRightEditor.setSelection(mRightEditor.getText().length());
    }

    public void makeLeftGravityHideArraw() {
        TextView tv = getRightTextView();
        tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL | Gravity.START);
        tv.setCompoundDrawables(null, null, null, null);
    }

    public void makeLeftGravity() {
        mParent.setPadding(OsUtil.dpToPx(10), OsUtil.dpToPx(15), OsUtil.dpToPx(10), OsUtil.dpToPx(15));
        if (mRightText != null) {
            mRightText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL | Gravity.START);
        }
        if (mSpace != null) {
            mSpace.setVisibility(View.GONE);
        }
    }

    public void hideArrow() {
        getRightTextView().setCompoundDrawables(null, null, null, null);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(mEdittextDeleteBtn != null && mRightEditor != null) {
            if (hasFocus && !TextUtils.isEmpty(mRightEditor.getText().toString())) {
                mEdittextDeleteBtn.setVisibility(VISIBLE);
            } else {
                mEdittextDeleteBtn.setVisibility(GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_setting_item_text_delete) {
            if(mEdittextDeleteBtn != null && mRightEditor != null) {
                mRightEditor.setText("");
            }
        }
    }

}
