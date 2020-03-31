package net.senink.seninkapp.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;
import com.telink.sig.mesh.model.DeviceInfo;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.services.PISxinColor;
import net.senink.seninkapp.GeneralDeviceModel;
import net.senink.seninkapp.R;
import net.senink.seninkapp.telink.model.TelinkBase;
import net.senink.seninkapp.telink.view.IconGenerator;
import net.senink.seninkapp.ui.activity.LightLEDDetailActivity;
import net.senink.seninkapp.ui.activity.LightRGBDetailActivity;
import net.senink.seninkapp.ui.constant.ProductClassifyInfo;
import net.senink.seninkapp.ui.home.HomeActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 主页中的插排和灯泡，网关的适配器  Piservice + Telink
 *
 * @author zhaojunfeng
 * @date 2015-06-29
 */
public class MixLightListAdapter extends BaseAdapter {
    // 存放设备信息的集合
    private ArrayList<GeneralDeviceModel[]> bases;
    // 布局引用器
    private LayoutInflater inflater = null;
    // 上下文
    private HomeActivity context;

    public MixLightListAdapter(HomeActivity context, ArrayList<GeneralDeviceModel[]> devices) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        setList(devices, true);
    }

    /**
     * 更新信息
     *
     * @param devices
     * @param isRequested 是否需要发送请求获取开关状态
     */
    public void setList(ArrayList<GeneralDeviceModel[]> devices, boolean isRequested) {
        try {
            if (bases == null)
                bases = new ArrayList<GeneralDeviceModel[]>();
            if (devices == null)
                return;
            bases.clear();
            bases.addAll(devices);

            if (isRequested) {
                getLightState();
            }
        } catch (Exception e) {
            PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
        }
    }

    public ArrayList<GeneralDeviceModel[]> getList() {
        return bases;
    }

    /**
     * 获取灯的状态
     */
    private void getLightState() {
        for (GeneralDeviceModel[] lights : bases) {
            if (lights != null && lights.length > 0) {
                for (GeneralDeviceModel base : lights) {
                    if (base.isTelink()) {

                    } else if (base != null) {
                        if (base.getPisBase().ServiceType != PISBase.SERVICE_TYPE_GROUP) {
							base.getPisBase().request(base.getPisBase().updatePISInfo());
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getCount() {
        return bases.size();
    }

    @Override
    public Object getItem(int position) {
        return bases.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.home_lightlist_item, null);
            holder.rootLayout = (RelativeLayout) convertView
                    .findViewById(R.id.lightlist_root);
            holder.nameBtn1 = (ImageButton) convertView
                    .findViewById(R.id.lightlist_name1);
            holder.nameBtn2 = (ImageButton) convertView
                    .findViewById(R.id.lightlist_name2);
            holder.nameBtn3 = (ImageButton) convertView
                    .findViewById(R.id.lightlist_name3);
            holder.nameBtn4 = (ImageButton) convertView
                    .findViewById(R.id.lightlist_name4);
            holder.nametv1 = (TextView) convertView
                    .findViewById(R.id.tv_name1);
            holder.nametv2 = (TextView) convertView
                    .findViewById(R.id.tv_name2);
            holder.nametv3 = (TextView) convertView
                    .findViewById(R.id.tv_name3);
            holder.nametv4 = (TextView) convertView
                    .findViewById(R.id.tv_name4);
            holder.itemLayout1 = (RelativeLayout) convertView
                    .findViewById(R.id.lightlist_item1);
            holder.itemLayout2 = (RelativeLayout) convertView
                    .findViewById(R.id.lightlist_item2);
            holder.itemLayout3 = (RelativeLayout) convertView
                    .findViewById(R.id.lightlist_item3);
            holder.itemLayout4 = (RelativeLayout) convertView
                    .findViewById(R.id.lightlist_item4);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (bases.get(position) != null && bases.get(position).length > 0) {
            holder.rootLayout.setVisibility(View.VISIBLE);
			GeneralDeviceModel[] array = bases.get(position);
            int len = array.length;

			setGeneralLightView(holder.itemLayout1, holder.nameBtn1, holder.nametv1, len > 0 ? array[0] : null, position, 0);
			setGeneralLightView(holder.itemLayout2, holder.nameBtn2, holder.nametv2, len > 1 ? array[1] : null, position, 1);
			setGeneralLightView(holder.itemLayout3, holder.nameBtn3, holder.nametv3, len > 2 ? array[2] : null, position, 2);
			setGeneralLightView(holder.itemLayout4, holder.nameBtn4, holder.nametv4, len > 3 ? array[3] : null, position, 3);

        } else {
            holder.rootLayout.setVisibility(View.GONE);
        }

        return convertView;
    }

    /**
     * 设置每个item的tag
     *
     * @param view
     * @param infor
     * @param pos
     * @param index
     */
    private void setTag(View view, PISBase infor, int pos, int index) {
        Object[] objs = new Object[3];
        objs[0] = pos;
        objs[1] = index;
        objs[2] = infor;
        view.setTag(objs);
    }

    private void setGrayOnBackgroud(Drawable iv, int value) {
        if (value == 0) {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            iv.setColorFilter(filter);
        }
    }

	/**
	 * 灯泡列表中 设置一行中的item
	 *
	 * @param layout
	 * @param nameBtn
	 * @param generalData
	 * @param pos
	 * @param index
	 */
	private void setGeneralLightView(RelativeLayout layout, ImageButton nameBtn, TextView nametv, GeneralDeviceModel generalData, int pos, int index) {
		if (generalData == null) {
			layout.setVisibility(View.INVISIBLE);
			nameBtn.setBackgroundResource(R.drawable.pro_default_selector);
			nametv.setText("");

			return;
		}
		if(generalData.isTelink()){
            setLightView(layout, nameBtn, nametv, generalData.getTelinkBase(), pos, index);
		}else{
			setLightView(layout, nameBtn, nametv, generalData.getPisBase(), pos, index);
		}
	}

    /**
     * 灯泡列表中 设置一行中的item
     *
     * @param layout
     * @param nameBtn
     * @param infor
     * @param pos
     * @param index
     */
    private void setLightView(RelativeLayout layout, ImageButton nameBtn, TextView nametv, TelinkBase infor, int pos, int index) {
        if(infor.isDevice()){
            DeviceInfo device = infor.getDevice();
            final int deviceType = device.nodeInfo != null && device.nodeInfo.cpsData.lowPowerSupport() ? 1 : 0;
            nameBtn.setImageResource(IconGenerator.getIcon(deviceType, device.getOnOff()));
            nametv.setText(device.macAddress);
            nameBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,
                            LightRGBDetailActivity.class);
                    intent.putExtra("TelinkString");
                    context.startActivity(intent);
                    context.overridePendingTransition(
                            R.anim.anim_in_from_right,
                            R.anim.anim_out_to_left);
                }
            });
        }else{

        }
    }

    /**
     * 灯泡列表中 设置一行中的item
     *
     * @param layout
     * @param nameBtn
     * @param infor
     * @param pos
     * @param index
     */
    private void setLightView(RelativeLayout layout, ImageButton nameBtn, TextView nametv, PISBase infor, int pos, int index) {
        if (infor == null) {
            layout.setVisibility(View.INVISIBLE);
            nameBtn.setBackgroundResource(R.drawable.pro_default_selector);
            nametv.setText("");

            return;
        }
        // 获取该组中所有服务的信息
        layout.setVisibility(View.VISIBLE);
        String strName;
        try {
            strName = infor.getName();
        } catch (Exception e) {
//				e.printStackTrace();
            strName = "Unknow";
        }
        nametv.setText(strName);

        setTag(layout, infor, pos, index);


        try {
            StateListDrawable sld = null;
//			int resourceId = 0;
            PISDevice dev;
            PISxinColor light = (PISxinColor) infor;
            if (infor.ServiceType != PISBase.SERVICE_TYPE_GROUP) {
                dev = infor.getDeviceObject();
                if (dev != null) {
                    if (infor.getT1() == 0x10 && infor.getT2() == 0x05) {
                        sld = ProductClassifyInfo.getProductStateListDrawable(this.context,
                                ProductClassifyInfo.CLASSID_EUREKA_CANDLE,
                                dev.getStatus(),
                                light.getLightStatus());
                    } else {
                        sld = ProductClassifyInfo.getProductStateListDrawable(this.context,
                                dev.getClassString(),
                                dev.getStatus(),
                                light.getLightStatus());
                    }
                } else {
                    sld = ProductClassifyInfo.getProductStateListDrawable(this.context,
                            ProductClassifyInfo.CLASSID_DEFAULT,
                            0, 0);
                }
            } else {
                //找到对应的Service，并利用其DEVICE找到classid
//				List<PISBase> srvs = PISManager.getInstance().PIServicesWithQuery(
//						infor.getIntegerType(), PISManager.EnumServicesQueryBaseonType);
                List<PISBase> srvs = PISManager.getInstance().PIServicesWithQuery(
                        infor.getClass(), PISManager.EnumServicesQueryBaseonClass);
                if (srvs == null || srvs.size() == 0)
                    dev = null;
                else
                    dev = srvs.get(0).getDeviceObject();
                if (dev != null) {
                    sld = ProductClassifyInfo.getGroupStateListDrawable(this.context,
                            dev.getClassString(),
                            dev.getStatus(),
                            light.getLightStatus());
                } else {
                    sld = ProductClassifyInfo.getGroupStateListDrawable(this.context,
                            ProductClassifyInfo.CLASSID_DEFAULT,
                            0, 0);
                }
            }
            if (sld != null)
                nameBtn.setBackground(sld);
//			if (resourceId != 0) {
//				try {
////					nameBtn.setBackgroundResource(resourceId);
//					if (sld != null)
//						nameBtn.setBackground(sld);
//
////					if (infor.getStatus() == PISBase.SERVICE_STATUS_ONLINE)
////						setGrayOnBackgroud(nameBtn.getBackground(), 1);
////					else
////						setGrayOnBackgroud(nameBtn.getBackground(), 0);
//
//				}catch (Exception e){
//					PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
//				}
//			}

        } catch (ClassCastException e) {
            PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
        } catch (Exception e) {
            PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
        }

        nameBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO 灯组列表点击跳转逻辑
                View v = (View) view.getParent();
                if (v.getTag() == null)
                    return;
                Object[] objs = (Object[]) v.getTag();
                if (objs == null || objs.length != 3)
                    return;

                int pos = (Integer) objs[0];
                int index = (Integer) objs[1];
                PISBase pisBase = null;
                try {
                    pisBase = bases.get(pos)[index].getPisBase();
                } catch (Exception e) {
                    PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
                }
                if (pisBase == null)
                    return;
                if (pisBase.ServiceType != PISBase.SERVICE_TYPE_GROUP &&
                        pisBase.getStatus() != PISBase.SERVICE_STATUS_ONLINE) {
                    //执行whoami命令
                    PISDevice dev = pisBase.getDeviceObject();
                    if (dev != null) {
                        final PISBase pis = pisBase;
                        Calendar nowDate = Calendar.getInstance();
                        nowDate.set(2010, 1, 1, 0, 0, 0);
                        //pm.BroadcastRequest(dev.wakeupDevice((int) ((System.currentTimeMillis() - nowDate.getTimeInMillis()) / 1000)));
                        PipaRequest req = dev.wakeupDevice((int) ((System.currentTimeMillis() - nowDate.getTimeInMillis()) / 1000));
                        req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                            @Override
                            public void onRequestStart(PipaRequest req) {

                            }

                            @Override
                            public void onRequestResult(PipaRequest req) {
                                if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED)
                                    goDetailActivity(pis);
                            }
                        });
                        dev.request(req);
                    } else {
                        //pisBase.getGroupObjects().get(0)
                    }
                    return;
                }
                goDetailActivity(pisBase);

            }
        });
    }

    private void goDetailActivity(PISBase pisbase) {
        Intent intent = null;
        switch (pisbase.getIntegerType()) {
            case PISConstantDefine.PIS_MAJOR_LIGHT + (PISConstantDefine.PIS_LIGHT_LIGHT << 8):
                intent = new Intent(context,
                        LightLEDDetailActivity.class);
                break;
            case PISConstantDefine.PIS_MAJOR_LIGHT + (PISConstantDefine.PIS_LIGHT_COLOR << 8):
                // TODO 跳转至这个界面进行灯控
                intent = new Intent(context,
                        LightRGBDetailActivity.class);
                break;
            case PISConstantDefine.PIS_MAJOR_MULTIMEDIA + (PISConstantDefine.PIS_MULTIMEDIA_COLOR << 8):
                intent = new Intent(context,
                        LightRGBDetailActivity.class);
                break;
            case PISConstantDefine.PIS_MAJOR_LIGHT + (PISConstantDefine.PIS_LIGHT_COLORLIGHT << 8):            // NextApp.tw
                intent = new Intent(context,
                        LightRGBDetailActivity.class);
                break;
        }

        try {
            if (intent != null) {
                intent.putExtra("keystring", pisbase.getPISKeyString());
                context.startActivity(intent);
                context.overridePendingTransition(
                        R.anim.anim_in_from_right,
                        R.anim.anim_out_to_left);
            }
        } catch (Exception e) {
            PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
        }
    }

    private class ViewHolder {
        RelativeLayout rootLayout;
        RelativeLayout itemLayout1;
        ImageButton nameBtn1;
        TextView nametv1;
        RelativeLayout itemLayout2;
        ImageButton nameBtn2;
        TextView nametv2;
        RelativeLayout itemLayout3;
        ImageButton nameBtn3;
        TextView nametv3;
        RelativeLayout itemLayout4;
        ImageButton nameBtn4;
        TextView nametv4;
    }
}
