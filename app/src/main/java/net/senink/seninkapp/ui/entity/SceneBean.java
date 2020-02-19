package net.senink.seninkapp.ui.entity;

import java.io.Serializable;

/**
 * 环形灯的场景的基类
 * 
 * @author zhaojunfeng
 * @date 2015-11-24
 * 
 */

public class SceneBean implements Serializable {

	/**
	 * version id
	 */
	private static final long serialVersionUID = 5615398170905554841L;
	public int id = -1;
	// 登录用户
	public String user;
	// 场景名称
	public String sceneName;
	/*
	 * 0:不常用1：常用
	 */
	public int used = 10000;
	// 亮度
	public int bright;
	// 冷暖色
	public int coldwarm;
	// 图片的url
	public String picture;
	public int t1;
	public int t2;
	public int rgb;
    public boolean selected;
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id = ").append(id);
		sb.append(", user = ").append(user).append(", sceneName = ")
				.append(sceneName);
		sb.append(",used = ").append(used).append(", bright = ").append(bright);
		sb.append(",t1 = ").append(t1).append(", t2 = ").append(t2)
				.append(", rgb = ").append(rgb);
		sb.append(",coldwarm = ").append(coldwarm).append(", picture = ")
				.append(picture);
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		boolean equal = false;
		if (id == ((SceneBean) o).id) {
			equal = true;
		}
		return equal;
	}
}
