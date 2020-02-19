package net.senink.seninkapp.ui.setting;

import java.util.List;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PISManager;
import net.senink.piservice.struct.UserInfo;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
import net.senink.seninkapp.ui.util.HttpUtils;
import net.senink.seninkapp.ui.util.SDCardUtils;
import net.senink.seninkapp.ui.view.CusPullListView;
import net.senink.seninkapp.ui.view.CusPullListView.OnRefreshListener;

public class ParentAccuActivity extends BaseActivity implements OnClickListener {
	private TextView tvTitle;
	private Button backBtn;
	private View yao_qing_area;
	private ProgressDialog progressDialog;
	private List<UserInfo> childAccuList;
	private List<UserInfo> parentAccuList;
	private CusPullListView clv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parent_accu_activity);
		initView();
        setListener();
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		yao_qing_area.setOnClickListener(this);
		clv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				info = (UserInfo) view.getTag();
				if (childAccuList.contains(info)) {
					deleteShouQuanAlert("delChild", info.id);
				} else if (parentAccuList.contains(info)) {
					deleteShouQuanAlert("delParent", info.id);
				}
				return true;
			}
		});
		clv.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				refresNetData(false);
			}

			@Override
			public void onStartRefresh() {
				// TODO Auto-generated method stub

			}

		});
	}


	@SuppressLint("InflateParams")
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backBtn = (Button) findViewById(R.id.title_back);
		clv = (CusPullListView) findViewById(R.id.parent_accu_list);
		View v = LayoutInflater.from(this).inflate(R.layout.parent_accu_root,
				null);
		clv.addFooterView(v, null, false);
		yao_qing_area = v.findViewById(R.id.yao_qing_area);

		tvTitle.setText(R.string.parent_accu);
		backBtn.setVisibility(View.VISIBLE);
		refresNetData(true);
	}

	private void refresNetData(boolean isShow) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getResources().getString(R.string.loading));
		if (isShow) {
			progressDialog.show();
		}
		new Thread(runnable).start();
	}

	UserInfo info;

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, AddNewAccuActivity.class);
		switch (v.getId()) {
		 case R.id.title_back:
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
						R.anim.anim_out_to_right);
		 break;
		case R.id.yao_qing_area:
			intent.putExtra("type", 2);
			startActivity(intent);
			overridePendingTransition(0, 0);
			break;
		}
	}

	RegistedDevicelistAdapter devicelistAdapter;
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			try {
				try {
					childAccuList = HttpUtils.getChildList(
							ParentAccuActivity.this,
							TabSettingActivity.userInfo.id);
					if (childAccuList.size() > 0) {
						UserInfo userInfo = childAccuList.get(childAccuList
								.size() - 1);
						userInfo.isShowBottom = true;
					}
					parentAccuList = HttpUtils.getParentList(
							ParentAccuActivity.this,
							TabSettingActivity.userInfo.id);
					if (parentAccuList.size() > 0) {
						UserInfo userInfo = parentAccuList.get(parentAccuList
								.size() - 1);
						userInfo.isShowBottom = true;
					}
				} catch (Exception e) {
					PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
				}
				handler.post(new Runnable() {

					@Override
					public void run() {
						refresh();
					}
				});
			} catch (Exception e) {
				PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
			}

		}
	};

	private void refresh() {
		try {
			clv.onRefreshComplete();
			if (childAccuList.size() > 0) {
				UserInfo userInfo = childAccuList.get(childAccuList.size() - 1);
				userInfo.isShowBottom = true;
			}
			if (parentAccuList.size() > 0) {
				UserInfo userInfo = parentAccuList
						.get(parentAccuList.size() - 1);
				userInfo.isShowBottom = true;
			}

			clv.setGroupIndicator(null);
			devicelistAdapter = new RegistedDevicelistAdapter(
					ParentAccuActivity.this);
			clv.setAdapter(devicelistAdapter);
			for (int i = 0; i < 2; i++) {
				clv.expandGroup(i);
			}
		} catch (Exception e) {
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		clv.onRefreshComplete();

	}

	private Handler handler = new Handler();

	@Override
	public void finish() {
		super.finish();
	}

	private void deleteShouQuanAlert(final String type, final int id) {
		this.type = type;
		this.id = id;
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(getResources().getString(
				R.string.alert_cancel_shou_quan));
		builder.setTitle(getResources().getString(R.string.cancel_shou_quan));
		builder.setPositiveButton(getResources().getString(R.string.ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						progressDialog.setMessage(getResources().getString(
								R.string.canceling_shou_quan));
						progressDialog.show();
						new Thread(deleShouQuanRunnable).start();
					}
				});

		builder.setNegativeButton(getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.create().show();
	}

	public String type;
	public int id;
	private Runnable deleShouQuanRunnable = new Runnable() {

		@Override
		public void run() {
			boolean flag = HttpUtils.delShouQuan(ParentAccuActivity.this,
					type, id);
			if (flag) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						childAccuList.remove(info);
						parentAccuList.remove(info);
						refresh();
					}
				});
			}
		}

	};

	public class RegistedDevicelistAdapter extends BaseExpandableListAdapter {
		private Context context;

		public RegistedDevicelistAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getGroupCount() {
			return 2;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if (groupPosition == 0) {
				return childAccuList.size();
			} else {
				return parentAccuList.size();
			}
		}

		@Override
		public String getGroup(int groupPosition) {
			if (groupPosition == 0) {
				return getResources()
						.getString(R.string.alert_you_can_fang_wen);
			}
			return getResources().getString(R.string.alert_can_fang_wen);
		}

		@Override
		public UserInfo getChild(int groupPosition, int childPosition) {
			if (groupPosition == 0) {
				return childAccuList.get(childPosition);
			} else {
				return parentAccuList.get(childPosition);
			}
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView tv = new TextView(context);
			tv.setTextSize(15);
			tv.setText(getGroup(groupPosition));
			tv.setBackgroundColor(0xffdddddd);
			tv.setFocusable(true);
			return tv;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ImageView intelligent_icon = null;
			TextView intelligent_name = null;
			TextView intelligent_desc = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(ParentAccuActivity.this)
						.inflate(R.layout.zhang_hao_list_item, null);
				convertView.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View view) {
						info = (UserInfo) view.getTag();
						if (childAccuList.contains(info)) {
							deleteShouQuanAlert("delChild", info.id);
						} else if (parentAccuList.contains(info)) {
							deleteShouQuanAlert("delParent", info.id);
						}
						return true;
					}
				});
			}
			intelligent_icon = (ImageView) convertView
					.findViewById(R.id.intelligent_icon);
			intelligent_name = (TextView) convertView
					.findViewById(R.id.intelligent_name);
			intelligent_desc = (TextView) convertView
					.findViewById(R.id.intelligent_desc);
			convertView.setTag(getChild(groupPosition, childPosition));
			// Bitmap bit = getChild(groupPosition, childPosition).userIcon;
			// if (bit != null) {
			// intelligent_icon.setImageBitmap(bit);
			// }
			new Thread(new LoadIcon(intelligent_icon, getChild(groupPosition,
					childPosition).loginUser, getChild(groupPosition,
					childPosition).nickSrc)).start();
			intelligent_name
					.setText(getChild(groupPosition, childPosition).loginUser);
			intelligent_desc
					.setText(getChild(groupPosition, childPosition).content);

			if (getChild(groupPosition, childPosition).isShowBottom) {
				intelligent_desc.setVisibility(View.GONE);
			} else {
				intelligent_desc.setVisibility(View.GONE);
			}

			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			childAccuList.clear();
			parentAccuList.clear();
			System.gc();
		} catch (Exception e) {
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}
	}

	class LoadIcon implements Runnable {
		private String iconUrl;
		private ImageView imageView;
		Bitmap bit;
		String iconName;

		public LoadIcon(ImageView imageView, String iconName, String iconUrl) {
			this.iconUrl = iconUrl;
			this.imageView = imageView;
			this.iconName = iconName;
		}

		@Override
		public void run() {
			try {
				if (iconUrl != null) {
					bit = HttpUtils.getImage(ParentAccuActivity.this,
							iconName, iconUrl);
					int radius = (bit.getWidth() < bit.getHeight() ? bit.getWidth()
							: bit.getHeight()) / 2;
					bit = SDCardUtils.getCroppedRoundBitmap(bit, radius);
					handler.post(new Runnable() {

						@Override
						public void run() {
							imageView.setImageBitmap(bit);
						}
					});
				}
			} catch (Exception e) {
				PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
			}
		}

	}
}
