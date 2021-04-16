/********************************************************************************************************
 * @file IconGenerator.java
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


import net.senink.seninkapp.R;

/**
 * Created by kee on 2018/9/29.
 */

public class IconGenerator {

    /**
     * @param type  0: common device, 1: lpn
     * @param onOff -1: offline; 0: off, 1: on
     * @return res
     */
    public static int getIcon(int type, int onOff) {
        if (type == 1){
            return R.drawable.ic_low_power;
        }else {
            if (onOff == -1) {
                return R.drawable.pro_000508000108_offline_normal;
            } else if (onOff == 0) {
                return R.drawable.pro_000508000108_off_normal;
            } else {
                return R.drawable.pro_000508000108_normal;
            }
        }
    }


    public static int generateDeviceIconRes(int onOff) {
        if (onOff == -1) {
            return R.drawable.candle_offline;
        } else if (onOff == 0) {
            return R.drawable.candle_off_normal;
        } else {
            return R.drawable.candle_normal;
        }
    }


    public static int getGroupIconRes(boolean isOn) {
        if(isOn){
            return R.drawable.grp_000508000108_normal;
        }else{
            return R.drawable.grp_000508000108_off_normal;
        }
    }
}
