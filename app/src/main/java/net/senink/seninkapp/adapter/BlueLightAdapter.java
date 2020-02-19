package net.senink.seninkapp.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.services.PISxinColor;
import net.senink.piservice.util.ByteUtilBigEndian;
import net.senink.piservice.util.ByteUtilLittleEndian;
import net.senink.seninkapp.ui.constant.ProductClassifyInfo;
import net.senink.seninkapp.ui.util.LogUtils;

import net.senink.seninkapp.ui.activity.AddBlueToothDeviceActivity;
import net.senink.seninkapp.ui.activity.AddBlueToothDeviceActivity.Appearance;
import net.senink.seninkapp.ui.entity.BlueToothBubble;

import net.senink.seninkapp.R;

import net.senink.piservice.pis.PISMCSManager;
import net.senink.piservice.pis.PISManager;

public class BlueLightAdapter extends BaseAdapter {

    private SparseArray<BlueToothBubble> list;
    private LayoutInflater inflater;
    private PISMCSManager mcm = null;
    // 被选中的下标
    private BlueToothBubble selectedBubble;

    private Handler mHandler;
    // 设备的类型
    private int type = 3;

    public BlueLightAdapter(AddBlueToothDeviceActivity context,
                            SparseArray<BlueToothBubble> infor, Handler mHandler, int type) {
        inflater = LayoutInflater.from(context);
//		setMcm(false);
        this.mcm = PISManager.getInstance().getMCSObject();
        this.mHandler = mHandler;
        this.type = type;
        setList(infor);
    }

    public void setList(SparseArray<BlueToothBubble> infor) {
        if (null == infor) {
            this.list = new SparseArray<BlueToothBubble>();
        } else {
            this.list = infor;
        }
        selectedBubble = null;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.valueAt(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.addbubble_item, null);
            holder.tvResult = (TextView) convertView
                    .findViewById(R.id.addbubble_item_result);
            holder.tvName = (Button) convertView
                    .findViewById(R.id.addbubble_item_name);
            holder.ivIcon = (ImageView) convertView
                    .findViewById(R.id.addbubble_item_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BlueToothBubble infor = list.valueAt(position);
        if (infor != null) {
            Appearance apprearance = infor.appearance;
            if (infor.appearance != null && apprearance.macAddr != null) {
                holder.tvName.setText(apprearance.macAddr);
            } else {
                holder.tvName.setText("");
            }
            holder.tvResult.setVisibility(View.GONE);
            setListener(holder.tvName, position);
//            int resid = ProductClassifyInfo.getThumbResourceId(infor.getClassId());
//            holder.ivIcon.setBackgroundResource(resid);

            StateListDrawable sld = null;

            try {
//                if ( infor.getT1() == 0x10 && base.getT2() == 0x05 )
//                {
//                    sld = ProductClassifyInfo.getProductStateListDrawable(convertView.getContext(),
//                            ProductClassifyInfo.CLASSID_EUREKA_CANDLE,
//                            PISBase.SERVICE_STATUS_ONLINE,
//                            PISxinColor.XINCOLOR_STATUS_ON);
//                }
//                else
                {
                    sld = ProductClassifyInfo.getProductStateListDrawable(convertView.getContext(),
                            infor.getClassId(),
                            PISBase.SERVICE_STATUS_ONLINE,
                            PISxinColor.XINCOLOR_STATUS_ON);
                }

                holder.ivIcon.setBackground(sld);
            }catch (Exception e){
                e.printStackTrace();
            }

//
//            if (type == 6) {
//                holder.ivIcon
//                        .setBackgroundResource(R.drawable.icon_item_addinsole);
//            } else if (type == 4) {
//                holder.ivIcon
//                        .setBackgroundResource(R.drawable.adddevice_item_remoter);
//            } else if (type == 1) {
//                holder.ivIcon
//                        .setBackgroundResource(R.drawable.adddevice_item_light);
//            } else if (type == 2) {
//                holder.ivIcon
//                        .setBackgroundResource(R.drawable.adddevice_item_link);
//            } else {
//                holder.ivIcon
//                        .setBackgroundResource(R.drawable.icon_equipment_light_group);
//            }
        }
        return convertView;
    }

    /**
     * 设置名称的监听器
     *
     * @param btn
     * @param pos
     */
    private void setListener(Button btn, int pos) {
        btn.setTag(pos);
        btn.setOnClickListener(new OnClickListener() {

            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {

                try {
                    int position = (Integer) v.getTag();
                    View tvResult = ((View) v.getParent()).findViewById(R.id.addbubble_item_result);
                    selectedBubble = list.valueAt(position);
                    if (!tvResult.isShown()) {
                        mHandler.sendEmptyMessage(AddBlueToothDeviceActivity.MSG_LINE_INIT);
                    }
                } catch (Exception e) {
                    PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
                }
            }
        });
    }

    /**
     * 获取被选中的item的下标
     *
     * @return
     */
    public int getIndexOnSelected() {
        int index = -1;
        if (list.size() > 0 && selectedBubble != null) {
            int size = list.size();
            String mac = selectedBubble.appearance.macAddr;
            for (int i = 0; i < size; i++) {
                BlueToothBubble bubble = list.valueAt(i);
                if (bubble.appearance.macAddr.equals(mac)) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    /**
     * 获取被选中的item的信息
     *
     * @return
     */
    public BlueToothBubble getSelectedBubble() {
        return selectedBubble;
    }

    /**
     * 设置按钮信息
     *
     * @param view
     * @param success  绑定是否成功
     * @param visiable 组件是否可见
     */
    public void setResult(View view, boolean success, boolean visiable) {
        if (view != null) {
            Button btn = (Button) view.findViewById(R.id.addbubble_item_result);
            if (visiable) {
                if (success) {
                    btn.setText(R.string.finish);
                    btn.setClickable(false);
                    btn.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    btn.setText("");
                    btn.setClickable(true);
                    btn.setBackgroundResource(R.drawable.icon_addbuble_failed);
                }
                btn.setVisibility(View.VISIBLE);
            } else {
                btn.setVisibility(View.GONE);
                btn.setClickable(false);
                btn.setText("");
                btn.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    private class ViewHolder {
        public Button tvName;
        @SuppressWarnings("unused")
        public TextView tvResult;
        public ImageView ivIcon;
    }

}
