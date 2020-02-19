package net.senink.seninkapp.ui.constant;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import net.senink.piservice.pis.PISBase;
import net.senink.piservice.services.PISXinLight;
import net.senink.piservice.services.PISxinColor;
import net.senink.piservice.struct.PIServiceInfo;
import net.senink.seninkapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wensttu on 2016/7/21.
 */
public class ProductClassifyInfo {
    /**
     * classid的定义
     * 厂商编号     产品分类   产品类型编号   网络类型
     * 0005         08         0001        08      -   eureka 音乐灯
     * 0005         05         0001        08      -   eureka 全彩灯
     *
     * 厂商编号： 0000 - senink；0001 - sogood；0002 - ipwgroup; 0005 - eureka; 0004 - 立鑫
     * 产品分类： 01 - 网络；02 - 电工； 03 - 白家电； 04 - 小家电； 05 - 照明； 06 - 周边； 07 - 可穿戴； 08 - 多媒体
     * 产品编号：
     * 网络类： 0001 - 路由器； 0002 - WIFI增强器； 0003 - 蓝牙桥接器
     * 电工类： 0001 - 普通插座；0002 - 模组化插座
     * 周边类： 0001 - 16键遥控器；0002 - 8键遥控器
     * 照明类： 0001 - RGBW； 0002 - 两路冷暖； 0003 - 单路调光； 0004 - 5路调光
     * 可穿戴： 0001 - 鞋垫
     * 多媒体： 0001 - 单声道音箱（蓝牙）
     * 网络类型：每一个bit代表一类网络类型
     * 0-WIFI; 1-433MHZ; 2-Zigbee; 3-BLE; 4-Wave; bit5~bit7 保留
     */
    //厂商ID
    public static final int MFID_SENINK = 0x0;
    public static final int MFID_SOGOOD = 0x1;
    public static final int MFID_IPWGRP = 0x2;
    public static final int MFID_LIXIN  = 0x4;
    public static final int MFID_EUREKA = 0x5;
    public static final int MFID_DOMIN  = 0x6;

    public static final int MFID_SENINK_XINCOLOR = 1000;  //为xinColor所出的特别版本


    public static final String CLASSID_DEFAULT        = "000000000008";
    public static final String CLASSID_SENINK_COLOR   = "000005000108";
    public static final String CLASSID_SENINK_REMOTER = "000006000208";
    public static final String CLASSID_SENINK_CENTER  = "000001000309";
    public static final String CLASSID_SENINK_SOCKET  = "000002000101";

    public static final String CLASSID_SOGOOD_LIGHT   = "000005000208";
    public static final String CLASSID_SOGOOD_REMOTER = "000006000108";

    public static final String CLASSID_IPWGROUP_BULB      = "000005000388";
    public static final String CLASSID_IPWGROUP_BULBLITE  = "000005000488";
    public static final String CLASSID_IPWGROUP_BULBLITE2 = "000005000288";

    public static final String CLASSID_LIXIN_INSOLES  = "000007000108";

    public static final String CLASSID_EUREKA_MUSIC   = "000508000108";
    public static final String CLASSID_EUREKA_COLOR   = "000505000208";
    public static final String CLASSID_EUREKA_CANDLE  = "000505000708";

    private static HashMap<String, Integer> nameResList;
    private static ArrayList<String> clsList;
    private static HashMap<String, SparseArray<Integer>> productStateResList;
    private static HashMap<String, SparseArray<Integer>> groupStateResList;

    private static SparseArray<StateListDrawable> sldCache;

