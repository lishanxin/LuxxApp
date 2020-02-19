package net.senink.seninkapp.ui.smart;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISDevice;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISMCSManager;
import net.senink.seninkapp.ui.util.HttpUtils;
import net.senink.seninkapp.ui.util.SDCardUtils;

public class AddInteligentAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<SmartCellItem> list;
	private PISManager pm;

	public AddInteligentAdapter(Context context, List<SmartCellItem> list) {
		mContext = context;
		if (mLayoutInflater == null) {
			mLayoutInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		this.list = list;
		pm = PISManager.getInstance();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public SmartCellItem getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SubCategoryHolder objectHolder = null;
		SmartCellItem cellItem = getItem(position);
		if (convertView == null) {

			convertView = mLayoutInflater.inflate(
					R.layout.intelligent_element_list_item, null);
			objectHolder = new SubCategoryHolder();
			objectHolder.mSubIcon = (ImageView) convertView
					.findViewById(R.id.intelligent_icon);
			objectHolder.installTextView = (TextView) convertView
					.findViewById(R.id.intelligent_name);
			objectHolder.download_bt = (TextView) convertView
					.findViewById(R.id.download_bt);
			objectHolder.intelligent_desc = (TextView) convertView
					.findViewById(R.id.intelligent_desc);
			objectHolder.download_bt.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					try {
						SmartCellItem cellItem = (SmartCellItem) view.getTag();
						showListDialog(cellItem);
//						if (cellItem.isInstall) {
//							PISSmartCell smartItem = cellItem.pisSmartCell;
//							if (PISManager.getInstance()
//									.isRunningOnPisMcmObject(smartItem)) {
//								deleteAlert(view.getContext(), PISManager
//										.getInstance().getPisMcmObject(),
//										smartItem.getInstanceID());
//							} else {
//								deleteAlert(
//										view.getContext(),
//										PISManager.getInstance()
//												.getRunningOnPisDeviceObject(
//														smartItem), smartItem
//												.getInstanceID());
//							}
//
//						} else {
//							showListDialog(cellItem);
//						}
					} catch (Exception e) {
						PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
					}

				}
			});
			objectHolder.download_bg = (ImageView) convertView
					.findViewById(R.id.download_bg);
			convertView.setTag(objectHolder);

		} else {
			objectHolder = (SubCategoryHolder) convertView.getTag();
		}
		/**
		 * cy，得到view的名字属性
		 */
		//objectHolder.installTextView.setText("111111");
		objectHolder.installTextView.setText(cellItem.title);
		objectHolder.intelligent_desc.setText(cellItem.intro);
		objectHolder.cellItem = cellItem;
		objectHolder.download_bt.setTag(cellItem);
		if (cellItem.icon != null) {
//			objectHolder.mSubIcon.setImageBitmap(cellItem.iconBit);
			new Thread(new LoadIcon(mContext, objectHolder.mSubIcon,
					cellItem.guid, cellItem.icon)).start();
		}
//		if (cellItem.isInstall) {
//			objectHolder.download_bg
//					.setBackgroundResource(R.drawable.btn_red_pressed);
//			objectHolder.download_bt.setText(R.string.uninstall);
//		} else {
			objectHolder.download_bg
					.setBackgroundResource(R.drawable.download_bt);
			objectHolder.download_bt.setText(R.string.install);
//		}
		return convertView;
	}

	public class SubCategoryHolder {
		private ImageView mSubIcon;
		private TextView installTextView;
		public TextView download_bt;
		public SmartCellItem cellItem;
		public TextView intelligent_desc;
		public ImageView download_bg;
	}

	private void showListDialog(final SmartCellItem cellItem) {
		try {
//			ArrayList<PISDevice> listDev = pm.getPisDeviceList();
//			final ArrayList<PISDevice> piPeiList = new ArrayList<PISDevice>();
//			String[] item = null;
//			int N = listDev.size();
//			for (int i = 0; i < N; i++) {
//				PISDevice pd = listDev.get(i);
//				if (pd != null && pd.getClassString() != null
//						&& cellItem.platformid != null
//						&& cellItem.platformid.length > 0) {
//					String[] ids = cellItem.platformid;
//					int len = ids.length;
//					for (int j = 0; j < len; j++) {
//						if (pd.getClassString().equals(ids[j])) {
//							piPeiList.add(pd);
//							break;
//						}
//					}
//				}
//			}
//			item = new String[piPeiList.size() + 1];
//			for (int j = 0; j < piPeiList.size(); j++) {
//				item[j] = piPeiList.get(j).getName();
//			}
//			item[piPeiList.size()] = mContext.getResources().getString(
//					R.string.server);
//			if (item != null && item.length > 1) {
//				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//
//				builder.setTitle(mContext.getResources().getString(
//						R.string.choose_install_device));
//
//				builder.setItems(item, new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						try {
//							if (which < piPeiList.size()) {
//								PISDevice pv = piPeiList.get(which);
//								pv.installSmartCell(cellItem.guid);
//							} else {
//								PISManager.getInstance().getPisMcmObject()
//										.installSmartCell(cellItem.guid);
//							}
//							PISManager.getInstance().refresh();
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							net.senink.seninkapp
//						}
//					}
//
//				});
//
//				builder.create().show();
//			} else if (item != null && item.length == 1) {
//				Log.w("test", "net cellItem.guid==>" + cellItem.guid);
//				PISManager.getInstance().getPisMcmObject()
//						.installSmartCell(cellItem.guid);
//				PISManager.getInstance().refresh();
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}

	}

	public void deleteAlert(Context context, final PISBase obj,
			final String instanceId) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(context.getResources().getString(
				R.string.del_smart_alert));
		builder.setTitle(context.getResources().getString(R.string.del_smart));
		builder.setPositiveButton(
				context.getResources().getString(R.string.ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
//						if (obj instanceof PISDevice) {
//							PISDevice pd = ((PISDevice) obj);
//							pd.uninstallSmartCell(instanceId);
//						} else if (obj instanceof PISMCSManager) {
//							((PISMCSManager) obj).uninstallSmartCell(instanceId);
//						}
						PISManager.getInstance().DiscoverAll();
					}
				});

		builder.setNegativeButton(
				context.getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.create().show();
	}

	private Handler handler = new Handler();

	class LoadIcon implements Runnable {
		private String iconUrl;
		private ImageView imageView;
		Bitmap bit;
		Context context;
		String iconName;

		public LoadIcon(Context context, ImageView imageView, String iconName,
				String iconUrl) {
			this.iconUrl = iconUrl;
			this.imageView = imageView;
			this.context = context;
			this.iconName = iconName;
		}

		@Override
		public void run() {
			if (iconUrl != null) {
				try {
					bit = HttpUtils.getImage(context, iconName, iconUrl);
					int radius = (bit.getWidth() < bit.getHeight() ? bit
							.getWidth() : bit.getHeight()) / 2;
					bit = SDCardUtils.getCroppedRoundBitmap(bit, radius);
					handler.post(new Runnable() {

						@Override
						public void run() {
							imageView.setImageBitmap(bit);
						}
					});
				} catch (Exception e) {
					PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
				}
			}
		}

	}
}
