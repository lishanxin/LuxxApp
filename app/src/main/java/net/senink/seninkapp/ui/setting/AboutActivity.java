package net.senink.seninkapp.ui.setting;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.BuildConfig;
import net.senink.seninkapp.MyApplication;
import net.senink.seninkapp.R;
import net.senink.seninkapp.ui.constant.Constant;
import net.senink.seninkapp.ui.upgrade.checkNew;

public class AboutActivity extends BaseActivity implements OnClickListener {
	private WebView mWebView;
    private Button backBtn;
    private TextView tvTitle;
    private Button upgradeBtn;
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		initView();
		setListener();
		setWebView();
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		upgradeBtn.setOnClickListener(this);
	}

	private void initView() {
		upgradeBtn = (Button) findViewById(R.id.update);
		backBtn = (Button) findViewById(R.id.title_back);
		tvTitle = (TextView) findViewById(R.id.title_name);
		tvTitle.setText(R.string.about);
		backBtn.setVisibility(View.VISIBLE);
		upgradeBtn.setText("Ver:"+ BuildConfig.VERSION_NAME);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("deprecation")
	private void setWebView() {
		mWebView = (WebView) findViewById(R.id.webview);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int mDensity = metrics.densityDpi;

		if (mDensity == 240) { // 可以让不同的density的情况下，可以让页面进行适配
			mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
		} else if (mDensity == 160) {
			mWebView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
		} else if (mDensity == 120) {
			mWebView.getSettings().setDefaultZoom(ZoomDensity.CLOSE);
		}
		WebSettings setting = mWebView.getSettings();
		setting.setPluginState(PluginState.ON);
		setting.setSupportZoom(true);
		setting.setJavaScriptEnabled(true);
		setting.setBuiltInZoomControls(true);
		setting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		if (BuildConfig.AboutURL.compareTo("") != 0) {
			mWebView.loadUrl(BuildConfig.AboutURL);

			mWebView.setWebViewClient(new WebViewClient() {

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				}
			});
		}
	}

	@Override
	public void onBackPressed() {
		backBtn.performClick();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		case R.id.update:
//			 new checkNew (AboutActivity.this,0).start();
			break;
		}
	}
}