    private static HashMap<String, Integer> productResList;
    private static HashMap<String, Integer> thumbProductResList;
    private static HashMap<String, Integer> groupResList;
    private static HashMap<String, Integer> thumbGroupResList;
    static {
        clsList = new ArrayList<>();
        nameResList = new HashMap<>();
        productStateResList = new HashMap<>();
        groupStateResList = new HashMap<>();
        sldCache = new SparseArray<>();


        nameResList.put(CLASSID_SENINK_COLOR, R.string.xincolor);
        nameResList.put(CLASSID_SENINK_REMOTER, R.string.xinremoter);
        nameResList.put(CLASSID_SENINK_CENTER, R.string.xincenter);
        nameResList.put(CLASSID_SENINK_SOCKET, R.string.xinswitch);

        nameResList.put(CLASSID_SOGOOD_LIGHT, R.string.xinlight);
        nameResList.put(CLASSID_SOGOOD_REMOTER, R.string.xinremoter16);

        nameResList.put(CLASSID_IPWGROUP_BULB, R.string.ipwbulb);
        nameResList.put(CLASSID_IPWGROUP_BULBLITE, R.string.ipwbulblite);
        nameResList.put(CLASSID_IPWGROUP_BULBLITE2, R.string.ipwbulblite);

        nameResList.put(CLASSID_LIXIN_INSOLES, R.string.xininsoles);

        nameResList.put(CLASSID_EUREKA_MUSIC, R.string.xinmusiccolor);
        nameResList.put(CLASSID_EUREKA_COLOR, R.string.luxxlight);
        nameResList.put(CLASSID_EUREKA_CANDLE, R.string.candlelight);

        productResList = new HashMap<>();
        productResList.put(CLASSID_SENINK_COLOR, R.drawable.pro_000005000108_selector);
        productResList.put(CLASSID_SENINK_REMOTER, R.drawable.pro_000006000208_selector);
        productResList.put(CLASSID_SENINK_CENTER, R.drawable.pro_000001000309_selector);
        productResList.put(CLASSID_SENINK_SOCKET, R.drawable.pro_000002000101_selector);

        productResList.put(CLASSID_SOGOOD_LIGHT, R.drawable.pro_000005000208_selector);
        productResList.put(CLASSID_SOGOOD_REMOTER, R.drawable.pro_000006000108_selector);

        productResList.put(CLASSID_IPWGROUP_BULB, R.drawable.pro_000005000108_selector);
        productResList.put(CLASSID_IPWGROUP_BULBLITE, R.drawable.pro_000005000108_selector);
        productResList.put(CLASSID_IPWGROUP_BULBLITE2, R.drawable.pro_000005000208_selector);

        productResList.put(CLASSID_LIXIN_INSOLES, R.drawable.pro_000007000108_selector);

 //       productResList.put(CLASSID_EUREKA_MUSIC, R.drawable.pro_000508000108_selector);
 //       productResList.put(CLASSID_EUREKA_COLOR, R.drawable.pro_000505000208_selector);
        productResList.put(CLASSID_EUREKA_MUSIC, R.drawable.pro_000508000108_selector);
        productResList.put(CLASSID_EUREKA_COLOR, R.drawable.pro_000508000108_selector);
//        productResList.put(CLASSID_EUREKA_COLOR, R.drawable.pro_000508000108_selector);

        SparseArray<Integer> stateReslist  = new SparseArray<>();
        stateReslist.put(PISBase.SERVICE_STATUS_OFFLINE, R.drawable.pro_000508000108_offline_normal);
        stateReslist.put(((PISxinColor.XINCOLOR_STATUS_ON & 0xF) << 4) | (PISBase.SERVICE_STATUS_ONLINE & 0xF),
                R.drawable.pro_000508000108_normal);
        stateReslist.put(((PISxinColor.XINCOLOR_STATUS_OFF & 0xF) << 4) | (PISBase.SERVICE_STATUS_ONLINE & 0xF),
                R.drawable.pro_000508000108_off_normal);
        productStateResList.put(CLASSID_EUREKA_MUSIC, stateReslist);
        productStateResList.put(CLASSID_EUREKA_COLOR, stateReslist);

        SparseArray<Integer> stateReslist2  = new SparseArray<>();
        stateReslist2.put(PISBase.SERVICE_STATUS_OFFLINE, R.drawable.candle_off_normal);
        stateReslist2.put(((PISxinColor.XINCOLOR_STATUS_ON & 0xF) << 4) | (PISBase.SERVICE_STATUS_ONLINE & 0xF),
                R.drawable.candle_normal);
        stateReslist2.put(((PISxinColor.XINCOLOR_STATUS_OFF & 0xF) << 4) | (PISBase.SERVICE_STATUS_ONLINE & 0xF),
                R.drawable.candle_off_normal);
        productStateResList.put(CLASSID_EUREKA_CANDLE, stateReslist2);        // NextApp.tw

        productStateResList.put(CLASSID_SENINK_COLOR, stateReslist);

        productStateResList.put(CLASSID_SOGOOD_LIGHT, stateReslist);

        productStateResList.put(CLASSID_IPWGROUP_BULB, stateReslist);
        productStateResList.put(CLASSID_IPWGROUP_BULBLITE, stateReslist);
        productStateResList.put(CLASSID_IPWGROUP_BULBLITE2, stateReslist);

        productStateResList.put(CLASSID_DEFAULT, stateReslist);

        SparseArray<Integer> stateReslist3  = new SparseArray<>();
        stateReslist3.put(PISBase.SERVICE_STATUS_OFFLINE, R.drawable.grp_000508000108_offline_normal);
        stateReslist3.put(((PISxinColor.XINCOLOR_STATUS_ON & 0xF) << 4) | (PISBase.SERVICE_STATUS_ONLINE & 0xF),
                R.drawable.grp_000508000108_normal);
        stateReslist3.put(((PISxinColor.XINCOLOR_STATUS_OFF & 0xF) << 4) | (PISBase.SERVICE_STATUS_ONLINE & 0xF),
                R.drawable.grp_000508000108_off_normal);
        groupStateResList.put(CLASSID_EUREKA_MUSIC, stateReslist3);
        groupStateResList.put(CLASSID_EUREKA_COLOR, stateReslist3);
        groupStateResList.put(CLASSID_SENINK_COLOR, stateReslist);

        groupStateResList.put(CLASSID_SOGOOD_LIGHT, stateReslist);

        groupStateResList.put(CLASSID_IPWGROUP_BULB, stateReslist);
        groupStateResList.put(CLASSID_IPWGROUP_BULBLITE, stateReslist);
        groupStateResList.put(CLASSID_IPWGROUP_BULBLITE2, stateReslist);

        groupStateResList.put(CLASSID_DEFAULT, stateReslist);

        thumbProductResList = new HashMap<>();
        thumbProductResList.put(CLASSID_SENINK_COLOR, R.drawable.thumb_pro_000005000108_selector);
        thumbProductResList.put(CLASSID_SENINK_REMOTER, R.drawable.thumb_pro_000006000208_selector);
        thumbProductResList.put(CLASSID_SENINK_CENTER, R.drawable.thumb_pro_000001000309_selector);
        thumbProductResList.put(CLASSID_SENINK_SOCKET, R.drawable.thumb_pro_000002000101_selector);

        thumbProductResList.put(CLASSID_SOGOOD_LIGHT, R.drawable.thumb_pro_000005000208_selector);
        thumbProductResList.put(CLASSID_SOGOOD_REMOTER, R.drawable.thumb_pro_000005000108_selector);

        thumbProductResList.put(CLASSID_IPWGROUP_BULB, R.drawable.thumb_pro_000005000108_selector);
        thumbProductResList.put(CLASSID_IPWGROUP_BULBLITE, R.drawable.thumb_pro_000005000108_selector);
        thumbProductResList.put(CLASSID_IPWGROUP_BULBLITE2, R.drawable.thumb_pro_000005000208_selector);

        thumbProductResList.put(CLASSID_LIXIN_INSOLES, R.drawable.thumb_pro_000007000108_selector);

        thumbProductResList.put(CLASSID_EUREKA_MUSIC, R.drawable.thumb_pro_000505000208_selector);
        thumbProductResList.put(CLASSID_EUREKA_COLOR, R.drawable.thumb_pro_000505000208_selector);
        thumbProductResList.put(CLASSID_EUREKA_CANDLE, R.drawable.thumb_pro_candle_selector);     // NextApp.tw

        groupResList = new HashMap<>();
        groupResList.put(CLASSID_SENINK_COLOR, R.drawable.grp_000005000108_selector);

        groupResList.put(CLASSID_SOGOOD_LIGHT, R.drawable.grp_000005000208_selector);

        groupResList.put(CLASSID_IPWGROUP_BULB, R.drawable.grp_000005000108_selector);
        groupResList.put(CLASSID_IPWGROUP_BULBLITE, R.drawable.grp_000005000108_selector);
        groupResList.put(CLASSID_IPWGROUP_BULBLITE2, R.drawable.grp_000005000208_selector);

        groupResList.put(CLASSID_EUREKA_MUSIC, R.drawable.grp_000508000108_selector);
        groupResList.put(CLASSID_EUREKA_COLOR, R.drawable.grp_000505000208_selector);
        groupResList.put(CLASSID_EUREKA_CANDLE, R.drawable.grp_000505000208_selector);          // NextApp.tw

        thumbGroupResList = new HashMap<>();
        thumbGroupResList.put(CLASSID_SENINK_COLOR, R.drawable.thumb_grp_000005000108_selector);

        thumbGroupResList.put(CLASSID_SOGOOD_LIGHT, R.drawable.thumb_grp_000005000208_selector);

        thumbGroupResList.put(CLASSID_IPWGROUP_BULB, R.drawable.thumb_grp_000005000108_selector);
        thumbGroupResList.put(CLASSID_IPWGROUP_BULBLITE, R.drawable.thumb_grp_000005000108_selector);
        thumbGroupResList.put(CLASSID_IPWGROUP_BULBLITE2, R.drawable.thumb_grp_000005000208_selector);

        thumbGroupResList.put(CLASSID_EUREKA_MUSIC, R.drawable.thumb_grp_000508000108_selector);
        thumbGroupResList.put(CLASSID_EUREKA_COLOR, R.drawable.thumb_grp_000505000208_selector);
        thumbGroupResList.put(CLASSID_EUREKA_CANDLE, R.drawable.thumb_grp_000505000208_selector);       // NextApp.tw

    }

