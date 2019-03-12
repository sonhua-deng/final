package com.sevenshop.widget;

import android.app.Dialog;
import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.sevenshop.R;
import com.sevenshop.SevenShopApplication;
import com.sevenshop.activity.BaseActivity;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import cn.bmob.v3.util.AppUtils;


// 示例
// 1. Alter
//MDDialog.newBuilder()
//        .alter()  // Alter模式
//        .title(R.string.take_photo)  // title
//        .content(R.string.choose_country) //content
//        .cancelOutside(false) // 点击屏幕外消失
//        .backHide(true)   // 返回键消失
//        .hasNegative(true)    // 是否显示取消
//        .hasPositive(true)    // 是否显示确定
//        .positive(R.string.take_photo) // 确定的文字
//        .negative(R.string.cancel)   // 取消的文字
//        .listener(new MDDialog.OnDialogClickListener() { //设置监听
//            @Override
//            public void onPositiveClick(Dialog dialog) {
//            }
//
//            @Override
//            public void onNegativeClick(Dialog dialog) {
//            }
//
//            @Override
//            public void onDismiss(Dialog dialog) {
//
//            }
//        })
//        .build()
//        .showWithActivity(HomeActivity.this);
// 2. Circle
//MDDialog.newBuilder()
//        .circle()  // circle模式
//        .cancelOutside(false)
//        .cancelOutside(false)
//        .listener(...)
//        .build()
//        .showWithActivity(HomeActivity.this);
// 3. List
//MDDialog.newBuilder()
//        .list() // 列表模式
//        .title(R.string.take_photo)
//        .items("abc", "def")
//        .listListener(new MDDialog.OnDialogListClickListener() {
//            @Override
//            public void onItemClick(Dialog dialog, View view, int position) {
//
//            }
//        })
//        .build()
//        .showWithActivity(HomeActivity.this);

