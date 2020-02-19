package net.senink.seninkapp.ui.util;

/**
 * 用于设置RGBW规则的工具类
 * （只实用于PISLightRGB类对应的灯）
 * @author zhaojunfeng
 * @date 2015-10-21
 * 
 */
public class RGBConfigUtils {
	
	/**
	 * 白光滚动轴的最大值
	 */
	public static final int MAX_VALUE = 255;
	/**
	 * 向PIPA发送请求时白光的最大值
	 */
	public static final int MAX_WHITE = 255;
	/**
	 * 当白光大于255，RGB三色的最大值
	 */
	public static final int MAX_COLOR = 120;
    /**
     * 把RGB最大值分成多少等分
     */
	public static final int COLORDIV_NUMBER = 20;
	
	/**
	 * 从白光中获取RGB三种颜色值
	 * @param white
	 * @return
	 *   包含RGB三色
	 */
	public static int[] getRGBFromWhite(int white){
		int[] colors = null;
		if (white >= 255 && white <= 275) {
			colors = new int[3];
			int value = (MAX_COLOR / COLORDIV_NUMBER) * (white - MAX_WHITE);
			colors[0] = value;
			colors[1] = value;
			colors[2] = value;
		}
		return colors;
	}
	
}
