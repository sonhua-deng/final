package com.sevenshop.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.AttrRes;

/**
 * @author wangfang on 2018/10/25.
 */
class MaterialDialogUtils {

    public static int resolveColor(Context context, @AttrRes int attr) {
        return resolveColor(context, attr, 0);
    }

    public static int resolveColor(Context context, @AttrRes int attr, int fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getColor(0, fallback);
        } catch (Exception e) {
            e.printStackTrace();
            return Color.WHITE;
        } finally {
            a.recycle();
        }
    }
}
