package cc.easyandroid.easyrecyclerview.animation;

import androidx.core.view.ViewCompat;
import android.view.View;

public final class ViewHelper {

	public static void clear(View v) {
		ViewCompat.setAlpha(v, 1);
		ViewCompat.setScaleY(v, 1);
		ViewCompat.setScaleX(v, 1);
		ViewCompat.setTranslationY(v, 0);
		ViewCompat.setTranslationX(v, 0);
		ViewCompat.setRotation(v, 0);
		ViewCompat.setRotationY(v, 0);
		ViewCompat.setRotationX(v, 0);
		// @TODO https://code.google.com/p/android/issues/detail?id=80863
		//        ViewCompat.setPivotY(v, v.getMeasuredHeight() / 2);
		v.setPivotY(v.getMeasuredHeight() / 2);
		ViewCompat.setPivotX(v, v.getMeasuredWidth() / 2);
		ViewCompat.animate(v).setInterpolator(null);
	}
}