    public static ArrayList<String> getClassIdList(int ManufacturerId){
        clsList.clear();
        switch (ManufacturerId) {
            case MFID_SENINK: //senink
                clsList.add(CLASSID_SENINK_COLOR);    //xinColor
                clsList.add(CLASSID_SENINK_REMOTER);    //xinRemoter8
                clsList.add(CLASSID_SENINK_CENTER);    //xinCenter
                clsList.add(CLASSID_SENINK_SOCKET);    //xinSocket

                clsList.add(CLASSID_SOGOOD_LIGHT);    //xinLight
                clsList.add(CLASSID_SOGOOD_REMOTER);    //xinRemoter16

                clsList.add(CLASSID_IPWGROUP_BULB);    //xinBulb
                clsList.add(CLASSID_IPWGROUP_BULBLITE);    //xinBulb
                clsList.add(CLASSID_IPWGROUP_BULBLITE2);    //xinBulb2

                clsList.add(CLASSID_LIXIN_INSOLES);    //xinInsoles

                clsList.add(CLASSID_EUREKA_MUSIC);    //xinMusicColor
                clsList.add(CLASSID_EUREKA_COLOR);    //xinColor(Eureka)
                clsList.add(CLASSID_EUREKA_CANDLE);   //NextApp.tw
                break;
            case MFID_SENINK_XINCOLOR:
                clsList.add(CLASSID_SENINK_COLOR);
                break;
            case MFID_SOGOOD:
                clsList.add(CLASSID_SOGOOD_LIGHT);    //xinLight
                clsList.add(CLASSID_SOGOOD_REMOTER);    //xinRemoter16
                break;
            case MFID_IPWGRP:
                clsList.add(CLASSID_IPWGROUP_BULB);    //xinBulb
                clsList.add(CLASSID_IPWGROUP_BULBLITE);    //xinBulb
                clsList.add(CLASSID_IPWGROUP_BULBLITE2);    //xinBulb2
                break;
            case MFID_LIXIN:
                clsList.add(CLASSID_LIXIN_INSOLES);    //xinInsoles
                break;
            case MFID_EUREKA:
                clsList.add(CLASSID_EUREKA_MUSIC);    //xinMusicColor
                clsList.add(CLASSID_EUREKA_COLOR);    //xinColor(Eureka)
                clsList.add(CLASSID_EUREKA_CANDLE);   //NextApp.tw
                break;
            case MFID_DOMIN:
                clsList.add(CLASSID_SENINK_COLOR);    //xinColor
                clsList.add(CLASSID_SENINK_REMOTER);    //xinRemoter8
                clsList.add(CLASSID_SENINK_CENTER);    //xinCenter
                clsList.add(CLASSID_SOGOOD_LIGHT);    //xinLight
                clsList.add(CLASSID_EUREKA_MUSIC);    //xinMusicColor
                break;
        }

        return clsList;
    }

