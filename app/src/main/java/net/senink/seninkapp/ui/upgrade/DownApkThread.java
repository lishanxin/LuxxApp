package net.senink.seninkapp.ui.upgrade;

import java.io.File;

import net.senink.seninkapp.ui.util.CommonUtils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;


/**
 * 用于下载apk的线程
 * @author zhaojunfeng
 * @date 2015-10-29
 */
public class DownApkThread extends Thread {
	//发送下载完成命令的消息
	public static final int MSG_DOWNLOAD_FINISHED = 150;
	//默认apk下载路径
	public static final String DWON_PATH = "/senink/seninkapp.apk";
	
    //apk下载的url
	private String url = null;
	//上下文
	private Context context;
	public DownApkThread(Context context,String url){
		this.context = context;
		this.url = url;
	}
	@Override
	public void run() {
		File file = Environment.getExternalStorageDirectory();
		if (!TextUtils.isEmpty(url)) {
			String path = null;
			if (file.exists()) {
				path = file.getAbsolutePath()+ DWON_PATH;
			}else{
				path = "/data/data/"+CommonUtils.getApkInfor(context).packageName + "/file"+ DWON_PATH;
			}
			file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			boolean success = CommonUtils.downloadFile(url, path);
			if (success) {
				CommonUtils.installApk(context, path);
			}
		}
	}
	
}
