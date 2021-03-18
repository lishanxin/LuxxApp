/********************************************************************************************************
 * @file DeviceProvisionListAdapter.java
 *
 * @brief for TLSR chips
 *
 * @author telink
 * @date Sep. 30, 2010
 *
 * @par Copyright (c) 2010, Telink Semiconductor (Shanghai) Co., Ltd.
 *           All rights reserved.
 *
 *			 The information contained herein is confidential and proprietary property of Telink 
 * 		     Semiconductor (Shanghai) Co., Ltd. and is available under the terms 
 *			 of Commercial License Agreement between Telink Semiconductor (Shanghai) 
 *			 Co., Ltd. and the licensee in separate contract or the terms described here-in. 
 *           This heading MUST NOT be removed from this file.
 *
 * 			 Licensees are granted free, non-transferable use of the information in this 
 *			 file under Mutual Non-Disclosure Agreement. NO WARRENTY of ANY KIND is provided. 
 *
 *******************************************************************************************************/
package net.senink.seninkapp.telink.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;
import com.telink.sig.mesh.light.MeshService;
import com.telink.sig.mesh.light.ProvisionDataGenerator;
import com.telink.sig.mesh.light.parameter.ProvisionParameters;
import com.telink.sig.mesh.model.DeviceInfo;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.R;
import net.senink.seninkapp.telink.api.TelinkApiManager;
import net.senink.seninkapp.telink.model.ProvisioningDevice;
import net.senink.seninkapp.ui.activity.AddBlueToothDeviceActivity;

import java.util.List;

/**
 * provision list adapter
 * Created by Administrator on 2016/10/25.
 */
public class DeviceProvisionListAdapter extends BaseRecyclerViewAdapter<DeviceProvisionListAdapter.ViewHolder> {
    List<ProvisioningDevice> mDevices;
    BaseRecyclerViewAdapter.OnItemClickListener onItemClickListener;
    Context mContext;
    Handler mHandler;

    public DeviceProvisionListAdapter(Context context, List<ProvisioningDevice> devices, Handler handler) {
        mContext = context;
        mDevices = devices;
        mHandler = handler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.addbubble_item, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        holder.tvResult = (TextView) itemView
                .findViewById(R.id.addbubble_item_result);
        holder.tvName = (Button) itemView
                .findViewById(R.id.addbubble_item_name);
        holder.ivIcon = (ImageView) itemView
                .findViewById(R.id.addbubble_item_icon);

        return holder;
    }

    @Override
    public int getItemCount() {
        return mDevices == null ? 0 : mDevices.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ProvisioningDevice device = mDevices.get(position);
        final int deviceType = device.nodeInfo != null && device.nodeInfo.cpsData.lowPowerSupport() ? 1 : 0;

        holder.ivIcon.setBackgroundResource(IconGenerator.getIcon(deviceType, 1));
        holder.tvName.setText(device.macAddress);
//        holder.tvName.setText(mContext.getString(R.string.device_prov_desc, String.format("%04X", device.unicastAddress), device.macAddress));
        setListener(holder.tvName, position);
        /*if (device.bindState == DeviceBindState.BINDING) {
//            holder.pb_binding.setVisibility(View.VISIBLE);
            holder.pb_binding.setIndeterminate(true);
        } else {
//            holder.pb_binding.setVisibility(View.INVISIBLE);
            holder.pb_binding.setIndeterminate(false);
            if (device.bindState == DeviceBindState.BOUND) {
                holder.pb_binding.setProgress(100);
            } else {
                holder.pb_binding.setProgress(0);
            }
        }*/
//        holder.tv_device_info.setText(Integer.toHexString(device.meshAddress).toUpperCase() + " - \n" + device.macAddress + " - \n" + (device.bindState));
    }

    /**
     * 设置名称的监听器
     *
     * @param btn
     * @param pos
     */
    private void setListener(Button btn, final int pos) {
        btn.setTag(pos);
        btn.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                try {
                    if(onItemClickListener != null){
                        int position = (Integer) v.getTag();
                        onItemClickListener.onItemClick(position);
                    }
//
//                    View tvResult = ((View) v.getParent()).findViewById(R.id.addbubble_item_result);
//
//                    mHandler.sendEmptyMessage(AddBlueToothDeviceActivity.MSG_TELINK_LINE_INIT);
//                    ProvisioningDevice provisioningDevice = mDevices.get(position);
//                    MeshService.getInstance().startProvision(
//                            TelinkApiManager.getInstance()
//                                    .getProvisionParameters(provisioningDevice.advertisingDevice, provisioningDevice.unicastAddress));

                } catch (Exception e) {
                    PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
                }

            }
        });
    }

    public void setListener(BaseRecyclerViewAdapter.OnItemClickListener listener){
        onItemClickListener = listener;
    }



    class ViewHolder extends RecyclerView.ViewHolder {
//        // device icon
//        public ImageView iv_device;
//        // device mac, provisioning state
//        public TextView tv_device_info, tv_state;
//        ProgressBar pb_provision;

        public Button tvName;
        @SuppressWarnings("unused")
        public TextView tvResult;
        public ImageView ivIcon;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