    public static int getNameResourceId(String classid){
        int result;

        if (nameResList.containsKey(classid))
            result = nameResList.get(classid);
        else
            result = R.string.unknown;

        return result;
    }

    public static int getProductResourceId(String classid){
        Integer resid;

        if (productResList.containsKey(classid))
            resid = productResList.get(classid);
        else
            resid = R.drawable.pro_default_selector;

        return resid;
    }

    private static StateListDrawable getStateListDrawable(Context context,
                                                          @NonNull SparseArray<Integer> resArray,
                                                          int devState,
                                                          int srvState){
        StateListDrawable sldTemp = null; //sldCache.get(resid);
        Integer resid;

        if (resArray.size() == 0)
            throw new NullPointerException("can't find state");

        resid = resArray.get((devState & 0xF) | ((srvState & 0xF) << 4));

        if (resid == null)
            resid = resArray.get((devState & 0xF));

        if (resid == null)
            resid = resArray.valueAt(0);

        if (sldTemp == null){
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resid);
            Bitmap bmMask = BitmapFactory.decodeResource(context.getResources(), R.drawable.button_default_page);

            if (bm == null || bmMask == null)
                throw new NullPointerException("can't find state");

            sldTemp = makeStateListDrawable(context, new BitmapDrawable(bm), bmMask, 0, 0 );

            if (sldTemp != null)
                sldCache.put(resid, sldTemp);
        }

