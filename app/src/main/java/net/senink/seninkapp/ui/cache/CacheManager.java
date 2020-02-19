package net.senink.seninkapp.ui.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.graphics.Bitmap;
//import com.senink.seninkapp.core.PISConstantDefine;
import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.struct.UserInfo;
import net.senink.seninkapp.ui.entity.SceneBean;

/**
 * 缓存存放的管理类
 * 
 * @author zhaojunfeng
 * @date 2015-11-24
 */

public class CacheManager {

	private static CacheManager manager;
	// 该用户下所有场景模式的缓存信息
	private static List<SceneBean> scenes;
    private static UserInfo infor;
    private static Bitmap headBitmap;
    public static ArrayList<String> bindedMacs;
    public static Map<String,String> imageUrls = new HashMap<String,String>();
    //用于保存界面跳转时传到下个界面的图片
    private static Bitmap itemBitmap;
	private CacheManager() {

	}

	public static CacheManager getInstance() {
		if (null == manager) {
			manager = new CacheManager();
		}
		return manager;
	}

	public List<SceneBean> getLEDScenes() {
		List<SceneBean> list = getLEDUsedScenes();
		if (null == scenes || scenes.size() == 0) {
			list = null;
		} else {
			if (null == list || list.size() == 0) {
				list = new ArrayList<SceneBean>();
			}
			for (SceneBean bean : scenes) {
				if (bean.used >= 6 && bean.t1 == PISConstantDefine.PIS_MAJOR_LIGHT
						&& bean.t2 == PISConstantDefine.PIS_LIGHT_LIGHT) {
					list.add(bean);
				}
			}
		}
		return list;
	}

	public static void saveItemPicture(Bitmap bm){
		itemBitmap = bm;
	}
	
	public static Bitmap getItemPicture(){
		return itemBitmap;
	}
	
	public List<SceneBean> getRGBScenes() {
		List<SceneBean> list = getRGBUsedScenes();
		if (null == scenes || scenes.size() == 0) {
			list = null;
		} else {
			if (null == list || list.size() == 0) {
				list = new ArrayList<SceneBean>();
			}
			for (SceneBean bean : scenes) {
				if (bean.used >= 6 && bean.t1 == PISConstantDefine.PIS_MAJOR_LIGHT
						&& bean.t2 == PISConstantDefine.PIS_LIGHT_COLOR) {
					list.add(bean);
				}
			}
		}
		return list;
	}

	public void setScenes(List<SceneBean> list) {
		scenes = list;
	}

	public void addScene(SceneBean bean) {
		if (bean != null) {
			if (null == scenes) {
				scenes = new ArrayList<SceneBean>();
				scenes.add(bean);
			} else {
				boolean isIn = false;
				int size = scenes.size();
				for (int i = 0; i < size; i++) {
					SceneBean infor = scenes.get(i);
					if (bean.equals(infor)) {
						isIn = true;
						scenes.set(i, bean);
						break;
					}
				}
				if (!isIn) {
					scenes.add(bean);
				}
			}
		}
	}

	public void removeScene(SceneBean bean) {
		if (scenes != null) {
			scenes.remove(bean);
		}
	}

	/**
	 * 获取常用的RGB场景模式
	 * 
	 * @return
	 */
	public List<SceneBean> getRGBUsedScenes() {
		List<SceneBean> list = null;
		if (scenes != null) {
			list = new ArrayList<SceneBean>();
			for (SceneBean bean : scenes) {
				if (bean.used < 6
						&& bean.t1 == PISConstantDefine.PIS_MAJOR_LIGHT
						&& bean.t2 == PISConstantDefine.PIS_LIGHT_COLOR) {
					list.add(bean);
				}
			}
		}
		return list;
	}

	/**
	 * 获取常用的场景模式
	 * 
	 * @return
	 */
	public List<SceneBean> getLEDUsedScenes() {
		List<SceneBean> list = null;
		if (scenes != null) {
			list = new ArrayList<SceneBean>();
			for (SceneBean bean : scenes) {
				if (bean.used < 6
						&& bean.t1 == PISConstantDefine.PIS_MAJOR_LIGHT
						&& bean.t2 == PISConstantDefine.PIS_LIGHT_LIGHT) {
					list.add(bean);
				}
			}
		}
		return list;
	}

	/**
	 * 清理所有缓存信息
	 */
	public void clear() {
		if (scenes != null) {
			scenes.clear();
			scenes = null;
		}
	}

	public static UserInfo getUserInfor() {
		return infor;
	}

	public static void setUserInfor(UserInfo infor) {
		CacheManager.infor = infor;
	}

	public static Bitmap getHeadBitmap() {
		return headBitmap;
	}

	public static void setHeadBitmap(Bitmap headBitmap) {
		CacheManager.headBitmap = headBitmap;
	}

	/**
	 * 清理缓存数据
	 */
	public static void clearData(){
		if (headBitmap != null && !headBitmap.isRecycled()) {
			headBitmap.recycle();
			headBitmap = null;
		}
		infor = null;
		if (scenes != null) {
			scenes.clear();
			scenes = null;
		}
		if (imageUrls != null) {
			imageUrls.clear();
			imageUrls = null;
		}
	}
}
