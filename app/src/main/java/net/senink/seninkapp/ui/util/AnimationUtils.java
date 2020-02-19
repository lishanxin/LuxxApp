package net.senink.seninkapp.ui.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import net.senink.seninkapp.R;

/**
 * 动画的工具类
 * 
 * @author zhaojunfeng
 * @date 2015-11-03
 * 
 */
public class AnimationUtils {

	/**
	 * 从顶部移出的动画
	 */
	public static void outToTopOnAnima(Context context, View v) {
		v.setVisibility(View.GONE);
		Animation anima = android.view.animation.AnimationUtils.loadAnimation(
				context, R.anim.anim_out_to_top);
		v.setAnimation(anima);
		anima.start();
	}
}