        return sldTemp;
    }
    public static StateListDrawable getProductStateListDrawable(Context context,
                                                                String classid,
                                                                int devState,
                                                                int srvState) throws NullPointerException {
        Integer resid;
        SparseArray<Integer> resArray = productStateResList.get(classid);

        if (resArray != null){
            return getStateListDrawable(context, resArray, devState, srvState);
        }
        return null;
    }

    public static StateListDrawable getGroupStateListDrawable(Context context,
                                                                String classid,
                                                                int devState,
                                                                int srvState) throws NullPointerException {
        Integer resid;
        SparseArray<Integer> resArray = groupStateResList.get(classid);

        if (resArray != null){
            return getStateListDrawable(context, resArray, devState, srvState);
        }
        return null;

    }

    public static int getThumbResourceId(String classid){
        Integer resid;

        if (thumbProductResList.containsKey(classid))
            resid = thumbProductResList.get(classid);
        else
            resid = R.drawable.thumb_pro_default_selector;

        return resid;
    }

    public static int getGroupResourceId(String classid){
        Integer resid;

        if (groupResList.containsKey(classid))
            resid = groupResList.get(classid);
        else
            resid = R.drawable.grp_default_selector;

        return resid;
    }

    public static int getGroupThumbResourceId(String classid){
        Integer resid;

        if (thumbGroupResList.containsKey(classid))
            resid = thumbGroupResList.get(classid);
        else
            resid = R.drawable.thumb_grp_default_selector;

        return resid;
    }

    public static List<PIServiceInfo> getServiceInfoList(String classid){
        List<PIServiceInfo> srvInfoList = new ArrayList<>();;

        if (classid.compareTo(CLASSID_SENINK_SOCKET) == 0) {
            srvInfoList.add(new PIServiceInfo((byte) 0x0, (byte) 0x0, 1));
            srvInfoList.add(new PIServiceInfo((byte) 0x10, (byte) 0x02, 2));
            srvInfoList.add(new PIServiceInfo((byte) 0x10, (byte) 0x02, 3));
            srvInfoList.add(new PIServiceInfo((byte) 0x10, (byte) 0x02, 4));
            srvInfoList.add(new PIServiceInfo((byte) 0x10, (byte) 0x02, 5));
        }
        else if(classid.compareTo(CLASSID_SENINK_COLOR) == 0) {
            srvInfoList.add(new PIServiceInfo((byte) 0x0, (byte) 0x0, 1));
            srvInfoList.add(new PIServiceInfo((byte) 0x10, (byte) 0x03, 2));
        }
        else if(classid.compareTo(CLASSID_SENINK_REMOTER) == 0) {
            srvInfoList.add(new PIServiceInfo((byte) 0x0, (byte) 0x0, 1));
            srvInfoList.add(new PIServiceInfo((byte) 0x40, (byte) 0x01, 2));
        }
        else if(classid.compareTo(CLASSID_SENINK_CENTER) == 0) {

            srvInfoList.add(new PIServiceInfo((byte) 0x0, (byte) 0x0, 1));
            srvInfoList.add(new PIServiceInfo((byte) 0x70, (byte) 0x01, 2));
        }
        else if(classid.compareTo(CLASSID_SOGOOD_LIGHT) == 0) {
            srvInfoList.add(new PIServiceInfo((byte) 0x0, (byte) 0x0, 1));
            srvInfoList.add(new PIServiceInfo((byte) 0x10, (byte) 0x04, 2));
        }
        else if(classid.compareTo(CLASSID_SOGOOD_REMOTER) == 0) {
            srvInfoList.add(new PIServiceInfo((byte) 0x0, (byte) 0x0, 1));
            srvInfoList.add(new PIServiceInfo((byte) 0x70, (byte) 0x01, 2));
        }
        else if(classid.compareTo(CLASSID_IPWGROUP_BULB) == 0 ||
                classid.compareTo(CLASSID_IPWGROUP_BULBLITE) == 0) {
            srvInfoList.add(new PIServiceInfo((byte) 0x0, (byte) 0x0, 1));
            srvInfoList.add(new PIServiceInfo((byte) 0x10, (byte) 0x03, 2));
        }
        else if(classid.compareTo(CLASSID_IPWGROUP_BULBLITE2) == 0) {
            srvInfoList.add(new PIServiceInfo((byte) 0x0, (byte) 0x0, 1));
            srvInfoList.add(new PIServiceInfo((byte) 0x10, (byte) 0x04, 2));
        }
        else if(classid.compareTo(CLASSID_LIXIN_INSOLES) == 0) {
            srvInfoList.add(new PIServiceInfo((byte) 0x0, (byte) 0x0, 1));
            srvInfoList.add(new PIServiceInfo((byte) 0x80, (byte) 0x01, 2));
        }
        else if(classid.compareTo(CLASSID_EUREKA_MUSIC) == 0) {
            srvInfoList.add(new PIServiceInfo((byte) 0x0, (byte) 0x0, 1));
            srvInfoList.add(new PIServiceInfo((byte) 0x13, (byte) 0x03, 2));
            srvInfoList.add(new PIServiceInfo((byte) 0x10, (byte) 0x05, 3));    // NextApp.tw
        }
        else if(classid.compareTo(CLASSID_EUREKA_COLOR) == 0) {
            srvInfoList.add(new PIServiceInfo((byte) 0x0, (byte) 0x0, 1));
            srvInfoList.add(new PIServiceInfo((byte) 0x10, (byte) 0x03, 2));
            srvInfoList.add(new PIServiceInfo((byte) 0x10, (byte) 0x05, 3));    // NextApp.tw
        }

        return srvInfoList;
    }

    // 增加 isTailoringMask参数，为 true 时表示是在进行裁剪，false 表示是在进行普通的叠加操作
    public static Bitmap overlayBitmaps(Context context, Bitmap bmp1, Bitmap bmp2,
                                        int drawableWidth, int drawableHeight, Rect destRect, boolean isTailoringMask) {

        try {
            int maxWidth = Math.max(bmp1.getWidth(), bmp2.getWidth());
            int maxHeight = Math.max(bmp1.getHeight(), bmp2.getHeight());
            maxWidth = Math.max(maxWidth, drawableWidth);
            maxHeight = Math.max(maxHeight, drawableHeight);

            Bitmap bmOverlay;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                bmOverlay = Bitmap.createBitmap(context.getResources().getDisplayMetrics(), maxWidth, maxHeight, bmp1.getConfig());
            } else {
                bmOverlay = Bitmap.createBitmap(maxWidth, maxHeight, bmp1.getConfig());
            }

            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(bmp1, null, destRect, null);

/*******************************************************************/
            // 这里指定一个paint，并设置 PorterDuff.Mode 为 SRC_IN，已达到裁剪效果
            Paint paint = null;
            if (isTailoringMask) {
                paint = new Paint();
                PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
                paint.setXfermode(new PorterDuffXfermode(mode));
            }
            canvas.drawBitmap(bmp2, null, destRect, paint);
/*******************************************************************/

            return bmOverlay;
        } catch (Exception e) {
            e.printStackTrace();
            return bmp1;
        }
    }

    public static StateListDrawable makeStateListDrawable(final Context context, Drawable normal, Bitmap pressedMask,
                                                          int drawableWidth, int drawableHeight) {
        if (pressedMask == null || normal == null) {
            return null;
        }

        int pressedWidth = Math.max(drawableWidth, Math.max(normal.getIntrinsicWidth(), pressedMask.getWidth()));
        int pressedHeight = Math.max(drawableHeight, Math.max(normal.getIntrinsicHeight(), pressedMask.getHeight()));

        StateListDrawable stateListDrawable = new StateListDrawable();

        normal.setBounds(0, 0, drawableWidth, drawableHeight);
        Bitmap normalBm = ((BitmapDrawable)normal).getBitmap();

        Rect destRect = new Rect(0, 0, pressedWidth, pressedHeight);//normal.copyBounds();

/*******************************************************************/
        // 先调用一次overlayBitmaps(), isTailoringMask 传 true，这一步只是裁剪出符合形状的 mask
        Bitmap tailoredMask = overlayBitmaps(context, normalBm, pressedMask, drawableWidth, drawableHeight, destRect, true);

        // 再调用一次，isTailoringMask 传 false，这一步是将裁剪好的 mask 叠加到 normal 图上， 生成 pressed 状态的图
        Bitmap pressedBm = overlayBitmaps(context, normalBm, tailoredMask, drawableWidth, drawableHeight, destRect, false);
/*******************************************************************/

        BitmapDrawable pressed = new BitmapDrawable(context.getResources(), pressedBm);
        pressed.setBounds(0, 0, pressedWidth, pressedHeight);

        stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, pressed);
        stateListDrawable.addState(new int[] { }, normal);
        stateListDrawable.setBounds(0, 0, drawableWidth, drawableHeight);

        return stateListDrawable;
    }
}