public class MDDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "MDDialog";

    private static final int STAT_ALTER = 0;
    private static final int STAT_PROGRESS_CIRCLE = 1;
    private static final int STAT_PROGRESS_LINEAR = 2;
    private static final int STAT_LIST = 3;
    private static final int STAT_INPUT = 4;
    private static final int STAT_CUSTOM = 5;

    private static final String KEY_STAT = "key_stat";
    private static final String KEY_TITLE = "key_title";
    private static final String KEY_NEG = "key_neg";
    private static final String KEY_POS = "key_pos";
    private static final String KEY_CONTENT = "key_content";
    private static final String KEY_TOKEN = "key_token";
    private static final String KEY_POS_TXT = "key_pos_txt";
    private static final String KEY_NEG_TXT = "key_neg_txt";
    private static final String KEY_BACK_HIDE = "key_back_hide";
    private static final String KEY_ITEMS = "key_items";
    private static final String KEY_CONTENT_STR = "key_content_str";
    private static final String KEY_INPUT_TYPE = "key_input_type";
    private static final String KEY_MIN_TXT = "key_min_txt";
    private static final String KEY_MAX_TXT = "key_max_txt";
    private static final String KEY_TXT_HINT = "key_txt_hint";
    private static final String KEY_CUSTOM = "key_custom";
    private static final String KEY_CUSTOM_STYLE = "key_custom_style";
    private static final String KEY_INPUT_HINT = "key_input_hint";
    private static final String KEY_INPUT_PRE_FILL = "key_input_pre_fill";
    private static final String KEY_INPUT_ALLOW_EMPTY = "key_input_allow_empty";
    private static final String KEY_TITLE_STR = "key_title_str";
    private static final String KEY_FULL_SCREEN = "key_full_screen";

    private static final String TAG_ALTER_DIALOG = "alter_dialog";

    private static SparseArray<OnDialogClickListener> sListenerMap = new SparseArray<>();
    private static SparseArray<OnDialogListClickListener> sListListenerMap = new SparseArray<>();
    private static SparseArray<OnDialogLifeListener> sLifeListenerMap = new SparseArray<>();
    private static SparseArray<OnDialogInputListener> sInputListenerMap = new SparseArray<>();
    private static AtomicInteger sTokenIndex = new AtomicInteger();

    private EditText mInputEditView;

    private int mStat;
    private int mTitle;
    private int mContent;
    private boolean mHasNegative;
    private boolean mHasPositive;
    private String mNegativeTxt;
    private String mPositiveTxt;
    private int mToken;
    private boolean mBackHide;
    private CharSequence[] mItems;
    private CharSequence mContentStr;
    private int mInputType;
    private int mMinText;
    private int mMaxText;
    private int mTextHintColor;
    private int mCustomLayout;
    private int mCustomStyle;
    private CharSequence mInputHint;
    private CharSequence mInputPreFill;
    private boolean mInputAllowEmpty;
    private boolean mIsShowing;
    private String mDialogTag;
    private CharSequence mTitleStr;
    private boolean mFullScreen; // 是否全屏 layout 并且隐藏 statusBar

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MD_Dialog_Light);

        Bundle bundle = getArguments();
        if (bundle == null) {
            throw new IllegalStateException("Should use Builder");
        }
        mStat = bundle.getInt(KEY_STAT);
        mHasNegative = bundle.getBoolean(KEY_NEG);
        mHasPositive = bundle.getBoolean(KEY_POS);
        mPositiveTxt = bundle.getString(KEY_POS_TXT);
        mNegativeTxt = bundle.getString(KEY_NEG_TXT);
        mTitle = bundle.getInt(KEY_TITLE);
        mContent = bundle.getInt(KEY_CONTENT);
        mToken = bundle.getInt(KEY_TOKEN);
        mBackHide = bundle.getBoolean(KEY_BACK_HIDE);
        mItems = bundle.getCharSequenceArray(KEY_ITEMS);
        mContentStr = bundle.getCharSequence(KEY_CONTENT_STR);
        mInputType = bundle.getInt(KEY_INPUT_TYPE);
        mMinText = bundle.getInt(KEY_MIN_TXT);
        mMaxText = bundle.getInt(KEY_MAX_TXT);
        mTextHintColor = bundle.getInt(KEY_TXT_HINT);
        mCustomLayout = bundle.getInt(KEY_CUSTOM);
        mCustomStyle = bundle.getInt(KEY_CUSTOM_STYLE);
        mFullScreen = bundle.getBoolean(KEY_FULL_SCREEN);
        if (0 != mCustomStyle) {
            setStyle(DialogFragment.STYLE_NO_TITLE, mCustomStyle);
        }
        mInputHint = bundle.getCharSequence(KEY_INPUT_HINT);
        mInputPreFill = bundle.getCharSequence(KEY_INPUT_PRE_FILL);
        mInputAllowEmpty = bundle.getBoolean(KEY_INPUT_ALLOW_EMPTY);
        mTitleStr = bundle.getCharSequence(KEY_TITLE_STR);
        if (savedInstanceState != null) {
            dismissAllowingStateLoss();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        setupBackPress(dialog);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View root;
        switch (mStat) {
            case STAT_PROGRESS_CIRCLE:
                root = setupWithCircle(inflater, container);
                break;
            case STAT_LIST:
                root = setupWithList(inflater, container);
                break;
            case STAT_INPUT:
                root = setupWithInput(inflater, container);
                break;
            case STAT_ALTER:
                root = setupWithAlter(inflater, container);
                break;
            case STAT_CUSTOM:
            default:
                root = setupWithCustom(inflater, container);
                break;
        }
        OnDialogLifeListener listener = sLifeListenerMap.get(mToken);
        if (listener != null) {
            listener.onCreate(this, root);
        }
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!mFullScreen) {
            return;
        }

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(width, height);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    window.getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    );
                } else {
                    // below api16
//                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                }
            }
        }
    }

    private View setupWithCircle(
            LayoutInflater inflater,
            ViewGroup container) {
        View root = inflater.inflate(R.layout.md_stub_progress_indeterminate, container, false);
        setupContent(root);
        return root;
    }

    private View setupWithInput(
            LayoutInflater inflater,
            ViewGroup container) {
        View root = inflater.inflate(R.layout.md_dialog_input, container, false);
        setupTile(root);
        setupContent(root);
        setupInput(root);
        setupBtns(root);
        return root;
    }

    private View setupWithList(
            LayoutInflater inflater,
            ViewGroup container) {
        View root = inflater.inflate(R.layout.md_dialog_list, container, false);
        setupTile(root);
        setupList(root);
        return root;
    }

    private View setupWithAlter(
            LayoutInflater inflater,
            ViewGroup container) {
        View root = inflater.inflate(R.layout.md_dialog_alter, container, false);
        setupTile(root);
        setupContent(root);
        setupBtns(root);
        return root;
    }

    private View setupWithCustom(
            LayoutInflater inflater,
            ViewGroup container) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.md_dialog_custom, container, false);
        if (mCustomLayout != 0) {
            try {
                ViewGroup customRoot = root.findViewById(R.id.customViewFrame);
                inflater.inflate(mCustomLayout, customRoot, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return root;
    }

    private void setupTile(View root) {
        View titleLayout = root.findViewById(R.id.titleFrame);
        TextView title = root.findViewById(R.id.title);
        if (mTitle != 0) {
            title.setText(mTitle);
        } else if (!TextUtils.isEmpty(mTitleStr)) {
            title.setText(mTitleStr);
        } else {
            if (titleLayout != null) {
                titleLayout.setVisibility(View.GONE);
            } else {
                title.setVisibility(View.GONE);
            }
        }
    }

    private void setupContent(View root) {
        TextView content = root.findViewById(R.id.content);
        if (mContent != 0) {
            content.setText(mContent);
        } else if (!TextUtils.isEmpty(mContentStr)) {
            content.setText(mContentStr);
        } else {
            content.setVisibility(View.GONE);
        }
    }

    private void setupBtns(View root) {
        TextView positive = root.findViewById(R.id.buttonDefaultPositive);
        TextView negative = root.findViewById(R.id.buttonDefaultNegative);
        if (!mHasNegative) {
            negative.setVisibility(View.GONE);
        } else {
            negative.setText(mNegativeTxt);
        }
        if (!mHasPositive) {
            positive.setVisibility(View.GONE);
        } else {
            positive.setText(mPositiveTxt);
        }
        negative.setOnClickListener(this);
        positive.setOnClickListener(this);
    }

    private void setupList(View root) {
        ListView listView = root.findViewById(R.id.contentListView);
        if (mItems != null && mItems.length > 0) {
            listView.setAdapter(new ArrayAdapter<>(
                    getActivity(), R.layout.md_listitem, R.id.title, mItems));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    notifyItemClick(view, position, mItems[position]);
                }
            });
        }
    }

    private void setupInput(View root) {
        final EditText input = root.findViewById(R.id.input);
        mInputEditView = input;
        final TextView positive = root.findViewById(R.id.buttonDefaultPositive);
        final TextView inputMinMax = root.findViewById(R.id.minMax);
        MDTintHelper.setTint(input, getResources().getColor(R.color.colorAccent));
        if (mInputPreFill != null) {
            input.setText(mInputPreFill);
        }
        input.setHint(mInputHint);
        input.setSingleLine();
        if (mInputType != -1) {
            input.setInputType(mInputType);
            if ((mInputType & InputType.TYPE_TEXT_VARIATION_PASSWORD) == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                // If the flags contain TYPE_TEXT_VARIATION_PASSWORD, apply the password transformation method automatically
                input.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }

        if (mMinText > 0 || mMaxText > -1) {
            invalidateInputMinMaxIndicator(input, positive, inputMinMax, input.getText().toString().length(),
                    !mInputAllowEmpty);
        } else {
            inputMinMax.setVisibility(View.GONE);
        }

        //把默认超出字符处理改为：如果设置了最大字符长度，则达到最大长度后，不给再输入
        if (mMaxText > 0) {
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mMaxText)});
        }


        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final int length = s.toString().trim().length();
                boolean emptyDisabled = !mInputAllowEmpty;
                if (mInputAllowEmpty) {
//                    emptyDisabled = length == 0;
                    if (length == 0) {
                        positive.setEnabled(!emptyDisabled);
                    }
                }
                invalidateInputMinMaxIndicator(input, positive, inputMinMax, length, emptyDisabled);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if ( s.length() != s.toString().trim().length()) {
                    String term = s.toString().trim();
                    s.clear();
                    s.append(term);
                }
            }
        });
        input.post(new Runnable() {
            @Override
            public void run() {
                input.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(input, 0);
                }
            }
        });
        if (input.getText().length() > 0) {
            input.setSelection(input.getText().length());
        }
    }

    protected void invalidateInputMinMaxIndicator(
            EditText input,
            TextView positive,
            TextView inputMinMax,
            int currentLength,
            boolean emptyDisabled) {
        if (inputMinMax != null) {
            if (mMaxText > 0) {
                inputMinMax.setText(String.format(
                        Locale.getDefault(), "%d/%d", currentLength, mMaxText));
                inputMinMax.setVisibility(View.VISIBLE);
            } else {
                inputMinMax.setVisibility(View.GONE);
            }
            final boolean isDisabled = (emptyDisabled && currentLength == 0) ||
                    (mMaxText > 0 && currentLength > mMaxText) ||
                    currentLength < mMinText;
            final boolean isAlter = isDisabled && (currentLength != 0);
            int originColor = getResources().getColor(R.color.colorAccent);
            final int colorText = isAlter ? mTextHintColor : originColor;
            final int colorWidget = isAlter ? mTextHintColor : originColor;
            if (mMaxText > 0) {
                inputMinMax.setTextColor(colorText);
            }
            MDTintHelper.setTint(input, colorWidget);
            positive.setEnabled(!isDisabled);
        }
    }

    /**
     * @param activity 展示的activity
     */
    public void showWithActivity(FragmentActivity activity) {
        log("showWithActivity");
        String dialogTag = getDialogTag();
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Fragment prev = manager.findFragmentByTag(dialogTag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.setCustomAnimations(FragmentTransaction.TRANSIT_FRAGMENT_FADE,
                FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        show(ft, dialogTag);
        executePendingTransactions();
        attachLife(activity);
        mIsShowing = true;
    }

    public String getDialogTag() {
        if (mDialogTag == null) {
            mDialogTag = TAG_ALTER_DIALOG;
        }
        return mDialogTag;
    }

    private void attachLife(LifecycleOwner activity) {
        activity.getLifecycle().addObserver(new GenericLifecycleObserver() {
            @Override
            public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    log("life destroy");
                    mIsShowing = false;
                    source.getLifecycle().removeObserver(this);
                    MDDialog.this.dismissAllowingStateLoss();
                }
            }
        });
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            dismissAllowingStateLoss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonDefaultPositive:
                log("before pos click");
                notifyInputIfNeed();
                notifyPositive();
                log("after pos click");
                break;

            case R.id.buttonDefaultNegative:
                log("before neg click");
                notifyNegative();
                log("after neg click");
                break;
        }
    }

    private void notifyInputIfNeed() {
        if (mInputEditView != null) {
            OnDialogInputListener listener = sInputListenerMap.get(mToken);
            if (listener != null) {
                listener.onInput(MDDialog.this, mInputEditView.getText());
            }
        }
    }

    private void notifyPositive() {
        OnDialogClickListener listener = sListenerMap.get(mToken);
        if (listener != null) {
            listener.onPositiveClick(this);
        }
    }

    private void notifyNegative() {
        OnDialogClickListener listener = sListenerMap.get(mToken);
        if (listener != null) {
            listener.onNegativeClick(this);
        }
    }

    private void notifyItemClick(View view, int position, CharSequence charSequence) {
        OnDialogListClickListener listener = sListListenerMap.get(mToken);
        if (listener != null) {
            listener.onItemClick(this, view, position, charSequence);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        executePendingTransactions();

        mIsShowing = false;
        log("onDismiss");
    }

    private void executePendingTransactions() {
        FragmentManager manager = getFragmentManager();
        if (manager != null) {
            try {
                manager.executePendingTransactions();
            } catch (Exception e) {
                e.printStackTrace();
                log("executePendingTransactions error");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OnDialogLifeListener listener = sLifeListenerMap.get(mToken);
        if (listener != null) {
            listener.onDismiss(this);
        }
        cleanUpListeners();
        mIsShowing = false;
        log("onDestroy");
    }

    private void cleanUpListeners() {
        sListenerMap.remove(mToken);
        sListListenerMap.remove(mToken);
        sLifeListenerMap.remove(mToken);
        sInputListenerMap.remove(mToken);
    }

    @Override
    public void dismiss() {
        dismissAllowingStateLoss();
    }

    @Override
    public void dismissAllowingStateLoss() {
        if (isAdded()
                && !isDetached()
                && !isHidden()) {
            try {
                super.dismissAllowingStateLoss();
                executePendingTransactions();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void setupBackPress(Dialog dialog) {
        if (!mBackHide) {
            return;
        }
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mBackHide) {
                        MDDialog.this.dismissAllowingStateLoss();
                        log("onBackDown");
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public boolean isShowing() {
        return mIsShowing;
    }

    public EditText getInputEditText() {
        return mInputEditView;
    }

    public static class Builder {
        private int mStat;
        private int mTitle;
        private String mPositive;
        private String mNegative;
        private int mContent;
        private CharSequence mContentStr;
        private boolean mHasNegativeTxt = true;
        private boolean mHasPositiveTxt = true;
        private boolean mCancelOutSide;
        private boolean mBackHide = true;
        private final int mToken;
        private CharSequence[] mItems;
        private int mInputType;
        private int mMinText;
        private int mMaxText;
        private int mTextHintColor;
        private int mCustomLayout;
        private int mCustomStyle;
        private CharSequence mInputHint;
        private CharSequence mInputPreFill;
        private boolean mInputAllowEmpty;
        private CharSequence mTitleStr;
        private boolean mFullScreen;

        private Builder() {
            mToken = sTokenIndex.getAndIncrement();
        }

        public Builder alter() {
            mStat = STAT_ALTER;
            return this;
        }

        public Builder circle() {
            mStat = STAT_PROGRESS_CIRCLE;
            return this;
        }

        public Builder list() {
            mStat = STAT_LIST;
            return this;
        }

        public Builder input() {
            mStat = STAT_INPUT;
            return this;
        }

        public Builder custom() {
            mStat = STAT_CUSTOM;
            return this;
        }

        public Builder title(int src) {
            mTitle = src;
            return this;
        }

        public Builder title(CharSequence title) {
            mTitleStr = title;
            return this;
        }

        public Builder content(int content) {
            mContent = content;
            return this;
        }

        public Builder content(String content) {
            mContentStr = content;
            return this;
        }

//        public Builder positive(int src) {
//            mPositive = src <= 0 ? "" : ResourceUtils.getString(src);
//            return this;
//        }

        public Builder positive(String src) {
            mPositive = src;
            return this;
        }

//        public Builder negative(int src) {
//            mNegative = src <= 0 ? "" : ResourceUtils.getString(src);
//            return this;
//        }

        public Builder negative(String src) {
            mNegative = src;
            return this;
        }

        public Builder hasNegative(boolean enable) {
            mHasNegativeTxt = enable;
            return this;
        }

        public Builder hasPositive(boolean enable) {
            mHasPositiveTxt = enable;
            return this;
        }

        public Builder listener(OnDialogClickListener listener) {
            sListenerMap.put(mToken, listener);
            return this;
        }

        public Builder listListener(OnDialogListClickListener listener) {
            sListListenerMap.put(mToken, listener);
            return this;
        }

        public Builder inputListener(OnDialogInputListener listener) {
            sInputListenerMap.put(mToken, listener);
            return this;
        }

        public Builder cancelOutside(boolean enable) {
            mCancelOutSide = enable;
            return this;
        }

        public Builder backHide(boolean backHide) {
            mBackHide = backHide;
            return this;
        }

        public Builder items(CharSequence... items) {
            mItems = items;
            mStat = STAT_LIST;
            return this;
        }

        public Builder items(int items) {
            mItems = SevenShopApplication.getApplication().getResources().getStringArray(items);
            mStat = STAT_LIST;
            return this;
        }

        public Builder inputType(int type) {
            mInputType = type;
            mStat = STAT_INPUT;
            return this;
        }

        public Builder inputRangeRes(int min, int max, int color) {
            mMinText = min;
            mMaxText = max;
            mTextHintColor = color;
            mStat = STAT_INPUT;
            return this;
        }

        public Builder fullScreen(boolean fullScreen) {
            this.mFullScreen = fullScreen;
            return this;
        }

        public Builder preInsert(
                @Nullable CharSequence hint,
                @Nullable CharSequence prefill,
                boolean allowEmptyInput,
                @NonNull OnDialogInputListener callback) {
            this.mInputHint = hint;
            this.mInputPreFill = prefill;
            this.mInputAllowEmpty = allowEmptyInput;
            sInputListenerMap.put(mToken, callback);
            mStat = STAT_INPUT;
            return this;
        }

        public Builder customView(@LayoutRes int layout) {
            mCustomLayout = layout;
            mStat = STAT_CUSTOM;
            return this;
        }

        public Builder style(@StyleRes int style) {
            mCustomStyle = style;
            return this;
        }

        public Builder lifeListener(OnDialogLifeListener lifeListener) {
            sLifeListenerMap.put(mToken, lifeListener);
            return this;
        }

        public MDDialog build() {
            Bundle bundle = new Bundle();
            bundle.putInt(KEY_STAT, mStat);
            bundle.putBoolean(KEY_NEG, mHasNegativeTxt);
            bundle.putBoolean(KEY_POS, mHasPositiveTxt);
            bundle.putString(KEY_POS_TXT, mPositive);
            bundle.putString(KEY_NEG_TXT, mNegative);
            bundle.putInt(KEY_TITLE, mTitle);
            bundle.putInt(KEY_CONTENT, mContent);
            bundle.putInt(KEY_TOKEN, mToken);
            bundle.putBoolean(KEY_BACK_HIDE, mBackHide);
            bundle.putCharSequenceArray(KEY_ITEMS, mItems);
            bundle.putCharSequence(KEY_CONTENT_STR, mContentStr);
            bundle.putInt(KEY_INPUT_TYPE, mInputType);
            bundle.putInt(KEY_MIN_TXT, mMinText);
            bundle.putInt(KEY_MAX_TXT, mMaxText);
            bundle.putInt(KEY_TXT_HINT, mTextHintColor);
            bundle.putInt(KEY_CUSTOM, mCustomLayout);
            bundle.putInt(KEY_CUSTOM_STYLE, mCustomStyle);
            bundle.putCharSequence(KEY_INPUT_HINT, mInputHint);
            bundle.putCharSequence(KEY_INPUT_PRE_FILL, mInputPreFill);
            bundle.putBoolean(KEY_INPUT_ALLOW_EMPTY, mInputAllowEmpty);
            bundle.putCharSequence(KEY_TITLE_STR, mTitleStr);
            bundle.putBoolean(KEY_FULL_SCREEN, mFullScreen);
            MDDialog dialog = new MDDialog();
            dialog.setArguments(bundle);
            dialog.setCancelable(mCancelOutSide);
            return dialog;
        }
    }

    public interface OnDialogClickListener {
        void onPositiveClick(MDDialog dialog);
        void onNegativeClick(MDDialog dialog);
    }

    public interface OnDialogLifeListener {
        void onCreate(MDDialog dialog, View view);
        void onDismiss(MDDialog dialog);
    }

    public interface OnDialogListClickListener {
        void onItemClick(MDDialog dialog, View view, int position, CharSequence charSequence);
    }

    public interface OnDialogInputListener {
        void onInput(@NonNull MDDialog dialog, CharSequence input);
    }

    private static void log(String format, Object... args) {
        Log.i(TAG, String.format(Locale.US, format, args));
//        Log.i(TAG, "->map:" + sListenerMap.size() + "<-");
//        Log.i(TAG, "->list-map:" + sListListenerMap.size() + "<-");
//        Log.i(TAG, "->life-map:" + sLifeListenerMap.size() + "<-");
//        Log.i(TAG, "->input-map:" + sInputListenerMap.size() + "<-");
    }

}

