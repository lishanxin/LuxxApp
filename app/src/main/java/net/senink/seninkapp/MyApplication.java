package net.senink.seninkapp;


import android.app.Application;
import android.content.Context;
import com.pgyersdk.crash.PgyCrashManager;


public class MyApplication extends Application {
//	public ArrayList<Activity> activities = new ArrayList<Activity>();
	private int myUpdateLabel;
	public static Context context;
    public static String company;
	@Override
	public void onCreate() {
		super.onCreate();
		PgyCrashManager.register(this);
		Foreground.init(this);
		context = this;
		company = getResources().getString(R.string.company);

	}



//	public void addActivity(Activity activity) {
//		activities.add(activity);
//	}

//	public void removeActivity(Activity activity) {
//		activities.remove(activity);
////	}
//
//	/**
//	 * 判断程序是否在后台运行
//	 * @return
//	 */
//	public boolean isRunningOnBackground(Class<?> cls){
//		boolean isRunning = false;
//		if (!activities.isEmpty()) {
//			for (Activity activity : activities) {
//				activity.getClass();
//				if (activity.getClass().isAssignableFrom(cls)) {
//					isRunning = true;
//					break;
//				}
//			}
//		}
//        return isRunning;
//	}
//
//	public Activity getHomeActivity(){
//		Activity act = null;
//		if (!activities.isEmpty()) {
//			for (Activity activity : activities) {
//				if (activity instanceof HomeActivity) {
//					act = activity;
//					break;
//				}
//			}
//		}
//		return act;
//	}
//
//	public void removeHomeActivity(){
//		if (!activities.isEmpty()) {
//			for (Activity activity : activities) {
//				if (activity instanceof HomeActivity) {
//					activity.finish();
//					activities.remove(activity);
//					break;
//				}
//			}
//		}
//	}
	
	/**
	 * CY修改，记录是否需要
	 */
	public void setLabel(int s) {
		this.myUpdateLabel = s;
	}

	public int getLabel() {
		return myUpdateLabel;
	}
	/**
	 * CY修改结束
	 */
}
