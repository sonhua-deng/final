package com.sevenshop.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class OsUtil {
	private static float sDensityFactor = -1.0f;

	public static float densityFactor(Context context) {
		if (sDensityFactor < 0) {
			WindowManager wMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics metrics = new DisplayMetrics();
			wMgr.getDefaultDisplay().getMetrics(metrics);
			sDensityFactor = metrics.density;
		}
		return sDensityFactor;
	}

	public static int dpToPx(int dp) {
		return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
	}

	public static int dpToPx(double dp){
		return  (int)(dp * Resources.getSystem().getDisplayMetrics().density);
	}

	public static float dipToPixels(float dipValue) {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
	}


	public static float spToPt(int sp) {
		return sp * Resources.getSystem().getDisplayMetrics().density;
	}

	public static int getScreenWidth(Context context) {
		WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = mgr.getDefaultDisplay();
		return display.getWidth();
	}

	public static int getScreenHeight(Context context) {
		WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = mgr.getDefaultDisplay();
		return display.getHeight();
	}

	/**
	 * 获取导航栏高度
	 * @param context
	 * @return
	 */
	public static int getNavigationHeight(Context context){
		return getScreenHeightCompat(context) - getScreenHeight(context);
	}

	public static int getScreenHeightCompat(Context context) {
		WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		final Display display = mgr.getDefaultDisplay();
		final Point screenResolution = new Point();
		display.getSize(screenResolution);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			final Point screenRealResolution = new Point();
			display.getRealSize(screenRealResolution);
//			screenResolution.x = Math.max(screenRealResolution.x, screenResolution.x);
			screenResolution.y = Math.max(screenRealResolution.y, screenResolution.y);
		}
		return screenResolution.y;
/*		if (screenResolution.x < screenResolution.y*//*screen portrait*//*) {
			return screenResolution.y;
		}
		return screenResolution.x;*/
	}

	public static String getPackageName(Context context) {
		return context.getPackageName();
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static boolean copyToClipBoard(Context context, String text) {
		try {
			int apiLevel = Build.VERSION.SDK_INT;
			Object service = context.getSystemService(Context.CLIPBOARD_SERVICE);
			if (apiLevel < 11) {
				android.text.ClipboardManager cb = (android.text.ClipboardManager) service;
				cb.setText(text);
			} else {
				android.content.ClipboardManager cb = (android.content.ClipboardManager) service;
				cb.setText(text);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static int getStatusBarHeight(Activity activity) {
		if (activity == null) {
			return 0;
		}
		Window window = activity.getWindow();
		if (window == null) {
			return 0;
		}
		View decorView = window.getDecorView();
		if (decorView == null) {
			return 0;
		}
		int statusHeight = 0;
		Rect localRect = new Rect();
		decorView.getWindowVisibleDisplayFrame(localRect);
		statusHeight = localRect.top;
		if (0 == statusHeight) {
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
				statusHeight = activity.getResources().getDimensionPixelSize(i5);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return statusHeight;
	}







	/**
	 * 手机设备 当设备锁屏时，输入框的键盘是否会放下
	 */
	private static boolean sKeyBoardCloseWhenScreenOffCheck = false;
	private static boolean sSupportKeyBoardCloseWhenScreenOff = true;

	public static boolean isSupportKeyBoardCloseWhenScreenOff() {
		if (sKeyBoardCloseWhenScreenOffCheck) {
			return sSupportKeyBoardCloseWhenScreenOff;
		}
		if ("Nexus 6P".equalsIgnoreCase(Build.MODEL)) {
			sSupportKeyBoardCloseWhenScreenOff = false;
		}
		sKeyBoardCloseWhenScreenOffCheck = true;
		return sSupportKeyBoardCloseWhenScreenOff;
	}

}
